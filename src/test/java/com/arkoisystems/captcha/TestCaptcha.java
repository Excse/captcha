package com.arkoisystems.captcha;

public class TestCaptcha
{
    
    public static void main(final String[] args) {
        Captcha captcha = Captcha.builder(300, 100)
                .gaussianNoise(true)
                .gaussianNoiseMean(0.1f)
                .gaussianNoiseSigma(15f)
                .strokeNoise(true)
                .strokeNoiseAmount(3)
                .textHalfRotation(30)
                .build();
        
        CaptchaGenerator.writeGIF(captcha, "./tmp/example.gif", 10, 250);
    }
    
}
