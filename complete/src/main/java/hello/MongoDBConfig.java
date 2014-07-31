/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hello;

import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoAuditing
@EnableMongoRepositories
public class MongoDBConfig {
    
    @Autowired
    private AuditorProvider auditorProvider;

    @Bean
    public MongoOperations mongoTemplate() throws Exception {
        MongoClient client = new MongoClient();
        return new MongoTemplate(new SimpleMongoDbFactory(client, "database"));
    }

    @Bean
    public MongoMappingContext mappingContext() {
        return new MongoMappingContext();
    }

    @Bean
    public AuditorAware<String> auditorProvider() {
        return auditorProvider;
    }

}
