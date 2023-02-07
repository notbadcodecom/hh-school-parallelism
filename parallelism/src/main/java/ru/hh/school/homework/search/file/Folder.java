package ru.hh.school.homework.search.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class Folder {

  private final List<Path> files;

  protected Folder(List<Path> files) {
    this.files = files;
  }

  protected List<Path> getFiles() {
    return files;
  }

  protected static Folder fromPath(Path path) throws IOException {
    try (Stream<Path> lines = Files.list(path)) {
      return new Folder(lines.toList());
    }
  }

}
