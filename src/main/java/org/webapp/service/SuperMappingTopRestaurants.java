package org.webapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class SuperMappingTopRestaurants {
    private static final Logger logger = LoggerFactory.getLogger(SuperMappingTopRestaurants.class);

    public List<String> mapTop30Restaurants(Map<Integer, List<String>> mapForLikesAndRestaurants) {
        logger.info("[FindHotFoodJob] : MappingTop10Restaurants-ItemProcessor-GetTop30RestaurantsList started.");

        Map<String, Integer> likesOfRestaurants = new HashMap<>();
        Map<String, Integer> countingRestaurants = new HashMap<>();
        List<String> tempRestaurants;
        int index = 0;

        for(int likes : mapForLikesAndRestaurants.keySet()) {
            tempRestaurants = mapForLikesAndRestaurants.get(likes);

            for(String item : tempRestaurants) {

                likesOfRestaurants.computeIfPresent(item,
                        (String key, Integer value) -> value += likes);
                likesOfRestaurants.putIfAbsent(item, likes);

                countingRestaurants.computeIfPresent(item,
                        (String key, Integer value) -> ++value);
                countingRestaurants.putIfAbsent(item, 1);
            }
        }

        return getTopRestaurants(countingRestaurants, likesOfRestaurants);
    }

    private List<String> getTopRestaurants(Map<String, Integer> countingRestaurants,
                                             Map<String, Integer> likesOfRestaurants) {

        List<String> restaurants = new LinkedList<>(countingRestaurants.keySet());

        Collections.sort(restaurants, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                int result = countingRestaurants.get(o2).compareTo(countingRestaurants.get(o1));

                if(result == 0) {
                    result = likesOfRestaurants.get(o2).compareTo(likesOfRestaurants.get(o1));
                }

                return result;
            }
        });

        return restaurants;
    }
}
