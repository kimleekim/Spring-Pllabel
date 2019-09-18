package org.webapp.batch.hotplaceJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.webapp.model.Instahot;

import java.util.Map;

@Component
@StepScope
public class SetupInstaHotPlaceReader implements ItemReader<Map.Entry<String, String>> {
    private static final Logger logger = LoggerFactory.getLogger(SetupInstaHotPlaceReader.class);

    private HotPlaceStepsDataShareBean dataShareBean;
    private IteratorItemReader<Map.Entry<String, String>> delegate;
    private Map<String, String> hotplacePerStation;

    SetupInstaHotPlaceReader() { }

    @Autowired
    public SetupInstaHotPlaceReader(HotPlaceStepsDataShareBean dataShareBean) {
        this.dataShareBean = dataShareBean;
    }

    @Override
    public Map.Entry<String, String> read() {
        logger.info("[FindHotPlaceJob] : SetupInstaHotPlace-ItemReader started.");

        this.hotplacePerStation = dataShareBean.getHotplacePerStation();

        if(delegate == null) {
            delegate = new IteratorItemReader<>(this.hotplacePerStation.entrySet().iterator());
        }
        return delegate.read();
    }
}
