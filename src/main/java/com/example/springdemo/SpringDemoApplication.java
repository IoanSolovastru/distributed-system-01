package com.example.springdemo;

import com.example.springdemo.services.MedicalPlanService;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;

import java.io.IOException;
import java.util.TimeZone;

@SpringBootApplication
public class SpringDemoApplication {

    public static final String EXCHANGE_NAME = "activity_txt";
    public static final String QUEUE_SPECIFIC_NAME = "default_queue";
    public static final String ROUTING_KEY = "activity";

    @Autowired
    private MedicalPlanService medicalPlanService;

    public static void main(String[] args) throws IOException, InterruptedException {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(SpringDemoApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
	public void startRPC() throws IOException, InterruptedException {
		medicalPlanService.startGRPC();
	}

    @Bean
    public TopicExchange appExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue appQueueSpecific() {
        return new Queue(QUEUE_SPECIFIC_NAME);
    }

    @Bean
    public Binding declareBindingSpecific() {
        return BindingBuilder.bind(appQueueSpecific()).to(appExchange()).with(ROUTING_KEY);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}

//////============= RUN AS TOMCAT - WAR ============
//@SpringBootApplication
//public class DBApiApplication extends SpringBootServletInitializer {
//	@Override
//	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
//		return application.sources(DBApiApplication.class);
//	}
//
//	public static void main(String[] args) {
//		SpringApplication.run(DBApiApplication.class, args);
//	}
//}
