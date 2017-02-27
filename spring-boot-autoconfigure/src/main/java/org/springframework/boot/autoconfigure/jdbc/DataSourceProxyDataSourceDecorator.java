package org.springframework.boot.autoconfigure.jdbc;

import javax.sql.DataSource;

import net.ttddyy.dsproxy.support.ProxyDataSource;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;

public class DataSourceProxyDataSourceDecorator
		implements DataSourceDecorator<ProxyDataSource> {

	@Override
	public ProxyDataSource decorate(DataSource dataSource) {
		return ProxyDataSourceBuilder.create(dataSource).build();
	}

}
