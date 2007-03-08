/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.view.components.menu.HelpMenu;
import edu.colorado.phet.common.view.components.menu.PhetFileMenu;
import edu.colorado.phet.common.view.util.SwingUtils;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * PhetFrame
 *
 * @author ?
 * @version $Revision$
 */
public class PhetFrame extends JFrame {
    HelpMenu helpMenu;
    private JMenu defaultFileMenu;
    private boolean paused;

    public PhetFrame(final ApplicationModel appDescriptor) {
        super(appDescriptor.getWindowTitle());
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }

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

    /**
     * Adds a JMenu before the Help Menu.
     *
     * @param menu
     */
    public void addMenu(JMenu menu) {
        SwingUtils.addMenuAt(menu, getJMenuBar(), getJMenuBar().getComponentCount() - 1);
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
