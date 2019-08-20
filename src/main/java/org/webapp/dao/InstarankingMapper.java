package org.webapp.dao;

import org.springframework.jdbc.core.RowMapper;
import org.webapp.model.Instaranking;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InstarankingMapper implements RowMapper<Instaranking> {
    @Override
    public Instaranking mapRow(ResultSet rs, int rowNum) throws SQLException {
        Instaranking instaranking = new Instaranking();

        instaranking.setKey(rs.getInt("key"));
        instaranking.setStation(rs.getString("station"));
        instaranking.setPlacetag(rs.getString("placetag"));
        instaranking.setPlacetagCNT(rs.getInt("placetagCNT"));
        instaranking.setLikeCNT(rs.getInt("likeCNT"));
        return instaranking;
    }
}
