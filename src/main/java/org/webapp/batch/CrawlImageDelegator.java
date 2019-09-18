package org.webapp.batch;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.webapp.dataset.FileDelete;
import org.webapp.dataset.InstaCrawlImpl;
import org.webapp.model.Instahot;
import org.webapp.model.Youtubehot;

import java.util.ArrayList;
import java.util.Map;

public class CrawlImageDelegator<T> {
    private static final Logger logger = LoggerFactory.getLogger(CrawlImageDelegator.class);
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

            // Instaplace, Instafood의 경우 추가하기
        }

        fileDelete.deleteFiles("/Users/neossmac/IdeaProjects/pllabel", ".jpg");

        return returnList;
    }
}
