package ru.hh.school.homework.search.file;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface FileSearcher {

  List<String> parallelSearch(Path path, String fileName) throws IOException;

}
