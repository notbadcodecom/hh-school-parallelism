package ru.hh.school.homework.search.file;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class ForkJionFilesSearcher implements FileSearcher {

  private final ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();

  @Override
  public List<String> parallelSearch(Path path, String fileName) throws IOException {
    return forkJoinPool.invoke(new SearchTask(path, fileName));
  }

}
