package org.webapp.dataset;

import org.openqa.selenium.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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
                                overallDao.save(subway.get("station"));
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
        List<String> insertRestaurantList = new ArrayList<>();

        this.webDriver = chromeDriverContext.setupChromeDriver();
        webDriver.get(url);
        WebElement searchArea = webDriver.findElement(By.xpath("//*[@id=\"search.keyword.query\"]"));
        searchArea.sendKeys(station + "역 맛집");
        searchArea.sendKeys(Keys.ENTER);
        Thread.sleep(1500);

        while (true) {
            List<WebElement> restaurantList = webDriver.findElements(By.xpath("//*[@id=\"info.search.place.list\"]/li"));
            //검색목록 n번째 페이지의 for문
            for (WebElement restaurant : restaurantList) {
                try {
                    if (restaurant.findElement(By.xpath(".//div[3]/span")).getText().contains("커피전문점") ||
                            restaurant.findElement(By.xpath(".//div[3]/span")).getText().contains("카페")) {
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
                        webDriver.findElement(By.xpath("//div[@id=\"info.search.page\"]/div/a[" + totalCount + "]")).sendKeys(Keys.ENTER);
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
            Thread.sleep(1500);
            totalCount++;
        }
        return insertRestaurantList;
    }
}
