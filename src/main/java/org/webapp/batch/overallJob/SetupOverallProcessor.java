package org.webapp.batch.overallJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.webapp.dataset.MapSearchContextImpl;
import org.webapp.dataset.MapSerachContext;
import org.webapp.model.Overall;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
@StepScope
public class SetupOverallProcessor implements ItemProcessor<Overall, Overall> {

    private static Logger logger = LoggerFactory.getLogger(SetupOverallProcessor.class);
    private OverallStepsDataShareBean<Overall> dataShareBean;
    private MapSerachContext mapSearchContext;
    private String station;
    private List<String> existedRestaurants;
    private List<String> resultList;
    private List<String> newRestaurants;
    private List<String> removedRestaurants;

    @Autowired
    public SetupOverallProcessor(OverallStepsDataShareBean<Overall> dataShareBean) {
        this.dataShareBean = dataShareBean;
    }

    SetupOverallProcessor() {}

    @Autowired
    public void setMapSearchContextImpl(MapSearchContextImpl mapSearchContextImpl) {
        this.mapSearchContext = mapSearchContextImpl;
    }

    @Override
    public Overall process(Overall overall) throws Exception {
        logger.info("overall-STEP1-processor: 시작");
        station = overall.getStation();
        try {
            existedRestaurants = overall.getRestaurantsOfJson();
        } catch(NullPointerException e) {
            existedRestaurants = new ArrayList<>();
        }
        resultList = mapSearchContext.getRestaurantList(station + "역");
        newRestaurants = resultList;
        removedRestaurants = new ArrayList<String>();

        if(existedRestaurants.size() == 0 && resultList.size() > 0) {
            overall.setStation(station);
            overall.setRestaurants(resultList);

            return overall;
        }
        else if(resultList == null || resultList.size() == 0) {
            return null;
        }

        for(String old : existedRestaurants) {
            if(! resultList.contains(old.trim())) {
                removedRestaurants.add(old.trim());
                continue;
            }

            newRestaurants.remove(old);
        }
        if(removedRestaurants.size() != 0) {
            dataShareBean.putExistedRestaurants(station, existedRestaurants);
            dataShareBean.putRemovedRestaurants(station, removedRestaurants);
        }

        overall.setStation(station);
        overall.setRestaurants(newRestaurants);
        return overall;
    }
}
