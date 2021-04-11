package com.arkoisystems.captcha.utils;

import lombok.experimental.UtilityClass;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@UtilityClass
public class RandomUtils
{
    
    protected ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
    
    public String randomAlphanumeric(final int length) {
        return RANDOM.ints(48, 123)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
    
    public float nextFloat(final float lowerBound, final float upperBound) {
        return (float) RANDOM.nextDouble(lowerBound, upperBound);
    }
    
    public float nextInt(final int upperBound) {
        return nextInt(0, upperBound);
    }
    
    public float nextInt(final int lowerBound, final int upperBound) {
        return (float) RANDOM.nextInt(lowerBound, upperBound);
    }
    
}
