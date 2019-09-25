package org.webapp.batch.foodJob;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.webapp.model.Instafood;

import javax.sql.DataSource;
import java.util.List;

@StepScope
@Component
public class MappingTop10RestaurantsReader implements ItemReader<String> {
    private static final Logger logger = LoggerFactory.getLogger(MappingTop10RestaurantsReader.class);
    private ListItemReader<String> delegate;
    private FoodStepsDataShareBean dataShareBean;

    MappingTop10RestaurantsReader() {}

    @Autowired
    public MappingTop10RestaurantsReader(FoodStepsDataShareBean dataShareBean) {
        this.dataShareBean = dataShareBean;
    }

    @Override
    public String read() {
        logger.info("[FindHotFoodJob] : MappingTop10Restaurnats-ItemReader started.");

        List<String> stations = this.dataShareBean.getStations();
        if(this.delegate == null) {
            this.delegate = new ListItemReader<String>(stations);
        }

        return this.delegate.read();
    }
}
