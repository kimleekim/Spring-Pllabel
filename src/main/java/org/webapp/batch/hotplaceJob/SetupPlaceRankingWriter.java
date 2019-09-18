package org.webapp.batch.hotplaceJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.webapp.model.Instaranking;

import javax.sql.DataSource;
import java.util.*;

@Component
@StepScope
public class SetupPlaceRankingWriter implements ItemWriter<List<Instaranking>> {

    private static final Logger logger = LoggerFactory.getLogger(SetupPlaceRankingWriter.class);
    private DataSource dataSource;
    private HotPlaceStepsDataShareBean dataShareBean;
    private String hotplace;
    private JdbcBatchItemWriter<Instaranking> delegate;
    private static final String sql
            = "INSERT INTO Instaranking(station, placetag, placetagCNT, likeCNT) " +
                "values (:station, :placetag, :placetagCNT, :likeCNT)";


    SetupPlaceRankingWriter() {}

    @Autowired
    public SetupPlaceRankingWriter(DataSource dataSource, HotPlaceStepsDataShareBean dataShareBean) {
        this.dataSource = dataSource;
        this.dataShareBean = dataShareBean;
    }

    @BeforeStep
    public void prepareForUpdate() {
        this.delegate = new JdbcBatchItemWriter<>();
        this.delegate.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Instaranking>());
        this.delegate.setDataSource(this.dataSource);
        this.delegate.setJdbcTemplate(new NamedParameterJdbcTemplate(this.dataSource));
        this.delegate.setSql(sql);
        this.delegate.afterPropertiesSet();
    }

    @Override
    public void write(List<? extends List<Instaranking>> objects) throws Exception {
        logger.info("[FindHotPlaceJob] : SetupPlaceRanking-ItemWriter started.");

        List<Instaranking> objectList = new ArrayList<>();
        for(List<Instaranking> object : objects) {
            objectList.addAll(object);
        }

        this.delegate.write(objectList);

        setHotplacePerStation(objectList);
    }

    private void setHotplacePerStation(List<Instaranking> objectList) {
        String hotplace = "";
        long max = 0;
        boolean marked = false;

        for(Instaranking object : objectList) {
            System.out.println(object.getPlacetag());
            System.out.println(object.getPlacetagCNT());
            System.out.println(object.getLikeCNT());

            if(max == 0
                    || object.getPlacetagCNT() > max) {

                if(! object.getPlacetag().equals(object.getStation())
                    || ! object.getPlacetag().equals(object.getStation() + "역")) {

                    marked = false;
                    max = object.getPlacetagCNT();
                    hotplace = object.getPlacetag();
                    System.out.println("핫플 : " + hotplace);
                }
            }
            else if(object.getPlacetagCNT() == max) {
                marked = true;
            }
        }

        if(marked) { // 동점인게 있음.
            hotplace = computeForHotplace(objectList, max);
        }

        System.out.println(hotplace);

        try {
            dataShareBean.putHotplacePerStation(objectList.get(0).getStation(), hotplace);

        } catch(IndexOutOfBoundsException e) { }
        System.out.println("역 별 핫플 목록 : " + dataShareBean.getHotplacePerStation());
    }

    private String computeForHotplace(List<Instaranking> objectList, long maxTags) {
        String hotplace = "";
        long maxLikes = -1;

        for(Instaranking instaranking : objectList) {
            if(instaranking.getPlacetag() == null)
                continue;

            if(instaranking.getPlacetagCNT() == maxTags
                && (! instaranking.getPlacetag().equals(instaranking.getStation())
                    || ! instaranking.getPlacetag().equals(instaranking.getStation() + "역"))) {

                maxLikes = (instaranking.getLikeCNT() > maxLikes) ?
                                            instaranking.getLikeCNT() : maxLikes;

                if(maxLikes == instaranking.getLikeCNT()) {
                    hotplace = instaranking.getPlacetag();
                }
            }
        }

        return hotplace;
    }

}
