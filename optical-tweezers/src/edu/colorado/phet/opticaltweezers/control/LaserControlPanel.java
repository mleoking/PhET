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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.model.Laser;
import edu.colorado.phet.opticaltweezers.util.DoubleRange;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;


public class LaserControlPanel extends PhetPNode implements Observer {

    private static final int X_MARGIN = 10;
    private static final int Y_MARGIN = 10;
    private static final int X_SPACING = 20; // horizontal spacing between components, in pixels
    
    private static final Stroke PANEL_STROKE = new BasicStroke( 1f );
    private static final Color PANEL_STROKE_COLOR = Color.BLACK;
    private static final Color PANEL_FILL_COLOR = Color.DARK_GRAY;
    
    private static final Dimension POWER_SLIDER_SIZE = new Dimension( 150, 25 );
    private static final int POWER_VALUE_DIGITS = 3;
        
    private Laser _laser;
    
    private JButton _startStopButton;
    private LaserPowerControl _powerControl;
    
    private String _startString, _stopString;
    
    public LaserControlPanel( PSwingCanvas canvas, Font font, double minPanelWidth, Laser laser, DoubleRange powerRange ) {
        super();
        
        _laser = laser;
        _laser.addObserver( this );
        
        // Warning sign
        PNode signNode = PImageFactory.create( OTConstants.IMAGE_LASER_SIGN );
        
        // Start/Stop button
        _startString = SimStrings.get( "label.startLaser" );
        _stopString = SimStrings.get( "label.stopLaser" );
        _startStopButton = new JButton( _laser.isRunning() ? _stopString : _startString );
        _startStopButton.setOpaque( false );
        _startStopButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                _laser.setRunning( !_laser.isRunning() );
                _startStopButton.setText( _laser.isRunning() ? _stopString : _startString );
            }
        } );
        PSwing startStopButtonWrapper = new PSwing( canvas, _startStopButton );
        
        // Power control
        String label = SimStrings.get( "label.power" );
        String units = SimStrings.get( "units.power" );
        int columns = POWER_VALUE_DIGITS;
        double wavelength = _laser.getWavelength();
        _powerControl = new LaserPowerControl( powerRange, label, units, columns, wavelength, POWER_SLIDER_SIZE, font );
        _powerControl.setLabelForeground( Color.WHITE );
        _powerControl.setUnitsForeground( Color.WHITE );
        _powerControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                int power = _powerControl.getValue();
                _laser.setPower( power );
            }
        } );
        PSwing powerControlWrapper = new PSwing( canvas, _powerControl );
        
        // Panel background
        double xMargin = X_MARGIN;
        double panelWidth = X_MARGIN + signNode.getWidth() + X_SPACING + startStopButtonWrapper.getFullBounds().getWidth() + 
            X_SPACING + powerControlWrapper.getFullBounds().getWidth() + X_MARGIN;
        if ( panelWidth < minPanelWidth ) {
            xMargin = ( minPanelWidth - panelWidth ) / 2;
            panelWidth = minPanelWidth;
        }
        double panelHeight = Y_MARGIN + 
            Math.max( Math.max( signNode.getHeight(), startStopButtonWrapper.getFullBounds().getHeight() ), powerControlWrapper.getFullBounds().getHeight() ) +
            Y_MARGIN;
        PPath backgroundNode = new PPath( new Rectangle2D.Double( 0, 0, panelWidth, panelHeight ) );
        backgroundNode.setStroke( PANEL_STROKE );
        backgroundNode.setStrokePaint( PANEL_STROKE_COLOR );
        backgroundNode.setPaint( PANEL_FILL_COLOR );
        
        // Layering
        addChild( backgroundNode );
        addChild( signNode );
        addChild( startStopButtonWrapper );
        addChild( powerControlWrapper );
        
        // Positioning, all components vertically centered
        final double bgHeight = backgroundNode.getFullBounds().getHeight();
        double x = 0;
        double y = 0;
        backgroundNode.setOffset( x, y );
        x += xMargin;
        y = ( bgHeight - signNode.getHeight() ) / 2;
        signNode.setOffset( x, y );
        x += signNode.getWidth() + X_SPACING;
        y = ( bgHeight - startStopButtonWrapper.getFullBounds().getHeight() ) / 2;
        startStopButtonWrapper.setOffset( x, y );
        x += startStopButtonWrapper.getFullBounds().getWidth() + X_SPACING;
        y = ( bgHeight - powerControlWrapper.getFullBounds().getHeight() ) / 2;
        powerControlWrapper.setOffset( x, y );
    }
    
    public void cleanup() {
        _laser.deleteObserver( this );
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    public void update( Observable o, Object arg ) {
        if ( o == _laser ) {
            if ( arg == Laser.PROPERTY_RUNNING ) {
                _startStopButton.setText( _laser.isRunning() ? _stopString : _startString );
            }
            else if ( arg == Laser.PROPERTY_POWER ) {
                double power = _laser.getPower();
                _powerControl.setValue( (int) power );
            }
        }
    }
}
