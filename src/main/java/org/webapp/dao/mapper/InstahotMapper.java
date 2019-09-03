package org.webapp.dao.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.webapp.model.Instahot;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InstahotMapper implements RowMapper<Instahot> {
    @Override
    public Instahot mapRow(ResultSet rs, int rowNum) throws SQLException {
        Instahot instahot = new Instahot();

        instahot.setKey(rs.getInt("key"));
        instahot.setStation(rs.getString("station"));
        instahot.setPost(rs.getString("post"));
        instahot.setPhotoURL(rs.getString("photoURL"));
        instahot.setDate(rs.getDate("date"));
        return instahot;
    }
}
