package ru.hh.school.homework.search.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

class SearchTask extends RecursiveTask<List<String>> {

  private final Folder folder;
  private final String fileName;

  protected SearchTask(Path path, String fileName) throws IOException {
    super();
    this.folder = Folder.fromPath(path);
    this.fileName = fileName;
  }

  @Override
  protected List<String> compute() {
    List<String> foundFiles = new ArrayList<>();
    List<RecursiveTask<List<String>>> forks = new ArrayList<>();
    for (Path path : folder.getFiles()) {
      if (Files.isDirectory(path)) {
        SearchTask task = null;
        try {
          task = new SearchTask(path, fileName);
          forks.add(task);
          task.fork();
        } catch (IOException e) {
          e.printStackTrace();
        }
      } else if (path.getFileName().toString().toUpperCase().contains(fileName.toUpperCase())) {
        foundFiles.add(path.toString());
      }
    }
    for (var task : forks) {
      foundFiles.addAll(task.join());
    }
    return foundFiles;
  }

}
