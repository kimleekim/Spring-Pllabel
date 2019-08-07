package org.webapp.dataset;

import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.webapp.config.ChromeDriverContext;

import java.util.ArrayList;

public class InstaCrawlImpl implements InstaCrawl {

    private WebDriver driver;
    private ChromeDriverContext driverContext;
    private ArrayList<Object> row;

    @Autowired
    public InstaCrawlImpl(ChromeDriverContext driverContext) {
        this.driverContext = driverContext;
    }

    @Override
    public WebDriver setUpWebDriver(String url) throws Exception{
        this.driver = driverContext.setupChromeDriver();
        this.row = new ArrayList<>();
        driver.get(url);

        Thread.sleep(1000);

        return driver;
    }
}
