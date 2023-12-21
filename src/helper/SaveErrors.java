package helper;

import UI.MainFrame;
import config.SystemSettings;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author harsha
 */
public class SaveErrors {
    
    public static void SaveErrorLog(Exception e) {
        try {
            System.out.println(":::::: EXCEPTION ::::: " + e);
            //Date d = new Date();
            //SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
            String fName = "Exceptions_" + new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            File f1 = new File(SystemSettings.ProgramDataExceptionPath);
            if(!f1.exists()){
                f1.mkdir();
            }
            FileWriter fstream = new FileWriter(SystemSettings.ProgramDataExceptionPath + "\\" + fName + ".txt", true);
            BufferedWriter out = new BufferedWriter(fstream);
            PrintWriter pWriter = new PrintWriter(out, true);
            e.printStackTrace(pWriter);
            
            fstream.write("====================================================================================\n"); //added 2020-04-21 (sam)
           // fstream.write("<< Terminal-NAME: " + SystemSettings.Terminal + "   | TIME: " + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "   | USER: " + MainFrame.CurrentSysUser.getUserID()+ " >>\n");
            fstream.write("====================================================================================\n\n");
            
            fstream.close();
            out.close();
            pWriter.close();
            
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("SaveErrorLog (class) ____________________________________ EXCEPTION : " + e.getMessage());
        }
        
    }
}
