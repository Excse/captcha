package com.arkoisystems.captcha;

public class OnlyTextCaptcha
{
    
    public static void main(final String[] args) {
        Captcha.CaptchaBuilder builder = Captcha.builder(300, 100)
                .textHalfRotation(30);
        
        CaptchaGenerator.writeGIF(builder, "./images/only-text.gif", 10);
    }
    
}
