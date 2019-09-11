package org.webapp.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.slf4j.*;

@Configuration
public class ChromeDriverContext {

    public WebDriver driver;
    Logger logger = LoggerFactory.getLogger(ChromeDriverContext.class);

    @Bean
    public WebDriver getDriver() {
        return driver;
    }

    @Bean
    public WebDriver setupChromeDriver() throws Exception {
        System.setProperty("webdriver.chrome.driver", "/Users/kym/Downloads/chromedriver");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--window-size=1366,768");
        options.addArguments("--headless");
        options.setProxy(null);
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);
        capabilities.setCapability("pageLoadStrategy", "none");

        try {
            /*
             *
             * @ params
             * option : headless
             *
             */
            driver = new ChromeDriver(capabilities);
        } catch (Exception e) {
            logger.error("### [driver error] msg: {}, cause: {}", e.getMessage(), e.getCause());
        }

        return driver;
    }

//    @Bean
//    public void dropChromeDriver() {
//        if(driver == null) {
//            this.driver.quit();
//            driver = null;
//        }
//    }
}
