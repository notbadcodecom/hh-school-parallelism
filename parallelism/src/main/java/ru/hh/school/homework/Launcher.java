package ru.hh.school.homework;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.lang.System.currentTimeMillis;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import static java.util.Collections.reverseOrder;
import static java.util.Map.Entry.comparingByValue;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import ru.hh.school.homework.search.PrintUtil;
import ru.hh.school.homework.search.file.ForkJionFilesSearcher;
import ru.hh.school.homework.search.file.FileSearcher;
import ru.hh.school.homework.search.google.CompletableFutureSearcher;
import ru.hh.school.homework.search.google.OnlineSearcher;
import ru.hh.school.homework.search.word.ParallelStreamTopWordsSearcher;
import ru.hh.school.homework.search.word.TopWordsSearcher;

public class Launcher {

  public static void main(String[] args) throws IOException {
    // Написать код, который, как можно более параллельно:
    // - по заданному пути найдет все "*.java" файлы
    Path root = Path.of("D:", "projects", "work", "hh-school",
        "parallelism", "src", "main", "java", "ru", "school", "parallelism");
    String fileName = ".java";
    FileSearcher fileSearcher = new ForkJionFilesSearcher();
    long startTime = currentTimeMillis();
    List<String> files = fileSearcher.parallelSearch(root, fileName);
    PrintUtil.printWordsOfFiles(files, startTime, currentTimeMillis());

    // - для каждого файла вычислит 10 самых популярных слов (см. #naiveCount())
    TopWordsSearcher topWordsSearcher = new ParallelStreamTopWordsSearcher();
    startTime = currentTimeMillis();
    Map<String, Integer> wordsFromFiles = topWordsSearcher.inFilesTopWordsCounter(files, 10);
    PrintUtil.printWordsOfFiles(wordsFromFiles, startTime, currentTimeMillis());

    // - соберет top 10 для каждой папки в которой есть хотя-бы один java файл
    startTime = currentTimeMillis();
    Map<String, Integer> wordsFromFolders = topWordsSearcher.inFoldersTopWordsCounter(files, 10);
    PrintUtil.printWordsOfFolders(wordsFromFolders, startTime, currentTimeMillis());

    // - для каждого слова сходит в гугл и вернет количество результатов по нему (см. #naiveSearch())
    // - распечатает в консоль результаты в виде:
    // <папка1> - <слово #1> - <кол-во результатов в гугле>
    // <папка1> - <слово #2> - <кол-во результатов в гугле>
    // ...
    // <папка1> - <слово #10> - <кол-во результатов в гугле>
    // <папка2> - <слово #1> - <кол-во результатов в гугле>
    // <папка2> - <слово #2> - <кол-во результатов в гугле>
    // ...
    // <папка2> - <слово #10> - <кол-во результатов в гугле>
    // ...
    //
    // Порядок результатов в консоли не обязательный.
    // При желании naiveSearch и naiveCount можно оптимизировать.
    OnlineSearcher onlineSearcher = new CompletableFutureSearcher();
    startTime = currentTimeMillis();
    List<String> resultsFromGoogle = onlineSearcher.onlineSearch(wordsFromFolders);
    PrintUtil.printStringList(resultsFromGoogle, startTime, currentTimeMillis());

    // test our naive methods:
    testCount();
    testSearch();
  }

  private static void testCount() {
    Path path = Path.of("d:\\projects\\work\\hh-school\\parallelism\\src\\main\\java\\ru\\hh\\school\\parallelism\\Runner.java");
    System.out.println(naiveCount(path));
  }

  private static Map<String, Long> naiveCount(Path path) {
    try {
      return Files.lines(path)
        .flatMap(line -> Stream.of(line.split("[^a-zA-Z0-9]")))
        .filter(word -> word.length() > 3)
        .collect(groupingBy(identity(), counting()))
        .entrySet()
        .stream()
        .sorted(comparingByValue(reverseOrder()))
        .limit(10)
        .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static void testSearch() throws IOException {
    System.out.println(naiveSearch("public"));
  }

  private static long naiveSearch(String query) throws IOException {
    Document document = Jsoup //
      .connect("https://www.google.com/search?q=" + query) //
      .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.116 Safari/537.36") //
      .get();

    Element divResultStats = document.select("div#slim_appbar").first();
    String text = divResultStats.text();
    String resultsPart = text.substring(0, text.indexOf('('));
    return Long.parseLong(resultsPart.replaceAll("[^0-9]", ""));
  }

}
