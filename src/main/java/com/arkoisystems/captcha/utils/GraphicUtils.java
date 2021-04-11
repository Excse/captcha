package com.arkoisystems.captcha.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class GraphicUtils
{
    
    /**
     * Turns RGBA values into a integer which you can use to draw graphics.
     *
     * @param red
     *         the red value of the color.
     * @param green
     *         the green value of the color.
     * @param blue
     *         the blue value of the color.
     * @param alpha
     *         the alpha value of the color
     *
     * @return a integer representing the provided RGBA values.
     */
    public int rgbaToInt(final byte red, final byte green, final byte blue, final byte alpha) {
        return (alpha << 24) + (red << 16) + (green << 8) + blue;
    }
    
}
