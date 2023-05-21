package com.github.nkinsp.clover.config;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.AnnotationMetadata;

import com.github.nkinsp.clover.code.BaseRepository;

public class RepositoryBeanDefinitionScanner extends ClassPathBeanDefinitionScanner{

	
	
	
	public RepositoryBeanDefinitionScanner(BeanDefinitionRegistry registry) {
		super(registry);
	}


	@Override
	protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
		AnnotationMetadata metadata = beanDefinition.getMetadata();
		
		Set<String>  interfaceNames= Stream.of(metadata.getInterfaceNames()).collect(Collectors.toSet());
		
		return metadata.isInterface() && interfaceNames.contains(BaseRepository.class.getName())&&metadata.isIndependent();
		
	}
	
	@Override
	protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
		return super.doScan(basePackages);
	}
}
