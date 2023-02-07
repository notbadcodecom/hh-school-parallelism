package ru.hh.school.homework.search.google;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CompletableFutureSearcher implements OnlineSearcher {

  private final String GOOGLE_URL = "https://www.google.com/search?q=";
  private final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
      "AppleWebKit/537.36 (KHTML, like Gecko) " +
      "Chrome/80.0.3987.116 Safari/537.36";
  private ExecutorService threadPool = null;

  @Override
  public List<String> onlineSearch(Map<String, Integer> files) {
    threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    List<String> searchInfo = files.entrySet().parallelStream()
        .map(this::searchGoogleAsync)
        .map(CompletableFuture::join)
        .toList();
    threadPool.close();
    return searchInfo;
  }

  private long searchGoogle(String query) {
    Document document;
    try {
      document = Jsoup
          .connect(GOOGLE_URL + query)
          .userAgent(USER_AGENT)
          .get();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    Element divResultStats = document.select("div#result-stats").first();
    String text = divResultStats.text();
    String resultsPart = text.substring(0, text.indexOf('('));
    return Long.parseLong(resultsPart.replaceAll("[^0-9]", ""));
  }

  private CompletableFuture<String> searchGoogleAsync(Map.Entry<String, Integer> file) {
    String query = file.getKey().split(" ")[2];
    return CompletableFuture
        .supplyAsync(() -> getSearchResultInfo(file.getKey(), file.getValue(), searchGoogle(query)), threadPool);
  }

  private String getSearchResultInfo(String file, Integer wordCount, long searchCount) {
    return file + " [" + wordCount + "] - " + searchCount + " google search results";
  }

}
