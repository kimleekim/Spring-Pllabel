package org.webapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.webapp.dao.*;
import org.webapp.model.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("secondpageservice")
public class SecondPageImpl implements SecondPage {
    @Autowired
    OverallDao overallDao;
    @Autowired
    InstahotDao instahotDao;
    @Autowired
    InstafoodDao instafoodDao;
    @Autowired
    InstaplaceDao instaplaceDao;
    @Autowired
    YoutubehotDao youtubehotDao;
    @Autowired
    YoutubefoodDao youtubefoodDao;
    private int checkYoutube = 1;
    private List<String> love = Arrays.asList("애인", "연인", "남친", "여친", "자친구", "자기", "쟈", "사랑", "럽", "연애", "lov",
            "luv", "데이트", "커플", "결혼");
    private Pattern lovePattern = Pattern.compile("^[0-9]+$");
    private List<String> friend = Arrays.asList("친구", "칭", "단짝", "지기", "동네", "사친", "우정", "동기", "친스타그램",
            "의리", "으리", "모임");
    private List<String> family = Arrays.asList("엄마", "아빠", "부모", "가족", "할", "식구", "패밀리", "애", "아이", "남편", "신랑",
            "부인", "아내", "육아", "부부", "family", "이네", "신혼", "남매", "형제", "자매");
    private Long count[] = {Long.valueOf(0), Long.valueOf(0), Long.valueOf(0)};

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
        Map<String, Object> param = new HashMap<>();
        param.put("station", station);
        List<Instaplace> totalData = instaplaceDao.findByParam(param);

        for (Instaplace data : totalData) {
            List<String> countedList = data.getHashtagOfJson();
            countHashtag(countedList, 0);
            countHashtag(countedList, 1);
            countHashtag(countedList, 2);
        }

        Long max = count[1];
        int index = 1;
        for (int i = 0; i < 3; i++) {
            if (max < count[i]) {
                index = i;
                max = count[i];
            }
        }

        if (love.equals(takeCategory(index))) {
            return "연인";
        }
        else if (family.equals(takeCategory(index))) {
            return "가족";
        }
        else {
            return "친구";
        }
    }

    private List<String> takeCategory (int index) {
        switch (index) {
            case 0:
                return love;
            case 1:
                return friend;
            default:
                return family;
        }
    }

    private void countHashtag (List<String> hashtags, int index) {
        List<String> filter = null;

        filter = takeCategory(index);
        for (String hashtag : hashtags) {
            if (index == 0) {
                Matcher matcher = lovePattern.matcher(hashtag);
                if (matcher.find()) {
                    if (hashtag.contains("주년") || hashtag.contains("일")) {
                        count[index]++;
                        continue;
                    }
                }
            }
            for (String countKeyword : filter) {
                if (hashtag.contains(countKeyword)) {
                    count[index]++;
                    break;
                }
            }
        }
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
