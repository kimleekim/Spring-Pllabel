package org.webapp.dataset;

import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface InstaCrawl {
    public WebDriver setUpWebDriver(String searchKeyword, String station) throws Exception;

    public String getDate(WebDriver driver);

    public String getPost(WebDriver driver);

    public String getDescription(WebDriver driver, int index);

    public long getLikeCNT(WebDriver driver);

    public boolean clickNextButton(WebDriver driver);

    public String getPhotoURL(WebDriver driver, String station, String url) throws IOException, Exception;

    public String getPhotopageURL(WebDriver driver);

    public List<String> getHashtags(WebDriver driver, String post);

}
