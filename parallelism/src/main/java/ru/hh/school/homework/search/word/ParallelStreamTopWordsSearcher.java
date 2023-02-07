package ru.hh.school.homework.search.word;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ParallelStreamTopWordsSearcher implements TopWordsSearcher {

  @Override
  public Map<String, Integer> inFilesTopWordsCounter(List<String> files, int limit) {
    return files.parallelStream()
        .flatMap(file -> fileReadLines(file).parallelStream()
            .map(line -> line.split("[^a-zA-Z]"))
            .flatMap(lines -> Arrays.stream(lines).parallel())
            .filter(word -> word.length() > 3)
            .collect(Collectors.groupingByConcurrent(Function.identity(), Collectors.counting()))
            .entrySet()
            .parallelStream()
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .limit(limit)
            .collect(Collectors.toMap(v -> getName(file, v.getKey()), v -> v.getValue().intValue()))
            .entrySet()
            .parallelStream())
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  @Override
  public Map<String, Integer> inFoldersTopWordsCounter(List<String> folders, int limit) {
    return folders.parallelStream()
        .collect(Collectors.groupingByConcurrent(file -> Path.of(file).getParent(), Collectors.toList()))
        .entrySet()
        .parallelStream()
        .flatMap(files -> files.getValue().parallelStream()
            .flatMap(file -> fileReadLines(file).parallelStream())
            .map(line -> line.split("[^a-zA-Z]"))
            .flatMap(lines -> Arrays.stream(lines).parallel())
            .filter(word -> word.length() > 3)
            .collect(Collectors.groupingByConcurrent(Function.identity(), Collectors.counting()))
            .entrySet()
            .parallelStream()
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .limit(limit)
            .collect(Collectors.toMap(v -> getName(files.getKey(), v.getKey()), v -> v.getValue().intValue()))
            .entrySet()
            .parallelStream())
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  private List<String> fileReadLines(String path) {
    try (Stream<String> stringStream = Files.lines(Path.of(path))) {
      return stringStream.toList();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private String getName(String filePath, String word) {
    return filePath + " - " + word;
  }

  private String getName(Path filePath, String word) {
    return filePath.toString() + " - " + word;
  }

}
