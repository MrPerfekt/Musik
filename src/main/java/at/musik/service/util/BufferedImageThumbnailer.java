package at.musik.service.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.event.Event;
import reactor.function.Function;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.nio.file.Files;
import java.nio.file.Path;

public class BufferedImageThumbnailer implements Function<Event<Path>, Path> {

    private static final ImageObserver DUMMY_OBSERVER = (img, infoflags, x, y, width, height) -> true;

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final int maxLongSide;

    public BufferedImageThumbnailer(int maxLongSide) {
        this.maxLongSide = maxLongSide;
    }

    @Override
    public Path apply(Event<Path> ev) {
        try {
            Path srcPath = ev.getData();
            Path thumbnailPath = Files.createTempFile("thumbnail", ".jpg").toAbsolutePath();
            BufferedImage imgIn = ImageIO.read(srcPath.toFile());
            BufferedImage imgOut;
            if (maxLongSide > 0) {
                double scale;
                if (imgIn.getWidth() >= imgIn.getHeight()) {
                    // horizontal or square image
                    scale = Math.min(maxLongSide, imgIn.getWidth()) / (double) imgIn.getWidth();
                } else {
                    // vertical image
                    scale = Math.min(maxLongSide, imgIn.getHeight()) / (double) imgIn.getHeight();
                }

                imgOut = new BufferedImage((int) (scale * imgIn.getWidth()),
                    (int) (scale * imgIn.getHeight()),
                    imgIn.getType());
                Graphics2D g = imgOut.createGraphics();

                AffineTransform transform = AffineTransform.getScaleInstance(scale, scale);
                g.drawImage(imgIn, transform, DUMMY_OBSERVER);
            } else {
                imgOut = imgIn;
            }
            ImageIO.write(imgOut, "jpeg", thumbnailPath.toFile());

            log.info("Image thumbnail now at: {}", thumbnailPath);

            return thumbnailPath;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

}
