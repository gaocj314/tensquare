package com.tensquare.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import us.codecraft.webmagic.scheduler.RedisScheduler;
import util.IdWorker;

@SpringBootApplication
@EnableScheduling //开启定时任务
public class CrawlerApplication {

    @Value("${redis.host}")
    private String redis_host;

    public static void main(String[] args) {
        SpringApplication.run(CrawlerApplication.class);
    }

    @Bean
    public IdWorker idWorker(){
        return new IdWorker(1, 1);
    }

    @Bean
    public RedisScheduler redisScheduler(){
        return new RedisScheduler(redis_host);
    }

}
