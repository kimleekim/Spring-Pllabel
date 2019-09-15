package org.webapp.dataset;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.webapp.config.ChromeDriverContext;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Component
public class InstaCrawlImpl implements InstaCrawl {
    private static final Logger logger = LoggerFactory.getLogger(InstaCrawlImpl.class);
    @Autowired
    S3Connector s3Connector;

    private WebDriver driver;
    private ChromeDriverContext driverContext;
    private WebDriverWait webDriverWait;
    private By findWebElement;
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
    private int isFoodpost = 0;
    private int totalPostCount = 0;
    private int limitPostatOnce = 180;

    final int goTargetpost = 1;
    final int goNextpost = 2;
    final int reachLastpost = 3;
    final int noExceptionOccur = 4;
    final int exceptionOccur = 5;
    final int infiniteLoopOccur = 6;

    @Autowired
    public InstaCrawlImpl(ChromeDriverContext driverContext) {
        this.driverContext = driverContext;
    }

    /*
    *
    *  @param
    *   isFoodPost = 1 => "이미지 : 음식" 인 이미지 crawl (insta-food)
    *   isFoodPost = 2 => "이미지 : 실내/실외" 인 이미지 crawl (insta-hot)
    *
     */
    public void setIsFoodPost(int isFoodPost) {
        this.isFoodpost = isFoodPost;
    }

    public int getIsFoodpost() {
        return this.isFoodpost;
    }

    @Override
    public WebDriver setUpWebDriver (String search, String station) throws Exception {
        //초기 드라이버 셋팅
        if (search.contains(station)) { isFoodpost = 1; }
        else { isFoodpost = 2; }

        this.driver = driverContext.setupChromeDriver();
        this.webDriverWait = new WebDriverWait(this.driver, 40);
        driver.manage().window().maximize();
        this.skipPopularposts = 9;
        driver.get(search);

        try {
            this.findWebElement = By.xpath("//*[@id=\"react-root\"]/section/main/header/div[2]/div/div[2]/span/span");
            webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(findWebElement));
            this.element = driver.findElement(findWebElement);
            totalPostCount = Integer.parseInt(element.getText().replaceAll("\\,", ""));
            System.out.println("total: " + totalPostCount);
        } catch (TimeoutException e) {
            logger.info("FAIL to get Total-posts-Count.");
            refreshPage(driver);
        }

        return driver;
    }

    private void refreshPage (WebDriver driver) {
        this.driver = driver;
        driver.navigate().refresh();
        this.findWebElement = By.cssSelector("#react-root > svg");
        while (true) {
            try {
                webDriverWait.until(ExpectedConditions.invisibilityOfElementLocated(findWebElement));
                break;
            } catch (TimeoutException e) {
                System.out.println("페이지 새로고침 후 로드 실패!");
                continue;
            }
        }
    }

    @Override
    public int waitPage (WebDriver driver, int position, int skipPosts) {
        this.driver = driver;

        if (position == goTargetpost) {
            moveTargetPost(driver, skipPosts);
        }
        else if (position == goNextpost) {

            if(! clickNextButton(this.driver)) {
                return reachLastpost; //return 3
            }

        }

        return noExceptionOccur; //return 4
    }

    private boolean waitImageLoad (WebDriver driver) {
        this.driver = driver;
        this.findWebElement = By.cssSelector("body > div._2dDPU.vCf6V > div.zZYga > div > div");
        boolean isNormal = true;

        while (true) {
            try {
                webDriverWait.until(ExpectedConditions.invisibilityOfElementLocated(findWebElement));
                isNormal = true;
                break;
            } catch (TimeoutException e) {
                logger.info("Loading Image Now ...");
                webDriverWait.until(ExpectedConditions.invisibilityOfElementLocated(findWebElement));

                if (isNormal) {
                    isNormal = false;
                }
                else {
                    break;
                }
            }
        }
        return isNormal;
    }

    @Override
    public boolean checkPoint(int storedPosts) {
        if (storedPosts < limitPostatOnce) {
            limitPostatOnce = limitPostatOnce + 15;
            return false;
        }
        else {
            return true;
        }
    }

    private void retryingFindClick(By by) {
        WebElement element;
        JavascriptExecutor executor;

        while(true) {
            try {
                element = driver.findElement(by);
                executor = (JavascriptExecutor)driver;
                executor.executeScript("arguments[0].click();", element);
                break;
            } catch(ElementClickInterceptedException
                    | NoSuchElementException
                    | StaleElementReferenceException e) {
                try {
                    webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(by));
                } catch(TimeoutException exception) {
                    continue;
                }
            }
        }
    }

    private int moveTargetPost(WebDriver driver, int skipPosts) {
        this.driver = driver;

        try {

            while (true) {
                this.findWebElement = By.xpath("//*[@id=\"react-root\"]/section/main/article/div[1]/div/div/div[1]/div[1]/a");
                webDriverWait.until(ExpectedConditions.elementToBeClickable(findWebElement));
                retryingFindClick(findWebElement);
                if (!waitImageLoad(driver)) { closePost(driver); refreshPage(driver); }
                else { break; }
            }

            for (int post = 0; post < skipPosts; post++) {
                clickNextButton(driver);
                waitImageLoad(driver);
            }

            return noExceptionOccur;

        } catch (TimeoutException e) {
            //System.out.println("첫번째 게시물 안 눌림!");
            refreshPage(driver);
            return exceptionOccur;
        }
    }

    private boolean clickNextButton(WebDriver driver) {
        //다음 버튼 누르기
        this.driver = driver;
        boolean doesNextExist = false;

        try {

            this.findWebElement = By.cssSelector("body > div._2dDPU.vCf6V > div.EfHg9 > div > div > a.HBoOv.coreSpriteRightPaginationArrow");
            webDriverWait.until(ExpectedConditions.elementToBeClickable(findWebElement));
            doesNextExist = true;
            retryingFindClick(findWebElement);
            waitImageLoad(driver);

        } catch (NoSuchElementException reach_last_post) {

            logger.info("Last post. ");
            closePost(driver);
            doesNextExist = false;

        } catch (TimeoutException e) {

            logger.info("FAIL to load next Page.");

        }
        //새로운 검색어 넣는 작업 필요 (리턴값이 false면)
        return doesNextExist;
    }

    private void closePost (WebDriver driver) {
        this.driver = driver;
        this.findWebElement = By.cssSelector("body > div._2dDPU.vCf6V > button.ckWGn");
        webDriverWait.until(ExpectedConditions.elementToBeClickable(findWebElement));
        retryingFindClick(findWebElement);
    }

    @Override
    public Date getDate(WebDriver driver) throws ParseException {
        this.driver = driver;
        String realdate = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM dd, yyyy");
        By by = By.xpath("/html/body/div[4]/div[2]/div/article/div[2]/div[2]/a/time");;

        try {
            webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(by));
            this.element = driver.findElement(by);
            realdate = element.getAttribute("title");

        } catch (NoSuchElementException one_more_try) {

            try {
                webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(by));
                this.element = driver.findElement(by);
                realdate = element.getAttribute("title");
            } catch (NoSuchElementException no_date_exist) {
                realdate = "";
            }

        } catch (NullPointerException e) {
            realdate = "";

        } finally {
            if (!realdate.equals("")) {
                realdate = getMonth(realdate);
                java.util.Date utilDate = simpleDateFormat.parse(realdate);
                Date settingDate = new Date(utilDate.getTime());
                return settingDate;
            }
            else {
                return new Date(0);
            }
        }
    }

    private String getMonth(String date) {
        switch (date.substring(0, 3)) {
            case "Jan" :
                date = date.replace("Jan", "1");
                break;

            case "Feb" :
                date = date.replace("Feb", "2");
                break;

            case "Mar" :
                date = date.replace("Mar", "3");
                break;

            case "Apr" :
                date = date.replace("Apr", "4");
                break;

            case "May" :
                date = date.replace("May", "5");
                break;

            case "Jun" :
                date = date.replace("Jun", "6");
                break;

            case "Jul" :
                date = date.replace("Jul", "7");
                break;

            case "Aug" :
                date = date.replace("Aug", "8");
                break;

            case "Sep" :
                date = date.replace("Sep", "9");
                break;

            case "Oct" :
                date = date.replace("Oct", "10");
                break;

            case "Nov" :
                date = date.replace("Nov", "11");
                break;

            case "Dec" :
                date = date.replace("Dec", "12");
                break;
        }

        return date;
    }

    @Override
    public String getPost(WebDriver driver) {
        this.driver = driver;
        String post = "";

        try {

            webDriverWait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.xpath("/html/body/div[4]/div[2]/div/article/div[2]/section[1]/span[1]/button"))));
            this.element = driver.findElement(By.xpath("/html/body/div[4]/div[2]/div/article/div[2]/div[1]/ul/div/li/div/div/div[2]/span"));
            post = element.getText();

        } catch (NoSuchElementException one_more_try) {

            try {
                this.element = driver.findElement(By.xpath("/html/body/div[4]/div[2]/div/article/div[2]/div[1]/ul/div/li/div/div/div[2]/span"));
                post = element.getText();
            } catch (NoSuchElementException no_post_exist) {
                post = "";
            }

        } catch (NullPointerException e) {
            post = "";
        } finally {
            return post;
        }
    }

    @Override
    public long getLikeCNT(WebDriver driver) {
        this.driver = driver;
        long likeCNT = 0;
        try {
            webDriverWait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.xpath("/html/body/div[4]/div[2]/div/article/div[2]/section[1]/span[1]/button"))));
            this.element = driver.findElement(By.xpath("/html/body/div[4]/div[2]/div/article/div[2]/section[2]/div/div/button/span"));
            likeCNT = Integer.parseInt(element.getText().replaceAll("\\,", ""));
        } catch (NoSuchElementException one_more_try) {
            try {
                this.element = driver.findElement(By.xpath("/html/body/div[4]/div[2]/div/article/div[2]/section[2]/div/div/button/span"));
                likeCNT = Integer.parseInt(element.getText().replaceAll("\\,", ""));
            } catch (NoSuchElementException no_like_exist) {
                likeCNT = 0;
            }
        } catch (NullPointerException e) {
            likeCNT = 0;
        }
        return likeCNT;
    }

    @Override
    public String getPlacetag(WebDriver driver) {
        this.driver = driver;
        String placetag = "";
        By placeElement;

        try {
            placeElement = By.cssSelector("body > div._2dDPU.vCf6V > div.zZYga > div > article > header > div.o-MQd.z8cbW > div.M30cS > div.JF9hh > a");
            webDriverWait.until(ExpectedConditions.elementToBeClickable(driver.findElement(placeElement)));

            placetag = driver.findElement(placeElement).getText();

        } catch(NoSuchElementException
                | TimeoutException one_more_try) {

            try {

                placeElement = By.xpath("/html/body/div[3]/div[2]/div/article/header/div[2]/div[2]/div[2]/a");
                webDriverWait.until(ExpectedConditions.elementToBeClickable(driver.findElement(placeElement)));
                placetag = driver.findElement(placeElement).getText();

            } catch(Exception e) {
                placetag = "";
            }

        } catch(NullPointerException e) {
            placetag = "";
        }

        return placetag;
    }

    @Override
    public String getDescription(WebDriver driver, int index) {
        this.driver = driver;
        String description = "";
        try {
            this.element = setPhotoURLElement(driver, index);
            if (!(element == null)) {
                description = element.getAttribute("alt");
            }
        } catch (NoSuchElementException one_more_try) {
            try {
                this.element = setPhotoURLElement(driver, index);
                if (!(element == null)) {
                    description = element.getAttribute("alt");
                }
            } catch (NoSuchElementException no_desc_exist) {
                description = "";
            }
        } catch (NullPointerException e) {
            description = "";
        } finally {
            return description;
        }
    }

    @Override
    public String getPhotopageURL(WebDriver driver) {   //게시글 화면의 url 리턴
        this.driver = driver;
        return driver.getCurrentUrl();
    }

    @Override
    public String getPhotoURL(WebDriver driver, String station, String url) throws Exception {
        String photoURL = "";
        boolean isFirstPhoto = true;
        int index = 1;
        this.driver = driver;

        driver.get(url);

        while (true) {

            try {

                this.findWebElement = By.cssSelector("#react-root > svg");
                webDriverWait.until(ExpectedConditions.invisibilityOfElementLocated(findWebElement));
                break;

            } catch (NoSuchElementException e) {

                try {
                    this.findWebElement = By.cssSelector("#react-root > section > main > div > div > article > div.eo2As > section.ltpMr.Slqrh > span.fr66n > button");
                    webDriverWait.until(ExpectedConditions.elementToBeClickable(findWebElement));
                    break;
                } catch (TimeoutException ee) {
                    refreshPage(driver);
                }

            } catch (TimeoutException ee) {
                refreshPage(driver);
            }

        }

        while (true) {
            try {
                String description = getDescription(driver, index);
                System.out.println(description);
                if (isFoodpost == 1
                        && description != null
                        && (description.contains("food") || description.contains("음식"))) {

                    photoURL = getPhotoSRC(driver, index);
                    break;

                }
                else if (isFoodpost == 2
                        && description != null
                        && ((description.contains("indoor") || description.contains("outdoor") || description.contains("실내") || description.contains("실외")))) {

                    photoURL = getPhotoSRC(driver, index);
                    break;

                }

                if (isFirstPhoto) {
                    driver.findElement(By.xpath("//*[@id=\"react-root\"]/section/main/div/div/article/div[1]/div/div/div/div[2]/button"))
                            .click();
                    isFirstPhoto = false;
                }
                else {
                    driver.findElement(By.xpath("//*[@id=\"react-root\"]/section/main/div/div/article/div[1]/div/div/div/div[2]/button[2]"))
                            .click();
                }

                index++;

                try {

                    webDriverWait.until(ExpectedConditions.attributeContains(
                            driver.findElement(
                                    By.xpath("//*[@id=\"react-root\"]/section/main/div/div/article/div[1]/div/div/div/div[2]/div/div")),
                            "style", "0.362783s"
                            )
                    );

                } catch (TimeoutException e) {
                }

                try {

                    webDriverWait.until(ExpectedConditions.attributeContains(
                            driver.findElement(
                                    By.xpath("//*[@id=\"react-root\"]/section/main/div/div/article/div[1]/div/div/div/div[2]/div/div")),
                            "style", "0s"
                            )
                    );

                } catch (TimeoutException e) {
                    refreshPage(driver);
                }

            } catch (NoSuchElementException e) {
                break;
            } catch (NullPointerException ee) {
                break;
            }

        }

        if (!(photoURL.equals(""))) {
            MultipartFile multipartFile = s3Connector.convertFileDatatype(1, photoURL, station);
            return s3Connector.upload(multipartFile, "static");
        }
        else {
            return photoURL;
        }

    }

    private WebElement setPhotoURLElement(WebDriver driver, int index) {
        //getDescription, getPhotoURL에서 포스팅 사진 태그를 읽어들이기 위한 공통적인 사전작업
        this.driver = driver;
        try {
            this.findWebElement = By.xpath("//*[@id=\"react-root\"]/section/main/div/div/article/div[1]/div/div/div/div[2]/button");
            webDriverWait.until(ExpectedConditions.elementToBeClickable(findWebElement));

            try {
                if (index == 1) {
                    try {
                        this.element = driver.findElement(By.xpath("//*[@id=\"react-root\"]/section/main/div/div/article/div[1]/div/div/div/div[2]/div/div/div/ul/li[1]/div/div/div/div[1]/img"));
                    } catch (NoSuchElementException have_friend_tag) {
                        this.element = driver.findElement(By.xpath("//*[@id=\"react-root\"]/section/main/div/div/article/div[1]/div/div/div/div[2]/div/div/div/ul/li[1]/div/div/div/div[1]/div[1]/img"));
                    }
                }
                else {
                    try {
                        this.element = driver.findElement(By.xpath("//*[@id=\"react-root\"]/section/main/div/div/article/div[1]/div/div/div/div[2]/div/div/div/ul/li["
                                + index + "]/div/div/div/div/div[1]/img"));
                    } catch (NoSuchElementException have_friend_tag) {
                        this.element = driver.findElement(By.xpath("//*[@id=\"react-root\"]/section/main/div/div/article/div[1]/div/div/div/div[2]/div/div/div/ul/li["
                                + index + "]/div/div/div/div/div[1]/div[1]/img"));
                    }
                }
            } catch (NoSuchElementException have_friend_tag) {
                try {
                    this.element = driver.findElement(By.xpath("//*[@id=\"react-root\"]/section/main/div/div/article/div[1]/div/div/div/div[2]/div/div/div/ul/li["
                            + index + "]/div/div/div/div/div[1]/div[1]/img"));
                } catch (NoSuchElementException no_image_exist) {
                    this.element = null;
                }
            }
        } catch (NoSuchElementException post_has_one_photo) {
            try {
                this.element = driver.findElement(By.xpath("//*[@id=\"react-root\"]/section/main/div/div/article/div[1]/div/div/div[1]/img"));
            } catch (NoSuchElementException have_friend_tag) {
                try {
                    this.element = driver.findElement(By.xpath("//*[@id=\"react-root\"]/section/main/div/div/article/div[1]/div/div/div[1]/div[1]/img"));
                } catch (NoSuchElementException no_image_exist) {
                    this.element = null;
                }
            }
        }
        return element;
    }

    private String getPhotoSRC (WebDriver driver, int index) {
        //aws에 저장할 포스팅 사진의 src값 얻어오는 작업
        this.driver = driver;
        String photoSRC = "";
        try {
            this.element = setPhotoURLElement(driver, index);
            if (!(element == null)) {
                photoSRC = element.getAttribute("srcset");
            }
            else {
                System.out.println("element null!!");
            }
        } catch (NoSuchElementException one_more_try) {
            try {
                this.element = setPhotoURLElement(driver, index);
                if (!(element == null)) {
                    photoSRC = element.getAttribute("srcset");
                }
            } catch (NoSuchElementException no_src_exist) {
                photoSRC = "";
            }
        } finally {
            return photoSRC;
        }
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

        for (int character=0; character<temp.length(); character++) {
            if (temp.charAt(character) == '#') {
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
