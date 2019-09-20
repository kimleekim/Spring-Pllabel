package org.webapp.batch;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.webapp.dataset.FileDelete;
import org.webapp.dataset.InstaCrawlImpl;
import org.webapp.model.Instahot;
import org.webapp.model.Instaplace;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CrawlingDelegator<T> {
    private static final Logger logger = LoggerFactory.getLogger(CrawlingDelegator.class);
    private String photoUrl;
    private FileDelete fileDelete;

    @Autowired
    public void setFileDelete(FileDelete fileDelete) {
        this.fileDelete = fileDelete;
    }

    protected ArrayList<T> setS3ImageUrl(InstaCrawlImpl crawl,
                                      WebDriver driver,
                                      Map<T, String> photoPageLinks) throws Exception {

        ArrayList<T> returnList = new ArrayList<>();

        for(T object : photoPageLinks.keySet()) {
            if(object instanceof Instahot) {

                crawl.setIsFoodPost(2);
                photoUrl = crawl.getPhotoURL(driver, ((Instahot) object).getStation(), photoPageLinks.get(object));

                if(! photoUrl.equals("")) {
                    ((Instahot) object).setPost(crawl.getPost(driver));
                    System.out.println(((Instahot) object).getPost());
                    ((Instahot) object).setPhotoURL(photoUrl);
                    returnList.add(object);
                }

                if(returnList.size() == 30)
                    break;

            }

            // Instafood의 경우 추가하기
        }

        fileDelete.deleteFiles("/Users/neossmac/IdeaProjects/pllabel", ".jpg");

        return returnList;
    }

    protected ArrayList<T> setPostContent(InstaCrawlImpl crawl,
                                          WebDriver driver,
                                          Map<T, String> photoPageLinks) throws InterruptedException {

        ArrayList<T> returnObjects = new ArrayList<T>();
        List<String> hashtags;
        String content = "";
        String description = "";

        for(T object : photoPageLinks.keySet()) {
            driver.get(photoPageLinks.get(object));

            if(object instanceof Instaplace) {
                content = crawl.getPost(driver);
                hashtags = crawl.getHashtags(driver);
                description = crawl.getDescription(driver, 1);

                ((Instaplace) object).setPost(content);
                ((Instaplace) object).setHashtag(crawl.findHashtags(content, hashtags));
                ((Instaplace) object).setDescription(description);

                returnObjects.add(object);
            }

        }

        return returnObjects;
    }
}
