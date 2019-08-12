package org.webapp.dataset;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.webapp.config.ChromeDriverContext;

import java.util.ArrayList;

@Component
public class InstaCrawlImpl implements InstaCrawl {

    private WebDriver driver;
    private ChromeDriverContext driverContext;
    private ArrayList<Object> row;
    private int skipPopularposts;

    @Autowired
    public InstaCrawlImpl(ChromeDriverContext driverContext) {
        this.driverContext = driverContext;
    }

    @Override
    public WebDriver setUpWebDriver(String url) throws Exception {
        this.driver = driverContext.setupChromeDriver();
        driver.manage().window().maximize();
        this.row = new ArrayList<>();
        this.skipPopularposts = 2;
        driver.get(url);

        Thread.sleep(4000);

        driver.findElement(By.xpath("//*[@id=\"react-root\"]/section/main/article/div[1]/div/div/div[1]/div[1]/a")).click();
        Thread.sleep(3000);

        for(int post=0; post<skipPopularposts; post++) {
            driver.findElement(By.cssSelector("body > div._2dDPU.vCf6V > div.EfHg9 > div > div > a.HBoOv.coreSpriteRightPaginationArrow")).click();
            Thread.sleep(1500);
        }
        // 최근 게시물에서 1번째 게시물 열린 상태로 멈춤

        return driver;
    }

    @Override
    public String getDate(WebDriver driver) {
        this.driver = driver;
        WebElement element = driver.findElement(By.xpath("/html/body/div[4]/div[2]/div/article/div[2]/div[2]/a/time"));
        String realdate = element.getAttribute("title");

        return realdate; //2019년 8월 8일 같은 형태로 출력
    }

    @Override
    public String getPost(WebDriver driver) {
        this.driver = driver;
        return "";
    }
}