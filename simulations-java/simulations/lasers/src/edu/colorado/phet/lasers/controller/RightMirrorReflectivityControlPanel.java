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

import java.awt.*;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.lasers.LasersResources;
import edu.colorado.phet.lasers.model.mirror.PartialMirror;

/**
 * A panel that provides a control for the reflectivity of the laser's right mirror
 */
public class RightMirrorReflectivityControlPanel extends JPanel implements SimpleObserver {

    private JSlider reflectivitySlider;
    private JTextField reflectivityTF;
    private PartialMirror mirror;

    /**
     */
    public RightMirrorReflectivityControlPanel( final PartialMirror mirror ) {

        this.mirror = mirror;
        mirror.addObserver( this );
        reflectivityTF = new JTextField( 4 );
        reflectivityTF.setEditable( false );
        reflectivityTF.setHorizontalAlignment( JTextField.RIGHT );
        Font clockFont = reflectivityTF.getFont();
        reflectivityTF.setFont( new Font( clockFont.getName(),
                                          LaserConfig.CONTROL_FONT_STYLE,
                                          LaserConfig.CONTROL_FONT_SIZE ) );
        reflectivityTF.setText( Double.toString( 10 ) );

        reflectivitySlider = new JSlider( JSlider.VERTICAL,
                                          0,
                                          100,
                                          50 );
        reflectivitySlider.setPreferredSize( new Dimension( 40, 50 ) );
        reflectivitySlider.setPaintTicks( true );
        reflectivitySlider.setMajorTickSpacing( 10 );
        reflectivitySlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                mirror.setReflectivity( ( (double) reflectivitySlider.getValue() ) / 100 );
                reflectivityTF.setText( Double.toString( reflectivitySlider.getValue() ) );
            }
        } );

        GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                         GridBagConstraints.CENTER,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );
        this.setLayout( new GridBagLayout() );
        JLabel title = new JLabel( LasersResources.getString( "RightMirrorReflectivityControlPanel.BorderTitle" ) );
        gbc.gridwidth = 2;
        this.add( title, gbc );
        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets( 0, 5, 0, 0 );
        this.add( reflectivitySlider, gbc );
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets( 0, 0, 0, 5 );
        this.add( reflectivityTF, gbc );
    }

    public void update() {
        int reflectivity = (int) ( mirror.getReflectivity() * 100 );
        if ( reflectivitySlider.getValue() != reflectivity ) {
            reflectivityTF.setText( Integer.toString( reflectivity ) );
            reflectivitySlider.setValue( reflectivity );

        }
    }
}
