package com.arkoisystems.captcha;

public class OnlyGaussianCaptcha
{
    
    public static void main(final String[] args) {
        Captcha.CaptchaBuilder builder = Captcha.builder(300, 100)
                .gaussianNoise(true)
                .gaussianNoiseMean(0.1f)
                .gaussianNoiseSigma(15f)
                .textHalfRotation(30);
        
        CaptchaGenerator.writeGIF(builder, "./images/only-gaussian.gif", 10);
    }
    
}
