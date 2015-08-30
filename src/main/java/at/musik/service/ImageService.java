package at.musik.service;

import liquibase.util.file.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Service
public class ImageService implements ResourceLoaderAware {
    public static final String imageFormat = "jpg";
    public static final String imageLocation = "resources/images/";
    private ResourceLoader resourceLoader;

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public Resource getResource(String location) {
        return resourceLoader.getResource(location);
    }

    public enum ImageSize {
        original(-1),
        converted(Integer.MAX_VALUE),
        thumbnail(250);

        private int maxLongSize;

        public int getMaxLongSize() {
            return maxLongSize;
        }

        public String getImageLocation() {
            return imageLocation + this.name() + "/";
        }

        ImageSize(int maxLongSize) {
            this.maxLongSize = maxLongSize;
        }
    }

    private final Logger log = LoggerFactory.getLogger(ImageService.class);


    public String saveImage(MultipartFile inputFile, String fileName, String location) {
        String filenameWithExtension = inputFile.getOriginalFilename();
        String baseName = (fileName == null || fileName.isEmpty()) ? FilenameUtils.getBaseName(filenameWithExtension) : fileName;
        String fileType = FilenameUtils.getExtension(filenameWithExtension);
        int fileNumber;
        String filename;

        String originalDirectory = getFolderPath(location, ImageSize.original);
        if (!inputFile.isEmpty()) {
            try {
                // Store image
                File originalDirectoryFile = resourceLoader.getResource(originalDirectory).getFile();
                originalDirectoryFile.mkdirs();
                String originalFilePath;
                File originalFile;
                fileNumber = -1;
                File[] files;
                do {
                    fileNumber++;
                    filename = baseName + "-" + fileNumber;
                    final String currentFilename = filename;
                    files = originalDirectoryFile.listFiles((File dir, String name) -> name.startsWith(currentFilename));
                } while (files.length > 0);
                originalFilePath = getFilePath(location, ImageSize.original, filename, fileType);
                originalFile = resourceLoader.getResource(originalFilePath).getFile();
                inputFile.transferTo(originalFile);
                log.info("Image original saved as {}", originalFilePath);

                for (ImageSize imageSize : ImageSize.values()) {
                    if (imageSize != ImageSize.original) {
                        saveImage(originalFile, imageSize, location, filename);
                    }
                }
            } catch (Exception e) {
                log.error("Product image saving failed", e);
                return "";
            }
        } else {
            return "";
        }
        return filename;
    }

    private void saveImage(File originalFile, ImageSize imageSize, String location, String fileName) throws IOException {
        BufferedImage imgIn = ImageIO.read(originalFile);
        double scale;
        int width = imgIn.getWidth();
        int heigh = imgIn.getHeight();
        int newWidth;
        int newHeigh;
        if (width >= heigh) { // horizontal or square image
            newWidth = Math.min(imageSize.getMaxLongSize(), width);
            scale = newWidth / (double) width;
            newHeigh = (int) (scale * heigh);
        } else { // vertical image
            newHeigh = Math.min(imageSize.getMaxLongSize(), heigh);
            scale = newHeigh / (double) heigh;
            newWidth = (int) (scale * width);
        }

        String folderPath = getFolderPath(location, imageSize);
        String imagePathName = getFilePath(location, imageSize, fileName);
        BufferedImage img = new BufferedImage(newWidth, newHeigh, BufferedImage.TYPE_INT_RGB);
        img.createGraphics().drawImage(ImageIO.read(originalFile).getScaledInstance(newWidth, newHeigh, Image.SCALE_SMOOTH), 0, 0, null);
        resourceLoader.getResource(folderPath).getFile().mkdirs();
        ImageIO.write(img, imageFormat, resourceLoader.getResource(imagePathName).getFile());
        img.flush();
        log.info("Image {} saved as: {}", imageSize.name(), imagePathName);
    }

    public byte[] loadImage(String name, String location, ImageSize imageSize) throws IOException {
        return Files.readAllBytes(resourceLoader.getResource(getFilePath(location, imageSize, name)).getFile().toPath());
    }


    public static String getFilePath(String location, ImageSize imageSize, String name) {
        return getFilePath(location, imageSize, name, imageFormat);
    }

    public static String getFilePath(String location, ImageSize imageSize, String name, String fileType) {
        return getFolderPath(location, imageSize) + name + "." + fileType;
    }

    public static String getFolderPath(String location, ImageSize imageSize) {
        return imageSize.getImageLocation() + location + "/";
    }
}
