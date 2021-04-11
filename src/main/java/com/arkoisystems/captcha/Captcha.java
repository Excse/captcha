package com.arkoisystems.captcha;

import com.arkoisystems.captcha.utils.Callback;
import com.arkoisystems.captcha.utils.GraphicUtils;
import com.arkoisystems.captcha.utils.RandomUtils;
import lombok.*;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@ToString
public class Captcha
{
    
    // --- GENERAL SETTINGS ---
    
    @Getter
    private final int width, height;
    
    @Builder.Default
    @Getter
    private final Font font = new Font("Tippa", Font.PLAIN, 30);
    
    // --- TEXT SETTINGS ---
    
    @Builder.Default
    @Getter
    private final Callback<String, Integer> textGenerator = value -> {
        final String randomString = RandomUtils.randomAlphanumeric(value);
        final byte[] stringBytes = randomString.getBytes(StandardCharsets.UTF_8);
        return new String(stringBytes);
    };
    
    @Builder.Default
    @Getter
    private final int textHalfRotation = 60;
    
    @Builder.Default
    @Getter
    private final Color textColor = new Color(139, 103, 103, 186);
    
    @Builder.Default
    @Getter
    @Setter
    private int textLength = 6;
    
    @Getter
    @Setter
    private String text;
    
    // --- GAUSSIAN NOISE SETTINGS ---
    
    @Getter
    private final boolean gaussianNoise;
    
    @Builder.Default
    @Getter
    private final float gaussianNoiseMean = 0f;
    
    @Builder.Default
    @Getter
    private final float gaussianNoiseSigma = 30f;
    
    // --- STROKE NOISE SETTINGS ---
    
    @Builder.Default
    @Getter
    private final int strokeNoiseAmount = 20;
    
    @Getter
    private final boolean strokeNoise;
    
    @Builder.Default
    @Getter
    private final Color strokeNoiseColor = new Color(241, 167, 167, 87);
    
    // --- MAIN METHODS ---
    
    public BufferedImage generate() {
        final BufferedImage image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
        
        final String text = this.getText() != null ? this.getText() : this.getTextGenerator().callback(this.getTextLength());
        this.setTextLength(text.length());
        this.setText(text);
        
        final Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setFont(this.getFont());
        
        this.drawText(graphics);
        this.drawStrokeNoise(graphics);
        this.drawGaussianNoise(image);
        
        graphics.dispose();
        
        return image;
    }
    
    private void drawText(final Graphics2D graphics) {
        graphics.setColor(this.getStrokeNoiseColor());
        
        final float segment = (float) this.getWidth() / (float) this.getTextLength();
        for (int index = 0; index < this.getTextLength(); index++) {
            final float minX = (index * segment) + (segment / 4);
            final float maxX = ((index + 1) * segment) - (segment / 4);
            final float minY = graphics.getFontMetrics().getHeight();
            final float maxY = this.getHeight() - graphics.getFontMetrics().getHeight();
            
            final int randomX = (int) RandomUtils.nextFloat(minX, maxX);
            final int randomY = (int) RandomUtils.nextFloat(minY, maxY);
            
            final double theta = Math.toRadians(ThreadLocalRandom.current().nextInt(
                    -this.getTextHalfRotation(),
                    this.getTextHalfRotation())
            );
            
            graphics.rotate(theta, randomX, randomY);
            graphics.drawChars(this.getText().toCharArray(), index, 1, randomX, randomY);
            graphics.rotate(-theta, randomX, randomY);
        }
    }
    
    private void drawGaussianNoise(final BufferedImage image) {
        if (!this.isGaussianNoise())
            return;
        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                final double noise = ThreadLocalRandom.current().nextGaussian() * this.getGaussianNoiseSigma() + this.getGaussianNoiseMean();
                final Color color = new Color(image.getRGB(x, y));
                
                final int gray = (color.getRed() + color.getGreen() + color.getBlue()) / 3;
                final byte alpha = (byte) color.getAlpha();
                
                final double newColorD = Math.min(Math.max(0, gray + noise), 255);
                final byte newColor = (byte) Math.round(newColorD);
                
                final int rgb = GraphicUtils.rgbaToInt(newColor, newColor, newColor, alpha);
                
                image.setRGB(x, y, rgb);
            }
        }
    }
    
    private void drawStrokeNoise(final Graphics2D graphics) {
        if (!this.isStrokeNoise())
            return;
        
        graphics.setColor(this.getStrokeNoiseColor());
        for (int index = 0; index < strokeNoiseAmount; index++) {
            final Path2D.Double path = new Path2D.Double();
            path.moveTo(
                    RandomUtils.nextInt(this.getWidth()),
                    RandomUtils.nextInt(this.getHeight())
            );
            path.curveTo(
                    RandomUtils.nextInt(this.getWidth()),
                    RandomUtils.nextInt(this.getHeight()),
                    RandomUtils.nextInt(this.getWidth()),
                    RandomUtils.nextInt(this.getHeight()),
                    RandomUtils.nextInt(this.getWidth()),
                    RandomUtils.nextInt(this.getHeight())
            );
            graphics.draw(path);
        }
    }
    
    // --- LOMBOK BUILDER ---
    
    private static CaptchaBuilder builder() {
        return new CaptchaBuilder();
    }
    
    public static CaptchaBuilder builder(final int width, final int height) {
        return builder().width(width).height(height);
    }
    
}
