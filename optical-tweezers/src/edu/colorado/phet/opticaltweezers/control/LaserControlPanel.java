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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.*;

import edu.colorado.phet.common.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.util.DoubleRange;


public class LaserControlPanel extends HorizontalLayoutPanel {

    private boolean _laserRunning;//XXX won't need this when we can check the model state
    private JButton _startStopButton;
    private LaserPowerControl _powerControl;
    
    public LaserControlPanel( Font font, double wavelength, DoubleRange powerRange ) {
        super();
        
        JLabel laserSign = null;
        try {
            BufferedImage image = ImageLoader.loadBufferedImage( OTConstants.IMAGE_LASER_SIGN );
            Icon icon = new ImageIcon( image );
            laserSign = new JLabel( icon );
        }
        catch ( IOException e ) {
            e.printStackTrace();
            laserSign = new JLabel( "Caution!" );
        }
        
        _laserRunning = false;
        _startStopButton = new JButton( SimStrings.get( "label.startLaser" ) );
        _startStopButton.setOpaque( false );
        _startStopButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( _laserRunning ) {
                    //XXX stop laser
                    _startStopButton.setText( SimStrings.get( "label.startLaser" ) );
                    _laserRunning = false;
                }
                else {
                    //XXX start laser
                    _startStopButton.setText( SimStrings.get( "label.stopLaser" ) );
                    _laserRunning = true;
                }
            }
        } );
        
        String label = SimStrings.get( "label.power" );
        String units = SimStrings.get( "units.power" );
        int columns = 3; //XXX
        Dimension size = new Dimension( 150, 25 );
        _powerControl = new LaserPowerControl( powerRange, label, units, columns, wavelength, size, font );
        _powerControl.setLabelForeground( Color.WHITE );
        _powerControl.setUnitsForeground( Color.WHITE );
        
        setBorder( BorderFactory.createLineBorder( Color.BLACK ) );
        setBackground( Color.DARK_GRAY );
        setInsets( new Insets( 5, 5, 5, 5 ) );
        add( laserSign );
        add( Box.createHorizontalStrut( 20 ) );
        add( _startStopButton );
        add( Box.createHorizontalStrut( 10 ) );
        add( _powerControl );
    }
}
