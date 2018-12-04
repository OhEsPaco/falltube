package org.vaporware.com.domain.agents;

/**
 *
 * @author pacog
 */
public interface Performatives {

    int ID_FOR_DOWNLOADER = 60;
    int DOWNLOADER_DOWN = 666;
    int MAX_RETRIES = 3;
    long MS_WAIT_ON_RETRY = 60000;
}
