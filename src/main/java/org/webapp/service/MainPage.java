package org.webapp.service;

import java.io.UnsupportedEncodingException;
import java.util.List;

public interface MainPage {
    boolean isExistStation(String station) throws UnsupportedEncodingException;
    List<String> getTOP3Station();
    String[] getTOP3Restaurant();
    String[] getTOP3Place();
}
