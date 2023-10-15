package com.koszczi.calendar.infrastructure.database;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories("com.koszczi.calendar.model.event")
@EnableJpaAuditing
public class JpaConfig {

  @Value("${spring.application.name}")
  private String appName;

  @Bean
  AuditorAware<String> auditorProvider() {
    return new AuditorAwareImpl(appName);
  }
}
