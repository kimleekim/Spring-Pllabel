package org.webapp.batch.foodJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.*;

@Component
public class FoodStepsDataShareBean {
    private static final Logger logger = LoggerFactory.getLogger(FoodStepsDataShareBean.class);

    private Map<String, Date> latestFoodDate = null;
    private List<String> stations = null;
    private Map<String, List<String>> top10RestaurantsPerStation = null;

    public FoodStepsDataShareBean() {
        this.latestFoodDate = new HashMap<>();
        this.stations = new ArrayList<>();
        this.top10RestaurantsPerStation = new HashMap<>();
    }

    public void putLatestFoodDatePerStation(String station, Date date) {
        if(this.latestFoodDate == null) {
            logger.error("latestFoodDate-Map is not initialized.");
        }

        this.latestFoodDate.putIfAbsent(station, date);
    }

    public void addStation(String station) {
        if(this.stations == null) {
            logger.error("stations-List is not initialized.");
        }

        this.stations.add(station);
    }

    public void putTop10RestaurantsPerStation(Map<String, List<String>> entry) {
        if(this.top10RestaurantsPerStation == null) {
            logger.error("top10RestaurantsPerStation-Map is not initialized.");
        }

        for(String station : entry.keySet()) {
            this.top10RestaurantsPerStation.put(station, entry.get(station));
        }
    }

    public Date getLatestFoodDateByStation(String station) {
        if(this.latestFoodDate == null) {
            logger.error("latestFoodDate-Map is not initialized.");
            return null;
        }

        return this.latestFoodDate.get(station);
    }

    public List<String> getStations() {
        if(this.stations == null) {
            logger.error("stations-List is not initialized.");
            return null;
        }

        return this.stations;
    }

    public Map<String, List<String>> getTop10RestaurantsPerStation() {
        if(this.top10RestaurantsPerStation == null) {
            logger.error("top10RestaurantsPerStation-Map is not initialized.");
            return null;
        }

        return this.top10RestaurantsPerStation;
    }
}
