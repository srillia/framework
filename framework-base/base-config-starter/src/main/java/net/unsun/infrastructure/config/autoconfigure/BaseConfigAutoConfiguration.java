package net.unsun.infrastructure.config.autoconfigure;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import net.unsun.infrastructure.config.config.ConfigProperties;
import net.unsun.infrastructure.config.config.LocalDateTimeSerializerConfig;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableConfigurationProperties(ConfigProperties.class)
@Import({LocalDateTimeSerializerConfig.class})
@AutoConfigureBefore(value = {HttpMessageConvertersAutoConfiguration.class})
public class BaseConfigAutoConfiguration implements WebMvcConfigurer {

    /**
     * 解决Jackson导致Long型数据精度丢失问题
     * @return
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer(LocalDateTimeSerializer localDateTimeSerializer, LocalDateTimeDeserializer localDateTimeDeserializer) {
        Jackson2ObjectMapperBuilderCustomizer customizer = new Jackson2ObjectMapperBuilderCustomizer() {
            @Override
            public void customize(Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder) {
                jacksonObjectMapperBuilder.serializerByType(Long.class, ToStringSerializer.instance)
                        .serializerByType(Long.TYPE, ToStringSerializer.instance)
                        .serializerByType(LocalDateTime.class,localDateTimeSerializer)
                        .deserializerByType(LocalDateTime.class, localDateTimeDeserializer);
            }
        };
        return customizer;
    }

}
