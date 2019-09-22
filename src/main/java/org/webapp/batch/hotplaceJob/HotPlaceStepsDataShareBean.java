package org.webapp.batch.hotplaceJob;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.webapp.model.Instafood;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class HotPlaceStepsDataShareBean {
    private static final Logger logger = LoggerFactory.getLogger(HotPlaceStepsDataShareBean.class);

    private Map<Instafood, String> photoPagelinks;
    private Map<String, String> hotplacePerStation = null;
    private Date latestDate = null;
    private Map<String, List<String>> restaurantsPerStation = null;

    public HotPlaceStepsDataShareBean() {
        this.photoPagelinks = new HashMap<>();
        this.hotplacePerStation = new HashMap<>();
        this.restaurantsPerStation = new HashMap<>();
    }

    public void putFoodPhotoPagelinks(Instafood instafood, String link) {
        if(photoPagelinks == null) {
            logger.error("photoPagelinks-Map is not initialized.");
        }

        photoPagelinks.put(instafood, link);
    }


    public void putHotplacePerStation(String station, String hotplace) {
        if(hotplacePerStation == null) {
            logger.error("hotplacePerStation-Map is not initialized.");
            return;
        }

        hotplacePerStation.put(station, hotplace);
    }

    public void setLatestDate(Date latestDate) {
        this.latestDate = Date.valueOf
                                (String.valueOf(LocalDate.fromDateFields(latestDate).minusDays(1)));
    }

    public void putRestaurantsPerStation(String station, List<String> restaurants) {
        if(restaurantsPerStation == null) {
            logger.error("restaurantsPerStation-Map is not initialized.");
            return;
        }

        restaurantsPerStation.putIfAbsent(station, restaurants);
    }

    public Map<Instafood, String> getPhotoPagelinks() {
        if(photoPagelinks == null) {
            logger.error("photoPagelinks-Map is not initialized.");
            return null;
        }

        return photoPagelinks;
    }

    public Map<String, String> getHotplacePerStation() {
        if(hotplacePerStation == null) {
            logger.error("hotplacePerStation-Map is not initialized.");
            return null;
        }

        return hotplacePerStation;
    }

    public Date getLatestDate() {
        if(latestDate == null) {
            logger.error("latestDate is not initialized.");
            return null;
        }

        return latestDate;
    }

    public List<String> getRestaurantsPerStation(String station) {
        if(restaurantsPerStation == null) {
            logger.error("restaurantsPerStation-Map is not initialized.");
            return null;
        }

        return restaurantsPerStation.get(station);
    }

}
