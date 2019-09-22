package org.webapp.batch;

import java.util.List;
import java.util.StringTokenizer;

public class FindMyRestaurantsInList {

    protected List<String> computingMyRestaurants(List<String> restaurants, List<String> myRestaurants, String post) {
        StringTokenizer stringTokenizer;

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
        return myRestaurants;
    }
}
