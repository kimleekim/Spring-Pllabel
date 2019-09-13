package org.webapp.batch.hotplaceJob;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.webapp.dataset.InstaCrawl;
import org.webapp.dataset.InstaCrawlImpl;
import org.webapp.model.Instaranking;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Component
@StepScope
public class SetupPlaceRankingProcessor implements ItemProcessor<Instaranking, List<Instaranking>> {

    private static final Logger logger = LoggerFactory.getLogger(SetupPlaceRankingProcessor.class);
    private HotPlaceStepsDataShareBean<Instaranking> dataShareBean;
    private InstaCrawlImpl instaCrawlImpl;
    private WebDriver webDriver;
    private static String url = "https://www.instagram.com/explore/tags/";
    private Map<String, Integer> countingTags;


    SetupPlaceRankingProcessor() {}

    @Autowired
    public SetupPlaceRankingProcessor(HotPlaceStepsDataShareBean<Instaranking> dataShareBean,
                                      InstaCrawlImpl instaCrawlImpl) {
        this.dataShareBean = dataShareBean;
        this.instaCrawlImpl = instaCrawlImpl;
    }

    private void getReadyForCrawling(String station) {
        countingTags = new HashMap<>();
        try {
            this.webDriver = this.instaCrawlImpl.setUpWebDriver(this.url + station, station);
        } catch (Exception e) {
            logger.error("Check your chrome-WebDriver location in local.");
        }
        System.out.println(this.webDriver);
    }

    @Override
    public List<Instaranking> process(Instaranking instaranking) throws ParseException {
        logger.info("[FindHotPlaceJob] : SetupPlaceRanking-ItemProcessor started.");

        List<Instaranking> objectList = new ArrayList<>();
        String station;
        String placetag = "";
        Date limitDate = null;
        ZoneId zid = ZoneId.of("Asia/Seoul");
        ZonedDateTime threeMonthsAgo = ZonedDateTime
                                        .now()
                                        .withZoneSameInstant(zid)
                                        .minusMonths(1);
        int index = 1;
        List<String> photolinks = new ArrayList<String>();
        station = instaranking.getStation();

        dataShareBean.setCountingTagsEmpty();
        getReadyForCrawling(station);

        while(true) {

            if(index == 1)
                index = this.instaCrawlImpl.waitPage(this.webDriver, 1, 9); //4
            else {
                // if last page
                if (instaCrawlImpl.waitPage(webDriver, 2, 0) == 3) // 다음 버튼 클릭
                    break;
            }

            limitDate = instaCrawlImpl.getDate(this.webDriver);
            System.out.println(limitDate);

            System.out.println("======================새 글===========================");
            if(limitDate != null
                    && limitDate.equals(Date.valueOf(threeMonthsAgo.toLocalDate()))) {

                break;
            } else if(limitDate != null
                    && limitDate.before(Date.valueOf(threeMonthsAgo.toLocalDate()))) {

                if(limitDate.equals(Date.valueOf("1970-01-01"))) {
                    System.out.println("여기 걸림");
                    continue;
                }
                break;
            }

            placetag = instaCrawlImpl.getPlacetag(this.webDriver);
            if(! placetag.equals("")) {
                System.out.println(placetag);
                instaranking.setPlacetag(placetag);
                dataShareBean.countPlaceTag(placetag);
            }
            instaranking.setLikeCNT(instaCrawlImpl.getLikeCNT(this.webDriver));
            System.out.println(instaCrawlImpl.getLikeCNT(this.webDriver));

            photolinks.add(instaCrawlImpl.getPhotopageURL(this.webDriver));

            objectList.add(instaranking);
        }

        dataShareBean.putPhotoPagelinks(instaCrawlImpl.getIsFoodpost(), photolinks);
        return objectList;
    }

}
