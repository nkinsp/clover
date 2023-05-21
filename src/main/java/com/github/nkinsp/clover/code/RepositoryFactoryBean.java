package com.github.nkinsp.clover.code;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;
import org.springframework.dao.support.DaoSupport;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import com.github.nkinsp.clover.code.handlers.DeleteHandler;
import com.github.nkinsp.clover.code.handlers.ExecuteHandler;
import com.github.nkinsp.clover.code.handlers.FindByQueryHandler;
import com.github.nkinsp.clover.code.handlers.FindEntityRowMapperHandler;
import com.github.nkinsp.clover.query.AbstractWrapper;
import com.github.nkinsp.clover.query.Condition;
import com.github.nkinsp.clover.query.ConditionAdapter;
import com.github.nkinsp.clover.query.DeleteWrapper;
import com.github.nkinsp.clover.query.QueryWrapper;
import com.github.nkinsp.clover.result.Rows;
import com.github.nkinsp.clover.table.TableInfo;

public class RepositoryFactoryBean<T> extends DaoSupport  implements FactoryBean<T>,InvocationHandler {


	private DbContext dbContext;
	
	private Class<T>  repositoryInterface;
	
	private TableInfo tableInfo;
	
	private static ConcurrentHashMap<Integer, MethodHandle> methodHandleMap = new ConcurrentHashMap<Integer, MethodHandle>();
	
	@SuppressWarnings("unchecked")
	@Override
	public T getObject() throws Exception {
		return (T) Proxy.newProxyInstance(getClass().getClassLoader(),new Class[] {repositoryInterface},this);
	}

	@Override
	public Class<?> getObjectType() {
		return this.repositoryInterface;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	
	
	@Override
	protected void checkDaoConfig() throws IllegalArgumentException {
		
		Assert.notNull(dbContext);
	}

	public DbContext getDbContext() {
		return dbContext;
	}

	public void setDbContext(DbContext dbContext) {
		this.dbContext = dbContext;
	}

	public Class<T> getRepositoryInterface() {
		return repositoryInterface;
	}

	public void setRepositoryInterface(Class<T> repositoryInterface) {
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
		Class<?> tableClass = (Class<?>) optional.get();
		this.tableInfo = DbContext.getTableInfo(tableClass);
	}
	
	private void  addCondition(AbstractWrapper<?> wrapper,Method method,Object[] args){
		Parameter[] parameters = method.getParameters();
		for (int i =0; i < parameters.length;i++) {
			Parameter parameter = parameters[i];
			Annotation[] annotations = parameter.getAnnotations();
			for (Annotation annotation : annotations) {
				List<ConditionAdapter<? extends Annotation>> adapters = dbContext.getConditionAdapters()
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

	private QueryWrapper<Object> buildMethodParamQueryWrapper(Method method,Object[] args){
		QueryWrapper<Object> wrapper = new QueryWrapper<>(tableInfo);
		addCondition(wrapper, method, args);
		return wrapper;
	}
	
	private DeleteWrapper buildMethodParamDeleteWrapper(Method method,Object[] args){
		DeleteWrapper wrapper = new  DeleteWrapper(tableInfo);
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
			return dbContext;
		}
		if ("tableInfo".equals(methodName)) {
			return this.tableInfo;
		}
		Class<?> returnType = method.getReturnType();
		if (methodName.startsWith("findBy")) {
			QueryWrapper<Object> wrapper = buildMethodParamQueryWrapper(method, args);
			if (Collection.class.isAssignableFrom(returnType)) {
				 return dbContext.executeHandler(new FindEntityRowMapperHandler<Object, Object>(tableInfo, wrapper));
			}
			return dbContext.executeHandler(new FindByQueryHandler<Object, Object>(tableInfo, wrapper));
		}
		if (methodName.startsWith("deleteBy")) {
			 return dbContext.executeHandler(new DeleteHandler(buildMethodParamDeleteWrapper(method, args)));
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

}
