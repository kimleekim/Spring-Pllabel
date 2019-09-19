package org.webapp.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public interface SecondPage {
    public String getLikeCNT(String station);

    public void updateLikeCNT(String station);

    public String withWho(String station);

    public Object[] showHotPost(String station, boolean isMorepage);

    public Object[] showFoodPost(String station, boolean isMorepage);

    public Object getPlaceLocation();
}
