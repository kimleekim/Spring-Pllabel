package org.webapp.batch.hotplaceJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.webapp.dataset.YoutubeApiTrim;
import org.webapp.model.Youtubehot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@StepScope
@Component
public class SetupYoutubeHotPlaceProcessor implements ItemProcessor<Map.Entry<String, String>, List<Youtubehot>> {
    private static final Logger logger = LoggerFactory.getLogger(SetupYoutubeHotPlaceProcessor.class);
    private YoutubeApiTrim youtubeApi;

    SetupYoutubeHotPlaceProcessor() {}

    @Autowired
    public SetupYoutubeHotPlaceProcessor(YoutubeApiTrim youtubeApi) {
        this.youtubeApi = youtubeApi;
    }

    @Override
    public List<Youtubehot> process(Map.Entry<String, String> hotplacePerStation) {
        logger.info("[FindHotPlaceJob] : SetupYoutubeHotPlace-ItemProcessor started.");

        Youtubehot youtubehot;
        List<Youtubehot> objects = new ArrayList<>();
        String station = hotplacePerStation.getKey();
        String hotplace = hotplacePerStation.getValue();

        List<Object> objectList
                = youtubeApi.SearchKeyword(hotplace, station, 2);

        for(Object object : objectList) {
            youtubehot = (Youtubehot) object;
            objects.add(youtubehot);
        }

        return objects;
    }
}
