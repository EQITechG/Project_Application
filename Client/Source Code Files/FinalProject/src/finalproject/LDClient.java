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

import finalproject.DesktopViewer;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

/**
 *
 * @author Chinedu Gabriel Asinugo
 */
public class LDClient implements Runnable{
    boolean run=false;
    Socket s;
    ObjectOutputStream oos;
    ObjectInputStream ois;
    public String MysUname;
    DesktopViewer dv;

    /*
     * contructor
     * connect to the broadcast server at the given
     * socket address
     */
    
    public LDClient(SocketAddress sa) throws SocketException, IOException {
       
            s=new Socket();
            s.setSoTimeout(0);
            s.setKeepAlive(true);
            
            s.connect(sa);
       
    }
    
    public void serMyUname(String s)
    {
        MysUname=s;
    }
    
    
    /*
     * start a thread that keeps updating the desktop we are viewing
     * it keeps polling the other end broadcast server for new images
     */
    public void xstart(DesktopViewer dvo)
    {
        dv=dvo;
        run=true;
        new Thread(this).start();
    }
    
    public void stop()
    {
        run=false;
        try {
            s.close();
        } catch (IOException ex) {
            //frmMain.logger.log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
        try
        {
            if(dv!=null)
                dv.dispose();
        }catch(Exception ex)
        {
            
        }
       
         Command t=new Command();
         t.cmd="ENDSCREEN"; //tell the server we have stopped viewing the desktop
         t.sa=ClientGUI.chatwith;
         ClientGUI.c.send(t);



        Message m=new Message();
        m.sender="Server";
        m.msg=" Screen share ended";
        ClientGUI.msgRecieved(m);
    }
    
    public void run() {
        try {
            BufferedImage bi=null;
            System.out.println("LDClient RUN");
            //Image im;
            oos = new ObjectOutputStream(s.getOutputStream());
            ois = new ObjectInputStream(s.getInputStream());
            oos.writeObject(MysUname);
            oos.flush();
            Object o;
            try {
                o = ois.readObject();
                System.out.println("Object Read");
                if(o instanceof Rectangle)
                {
                  dv.setrect((Rectangle)o); //set dimensions as per the reciever resolution
                }
            } catch (IOException ex) {
                //frmMain.logger.log(Level.SEVERE, null, ex);
                ex.printStackTrace();
            } catch (ClassNotFoundException ex) {
                //frmMain.logger.log(Level.SEVERE, null, ex);
                ex.printStackTrace();
            }
            
            while (run) {
                try {
                    Thread.sleep(800);
                } catch (InterruptedException ex) {
                    //frmMain.logger.log(Level.SEVERE, null, ex);
                    ex.printStackTrace();
                }
                        int l=ois.readInt();
                        byte[] buf=new byte[l];
                        ois.readFully(buf);
                        bi=ImageUtility.read(buf);
                        //bi = (ImageIcon) o;
                        try {

                            dv.UpdateView(bi);  //update the image on the viwer
                        } catch (Exception ex) {
                            //frmMain.logger.log(Level.SEVERE, null, ex);
                        }
                    
                
            }
        } catch (IOException ex) {
            //frmMain.logger.log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
    }
    
}
