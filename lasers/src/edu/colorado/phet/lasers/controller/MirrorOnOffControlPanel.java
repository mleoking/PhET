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

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.lasers.controller.module.BaseLaserModule;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

        final String addMirrorsStr = SimStrings.get( "LaserControlPanel.AddMirrorsCheckBox" );
        final String removeMirrorsStr = SimStrings.get( "LaserControlPanel.RemoveMirrorsCheckBox" );
        final JCheckBox mirrorCB = new JCheckBox( addMirrorsStr );
        mirrorCB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if( mirrorCB.isSelected() ) {
                    mirrorCB.setText( removeMirrorsStr );
                    module.setMirrorsEnabled( true );
                }
                else {
                    mirrorCB.setText( addMirrorsStr );
                    module.setMirrorsEnabled( false );
                }
            }
        } );
        this.setLayout( new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                         1, 1, 1, 1,
                                                         GridBagConstraints.NORTHWEST,
                                                         GridBagConstraints.HORIZONTAL,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );
        this.add( mirrorCB, gbc );
    }
}
