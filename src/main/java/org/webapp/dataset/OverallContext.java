package org.webapp.dataset;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.webapp.config.ChromeDriverContext;
import org.webapp.dao.OverallDao;

import javax.sql.DataSource;
import java.util.*;

@Component
public class OverallContext {
    @Autowired
    CSVFileContext stationContext;
    @Autowired
    DataSource dataSource;
    @Autowired
    ChromeDriverContext chromeDriverContext;
    @Autowired
    OverallDao overallDao;

    private List<Map<String, String>> subwayList;
    private List<String> stationList = new ArrayList<>();
    private WebDriver webDriver;
    private WebElement searchArea;
    private String url = "https://www.google.co.kr/maps";

    @Autowired
    OverallContext(CSVFileContext csvFileContext, ChromeDriverContext chromeDriverContext) {
        this.subwayList = csvFileContext.readFile();
        this.chromeDriverContext = chromeDriverContext;
    }

    OverallContext() {
    }

    public void getStationList () throws Exception {
        this.webDriver = chromeDriverContext.setupChromeDriver();
        List<WebElement> locations = new ArrayList<>();
        webDriver.get(url);
        for (Map<String, String> subway : subwayList) {
            int count = 0;
            boolean isSubwayMark = false;
            System.out.println("-----------------------------------------------------------------");
            Thread.sleep(1500);
            searchArea = webDriver.findElement(By.name("q"));
            searchArea.sendKeys(subway.get("station") + "역");
            Thread.sleep(2000);
            try {
                List<WebElement> ss = webDriver.findElements(By.xpath("//div[@class=\"sbqs_c\"]/div/div/div[1]"));
                for (WebElement s : ss) {
                    if (s.findElement(By.xpath(".//div")).getAttribute("class")
                            .contains("suggest-icon-container maps-sprite-suggest-transit")) {
                        locations.add(s.findElement(By.xpath(".//span[3]/span")));
                        isSubwayMark = true;
                        System.out.println("지하철역 마크 있는 : " + locations.size());
                    }
                }
                if (!isSubwayMark) {
                    locations = webDriver.findElements(By.xpath("//div[@class=\"sbqs_c\"]/div/div/div[1]/span[3]/span"));
                    System.out.println("지하철역 마크 없는 : " + locations.size());
                }
                for (WebElement location : locations) {
                    if (location.getText().contains("서울특별시")) {
                        count++;
                    }
                }
                System.out.println(count);
                if ((isSubwayMark && count != 0) || (!isSubwayMark && count >= locations.size()/2)) {
                    System.out.println("추가될 지하철역 : " + subway.get("station"));
                    overallDao.save(subway.get("station"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                searchArea.clear();
                locations.clear();
                continue;
            }
        }
    }
}
