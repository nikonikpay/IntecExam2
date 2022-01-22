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
        Set<String> extensions;

        List<File> sortedFilesDirs;


        unsortedFilesDirs = getFiles(unsortedDirectory);

        unsortedFilesDirs.forEach(System.out :: println);
        extensions = extensionsFinder(unsortedFilesDirs);
        extensions.forEach(System.out :: println);


        directoryMaker(sortedDirectory, extensions);


        try {
            fileMover(unsortedFilesDirs, sortedDirectory.toFile());

        } catch(IOException e) {
            e.printStackTrace();
        }


        sortedFilesDirs = getFiles(sortedDirectory);
        sortedFilesDirs.forEach(System.out :: println);


        try {
            summaryMaker(sortedFilesDirs,sortedDirectory);
        } catch(IOException e) {
            e.printStackTrace();
        }


    }


    public static List<File> getFiles(Path unsorted) {
        List<File> results = new ArrayList<>();
        try(Stream<Path> subPaths = Files.walk(unsorted, Integer.MAX_VALUE)) {
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
                    System.out.println(list.getName());
                    extensions.add("hidden");
                }
                if(!extensions.contains("summary")) {
                    extensions.add("summary");
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
                    if(source.toFile().isHidden()) {
                        destination = dir.toPath().resolve("hidden").resolve(file.getName());
                    }
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


    private static void summaryMaker(List<File> filesPath, Path sortedPath) throws IOException {

        List<String> summary = new ArrayList<>();
        Set<String> ext = new HashSet<>();
        String sum = "/summary/summary.txt";


        for(File file : filesPath) {
            ext.add(getExtension(file.getName()));

        }

        summary.add(String.format("%-50s%13s%13s%13s", "name", "readable", "writeable", "hidden"));

        for(String s : ext) {
            summary.add("\n");
            summary.add(s + ":");
            summary.add("----------");
            for(File file : filesPath) {
                if(s.equals(file.getParentFile().getName())) {
                    summary.add(String.format("%-50s%13s%13s%13s", file.getName(), file.canRead() ? "Y" : "N", file.canWrite() ? "Y" : "N", file.isHidden() ? "Y" : "N"));
                }
            }
        }
        //check if the summarty file already exists, if not, it's being created and written
        if(!Paths.get(sortedPath + sum).toFile().exists()) {
            Files.createFile(Paths.get(sortedPath + sum));
        }
        Files.write(Paths.get(sortedPath + sum), summary);


    }
}
