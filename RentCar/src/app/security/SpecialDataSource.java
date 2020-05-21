package app.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.client.MongoClients;

@Configuration
@EnableMongoRepositories(basePackages = "app.security", mongoTemplateRef = "specialMongoTemplate")
public class SpecialDataSource {

	@Primary
	@Bean(name = "specialMongoDataSource")
	@ConfigurationProperties("security.spring.data.mongodb")
	public MongoProperties mogoProperty() {
		return new MongoProperties();
	}
	
	@Primary
	@Bean(name = "specialMongoDBFactory")
	public MongoDbFactory mongoDbFactory(@Qualifier("specialMongoDataSource") MongoProperties properties){
		return new SimpleMongoClientDbFactory(MongoClients.create(String.format("mongodb+srv://%s", properties.getHost())),properties.getDatabase());
	}
	
	@Primary
	@Bean(name = "specialMongoTemplate")
	public MongoTemplate mongoTemplate(@Qualifier("specialMongoDBFactory") MongoDbFactory factory) {
		return new MongoTemplate(factory);
	}
}
