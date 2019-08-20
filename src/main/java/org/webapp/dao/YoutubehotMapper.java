package org.webapp.dao;

import org.springframework.jdbc.core.RowMapper;
import org.webapp.model.Youtubehot;

import java.sql.ResultSet;
import java.sql.SQLException;

public class YoutubehotMapper implements RowMapper<Youtubehot> {
    @Override
    public Youtubehot mapRow(ResultSet rs, int rowNum) throws SQLException {
        Youtubehot youtubehot = new Youtubehot();

        youtubehot.setKey(rs.getInt("key"));
        youtubehot.setStation(rs.getString("station"));
        youtubehot.setTitle(rs.getString("title"));
        youtubehot.setCreator(rs.getString("creator"));
        youtubehot.setDate(rs.getDate("date"));
        youtubehot.setThumbnailURL(rs.getString("thumbnailURL"));
        youtubehot.setVideoLink(rs.getString("videoLink"));
        return youtubehot;
    }
}
