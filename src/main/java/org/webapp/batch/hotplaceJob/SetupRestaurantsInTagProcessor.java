package org.webapp.batch.hotplaceJob;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.webapp.batch.FindMyRestaurantsInList;
import org.webapp.config.ChromeDriverContext;
import org.webapp.dataset.InstaCrawlImpl;
import org.webapp.model.Instafood;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@StepScope
@Component
public class SetupRestaurantsInTagProcessor extends FindMyRestaurantsInList
                                            implements ItemProcessor<Map.Entry<Instafood, String>, Instafood> {

    private static final Logger logger = LoggerFactory.getLogger(SetupRestaurantsInTagProcessor.class);

    private Instafood instafood;
    private InstaCrawlImpl instaCrawl;
    private ChromeDriverContext driverContext;
    private WebDriver driver;
    private HotPlaceStepsDataShareBean dataShareBean;

    SetupRestaurantsInTagProcessor() {}

    @Autowired
    public SetupRestaurantsInTagProcessor(InstaCrawlImpl instaCrawl,
                                          ChromeDriverContext driverContext,
                                          HotPlaceStepsDataShareBean dataShareBean) {
        this.instaCrawl = instaCrawl;
        this.driverContext = driverContext;
        this.dataShareBean = dataShareBean;
    }

    private void getReadyForCrawling() throws Exception {
        this.instaCrawl.setIsFoodPost(1); //to get only 'food' photo-url
        this.driver = driverContext.setupChromeDriver();
        this.driver.manage().window().maximize();
        this.instaCrawl.setWebDriverWait(new WebDriverWait(this.driver, 40));
    }

    @Override
    public Instafood process(Map.Entry<Instafood, String> entry) throws Exception {
        logger.info("[FindHotPlaceJob] : SetupRestaurantsInTag-ItemProcessor started.");
        //instafood : station, Date, likeCNT 채워져있는 상태
        String photoUrl;
        String post;
        List<String> restaurants;
        List<String> myRestaurants = new ArrayList<>();
        String station = entry.getKey().getStation();

        if(this.driver == null) {
            getReadyForCrawling();
        }

        photoUrl = instaCrawl.getPhotoURL(this.driver, station, entry.getValue());

        if(! photoUrl.equals("")) {
            instafood = entry.getKey();
            post = instaCrawl.getPost(this.driver);
            restaurants = this.dataShareBean.getRestaurantsPerStation(station);
            myRestaurants = super.computingMyRestaurants(restaurants, myRestaurants, post);
            System.out.println("========이게 디비 저장==========");
            System.out.println(post);
            System.out.println("&&&이 역의 음식점들 : " + myRestaurants);

            if(myRestaurants.size()!=0) {
                instafood.setPost(post);
                instafood.setMyRestaurant(myRestaurants);
                instafood.setPhotoURL(photoUrl);

                return instafood;
            }
        }

        return null;
    }
}
