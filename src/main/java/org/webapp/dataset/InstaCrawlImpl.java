package org.webapp.dataset;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.webapp.config.ChromeDriverContext;

import java.util.ArrayList;
import java.util.List;

@Component
public class InstaCrawlImpl implements InstaCrawl {

    private WebDriver driver;
    private ChromeDriverContext driverContext;
    private WebDriverWait webDriverWait;
    private By findWebElement;
    private ArrayList<Object> row;
    private int skipPopularposts;
    private int space = 0;
    private String hashtag;
    private List<String> hashtags;
    private String username;
    private List<WebElement> commentList;
    private WebElement element;
    private String commentcheck;
    private List<WebElement> replyList;
    private String replycheck;

    @Autowired
    public InstaCrawlImpl(ChromeDriverContext driverContext) {
        this.driverContext = driverContext;
    }

    @Override
    public WebDriver setUpWebDriver(String url) throws Exception {
        this.driver = driverContext.setupChromeDriver();
        this.webDriverWait = new WebDriverWait(this.driver, 3600);
        driver.manage().window().maximize();
        this.row = new ArrayList<>();
        this.skipPopularposts = 9;
        driver.get(url);

        this.findWebElement = By.xpath("//*[@id=\"react-root\"]/section/main/article/div[1]/div/div/div[1]/div[1]/a");
        this.element = webDriverWait.until(ExpectedConditions.elementToBeClickable(this.findWebElement));

        this.element.click();

        for(int post=0; post<skipPopularposts; post++) {
            this.findWebElement = By.cssSelector("body > div._2dDPU.vCf6V > div.EfHg9 > div > div > a.HBoOv.coreSpriteRightPaginationArrow");
            this.element = webDriverWait.until(ExpectedConditions.elementToBeClickable(this.findWebElement));
            this.element.click();
        }
        // 최근 게시물에서 1번째 게시물 열린 상태로 멈춤

        return driver;
    }

    @Override
    public String getDate(WebDriver driver) {
        this.driver = driver;
        this.element = driver.findElement(By.xpath("/html/body/div[4]/div[2]/div/article/div[2]/div[2]/a/time"));
        String realdate = element.getAttribute("title");

        return realdate; //2019년 8월 8일 같은 형태로 출력
    }

    @Override
    public String getPost(WebDriver driver) {
        this.driver = driver;
        this.element = driver.findElement(By.xpath("/html/body/div[4]/div[2]/div/article/div[2]/div[1]/ul/div/li/div/div/div[2]/span"));
        String post = element.getText();
        return post;
    }

    @Override
    public List<String> getHashtags(WebDriver driver, String posts) {
        this.webDriverWait = new WebDriverWait(this.driver, 20);
        hashtags = new ArrayList<>();

        if(!posts.equals(""))
            findHashtags(posts, hashtags);

        this.driver = driver;
        this.element = driver.findElement(By.xpath("/html/body/div[4]/div[2]/div/article/div[2]/div[1]/ul/div/li/div/div/div[2]/h2/a"));
        username = element.getText();
        while(true) {
            try{
                this.findWebElement = By.xpath("/html/body/div[4]/div[2]/div/article/div[2]/div[1]/ul/li/div/button");
                this.element = webDriverWait.until(ExpectedConditions.elementToBeClickable(this.findWebElement));

                this.element.click();
            } catch(Exception e) {
                break;
            }
        }

        try {
            commentList = driver.findElements(By.xpath("/html/body/div[4]/div[2]/div/article/div[2]/div[1]/ul/ul"));

            for (WebElement nthComment : commentList) {
                this.element = nthComment.findElement(By.xpath(".//div/li/div/div/div[2]/h3/a"));

                if (this.element.getText().equals(username)) {
                    commentcheck = nthComment.findElement(By.xpath(".//div/li/div/div/div[2]/span")).getText();

                    findHashtags(commentcheck, hashtags);
                }

                try {
                    this.element = nthComment.findElement(By.xpath(".//li/ul/li/div/button/span"));

                    while (true) {
                        this.element = nthComment.findElement(By.xpath(".//li/ul/li/div/button"));
                        //리댓 테스트시 자꾸 NoSuchElementException 에러나면 이거 추가 : Thread.sleep(900);

                        if(this.element.findElement(By.xpath(".//span")).getText().equals("답글 숨기기"))
                            break;

                        this.element.click();
                    }

                    replyList = nthComment.findElements(By.xpath(".//li/ul/div"));

                    for (WebElement nthReply : replyList) {
                        this.element = nthReply.findElement(By.xpath(".//li/div/div/div[2]/h3/a"));

                        if (this.element.getText().equals(username)) {
                            replycheck = nthReply.findElement(By.xpath(".//li/div/div/div[2]/span")).getText();

                            findHashtags(replycheck, hashtags);
                        }
                    }
                } catch (Exception e) {
                    continue;
                }
            }
        }
        catch(Exception e) {
            return hashtags;
        }

        return hashtags;
    }

    private List<String> findHashtags(String content, List<String> hashtags) {
        String temp = content;

        for(int character=0; character<temp.length(); character++) {
            if(temp.charAt(character) == '#') {
                temp = temp.substring(character);
                character = 0;

                space = findNearestSpace(temp, character);

                if (space == 2201)
                    hashtag = temp;
                else
                    hashtag = temp.substring(0, space);

                hashtags.add(hashtag);
                hashtags.size();
            }
        }
        return hashtags;
    }

    private int findNearestSpace(String temp, int character) {
        int minimum;
        int empty = temp.indexOf(' ');
        int newLine = temp.indexOf('\n');
        int newTag = (temp.substring(character+1)).indexOf('#') + 1;

        if(empty == -1)
            empty = 2201;
        if (newLine == -1)
            newLine = 2201;
        if (newTag == 0)
            newTag = 2201;

        minimum = (empty < newLine) ? empty : newLine;
        minimum = (minimum < newTag) ? minimum : newTag;

        return minimum;
    }
}