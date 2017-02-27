package org.springframework.boot.autoconfigure.jdbc;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

/**
 * TODO.
 *
 * @author Vedran Pavic
 * @since 2.0.0
 */
public class DataSourceDecoratorPostProcessor implements BeanPostProcessor {

	private List<DataSourceDecorator> decorators;

	public DataSourceDecoratorPostProcessor(
			ObjectProvider<List<DataSourceDecorator>> decorators) {
		this.decorators = decorators.getObject();
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		if (bean instanceof DataSource) {
			return decorate((DataSource) bean);
		}
		return bean;
	}

	private DataSource decorate(DataSource dataSource) {
		if (this.decorators != null) {
			AnnotationAwareOrderComparator.sort(this.decorators);
			for (DataSourceDecorator decorator : this.decorators) {
				dataSource = decorator.decorate(dataSource);
			}
		}
		return dataSource;
	}

}
