package org.webapp.batch.hotplaceJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.*;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.webapp.model.Instaranking;
import org.webapp.model.Overall;

import javax.sql.DataSource;
import java.util.List;
import java.util.Objects;


@StepScope
public class SetupPlaceRankingReader implements ItemReader<Instaranking>, ItemStream {

    private static final Logger logger = LoggerFactory.getLogger(SetupPlaceRankingReader.class);
    private int chunkSize;
    private DataSource dataSource;
    private HotPlaceStepsDataShareBean<Instaranking> dataShareBean;
    private JdbcCursorItemReader<Overall> delegate;
    private static final String query = "SELECT STATION FROM OVERALL";
    private String result;
    private Instaranking instaranking;

    SetupPlaceRankingReader() {}

    public SetupPlaceRankingReader(int chunkSize, DataSource dataSource) {
        this.chunkSize = chunkSize;
        this.dataSource = dataSource;
    }

    @Autowired
    public void setHotPlaceStepsDataShareBean(HotPlaceStepsDataShareBean dataShareBean) {
        this.dataShareBean = dataShareBean;
    }

    public void prepareForRead() {
        delegate = new JdbcCursorItemReader<Overall>();
        delegate.setFetchSize(this.chunkSize);
        delegate.setDataSource(this.dataSource);
        delegate.setRowMapper(new BeanPropertyRowMapper<>(Overall.class));
        delegate.setName("place-ItemReader");
        delegate.setSql(query);
    }

    @Override
    public Instaranking read() throws Exception{
        logger.info("[FindHotPlaceJob] : SetupPlaceRanking-ItemReader started.");

        instaranking = new Instaranking();
        result = Objects.requireNonNull(delegate.read()).getStation();

        System.out.println(result);
        dataShareBean.addStation(result);
        instaranking.setStation(result);

        return instaranking;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        try {
            this.delegate.open(executionContext);
        } catch(IllegalStateException e) {
            delegate.close();
            delegate.open(executionContext);
        }
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        this.delegate.update(executionContext);
    }

    @Override
    public void close() throws ItemStreamException {
        this.delegate.close();
    }
}