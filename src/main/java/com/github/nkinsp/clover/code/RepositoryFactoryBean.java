package com.github.nkinsp.clover.code;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.dao.support.DaoSupport;
import org.springframework.util.Assert;

import com.github.nkinsp.clover.code.handlers.DeleteHandler;
import com.github.nkinsp.clover.code.handlers.FindByQueryHandler;
import com.github.nkinsp.clover.code.handlers.FindEntityRowMapperHandler;
import com.github.nkinsp.clover.code.handlers.UpdateHandler;
import com.github.nkinsp.clover.query.AbstractWrapper;
import com.github.nkinsp.clover.query.Condition;
import com.github.nkinsp.clover.query.ConditionAdapter;
import com.github.nkinsp.clover.query.DeleteWrapper;
import com.github.nkinsp.clover.query.QueryWrapper;
import com.github.nkinsp.clover.query.UpdateWrapper;
import com.github.nkinsp.clover.table.TableInfo;

public class RepositoryFactoryBean<T> extends DaoSupport  implements FactoryBean<T>,InvocationHandler {


	private DbContext dbContext;
	
	private Class<T>  repositoryInterface;
	
	private TableInfo<T> tableInfo;
	
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
		
		Assert.notNull(dbContext,"checkDaoConfig error");
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

	@SuppressWarnings("unchecked")
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
		Class<T> tableClass = (Class<T>) optional.get();
		this.tableInfo = DbContext.getTableInfo(tableClass);
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

	private QueryWrapper<T> buildMethodParamQueryWrapper(Method method,Object[] args){
		QueryWrapper<T> wrapper = new QueryWrapper<>(tableInfo);
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
			QueryWrapper<T> wrapper = buildMethodParamQueryWrapper(method, args);
			//返回列表数据
			if (List.class.isAssignableFrom(returnType)) {
				ParameterizedType type = (ParameterizedType) method.getGenericReturnType();
				Class<?> entityClass = (Class<?>) type.getActualTypeArguments()[0];
				return dbContext.executeHandler(new FindEntityRowMapperHandler<>(entityClass, wrapper));
			}
			return dbContext.executeHandler(new FindByQueryHandler<>(returnType, wrapper));
		}
		if (methodName.startsWith("deleteBy")) {
			 return dbContext.executeHandler(new DeleteHandler<>(buildMethodParamDeleteWrapper(method, args)));
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
	
	public List ids = new ArrayList<>();
	
	
	public static void main(String[] args) {
		
		try {
			
			
			Field field = RepositoryFactoryBean.class.getField("ids");
			
			Class<?> type = field.getType();
			
			ParameterizedType genericType = (ParameterizedType) field.getGenericType();
//			System.out.println(type);
			System.out.println(genericType.getActualTypeArguments()[0]);
			
//			Method method = RepositoryFactoryBean.class.getMethod("hello");
//			
//			Type type = method.getGenericReturnType();
////			Class<?> returnType = method.getReturnType();
////			
////			TypeVariable<?>[] typeParameters = returnType.getTypeParameters();
////			
////			for (TypeVariable<?> typeVariable : typeParameters) {
////				System.out.println(typeVariable.getName());
////			}
//			
//			
//			if(type instanceof ParameterizedType) {
//				
////				System.out.println("type=>"+type);
//				
//				ParameterizedType pType = (ParameterizedType) type;
//
//				Type type2 = pType.getActualTypeArguments()[0];
//				
//				System.out.println((Class<?>)type2);
//			}
			
//			System.out.println(type);
			
//			System.out.println(returnType);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
//		System.out.println(type);
		
	}

}
