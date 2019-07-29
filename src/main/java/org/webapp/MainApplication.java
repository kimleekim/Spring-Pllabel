package org.webapp;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

import java.io.File;


public class MainApplication {
    public static void main(String[] args) throws LifecycleException {
        Tomcat tomcat = new Tomcat();
        Connector connector = tomcat.getConnector();
        connector.setURIEncoding("UTF-8");

        tomcat.addWebapp("/", new File("src/main/web").getAbsolutePath());

        tomcat.setPort(8080);
        tomcat.start();
        tomcat.getServer().await();

    }
}
