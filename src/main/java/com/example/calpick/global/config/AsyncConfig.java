package com.example.calpick.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {


    @Bean(name = "mailExecutor")
    public Executor mailExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);            // 기본 스레드 수
        executor.setMaxPoolSize(10);            // 최대 스레드 수
        executor.setQueueCapacity(100);         // 대기 큐 용량
        executor.setThreadNamePrefix("mail-async-");
        executor.setWaitForTasksToCompleteOnShutdown(true); // 종료 전 작업 완료 기다림
        executor.initialize();
        return executor;
    }
}
