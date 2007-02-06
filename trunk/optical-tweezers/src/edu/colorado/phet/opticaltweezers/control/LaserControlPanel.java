/* Copyright 2007, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.opticaltweezers.control;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;

import edu.colorado.phet.common.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.util.DoubleRange;


public class LaserControlPanel extends HorizontalLayoutPanel {

    private JButton _startStopButton;
    private LaserPowerControl _powerControl;
    
    public LaserControlPanel( Font font ) {
        super();
        
        JLabel cautionLabel = new JLabel( "CAUTION!" );
        
        _startStopButton = new JButton( SimStrings.get( "label.startLaser" ) );
        _startStopButton.setOpaque( false );
        
        DoubleRange range = OTConstants.LASER_POWER_RANGE;
        String label = SimStrings.get( "label.power" );
        String units = SimStrings.get( "units.power" );
        int columns = 3; //XXX
        double wavelength = OTConstants.LASER_WAVELENGTH;
        Dimension size = new Dimension( 150, 15 );
        _powerControl = new LaserPowerControl( range, label, units, columns, wavelength, size, font );
        _powerControl.setLabelForeground( Color.WHITE );
        _powerControl.setUnitsForeground( Color.WHITE );
        
        setBorder( BorderFactory.createLineBorder( Color.BLACK ) );
        setBackground( Color.DARK_GRAY );
        setInsets( new Insets( 5, 5, 5, 5 ) );
        add( cautionLabel );
        add( Box.createHorizontalStrut( 20 ) );
        add( _startStopButton );
        add( Box.createHorizontalStrut( 20 ) );
        add( _powerControl );
    }
}
