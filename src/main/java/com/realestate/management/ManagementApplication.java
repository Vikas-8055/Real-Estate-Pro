package com.realestate.management;

import java.awt.Desktop;
import java.net.URI;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class ManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(ManagementApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void openBrowser() {
        String url = "http://localhost:8080";
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            }
        } catch (Exception e) {
            System.out.println("Could not open browser automatically. Please open: " + url);
        }
    }
}