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
    
    /**
     * The width and height of the image.
     */
    @Getter
    private final int width, height;
    
    /**
     * The default font which is used to write the text.
     */
    @Builder.Default
    @Getter
    private final Font font = new Font("Tippa", Font.PLAIN, 30);
    
    // --- TEXT SETTINGS ---
    
    /**
     * The callback used to generate the random string. If not set it will use a default
     * callback which produces an alphanumeric string.
     */
    @Builder.Default
    @Getter
    private final Callback<String, Integer> textGenerator = value -> {
        final String randomString = RandomUtils.randomAlphanumeric(value);
        final byte[] stringBytes = randomString.getBytes(StandardCharsets.UTF_8);
        return new String(stringBytes);
    };
    
    /**
     * The values describes in which angle the characters can rotate (e.g. -60 & +60).
     */
    @Builder.Default
    @Getter
    private final int textHalfRotation = 60;
    
    /**
     * The text color of the characters.
     */
    @Builder.Default
    @Getter
    private final Color textColor = new Color(139, 103, 103, 186);
    
    /**
     * The amount of characters the text should have.
     */
    @Builder.Default
    @Getter
    @Setter
    private int textLength = 6;
    
    /**
     * If you set this variable you can provide your own text and so nothing will be
     * generated.
     */
    @Getter
    @Setter
    private String text;
    
    // --- GAUSSIAN NOISE SETTINGS ---
    
    /**
     * The flag if the gaussian noise is enabled or not.
     */
    @Getter
    private final boolean gaussianNoise;
    
    /**
     * The mean of the gaussian noise.
     */
    @Builder.Default
    @Getter
    private final float gaussianNoiseMean = 0f;
    
    /**
     * The sigma value of the gaussian noise.
     */
    @Builder.Default
    @Getter
    private final float gaussianNoiseSigma = 30f;
    
    // --- STROKE NOISE SETTINGS ---
    
    /**
     * The amount of strokes which will be generated.
     */
    @Builder.Default
    @Getter
    private final int strokeNoiseAmount = 20;
    
    /**
     * The flag if the stroke noise is enabled or not.
     */
    @Getter
    private final boolean strokeNoise;
    
    /**
     * The color of the strokes.
     */
    @Builder.Default
    @Getter
    private final Color strokeNoiseColor = new Color(241, 167, 167, 87);
    
    // --- MAIN METHODS ---
    
    /**
     * Generates a {@link BufferedImage} from the settings provided.
     *
     * @return a generated {@link BufferedImage}
     */
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
    
    /**
     * Writes a text to the provided graphics. Each character gets rotated and is
     * positioned at random locations.
     *
     * @param graphics
     *         the graphics used to write the text to.
     */
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
    
    /**
     * Adds gaussian noise to the image provided. This makes it harder to read the captcha
     * (used to prevent bots).
     *
     * @param image
     *         the image which will get noised.
     */
    private void drawGaussianNoise(final BufferedImage image) {
        if (!this.isGaussianNoise())
            return;
        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                final double noise = RandomUtils.nextGaussian() * this.getGaussianNoiseSigma() + this.getGaussianNoiseMean();
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
    
    /**
     * Draws random strokes to the graphics so the text is not as visible as before.
     *
     * @param graphics
     *         the graphics used to draw the strokes to.
     */
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
    
    /**
     * Makes the lombok builder method private so we can use our own method.
     *
     * @return a {@link CaptchaBuilder} generated by lombok.
     */
    private static CaptchaBuilder builder() {
        return new CaptchaBuilder();
    }
    
    /**
     * Our own method to override the method from lombok. This is used to give necessary
     * information to the builder e.g. the width or height.
     *
     * @param width
     *         the width of the generated image.
     * @param height
     *         the height of the generated image.
     *
     * @return a {@link CaptchaBuilder} generated by lombok.
     */
    public static CaptchaBuilder builder(final int width, final int height) {
        return builder().width(width).height(height);
    }
    
}
