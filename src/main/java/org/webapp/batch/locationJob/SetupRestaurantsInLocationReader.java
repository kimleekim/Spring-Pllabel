package org.webapp.batch.locationJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.webapp.model.Instafood;

import java.util.List;

@StepScope
@Component
public class SetupRestaurantsInLocationReader implements ItemReader<Instafood> {
    private static final Logger logger = LoggerFactory.getLogger(SetupRestaurantsInLocationReader.class);

    private LocationStepsDataShareBean dataShareBean;
    private ListItemReader<Instafood> delegate;

    SetupRestaurantsInLocationReader() {}

    @Autowired
    public SetupRestaurantsInLocationReader(LocationStepsDataShareBean dataShareBean) {
        this.dataShareBean = dataShareBean;
    }

    @Override
    public Instafood read() {
        logger.info("[SearchLocationJob] : SetupRestaurantsInLocation-ItemReader started.");
        List<Instafood> instafoodList;
        instafoodList = this.dataShareBean.getInstafoodList();

        if(this.delegate == null) {
            this.delegate = new ListItemReader<Instafood>(instafoodList);
        }

        return this.delegate.read();
    }
}
