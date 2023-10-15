package com.koszczi.calendar.infrastructure.database;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

@AllArgsConstructor
public class AuditorAwareImpl implements AuditorAware<String> {

  public static final String DEFAULT_DB_AUDITOR_SUFFIX = "_user";

  private String appName;

  @Override
  public Optional<String> getCurrentAuditor() {
    return Optional.of(appName + DEFAULT_DB_AUDITOR_SUFFIX);
  }
}
