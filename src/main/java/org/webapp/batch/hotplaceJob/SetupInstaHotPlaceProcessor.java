package org.webapp.batch.hotplaceJob;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.webapp.batch.CrawlingDelegator;
import org.webapp.dataset.InstaCrawlImpl;
import org.webapp.model.Instahot;

import java.sql.Date;
import java.text.ParseException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@StepScope
@Component
public class SetupInstaHotPlaceProcessor extends CrawlingDelegator<Instahot>
                        implements ItemProcessor<Map.Entry<String, String>, List<Instahot>> {
    private static final Logger logger = LoggerFactory.getLogger(SetupInstaHotPlaceProcessor.class);

    private InstaCrawlImpl instaCrawlImpl;
    private WebDriver webDriver;
    private static String tempUrl = "https://www.instagram.com/explore/tags/temp";

    SetupInstaHotPlaceProcessor() {}

    @Autowired
    public SetupInstaHotPlaceProcessor(InstaCrawlImpl instaCrawlImpl) {
        this.instaCrawlImpl = instaCrawlImpl;
    }

    private WebDriver getReadyForLocationCrawling(String station, String hotplace) {
        String url = "";
        WebDriver webDriver = null;
        try {

            url = this.instaCrawlImpl.setUpWebDriverForLocation(tempUrl, hotplace);

            if(url.equals("")) {
                return null;
            }
            else {
                webDriver = this.instaCrawlImpl.setUpWebDriver(url, station);
            }
        } catch (Exception e) {
            logger.error("Check your chrome-WebDriver location in local.");
            e.printStackTrace();
        }
        return webDriver;
    }

    @Override
    public List<Instahot> process(Map.Entry<String, String> hotplacePerStation) throws Exception {
        logger.info("[FindHotPlaceJob] : SetupInstaHotPlace-ItemProcessor started.");

        List<Instahot> objectList = new ArrayList<>();
        Map<Instahot, String> photoPagelinks = new HashMap<>();
        Instahot object;
        Date limitDate = null;
        int index = 1;
        int chance = 0;
        ZoneId zid = ZoneId.of("Asia/Seoul");
        ZonedDateTime oneMonthsAgo = ZonedDateTime
                .now()
                .withZoneSameInstant(zid)
                .minusMonths(1);

        this.webDriver = getReadyForLocationCrawling
                (hotplacePerStation.getKey(), hotplacePerStation.getValue());

        if (this.webDriver == null) {
            return null;
        }

        while(true) {

            if(index == 1) {
                index = this.instaCrawlImpl.waitPage(this.webDriver, 1, 9); //4
            }
            else {
                // if last page
                if (instaCrawlImpl.waitPage(webDriver, 2, 0) == 3) { // 다음 버튼 클릭
                    break;
                }
            }

            limitDate = instaCrawlImpl.getDate(this.webDriver);

            if(limitDate != null
                    && limitDate.equals(Date.valueOf(oneMonthsAgo.toLocalDate()))) {

                if(chance < 5) {
                    chance ++;
                    continue;
                }
                else if(chance == 5) {
                    break;
                }

            } else if(limitDate != null
                    && limitDate.before(Date.valueOf(oneMonthsAgo.toLocalDate()))) {

                if(limitDate.toString().equals("1970-01-01")) {
                    continue;
                }
                else if(chance < 5) {
                    chance ++;
                    continue;
                }
                else if(chance == 5) {
                    break;
                }

            }

            chance = 0;
            object = new Instahot();

            object.setStation(hotplacePerStation.getKey());
            object.setDate(limitDate);

            photoPagelinks.put(object, instaCrawlImpl.getPhotopageURL(this.webDriver));
        }

        objectList = super.setS3ImageUrl(instaCrawlImpl, webDriver, photoPagelinks);

        return objectList;
    }

}
