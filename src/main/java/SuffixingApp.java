import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;
import java.util.logging.Logger;

public class SuffixingApp {
    private static final Logger logger = Logger.getLogger(SuffixingApp.class.getName());

    public static void main(String[] args) {

        Properties properties = new Properties();
        FileInputStream fileInputStream;

        try {
            fileInputStream = new FileInputStream(args[0]);
            properties.load(fileInputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String mode = properties.getProperty("mode");
        String suffix = properties.getProperty("suffix");
        String files = properties.getProperty("files");


        if (!mode.equalsIgnoreCase("copy") && !mode.equalsIgnoreCase("move")) {
            logger.severe("Mode is not recognized: " + mode);
            return;
        }

        if (suffix == null) {
            logger.severe("No suffix is configured");
            return;
        }

        if (files == null || files.isEmpty()) {
            logger.warning("No files are configured to be copied/moved");
            return;
        }

        String[] directories = files.split(":");
        processFile(directories, suffix, mode);
    }

    private static void processFile(String[] directories, String suffix, String mode) {
        for (String nameFile : directories) {
            File file = new File(nameFile);
            if (!file.exists()) {
                logger.severe("No such file: " + changeSlashes(file.getPath()));
                continue;
            }

            String newFileName = newFileName(file, suffix);
            File newFile = new File(file.getParent(), newFileName);
            try {
                if (mode.equalsIgnoreCase("move")) {
                    Files.move(file.toPath(), newFile.toPath());
                    logger.info(changeSlashes(file.getPath()) + " => " + changeSlashes(newFile.getPath()));

                } else if (mode.equalsIgnoreCase("copy")) {
                    Files.copy(file.toPath(), newFile.toPath());
                    logger.info(changeSlashes(file.getPath()) + " -> " + changeSlashes(newFile.getPath()));
                }
            } catch (IOException e) {
                throw new RuntimeException();
            }
        }
    }

    private static String changeSlashes(String path) {
        return path.replace("\\", "/");
    }

    private static String newFileName(File file, String s) {
        String fileName = file.getName();
        int indexLastDot = fileName.lastIndexOf('.');
        return fileName.substring(0, indexLastDot) + s + fileName.substring(indexLastDot);
    }

}
