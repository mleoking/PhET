package edu.colorado.phet.lasers.controller;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.lasers.controller.module.BaseLaserModule;
import edu.colorado.phet.lasers.controller.module.WaveViewControlPanel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Class: MultipleAtomControlPanel
 * Package: edu.colorado.phet.lasers.controller
 * Author: Another Guy
 * Date: Nov 23, 2004
 * <p/>
 * CVS Info:
 * Current revision:   $Revision$
 * On branch:          $Name$
 * Latest change by:   $Author$
 * On date:            $Date$
 */
public class MultipleAtomControlPanel extends LaserControlPanel {

    public MultipleAtomControlPanel( final BaseLaserModule module ) {
        super( module );
        // Add a controls for the mirrors and for chosing the photon view
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

        JPanel optionsPanel = new JPanel( new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );
        optionsPanel.setBorder( new TitledBorder( SimStrings.get( "LaserControlPanel.OptionsBorderTitle" ) ) );
        optionsPanel.add( mirrorCB, gbc );
        gbc.gridy++;
        optionsPanel.add( new WaveViewControlPanel( module ), gbc );
        JPanel container = new JPanel();
        Border border = BorderFactory.createEtchedBorder();
        container.setBorder( border );
        container.add( optionsPanel );

        super.addControl( container );
    }
}
