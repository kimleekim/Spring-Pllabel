package org.webapp.dataset;

import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;

public interface InstaCrawl {
    public WebDriver setUpWebDriver(String url) throws Exception;

    public String getDate(WebDriver driver);

    public String getPost(WebDriver driver);

    public List<String> getHashtags(WebDriver driver, String post);

}
