package cn.hackingwu.config;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.authentication.UserCredentials;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

/**
 * @author wuzj(971643).
 * @since 0.2
 */
@Configuration
@PropertySource("classpath:mongo.properties")
public class MongoConfig extends AbstractMongoConfiguration{

    @Value("${database.host}")
    private String host;
    @Value("${database.port}")
    private int port;
    @Value("${database.databaseName}")
    private String databaseName;
    @Value("${database.userName}")
    private String userName;
    @Value("${database.password}")
    private String password;

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    @Override
    @Bean
    public Mongo mongo() throws Exception {
        return new MongoClient(new ServerAddress(host,port));
    }

    @Override
    protected UserCredentials getUserCredentials() {
        return new UserCredentials(userName,password);
    }
}
