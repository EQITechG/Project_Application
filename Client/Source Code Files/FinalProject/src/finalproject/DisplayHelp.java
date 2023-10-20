/*
 * This code was copied from Engr. Jomin N. Yu
 * Moderated by me (Chinedu Gabriel Asinugo, in 
 */


package finalproject;

import java.io.*;

/**
 * <code>Display Help</code>
 *This code was copied from the below Author and moderated by me
 * @author Engr. Jomin N. Yu
 * @version 0.1.0
 * @Copyright JoEs Automated M.I.S.
 */

public class DisplayHelp  {

    /**
     * Show help via default browser
     */
    public void openHelp()
    {
        // run start command for the default browser
        try
        {
            Runtime run = Runtime.getRuntime();

            String command = "cmd";
            String osname = System.getProperty("os.name", "Windows 2000");
            if (osname.endsWith("98") || osname.endsWith("ME") || osname.endsWith("Home")) {
                command = "command.com";
            }

            Process cmd;
            if (command.equals("cmd")) {
                cmd = run.exec(command + " /C start /d\"" +
                    System.getProperty("user.dir") + "\\docs\" index.html");
            }
            else {
                cmd = run.exec(command + " /C start \"" +
                   
                System.getProperty("user.dir") + "\\docs\" index.html");
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(cmd.getInputStream()));
            String line;
            while ((line = in.readLine()) != null)
            {
                System.out.println(line);
            }
            in.close();
            cmd.destroy();
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


} /** End class DisplayHelp */
