
/**
 *
University of Greenwich
School of Computing & Mathematical Sciences
Final Year Project
Author: Chinedu Gabriel Asinugo.
Student ID: 000433816
Class of 2013
 */
package finalproject;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;

/**
 *
 * @author Chinedu Gabriel Asinugo
 */
class Command implements Serializable
    {//the type of command
        String cmd; 
        String data;
        SocketAddress sa;
    }
    class Message implements Serializable
    {//Message parameters
        SocketAddress from;
        SocketAddress to;
        String sender;
        String msg;
    }

class MyFile implements Serializable
{//File Share parameters
    byte[] content;
    String name;
    SocketAddress sa;
}

public class Server implements Runnable{
    //Hashmap for user list to be associated with user name
    ServerSocket s;
    static DefaultListModel dlm=new DefaultListModel();
    static HashMap<SocketAddress,ServerWorker> master=new HashMap<SocketAddress,ServerWorker>();
    static HashMap<SocketAddress,ServerWorker> availabletable=new HashMap<SocketAddress,ServerWorker>();
    static HashMap<SocketAddress,SocketAddress> brcstServer=new HashMap<SocketAddress,SocketAddress>();
    boolean run=false;
    
    
    
    @Override
    public void run() {
        while(run)
        {
            try {
                ServerWorker sw=new ServerWorker(s.accept()); //give each connection each its own worker thread
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        stop();
    }

    void start(int port, String ia) throws IOException {
       SocketAddress sa=new InetSocketAddress(ia, port);
       try{
           //clear user list
        dlm.clear();
        ServerGUI.lstUsers.setModel(dlm);
        ServerGUI.lstUsers.invalidate();
        s.close();
       }catch (Exception ex)
       {
           
       }
       s=new ServerSocket();
       s.bind(sa);
       run=true;
       new Thread(this).start();
    }
    
    void stop()
    {
        try {
            s.close();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        run=false;
    }

    public void shutdown() 
    {
        //tell every client that server is shutting down
        Set<Map.Entry<SocketAddress,ServerWorker>> s=master.entrySet();
        Iterator<Map.Entry<SocketAddress,ServerWorker>> it=s.iterator();
        while(it.hasNext())
        {
            Map.Entry<SocketAddress,ServerWorker> e=it.next();
            ServerWorker sw=e.getValue();
            Command t=new Command();
            t.cmd="SERVERSHUT";
            sw.send(t);
        }
    }
    //actual thread per client
    class ServerWorker implements Runnable 
    {
        Socket s;
        public String uname;
        ObjectInputStream ois;
        ObjectOutputStream oos;
        Object ob;
        boolean run;
        
        private ServerWorker(Socket s) throws IOException {
           
                this.s=s;
                oos=new ObjectOutputStream(s.getOutputStream());
                ois=new ObjectInputStream(s.getInputStream());
                run=true;
                new Thread(this).start();
        }

        @Override
        public void run() {
            try {
                Class.forName("finalproject.Command");
                Class.forName("finalproject.Message");
                Class.forName("finalproject.MyFile");
                availabletable.put(s.getRemoteSocketAddress(), this);
                master.put(s.getRemoteSocketAddress(), this);
                Command c=new Command();
                c.cmd="WHO";    //ask for username of the client
                send(c);
                while(run)
                {
                    ois.read();
                    ob=ois.readObject();

                     if(ob instanceof Command)
                     {
                        Command t=(Command)ob;
                        System.out.println("XREC: " + t.cmd);
                        
                        if(t.cmd.equals("RWHO"))
                        {
                            this.uname=t.data;
                            addUser();

                        }
                        else if(t.cmd.equals("USRLIST"))    //provide available user list
                        {
                            System.out.println("USRLIST From " + uname + " " + s.getRemoteSocketAddress().toString());
                            Set<Map.Entry<SocketAddress,ServerWorker>> s=availabletable.entrySet();
                            Iterator<Map.Entry<SocketAddress,ServerWorker>> it=s.iterator();
                            while(it.hasNext())
                            {
                                Map.Entry<SocketAddress,ServerWorker> e=it.next();
                                ServerWorker sw=e.getValue();
                                if(sw!=this)
                                {
                                    t=new Command();
                                    t.cmd="AUSR";   //add user is AUSR
                                    t.data=sw.uname;
                                    t.sa=e.getKey();
                                    send(t);
                               }
                            }
                        }
                        else if(t.cmd.equals("CHAT?"))  //ask if user wants to chat
                        {
                             ServerWorker sw=master.get(t.sa);
                             t.sa=s.getRemoteSocketAddress();
                             t.data=uname;
                             sw.send(t);
                        }
                        else if(t.cmd.equals("NOCHAT")) //other user denied the chat request
                        {
                            ServerWorker sw=master.get(t.sa);
                            t.sa=s.getRemoteSocketAddress();
                            t.data=uname;
                            sw.send(t);
                        }
                        else if(t.cmd.equals("YESCHAT"))    //other user agreed to chat
                        {
                            
                            //remove from available for all
                            removeUser(true);
                            ServerWorker sw=master.get(t.sa);
                            t.data=uname;
                            t.sa=s.getRemoteSocketAddress();
                            sw.send(t);
                            availabletable.remove(t.sa);
                            availabletable.remove(s.getRemoteSocketAddress());
                            sw.removeUser(true); 
                            ServerGUI.log(sw.uname + " started chat with " + uname);
                        }
                        else if(t.cmd.equals("CHATDONE"))   //notify user that the chat he was invlolved in has ended
                        {
                           ServerWorker sw=master.get(t.sa);
                            
                            
                            Set<Map.Entry<SocketAddress,ServerWorker>> s=master.entrySet();
                            Iterator<Map.Entry<SocketAddress,ServerWorker>> it=s.iterator();
                            while(it.hasNext())
                            {
                                Map.Entry<SocketAddress,ServerWorker> e=it.next();
                                ServerWorker swt=e.getValue();
                                Command t2=new Command();
                                if(swt!=sw)
                                {
                                    t2=new Command();
                                    t2.cmd="AUSR";
                                    t2.data=sw.uname;
                                    t2.sa=t.sa;
                                    swt.send(t2);
                               }
                            }
                            availabletable.put(t.sa, sw);
                            t.sa=this.s.getRemoteSocketAddress();
                            t.data=uname;
                            sw.send(t);
                            ServerGUI.log("Chat between " + uname + " and " + sw.uname + " ended");
                        }
                        else if(t.cmd.equals("BUZZ"))
                        {
                            ServerWorker sw=master.get(t.sa);
                            t.sa=s.getRemoteSocketAddress();
                            t.data=uname;
                            sw.send(t);
                        }
                        else if(t.cmd.equals("SENDFILE"))   //ask if user wants to recieve file
                        {
                             ServerWorker sw=master.get(t.sa);
                             t.sa=s.getRemoteSocketAddress();
                             sw.send(t);
                        }
                        else if(t.cmd.equals("YESFILE"))    //acknowledge file and start sending file
                        {
                            ServerWorker sw=master.get(t.sa);
                            t.sa=s.getRemoteSocketAddress();
                            sw.send(t);
                            ServerGUI.log(sw.uname + " started file transfer to " + uname);
                        }
                        else if(t.cmd.equals("FILESENT"))   //file transfer completed
                        {
                            ServerWorker sw=master.get(t.sa);
                            t.sa=s.getRemoteSocketAddress();
                            sw.send(t);
                            ServerGUI.log("file transfer between " + uname + " and " + sw.uname +" completed");
                        }
                        else if(t.cmd.equals("BSERVER"))    //screen broadcast server started on client
                        {
                             InetSocketAddress isad=(InetSocketAddress)s.getRemoteSocketAddress();
                             SocketAddress sad=new InetSocketAddress(isad.getHostString(), 8005);
                             brcstServer.put(s.getRemoteSocketAddress(), sad);
                             System.out.println(s.getRemoteSocketAddress().toString() + " => " + sad.toString());
                        }
                        else if(t.cmd.equals("BSERVERSTOP"))    //screen broadcast server stopped on client
                        {
                            brcstServer.remove(s.getRemoteSocketAddress());
                        }
                        else if(t.cmd.equals("INVITESCREEN"))   //invite to view screen
                        {
                             ServerWorker sw=master.get(t.sa);
                             t.data=uname;
                             t.sa=brcstServer.get(s.getRemoteSocketAddress());
                             System.out.println("SADD2: " + t.sa.toString());
                             sw.send(t);
                        }
                        else if(t.cmd.equals("YESSCREEN"))  //acknowledge screen share ask the other person to start
                        {
                            ServerWorker sw=master.get(t.sa);
                            t.sa=s.getRemoteSocketAddress();
                            System.out.println("SADD: " + t.sa.toString());
                            sw.send(t);
                            ServerGUI.log(sw.uname + " started sharing screen with " + uname);
                        }
                        else if(t.cmd.equals("NOSCREEN"))   //decline screen share request
                        {
                            ServerWorker sw=master.get(t.sa);
                            t.sa=s.getRemoteSocketAddress();
                            sw.send(t);
                            brcstServer.remove(sw.s.getRemoteSocketAddress());
                        }
                        else if(t.cmd.equals("ENDSCREEN"))  //screen share stopped, so notify to stop server
                        {
                            ServerWorker sw=master.get(t.sa);
                            t.sa=s.getRemoteSocketAddress();
                            sw.send(t);
                            brcstServer.remove(sw.s.getRemoteSocketAddress());
                            ServerGUI.log("Screen share between " + uname + " and " + sw.uname + " ended");
                        }
                        else if(t.cmd.equals("CLOSESCREEN"))    //the person closed the screen share window
                        {
                            ServerWorker sw=master.get(t.sa);
                            t.sa=s.getRemoteSocketAddress();
                            sw.send(t);
                            brcstServer.remove(s.getRemoteSocketAddress());
                            ServerGUI.log(uname + " stopped screen share with " + sw.uname);
                        }
                     }
                     else if(ob instanceof Message) //if a text message if sent
                     {
                         Message m=(Message)ob;
                         ServerWorker sw=master.get(m.to);
                         sw.send(m);
                         System.out.println("sending chat : " + m.msg);
                     }
                     else if(ob instanceof MyFile)  //if file is sent to other person
                     {
                         MyFile m=(MyFile)ob;
                         ServerWorker sw=master.get(m.sa);
                         m.sa=s.getRemoteSocketAddress();
                         sw.send(m);
                         System.out.println("sending file : " + m.name);
                     }
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            finally
            {
                removeUser(false);
                run=false;
                try {
                    s.close();
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        
        /*
         * stop the server
         */
        public void stop()
        {
            run=false;
            try {
                s.close();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        /*
         * send the object (msg/command/file) to the connected client
         */
        public void send(Object ob)
        {
            try {
                oos.writeObject(ob);
                oos.flush();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                stop();
            }
        }
        /*
         * add the current mentioned username to the map 
         * and inform everyone
         */
        private void addUser() {
            Server.dlm.addElement(uname);
            
            SocketAddress sa=s.getRemoteSocketAddress();
            
            ServerGUI.log( uname + " connected from " + s.getInetAddress().getHostAddress());
            ServerGUI.lstUsers.setModel(Server.dlm);
            //tell everyone we have a new user
            Command t=new Command();
            t.cmd="AUSR";
            t.data=uname;
            t.sa=s.getRemoteSocketAddress();
            Set<Map.Entry<SocketAddress,ServerWorker>> s=master.entrySet();
            Iterator<Map.Entry<SocketAddress,ServerWorker>> it=s.iterator();
            while(it.hasNext())
            {
                Map.Entry<SocketAddress,ServerWorker> e=it.next();
                ServerWorker sw=e.getValue();
                if(sw!=this)
                {
                   sw.send(t);
               }
            }
            
            //refresh display of the user list
            ServerGUI.lstUsers.invalidate();
        }
      
        /*
         * remove the user from available list.
         * we hide the user if he is in some chat and has not gone offline
         * in hiding we remove user from available map but stays in master map
         */
        public void removeUser(boolean hide)
        {
            
            availabletable.remove(s.getRemoteSocketAddress());
            if(!hide)
            {
                master.remove(s.getRemoteSocketAddress());
                Server.dlm.removeElement(uname);
                ServerGUI.lstUsers.setModel(Server.dlm);
                ServerGUI.lstUsers.invalidate();
                System.out.println("removing user");
                ServerGUI.log( uname + " disconnected from " + 
                        s.getInetAddress().getHostAddress());
            }
            //tell everyone we lost a user
            Command t=new Command();
            t.cmd="RUSR";   //remove user
            if(hide)
                t.cmd="HIDEUSR";    //hide user
            t.data=uname;
            t.sa=s.getRemoteSocketAddress();
            //ask every connected user to do the needful
            Set<Map.Entry<SocketAddress,ServerWorker>> s=master.entrySet();
            Iterator<Map.Entry<SocketAddress,ServerWorker>> it=s.iterator();
            while(it.hasNext())
            {
                Map.Entry<SocketAddress,ServerWorker> e=it.next();
                ServerWorker sw=e.getValue();
                System.out.println("asking "+t.data+" to be removed");
                sw.send(t);
            }
        }
    }
}
