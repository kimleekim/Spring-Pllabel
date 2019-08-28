package org.webapp.dataset;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.webapp.config.ChromeDriverContext;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Component
public class InstaCrawlImpl implements InstaCrawl {
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

    @Autowired
    public InstaCrawlImpl(ChromeDriverContext driverContext) {
        this.driverContext = driverContext;
    }

    @Override
    public WebDriver setUpWebDriver(String search, String station) throws Exception {
        this.driver = driverContext.setupChromeDriver();
        this.webDriverWait = new WebDriverWait(this.driver, 3600);
        driver.manage().window().maximize();
        this.skipPopularposts = 9;
        //String search = URLEncoder.encode(searchKeyword, "UTF-8");
        driver.get(search);

        if (search.contains(station)) {
            isFoodpost = 1;
            //이게 1이면 alt=음식 포함된 이미지 글만 크롤링 (instafood 채우기)
        }
        else {
            isFoodpost = 2;
            //이게 2이면 alt=실내/실외 포함된 이미지 글만 크롤링 (instahot 채우기)
        }

        this.findWebElement = By.xpath("//*[@id=\"react-root\"]/section/main/article/div[1]/div/div/div[1]/div[1]/a");
        this.element = webDriverWait.until(ExpectedConditions.elementToBeClickable(this.findWebElement));

        this.element.click();

        for(int post=0; post<skipPopularposts; post++) {
            this.findWebElement = By.cssSelector("body > div._2dDPU.vCf6V > div.EfHg9 > div > div > a.HBoOv.coreSpriteRightPaginationArrow");
            this.element = webDriverWait.until(ExpectedConditions.elementToBeClickable(this.findWebElement));
            this.element.click();
        }
        // 최근 게시물에서 1번째 게시물 열린 상태로 멈춤
        Thread.sleep(1000);
        return driver;
    }

    @Override
    public String getDate(WebDriver driver) {
        this.driver = driver;
        String realdate = null;
        try {
            this.element = driver.findElement(By.xpath("/html/body/div[4]/div[2]/div/article/div[2]/div[2]/a/time"));
            realdate = element.getAttribute("title");
        } catch (Exception one_more_try) {
            try {
                this.element = driver.findElement(By.xpath("/html/body/div[4]/div[2]/div/article/div[2]/div[2]/a/time"));
                realdate = element.getAttribute("title");
            } catch (Exception no_date_exist) {
                realdate = null;
            }
        } finally {
            return realdate; //2019년 8월 8일 같은 형태로 출력
        }
    }

    @Override
    public String getPost(WebDriver driver) {
        this.driver = driver;
        String post = null;
        try {
            this.element = driver.findElement(By.xpath("/html/body/div[4]/div[2]/div/article/div[2]/div[1]/ul/div/li/div/div/div[2]/span"));
            post = element.getText();
        } catch (Exception one_more_try) {
            try {
                this.element = driver.findElement(By.xpath("/html/body/div[4]/div[2]/div/article/div[2]/div[1]/ul/div/li/div/div/div[2]/span"));
                post = element.getText();
            } catch (Exception no_post_exist) {
                post = null;
            }
        } finally {
            return post;
        }
    }

    @Override
    public long getLikeCNT(WebDriver driver) {
        this.driver = driver;
        long likeCNT = 0;
        try {
            this.element = driver.findElement(By.xpath("/html/body/div[4]/div[2]/div/article/div[2]/section[2]/div/div/button/span"));
            likeCNT = Integer.parseInt(element.getText().replaceAll("\\,", ""));
        } catch (Exception one_more_try) {
            try {
                this.element = driver.findElement(By.xpath("/html/body/div[4]/div[2]/div/article/div[2]/section[2]/div/div/button/span"));
                likeCNT = Integer.parseInt(element.getText().replaceAll("\\,", ""));
            } catch (Exception no_like_exist) {
                likeCNT = 0;
            }
        }
        return likeCNT;
    }

    @Override
    public String getDescription(WebDriver driver, int index) {
        this.driver = driver;
        String description = null;
        try {
            this.element = setPhotoURLElement(driver, index);
            if (!(element == null)) {
                description = element.getAttribute("alt");
            }
        } catch (Exception one_more_try) {
            try {
                this.element = setPhotoURLElement(driver, index);
                if (!(element == null)) {
                    description = element.getAttribute("alt");
                }
            } catch (Exception no_desc_exist) {
                description = null;
            }
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
        String photoURL = null;
        boolean isFirstPhoto = true;
        int index = 1;
        this.driver = driver;

        driver.get(url);
        Thread.sleep(1500);
        while (true) {
            try {
                String description = getDescription(driver, index);
                System.out.println(description);
                if (isFoodpost == 1 && description != null && description.contains("음식")) {
                    photoURL = getPhotoSRC(driver, index);
                    break;
                }
                else if (isFoodpost == 2 && description != null && ((description.contains("실내") || description.contains("실외")))) {
                    photoURL = getPhotoSRC(driver, index);
                    break;
                }
                if (isFirstPhoto) {
                    driver.findElement(By.xpath("//*[@id=\"react-root\"]/section/main/div/div/article/div[1]/div/div/div/div[2]/button")).click();
                    isFirstPhoto = false;
                }
                else {
                    driver.findElement(By.xpath("//*[@id=\"react-root\"]/section/main/div/div/article/div[1]/div/div/div/div[2]/button[2]")).click();
                }
                index++;
                Thread.sleep(1000);
            } catch (Exception e) {
                break;
            }
        }
        if (!(photoURL == null)) {
            MultipartFile multipartFile = s3Connector.convertFileDatatype(1, photoURL, station);
            return s3Connector.upload(multipartFile, "static");
        }
        else {
            return null;
        }
    }

    private WebElement setPhotoURLElement(WebDriver driver, int index) {
        //getDescription, getPhotoURL에서 포스팅 사진 태그를 읽어들이기 위한 공통적인 사전작업
        this.driver = driver;
        try {
            driver.findElement(By.xpath("//*[@id=\"react-root\"]/section/main/div/div/article/div[1]/div/div/div/div[2]/button"));
            try {
                if (index == 1) {
                    try {
                        this.element = driver.findElement(By.xpath("//*[@id=\"react-root\"]/section/main/div/div/article/div[1]/div/div/div/div[2]/div/div/div/ul/li[1]/div/div/div/div[1]/img"));
                    } catch (Exception have_friend_tag) {
                        this.element = driver.findElement(By.xpath("//*[@id=\"react-root\"]/section/main/div/div/article/div[1]/div/div/div/div[2]/div/div/div/ul/li[1]/div/div/div/div[1]/div[1]/img"));
                    }
                }
                else {
                    try {
                        this.element = driver.findElement(By.xpath("//*[@id=\"react-root\"]/section/main/div/div/article/div[1]/div/div/div/div[2]/div/div/div/ul/li["
                                + index + "]/div/div/div/div/div[1]/img"));
                    } catch (Exception have_friend_tag) {
                        this.element = driver.findElement(By.xpath("//*[@id=\"react-root\"]/section/main/div/div/article/div[1]/div/div/div/div[2]/div/div/div/ul/li["
                                + index + "]/div/div/div/div/div[1]/div[1]/img"));
                    }
                }
            } catch (Exception have_friend_tag) {
                try {
                    this.element = driver.findElement(By.xpath("//*[@id=\"react-root\"]/section/main/div/div/article/div[1]/div/div/div/div[2]/div/div/div/ul/li["
                            + index + "]/div/div/div/div/div[1]/div[1]/img"));
                } catch (Exception no_image_exist) {
                    this.element = null;
                }
            }
        } catch (Exception post_has_one_photo) {
            try {
                this.element = driver.findElement(By.xpath("//*[@id=\"react-root\"]/section/main/div/div/article/div[1]/div/div/div[1]/img"));
            } catch (Exception have_friend_tag) {
                try {
                    this.element = driver.findElement(By.xpath("//*[@id=\"react-root\"]/section/main/div/div/article/div[1]/div/div/div[1]/div[1]/img"));
                } catch (Exception no_image_exist) {
                    this.element = null;
                }
            }
        }
        return element;
    }

    private String getPhotoSRC (WebDriver driver, int index) {
        //aws에 저장할 포스팅 사진의 src값 얻어오는 작업
        this.driver = driver;
        String photoSRC = null;
        try {
            this.element = setPhotoURLElement(driver, index);
            if (!(element == null)) {
                photoSRC = element.getAttribute("srcset");
            }
        } catch (Exception one_more_try) {
            try {
                this.element = setPhotoURLElement(driver, index);
                if (!(element == null)) {
                    photoSRC = element.getAttribute("srcset");
                }
            } catch (Exception no_src_exist) {
                photoSRC = null;
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

    @Override
    public boolean clickNextButton(WebDriver driver) {
        this.driver = driver;
        boolean doesNextExist = true;
        try {
            driver.findElement(By.cssSelector("body > div._2dDPU.vCf6V > div.EfHg9 > div > div > a.HBoOv.coreSpriteRightPaginationArrow")).click();
            doesNextExist = true;
            Thread.sleep(1500);
        } catch (Exception reach_last_post) {
            doesNextExist = false;
            driver.findElement(By.xpath("/html/body/div[4]/button[1]")).click();
            //새로운 검색어 넣는 작업 필요 (리턴값이 false면)
        }
        return doesNextExist;
    }
}