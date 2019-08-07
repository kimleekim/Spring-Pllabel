package org.webapp.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = {
        "org.webapp.dao",
        "org.webapp.crawling"
})
@Import({
        DataSourceContext.class,
        ChromeDriverContext.class
})
public class RootContextConfiguration {
}
