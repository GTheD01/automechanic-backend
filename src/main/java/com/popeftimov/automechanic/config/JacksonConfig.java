package com.popeftimov.automechanic.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.popeftimov.automechanic.deserializer.ReportTypeEnumDeserializer;
import com.popeftimov.automechanic.model.ReportType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public SimpleModule customEnumModule() {
        SimpleModule module = new SimpleModule();

        module.addDeserializer(ReportType.class, new ReportTypeEnumDeserializer());

        return module;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(customEnumModule());
        mapper.findAndRegisterModules();
        return mapper;
    }
}
