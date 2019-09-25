package org.webapp.batch.foodJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.webapp.model.Instafood;
import org.webapp.service.SuperMappingTopRestaurants;

import javax.sql.DataSource;
import java.util.*;

@StepScope
@Component
public class MappingTop10RestaurantsProcessor extends SuperMappingTopRestaurants
                                  implements ItemProcessor<String, Map<String, List<String>>> {

    private static final Logger logger = LoggerFactory.getLogger(MappingTop10RestaurantsProcessor.class);
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;

    MappingTop10RestaurantsProcessor() {}

    @Autowired
    public MappingTop10RestaurantsProcessor(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(this.dataSource);
    }

    private Map<Integer, List<String>> selectInformationPerStation(String station) {
        Map<Integer, List<String>> mapForLikesAndRestaurants = new HashMap<>();
        List<Map<String, Object>> resultList;
        String sql = "select likeCNT, myRestaurant from instafood where station=\"" + station + "\"";
        Instafood temp = new Instafood();

        resultList = this.jdbcTemplate.queryForList(sql);

        for(Map<String, Object> result : resultList) {
            temp.setMyRestaurantForString(result.get("myRestaurant"));
            temp.setLikeCNT((Long) result.get("likeCNT"));
            mapForLikesAndRestaurants.put((int) temp.getLikeCNT(), temp.getMyRestaurantOfJson());
        }

        return mapForLikesAndRestaurants;
    }

    @Override
    public Map<String, List<String>> process(String station) {
        logger.info("[FindHotFoodJob] : MappingTop10Restaurants-ItemProcessor started.");
        Map<Integer, List<String>> mapForLikesAndRestaurants;
        List<String> top10;
        List<String> sorted;

        mapForLikesAndRestaurants = selectInformationPerStation(station);
        sorted = super.mapTop30Restaurants(mapForLikesAndRestaurants);

        if(sorted.size() > 10) {
            top10 = sorted.subList(0, 10);
        }
        else {
            top10 = sorted;
        }

        return Collections.singletonMap(station, top10);
    }
}
