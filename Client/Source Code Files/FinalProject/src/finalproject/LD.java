/*
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

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import finalproject.ImageUtility;

/**
 *
 * @author Chinedu Gabriel Asinugo
 */
public class LD {

    private Robot rt;

    private Toolkit tk = null;
    private Rectangle screenRect;
    
    public LD() {
        tk = Toolkit.getDefaultToolkit();
        screenRect = new Rectangle(tk.getScreenSize()); 
        try {               
            rt = new Robot();
        }
        catch (AWTException awte) {
            awte.getStackTrace();
        }     
    }
    /*
     * capture the current screen display
     * as bufferedImage
     */
    public BufferedImage captureScreen() {    
        screenRect = new Rectangle(tk.getScreenSize()); 
        return rt.createScreenCapture(screenRect); 
    }

    /*
     * capture the current screen as 
     * byte array
     */
    public byte[] CaptureScreenByteArray() {  
        return ImageUtility.toByteArray(captureScreen(),(float)0.4);
    }        
    
    public Rectangle getScreenRect() {  
        return screenRect;
    }  
}