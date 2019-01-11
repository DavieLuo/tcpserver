package com.example;

import com.example.conf.SpringContextUtils;
import com.example.unit.ToWebmsgThread;
import com.sun.glass.ui.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@EnableAutoConfiguration
@SpringBootApplication
public class TcpserverdemoApplication {

	public static void main(String[] args) {
		ApplicationContext applicationContext = SpringApplication.run(TcpserverdemoApplication.class, args);
		SpringContextUtils.setApplicationContext(applicationContext);
		new ToWebmsgThread().start();
		System.out.println("启动webmsgThread");
	}
}
