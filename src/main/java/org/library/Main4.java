/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package main4;

/**
 *
 * @author Munzer
 */
import javax.swing.SwingUtilities;
public class Main4 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // تشغيل الواجهة الرسومية بشكل آمن داخل الـ Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            new LibraryGUI().setVisible(true);
        });
    }
}
