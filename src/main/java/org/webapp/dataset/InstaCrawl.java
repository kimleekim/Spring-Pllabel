package org.webapp.dataset;

import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public interface InstaCrawl {
    public WebDriver setUpWebDriver(String searchKeyword, String station) throws Exception;

    public int waitPage(WebDriver driver, int position, int skipPosts);

    public boolean checkPoint(int storedPosts);

    public Date getDate(WebDriver driver) throws ParseException;

    public String getPost(WebDriver driver);

    public String getDescription(WebDriver driver, int index);

    public long getLikeCNT(WebDriver driver);

    public String getPhotoURL(WebDriver driver, String station, String url) throws IOException, Exception;

    public String getPhotopageURL(WebDriver driver);

    public List<String> getHashtags(WebDriver driver, String post);

}
