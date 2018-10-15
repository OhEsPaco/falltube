package org.vaporware.com;

import java.io.IOException;
import org.vaporware.com.domain.YoutubeDataDownloader;


public class App {


   
    private static final String APIKEY = "XXXXXXXXX";

    
    public static void main(String[] args) throws IOException {

        
      YoutubeDataDownloader d=new YoutubeDataDownloader(APIKEY);
      
 
    }

   
}