package org.webapp.batch.foodJob;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.webapp.batch.CrawlingDelegator;
import org.webapp.dataset.InstaCrawlImpl;
import org.webapp.model.Instafood;
import org.webapp.model.Overall;
import java.sql.Date;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@StepScope
@Component
public class GetInstaFoodPostsProcessor extends CrawlingDelegator<Instafood>
                                            implements ItemProcessor<Overall, List<Instafood>> {

    private static final Logger logger = LoggerFactory.getLogger(GetInstaFoodPostsProcessor.class);
    private InstaCrawlImpl instaCrawlImpl;
    private FoodStepsDataShareBean dataShareBean;
    private static String url = "https://www.instagram.com/explore/tags/";

    GetInstaFoodPostsProcessor() {}

    @Autowired
    public GetInstaFoodPostsProcessor(InstaCrawlImpl instaCrawlImpl,
                                      FoodStepsDataShareBean dataShareBean) {
        this.instaCrawlImpl = instaCrawlImpl;
        this.dataShareBean = dataShareBean;
    }

    private WebDriver getReadyForCrawling(String station) {
        WebDriver webDriver = null;

        try {
            webDriver = instaCrawlImpl.setUpWebDriver(url + station + "맛집", station);
        } catch(Exception e) {
            logger.error("Check your chrome-WebDriver location in local.");
        }

        return webDriver;
    }

    @Override
    public List<Instafood> process(Overall overall) throws Exception {
        logger.info("[FindHotFoodJob] : SetupInstaFoodPosts-ItemProcessor started.");

        List<Instafood> objectList = new ArrayList<>();
        Instafood object;
        String station = overall.getStation();
        int chance = 0;
        ZoneId zid = ZoneId.of("Asia/Seoul");
        Date limitDate;
        Date uploadDate;
        Map<Instafood, String> pageLinks = new HashMap<>();
        Date today = Date.valueOf(ZonedDateTime.now().withZoneSameInstant(zid).toLocalDate());
        Date latestFood = this.dataShareBean.getLatestFoodDateByStation(station);
        WebDriver webDriver;
        int index = 1;
        long likeCnt = 0;

        if(latestFood == null) {
            limitDate = Date.valueOf(ZonedDateTime
                                    .now()
                                    .withZoneSameInstant(zid)
                                    .minusMonths(2)
                                    .toLocalDate());
        }
        else {
            limitDate = latestFood;
        }

        webDriver = getReadyForCrawling(station);

        if(webDriver == null) {
            return null;
        }

        while(true) {

            if(index == 1) {
                index = this.instaCrawlImpl.waitPage(webDriver, 1, 9); //4
            }
            else {
                // if last page
                if (instaCrawlImpl.waitPage(webDriver, 2, 0) == 3) {
                    break;
                }
            }

            uploadDate = instaCrawlImpl.getDate(webDriver);

            if(uploadDate != null && uploadDate.equals(limitDate)) {
                if(chance < 11) {
                    chance ++;
                    continue;
                }
                else if(chance == 11) {
                    break;
                }

            } else if(uploadDate != null && uploadDate.before(limitDate)) {

                if(limitDate.toString().equals("1970-01-01")) {
                    continue;
                }
                else if(chance < 11) {
                    chance ++;
                    continue;
                }
                else if(chance == 11) {
                    break;
                }

            } else if(uploadDate != null && uploadDate.equals(today)) {
                continue;
            }

            chance = 0;

            object = new Instafood();
            likeCnt = instaCrawlImpl.getLikeCNT(webDriver);

            object.setStation(station);
            object.setDate(uploadDate);
            object.setLikeCNT(likeCnt);

            pageLinks.put(object, instaCrawlImpl.getPhotopageURL(webDriver));
        }

        this.dataShareBean.addStation(station);
        super.setRestaurants(overall.getRestaurantsOfJson());
        objectList = super.setS3ImageUrlAndPost(instaCrawlImpl, webDriver, pageLinks);

        return objectList;
    }
}
