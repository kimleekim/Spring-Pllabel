package org.webapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.webapp.dao.*;
import org.webapp.model.*;

import java.util.*;

@Service("secondpageservice")
public class SecondPageImpl implements SecondPage {
    @Autowired
    OverallDao overallDao;
    @Autowired
    InstahotDao instahotDao;
    @Autowired
    InstafoodDao instafoodDao;
    @Autowired
    YoutubehotDao youtubehotDao;
    @Autowired
    YoutubefoodDao youtubefoodDao;
    private int checkYoutube = 1;

    @Override
    public String getLikeCNT(String station) {
        Map<String, Object> param = new HashMap<>();

        param.put("station", station);
        Overall overall = overallDao.findByParam(param).get(0);

        return Long.toString(overall.getLikeCNT());
    }

    @Override
    public void updateLikeCNT(String station) {
        Map<String, Object> param = new HashMap<>();
        Map<Object, String> updateParam = new HashMap<>();

        param.put("station", station);
        Overall overall = overallDao.findByParam(param).get(0);
        updateParam.put(station, "station");
        updateParam.put(overall.getLikeCNT() + 1, "likeCNT");
        overallDao.update(updateParam, 2);
    }

    @Override
    public String withWho(String station) {
        return "미완";
    }

    @Override
    public Object[] showHotPost(String station, boolean isMorepage) {
        int POST_COUNT_TO_SHOW = 4;
        
//        if (isMorepage) {
//            POST_COUNT_TO_SHOW = 40;
//        }
        
        Object[] returnPosts = new Object[POST_COUNT_TO_SHOW];
        Map<String, Object> param = new HashMap<>();

        param.put("station", station);
        List<Instahot> instahots = instahotDao.findByParam(param);
        List<Youtubehot> youtubehots = youtubehotDao.findByParam(param);

        Collections.sort(instahots, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                Instahot i1 = (Instahot) o1;
                Instahot i2 = (Instahot) o2;
                return i1.getDate().compareTo(i2.getDate());
            }
        });
        Collections.reverse(instahots);

        for (int i = 0; i < POST_COUNT_TO_SHOW; i++) {
            if (checkYoutube == 4) {
                returnPosts[i] = new Object[] {
                        youtubehots.get(i / 4).getThumbnailURL(),
                        youtubehots.get(i / 4).getTitle(),
                        youtubehots.get(i / 4).getContent(),
                        youtubehots.get(i / 4).getDate(),
                        youtubehots.get(i / 4).getCreator(),
                        youtubehots.get(i / 4).getTotalview(),
                        youtubehots.get(i / 4).getVideoLink()
                };
                checkYoutube = 1;
            }
            else {
                returnPosts[i] = new Object[] {
                        instahots.get(i).getPhotoURL(),
                        instahots.get(i).getDate(),
                        instahots.get(i).getPost()
                };
                checkYoutube++;
            }
        }

        return returnPosts;
    }

    @Override
    public Object[] showFoodPost(String station, boolean isMorepage) {
        int POST_COUNT_TO_SHOW = 4;

//        if (isMorepage) {
//            POST_COUNT_TO_SHOW = 40;
//        }

        Object[] returnPosts = new Object[POST_COUNT_TO_SHOW];
        Map<String, Object> param = new HashMap<>();

        param.put("station", station);
        List<Instafood> instafoods = instafoodDao.findByParam(param);
        List<Youtubefood> youtubefoods = youtubefoodDao.findByParam(param);

        Collections.sort(instafoods, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                Instafood i1 = (Instafood) o1;
                Instafood i2 = (Instafood) o2;
                return i1.getDate().compareTo(i2.getDate());
            }
        });
        Collections.reverse(instafoods);

        for (int i = 0; i < POST_COUNT_TO_SHOW; i++) {
            if (checkYoutube == 4) {
                returnPosts[i] = new Object[] {
                        youtubefoods.get(i / 4).getThumbnailURL(),
                        youtubefoods.get(i / 4).getTitle(),
                        youtubefoods.get(i / 4).getContent(),
                        youtubefoods.get(i / 4).getDate(),
                        youtubefoods.get(i / 4).getCreator(),
                        youtubefoods.get(i / 4).getTotalview(),
                        youtubefoods.get(i / 4).getVideoLink()
                };
                checkYoutube = 1;
            }
            else {
                returnPosts[i] = new Object[] {
                        instafoods.get(i).getPhotoURL(),
                        instafoods.get(i).getDate(),
                        instafoods.get(i).getPost()
                };
                checkYoutube++;
            }
        }

        return returnPosts;
    }

    @Override
    public Object getPlaceLocation() {
        return null;
    }
}
