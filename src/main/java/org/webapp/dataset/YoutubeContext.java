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

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

@Component
public class YoutubeContext {
    /** Global instance properties filename. */
    private static String PROPERTIES_FILENAME = "application.properties";
    /** Global instance of the HTTP transport. */
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();
    /** Global instance of the max number of videos we want returned (50 = upper limit per page). */
    private static final long NUMBER_OF_VIDEOS_RETURNED = 5;
    /** Global instance of Youtube object to make all API requests. */
    private static YouTube youtube;

    YoutubeContext() {
    }

    private static String getInputQuery() throws IOException {
//        String inputQuery = "";
//        System.out.print("Please enter a search term: ");
//        BufferedReader bReader = new BufferedReader(new InputStreamReader(System.in));
//        inputQuery = bReader.readLine();
        //overall에서 지하철역 가져와서 inputQuery 변수에 넣어주면 검색키워드로 작용함
        String inputQuery = "합정맛집";
        return inputQuery;
    }

    private static void insertVideoData(Iterator<SearchResult> iteratorSearchResults, String query) throws ParseException {
        System.out.println("\n=============================================================");
        System.out.println(
                "   First " + NUMBER_OF_VIDEOS_RETURNED + " videos for search on \"" + query + "\".");
        System.out.println("=============================================================\n");
        if (!iteratorSearchResults.hasNext()) {
            System.out.println(" There aren't any results for your query.");
        }
        while (iteratorSearchResults.hasNext()) {
            SearchResult singleVideo = iteratorSearchResults.next();
            ResourceId rId = singleVideo.getId();
            long publishedDateTemp = singleVideo.getSnippet().getPublishedAt().getValue();
            if (rId.getKind().equals("youtube#video")) {
                //모두 string타입이어서 바로 youtubehot, youtubefood 테이블에 넣을 수 있음
                System.out.println(" Title: " + singleVideo.getSnippet().getTitle());
                System.out.println(" ChannelTitle: " + singleVideo.getSnippet().getChannelTitle());
                System.out.println(" Published Date: " + convertDateFormat(publishedDateTemp));
                System.out.println(" Thumbnail: " + "http://img.youtube.com/vi/" + rId.getVideoId() + "/hqdefault.jpg");
                System.out.println("Video link: " + "https://www.youtube.com/watch?v=" + rId.getVideoId());
                System.out.println("\n-------------------------------------------------------------\n");
            }
        }
    }

    private static String convertDateFormat(long inputDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
        Date date = new Date(inputDate);
        String publishedDate = simpleDateFormat.format(date);
        return publishedDate;
    }

    public void SearchKeyword() {
        // Read the developer key from youtube.properties
        Properties properties = new Properties();

        //properties 셋팅
        try {
            InputStream in = YoutubeContext.class.getResourceAsStream("/" + PROPERTIES_FILENAME);
            properties.load(in);
        } catch (IOException e) {
            System.err.println("There was an error reading " + PROPERTIES_FILENAME + ": " + e.getCause()
                    + " : " + e.getMessage());
            System.exit(1);
        }
        try {
            /*
             * The YouTube object is used to make all API requests. The last argument is required, but
             * because we don't need anything initialized when the HttpRequest is initialized, we override
             * the interface and provide a no-op function.
             */
            //http와 연결할 youtube객체 생성
            youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
                public void initialize(HttpRequest request) throws IOException {}
            }).setApplicationName("youtube-cmdline-search-sample").build();

            //검색키워드 셋팅
            String queryTerm = getInputQuery();

            //http와 연결하여 검색키워드를 삽입하기 전 apikey 연결
            YouTube.Search.List search = youtube.search().list("id,snippet");
            String apiKey = properties.getProperty("youtube.apikey");
            search.setKey(apiKey);
            search.setQ(queryTerm);
            /*
             * We are only searching for videos (not playlists or channels). If we were searching for
             * more, we would add them as a string like this: "video,playlist,channel".
             */
            search.setType("video");
            /*
             * This method reduces the info returned to only the fields we need and makes calls more
             * efficient.
             */
            search.setFields("items(id/kind,id/videoId,snippet/title,snippet/channelTitle,snippet/publishedAt,snippet/thumbnails/default/url)");
            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
            SearchListResponse searchResponse = search.execute();
            List<SearchResult> searchResultList = searchResponse.getItems();
            if (searchResultList != null) {
                insertVideoData(searchResultList.iterator(), queryTerm);
            }
        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}