package com.arkoisystems.captcha;

public class OnlyTextCaptcha
{
    
    public static void main(final String[] args) {
        Captcha captcha = Captcha.builder(300, 100)
                .textHalfRotation(30)
                .build();
        
        CaptchaGenerator.writeGIF(captcha, "./images/only-text.gif", 10, 250);
    }
    
}
