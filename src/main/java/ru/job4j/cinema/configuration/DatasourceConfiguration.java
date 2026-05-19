package ru.job4j.cinema.configuration;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.dbcp2.BasicDataSource;
import org.sql2o.Sql2o;
import org.sql2o.converters.Converter;
import org.sql2o.converters.ConverterException;
import org.sql2o.quirks.NoQuirks;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatasourceConfiguration {

    @Bean(destroyMethod = "close")
    public BasicDataSource connectionPool(DataSourceProperties dataSourceProperties,
                                          ObjectProvider<JdbcConnectionDetails> connectionDetailsProvider) {
        var connectionDetails = connectionDetailsProvider.getIfAvailable();
        var dataSource = new BasicDataSource();
        dataSource.setDriverClassName(driverClassName(connectionDetails, dataSourceProperties));
        dataSource.setUrl(connectionDetails == null
                ? dataSourceProperties.determineUrl()
                : connectionDetails.getJdbcUrl());
        dataSource.setUsername(connectionDetails == null
                ? dataSourceProperties.determineUsername()
                : connectionDetails.getUsername());
        dataSource.setPassword(connectionDetails == null
                ? dataSourceProperties.determinePassword()
                : connectionDetails.getPassword());
        return dataSource;
    }

    @Bean
    public Sql2o databaseClient(BasicDataSource connectionPool) {
        return new Sql2o(connectionPool, new NoQuirks(createConverters()));
    }

    private Map<Class, Converter> createConverters() {
        var converters = new HashMap<Class, Converter>();
        converters.put(LocalDateTime.class, localDateTimeConverter());
        return converters;
    }

    private String driverClassName(JdbcConnectionDetails connectionDetails,
                                   DataSourceProperties dataSourceProperties) {
        if (connectionDetails == null || connectionDetails.getDriverClassName() == null) {
            return dataSourceProperties.determineDriverClassName();
        }
        return connectionDetails.getDriverClassName();
    }

    private Converter<LocalDateTime> localDateTimeConverter() {
        return new Converter<>() {
            @Override
            public LocalDateTime convert(Object value) throws ConverterException {
                if (value == null) {
                    return null;
                }
                if (value instanceof Timestamp timestamp) {
                    return timestamp.toLocalDateTime();
                }
                if (value instanceof LocalDateTime localDateTime) {
                    return localDateTime;
                }
                throw new ConverterException("Cannot convert value to LocalDateTime");
            }

            @Override
            public Object toDatabaseParam(LocalDateTime value) {
                return value == null ? null : Timestamp.valueOf(value);
            }
        };
    }
}
