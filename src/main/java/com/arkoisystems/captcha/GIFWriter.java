package com.arkoisystems.captcha;

import lombok.Getter;
import lombok.SneakyThrows;

import javax.imageio.*;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.RenderedImage;
import java.io.Closeable;
import java.io.IOException;

// https://memorynotfound.com/generate-gif-image-java-delay-infinite-loop-example/
public class GIFWriter implements Closeable
{
    
    @Getter
    private final ImageWriteParam params;
    
    @Getter
    private final ImageWriter writer;
    
    @Getter
    private final IIOMetadata metadata;
    
    @SneakyThrows
    public GIFWriter(final ImageOutputStream output, final int imageType, final int delay, final boolean loop) {
        this.writer = ImageIO.getImageWritersBySuffix("gif").next();
        this.params = writer.getDefaultWriteParam();
        
        final ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(imageType);
        this.metadata = writer.getDefaultImageMetadata(typeSpecifier, params);
        
        this.configureRootMetadata(delay, loop);
    
        this.getWriter().setOutput(output);
        this.getWriter().prepareWriteSequence(null);
    }
    
    private void configureRootMetadata(final int delay, final boolean loop) throws IIOInvalidTreeException {
        final String metaFormatName = metadata.getNativeMetadataFormatName();
        final IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(metaFormatName);
        
        final IIOMetadataNode controlExtension = this.getNode(root, "GraphicControlExtension");
        controlExtension.setAttribute("disposalMethod", "none");
        controlExtension.setAttribute("userInputFlag", "FALSE");
        controlExtension.setAttribute("transparentColorFlag", "FALSE");
        controlExtension.setAttribute("delayTime", Integer.toString(delay / 10));
        controlExtension.setAttribute("transparentColorIndex", "0");
        
        final IIOMetadataNode appExtensions = this.getNode(root, "ApplicationExtensions");
        final IIOMetadataNode child = new IIOMetadataNode("ApplicationExtension");
        child.setAttribute("applicationID", "NETSCAPE");
        child.setAttribute("authenticationCode", "2.0");
        
        final int loopContinuously = loop ? 0 : 1;
        child.setUserObject(new byte[] { 0x1, (byte) (loopContinuously & 0xFF), (byte) ((loopContinuously >> 8) & 0xFF) });
        appExtensions.appendChild(child);
        
        metadata.setFromTree(metaFormatName, root);
    }
    
    private IIOMetadataNode getNode(final IIOMetadataNode root, final String name) {
        final int nodes = root.getLength();
        for (int index = 0; index < nodes; index++) {
            if (!root.item(index).getNodeName().equalsIgnoreCase(name))
                continue;
            return (IIOMetadataNode) root.item(index);
        }
        
        final IIOMetadataNode node = new IIOMetadataNode(name);
        root.appendChild(node);
        
        return node;
    }
    
    public void writeToSequence(final RenderedImage image) throws IOException {
        this.getWriter().writeToSequence(new IIOImage(image, null, metadata), params);
    }
    
    @Override
    public void close() throws IOException {
        this.getWriter().endWriteSequence();
    }
    
}
