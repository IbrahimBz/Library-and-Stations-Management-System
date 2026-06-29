
package org.library;

import javax.swing.SwingUtilities;
public class LibraryMain {


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LibraryGUI().setVisible(true);
        });
    }
}
