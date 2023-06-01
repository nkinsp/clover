package com.github.nkinsp.clover.config;

import java.util.Map;
import java.util.Set;


import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Repository;
import org.springframework.util.ClassUtils;

import com.github.nkinsp.clover.annotation.EnableClover;
import com.github.nkinsp.clover.annotation.RepositoryBean;
import com.github.nkinsp.clover.code.RepositoryFactoryBean;



public class DbContextConfiguration implements ImportBeanDefinitionRegistrar {
	

	

	

	@Override
	public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
		
		Map<String, Object> attributes = metadata.getAnnotationAttributes(EnableClover.class.getName());
		String[] basePackages =  (String[]) attributes.get("basePackages");
		String dbContextBeanName = (String) attributes.get("dbContextBeanName");
		if(basePackages == null || basePackages.length == 0) {
			basePackages = new String[]{ClassUtils.getPackageName(metadata.getClassName())};
		}
		RepositoryBeanDefinitionScanner scanner = new RepositoryBeanDefinitionScanner(registry);
		scanner.addIncludeFilter(new AnnotationTypeFilter(Repository.class));
		scanner.addIncludeFilter(new AnnotationTypeFilter(RepositoryBean.class));

		Set<BeanDefinitionHolder> beanDefinitionHolders = scanner.doScan(basePackages);
		
		for (BeanDefinitionHolder beanDefinitionHolder : beanDefinitionHolders) {
			
			registry.removeBeanDefinition(beanDefinitionHolder.getBeanName());
			String beanClassName = beanDefinitionHolder.getBeanDefinition().getBeanClassName();

			Class<?> beanClass = getBeanClass(beanClassName);
			BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(RepositoryFactoryBean.class);
			
			builder.addPropertyValue("repositoryInterface", beanClass);
			builder.addPropertyReference("dbContext", dbContextBeanName);
			builder.setScope(ConfigurableBeanFactory.SCOPE_SINGLETON);
			builder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
			
			AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
			
			beanDefinition.setAttribute("factoryBeanObjectType",beanClassName);
					
			registry.registerBeanDefinition(beanDefinitionHolder.getBeanName(),beanDefinition);
	
			
			
		}

		
	}

	private Class<?> getBeanClass(String beanClassName)  {
		try {
			return ClassUtils.forName(beanClassName, ClassUtils.getDefaultClassLoader());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (LinkageError e) {
			throw new RuntimeException(e);
		}
	}
	
	
	

}
