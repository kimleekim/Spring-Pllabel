package org.webapp.dataset;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.webapp.config.ChromeDriverContext;
import org.webapp.dao.SettingStationDao;

import java.util.*;

public class StationContext {
    @Autowired
    CSVFileContext stationContext = new CSVFileContext();
    private List<Map<String, String>> subwayList = new ArrayList<>();
    private SettingStationDao settingStationDao = new SettingStationDao();
    private List<String> stationList = new ArrayList<>();

    @Autowired
    ChromeDriverContext chromeDriverContext = new ChromeDriverContext();
    private WebDriver webDriver;
    private WebElement searchArea;
    private String url = "https://www.google.co.kr/maps";

    public void getStationList () throws Exception {
        subwayList = stationContext.readFile();
        webDriver = chromeDriverContext.setupChromeDriver();
        webDriver.get(url);
        for (Map<String, String> subway : subwayList) {
            Thread.sleep(2000);
            searchArea = webDriver.findElement(By.name("q"));
            searchArea.sendKeys(subway.get("station") + "역");
            Thread.sleep(2000);
            try {
                List<WebElement> locations = webDriver.findElements(By.xpath("//div[@class=\"sbqs_c\"]/div/div/div[1]/span[3]/span"));
                int count = 0;

                for (WebElement location : locations) {
                    if (location.getText().contains("서울특별시")) {
                        count++;
                    }
                }
                if (count >= locations.size()/2) {
                    stationList.add(subway.get("station"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                System.out.println(stationList);
                searchArea.clear();
                continue;
            }
        }
        settingStationDao.setStation(stationList);
        chromeDriverContext.dropChromeDriver();
    }
}
