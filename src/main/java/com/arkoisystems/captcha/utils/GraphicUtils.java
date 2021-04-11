package com.arkoisystems.captcha.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class GraphicUtils
{
    
    public int rgbaToInt(final byte red, final byte green, final byte blue, final byte alpha) {
        return (alpha << 24) + (red << 16) + (green << 8) + blue;
    }

}
