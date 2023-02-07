package ru.hh.school.homework.search.google;

import java.util.List;
import java.util.Map;

public interface OnlineSearcher {

  List<String> onlineSearch(Map<String, Integer> files);

}
