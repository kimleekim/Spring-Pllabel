package org.webapp.batch.foodJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class FoodStepsDataShareBean {
    private static final Logger logger = LoggerFactory.getLogger(FoodStepsDataShareBean.class);

    private Map<String, Date> latestFoodDate = null;

    public FoodStepsDataShareBean() {
        this.latestFoodDate = new HashMap<>();
    }

    public void putLatestFoodDatePerStation(String station, Date date) {
        if(this.latestFoodDate == null) {
            logger.error("latestFoodDate-Map is not initialized.");
        }

        this.latestFoodDate.putIfAbsent(station, date);
    }

    public Date getLatestFoodDateByStation(String station) {
        if(this.latestFoodDate == null) {
            logger.error("latestFoodDate-Map is not initialized.");
            return null;
        }

        return this.latestFoodDate.get(station);
    }
}
