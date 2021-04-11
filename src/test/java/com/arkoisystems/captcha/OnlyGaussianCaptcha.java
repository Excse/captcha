package com.arkoisystems.captcha;

public class OnlyGaussianCaptcha
{
    
    public static void main(final String[] args) {
        Captcha captcha = Captcha.builder(300, 100)
                .gaussianNoise(true)
                .gaussianNoiseMean(0.1f)
                .gaussianNoiseSigma(15f)
                .textHalfRotation(30)
                .build();
        
        CaptchaGenerator.writeGIF(captcha, "./images/only-gaussian.gif", 10, 250);
    }
    
}
