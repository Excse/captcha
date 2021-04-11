package com.arkoisystems.captcha;

import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;

public class TestCaptcha
{
    
    public static void main(final String[] args) throws Exception {
        Captcha captcha = Captcha.builder(300, 100)
                .gaussianNoise(true)
                .gaussianNoiseMean(0.1f)
                .gaussianNoiseSigma(15f)
                .strokeNoise(true)
                .strokeNoiseAmount(3)
                .textHalfRotation(30)
                .build();
        
        BufferedImage captchaImage = captcha.generate();
        
        final ImageOutputStream output = new FileImageOutputStream(new File("./tmp/example.gif"));
        final GIFWriter writer = new GIFWriter(output, captchaImage.getType(), 300, true);
        
        writer.writeToSequence(captchaImage);
        for(int index = 0; index < 10; index++) {
            captchaImage = captcha.generate();
            writer.writeToSequence(captchaImage);
        }
        
        writer.close();
        output.close();
    }
    
}
