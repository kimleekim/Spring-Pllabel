package org.webapp.batch.foodJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


@StepScope
@Component
public class SetupYoutubeHotFoodReader implements ItemReader<Map.Entry<String, List<String>>> {
    private static final Logger logger = LoggerFactory.getLogger(SetupYoutubeHotFoodReader.class);
    private FoodStepsDataShareBean dataShareBean;
    private IteratorItemReader<Map.Entry<String, List<String>>> delegate;
    private Map<String, List<String>> hotFoodPerStation;

    SetupYoutubeHotFoodReader() {}

    @Autowired
    public SetupYoutubeHotFoodReader(FoodStepsDataShareBean dataShareBean) {
        this.dataShareBean = dataShareBean;
    }

    @Override
    public Map.Entry<String, List<String>> read() {
        logger.info("[FindHotFoodJob] : SetupYoutubeHotFood-ItemReader started.");

        this.hotFoodPerStation = this.dataShareBean.getTop10RestaurantsPerStation();

        if(this.delegate == null) {
            delegate = new IteratorItemReader<>(this.hotFoodPerStation.entrySet().iterator());
        }

        return this.delegate.read();
    }
}
