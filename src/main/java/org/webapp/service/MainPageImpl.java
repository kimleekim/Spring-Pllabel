package org.webapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.webapp.dao.InstafoodDao;
import org.webapp.dao.InstarankingDao;
import org.webapp.dao.OverallDao;
import org.webapp.model.Instafood;
import org.webapp.model.Instaranking;
import org.webapp.model.Overall;

import java.util.*;

@Service("mainservice")
public class MainPageImpl implements MainPage {
    @Autowired
    OverallDao overallDao;
    @Autowired
    InstafoodDao instafoodDao;
    @Autowired
    InstarankingDao instarankingDao;

    @Override
    public boolean isExistStation(String station) {
        Map<String, Object> input = new HashMap<>();

        station = setStationframe(station);
        input.put("station", station);
        if (overallDao.findByParam(input).size() > 0) {
            Map<Object, String> addSearchCount = new LinkedHashMap<>();
            Map<String, Object> findBefData = new HashMap<>();

            findBefData.put("station", station);
            Overall befData = overallDao.findByParam(findBefData).get(0);
            addSearchCount.put(station, "station");
            addSearchCount.put(befData.getSearchCNT() + Long.valueOf(1), "searchCNT");
            overallDao.update(addSearchCount, 2);
            return true;
        }
        else {
            return false;
        }
    }

    public String setStationframe(String station) {
        if (station.substring(station.length() - 1).contains("역")) {
            StringBuffer stringBuffer = new StringBuffer(station);
            station = stringBuffer.deleteCharAt(station.length() - 1).toString();
        }
        return station;
    }

    @Override
    public List<String> getTOP3Station() {
        List<Overall> totalData = overallDao.findAll();
        List<String> stations = new ArrayList<>();
        Long[] top3 = {Long.valueOf(0), Long.valueOf(0), Long.valueOf(0)};
        String[] top3Station = {"", "", ""};

        for (Overall data : totalData) {
            for (int i = 0; i < 3; i++) {
                if (top3[i] <= data.getSearchCNT()) {
                    if (top3[i] == data.getSearchCNT() && top3[i] != 0) {
                        Map<String, Object> befData = new HashMap<>();

                        befData.put("station", top3Station[i]);
                        if (overallDao.findByParam(befData).get(0).getInstaCNT() < data.getInstaCNT()) {
                            updateRanking(top3Station, top3, i);
                            top3[i] = data.getSearchCNT();
                            top3Station[i] = data.getStation();
                            break;
                        }
                    }
                    else {
                        updateRanking(top3Station, top3, i);
                        top3[i] = data.getSearchCNT();
                        top3Station[i] = data.getStation();
                        break;
                    }
                }
            }
        }
        for (int i = 0; i < 3; i++) {
            stations.add(top3Station[i]);
        }
        return stations;
    }

    @Override
    public String[] getTOP3Restaurant() {
        List<Instafood> totalData = instafoodDao.findAll();
        List<List<String>> restaurants = new ArrayList<>();
        List<Long> restaurants_like = new ArrayList<>();
        Map<String, Long[]> ranking = new HashMap<>();
        int index = 0;
        Long[] top3 = {Long.valueOf(0), Long.valueOf(0), Long.valueOf(0)};
        String[] top3Restaurant = {"", "", ""};

        for (Instafood data : totalData) {
            restaurants.add(data.getMyRestaurantOfJson());
            restaurants_like.add(data.getLikeCNT());
        }

        for (Object restaurant : restaurants) {
            List<String> restaurantGroup = (List<String>) restaurant;
            for (String eachRestaurant : restaurantGroup) {
                if (!ranking.containsKey(eachRestaurant)) {
                    ranking.put(eachRestaurant, new Long[] {Long.valueOf(1), restaurants_like.get(index)});
                }
                else {
                    Long count = ranking.get(eachRestaurant)[0];
                    Long likeCNT = ranking.get(eachRestaurant)[1];
                    ranking.replace(eachRestaurant, new Long[] {count + 1, likeCNT + restaurants_like.get(index)});
                }
            }
            index++;
        }

        for (String restaurant : ranking.keySet()) {
            for (int i = 0; i < 3; i++) {
                if (top3[i] <= ranking.get(restaurant)[0]) {
                    if (top3[i] == ranking.get(restaurant)[0] && top3[i] != 0) {
                        if (ranking.get(top3Restaurant[i])[1] < ranking.get(restaurant)[1]) {
                            updateRanking(top3Restaurant, top3, i);
                            top3[i] = ranking.get(restaurant)[0];
                            top3Restaurant[i] = restaurant;
                            break;
                        }
                    }
                    else {
                        updateRanking(top3Restaurant, top3, i);
                        top3[i] = ranking.get(restaurant)[0];
                        top3Restaurant[i] = restaurant;
                        break;
                    }
                }
            }
        }
        return top3Restaurant;
    }

    @Override
    public String[] getTOP3Place() {
        List<Instaranking> totalData = instarankingDao.findAll();
        Map<String, Long[]> ranking = new HashMap<>();
        Long[] top3 = {Long.valueOf(0), Long.valueOf(0), Long.valueOf(0)};
        String[] top3Place = {"", "", ""};

        for (Instaranking data : totalData) {
            ranking.put(data.getPlacetag(), new Long[] {data.getPlacetagCNT(), data.getLikeCNT()});
        }

        for (String place : ranking.keySet()) {
            for (int i = 0; i < 3; i++) {
                if (top3[i] <= ranking.get(place)[0]) {
                    if (top3[i] == ranking.get(place)[0] && top3[i] != 0) {
                        if (ranking.get(top3Place[i])[1] < ranking.get(place)[1]) {
                            updateRanking(top3Place, top3, i);
                            top3[i] = ranking.get(place)[0];
                            top3Place[i] = place;
                            break;
                        }
                    }
                    else {
                        updateRanking(top3Place, top3, i);
                        top3[i] = ranking.get(place)[0];
                        top3Place[i] = place;
                        break;
                    }
                }
            }
        }
        return top3Place;
    }

    private void updateRanking(String[] top3, Long[] count, int index) {
        if (index == 0) {
            top3[index + 2] = top3[index + 1];
            count[index + 2] = count[index + 1];
        }
        if (index < 2) {
            top3[index + 1] = top3[index];
            count[index + 1] = count[index];
        }
    }
}
