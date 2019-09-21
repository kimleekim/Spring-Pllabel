package org.webapp.batch.locationJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.webapp.model.Instafood;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


@StepScope
@Component
public class SetupRestaurantsInLocationProcessor implements ItemProcessor<Instafood, Instafood> {
    private static Logger logger = LoggerFactory.getLogger(SetupRestaurantsInLocationProcessor.class);

    private LocationStepsDataShareBean dataShareBean;

    SetupRestaurantsInLocationProcessor() {}

    @Autowired
    public SetupRestaurantsInLocationProcessor(LocationStepsDataShareBean dataShareBean) {
        this.dataShareBean = dataShareBean;
    }

    @Override
    public Instafood process(Instafood instafood) {
        logger.info("[SearchLocationJob] : SetupRestaurantsInLocation-ItemProcessor started.");
        String station = instafood.getStation();
        List<String> restaurants;
        List<String> myRestaurants = new ArrayList<>();
        String post = instafood.getPost();
        StringTokenizer stringTokenizer;
        restaurants = this.dataShareBean.getRestaurantsPerStation(station);

        for(String restaurant : restaurants) {
            boolean checkContains = true;
            boolean checkFirstWord = false;
            String trimmedRestaurant = restaurant
                        .replace("(주)", "")
                        .replace("u0026", " ");
            stringTokenizer = new StringTokenizer(trimmedRestaurant);

            while(stringTokenizer.hasMoreTokens()) {
                String token = stringTokenizer.nextToken();
                if(checkFirstWord && token.endsWith("점")) {
                    break;
                }

                checkFirstWord = true;

                if(! post.contains(token)) {
                    checkContains = false;
                    break;
                }
            }

            if(checkContains) {
                myRestaurants.add(restaurant.replace("u0026", "&"));
            }

        }
        instafood.setMyRestaurant(myRestaurants);

        return instafood;
    }
}
