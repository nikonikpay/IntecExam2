import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileIOApp {

    public static void main(String[] args) {

        Path unsortedDirectory = Paths.get("D:\\unsorted");
        Path sortedDirectory = Paths.get("D:\\sorted");

        List<File> unsortedFilesDirs;
        List<File> hiddenFiles;
        Set<String> extensions;


        hiddenFiles = getHiddenFiles(unsortedDirectory);
        hiddenFiles.forEach(System.out :: println);


//        unsortedFilesDirs = getFiles(unsortedDirectory);
//
//        //        unsortedFilesDirs.forEach(System.out::println);
//        extensions = extensionsFinder(unsortedFilesDirs);
//        extensions.forEach(System.out :: println);
//        directoryMaker(sortedDirectory, extensions);
//
//
//        try {
//            fileMover(unsortedFilesDirs, sortedDirectory.toFile());
//
//        } catch(IOException e) {
//            e.printStackTrace();
//        }


    }


    public static List<File> getHiddenFiles(Path path) {

        List<File> results = new ArrayList<>();
        try(Stream<Path> subPaths = Files.walk(path, Integer.MAX_VALUE);) {
            results = subPaths.filter(e -> e.toFile().isHidden()).map(Path :: toFile).collect(Collectors.toList());
        } catch(IOException e) {
            e.printStackTrace();
        }
        return results;
    }


    public static List<File> getFiles(Path unsorted) {
        List<File> results = new ArrayList<>();
        try(Stream<Path> subPaths = Files.walk(unsorted, Integer.MAX_VALUE);) {
            results = subPaths.filter(e -> e.toFile().isFile()).map(Path :: toFile).collect(Collectors.toList());
        } catch(IOException e) {
            e.printStackTrace();
        }
        return results;
    }


    public static HashSet<String> extensionsFinder(List<File> files) {
        HashSet<String> extensions = new HashSet<>();
        for(File list : files) {
            int i = list.toString().lastIndexOf('.');
            if(i >= 0) {
                extensions.add(list.toString().substring(i + 1));
                if(list.isHidden()) {
                    extensions.add("hidden");
                }
            }
        }
        return extensions;
    }


    public static void directoryMaker(Path sorted, Set<String> extensions) {
        Iterator extension = extensions.iterator();
        while(extension.hasNext()) {
            File dir = new File(String.valueOf(sorted.resolve(extension.next().toString())));
            if(!dir.exists()) {
                dir.mkdir();
            }
        }

    }


    public static void fileMover(List<File> files, File dir) throws IOException {
        for(File file : files) {
            String extension = getExtension(file.getName());
            if(extension != null) {
                Path source = file.toPath();
                Path destination = dir.toPath().resolve(extension).resolve(file.getName());
                if(!destination.toFile().exists()) {
                    Files.move(source, destination);

                }
            }
        }
    }

    private static String getExtension(String file) {
        String ext = null;
        try {
            if(file != null) {
                ext = file.substring(file.lastIndexOf('.') + 1);
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        return ext;
    }


}
