package org.webapp.dataset;

import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.webapp.config.ChromeDriverContext;
import org.webapp.dao.OverallDao;
import org.webapp.model.Overall;

import javax.sql.DataSource;
import java.util.*;


@Component
public class MapSearchContextImpl implements MapSerachContext {
    @Autowired
    OverallDao overallDao;
    private ChromeDriverContext chromeDriverContext;
    private List<Map<String, String>> subwayList;
    private WebDriver webDriver;
    private WebDriverWait webDriverWait;
    private CSVFileContext csvFileContext;
    private String url = "https://map.kakao.com/";

    @Autowired
    MapSearchContextImpl(CSVFileContext csvFileContext, ChromeDriverContext chromeDriverContext) {
        this.csvFileContext = csvFileContext;
        this.chromeDriverContext = chromeDriverContext;
    }

    MapSearchContextImpl() {
    }

    public void getStationFile(String url) {
        this.subwayList = csvFileContext.readFile(url);
    }

    public void getStationList () throws Exception {
        Overall overall = new Overall();
        this.webDriver = chromeDriverContext.setupChromeDriver();
        webDriver.get(url);
        for (Map<String, String> subway : subwayList) {
            if (subway.get("station").equals("총신대입구")) {
                continue;
            }
            WebElement searchArea = webDriver.findElement(By.xpath("//*[@id=\"search.keyword.query\"]"));
            searchArea.sendKeys(subway.get("station") + "역");
            searchArea.sendKeys(Keys.ENTER);
            Thread.sleep(1500);

            List<WebElement> stationList = webDriver.findElements(By.xpath("//*[@id=\"info.search.place.list\"]/li"));
            for (WebElement station : stationList) {
                try {
                    if (station.findElement(By.xpath(".//div[3]/span")).getText().contains("수도권") || station.findElement(By.xpath(".//div[3]/span")).getText().contains("분당선")) {
                        if (station.findElement(By.xpath(".//div[3]/strong/a[2]")).getAttribute("title").contains(subway.get("station"))) {
                            if (station.findElement(By.xpath(".//div[5]/div[2]/p[1]")).getText().contains("서울")) {
                                System.out.println("추가될 지하철역 : " + subway.get("station"));
                                overall.setStation(subway.get("station"));
                                overallDao.save(overall);
                            }
                            break;
                        }
                    }
                } catch (Exception e) {
                    continue;
                }
            }
            searchArea.clear();
        }
    }

    public List<String> getRestaurantList(String station) throws Exception {
        int totalCount = 1;
        boolean isFifth = false;
        String checkWebLoad = null;
        boolean isWebLoad = false;
        List<String> insertRestaurantList = new ArrayList<>();

        this.webDriver = chromeDriverContext.setupChromeDriver();
        this.webDriverWait = new WebDriverWait(this.webDriver, 20);

        webDriver.get(url);
        WebElement searchArea = webDriver.findElement(By.xpath("//*[@id=\"search.keyword.query\"]"));
        searchArea.clear();
        searchArea.sendKeys(station + "역 음식점");

        while(!isWebLoad) {
            System.out.println(111111);
            searchArea.sendKeys(Keys.ENTER);
            searchArea.clear();
            try {
                try {
                    webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"info.searchHeader.message\"]/div/div[1]/p")));
                } catch(TimeoutException e) {
                    continue;
                }
                System.out.println(333);
                try {
                    checkWebLoad = webDriver.findElement(By.id("search.keyword.query")).getAttribute("value");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int count = StringUtils.countOccurrencesOf(checkWebLoad, station + "역");
                if (count == 1) {
                    isWebLoad = true;
                }
                else {
                    searchArea.clear();
                    searchArea.sendKeys(station + "역 음식점");
                }
            } catch (NoSuchElementException e) {
                searchArea.clear();
                searchArea.sendKeys(station + "역 음식점");
                continue;
            }
        }
        System.out.println(44444);
        while (true) {
            System.out.println("페이지 넘어갔다.");
            List<WebElement> restaurantList = webDriver.findElements(By.xpath("//*[@id=\"info.search.place.list\"]/li"));

            for (WebElement restaurant : restaurantList) {
                try {
                    if (restaurant.findElement(By.xpath(".//div[3]/span")).getText().contains("전문점") ||
                            restaurant.findElement(By.xpath(".//div[3]/span")).getText().contains("카페") ||
                            restaurant.findElement(By.xpath(".//div[3]/span")).getText().contains("패스트푸드"))
                    {
                        continue;
                    }
                    else {
                        if (!insertRestaurantList.contains(restaurant.findElement(By.xpath(".//div[3]/strong/a[2]")).getText())) {
                            insertRestaurantList.add(restaurant.findElement(By.xpath(".//div[3]/strong/a[2]")).getText());
                        }
                    }
                } catch (Exception e) {
                    continue;
                }
            }
            if (totalCount == 1) {
                try {
                    webDriver.findElement(By.xpath("//*[@id=\"info.search.place.more\"]")).sendKeys(Keys.ENTER);
                } catch (Exception e) {
                    break;
                }
            }
            else {
                if (isFifth) {
                    isFifth = false;
                    try {
                        webDriver.findElement(By.xpath("//*[@id=\"info.search.page.next\"]")).sendKeys(Keys.ENTER);
                    } catch (Exception e) {
                        break;
                    }
                }
                else if (totalCount % 5 == 0) {
                    try {
                        isFifth = true;
                        webDriver.findElement(By.xpath("//div[@id=\"info.search.page\"]/div/a[" + 5 + "]")).sendKeys(Keys.ENTER);
                    } catch (Exception e) {
                        break;
                    }
                }
                else {
                    try {
                        webDriver.findElement(By.xpath("//div[@id=\"info.search.page\"]/div/a[" + totalCount % 5 + "]")).sendKeys(Keys.ENTER);
                    } catch (Exception e) {
                        break;
                    }
                }
            }
            totalCount++;
            Thread.sleep(1000);
//            webDriverWait.until(webDriver1 ->  ((JavascriptExecutor)webDriver1).executeScript("return document.readyState").equals("complete"));
            searchArea.clear();
        }
        return insertRestaurantList;
    }
}
