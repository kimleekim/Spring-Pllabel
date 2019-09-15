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
    }

    @Override
    public List<Instaranking> process(Instaranking instaranking) throws ParseException {
        logger.info("[FindHotPlaceJob] : SetupPlaceRanking-ItemProcessor started.");

        List<Instaranking> objectList = new ArrayList<>();
        String station;
        String placetag = "";
        long likeCnt = 0;
        Date limitDate = null;
        int chance = 0;
        ZoneId zid = ZoneId.of("Asia/Seoul");
        ZonedDateTime threeMonthsAgo = ZonedDateTime
                                        .now()
                                        .withZoneSameInstant(zid)
                                        .minusMonths(1);
        int index = 1;
        List<String> photolinks = new ArrayList<String>();
        Map<String, Integer> countingTags = new HashMap<>();
        Map<String, Long> countingLikes = new HashMap<>();
        station = instaranking.getStation();

        getReadyForCrawling(station);

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
            System.out.println(limitDate);

            System.out.println("======================새 글===========================");
            if(limitDate != null
                    && limitDate.equals(Date.valueOf(threeMonthsAgo.toLocalDate()))) {

                if(chance < 5) {
                    chance ++;
                    continue;
                }
                else if(chance == 5) {
                    break;
                }

            } else if(limitDate != null
                    && limitDate.before(Date.valueOf(threeMonthsAgo.toLocalDate()))) {

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

            placetag = instaCrawlImpl.getPlacetag(this.webDriver);
            if(! placetag.equals("")) {
                System.out.println(placetag);

                countingTags.computeIfPresent(placetag,
                        (String key, Integer value) -> ++value);
                countingTags.putIfAbsent(placetag, 1);

                likeCnt = instaCrawlImpl.getLikeCNT(this.webDriver);
                long finalLikeCnt = likeCnt;
                System.out.println("좋아요수 : " + finalLikeCnt);

                countingLikes.computeIfPresent(placetag,
                        (String key, Long value) -> value += finalLikeCnt);
                countingLikes.putIfAbsent(placetag, finalLikeCnt);
            }

            photolinks.add(instaCrawlImpl.getPhotopageURL(this.webDriver));
        }

        objectList = setObjectList(station, countingTags, countingLikes, objectList);

        dataShareBean.putFoodPhotoPagelinks(instaCrawlImpl.getIsFoodpost(), photolinks);

        return objectList;
    }

    private List<Instaranking> setObjectList(String station,
                                             Map<String, Integer> countingTags,
                                             Map<String, Long> countingLikes,
                                             List<Instaranking> objectList) {

        Instaranking object;
        Set<String> keyset = countingTags.keySet();

        for(String placetag : keyset) {
            object = new Instaranking();
            object.setStation(station);
            object.setPlacetag(placetag);
            object.setPlacetagCNT(countingTags.get(placetag));
            object.setLikeCNT(countingLikes.get(placetag));

            objectList.add(object);
        }

        return objectList;
    }

}
