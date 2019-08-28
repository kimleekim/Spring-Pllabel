package org.webapp.dataset;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.webapp.dao.InstarankingDao;
import org.webapp.dao.OverallDao;
import org.webapp.model.Instaranking;
import org.webapp.model.Overall;
import org.webapp.model.Youtubefood;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class YoutubeContext {
    private static final long NUMBER_OF_VIDEOS_RETURNED = 5;
    private static String PROPERTIES_FILENAME = "application.properties";
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();
    private static YouTube youtube;
    private static String inputQuery;
    @Autowired
    OverallDao overallDao;
    @Autowired
    InstarankingDao instarankingDao;
    @Autowired
    S3Connector s3Connector;

    YoutubeContext() {
    }

    private String getInputQuery(String keyword, int isFoodVideo) throws IOException {
        Map<String, Object> parameter = new HashMap<>();

        if (isFoodVideo == 1) {
            parameter.put("station", keyword);
            Overall overall = overallDao.findByParam(parameter).get(0);
            inputQuery = overall.getStation();
            return inputQuery + "맛집";
        }
        else if (isFoodVideo == 2) {
            parameter.put("placeTag", keyword);
            Instaranking instaranking = instarankingDao.findByParam(parameter).get(0);
            inputQuery = instaranking.getPlacetag();
            return inputQuery;
        }
        else {
            return null;
        }
    }

    private List<Object> insertVideoData(Iterator<SearchResult> iteratorSearchResults, String station) throws InterruptedException, IOException {
        List<Object> returnVideoList = new ArrayList<>();

        if (!iteratorSearchResults.hasNext()) {
            System.out.println(" There aren't any results for your query.");
        }

        while (iteratorSearchResults.hasNext()) {
            SearchResult singleVideo = iteratorSearchResults.next();
            ResourceId rId = singleVideo.getId();
            long publishedDateTemp = singleVideo.getSnippet().getPublishedAt().getValue();

            if (rId.getKind().equals("youtube#video")) {
                Youtubefood youtubefood = new Youtubefood();
                String thumbnailURL = "http://img.youtube.com/vi/" + rId.getVideoId() + "/hqdefault.jpg";
                String videoLink = "https://www.youtube.com/watch?v=" + rId.getVideoId();
                thumbnailURL = s3Connector.upload(s3Connector.convertFileDatatype(2, thumbnailURL, station)
                        , "static");

                youtubefood.setStation(station);
                youtubefood.setTitle(singleVideo.getSnippet().getTitle());
                youtubefood.setCreator(singleVideo.getSnippet().getChannelTitle());
                youtubefood.setDate(java.sql.Date.valueOf(convertDateFormat(publishedDateTemp)));
                youtubefood.setThumbnailURL(thumbnailURL);
                youtubefood.setVideoLink(videoLink);
                returnVideoList.add(youtubefood);
            }
        }
        return returnVideoList;
    }

    private static String convertDateFormat(long inputDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(inputDate);
        String publishedDate = simpleDateFormat.format(date);
        return publishedDate;
    }

    public List<Object> SearchKeyword(String keyword, String station, int isFoodVideo) {
        List<Object> returnVideoList = null;
        Properties properties = new Properties();

        try {
            InputStream in = YoutubeContext.class.getResourceAsStream("/" + PROPERTIES_FILENAME);
            properties.load(in);
        } catch (IOException e) {
            System.err.println("There was an error reading " + PROPERTIES_FILENAME + ": " + e.getCause()
                    + " : " + e.getMessage());
            System.exit(1);
        }
        try {
            youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
                public void initialize(HttpRequest request) throws IOException {}
            }).setApplicationName("youtube-cmdline-search-sample").build();

            String queryTerm = getInputQuery(keyword, isFoodVideo);

            YouTube.Search.List search = youtube.search().list("id,snippet");
            String apiKey = properties.getProperty("youtube.apikey");
            search.setKey(apiKey);
            search.setQ(queryTerm);
            search.setType("video");
            search.setFields("items(id/kind,id/videoId,snippet/title,snippet/channelTitle,snippet/publishedAt,snippet/thumbnails/default/url)");
            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
            SearchListResponse searchResponse = search.execute();
            List<SearchResult> searchResultList = searchResponse.getItems();

            if (searchResultList != null) {
                returnVideoList = insertVideoData(searchResultList.iterator(), station);
            }
        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            return returnVideoList;
        }
    }
}