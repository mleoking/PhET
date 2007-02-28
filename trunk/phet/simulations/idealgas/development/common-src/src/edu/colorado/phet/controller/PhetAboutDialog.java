/**
 * Created by IntelliJ IDEA.
 * User: Ron LeMaster
 * Date: Dec 9, 2002
 * Time: 4:01:35 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.controller;

import javax.swing.*;

/**
 * This abstract class provides a modal dialog that appears when the Help>About
 * menu item is selected.
 * <p>
 * Applications are required to provide a concrete subclass so that the application's
 * name and version can be viewed by someone running it.
 */
public abstract class PhetAboutDialog {

    private JFrame parent;

    /**
     * @param parent The application's PhetFrame
     */
    public PhetAboutDialog( JFrame parent ) {
        this.parent = parent;
    }

    /**
     * Displays the dialog
     */
    public void show() {
        JOptionPane.showMessageDialog( parent,
                                       "Version: " + getAppVersion(),
                                       getAppTitle(),
                                       JOptionPane.INFORMATION_MESSAGE );
    }

    /**
     * Returns the title of the application
     * @return The title of the application
     */
    protected abstract String getAppTitle();

    /**
     * Returns the version of the application
     * @return The application version number
     */
    protected abstract String getAppVersion();
}
