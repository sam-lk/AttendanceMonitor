/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package config;

import java.sql.ResultSet;
import database.DatabaseConnection;
import helper.SaveErrors;

/**
 *
 * @since : 2020-03-15
 */
public class SystemSettings {

    public static String Version_Number = "1.0.2.0"; //https://semver.org/ & https://www.cloudbees.com/blog/best-practices-when-versioning-a-release
    public static String Build_Date = "Built on 13/Mar/2023";
    public static boolean SQLITE_DB_TYPE = true;
    public static boolean Local_Version = true;
    public static boolean DeveloperMode = false;
    public static boolean Interithm_DB = false;
    
    public static String ConfigFilePath = System.getenv("ProgramData") + "\\Interithm\\Attendance Monitor\\local\\config_.properties";
    public static String ProgramDataExceptionPath = System.getenv("ProgramData") + "\\Interithm\\Attendance Monitor\\Exceptions";
    
    public static String Language = "English";
    public static String PackageType = "Business1"; //Starter, Business, Premium, Enterprise, etc..
    public static String Pos_Type = "Computer Shop"; //Retail, Pharmacy, Resturant, Saloon, BookShop, Hardware, Textile, Computer Shop, Grocery, Clothes
    
    public static String Terminal = "";
    public static String BranchName = "Default Branch";
    public static boolean FirstRunMode = false;
    

    
    //Libraries
    //https://javalibs.com/artifact/com.formdev/flatlaf
    
    public static String CurruncyFormat_String = "Rs.";
    public static String DateFormat_String = "dd-MM-yyyy";
    public static String NumberFormat_String = "###,###,##0.00";
    public static String NumberFormatForQty_String = "###,###,##0.###";
    public static String TimeFormat_String = "HH:mm:ss";
    
    //<editor-fold defaultstate="collapsed" desc="----- READ AND SET CURRENCY TYPE & DATE-TIME FORMAT & NUMBER FORMAT -----">
    public static void LoadBasicSettings() {
        ConfigFilePath = System.getenv("ProgramData") + "\\Interithm\\Freidea POS\\local\\config_.properties";
        ProgramDataExceptionPath = System.getenv("ProgramData") + "\\Interithm\\Freidea POS\\Exceptions";
        
        //Splash.f1.ProgramDataLocalFolder = System.getenv("ProgramData") + "\\Interithm\\Freidea POS\\local";
        //Splash.f1.ProgramDataImageFolder = System.getenv("ProgramData") + "\\Interithm\\Freidea POS\\local\\Images";
                
        try {

            ResultSet res1 = DatabaseConnection.Search("Select * FROM settings");
            while (res1.next()) {
                
                PackageType = res1.getString("System_Package_Type");
                Pos_Type = res1.getString("System_Pos_Type");
                //SQLITE_DB_TYPE = res1.getString("System_DB_Mode").trim().toUpperCase().equals("SQLITE") ? true : false;
                
                CurruncyFormat_String = res1.getString("Currency");
                DateFormat_String = res1.getString("DateFormat");
                NumberFormat_String = res1.getString("NumberFormat");
                NumberFormatForQty_String = res1.getString("NumberFormatForQty");
                TimeFormat_String = res1.getString("TimeFormat");
                
                //Splash.f1.CurruncyFormat = CurruncyFormat_String;
                
                if(res1.getString("StockColumnName") != null || res1.getString("StockOrder") != null){
                 //   Splash.f1.AscOrDesc = res1.getString("StockOrder");
                  //  Splash.f1.OrderColumn = res1.getString("StockColumnName");
                }
                
                
                if(res1.getString("TableContentFontSize") != null || res1.getInt("TableContentFontSize") != 0){
                   // Splash.f1.TableContentFontSize = res1.getInt("TableContentFontSize");
                   // Splash.f1.TableHeaderFontSize = res1.getInt("TableHeaderFontSize");
                }
                
            }


        } catch (Exception e) {
            SaveErrors.SaveErrorLog(e);
        }

    }
    //</editor-fold>
    


    

}
