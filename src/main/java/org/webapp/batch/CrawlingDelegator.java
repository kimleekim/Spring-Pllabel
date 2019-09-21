package org.webapp.batch;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.webapp.batch.locationJob.LocationStepsDataShareBean;
import org.webapp.dataset.FileDelete;
import org.webapp.dataset.InstaCrawlImpl;
import org.webapp.model.Instafood;
import org.webapp.model.Instahot;
import org.webapp.model.Instaplace;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CrawlingDelegator<T> {
    private static final Logger logger = LoggerFactory.getLogger(CrawlingDelegator.class);
    private String photoUrl;
    private FileDelete fileDelete;
    private LocationStepsDataShareBean dataShareBean;

    @Autowired
    public void setFileDelete(FileDelete fileDelete) {
        this.fileDelete = fileDelete;
    }

    public void setDataShareBean(LocationStepsDataShareBean dataShareBean) {
        this.dataShareBean = dataShareBean;
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

    protected ArrayList<Instaplace> setLocationPostContent(InstaCrawlImpl crawl,
                                                           WebDriver driver,
                                                           Map<Instaplace, String> photoPageLinks) throws Exception {

        ArrayList<Instaplace> returnObjects = new ArrayList<Instaplace>();
        List<String> hashtags;
        String content = "";
        String description = "";
        String photoUrl = "";

        for(Instaplace object : photoPageLinks.keySet()) {
            driver.get(photoPageLinks.get(object));
            crawl.setIsFoodPost(1); //to get only 'food' photo-url

            photoUrl = crawl.getPhotoURL(driver, object.getStation(), photoPageLinks.get(object));
            content = crawl.getPost(driver);
            hashtags = crawl.getHashtags(driver);
            description = crawl.getDescription(driver, 1);

            object.setPost(content);
            object.setHashtag(crawl.findHashtags(content, hashtags));
            object.setDescription(description);

            if(! photoUrl.equals("")) {
                extractFoodContent(object, photoUrl);
            }

            returnObjects.add(object);

        }

        return returnObjects;
    }

    private void extractFoodContent(Instaplace instaplace, String photoUrl) {
        Instafood contentAboutFood = new Instafood();
        contentAboutFood.setStation(instaplace.getStation());
        contentAboutFood.setPost(instaplace.getPost());
        contentAboutFood.setDate(instaplace.getDate());
        contentAboutFood.setLikeCNT(instaplace.getLikeCNT());
        contentAboutFood.setPhotoURL(photoUrl);

        this.dataShareBean.addInstaFoodList(contentAboutFood);
    }
}
