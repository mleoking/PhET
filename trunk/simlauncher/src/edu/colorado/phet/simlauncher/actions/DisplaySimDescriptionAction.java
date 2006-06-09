/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher.actions;

import edu.colorado.phet.simlauncher.SimContainer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * DisplaySimDescriptionAction
 * <p/>
 * Displays the abstract for a simulation in a JOptionPane
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class DisplaySimDescriptionAction extends AbstractAction {
    private SimContainer simContainer;
    private Component parent;

    public DisplaySimDescriptionAction( SimContainer simContainer, Component parent ) {
        this.simContainer = simContainer;
        this.parent = parent;
    }

    public void actionPerformed( ActionEvent e ) {
        JOptionPane.showMessageDialog( parent,
                                       "Not yet implemented",
                                       "Simulation description",
                                       JOptionPane.PLAIN_MESSAGE );
    }
}
