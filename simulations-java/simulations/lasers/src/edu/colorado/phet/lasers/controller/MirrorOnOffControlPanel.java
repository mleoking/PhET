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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import edu.colorado.phet.lasers.LasersResources;
import edu.colorado.phet.lasers.controller.module.BaseLaserModule;

/**
 * MirrorOnOffControlPanel
 * <p/>
 * Provides a check box for enabling and disabling mirror on the cavity
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MirrorOnOffControlPanel extends JPanel {

    public MirrorOnOffControlPanel( final BaseLaserModule module ) {

        final String addMirrorsStr = LasersResources.getString( "LaserControlPanel.AddMirrorsCheckBox" );
        final JCheckBox mirrorCB = new JCheckBox( addMirrorsStr );
        mirrorCB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( mirrorCB.isSelected() ) {
                    module.setMirrorsEnabled( true );
                }
                else {
                    module.setMirrorsEnabled( false );
                }
            }
        } );
        this.setLayout( new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                         1, 1, 1, 1,
                                                         GridBagConstraints.CENTER,
                                                         GridBagConstraints.HORIZONTAL,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );
        this.add( mirrorCB, gbc );
    }
}
