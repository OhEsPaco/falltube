/*
MIT License

Copyright (c) 2018 Francisco Manuel Garcia Sanchez

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package org.vaporware.com.domain.agents;

public interface CCS {

    int ID_FOR_DOWNLOADER = 60;
    int DOWNLOADER_DOWN = 666985;
    int DOWNLOADED_OK=788175;
    
    int UI_PRINT = 434;

    int WANT_QUERY = 23678;
    int TAKE_YOUR_QUERY = 4531432;
    int COMPLETED_QUERY = 777;
    int FAILED_QUERY = 998;

    int WANT_API = 129834;
    int TAKE_YOUR_API = 73825;

    int KILL_YOURSELF = 666;

    String DOWNLOADER_DF = "downloader";
    String SEARCHER_DF = "searcher";
    String UI_DF = "userInterface";
    String MANAGEMENT_DF = "management";

    String SEARCH_AGENT_CLASS = "org.vaporware.com.domain.agents.SearchAgent";
    String DOWNLOAD_AGENT_CLASS = "org.vaporware.com.domain.agents.DownloadAgent";
    String UI_AGENT_CLASS = "org.vaporware.com.domain.agents.UIAgent";

    int MAX_RETRIES = 3;

    long MS_WAIT_ON_RETRY = 60000;

    String COLOR_BLACK = "black";
    String COLOR_RED = "red";
    String COLOR_GREEN = "green";
    String COLOR_BLUE = "blue";
    String COLOR_MAGENTA = "magenta";
    String APPCONFIGPATH = "falltube.properties";
    String PROPERTIES_STRING = "#Region code\n"
            + "regionCode=ES\n"
            + "#Files containing API keys (separated by commas)\n"
            + "apiKeys=apikeys.txt\n"
            + "#Files containing queries (separated by commas)\n"
            + "queries=words.txt\n"
            + "#Port used by jade\n"
            + "jadePort=6743\n"
            + "#Download agents\n"
            + "downloadAgents=2\n"
            + "#Search agents\n"
            + "searchAgents=3\n"
            + "#UI agents\n"
            + "uiAgents=1\n"
            + "#Database host\n"
            + "host=localhost\n"
            + "#Database port\n"
            + "port=\n"
            + "#Database name\n"
            + "database=\n"
            + "#Database user\n"
            + "user=\n"
            + "#Database user password\n"
            + "password=";
}
