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

import edu.colorado.phet.mri.view.MonitorPanel;
import edu.colorado.phet.mri.MriConfig;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;

/**
 * MonitorPaneRepControl
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MonitorPanelRepControl extends JPanel {

    public MonitorPanelRepControl( final MonitorPanel monitorPanel ) {
        super( new GridBagLayout() );

        final JRadioButton transparent = new JRadioButton( "transparency" );
        transparent.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                if( transparent.isSelected() ) {
                    monitorPanel.setRepresentationPolicy( new MonitorPanel.TransparencyPolicy() );
                }
            }
        } );

        final JRadioButton discrete = new JRadioButton( "discrete" );
        discrete.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                if( discrete.isSelected() ) {
                    monitorPanel.setRepresentationPolicy( new MonitorPanel.DiscretePolicy() );
                }
            }
        } );

        transparent.setSelected( MriConfig.InitialConditions.MONITOR_PANEL_REP_POLICY instanceof MonitorPanel.TransparencyPolicy );
        discrete.setSelected( MriConfig.InitialConditions.MONITOR_PANEL_REP_POLICY instanceof MonitorPanel.DiscretePolicy );

        ButtonGroup btnGrp = new ButtonGroup();
        btnGrp.add( discrete );
        btnGrp.add( transparent );

        GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 1, 1,
                                                         GridBagConstraints.NORTHWEST,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 0, 30, 0, 0 ), 0, 0 );
        add( transparent, gbc );
        add( discrete, gbc );

        setBorder( new TitledBorder( "Monitor panel representation" ) );
        setPreferredSize( new Dimension( 180, (int)getPreferredSize().getHeight() ) );
    }
}

