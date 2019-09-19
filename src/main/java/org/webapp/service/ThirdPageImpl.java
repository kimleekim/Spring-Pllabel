package org.webapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.webapp.dao.InstaplaceDao;
import org.webapp.model.Instaplace;

import java.util.*;

@Service("thirdpageservice")
public class ThirdPageImpl implements ThirdPage {
    @Autowired
    InstaplaceDao instaplaceDao;
    private List<Instaplace> totalData;
    private List<String> blackList = Arrays.asList(
            "ootd", "좋아요", "좋반", "스타", "일상", "daily", "소통", "follow", "감성", "인친",
            "데일리", "반사", "퇴근", "출근", "직장", "불금", "오오티디", "팔", "sel", "insta", "셀", "flf");
    private Long top5[] = {Long.valueOf(0), Long.valueOf(0), Long.valueOf(0), Long.valueOf(0), Long.valueOf(0)};
    private String top5hashtag[] = {"", "", "", "", ""};

    private void getData (String station) {
        Map<String, Object> param = new HashMap<>();
        param.put("station", station);
        totalData = instaplaceDao.findByParam(param);
    }

    @Override
    public String[] getRelatedHashtags(String station) {
        List<List<String>> totalHashtags = new ArrayList<>();
        Map<String, Long> hashtagRanking = new LinkedHashMap<>();

        getData(station);
        for (Instaplace data : totalData) {
            List<String> filter = data.getHashtagOfJson();
            totalHashtags.add(exceptBlackList(filter, station));
        }
        for (List<String> hashtags : totalHashtags) {
            for (String hashtag : hashtags) {
                if (hashtagRanking.containsKey(hashtag)) {
                    hashtagRanking.replace(hashtag, hashtagRanking.get(hashtag) + Long.valueOf(1));
                }
                else {
                    hashtagRanking.put(hashtag, Long.valueOf(1));
                }
            }
        }
        getTop5hashtag(hashtagRanking);

        return top5hashtag;
    }

    @Override
    public Long[] getRelatedHashtagCounts(String station) {
        return top5;
    }


    private List<String> exceptBlackList (List<String> hashtags, String station) {
        station = station.substring(0, station.length() - 1);
        Iterator<String> iterator = hashtags.iterator();
        List<String> modifiedHashtags = new ArrayList<>();
        boolean isRemoved = false;

        while (iterator.hasNext()) {
            String hashtag = iterator.next();
            for (String exceptKeyword : blackList) {
                if (hashtag.contains(exceptKeyword) || hashtag.contains(station)) {
                    iterator.remove();
                    isRemoved = true;
                    break;
                }
            }
            if (!isRemoved) {
                modifiedHashtags.add(hashtag);
            }
            isRemoved = false;
        }
        return modifiedHashtags;
    }

    private void getTop5hashtag (Map<String, Long> hashtags) {
        for (String hashtag : hashtags.keySet()) {
            for (int i = 0; i < 5; i++) {
                if (hashtags.get(hashtag) >= top5[i]) {
                    updateRanking(top5hashtag, top5, i);
                    top5[i] = hashtags.get(hashtag);
                    top5hashtag[i] = hashtag;
                    break;
                }
            }
        }
    }

    private void updateRanking(String[] top5hashtag, Long[] top5, int index) {
        if (index == 0) {
            top5[index + 4] = top5[index + 3];
            top5hashtag[index + 4] = top5hashtag[index + 3];
        }
        if (index < 2) {
            top5[index + 3] = top5[index + 2];
            top5hashtag[index + 3] = top5hashtag[index + 2];
        }
        if (index < 3) {
            top5[index + 2] = top5[index + 1];
            top5hashtag[index + 2] = top5hashtag[index + 1];
        }
        if (index < 4) {
            top5[index + 1] = top5[index];
            top5hashtag[index + 1] = top5hashtag[index];
        }
    }

    @Override
    public Long[] getByDayPostGraph(String station) {
        List<Date> postDates = new ArrayList<>();
        Long[] byDayPostCount = {Long.valueOf(0), Long.valueOf(0), Long.valueOf(0), Long.valueOf(0),
                Long.valueOf(0), Long.valueOf(0), Long.valueOf(0)}; //일주일 일~토 순서
        Calendar cal = Calendar.getInstance() ;

        getData(station);
        for (Instaplace data : totalData) {
            postDates.add(data.getDate());
        }

        for (Date postDate : postDates) {
            cal.setTime(postDate);
            byDayPostCount[cal.get(Calendar.DAY_OF_WEEK) - 1]++;
        }
        byDayPostCount = changeOrder(byDayPostCount);

        return byDayPostCount;
    }

    @Override
    public Long[] getThisWeekPostGraph(String station) {
        List<Date> postDates = new ArrayList<>();
        Long[] byDayPostCount = {Long.valueOf(0), Long.valueOf(0), Long.valueOf(0), Long.valueOf(0),
                Long.valueOf(0), Long.valueOf(0), Long.valueOf(0)};
        Calendar thisWeekStart = Calendar.getInstance();
        Calendar thisWeekEnd = Calendar.getInstance();
        Calendar cal = Calendar.getInstance();

        thisWeekStart.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        thisWeekEnd.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);

        getData(station);
        for (Instaplace data : totalData) {
            postDates.add(data.getDate());
        }

        for (Date postDate : postDates) {
            cal.setTime(postDate);

            if (cal.get(cal.YEAR) == thisWeekStart.get(thisWeekStart.YEAR) &&
                    cal.get(cal.MONTH) == thisWeekStart.get(thisWeekStart.MONTH) &&
                    cal.get(cal.DATE) == thisWeekStart.get(thisWeekStart.DATE)) {
                byDayPostCount[cal.get(Calendar.DAY_OF_WEEK) - 1]++;
            }
            if (cal.compareTo(thisWeekStart) >= 0 && cal.compareTo(thisWeekEnd) <= 0) {
                byDayPostCount[cal.get(Calendar.DAY_OF_WEEK) - 1]++;
            }
        }
        byDayPostCount = changeOrder(byDayPostCount);

        return byDayPostCount;
    }

    private Long[] changeOrder(Long[] byDayPostCount) {
        Long changeOrder = byDayPostCount[0];

        for (int i = 0; i < 6; i++) {
            byDayPostCount[i] = byDayPostCount[i + 1];
        }
        byDayPostCount[6] = changeOrder;

        return byDayPostCount;
    }
}
