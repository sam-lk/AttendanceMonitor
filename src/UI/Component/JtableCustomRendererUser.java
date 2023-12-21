/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package UI.Component;

import java.awt.Component;
import java.awt.ComponentOrientation;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Harsha
 */

public class JtableCustomRendererUser extends DefaultTableCellRenderer{
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        label.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/person22.png")));
        label.setHorizontalAlignment(SwingConstants.LEFT);
        label.setIconTextGap(10);
        label.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

        return label;
    }
}