package org.webapp.dao.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.webapp.model.Youtubefood;

import java.sql.ResultSet;
import java.sql.SQLException;

public class YoutubefoodMapper implements RowMapper<Youtubefood> {
    @Override
    public Youtubefood mapRow(ResultSet rs, int rowNum) throws SQLException {
        Youtubefood youtubefood = new Youtubefood();

        youtubefood.setKey(rs.getInt("key"));
        youtubefood.setStation(rs.getString("station"));
        youtubefood.setKeyword(rs.getString("keyword"));
        youtubefood.setTitle(rs.getString("title"));
        youtubefood.setContent(rs.getString("content"));
        youtubefood.setTotalview(rs.getLong("totalview"));
        youtubefood.setCreator(rs.getString("creator"));
        youtubefood.setDate(rs.getDate("date"));
        youtubefood.setThumbnailURL(rs.getString("thumbnailURL"));
        youtubefood.setVideoLink(rs.getString("videoLink"));
        return youtubefood;
    }
}
