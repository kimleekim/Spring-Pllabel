package org.webapp.batch.hotplaceJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.webapp.model.Instaranking;

import java.util.*;

@Component
@StepScope
public class SetupPlaceRankingWriter implements ItemWriter<List<Instaranking>> {

    private static final Logger logger = LoggerFactory.getLogger(SetupPlaceRankingWriter.class);
    private HotPlaceStepsDataShareBean<Instaranking> dataShareBean;
    private Map<String, Integer> countedPlacetags;
    private String hotplace;


    SetupPlaceRankingWriter() {}

    @Autowired
    public SetupPlaceRankingWriter(HotPlaceStepsDataShareBean<Instaranking> dataShareBean) {
        this.dataShareBean = dataShareBean;
    }

    @Override
    public void write(List<? extends List<Instaranking>> objects) {
        logger.info("[FindHotPlaceJob] : SetupPlaceRanking-ItemWriter started.");
        Map<String, Long> hasSameCount = new HashMap<>();
        Map.Entry<String, Integer> maxEntry = null;
        countedPlacetags = dataShareBean.getCountingTags();

        for(Map.Entry<String, Integer> entry : countedPlacetags.entrySet()) {
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());

            if(maxEntry == null
                || entry.getValue().compareTo(maxEntry.getValue()) > 0) {

                maxEntry = entry;
                hasSameCount.clear();
                System.out.println("최대 : " + maxEntry.getKey());
            }
            else if(entry.getValue().compareTo(maxEntry.getValue()) == 0) {
                if(hasSameCount.size() == 0) {
                    hasSameCount.put(maxEntry.getKey(), (long) 0);
                }

                hasSameCount.put(entry.getKey(), (long) 0);
            }
        }

        if(hasSameCount.size() == 0 && maxEntry != null) {
            hotplace = maxEntry.getKey();
        }
        else { //동점인 게 있음 -> likeCNT 다 더하기
            hotplace = computeForHotplace(objects.get(0), hasSameCount);
        }
        System.out.println(hotplace);

        try {
            dataShareBean.putHotplacePerStation(objects.get(0).get(0).getStation(), hotplace);

        } catch(IndexOutOfBoundsException e) { }

        dataShareBean.setCountingTagsEmpty();
    }

    private String computeForHotplace(List<Instaranking> objects, Map<String, Long> placetags) {
        String hotplace = "";
        Set<String> keyset = null;
        long maxLikes = -1;

        for(Instaranking instaranking : objects) {
            if(instaranking.getPlacetag() == null)
                continue;

            placetags.computeIfPresent(instaranking.getPlacetag(),
                    (String key, Long value) ->
                                            value += instaranking.getLikeCNT());
        }

        keyset = placetags.keySet();

        for(String placetag : keyset) {
            if(placetags.get(placetag) > maxLikes) {
                maxLikes = placetags.get(placetag);
                hotplace = placetag;
            }
        }

        return hotplace;
    }

}
