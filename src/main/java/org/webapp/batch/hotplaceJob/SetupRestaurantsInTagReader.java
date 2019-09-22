package org.webapp.batch.hotplaceJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.webapp.batch.locationJob.SetupRestaurantsInLocationReader;
import org.webapp.model.Instafood;

import java.util.Map;

@StepScope
@Component
public class SetupRestaurantsInTagReader implements ItemReader<Map.Entry<Instafood, String>> {
    private static final Logger logger = LoggerFactory.getLogger(SetupRestaurantsInLocationReader.class);

    private HotPlaceStepsDataShareBean dataShareBean;
    private IteratorItemReader<Map.Entry<Instafood, String>> delegate;
    private Map<Instafood, String> foodPhotoPageLinks;

    SetupRestaurantsInTagReader() {}

    @Autowired
    public SetupRestaurantsInTagReader(HotPlaceStepsDataShareBean dataShareBean) {
        this.dataShareBean = dataShareBean;
    }

    @Override
    public Map.Entry<Instafood, String> read() {
        logger.info("[FindHotPlaceJob] : SetupRestaurantsInTag-ItemReader started.");

        this.foodPhotoPageLinks = this.dataShareBean.getPhotoPagelinks();

        if(this.delegate == null) {
            delegate = new IteratorItemReader<>(this.foodPhotoPageLinks.entrySet().iterator());
        }

        return this.delegate.read();
    }
}
