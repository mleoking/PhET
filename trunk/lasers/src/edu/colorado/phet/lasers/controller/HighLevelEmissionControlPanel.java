/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.lasers.controller;

import edu.colorado.phet.lasers.controller.module.BaseLaserModule;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * HighLevelEmissionControlPanel
 * <p/>
 * Provide user control over whether photons are shown when an atom in the high energy state
 * drops to the middle energy state
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class HighLevelEmissionControlPanel extends JPanel {

    public HighLevelEmissionControlPanel( final BaseLaserModule module ) {
        final JCheckBox displayHighLevelEmissionsCB = new JCheckBox( "<html>Display photons<br>emitted from upper<br>energy state</html>" );
        displayHighLevelEmissionsCB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setDisplayHighLevelEmissions( displayHighLevelEmissionsCB.isSelected() );
            }
        } );
        this.setLayout( new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                         1, 1, 1, 1,
                                                         GridBagConstraints.NORTHWEST,
                                                         GridBagConstraints.HORIZONTAL,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );
        this.add( displayHighLevelEmissionsCB, gbc );
    }
}
