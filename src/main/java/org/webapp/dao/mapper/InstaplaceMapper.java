package org.webapp.dao;

import org.springframework.jdbc.core.RowMapper;
import org.webapp.model.Instaplace;
import org.webapp.model.Instaranking;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InstaplaceMapper implements RowMapper<Instaplace> {
    @Override
    public Instaplace mapRow(ResultSet rs, int rowNum) throws SQLException {
        Instaplace instaplace = new Instaplace(rs.getLong("key"),
                                            rs.getString("station"),
                                            rs.getString("post"),
                                            rs.getLong("likeCNT"),
                                            rs.getDate("date"),
                                            rs.getString("hashtag"),
                                            rs.getString("description"));

        return instaplace;

    }
}