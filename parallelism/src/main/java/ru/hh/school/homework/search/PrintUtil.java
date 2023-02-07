package ru.hh.school.homework.search;

import java.util.List;
import java.util.Map;

public class PrintUtil {

  public static void printWordsOfFiles(List<String> files, long start, long end) {
    System.out.println("Found " + files.size() + " file(s) in " + (end - start) + " ms:");
    for (var file : files) {
      System.out.println("  " + file);
    }
  }

  public static void printWordsOfFiles(Map<String, Integer> folders, long start, long end) {
    System.out.println("Found " + folders.size() + " word(s) of each file in " + (end - start) + " ms:");
      for (var word : folders.entrySet()) {
        System.out.println("  " + word.getKey() + " [" + word.getValue() + "]");
      }
  }

  public static void printWordsOfFolders(Map<String, Integer> folders, long start, long end) {
    System.out.println("Found " + folders.size() + " word(s) of folder's file in " + (end - start) + " ms:");
    for (var word : folders.entrySet()) {
      System.out.println("  " + word.getKey() + " [" + word.getValue() + "]");
    }
  }

  public static void printStringList(List<String> list, long start, long end) {
    System.out.println("Search results found in Google in " + (end - start) + " ms:");
    for (var line : list) {
      System.out.println("  " + line);
    }
  }

}
