/*
University of Greenwich
School of Computing & Mathematical Sciences
Final Year Project
Author: Chinedu Gabriel Asinugo.
Student ID: 000433816
Class of 2013
* 
* Bits and parts of this class was obtained from various (http://code.google.com/), Sourceforge and guthub Projects,
 */

package finalproject;



import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.imageio.*;
import javax.imageio.stream.*;

/**
 * ImageUtility.java
 * @author Chinedu Gabriel Asinugo
 */

public class ImageUtility {

    static {ImageIO.setUseCache(false);}

    public static BufferedImage read(InputStream in) throws IOException {
        BufferedImage image = null;  
        image = ImageIO.read(in);
        if (image == null)
            throw new IOException("Read fails");                  
        return image;
    }
 
    public static BufferedImage read(byte[] bytes) {
        try {
            return read(new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            e.getStackTrace();
            return null;
        }
    } 

    public static void write(BufferedImage image, 
            float quality, OutputStream out) throws IOException {
        Iterator writers = ImageIO.getImageWritersBySuffix("jpeg");
        if (!writers.hasNext()) {
            throw new IllegalStateException("No writers found");
        }
        ImageWriter writer = (ImageWriter) writers.next();
        ImageOutputStream ios = ImageIO.createImageOutputStream(out);
        writer.setOutput(ios);
        ImageWriteParam param = writer.getDefaultWriteParam();
        if (quality >= 0) {
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality);
        }
        writer.write(null, new IIOImage(image, null, null), param);
        ios.close();
        writer.dispose();
    }

    public static byte[] toByteArray(BufferedImage image, float quality) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            if (quality == -1)
                ImageIO.write(image, "jpeg", out); // write without compression                                
            else
                write(image, quality, out);       // write with compression
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static BufferedImage compress(BufferedImage image, float quality) {
        return read(toByteArray(image, quality));
    }
}
