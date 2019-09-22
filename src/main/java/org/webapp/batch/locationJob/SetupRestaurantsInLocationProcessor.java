package org.webapp.batch.locationJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.webapp.batch.FindMyRestaurantsInList;
import org.webapp.model.Instafood;

import java.util.ArrayList;
import java.util.List;


@StepScope
@Component
public class SetupRestaurantsInLocationProcessor extends FindMyRestaurantsInList
                                                implements ItemProcessor<Instafood, Instafood> {

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
        restaurants = this.dataShareBean.getRestaurantsPerStation(station);

        myRestaurants = super.computingMyRestaurants(restaurants, myRestaurants, post);

        instafood.setMyRestaurant(myRestaurants);

        return instafood;
    }
}
