/*
University of Greenwich
School of Computing & Mathematical Sciences
Final Year Project
Author: Chinedu Gabriel Asinugo.
Student ID: 000433816
Class of 2013
 */
package finalproject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author Chinedu Gabriel Asinugo
 */

class Command implements Serializable
{
    String cmd;
    String data;
    SocketAddress sa;
    /**
    *Initializing Message control information
    */
}
class Message implements Serializable
{
    SocketAddress from;
    SocketAddress to;
    String sender;
    String msg;
    /**
    *Initializing Sender and receiver sockets
    */
}
class MyFile implements Serializable
{
    byte[] content;
    String name;
    SocketAddress sa;
     /**
    *Initializing File transfer variables
    */
}

public class Client implements Runnable{
    ArrayList<SocketAddress> vsa;
    DefaultListModel dlm;
    JFrame jf;  //is set the ClientGUI main frame
    LDClient lcl; 
    DesktopViewer dv; //JFrame for Screen Share desktop view
    
    boolean run=false;
    Socket s;
    ObjectOutputStream oos;
    ObjectInputStream ois;
    Object ob;
    String uname;
    //Input and out put streams, sockey and username strings
    
    /*
     * Method below, will stop the running client, when the need be
     * It can be called from any part of the code, even from another class
     */
    public void stop() {
        run=false;
        try {
            s.close();//close socket
        } catch (IOException ex) {
            //Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Manage GUI as disconnected
        ClientGUI.CButton.setEnabled(true);
        ClientGUI.DButton.setEnabled(false);
        ClientGUI.SendButton.setEnabled(false);
        ClientGUI.SendTextArea.setEnabled(false);
        ClientGUI.ChatButton.setEnabled(false);
        ClientGUI.IpField.setEnabled(true);
        ClientGUI.NField.setEnabled(true);
        ClientGUI.PField.setEnabled(true);
        ClientGUI.BuzzButton.setEnabled(false);
        ClientGUI.SendFileButton.setEnabled(false);
        ClientGUI.ShareScreenButton.setEnabled(false);
        ClientGUI.TextColourButton.setEnabled(false);
        ClientGUI.MorseCodeButton.setEnabled(false);
        ClientGUI.statusLabel.setText("Disconnected");
    }
    
    
    /*
     * start a new thread that deals with receiving data from
     * the server
     */
    public void start(String ip,int port,String uname) throws IOException
    {
        SocketAddress sa=new InetSocketAddress(ip, port);
        s=new Socket();
        s.connect(sa, 2000);
        oos=new ObjectOutputStream(s.getOutputStream());
        ois=new ObjectInputStream(s.getInputStream());
        vsa=new ArrayList<SocketAddress>();
        dlm=new DefaultListModel();
        this.uname=uname;
        new Thread(this).start();
    }
    
    /*
     * add user to the client
     * user list
     */
    public void addUser(SocketAddress sa,String name)
    {
        dlm.addElement(name);
        vsa.add(sa);
        ClientGUI.lstClients.setModel(dlm);
    }
    
    /*
     * remove a user from list of available
     * client as this one is goin offline
     */
    public void removeUser(SocketAddress sa,String name)
    {
        int r=vsa.indexOf(sa);
        if(r>=0)
        {
            vsa.remove(r);
            dlm.remove(r);
            ClientGUI.lstClients.setModel(dlm);
            ClientGUI.lstClients.invalidate();
        }
        System.out.println("asked to remove "+name + " @ " +sa.toString());
    }
    
    /*
     * report that this user is in chat with some user
     */
    public void hideUser(SocketAddress sa,String name)
    {
        int r=vsa.indexOf(sa);
        if(r>=0)
        {
            vsa.remove(r);
            dlm.remove(r);
            ClientGUI.lstClients.setModel(dlm);
            ClientGUI.lstClients.invalidate();
        }
        System.out.println("asked to remove "+name + " @ " +sa.toString());
    }
    
    
    /*
     * send method very similar to the server
     * actually send the object o
     */
    public void send(Object ob)
    {
        try {
            oos.writeObject(ob);
            oos.flush();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            stop();
        }
    }
    
    @Override
    public void run() {
        run=true;
        try
        {
            Class.forName("finalproject.Command");
            Class.forName("finalproject.Message");
            Class.forName("finalproject.MyFile");
            while(run)
            {
                try {
                    
                    ois.read();
                    ob=ois.readObject();
                    if(ob instanceof Command)
                    {
                        Command c=(Command)ob;
                        if(c.cmd.equals("WHO")) //tell the server who I am
                        {
                            Command t=new Command();
                            t=new Command();
                            t.cmd="RWHO";   //reply to who = RWHO
                            t.data=uname;
                            send(t);
                            t=new Command();
                            t.cmd="USRLIST";    //ask who all are online
                            send(t);
                        }
                        else if(c.cmd.equals("AUSR"))   //add user
                        {
                            addUser(c.sa, c.data);
                        }
                        else if(c.cmd.equals("RUSR"))   //remove user
                        {
                            if(c.sa.equals(ClientGUI.chatwith)) //if i am chatting with user to be removed
                            {
                                chatEnded(ClientGUI.chatWithLabel.getText());
                            }
                            removeUser(c.sa, c.data);
                        }
                        else if(c.cmd.equals("HIDEUSR"))    //hide this user as he is in chat
                        {
                            hideUser(c.sa,c.data);
                        }
                        else if(c.cmd.equals("BUZZ"))   //buzzzzzzzzzzzzz
                        {
                            ClientGUI.buzz(c.data);
                            
                        }
                        else if(c.cmd.equals("CHAT?"))  //ask if you want to chat with given person
                        {
                             int reply = JOptionPane.showConfirmDialog(null, 
                                     "Do you want to start chat with " + c.data, "Start New Chat", JOptionPane.YES_NO_OPTION);
                             Message m=new Message();
                             m.from=c.sa;
                             m.sender=c.data;
                             m.msg=" offered to chat ";
                             //ClientGUI.msgRecieved(m);
                             ClientGUI.statusmsgRecieved(m);
                             if(reply==JOptionPane.YES_OPTION)  //if you do
                             {
                                 Command t=new Command();
                                 t.cmd="YESCHAT";   //tell server to initiate chat
                                 t.sa=c.sa;
                                 send(t);
                                 
                                 if(ClientGUI.chatwith!=null)   //if already in chat then tell the other person their chat ended
                                 {
                                     t=new Command();
                                     t.cmd="CHATDONE";  //chat ended
                                     System.out.println("CHAT DONE WITH " + ClientGUI.chatWithLabel.getText());
                                     t.sa=ClientGUI.chatwith;
                                     send(t);
                                 }
                                 startChat(c.sa,m.sender);  //manage GUI to say chat started
                             }
                             else
                             {
                                 Command t=new Command();
                                 t.cmd="NOCHAT";    //declined offer to chat
                                 t.sa=c.sa;
                                 send(t);
                             }
                        }
                        else if(c.cmd.equals("YESCHAT"))    //the other client accepted chat invite
                        {
                             if(ClientGUI.chatwith!=null)
                             {
                                 Command t;
                                 t=new Command();
                                 t.cmd="CHATDONE";  //if already in chat then tell the other person their chat ended
                                 System.out.println("CHAT DONE WITH " + ClientGUI.chatWithLabel.getText());
                                 t.sa=ClientGUI.chatwith;
                                 send(t);
                             }
                            startChat(c.sa, c.data);
                        }
                        else if(c.cmd.equals("NOCHAT")) //other person declined to chat
                        {
                            Message m=new Message();
                            m.from=c.sa;
                            m.msg=" declined offer for chat ";
                            m.sender=c.data;
                            //ClientGUI.msgRecieved(m);
                            ClientGUI.statusmsgRecieved(m);
                        }
                        else if(c.cmd.equals("CHATDONE"))   //chat ended
                        {           
                            Message m=new Message();
                            m.from=c.sa;
                            m.msg=" has ended chat";
                            m.sender=ClientGUI.chatWithLabel.getText();
                            //ClientGUI.msgRecieved(m);
                            ClientGUI.statusmsgRecieved(m);
                            System.out.println("CHAT ENDED..." + ClientGUI.chatWithLabel.getText());
                            chatEnded(c.data);
                            
                        }
                        else if(c.cmd.equals("BUZZ"))
                        {
                            ClientGUI.buzz(c.data);
                            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
                            Date d=new Date();
                            ClientGUI.statusLabel.setText("Last Message Recieved From " 
                                + ClientGUI.chatWithLabel.getText() + " at "+  sdf.format(d));
                        }
                        else if(c.cmd.equals("SENDFILE"))   //asked to recieve file
                        {
                            int reply = JOptionPane.showConfirmDialog(null, 
                                     "Do you want to Recieve File " + c.data, "Transfer File", JOptionPane.YES_NO_OPTION);
                             Message m=new Message();
                             m.from=c.sa;
                             m.sender=ClientGUI.chatWithLabel.getText();
                             m.msg=" offered to send file " + c.data;
                             //ClientGUI.msgRecieved(m);
                             ClientGUI.statusmsgRecieved(m);
                             if(reply==JOptionPane.YES_OPTION)
                             {
                                 Command t=new Command();
                                 t.cmd="YESFILE";   //tell the server to ask the other to start transfer
                                 t.sa=c.sa;
                                 send(t);
                                 
                                m=new Message();
                                m.from=c.sa;
                                m.sender="";
                                m.msg=" File Transfer started";
                                //ClientGUI.msgRecieved(m);
                                ClientGUI.statusmsgRecieved(m);
                             }
                        }
                        else if(c.cmd.equals("YESFILE"))    //the other person accepted the transfer so now send file
                        {
                            MyFile mf=new MyFile();
                            System.out.println("Sending file form client : " + ClientGUI.sf.exists());
                            File f=ClientGUI.sf;
                            try{
                            byte[] buf=new byte[(int)f.length()];
                            BufferedInputStream bis=new BufferedInputStream(new FileInputStream(f));
                            bis.read(buf);
                            mf.content=buf;
                            bis.close();
                            mf.name=ClientGUI.sf.getName();
                            mf.sa=ClientGUI.chatwith;
                            Message m=new Message();
                            m.from=c.sa;
                            m.sender="";
                            m.msg=" File Transfer started";
                            //ClientGUI.msgRecieved(m);
                            ClientGUI.statusmsgRecieved(m);
                            send(mf);
                            }catch (Exception e)
                            {
                                Message m=new Message();
                                m.from=c.sa;
                                m.sender="";
                                m.msg=" File Transfer error";
                                //ClientGUI.msgRecieved(m);
                                ClientGUI.statusmsgRecieved(m);
                            }
                        }
                        else if(c.cmd.equals("FILESENT"))   //recieved ack that file was sent succesfully
                        {
                            Message ms=new Message();
                            ms.sender="";
                            ms.msg=" File Transfered successfully";
                            //ClientGUI.msgRecieved(ms);
                            ClientGUI.statusmsgRecieved(ms);
                        }
                        else if(c.cmd.equals("INVITESCREEN"))   //the other client offered to share his screen
                        {
                            int reply = JOptionPane.showConfirmDialog(null, 
                                     "Do you want to view screen of " + c.data, "View Screen", JOptionPane.YES_NO_OPTION);
                             SocketAddress sscreena=c.sa;
                             Message m=new Message();
                             m.sender=ClientGUI.chatWithLabel.getText();
                             m.msg=" offered to share screen";
                             //ClientGUI.msgRecieved(m);
                             ClientGUI.statusmsgRecieved(m);
                             if(reply==JOptionPane.YES_OPTION)
                             {
                                 Command t=new Command();
                                 t.cmd="YESSCREEN";
                                 t.sa=ClientGUI.chatwith;
                                 send(t);
                                 System.out.println("CHK1");
                                 if(lcl!=null && lcl.run)
                                     lcl.stop();
                                 System.out.println("CHK2 " + sscreena.toString());
                                 
                                 lcl=new LDClient(sscreena);    //start the client with the parameters to connect to screen
                                                                //share server
                                 if(dv!=null)
                                     dv.dispose();
                                 System.out.println("CHK3");
                                 dv=new DesktopViewer(jf);
                                 dv.setVisible(true);   //make viewer visible
                                 dv.setTitle("Desktop of " + ClientGUI.chatWithLabel.getText());
                                 dv.lcl=lcl;
                                 lcl.MysUname=t.data;
                                 System.out.println("CHK4");
                                 lcl.xstart(dv);    //start our viewer
                                 
                                m=new Message();
                                m.from=c.sa;
                                m.sender="";
                                m.msg=" Screen share started";
                                //ClientGUI.msgRecieved(m);
                                ClientGUI.statusmsgRecieved(m);
                             }
                             else
                             {
                                 Command t=new Command();
                                 t.cmd="NOSCREEN";  //decline the offer to view screen
                                 t.sa=ClientGUI.chatwith;
                                 send(t);
                                 
                                 
                                 
                                m=new Message();
                                m.from=c.sa;
                                m.sender="";
                                m.msg=" Screen share declined";
                                //ClientGUI.msgRecieved(m);
                                ClientGUI.statusmsgRecieved(m);
                             }
                        }
                        else if(c.cmd.equals("YESSCREEN"))  //received ack that successfully 
                        {
                            Message m=new Message();
                            m.sender="";
                            m.msg=" Screen share started";
                            //ClientGUI.msgRecieved(m);
                            ClientGUI.statusmsgRecieved(m);
                        }
                         else if(c.cmd.equals("NOSCREEN"))  //the other person declined screen share
                         {
                             System.out.println("REC: NOSCREEN");
                             Message m=new Message();
                            m.sender="";
                            m.msg=" Screen share declined";
                            //ClientGUI.msgRecieved(m);
                            ClientGUI.statusmsgRecieved(m);
                            ClientGUI.lds.stop();   //stop the screen server
                         }
                         else if(c.cmd.equals("ENDSCREEN")) //end screen server
                         {
                             System.out.println("REC: ENDSCREEN");
                             Message m=new Message();
                             m.sender="";
                             m.msg=" Screen share ended";
                             ClientGUI.statusmsgRecieved(m);
                             ClientGUI.lds.stop();
                         }
                         else if(c.cmd.equals("CLOSESCREEN"))   //screen share closed by the other side
                         {
                                System.out.println("REC: CLOSESCREEN");
                                Message m=new Message();
                                m.sender="";
                                m.msg=" Screen share ended";
                                ClientGUI.statusmsgRecieved(m);
                                lcl.stop(); //stop client
                                dv.dispose();//close the viewer
                         }
                        else if(c.cmd.equals("SERVERSHUT")) //server shutting down and thus we must tell user and close
                        {
                                JOptionPane.showMessageDialog(jf, "Server shutting down !!");
                                jf.dispose();   //close
                        }
                    }
                    else if(ob instanceof Message)
                    {
                        Message m=(Message)ob;
                        ClientGUI.msgRecieved(m);
                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
                        Date d=new Date();
                        ClientGUI.statusLabel.setText("Last Message Recieved From " 
                                + m.sender + " at "+  sdf.format(d));
                        //jf.setTitle(uname +": Message Recieved" +  sdf.format(d));
                    }
                    else if(ob instanceof MyFile)
                    {
                        MyFile m=(MyFile)ob;
                        File f=new File("rec" + ClientGUI.chatWithLabel.getText() + m.name);
                        
                        BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream(f));
                        bos.write(m.content);
                        bos.flush();
                        bos.close();
                        Message ms=new Message();
                        ms.sender="";
                        ms.msg=" File Transfered successfully";
                        //ClientGUI.msgRecieved(ms);
                        ClientGUI.statusmsgRecieved(ms);
                        Command t=new Command();
                        t.cmd="FILESENT";
                        t.sa=m.sa;
                        send(t);
                        
                    }
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                } catch(EOFException ex)
                {     try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex1) {
                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch(SocketException ex)
        {
            stop();
        }
        catch(IOException ex)
        {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        stop();
    }

    /*
     * update that the chat
     * ended with other person
     * and hence update display
     */
    public void chatEnded(String data)
    {
        ClientGUI.chatwith=null;
        ClientGUI.chatWithLabel.setText(null);
        ClientGUI.SendButton.setEnabled(false);
        ClientGUI.BuzzButton.setEnabled(false);
        ClientGUI.SendTextArea.setEnabled(false);
        ClientGUI.SendFileButton.setEnabled(false);
        ClientGUI.ShareScreenButton.setEnabled(false);
        ClientGUI.ShareScreenButton.setEnabled(false);
        ClientGUI.TextColourButton.setEnabled(false);
        ClientGUI.MorseCodeButton.setEnabled(false);
        Message m=new Message();
        m.msg=" ended chat ";
        m.sender=data;
        ClientGUI.msgRecieved(m);
        dlm.clear();
        vsa.clear();
        Command t;
        t=new Command();
        t.cmd="USRLIST";    //ask for rest of available users
        send(t);
    }
    
    /*
     * ask the selected user to chat with
     * other if he is interested
     */
    void askToChat(SocketAddress get) {
        Command t=new Command();
        t.cmd="CHAT?";
        t.data=uname;
        t.sa=get;
        send(t);
        Message m=new Message();
        m.sender="";
        m.msg="Chat request sent";
        //ClientGUI.msgRecieved(m);
        ClientGUI.statusmsgRecieved(m);
    }

    
    /*
     * update the display on UI
     * and update chat with textbox
     */
    private void startChat(SocketAddress sa,String with) {
        ClientGUI.chatwith=sa;
        ClientGUI.chatWithLabel.setText(with);
        ClientGUI.chatWithLabel.invalidate();
        ClientGUI.SendButton.setEnabled(true);
        ClientGUI.BuzzButton.setEnabled(true);
        ClientGUI.SendTextArea.setEnabled(true);
        ClientGUI.SendFileButton.setEnabled(true);
        ClientGUI.ShareScreenButton.setEnabled(true);
        ClientGUI.TextColourButton.setEnabled(true);
        ClientGUI.MorseCodeButton.setEnabled(true);
        Message m=new Message();
        m.sender="";
        m.msg="chat started with " +with;
        //ClientGUI.msgRecieved(m);
        ClientGUI.statusmsgRecieved(m);
    }
    
}
