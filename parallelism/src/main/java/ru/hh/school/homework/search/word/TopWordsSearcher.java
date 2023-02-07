package ru.hh.school.homework.search.word;

import java.util.List;
import java.util.Map;

public interface TopWordsSearcher {

  Map<String, Integer> inFilesTopWordsCounter(List<String> files, int limit);

  Map<String, Integer> inFoldersTopWordsCounter(List<String> files, int limit);

}
