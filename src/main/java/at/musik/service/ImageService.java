package at.musik.service;

import io.netty.buffer.ByteBuf;
import liquibase.util.file.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class ImageService implements ResourceLoaderAware {
    public static final String imageLocation = "resources/images/";
    public enum ImageSize {
        original(-1),
        converted(Integer.MAX_VALUE),
        thumbnail(250);

        private int maxLongSize;

        public int getMaxLongSize() {
            return maxLongSize;
        }

        public String getImageLocation() {
            return imageLocation + "/" + this.name() + "/";
        }

        ImageSize(int maxLongSize) {
            this.maxLongSize = maxLongSize;
        }
    }
    public static final String imageFormat = "jpg";

    private final Logger log = LoggerFactory.getLogger(MailService.class);

    private ResourceLoader resourceLoader;

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public Resource getResource(String location){
        return resourceLoader.getResource(location);
    }

    public String saveImage(MultipartFile inputFile, String location) {
        String filenameWithExtension = inputFile.getOriginalFilename();
        String baseName = FilenameUtils.getBaseName(filenameWithExtension);
        String extension = FilenameUtils.getExtension(filenameWithExtension);
        int fileNumber;

        String originalDirectory = ImageSize.original.getImageLocation() +location+"/";
        if (!inputFile.isEmpty()) {
            try {
                // Store image
                File originalDirectoryFile = resourceLoader.getResource(originalDirectory).getFile();
                originalDirectoryFile.mkdirs();
                String originalFilePath;
                File originalFile;
                fileNumber = -1;
                File[] files;
                String filename;
                do {
                    fileNumber++;
                    filename = baseName+"-"+fileNumber;
                    final String currentFilename = filename;
                    files = originalDirectoryFile.listFiles((File dir, String name) -> name.startsWith(currentFilename));
                } while (files.length > 0);
                originalFilePath = originalDirectory + baseName + "-" + fileNumber + "." + extension;
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
        return location + "/" + baseName + "-" + fileNumber;
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
            newHeigh = (int)(scale * heigh);
        } else { // vertical image
            newHeigh = Math.min(imageSize.getMaxLongSize(), heigh);
            scale = newHeigh / (double) heigh;
            newWidth = (int)(scale * width);
        }

        String imagePath = imageSize.getImageLocation() + location + "/";
        String imagePathName = imagePath + fileName + "." + imageFormat;
        BufferedImage img = new BufferedImage(newWidth, newHeigh, BufferedImage.TYPE_INT_RGB);
        img.createGraphics().drawImage(ImageIO.read(originalFile).getScaledInstance(newWidth, newHeigh, Image.SCALE_SMOOTH), 0, 0, null);
        resourceLoader.getResource(imagePath).getFile().mkdirs();
        ImageIO.write(img, imageFormat, resourceLoader.getResource(imagePathName).getFile());

        log.info("Image {} saved as: {}", imageSize.name(), imagePathName);
    }

    private static Path readUpload(ByteBuf content) throws IOException {
        byte[] bytes = new byte[content.readableBytes()];
        content.readBytes(bytes);
        content.release();

        // write to a temp file
        Path imgIn = Files.createTempFile("upload", ".jpg");
        Files.write(imgIn, bytes);

        imgIn.toFile().deleteOnExit();

        return imgIn;
    }
}
