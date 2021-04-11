package com.arkoisystems.captcha;

import lombok.Getter;
import lombok.SneakyThrows;

import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.RenderedImage;
import java.io.Closeable;

// https://memorynotfound.com/generate-gif-image-java-delay-infinite-loop-example/
public class GIFWriter implements Closeable
{
    
    @Getter
    private final ImageWriteParam parameter;
    
    @Getter
    private final ImageWriter writer;
    
    @Getter
    private final IIOMetadata metadata;
    
    /**
     * The default constructor for this class.
     *
     * @param output
     *         the output stream used to write the GIF to.
     * @param imageType
     *         the image type e.g. PNG.
     * @param delay
     *         the delay between every frame (image)
     * @param loop
     *         the flag if the GIF should be looped or not.
     */
    @SneakyThrows
    public GIFWriter(final ImageOutputStream output, final int imageType, final int delay, final boolean loop) {
        this.writer = ImageIO.getImageWritersBySuffix("gif").next();
        this.parameter = writer.getDefaultWriteParam();
        
        final ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(imageType);
        this.metadata = writer.getDefaultImageMetadata(typeSpecifier, parameter);
        
        this.configureRootMetadata(delay, loop);
        
        this.getWriter().setOutput(output);
        this.getWriter().prepareWriteSequence(null);
    }
    
    /**
     * Configures the metadata so you can loop or delay each image of the GIF.
     *
     * @param delay
     *         the delay between every frame (image).
     * @param loop
     *         the flag if the GIF should be looped or not.
     */
    @SneakyThrows
    private void configureRootMetadata(final int delay, final boolean loop) {
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
    
    /**
     * Gets a node from a root node. If it doesn't exist it will create a new child.
     *
     * @param root
     *         the root node used to search the provided name.
     * @param name
     *         the name used to search for the node.
     *
     * @return the resulting node found or created by the method.
     */
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
    
    /**
     * Writes a new image to the sequence used to generate the GIF from.
     *
     * @param image
     *         the image which will be displayed in the GIF.
     */
    @SneakyThrows
    public void writeToSequence(final RenderedImage image) {
        this.getWriter().writeToSequence(new IIOImage(image, null, metadata), parameter);
    }
    
    /**
     * The {@link Closeable#close()} method used to close every open stream.
     */
    @SneakyThrows
    @Override
    public void close() {
        this.getWriter().endWriteSequence();
    }
    
}
