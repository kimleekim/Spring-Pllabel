package org.webapp.batch.locationJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.webapp.model.Instafood;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component("LocationStepsDataShareBean")
public class LocationStepsDataShareBean {
    private static final Logger logger = LoggerFactory.getLogger(LocationStepsDataShareBean.class);

    private Map<String, Date> latestDatePerStation;
    private List<Instafood> instafoodList;
    private Map<String, List<String>> restaurantsPerStation;


    public LocationStepsDataShareBean() {
        latestDatePerStation = new HashMap<>();
        instafoodList = new ArrayList<>();
        restaurantsPerStation = new HashMap<>();
    }

    public void putLatestDatePerStation(String station, Date date) {
        if(this.latestDatePerStation == null) {
            logger.error("LatestDatePerStation-Map is not initialized.");
        }

        this.latestDatePerStation.putIfAbsent(station, date);
    }

    public void updateLastestDate(String station, Date date) {
        if(this.latestDatePerStation == null) {
            logger.error("LatestDatePerStation-Map is not initialized.");
        }

        this.latestDatePerStation.computeIfPresent(station, (String key, Date value) -> date);
    }

    public void addInstaFoodList(Instafood instafood) {
        if(this.instafoodList == null) {
            logger.error("Instafood-List is not initialized.");
        }

        this.instafoodList.add(instafood);
    }

    public void putRestaurantsPerStation(String station, List<String> restaurants) {
        if(this.restaurantsPerStation == null) {
            logger.error("RestaurantsPerStation-Map is not initialized.");
        }

        this.restaurantsPerStation.putIfAbsent(station, restaurants);
    }

    public Map<String, Date> getLatestDatePerStation() {
        if(this.latestDatePerStation == null) {
            logger.error("LatestDatePerStation-Map is not initialized.");
            return null;
        }

        return this.latestDatePerStation;
    }

    public List<Instafood> getInstafoodList() {
        if(this.instafoodList == null) {
            logger.error("Instafood-List is not initialized.");
            return null;
        }

        return this.instafoodList;
    }

    public List<String> getRestaurantsPerStation(String station) {
        if(this.restaurantsPerStation == null) {
            logger.error("RestaurantsPerStation-Map is not initialized.");
            return null;
        }

        return this.restaurantsPerStation.get(station);
    }
}
