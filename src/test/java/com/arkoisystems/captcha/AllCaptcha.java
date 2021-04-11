package com.arkoisystems.captcha;

public class AllCaptcha
{
    
    public static void main(final String[] args) {
        Captcha.CaptchaBuilder builder = Captcha.builder(300, 100)
                .gaussianNoise(true)
                .gaussianNoiseMean(0.1f)
                .gaussianNoiseSigma(15f)
                .strokeNoise(true)
                .strokeNoiseAmount(3)
                .textHalfRotation(30);
        
        CaptchaGenerator.writeGIF(builder, "./tmp/all.gif", 10);
    }
    
}