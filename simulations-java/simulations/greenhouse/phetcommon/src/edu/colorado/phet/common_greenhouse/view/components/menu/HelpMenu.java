/**
 * Class: HelpMenu
 * Package: edu.colorado.phet.common.view
 * Author: Another Guy
 * Date: May 28, 2003
 */
package edu.colorado.phet.common_greenhouse.view.components.menu;

import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.phetcommon.application.PhetAboutDialog;
import edu.colorado.phet.common.phetcommon.application.NonPiccoloPhetApplication;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;

public class HelpMenu extends JMenu {
    public HelpMenu(final String name, String description, String version) {
        super(SimStrings.get("HelpMenu.MenuTitle"));
        final JMenuItem about = new JMenuItem(SimStrings.get("HelpMenu.AboutMenuItem"));
        about.setMnemonic(SimStrings.get("HelpMenu.AboutMenuItemMnemonic").charAt(0));
        this.setMnemonic(SimStrings.get("HelpMenu.MenuTitleMnemonic").charAt(0));
        final String message = name + "\n" + description + "\n" + SimStrings.get("HelpMenu.VersionLabel") + ": " + version;
        about.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAboutDialog(about, message, name);
            }
        });
        add(about);
    }

    private void showAboutDialogORIG(JMenuItem about, String message, String name) {
        JOptionPane.showMessageDialog(about, message, SimStrings.get("HelpMenu.AboutLabel") + " " + name, JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAboutDialog(JMenuItem about, String message, String name) {
        Window ax = SwingUtilities.getWindowAncestor(about);
        PhetAboutDialog phetAboutDialog = new PhetAboutDialog((Frame) ax, "greenhouse");
//        SwingUtils.centerDialogInParent(phetAboutDialog);
        SwingUtils.centerWindowOnScreen(phetAboutDialog);
        phetAboutDialog.setVisible(true);

//        JOptionPane.showMessageDialog(about, message, SimStrings.get("HelpMenu.AboutLabel") + " " + name, JOptionPane.INFORMATION_MESSAGE);
    }
}
