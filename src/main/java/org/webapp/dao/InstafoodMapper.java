package org.webapp.dao;

import org.springframework.jdbc.core.RowMapper;
import org.webapp.model.Instafood;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InstafoodMapper implements RowMapper<Instafood> {
    @Override
    public Instafood mapRow(ResultSet rs, int rowNum) throws SQLException {
        Instafood instafood = new Instafood();

        instafood.setKey(rs.getInt("key"));
        instafood.setStation(rs.getString("station"));
        instafood.setPost(rs.getString("post"));
        instafood.setPhotoURL(rs.getString("photoURL"));
        instafood.setDate(rs.getDate("date"));
        return instafood;
    }
}
