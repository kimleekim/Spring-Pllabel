package org.webapp.batch.foodJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@StepScope
@Component
public class MappingTop10RestaurantsWriter implements ItemWriter<Map<String, List<String>>> {
    private static final Logger logger = LoggerFactory.getLogger(MappingTop10RestaurantsWriter.class);
    private FoodStepsDataShareBean dataShareBean;

    MappingTop10RestaurantsWriter() {}

    @Autowired
    public MappingTop10RestaurantsWriter(FoodStepsDataShareBean dataShareBean) {
        this.dataShareBean = dataShareBean;
    }

    @Override
    public void write(List<? extends Map<String, List<String>>> top10RestaurantsPerStation) {
        logger.info("[FindHotFoodJob] : MappingTop10Restaurnats-ItemWriter started.");

        for(Map<String, List<String>> entry : top10RestaurantsPerStation) {
            this.dataShareBean.putTop10RestaurantsPerStation(entry);
        }
    }
}
