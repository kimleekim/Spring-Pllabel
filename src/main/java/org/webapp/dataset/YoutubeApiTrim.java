package org.webapp.dataset;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
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
import org.webapp.dao.InstafoodDao;
import org.webapp.dao.InstarankingDao;
import org.webapp.model.Instafood;
import org.webapp.model.Instaranking;
import org.webapp.model.Youtubefood;
import org.webapp.model.Youtubehot;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class YoutubeApiTrim {
    private static long NUMBER_OF_VIDEOS_RETURNED = 5;
    private static String PROPERTIES_FILENAME = "application.properties";
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();
    private static YouTube youtube;
    private static String foodKeyword;

    @Autowired
    InstafoodDao instafoodDao;
    @Autowired
    InstarankingDao instarankingDao;
    @Autowired
    S3Connector s3Connector;

    YoutubeApiTrim() {}


    private List<Object> insertVideoData (Iterator<SearchResult> iteratorSearchResults,
                                          String station,
                                          Properties properties,
                                          int isFoodVideo) throws InterruptedException, IOException, JSONException {

        List<Object> returnVideoList = new ArrayList<>();

        if (!iteratorSearchResults.hasNext()) {
            System.out.println(" There aren't any results for your query.");
        }

        while (iteratorSearchResults.hasNext()) {
            SearchResult singleVideo = iteratorSearchResults.next();
            ResourceId rId = singleVideo.getId();
            long publishedDateTemp = singleVideo.getSnippet().getPublishedAt().getValue();

            if (rId.getKind().equals("youtube#video")) {
                Youtubefood food;
                Youtubehot hot;
                String thumbnailURL = "http://img.youtube.com/vi/" + rId.getVideoId() + "/hqdefault.jpg";
                String videoLink = "https://www.youtube.com/watch?v=" + rId.getVideoId();
                thumbnailURL = s3Connector.upload(s3Connector.convertFileDatatype(2, thumbnailURL, station)
                        , "static");

                if (isFoodVideo == 1) {
                    food = new Youtubefood();
                    food.setStation(station);
                    food.setKeyword(foodKeyword);
                    food.setTitle(singleVideo.getSnippet().getTitle());
                    food.setCreator(singleVideo.getSnippet().getChannelTitle());
                    food.setDate(java.sql.Date.valueOf(convertDateFormat(publishedDateTemp)));
                    food.setThumbnailURL(thumbnailURL);
                    food.setVideoLink(videoLink);
                    food.setTotalview(getViewCountFromVideo(properties.getProperty("youtube.apikey"), rId.getVideoId()));
                    food.setContent(getContentFromVideo(properties.getProperty("youtube.apikey"), rId.getVideoId()));
                    returnVideoList.add(food);
                }
                else {
                    hot = new Youtubehot();
                    hot.setStation(station);
                    hot.setTitle(singleVideo.getSnippet().getTitle());
                    hot.setCreator(singleVideo.getSnippet().getChannelTitle());
                    hot.setDate(java.sql.Date.valueOf(convertDateFormat(publishedDateTemp)));
                    hot.setThumbnailURL(thumbnailURL);
                    hot.setVideoLink(videoLink);
                    hot.setTotalview(getViewCountFromVideo(properties.getProperty("youtube.apikey"), rId.getVideoId()));
                    hot.setContent(getContentFromVideo(properties.getProperty("youtube.apikey"), rId.getVideoId()));
                    returnVideoList.add(hot);
                }
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

    private String getContentFromVideo(String apikey, String videoId) throws IOException, JSONException {
        String content = "";

        String url = "https://www.googleapis.com/youtube/v3/videos?part=snippet&id="+videoId+"&key="+apikey;
        JSONObject json = readJsonFromUrl(url);
        String parentKey = json.getString("items");
        parentKey = parentKey.substring(1, parentKey.length() - 1);
        JSONObject parentKeyJson = new JSONObject(parentKey);
        String snippet = parentKeyJson.getString("snippet");
        JSONObject snippetJson = new JSONObject(snippet);
        Iterator iterator = snippetJson.keys();

        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            if (key.contains("description")) {
                content = snippetJson.getString(key);
                break;
            }
        }
        return content;
    }

    private long getViewCountFromVideo(String apikey, String videoId) throws IOException, JSONException {
        long viewCount = 0;

        String url = "https://www.googleapis.com/youtube/v3/videos?part=statistics&id="+videoId+"&key="+apikey;
        JSONObject json = readJsonFromUrl(url);
        String parentKey = json.getString("items");
        parentKey = parentKey.substring(1, parentKey.length() - 1);
        JSONObject parentKeyJson = new JSONObject(parentKey);
        String statistics = parentKeyJson.getString("statistics");
        JSONObject statisticsJson = new JSONObject(statistics);
        Iterator iterator = statisticsJson.keys();

        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            if (key.contains("viewCount")) {
                viewCount = statisticsJson.getLong(key);
                break;
            }
        }
        return viewCount;
    }

    private static String readAll(Reader reader) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        int cp;
        while ((cp = reader.read()) != -1) {
            stringBuilder.append((char) cp);
        }
        return stringBuilder.toString();
    }

    private static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream inputStream = new URL(url).openStream();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            String jsonText = readAll(bufferedReader);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            inputStream.close();
        }
    }

    public List<Object> SearchKeyword(String keyword, String station, int isFoodVideo) { //2면 hot
        List<Object> returnVideoList = null;
        Properties properties = new Properties();

        try {
            InputStream in = YoutubeApiTrim.class.getResourceAsStream("/" + PROPERTIES_FILENAME);
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


            YouTube.Search.List search = youtube.search().list("id,snippet");
            String apiKey = properties.getProperty("youtube.apikey");
            search.setKey(apiKey);
            search.setType("video");
            search.setFields("items(id/kind,id/videoId,snippet/title,snippet/channelTitle,snippet/publishedAt,snippet/thumbnails/default/url)");
            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);

            if (isFoodVideo == 1) {
                NUMBER_OF_VIDEOS_RETURNED = 1;
                foodKeyword = keyword;
            }
            search.setQ(keyword); //setting search keyword.
            SearchListResponse searchResponse = search.execute();
            List<SearchResult> searchResultList = searchResponse.getItems();
            returnVideoList = insertResult(searchResultList, station, properties, isFoodVideo);

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

    private List<Object> insertResult (List<SearchResult> searchResultList, String station, Properties properties, int isFoodVideo) throws IOException, InterruptedException, JSONException {
        List<Object> returnVideoList = null;

        returnVideoList = insertVideoData(searchResultList.iterator(), station, properties, isFoodVideo);
        return returnVideoList;
    }
}