/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package UI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author saman
 */
public class TestFile extends javax.swing.JFrame {

    /**
     * Creates new form TestFile
     */
    public TestFile() {
        initComponents();

        try {
            ReadFile();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void ReadFile() throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d", Locale.ENGLISH);
        //DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.ENGLISH);
        SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
        int logcount = 0;
        String ProgramDataPath = System.getenv("ProgramData") + "\\Interithm\\Attendance Monitor\\local\\";
        File file = new File(ProgramDataPath + "AT_LOG.txt"); //ATTENDANCE TEXT FILE

        List<String> lines = Collections.emptyList();

        lines = Files.readAllLines(file.toPath());
        //Iterator<String> itr = lines.iterator();

        System.out.println("size " + lines.size());
        for(String st : lines){
            try {
                System.out.println(">>>");
                System.out.println(st);

                String[] splitSt = st.trim().split(";");
                if (splitSt.length >= 3) {
                    //String [] splitSt =sc.nextLine().trim().split(",");                           
                    String[] splitSt2 = splitSt[3].split(" ");

                    //System.out.println("LineNo: " + splitSt[0]+"   UserID: " + splitSt[1]+"   InOut Code: " + splitSt[2]+"   Date: " + splitSt2[0] +"   Time: " + splitSt2[1]);                              
                    //CONVERT DATE 
                    LocalDate date = LocalDate.parse(splitSt2[0], formatter);
                    //CONVERT TIME
                    Date TIME = parser.parse(splitSt2[1]);

                    System.out.println("--------------------------------");
                    System.out.println("ScannerID : 1");
                    System.out.println("EmpID : " + splitSt[1].trim());
                    System.out.println("Date Time : " + date + "" + parser.format(TIME));

                    logcount++; //COUNT LOOP
                }
                System.out.println(">>>");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        while (itr.hasNext()) {
//            try {
//                System.out.println(itr.next());
//
//                String[] splitSt = itr.next().trim().split(";");
//                if (splitSt.length >= 3) {
//                    //String [] splitSt =sc.nextLine().trim().split(",");                           
//                    String[] splitSt2 = splitSt[3].split(" ");
//
//                    //System.out.println("LineNo: " + splitSt[0]+"   UserID: " + splitSt[1]+"   InOut Code: " + splitSt[2]+"   Date: " + splitSt2[0] +"   Time: " + splitSt2[1]);                              
//                    //CONVERT DATE 
//                    LocalDate date = LocalDate.parse(splitSt2[0], formatter);
//                    //CONVERT TIME
//                    Date TIME = parser.parse(splitSt2[1]);
//
//                    System.out.println("--------------------------------");
//                    System.out.println("ScannerID : 1");
//                    System.out.println("EmpID : " + splitSt[1].trim());
//                    System.out.println("Date Time : " + date + "" + parser.format(TIME));
//
//                    logcount++; //COUNT LOOP
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
        
//        System.out.println("EDDDDD");
//        if (file.exists()) {
//            try {
//                java.util.Scanner sc = new java.util.Scanner(file);
//                System.out.println("START");
//                while (sc.hasNextLine()) { //SCANE FILE
//                    if (sc.nextLine() != null && !sc.nextLine().trim().isEmpty()) {
//                        String[] splitSt = sc.nextLine().trim().split(";");
//                        if (splitSt.length >= 3) {
//                            //String [] splitSt =sc.nextLine().trim().split(",");                           
//                            String[] splitSt2 = splitSt[3].split(" ");
//
//                            //System.out.println("LineNo: " + splitSt[0]+"   UserID: " + splitSt[1]+"   InOut Code: " + splitSt[2]+"   Date: " + splitSt2[0] +"   Time: " + splitSt2[1]);                              
//                            //CONVERT DATE 
//                            LocalDate date = LocalDate.parse(splitSt2[0], formatter);
//                            //CONVERT TIME
//                            Date TIME = parser.parse(splitSt2[1]);
//
//                            System.out.println("--------------------------------");
//                            System.out.println("ScannerID : 1");
//                            System.out.println("EmpID : " + splitSt[1].trim());
//                            System.out.println("Date Time : " + date + "" + parser.format(TIME));
//
//                            logcount++; //COUNT LOOP
//                        }
//                    } else {
//                        System.out.println("NULL OR EMPTY ROW >>>>");
//                    }
//                }
//                System.out.println("END");
//
////                // delete the contents of the file
////                PrintWriter writer = new PrintWriter(file);
////                writer.print("");
////                writer.close();
//            } catch (FileNotFoundException ex) {
//                Logger.getLogger(TestFile.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (ParseException ex) {
//                Logger.getLogger(TestFile.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TestFile.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TestFile.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TestFile.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TestFile.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TestFile().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
