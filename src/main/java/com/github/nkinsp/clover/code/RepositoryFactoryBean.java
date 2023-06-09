package com.github.nkinsp.clover.code;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ClassUtils;

import com.github.nkinsp.clover.code.handlers.DeleteHandler;
import com.github.nkinsp.clover.code.handlers.FindByQueryHandler;
import com.github.nkinsp.clover.code.handlers.FindEntityRowMapperHandler;
import com.github.nkinsp.clover.query.AbstractWrapper;
import com.github.nkinsp.clover.query.Condition;
import com.github.nkinsp.clover.query.ConditionAdapter;
import com.github.nkinsp.clover.query.DeleteWrapper;
import com.github.nkinsp.clover.query.QueryWrapper;
import com.github.nkinsp.clover.table.TableInfo;

public class RepositoryFactoryBean implements FactoryBean<Object>,InvocationHandler,ApplicationContextAware {


	private String dbContextBeanName;
	
	private String repositoryInterfaceClassName;
	
	private DbContext dbContext;
	
	private Class<?>  repositoryInterface;
	
	private TableInfo<?> tableInfo;
	
	private Class<?> tableClass;
	
	private ApplicationContext applicationContext;
	
	private static ConcurrentHashMap<Integer, MethodHandle> methodHandleMap = new ConcurrentHashMap<Integer, MethodHandle>();
	
	@Override
	public Object getObject() throws Exception {
		return  Proxy.newProxyInstance(getClass().getClassLoader(),new Class[] {repositoryInterface},this);
	}

	@Override
	public Class<?> getObjectType() {
		return this.repositoryInterface;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}


	private DbContext getDbContext() {
		
		if(dbContext != null) {
			return dbContext;
		}
		
		this.dbContext = (DbContext) applicationContext.getBean(dbContextBeanName);
		
		return dbContext;
	}
	
	private TableInfo<?> getTableInfo(){
		
		if(tableInfo != null) {
			return tableInfo;
		}
		
		this.tableInfo = DbContext.getTableInfo(tableClass);
		
		return tableInfo;
		
	}
	


	public void setRepositoryInterface(Class<?> repositoryInterface) {
		this.repositoryInterface = repositoryInterface;


		Optional<?> optional = Stream.of(repositoryInterface.getGenericInterfaces())
				.map(type -> ((ParameterizedType) type))
				.filter(type -> BaseRepository.class.equals(type.getRawType()))
				.map(x -> x.getActualTypeArguments()[1])
				.map(type-> ((Class<?>) type))
				.findAny()
				;
		if(!optional.isPresent()) {
			throw new RuntimeException("no table entity mappping");
		}
		this.tableClass = (Class<?>) optional.get();
	}
	
	private void  addCondition(AbstractWrapper<?> wrapper,Method method,Object[] args){
		Parameter[] parameters = method.getParameters();
		for (int i =0; i < parameters.length;i++) {
			Parameter parameter = parameters[i];
			Annotation[] annotations = parameter.getAnnotations();
			for (Annotation annotation : annotations) {
				List<ConditionAdapter<? extends Annotation>> adapters = DbContext.getConditionAdapters()
						.stream()
						.filter(s->s.annotationType().equals(annotation.annotationType()))
						.collect(Collectors.toList());
				for (ConditionAdapter<? extends Annotation> adapter : adapters) {
					if(!wrapper.getConditions().isEmpty()) {
						wrapper.and();
					}
					Consumer<Condition<?>> consumer = adapter.adapter(annotation, parameter.getName(), args[i]);
					consumer.accept(wrapper);
				}
			}
		}
	}

	private QueryWrapper<?> buildMethodParamQueryWrapper(Method method,Object[] args){
		QueryWrapper<?> wrapper = new QueryWrapper<>(tableInfo);
		addCondition(wrapper, method, args);
		return wrapper;
	}
	
	private DeleteWrapper<?> buildMethodParamDeleteWrapper(Method method,Object[] args){
		DeleteWrapper<?> wrapper = new  DeleteWrapper<>(tableInfo);
		addCondition(wrapper, method, args);
		return wrapper;
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		if (method.isDefault()) {
			int key = repositoryInterface.getName().hashCode()^method.hashCode();
			MethodHandle handle = methodHandleMap.computeIfAbsent(key, (s) ->getMethodHandle(proxy, method));
			return handle.invokeWithArguments(args);
		}
		String methodName = method.getName();
		if ("dbContext".equals(methodName)) {
			return getDbContext();
		}
		if ("tableInfo".equals(methodName)) {
			return getTableInfo();
		}
		Class<?> returnType = method.getReturnType();
		if (methodName.startsWith("findBy")) {
			QueryWrapper<?> wrapper = buildMethodParamQueryWrapper(method, args);
			//返回列表数据
			if (List.class.isAssignableFrom(returnType)) {
				ParameterizedType type = (ParameterizedType) method.getGenericReturnType();
				Class<?> entityClass = (Class<?>) type.getActualTypeArguments()[0];
				return getDbContext().executeHandler(new FindEntityRowMapperHandler<>(entityClass, wrapper));
			}
			return getDbContext().executeHandler(new FindByQueryHandler<>(returnType, wrapper));
		}
		
		if(methodName.startsWith("findCountBy")) {
			QueryWrapper<?> wrapper = buildMethodParamQueryWrapper(method, args);
			wrapper.select("COUNT(1)");
			return getDbContext().executeHandler(new FindByQueryHandler<>(returnType, wrapper));
			
			
		}
		if (methodName.startsWith("deleteBy")) {
			 return getDbContext().executeHandler(new DeleteHandler<>(buildMethodParamDeleteWrapper(method, args)));
		}
		
		return null;
	}

	private MethodHandle getMethodHandle(Object proxy, Method method) {
		try {
			return MethodHandles.lookup().unreflectSpecial(method, method.getDeclaringClass()).bindTo(proxy);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
	
		this.applicationContext = applicationContext;
		
		
	}

	public String getDbContextBeanName() {
		return dbContextBeanName;
	}

	public void setDbContextBeanName(String dbContextBeanName) {
		this.dbContextBeanName = dbContextBeanName;
	}


	public String getRepositoryInterfaceClassName() {
		return repositoryInterfaceClassName;
	}

	public void setRepositoryInterfaceClassName(String repositoryInterfaceClassName)
			throws ClassNotFoundException, LinkageError {
		this.repositoryInterfaceClassName = repositoryInterfaceClassName;
		this.setRepositoryInterface(ClassUtils.forName(repositoryInterfaceClassName, this.getClass().getClassLoader()));

	}

	
	
	

}
