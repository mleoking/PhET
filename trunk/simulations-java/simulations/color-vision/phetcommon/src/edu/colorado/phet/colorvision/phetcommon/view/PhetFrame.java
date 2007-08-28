/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author:samreid $
 * Revision : $Revision:14443 $
 * Date modified : $Date:2007-04-12 23:10:41 -0600 (Thu, 12 Apr 2007) $
 */
package edu.colorado.phet.colorvision.phetcommon.view;

import edu.colorado.phet.colorvision.phetcommon.application.ApplicationModel;
import edu.colorado.phet.colorvision.phetcommon.view.components.menu.HelpMenu;
import edu.colorado.phet.colorvision.phetcommon.view.components.menu.PhetFileMenu;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * PhetFrame
 *
 * @author ?
 * @version $Revision:14443 $
 */
public class PhetFrame extends JFrame {
    HelpMenu helpMenu;
    private JMenu defaultFileMenu;
    private boolean paused;

    public PhetFrame(final ApplicationModel appDescriptor) {
        super(appDescriptor.getWindowTitle() + " (" + appDescriptor.getVersion() + ")" );
        this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        this.addWindowListener(new WindowAdapter() {
            
            // Pause the clock if the simulation window is iconified.
            public void windowIconified(WindowEvent e) {
                super.windowIconified(e);
                paused = appDescriptor.getClock().isPaused(); // save clock state
                if (!paused) {
                    appDescriptor.getClock().setPaused(true);
                }
            }

            // Restore the clock state if the simulation window is deiconified.
            public void windowDeiconified(WindowEvent e) {
                super.windowDeiconified(e);
                if (!paused) {
                    appDescriptor.getClock().setPaused(false);
                }
            }
        });
        JMenuBar menuBar = new JMenuBar();
        this.helpMenu = new HelpMenu(appDescriptor);
        defaultFileMenu = new PhetFileMenu();
        menuBar.add(defaultFileMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
        appDescriptor.getFrameSetup().initialize(this);
    }

    public void setVisible(boolean b) {
        super.setVisible(b);
    }

    public void addFileMenuSeparator() {
        defaultFileMenu.insertSeparator(defaultFileMenu.getComponentCount() + 1);
    }

    public void addFileMenuItem(JMenuItem menuItem) {
        defaultFileMenu.insert(menuItem, defaultFileMenu.getComponentCount());
    }

    public void removeFileMenuItem(JMenuItem menuItem) {
        JMenu testMenu = getJMenuBar().getMenu(0);
        if (testMenu != null && testMenu instanceof PhetFileMenu) {
            getJMenuBar().remove(testMenu);
        }
        getJMenuBar().add(defaultFileMenu, 0);
    }

    public void setFileMenu(PhetFileMenu defaultFileMenu) {
        JMenu testMenu = getJMenuBar().getMenu(0);
        if (testMenu != null && testMenu instanceof PhetFileMenu) {
            getJMenuBar().remove(testMenu);
        }
        getJMenuBar().add(defaultFileMenu, 0);
    }
}
