package org.webapp.service;

import java.io.UnsupportedEncodingException;

public interface MainPage {
    boolean isExistStation(String station) throws UnsupportedEncodingException;
    String[] getTOP3Station();
    String[] getTOP3Restaurant();
    String[] getTOP3Place();
}
