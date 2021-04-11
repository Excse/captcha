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

@UtilityClass
public class CaptchaGenerator
{
    
    /**
     * Generates a {@link BufferedImage} from the provided {@link
     * Captcha.CaptchaBuilder}.
     *
     * @param captchaBuilder
     *         the {@link Captcha.CaptchaBuilder} used to generate the captcha from.
     *
     * @return a {@link BufferedImage} from the provided {@link Captcha.CaptchaBuilder}.
     */
    public BufferedImage generateImage(final Captcha.CaptchaBuilder captchaBuilder) {
        final Captcha captcha = captchaBuilder.build();
        return captcha.generate();
    }
    
    /**
     * Writes a captcha by the provided {@link Captcha.CaptchaBuilder} and path.
     *
     * @param captchaBuilder
     *         the {@link Captcha.CaptchaBuilder} used to generate the captcha from.
     * @param path
     *         the path to the destination file.
     */
    @SneakyThrows
    public void writeImage(final Captcha.CaptchaBuilder captchaBuilder, final String path) {
        final BufferedImage captchaImage = generateImage(captchaBuilder);
        ImageIO.write(captchaImage, "png", new File(path));
    }
    
    /**
     * Generates a {@link ByteArrayOutputStream} from the provided {@link
     * Captcha.CaptchaBuilder} with n frames.
     *
     * @param captchaBuilder
     *         the {@link Captcha.CaptchaBuilder} used to generate the captcha from.
     * @param frames
     *         the amount of frames the GIF should have.
     *
     * @return a {@link ByteArrayOutputStream} containing the generated GIF.
     */
    @SneakyThrows
    public ByteArrayOutputStream generateGIF(final Captcha.CaptchaBuilder captchaBuilder, final int frames) {
        final Captcha captcha = captchaBuilder.build();
        BufferedImage captchaImage = captcha.generate();
        
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        @Cleanup final ImageOutputStream memoryOutput = new MemoryCacheImageOutputStream(outputStream);
        @Cleanup final GIFWriter writer = new GIFWriter(memoryOutput, captchaImage.getType(), 300, true);
        
        writer.writeToSequence(captchaImage);
        for (int index = 0; index < frames; index++) {
            captchaImage = captcha.generate();
            writer.writeToSequence(captchaImage);
        }
        
        return outputStream;
    }
    
    /**
     * Writes a GIF captcha by the provided {@link Captcha.CaptchaBuilder} and path with n
     * frames.
     *
     * @param captchaBuilder
     *         the {@link Captcha.CaptchaBuilder} used to generate the captcha from.
     * @param path
     *         the path to the destination file.
     * @param frames
     *         the amount of frames the GIF should have.
     */
    @SneakyThrows
    public void writeGIF(final Captcha.CaptchaBuilder captchaBuilder, final String path, final int frames) {
        @Cleanup final ByteArrayOutputStream outputStream = generateGIF(captchaBuilder, frames);
        @Cleanup final FileOutputStream fileOutputStream = new FileOutputStream(path);
        
        outputStream.writeTo(fileOutputStream);
    }
    
}
