package compute.scut;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class Application {
	public static void main(String [] args){
		SpringApplication.run(Application.class, args);
	}
	
	@Bean
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}
	
	@Bean
	public RedisTemplate<String,HashMap<String, ArrayList<String>>> redisTemplate(RedisConnectionFactory factory) { 
	   RedisTemplate<String,HashMap<String, ArrayList<String>>> redisTemplate
	   	= new RedisTemplate<String,HashMap<String, ArrayList<String>>>(); 
	   redisTemplate.setConnectionFactory(factory); 
	   return redisTemplate; 
	} 

}
