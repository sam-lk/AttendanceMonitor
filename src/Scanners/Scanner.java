/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Scanners;

/**
 *
 * @author Harsha
 */
public class Scanner {
    private String ScannerID="";
    private String Name="";
    private String IP="";
    private String Port="";
    private String Note="";

    public String getScannerID() {
        return ScannerID;
    }

    public void setScannerID(String ScannerID) {
        this.ScannerID = ScannerID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public String getPort() {
        return Port;
    }

    public void setPort(String Port) {
        this.Port = Port;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String Note) {
        this.Note = Note;
    }
    
    
    
}
