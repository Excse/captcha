package com.arkoisystems.captcha;

import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

@UtilityClass
public class CaptchaGenerator
{
    
    /**
     * Generates a {@link BufferedImage} from the provided {@link Captcha}.
     *
     * @param captcha
     *         the {@link Captcha} used to generate the captcha from.
     *
     * @return a {@link BufferedImage} from the provided {@link Captcha}.
     */
    public BufferedImage generateImage(final Captcha captcha) {
        return captcha.generate();
    }
    
    /**
     * Writes a captcha by the provided {@link Captcha} and path.
     *
     * @param captcha
     *         the {@link Captcha} used to generate the captcha from.
     * @param path
     *         the path to the destination file.
     */
    @SneakyThrows
    public void writeImage(final Captcha captcha, final String path) {
        final BufferedImage captchaImage = generateImage(captcha);
        ImageIO.write(captchaImage, "png", new File(path));
    }
    
    /**
     * Generates a {@link ByteArrayOutputStream} from the provided {@link Captcha} with n
     * frames.
     *
     * @param captcha
     *         the {@link Captcha} used to generate the captcha from.
     * @param frames
     *         the amount of frames the GIF should have.
     * @param speed
     *         the milliseconds between each frame.
     * @param outputStream
     *         the output stream where the GIF is getting stored.
     */
    @SneakyThrows
    public void generateGIF(final Captcha captcha, final int frames, final int speed, final OutputStream outputStream) {
        BufferedImage captchaImage = captcha.generate();
        
        @Cleanup final ImageOutputStream memoryOutput = new MemoryCacheImageOutputStream(outputStream);
        @Cleanup final GIFWriter writer = new GIFWriter(memoryOutput, captchaImage.getType(), speed <= 0 ? 1 : speed, true);
        
        writer.writeToSequence(captchaImage);
        for (int index = 0; index < frames; index++) {
            captchaImage = captcha.generate();
            writer.writeToSequence(captchaImage);
        }
    }
    
    /**
     * Writes a GIF captcha by the provided {@link Captcha} and path with n frames.
     *
     * @param captcha
     *         the {@link Captcha} used to generate the captcha from.
     * @param path
     *         the path to the destination file.
     * @param frames
     *         the amount of frames the GIF should have.
     * @param speed
     *         the milliseconds between each frame.
     */
    @SneakyThrows
    public void writeGIF(final Captcha captcha, final String path, final int frames, final int speed) {
        @Cleanup final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        generateGIF(captcha, frames, speed, outputStream);
        @Cleanup final FileOutputStream fileOutputStream = new FileOutputStream(path);
        
        outputStream.writeTo(fileOutputStream);
    }
    
}
