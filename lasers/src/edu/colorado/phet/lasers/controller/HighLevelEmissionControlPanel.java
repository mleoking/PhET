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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * HighLevelEmissionControlPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class HighLevelEmissionControlPanel extends JPanel {

    public HighLevelEmissionControlPanel( final BaseLaserModule module ) {
        final JCheckBox displayHighLevelEmissionsCB = new JCheckBox( "<html>Display photons emitted<br>from upperenergy state</html>" );
        displayHighLevelEmissionsCB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setDisplayHighLevelEmissions( displayHighLevelEmissionsCB.isSelected() );
            }
        } );
        this.add( displayHighLevelEmissionsCB );
    }
}
