package org.webapp.batch.locationJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;


@Component("LocationStepsDataShareBean")
public class LocationStepsDataShareBean {
    private static final Logger logger = LoggerFactory.getLogger(LocationStepsDataShareBean.class);

    private Map<String, Date> latestDatePerStation;


    public LocationStepsDataShareBean() {
        latestDatePerStation = new HashMap<>();
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

    public Map<String, Date> getLatestDatePerStation() {
        if(this.latestDatePerStation == null) {
            logger.error("LatestDatePerStation-Map is not initialized.");
        }

        return this.latestDatePerStation;
    }
}
