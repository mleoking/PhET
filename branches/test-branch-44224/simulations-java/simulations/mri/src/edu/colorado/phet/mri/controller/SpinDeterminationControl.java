/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.controller;

import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.mri.model.DipoleOrientationAgent;
import edu.colorado.phet.mri.model.MriModel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * SpinDeterminationControl
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SpinDeterminationControl extends JPanel {
    public SpinDeterminationControl( final MriModel model ) {
        super( new GridBagLayout() );
        final JRadioButton deterministicRb = new JRadioButton( "deterministic" );
        deterministicRb.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                if( deterministicRb.isSelected() ) {
                    model.setSpinDeterminationPolicy( new DipoleOrientationAgent.DeterministicPolicy() );
                }
            }
        } );
        final JRadioButton stocasticRB = new JRadioButton( "stocastic" );
        stocasticRB.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                if( stocasticRB.isSelected() ) {
                    model.setSpinDeterminationPolicy( new DipoleOrientationAgent.StocasticPolicy() );
                }
            }
        } );

        deterministicRb.setSelected( MriConfig.InitialConditions.SPIN_DETERMINATION_POLICY instanceof DipoleOrientationAgent.DeterministicPolicy );
        stocasticRB.setSelected( MriConfig.InitialConditions.SPIN_DETERMINATION_POLICY instanceof DipoleOrientationAgent.StocasticPolicy );

        ButtonGroup btnGrp = new ButtonGroup();
        btnGrp.add( deterministicRb );
        btnGrp.add( stocasticRB );

        GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 1, 1,
                                                         GridBagConstraints.NORTHWEST,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 0, 30, 0, 0 ), 0, 0 );
        add( stocasticRB, gbc );
        add( deterministicRb, gbc );

        setBorder( new TitledBorder( "Spin determination policy" ) );
        setPreferredSize( new Dimension( 180, (int)getPreferredSize().getHeight() ) );
    }
}
