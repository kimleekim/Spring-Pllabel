package org.webapp.service;

public interface ThirdPage {
    public String[] getRelatedHashtags(String station);

    public Long[] getRelatedHashtagCounts(String station);

    public Long[] getByDayPostGraph(String station);

    public Long[] getThisWeekPostGraph(String station);

    public Object[] getBestDayPost(String station, Long[] ranking);
}
