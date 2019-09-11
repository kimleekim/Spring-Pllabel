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

import static org.openqa.selenium.support.ui.ExpectedConditions.not;


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
                                if((subway.get("station") + "역").equals("서울역역")) {
                                    overall.setStation(subway.get("station"));
                                }
                                else {
                                    overall.setStation(subway.get("station") + "역");
                                }
                                overallDao.save(overall);
                            }
                            break;
                        }
                    }
                } catch (Exception e) {
                }

            }
            searchArea.clear();

            if (subway.get("station").equals("수원")) {
                System.out.println("추가될 지하철역 : " + subway.get("station"));
                overall.setStation(subway.get("station") + "역");
                overallDao.save(overall);
            }
        }
    }

    public List<String> getRestaurantList(String station) throws Exception {
        int totalCount = 1;
        boolean isFifth = false;
        WebElement searchArea;
        String toKnowPageChanged = null;
        List<WebElement> restaurantList;
        List<String> insertRestaurantList = new ArrayList<>();

        this.webDriver = chromeDriverContext.setupChromeDriver();
        this.webDriverWait = new WebDriverWait(this.webDriver, 20);

        webDriver.get(url);

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"search.keyword.query\"]")));
        searchArea = webDriver.findElement(By.xpath("//*[@id=\"search.keyword.query\"]"));

        checkAndPutKeyword(searchArea, station);
        int totalRestaurantCount = Integer.parseInt(webDriver.findElement(By.xpath("//*[@id=\"info.search.place.cnt\"]"))
                .getText()
                .replaceAll("\\,", ""));

        while (true) {
            restaurantList = webDriver.findElements(By.xpath("//*[@id=\"info.search.place.list\"]/li"));

            try {
                toKnowPageChanged = webDriver.findElement(By.xpath("//*[@id=\"info.search.place.list\"]/li[1]/div[3]/strong/a[2]")).getText();
            } catch (NoSuchElementException lastpage) {
                if (totalRestaurantCount < 500 && totalCount < totalRestaurantCount/15 || totalRestaurantCount >= 500 && totalCount <= 34) {
                    continue;
                }
                else {
                    break;
                }
            } catch (TimeoutException e) {
                System.out.println("1 timeout");
                continue;
            }

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
                } catch (NoSuchElementException bugORad) {
                    continue;
                } catch (TimeoutException ee) {
                    System.out.println("2 timeout");
                    continue;
                }
            }
            if (totalCount == 1) {
                try {
                    retryingFindClick(By.xpath("//*[@id=\"info.search.place.more\"]"));
                } catch (NoSuchElementException e) {
                    break;
                } catch (TimeoutException ee) {
                    System.out.println("3 timeout");
                    continue;
                }
            }
            else {
                if (isFifth) {
                    isFifth = false;
                    try {
                        retryingFindClick(By.xpath("//*[@id=\"info.search.page.next\"]"));
                    } catch (NoSuchElementException e) {
                        break;
                    } catch (TimeoutException ee) {
                        System.out.println("4 timeout");
                        continue;
                    }
                }
                else if (totalCount % 5 == 0) {
                    try {
                        isFifth = true;
                        retryingFindClick(By.xpath("//div[@id=\"info.search.page\"]/div/a[" + 5 + "]"));
                    } catch (NoSuchElementException e) {
                        break;
                    } catch (TimeoutException ee) {
                        System.out.println("5 timeout");
                        continue;
                    }
                }
                else {
                    try {
                        retryingFindClick(By.xpath("//div[@id=\"info.search.page\"]/div/a[" + totalCount % 5 + "]"));
                    } catch (NoSuchElementException e) {
                        break;
                    } catch (TimeoutException ee) {
                        System.out.println("6 timeout");
                        continue;
                    }
                }
            }
            totalCount++;
            try {
                webDriverWait.until(not(ExpectedConditions.textToBePresentInElement(webDriver.findElement(By.xpath("//*[@id=\"info.search.place.list\"]/li[1]/div[3]/strong/a[2]")), toKnowPageChanged)));
            } catch(TimeoutException e) {
                System.out.println("7 timeout");
                if (totalRestaurantCount/15 < 34 && totalCount >= totalRestaurantCount/15) {
                    break;
                }
                else if (totalCount > 34) {
                    break;
                }
                else {
                    totalCount--;
                    continue;
                }
            } catch (NoSuchElementException ee) {
                continue;
            }
            searchArea.clear();
        }
        System.out.println(station + ": " + insertRestaurantList.size());
        return insertRestaurantList;
    }

    private void checkAndPutKeyword(WebElement searchArea, String station) {
        boolean isWebLoad = false;
        String checkWebLoad = null;
        String rightPlace = null;

        searchArea.clear();
        searchArea.sendKeys(station + " 음식점");

        while(!isWebLoad) {
            searchArea.submit();
            searchArea.sendKeys(Keys.ENTER);
            searchArea.clear();
            try {
                try {
                    webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"info.searchHeader.message\"]/div/div[1]/p")));
                } catch(TimeoutException e) {
                    searchArea.sendKeys(station + " 음식점");
                    continue;
                }
                try {
                    checkWebLoad = webDriver.findElement(By.id("search.keyword.query")).getAttribute("value");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int count = StringUtils.countOccurrencesOf(checkWebLoad, station);
                if (count == 1) {
                    isWebLoad = true;
                }
                else {
                    searchArea.clear();
                    searchArea.sendKeys(station + " 음식점");
                }

            } catch (NoSuchElementException e) {
                searchArea.clear();
                searchArea.sendKeys(station + " 음식점");
                continue;
            }
        }
        rightPlace = checkRightPlace(searchArea, station);

        if(rightPlace != null) {
            checkAndPutKeyword(searchArea, rightPlace);
        }
    }

    private String checkRightPlace(WebElement searchArea, String station) {
        String searchHeader = null;
        String resultPlace = null;
        String checkedStation = null;
        By searchHeaderLocator;

        searchHeaderLocator = By.xpath("//*[@id=\"info.searchHeader.message\"]/div/div[1]/p");
        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(searchHeaderLocator));
        searchHeader = webDriver.findElement(searchHeaderLocator)
                .getText();
        try {
            resultPlace = webDriver
                    .findElement(By.xpath("//*[@id=\"info.search.place.list\"]/li[1]/div[5]/div[2]/p[1]"))
                    .getText();
        } catch(NoSuchElementException e) {
            resultPlace = "";
        }
        if(! searchHeader.contains("서울") && !resultPlace.substring(0, 2).equals("서울")) {
            checkedStation = searchRightPlace(searchArea, station);
        }
        else if(resultPlace == "") {
            checkedStation = null;
        }
        return checkedStation;
    }

    private String searchRightPlace(WebElement searchArea, String station) {
        By detailPlaceLocator = null;
        int listCount = 1;
        int endCount;
        String xpath;
        String subway;
        boolean onlyOneResult = false;
        String resultCount;

        resultCount = webDriver.findElement(By.xpath("//*[@id=\"info.search.place.cnt\"]")).getText();
        searchArea.clear();
        webDriverWait.until(ExpectedConditions.textToBePresentInElement(searchArea, ""));
        searchArea.sendKeys("지하철 " + station);

        retryingFindClick(By.xpath("//*[@id=\"search.keyword.submit\"]"));
        webDriverWait.until(not(ExpectedConditions.textToBe(By.xpath("//*[@id=\"info.search.place.cnt\"]"), resultCount)));
        endCount = webDriver.findElements(By.xpath("//*[@id=\"info.search.place.list\"]/li")).size();

        if(endCount > 16)
            endCount = 16;

        while(listCount <= endCount) {
            try {
                xpath = "//*[@id=\"info.search.place.list\"]/li[" + listCount + "]/div[3]";
                subway = webDriver.findElement(By.xpath(xpath + "/span")).getText();

                if(endCount == 2) {
                    onlyOneResult = true;
                    break;
                }

                if(! webDriver
                        .findElement(By.cssSelector("#info\\.search\\.place\\.list > li:nth-child(" + listCount + ") > div.head_item.clickArea > strong > a.link_name"))
                        .getText()
                        .contains(station)) {

                    break;
                }

                if(subway.substring(0, 3).equals("수도권") || subway.equals("분당선")) {
                    break;
                }
                else {
                    listCount++;
                    continue;
                }

            } catch(NoSuchElementException e) {
                listCount++;
                continue;
            }
        }

        if(onlyOneResult) {
            detailPlaceLocator = By.cssSelector("#info\\.search\\.place\\.list > li.PlaceItem.clickArea.PlaceItem-DUP.PlaceItem-ACTIVE > div.head_item.clickArea > span");
        }
        else {
            detailPlaceLocator = By.cssSelector("#info\\.search\\.place\\.list > li:nth-child(" + listCount + ") > div.head_item.clickArea > span");
        }

        retryingFindClick(detailPlaceLocator);

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"info.search.place.breadcrumb\"]")));

        if(webDriver.findElement(By.xpath("//*[@id=\"info.search.place.breadcrumb\"]/a[2]")).getText().equals("지하철,전철")) {

            return webDriver
                    .findElement(By.xpath("//*[@id=\"info.search.place.list\"]/li[1]/div[3]/strong/a[2]"))
                    .getText();
        }

        else return searchRightPlace(searchArea, station);

    }

    private void retryingFindClick(By by) {
        WebElement element;
        JavascriptExecutor executor;

        while(true) {
            try {
                element = webDriver.findElement(by);
                executor = (JavascriptExecutor)webDriver;
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
}

