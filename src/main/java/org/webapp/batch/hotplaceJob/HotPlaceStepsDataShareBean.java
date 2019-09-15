package org.webapp.batch.hotplaceJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.webapp.dataset.InstaCrawl;

import java.util.*;

@Component
public class HotPlaceStepsDataShareBean<T> {
    private static final Logger logger = LoggerFactory.getLogger(HotPlaceStepsDataShareBean.class);

    private List<String> stations = null;
    private Map<Integer, List<String>> photoPagelinks;
    private Map<String, String> hotplacePerStation = null;

    public HotPlaceStepsDataShareBean() {
        this.stations = new ArrayList<>();
        this.photoPagelinks = null;
        this.hotplacePerStation = new HashMap<>();
    }

    public void addStation(String station) {
        if(stations == null) {
            logger.error("stations-List is not initialized.");
            return;
        }
        else {
            this.stations.add(station);
            Collections.sort(this.stations);
        }
    }

    public void putFoodPhotoPagelinks(int isFoodPost, List<String> links) {
        if(photoPagelinks == null) {
            photoPagelinks = Collections.singletonMap(isFoodPost, links);

        } else {
            logger.info("photoPagelinks-Map is already full. Set new SingletonMap.");
            photoPagelinks = Collections.singletonMap(isFoodPost, links);
        }
    }


    public void putHotplacePerStation(String station, String hotplace) {
        if(hotplacePerStation == null) {
            logger.error("hotplacePerStation-Map is not initialized.");
            return;
        }

        hotplacePerStation.put(station, hotplace);
    }

    public boolean isStationsContain(String keyword) {
        return stations.contains(keyword);
    }

    public List<String> getStations() {
        if(stations == null) {
            logger.error("stations-List is not initialized.");
            return null;
        }

        return stations;
    }

    public Map<Integer, List<String>> getPhotoPagelinks() {
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

    public int getSizeOfStations() {
        if(stations == null) {
            logger.error("stations-List is not initialized.");
            return 0;
        }

        return stations.size();
    }
}
