package org.vaporware.com;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.vaporware.com.domain.YoutubeDataDownloader;
import org.vaporware.com.domain.exceptions.NoMorePagesException;
import org.vaporware.com.domain.exceptions.NoResultsException;
import org.vaporware.com.domain.search.SearchObject;

public class App {

    private static final String APIKEY = "xxxxxx";

    public static void main(String[] args) throws IOException, Exception {

        YoutubeDataDownloader downloader = new YoutubeDataDownloader(APIKEY);
        String query = "alexelcapo";
        long maxResults = 10;
        int paginas = 100;
        try {
            SearchObject aux = downloader.search(query, maxResults);
            System.out.println("Resultados de la busqueda: " + aux.getPageInfo().getTotalResults());
            //System.out.println("Pagina 1");
            String[] ids=downloader.searchObjectToIDs(aux);
             for(int a=0;a<ids.length;a++){
                     downloader.videoIdToSql(ids[a],50);
                }
            for (int i = 1; i < paginas; i++) {
                aux = downloader.search(query, maxResults, aux);
                //System.out.println("Pagina " + (i + 1));
               // print(downloader.searchObjectToIDs(aux));
                ids=downloader.searchObjectToIDs(aux);
                for(int a=0;a<ids.length;a++){
                     downloader.videoIdToSql(ids[a],50);
                }
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoMorePagesException ex) {
            System.out.println("No hay mas paginas en la busqueda");
        } catch (NoResultsException ex2) {
            System.out.println("No hay resultados de busqueda");
        }
       
        
       
    }

    public static void print(String[] array) {
        for (String array1 : array) {
            System.out.println("Video id: " + array1);
        }
    }

}
