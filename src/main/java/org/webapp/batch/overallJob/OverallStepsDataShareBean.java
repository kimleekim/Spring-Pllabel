package org.webapp.batch.overallJob;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component("OverallDataShareBean")
public class OverallStepsDataShareBean<T> {
    private static final Logger logger = LoggerFactory.getLogger(OverallStepsDataShareBean.class);
    private Map<String, List<String>> existedRestaurants;
    private Map<String, List<String>> removedRestaurants;

    public OverallStepsDataShareBean() {
        this.existedRestaurants = Maps.newConcurrentMap();
        this.removedRestaurants = Maps.newConcurrentMap();
    }

    public void putExistedRestaurants(String key, List<String> list) {
        if(existedRestaurants == null) {
            logger.error("existedRestaurants-Map is not initialized.");
            return;
        }

        existedRestaurants.put(key, list);
    }

    public void putRemovedRestaurants(String key, List<String> list) {
        if(removedRestaurants == null) {
            logger.error("removedRestaurants-Map is not initialized.");
            return;
        }

        removedRestaurants.put(key, list);
    }

    public boolean isRemovedRestaurantsContainsKey(String key) {
        return removedRestaurants.containsKey(key);
    }

    public List<String> getExistedRestaurants(String key) {
        if(existedRestaurants == null) {
            logger.error("Cannot get list : existedRestaurants-Map is null.");
            return null;
        }

        return existedRestaurants.get(key);
    }

    public List<String> getRemovedRestaurants(String key) {
        if(removedRestaurants == null) {
            logger.error("Cannot get list : removedRestaurants-Map is null.");
            return null;
        }

        return removedRestaurants.get(key);
    }

    public int getSizeOfRemovedRestaurants() {
        if(removedRestaurants == null)
            return 0;

        return removedRestaurants.size();
    }

    public int getSizeOfExistedRestaurants() {
        if(existedRestaurants == null)
            return 0;

        return existedRestaurants.size();
    }
}
