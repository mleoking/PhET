/* Copyright 2007, University of Colorado */

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

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.model.Laser;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * LaserControlPanel is the panel used to control laser properties. 
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LaserControlPanel extends PhetPNode implements Observer {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final int X_MARGIN = 10;
    private static final int Y_MARGIN = 10;
    private static final int X_SPACING = 10; // horizontal spacing between components, in pixels
    
    private static final Stroke PANEL_STROKE = new BasicStroke( 1f );
    private static final Color PANEL_STROKE_COLOR = Color.BLACK;
    private static final Color PANEL_FILL_COLOR = Color.DARK_GRAY;
    
    private static final Dimension POWER_CONTROL_SLIDER_SIZE = new Dimension( 150, 25 );
    private static final String POWER_CONTROL_PATTERN = "0";
    private static final int POWER_CONTROL_COLUMNS = 4;
        
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Laser _laser;
    
    private JButton _startStopButton;
    private LaserPowerControl _powerControl;
    private ChangeListener _powerControlListener;
    
    private String _startString, _stopString;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param font
     * @param minPanelWidth
     * @param laser
     * @param powerRange
     */
    public LaserControlPanel( Laser laser, Font font, double minPanelWidth ) {
        super();
        
        _laser = laser;
        _laser.addObserver( this );
        
        // Warning sign
        PNode signNode = new PImage( OTResources.getImage( OTConstants.IMAGE_LASER_SIGN  ) );
        
        // Start/Stop button
        _startString = OTResources.getString( "label.startLaser" );
        _stopString = OTResources.getString( "label.stopLaser" );
        _startStopButton = new JButton( _laser.isRunning() ? _stopString : _startString );
        _startStopButton.setOpaque( false );
        _startStopButton.setFont( font );
        _startStopButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                _laser.setRunning( !_laser.isRunning() );
                _startStopButton.setText( _laser.isRunning() ? _stopString : _startString );
            }
        } );
        PSwing startStopButtonWrapper = new PSwing( _startStopButton );
        
        // Power control
        DoubleRange powerRange = _laser.getPowerRange();
        String label = OTResources.getString( "label.power" );
        String units = OTResources.getString( "units.power" );
        double wavelength = laser.getVisibleWavelength();
        _powerControl = new LaserPowerControl( powerRange, label, units, POWER_CONTROL_PATTERN, POWER_CONTROL_COLUMNS, wavelength, POWER_CONTROL_SLIDER_SIZE, font );
        _powerControl.setLabelForeground( Color.WHITE );
        _powerControl.setUnitsForeground( Color.WHITE );
        _powerControlListener = new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                double power = _powerControl.getPower();
                _laser.setPower( power );
            }
        };
        _powerControl.addChangeListener( _powerControlListener );
        PSwing powerControlWrapper = new PSwing( _powerControl );
        
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
    // Mutators and accessors
    //----------------------------------------------------------------------------
    
    public double getMinPower() {
        return _powerControl.getMinPower();
    }
    
    public double getMaxPower() {
        return _powerControl.getMaxPower();
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
                _powerControl.removeChangeListener( _powerControlListener );
                _powerControl.setPower( (int) power );
                _powerControl.addChangeListener( _powerControlListener );
            }
        }
    }
}
