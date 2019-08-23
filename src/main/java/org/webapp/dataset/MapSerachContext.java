package org.webapp.dataset;

import java.util.List;

public interface MapSerachContext {
    public void getStationFile(String url);
    public void getStationList () throws Exception;
    public List<String> getRestaurantList(String station) throws Exception;
}
