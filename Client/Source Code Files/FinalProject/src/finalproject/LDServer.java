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

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;


/**
 *
 * @author Chinedu Gabriel Asinugo
 */
public class LDServer implements Runnable{
    ServerSocket s;
    boolean run=false;
    static Hashtable<String,LDServerWorker> htable=new Hashtable<String, LDServerWorker>();
    
    public boolean isRunning()
    {
        return run;
    }
    
    public void xstart()
    {
        run=true;
        new Thread(this).start();
    }
    
    public void stop()
    {
        run=false;
        Message m=new Message();
        m.sender="Server";
        m.msg="Broadcast server shutdown";
        ClientGUI.msgRecieved(m);
        ClientGUI.ShareScreenButton.setSelected(false);
        
        Command t=new Command();
        t.cmd="BSERVERSTOP";    //tell the server screen broadcast server
                                // ended
        t.sa=s.getLocalSocketAddress();
        ClientGUI.c.send(t);
        
        try {
            s.close();
        } catch (IOException ex) {
            //ex.printStackTrace();
            ex.printStackTrace();
        }
    }
    
    /*
     * start our screen broadcast screen server
     * at a given @port
     */
    public LDServer(int port) {
        try {
            s = new ServerSocket(port);
            s.setSoTimeout(0);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
    }
    
    

    public void run() {
        while(run)
        {
            try {
                try {
                    Thread.sleep(800);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                
                Socket sock = s.accept();
                LDServerWorker sw = new LDServerWorker(sock);   //start a thread per server
                sw.xstart();
            } catch (IOException ex) {
                ex.printStackTrace();
                stop();
            }
        }
    }
}

class LDServerWorker implements Runnable
{
    public Socket s;
    public boolean run=false;
    public ObjectInputStream ois;
    public ObjectOutputStream oos;
    public String Uname;
    public LD l=new LD();
    
    public void xstart()
    {
        run=true;
        new Thread(this).start();
    }
    
    public void stop()
    {
        try {
            run = false;
            //LDServer.htable.remove(Uname);
            s.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public LDServerWorker(Socket so) {
        try {
            s = so;
            s.setSoTimeout(0);
            s.setKeepAlive(true);
            ois = new ObjectInputStream(s.getInputStream());
            oos = new ObjectOutputStream(s.getOutputStream());
           // l=new LD();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    
    
    public void run() {
        System.out.println("Server Worker Run");
        try {
            
            try {
                ois.read();
                String str = (String) ois.readObject();
                //LDServer.htable.put(str, this);
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
            oos.writeObject(l.getScreenRect());
            System.out.println("Written Screen Rect");
            oos.flush();
            while (run) {

                try {
                    Thread.sleep(800);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                byte[] buf = l.CaptureScreenByteArray();    //get current screen as byte array
                //ImageIcon ic=new ImageIcon(buf);
                if (buf != null) {
                    try {
                        //System.out.println("Writting IC");
                        oos.writeInt(buf.length);
                        oos.flush();
                        oos.write(buf);
                        oos.flush();
                        //System.out.println("Written IC");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        break;
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
    

