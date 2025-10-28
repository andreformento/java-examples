package com.formento.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.core.DatabaseClient;

@Configuration
@EnableR2dbcRepositories
public class DatabaseConfig {
}
