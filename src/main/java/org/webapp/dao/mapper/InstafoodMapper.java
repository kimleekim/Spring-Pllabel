package org.webapp.dao.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.webapp.model.Instafood;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InstafoodMapper implements RowMapper<Instafood> {
    @Override
    public Instafood mapRow(ResultSet rs, int rowNum) throws SQLException {
        Instafood instafood = new Instafood(rs.getLong("key"),
                                            rs.getString("station"),
                                            rs.getString("post"),
                                            rs.getDate("date"),
                                            rs.getLong("likeCNT"),
                                            rs.getString("myRestaurant"),
                                            rs.getString("photoURL"));

        return instafood;
    }
}
