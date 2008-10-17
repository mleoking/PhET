/**
 * Class: HelpMenu
 * Package: edu.colorado.phet.common.view
 * Author: Another Guy
 * Date: May 28, 2003
 */
package edu.colorado.phet.greenhouse.phetcommon.view.components.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import edu.colorado.phet.common.phetcommon.application.PhetAboutDialog;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

public class HelpMenu extends JMenu {
    private JFrame phetFrame;

    public HelpMenu(JFrame phetFrame, final String name, String description, String version) {
        super(PhetCommonResources.getString("Common.HelpMenu.Title"));
        this.phetFrame = phetFrame;
        final JMenuItem about = new JMenuItem(PhetCommonResources.getString("Common.HelpMenu.About"));
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
