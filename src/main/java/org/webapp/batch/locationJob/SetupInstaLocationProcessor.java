package org.webapp.batch.locationJob;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.webapp.batch.CrawlingDelegator;
import org.webapp.dataset.InstaCrawlImpl;
import org.webapp.model.Instahot;
import org.webapp.model.Instaplace;
import org.webapp.model.Overall;

import java.sql.Date;
import java.text.ParseException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@StepScope
@Component
public class SetupInstaLocationProcessor extends CrawlingDelegator<Instaplace>
                                                implements ItemProcessor<Overall, List<Instaplace>> {

    private static final Logger logger = LoggerFactory.getLogger(SetupInstaLocationProcessor.class);
    private LocationStepsDataShareBean dataShareBean;
    private InstaCrawlImpl instaCrawlImpl;
    private WebDriver webDriver;
    private static String tempUrl = "https://www.instagram.com/explore/tags/location";

    SetupInstaLocationProcessor() {}

    @Autowired
    public SetupInstaLocationProcessor(LocationStepsDataShareBean dataShareBean,
                                       InstaCrawlImpl instaCrawlImpl) {
        this.dataShareBean = dataShareBean;
        this.instaCrawlImpl = instaCrawlImpl;
    }

    private WebDriver getReadyForCrawling(String station) {
        String url = "";
        WebDriver webDriver = null;

        try {
            url = this.instaCrawlImpl.setUpWebDriverForLocation(tempUrl, station);

            if(url.equals(""))
                return null;
            else {
                webDriver = this.instaCrawlImpl.setUpWebDriver(url, station);
            }

        } catch(Exception e) {
            logger.error("Check your chrome-WebDriver location in local.");
            e.printStackTrace();
        }

        return webDriver;
    }

    @Override
    public List<Instaplace> process(Overall overall) throws Exception {
        logger.info("[SearchLocationJob] : SetupInstaLocation-ItemProcessor started.");

        List<Instaplace> objectList = new ArrayList<>();
        Instaplace object;
        String station = "";
        int chance = 0;
        ZoneId zid = ZoneId.of("Asia/Seoul");
        Date limitDate;
        int index = 1;
        Date uploadDate = null;
        station = overall.getStation();
        Map<Instaplace, String> pagelinks = new HashMap<>();
        Map<String, Date> latestCrawlDate = this.dataShareBean.getLatestDatePerStation();
        Date today = Date.valueOf(ZonedDateTime.now().withZoneSameInstant(zid).toLocalDate());

        this.dataShareBean.putRestaurantsPerStation(station, overall.getRestaurantsOfJson());

        if(latestCrawlDate.size() == 0 || latestCrawlDate.get(station) == null) {
            limitDate = Date.valueOf(ZonedDateTime
                                    .now()
                                    .withZoneSameInstant(zid)
                                    .minusMonths(3)
                                    .toLocalDate());
        }
        else {
            limitDate = latestCrawlDate.get(station);
        }

        this.webDriver = getReadyForCrawling(station);
        if(this.webDriver == null) {
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

            uploadDate = instaCrawlImpl.getDate(this.webDriver);
            System.out.println(uploadDate);

            if(uploadDate != null && uploadDate.equals(limitDate)) {

                if(chance < 5) {
                    chance ++;
                    continue;
                }
                else if(chance == 5) {
                    break;
                }

            } else if(uploadDate != null && uploadDate.before(limitDate)) {

                if(uploadDate.toString().equals("1970-01-01")) {
                    continue;
                }
                else if(chance < 10) {
                    chance ++;
                    continue;
                }
                else if(chance == 10) {
                    break;
                }

            } else if(uploadDate != null && uploadDate.equals(today)) {
                continue;
            }

            chance = 0;
            object = new Instaplace();

            object.setStation(station);
            object.setDate(uploadDate);
            object.setLikeCNT(this.instaCrawlImpl.getLikeCNT(this.webDriver));


            pagelinks.put(object, instaCrawlImpl.getPhotopageURL(this.webDriver));
        }

        super.setDataShareBean(this.dataShareBean);
        objectList = super.setLocationPostContent(this.instaCrawlImpl, this.webDriver, pagelinks);

        return objectList;
    }
}
