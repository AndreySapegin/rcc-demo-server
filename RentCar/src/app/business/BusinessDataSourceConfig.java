package app.business;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.spel.spi.EvaluationContextExtension;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariDataSource;

import app.config.DatabaseProperty;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "app",
        entityManagerFactoryRef = "businessEntityManagerFactory",
        transactionManagerRef= "businessTransactionManager"
)
public class BusinessDataSourceConfig {
	
	@Bean
	@Lazy
	public EvaluationContextExtension databasePropery() {
		return new EvaluationContextExtension() {
			
			@Autowired DatabaseProperty config;
			
			@Override
			public String getExtensionId() {
				return "config";
			}
			
			@Override
			public DatabaseProperty getRootObject() {
				return config;
			}
		
		};
	}
	
	
	@Primary
	@Bean(name = "businessDataSourceProperties")
	@ConfigurationProperties("business.datasource")
	public DataSourceProperties dataSourceProperties() {
		return new DataSourceProperties();
	}
	
	@Primary
	@Bean(name = "businessDataSource")
	public DataSource dataSource(@Qualifier("businessDataSourceProperties") DataSourceProperties dataSourceProperties) {
		return dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
	}
	
	@Primary
	@Bean(name = "businessEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(
			EntityManagerFactoryBuilder builder, @Qualifier("businessDataSource") DataSource businessDataSource) {
		return builder
				.dataSource(businessDataSource)
				.packages("app")
				.build();
	}
	
	@Primary
	@Bean(name = "businessTransactionManager")
	public PlatformTransactionManager transactionManager(
			@Qualifier("businessEntityManagerFactory") EntityManagerFactory businessEntityManagerFactory) {
		return new JpaTransactionManager(businessEntityManagerFactory);
	}


}
