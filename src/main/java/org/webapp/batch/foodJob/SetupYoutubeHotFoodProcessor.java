package org.webapp.batch.foodJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.webapp.dataset.YoutubeApiTrim;
import org.webapp.model.Youtubefood;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@StepScope
@Component
public class SetupYoutubeHotFoodProcessor implements ItemProcessor<Map.Entry<String, List<String>>, List<Youtubefood>> {
    private static final Logger logger = LoggerFactory.getLogger(SetupYoutubeHotFoodProcessor.class);
    private YoutubeApiTrim youtubeApiTrim;

    SetupYoutubeHotFoodProcessor() {}

    @Autowired
    public SetupYoutubeHotFoodProcessor(YoutubeApiTrim youtubeApiTrim) {
        this.youtubeApiTrim = youtubeApiTrim;
    }

    @Override
    public List<Youtubefood> process(Map.Entry<String, List<String>> hotFoodPerStation) {
        logger.info("[FindHotFoodJob] : SetupYoutubeHotFood-ItemProcessor started.");

        Youtubefood youtubefood;
        List<Youtubefood> objectList = new ArrayList<>();
        List<List<Object>> resultList = new ArrayList<>();
        String station = hotFoodPerStation.getKey();
        List<String> hotFood = hotFoodPerStation.getValue();

        for(String food : hotFood) {
            List<Object> result = this.youtubeApiTrim.SearchKeyword(food, station, 1);
            resultList.add(result);
        }

        for(List<Object> result : resultList) {
            try {
                youtubefood = (Youtubefood) result.get(0);
                objectList.add(youtubefood);
            } catch(IndexOutOfBoundsException e) {}
        }

        return objectList;
    }
}
