package org.webapp.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        DataSourceContext.class
})
@ComponentScan(basePackages = {"org.webapp.service"})
public class RootContextConfiguration {
}
