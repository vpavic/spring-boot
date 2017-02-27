package org.springframework.boot.autoconfigure.jdbc;

import javax.sql.DataSource;

/**
 * TODO.
 *
 * @author Vedran Pavic
 * @since 2.0.0
 */
public interface DataSourceDecorator<T extends DataSource> {

	T decorate(DataSource dataSource);

}
