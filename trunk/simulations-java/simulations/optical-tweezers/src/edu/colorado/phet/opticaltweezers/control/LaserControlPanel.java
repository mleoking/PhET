/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.BoundedDragHandler;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.model.Laser;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
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
    private static final int X_SPACING = 20; // horizontal spacing between components, in pixels
    private static final int X_SPACING_INTENSITY_CONTROL = 3; // spacing between parts of the intensity control
    
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

    private PPath _backgroundNode;
    private PSwing _startStopButtonWrapper;
    private PSwing _labelWrapper;
    private PSwing _sliderWrapper;
    private PSwing _textFieldWrapper;
    private PSwing _unitsWrapper;
    private PNode _signNode;
    
    private Icon _startIcon, _stopIcon;
    private ChangeListener _powerControlListener;
    
    //----------------------------------------------------------------------------
    // Constructors & initializers
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
        _signNode = new PImage( OTResources.getImage( OTConstants.IMAGE_LASER_SIGN  ) );
        
        // Start/Stop button
        _startIcon = new ImageIcon( OTResources.getImage( OTConstants.IMAGE_OFF_BUTTON  ) );
        _stopIcon = new ImageIcon( OTResources.getImage( OTConstants.IMAGE_ON_BUTTON  ) );
        _startStopButton = new JButton( _laser.isRunning() ? _stopIcon : _startIcon );
        _startStopButton.setOpaque( false );
        _startStopButton.setMargin( new Insets( 0, 0, 0, 0 ) );
        _startStopButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                _laser.setRunning( !_laser.isRunning() );
                _startStopButton.setIcon( _laser.isRunning() ? _stopIcon : _startIcon );
            }
        } );
        _startStopButtonWrapper = new PSwing( _startStopButton );
        
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
        _labelWrapper = new PSwing( _powerControl.getLabel() );
        _sliderWrapper = new PSwing( _powerControl.getSlider() );
        _textFieldWrapper = new PSwing( _powerControl.getTextField() );
        _unitsWrapper = new PSwing( _powerControl.getUnitsLabel() );
        
        // Panel background
        double xMargin = X_MARGIN;
        double panelWidth = X_MARGIN + 
            _startStopButtonWrapper.getFullBounds().getWidth() + X_SPACING +
            _labelWrapper.getFullBounds().getWidth() + X_SPACING_INTENSITY_CONTROL + 
            _sliderWrapper.getFullBounds().getWidth() + X_SPACING_INTENSITY_CONTROL + 
            _textFieldWrapper.getFullBounds().getWidth() + X_SPACING_INTENSITY_CONTROL + 
            _unitsWrapper.getFullBounds().getWidth() + X_SPACING + 
            _signNode.getWidth() + 
            X_MARGIN;
        if ( panelWidth < minPanelWidth ) {
            xMargin = ( minPanelWidth - panelWidth ) / 2;
            panelWidth = minPanelWidth;
        }
        double maxComponentHeight = Math.max( _startStopButtonWrapper.getFullBounds().getHeight(), _labelWrapper.getFullBounds().getHeight() );
        maxComponentHeight = Math.max( maxComponentHeight, _labelWrapper.getFullBounds().getHeight() );
        maxComponentHeight = Math.max( maxComponentHeight, _sliderWrapper.getFullBounds().getHeight() );
        maxComponentHeight = Math.max( maxComponentHeight, _textFieldWrapper.getFullBounds().getHeight() );
        maxComponentHeight = Math.max( maxComponentHeight, _unitsWrapper.getFullBounds().getHeight() );
        maxComponentHeight = Math.max( maxComponentHeight, _signNode.getFullBounds().getHeight() );
        double panelHeight = Y_MARGIN + maxComponentHeight + Y_MARGIN;
        _backgroundNode = new PPath( new Rectangle2D.Double( 0, 0, panelWidth, panelHeight ) );
        _backgroundNode.setStroke( PANEL_STROKE );
        _backgroundNode.setStrokePaint( PANEL_STROKE_COLOR );
        _backgroundNode.setPaint( PANEL_FILL_COLOR );
        
        // Layering
        addChild( _backgroundNode );
        addChild( _startStopButtonWrapper );
        addChild( _labelWrapper );
        addChild( _sliderWrapper );
        addChild( _textFieldWrapper );
        addChild( _unitsWrapper );
        addChild( _signNode );
        
        // Hand cursor on Swing controls
        _startStopButtonWrapper.addInputEventListener( new CursorHandler() );

        // Positioning, all components vertically centered
        {
            final double bgHeight = _backgroundNode.getFullBounds().getHeight();
            double x = 0;
            double y = 0;
            _backgroundNode.setOffset( x, y );

            x += xMargin;
            y = ( bgHeight - _startStopButtonWrapper.getHeight() ) / 2;
            _startStopButtonWrapper.setOffset( x, y );

            x += _startStopButtonWrapper.getWidth() + X_SPACING;
            y = ( bgHeight - _labelWrapper.getFullBounds().getHeight() ) / 2;
            _labelWrapper.setOffset( x, y );

            x += _labelWrapper.getFullBounds().getWidth() + X_SPACING_INTENSITY_CONTROL;
            y = ( bgHeight - _sliderWrapper.getFullBounds().getHeight() ) / 2;
            _sliderWrapper.setOffset( x, y );
            
            x += _sliderWrapper.getFullBounds().getWidth() + X_SPACING_INTENSITY_CONTROL;
            y = ( bgHeight - _textFieldWrapper.getFullBounds().getHeight() ) / 2;
            _textFieldWrapper.setOffset( x, y );
            
            x += _textFieldWrapper.getFullBounds().getWidth() + X_SPACING_INTENSITY_CONTROL;
            y = ( bgHeight - _unitsWrapper.getFullBounds().getHeight() ) / 2;
            _unitsWrapper.setOffset( x, y );
            
            x += _unitsWrapper.getFullBounds().getWidth() + X_SPACING;
            y = ( bgHeight - _signNode.getFullBounds().getHeight() ) / 2;
            _signNode.setOffset( x, y );
        }
    }
    
    public void cleanup() {
        _laser.deleteObserver( this );
    }
    
    /**
     * Initializes a special drag handler that allows us to drag the 
     * entire control panel while still getting drag events to Swing controls.
     * 
     * @param dragNode
     * @param boundingNode
     */
    public void initDragHandler( PNode dragNode, PNode boundingNode ) {
        addInputEventListener( new DragHandler( dragNode, boundingNode ) );
    }
    
    /**
     * Sets the cursor for parts of the control panel that are draggable.
     * 
     * @param cursor
     */
    public void initDragCursor( Cursor cursor ) {
        _backgroundNode.addInputEventListener( new CursorHandler( cursor ) );
        _labelWrapper.addInputEventListener( new CursorHandler( cursor ) );
        _unitsWrapper.addInputEventListener( new CursorHandler( cursor ) );
        _signNode.addInputEventListener( new CursorHandler( cursor ) );
        
        // Hand cursors
        _sliderWrapper.addInputEventListener( new CursorHandler() );
        _textFieldWrapper.addInputEventListener( new CursorHandler() );
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
                _startStopButton.setIcon( _laser.isRunning() ? _stopIcon : _startIcon );
            }
            else if ( arg == Laser.PROPERTY_POWER ) {
                double power = _laser.getPower();
                _powerControl.removeChangeListener( _powerControlListener );
                _powerControl.setPower( (int) power );
                _powerControl.addChangeListener( _powerControlListener );
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /**
     * This drag handler short-circuits drags that occur when the 
     * user grabs any of the Swing controls.  This ensures that the 
     * Swing components behave as expected, while still allowing us
     * to drag anything that isn't a Swing control.
     */
    private class DragHandler extends BoundedDragHandler {

        public DragHandler( PNode dragNode, PNode boundingNode ) {
            super( dragNode, boundingNode );
        }
        
        public void mouseDragged( PInputEvent event ) {
            PNode pickedNode = event.getPickedNode();
            if ( pickedNode != _startStopButtonWrapper && pickedNode != _sliderWrapper && pickedNode != _textFieldWrapper ) {
                super.mouseDragged( event );
            }
        }
    }
}
