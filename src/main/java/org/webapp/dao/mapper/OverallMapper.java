package org.webapp.dao.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.webapp.model.Overall;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OverallMapper implements RowMapper<Overall> {
    @Override
    public Overall mapRow(ResultSet rs, int rowNum) throws SQLException {
        Overall overall = new Overall();

        overall.setStation(rs.getString("station"));
        overall.setInstaCNT(rs.getInt("instaCNT"));
        overall.setYoutubeCNT(rs.getInt("youtubeCNT"));
        overall.setLikeCNT(rs.getLong("likeCNT"));
        overall.setSearchCNT(rs.getLong("searchCNT"));
        return overall;
    }
}
