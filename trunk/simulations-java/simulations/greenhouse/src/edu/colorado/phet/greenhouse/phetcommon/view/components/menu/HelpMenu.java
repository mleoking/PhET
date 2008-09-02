/**
 * Class: HelpMenu
 * Package: edu.colorado.phet.common.view
 * Author: Another Guy
 * Date: May 28, 2003
 */
package edu.colorado.phet.greenhouse.phetcommon.view.components.menu;

import edu.colorado.phet.common.phetcommon.application.PhetAboutDialog;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HelpMenu extends JMenu {
    private JFrame phetFrame;

    public HelpMenu(JFrame phetFrame, final String name, String description, String version) {
        super(SimStrings.get("HelpMenu.MenuTitle"));
        this.phetFrame = phetFrame;
        final JMenuItem about = new JMenuItem(SimStrings.get("HelpMenu.AboutMenuItem"));
        about.setMnemonic(SimStrings.get("HelpMenu.AboutMenuItemMnemonic").charAt(0));
        this.setMnemonic(SimStrings.get("HelpMenu.MenuTitleMnemonic").charAt(0));
        about.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAboutDialog();
            }
        });
        add(about);
    }

    private void showAboutDialog() {
        PhetAboutDialog phetAboutDialog = new PhetAboutDialog(phetFrame, "greenhouse");
        SwingUtils.centerDialogInParent(phetAboutDialog);
        phetAboutDialog.setVisible(true);
    }
}
