/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.geom.GeneralPath;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.ParseException;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.piccolo.event.ConstrainedDragHandler;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PComposite;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * WavelengthControl is the control used for setting wavelength.
 * It handles wavelengths inside and outside the visible spectrum.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class WavelengthControl extends PNode {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Dimension DEFAULT_KNOB_SIZE = new Dimension( 20, 20 );
    private static final Stroke KNOB_STROKE = new BasicStroke( 1f );
    private static final String UV_STRING = "< UV";
    private static final String IR_STRING = "IR >";
    private static final Color UV_LABEL_COLOR = Color.WHITE;
    private static final Color IR_LABEL_COLOR = Color.WHITE;
    private static final Font UV_IR_FONT = new Font( HAConstants.FONT_NAME, Font.BOLD, 12 );
    private static final double UV_IR_LABEL_MARGIN = 3;
    private static final DecimalFormat VALUE_FORMAT = new DecimalFormat( "0" );
    private static final double VALUE_Y_OFFSET = 2;
    private static final String UNITS_LABEL = "nm";
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PSwingCanvas _canvas;
    private double _minWavelength, _maxWavelength;
    private Color _uvColor, _irColor;
    private Knob _knob;
    private Track _track;
    private ValueDisplay _valueDisplay;
    private ConstrainedDragHandler _dragHandler;
    private EventListenerList _listenerList;
    private double _wavelength;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Creates a wavelength control for the visible spectrum.
     */
    public WavelengthControl( PSwingCanvas canvas ) {
        this( canvas, VisibleColor.MIN_WAVELENGTH, VisibleColor.MAX_WAVELENGTH, Color.BLACK, Color.BLACK );
    }
    
    /**
     * Creates a wavelength control for a range of wavelengths,
     * including wavelengths outside the visible spectrum.
     * 
     * @param canvas
     * @param minWavelength minimum wavelength, in nanometers
     * @param maxWavelength maximum wavelength, in nanometers
     * @param uvColor color used for UV
     * @param irColor color used for IR
     */
    public WavelengthControl( PSwingCanvas canvas, double minWavelength, double maxWavelength, Color uvColor, Color irColor ) {
        super();
        
        if ( minWavelength >= maxWavelength ) {
            throw new IllegalArgumentException( "have you reversed the minWavelength and maxWavelength args?" );
        }
        if ( minWavelength > VisibleColor.MIN_WAVELENGTH || maxWavelength < VisibleColor.MAX_WAVELENGTH ) {
            throw new UnsupportedOperationException( "entire visible spectrum must be shown" );
        }
            
        _canvas = canvas;
        _minWavelength = minWavelength;
        _maxWavelength = maxWavelength;
        _wavelength = _minWavelength - 1; // any value outside the range
        _uvColor = uvColor;
        _irColor = irColor;
        _listenerList = new EventListenerList();
        
        _knob = new Knob( DEFAULT_KNOB_SIZE );
        _track = new Track( minWavelength, maxWavelength, uvColor, irColor );
        _valueDisplay = new ValueDisplay( _canvas );
        
        addChild( _track );
        addChild( _knob );
        addChild( _valueDisplay );
        
        _track.setOffset( 0, 0 );
        _knob.setOffset( 0, _track.getFullBounds().getHeight() );
        _valueDisplay.setOffset( 0, -_valueDisplay.getFullBounds().getHeight() - VALUE_Y_OFFSET );
        
        _dragHandler = new ConstrainedDragHandler();
        _dragHandler.setVerticalLockEnabled( true );
        _dragHandler.setTreatAsPointEnabled( true );
        _dragHandler.setNodeCenter( _knob.getFullBounds().getWidth() / 2, 0 );
        _dragHandler.setDragBounds( _track.getGlobalFullBounds() ); 
        _knob.addInputEventListener( _dragHandler );
        _knob.addInputEventListener( new CursorHandler() );
        _knob.addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                handleKnobDrag();
            }
        } );
        
        // Adjust the knob's drag bounds if this control's bounds are changed.
        addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent event ) {
                if ( PNode.PROPERTY_FULL_BOUNDS.equals( event.getPropertyName() ) ) {
                    _dragHandler.setDragBounds( _track.getGlobalFullBounds() ); 
                }
            }
        } );
        
        // Default state 
        setWavelength( VisibleColor.MIN_WAVELENGTH );
    }
    
    //----------------------------------------------------------------------------
    // Mutators
    //----------------------------------------------------------------------------
    
    /**
     * Sets the wavelength.
     * 
     * @param wavelength wavelength in nanometers
     */
    public void setWavelength( double wavelength ) {
        if ( wavelength != _wavelength ) {
            _wavelength = wavelength;
            updateKnobColor( wavelength );
            updateKnobPosition( wavelength );
            updateValueDisplay( wavelength );
            fireChangeEvent( new ChangeEvent( this ) );
        }
    }
    
    /**
     * Gets the wavelength.
     * 
     * @return wavelength in nanometers
     */
    public double getWavelength() {
        return _wavelength;
    }
    
    //----------------------------------------------------------------------------
    // Private methods
    //----------------------------------------------------------------------------
    
    /*
     * Calculates the wavelength that corresponds to the knob position.
     */
    private double calculateWavelength() {
        double bandwidth = _maxWavelength - _minWavelength;
        PBounds trackBounds = _track.getFullBounds();
        PBounds knobBounds = _knob.getFullBounds();
        double trackX = trackBounds.getX();
        double trackWidth = trackBounds.getWidth();
        double knobX = knobBounds.getX() + ( knobBounds.getWidth() / 2 );
        double wavelength = _minWavelength + ( ( ( knobX - trackX ) / trackWidth ) * bandwidth );
        return wavelength;
    }
    
    /*
     * Handles dragging of the knob.
     */
    private void handleKnobDrag() {
        double wavelength = calculateWavelength();
        if ( wavelength != _wavelength ) {
            _wavelength = wavelength;
            updateKnobColor( wavelength );
            updateValueDisplay( wavelength );
            fireChangeEvent( new ChangeEvent( this ) );
        }
    }
    
    /*
     * Handles entry of values in the text field.
     */
    public void handleValueEntry() {
        double wavelength = _valueDisplay.getValue();
        if ( wavelength >= _minWavelength && wavelength <= _maxWavelength ) {
            if ( wavelength != _wavelength ) {
                _wavelength = wavelength;
                updateKnobColor( wavelength );
                updateKnobPosition( wavelength );
                updateValueDisplay( wavelength );
                fireChangeEvent( new ChangeEvent( this ) );
            }
        }
        else {
            warnUser();
            updateValueDisplay( wavelength );
        }
    }
    
    /*
     * Updates the knob's color to match a specified wavelength.
     */
    private void updateKnobColor( double wavelength ) {
        if ( wavelength < VisibleColor.MIN_WAVELENGTH ) {
            _knob.setPaint( _uvColor );
        }
        else if ( wavelength > VisibleColor.MAX_WAVELENGTH ) {
            _knob.setPaint( _irColor );
        }
        else {
            Color color = VisibleColor.wavelengthToColor( wavelength );
            _knob.setPaint( color );
        }
    }
    
    /*
     * Updates the knob's position to match a specified wavelength.
     */
    private void updateKnobPosition( double wavelength ) {
        if ( wavelength < _minWavelength || wavelength > _maxWavelength ) {
            throw new IllegalArgumentException( "wavelength out of range: " + wavelength );
        }
        double bandwidth = _maxWavelength - _minWavelength;
        PBounds trackBounds = _track.getFullBounds();
        PBounds knobBounds = _knob.getFullBounds();
        double trackX = trackBounds.getX();
        double trackWidth = trackBounds.getWidth();
        double knobY = knobBounds.getY();
        double knobX = trackX + ( trackWidth * ( ( wavelength - _minWavelength ) / bandwidth ) );
        _knob.setOffset( knobX, knobY );
    }
    
    /*
     * Update the value display, changing the value shown and the position of the textfield.
     */
    private void updateValueDisplay( double wavelength ) {
        _valueDisplay.setValue( wavelength );
        PBounds valueDisplayBound = _valueDisplay.getFullBounds();
        PBounds knobBonds = _knob.getFullBounds();
        final double x = knobBonds.getX() + ( knobBonds.getWidth() / 2 ) - ( valueDisplayBound.getWidth() / 2 );
        final double y = valueDisplayBound.getY();
        _valueDisplay.setOffset( x, y );
    }
    
    /*
     * Produces an audible beep, used to indicate invalid text entry.
     */
    private void warnUser() {
        Toolkit.getDefaultToolkit().beep();
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /*
     * The slider knob.
     */
    private static class Knob extends PPath {
        
        public Knob( Dimension size ) {
            super();
            
            GeneralPath path = new GeneralPath();
            path.moveTo( 0, 0 ); // tip of the knob
            path.lineTo( -0.5f * size.width, 0.3f * size.height );
            path.lineTo( -0.5f * size.width, 1f * size.height );
            path.lineTo( 0.5f * size.width, 1f * size.height );
            path.lineTo( 0.5f * size.width, 0.3f * size.height );
            path.closePath();    
            setPathTo( path );
            
            setStroke( KNOB_STROKE );
            setPaint( Color.WHITE );
        }
    }
    
    /*
     * The track that the slider knob moves in.
     */
    private static class Track extends PComposite {
        
        public Track( double minWavelength, double maxWavelength, Color uvColor, Color irColor ) {
            super();
            
            PImage spectrumTrack = PImageFactory.create( HAConstants.IMAGE_SPECTRUM );
            double spectrumTrackWidth = spectrumTrack.getFullBounds().getWidth();
            double spectrumTrackHeight = spectrumTrack.getFullBounds().getHeight();
            
            double visibleBandwidth = VisibleColor.MAX_WAVELENGTH - VisibleColor.MIN_WAVELENGTH;
            double uvBandwidth = VisibleColor.MIN_WAVELENGTH - minWavelength;
            double irBandwith = maxWavelength - VisibleColor.MAX_WAVELENGTH;
            double uvTrackWidth = ( uvBandwidth / visibleBandwidth ) * spectrumTrackWidth;
            double irTrackWidth = ( irBandwith / visibleBandwidth ) * spectrumTrackWidth;
            
            boolean hasUV = ( uvTrackWidth > 0 );
            boolean hasIR = ( irTrackWidth > 0 );
            
            PPath uvTrack = null;
            PText uvText = null;
            if ( hasUV ) {
                
                uvTrack = new PPath();
                uvTrack.setPathTo( new Rectangle.Double( 0, 0, uvTrackWidth, spectrumTrackHeight ) );
                uvTrack.setPaint( uvColor );
                uvTrack.setStrokePaint( null );
                
                uvText = new PText( UV_STRING );
                uvText.setFont( UV_IR_FONT );
                uvText.setTextPaint( UV_LABEL_COLOR );
            }
            
            PPath irTrack = null;
            PText irText = null;
            if ( hasIR ) {
                
                irTrack = new PPath();
                irTrack.setPathTo( new Rectangle.Double( 0, 0, irTrackWidth, spectrumTrackHeight ) );
                irTrack.setPaint( irColor );
                irTrack.setStrokePaint( null );

                irText = new PText( IR_STRING );
                irText.setFont( UV_IR_FONT );
                irText.setTextPaint( IR_LABEL_COLOR );
            }
            
            // Layering
            if ( hasUV ) {
                addChild( uvTrack );
                addChild( uvText );
            }
            if ( hasIR ) {
                addChild( irTrack );
                addChild( irText );
            }
            addChild( spectrumTrack );

            // Positioning
            if ( hasUV ) {
                uvTrack.setOffset( 0, 0 );
                spectrumTrack.setOffset( uvTrack.getFullBounds().getX() + uvTrack.getFullBounds().getWidth(), 0 );
                uvText.setOffset( spectrumTrack.getFullBounds().getX() - uvText.getFullBounds().getWidth() - UV_IR_LABEL_MARGIN, 
                        ( spectrumTrack.getFullBounds().getHeight() - uvText.getFullBounds().getHeight() ) / 2 );
            }
            else {
                spectrumTrack.setOffset( 0, 0 );
            }
            
            if ( hasIR ) {
                irTrack.setOffset( spectrumTrack.getFullBounds().getX() + spectrumTrack.getFullBounds().getWidth(), 0 );
                irText.setOffset( spectrumTrack.getFullBounds().getX() + spectrumTrack.getFullBounds().getWidth() + UV_IR_LABEL_MARGIN, 
                        ( spectrumTrack.getFullBounds().getHeight() - irText.getFullBounds().getHeight() ) / 2 );
            }
        }
    }
    
    /*
     * Displays and edits the wavelength value as text.
     */
    private class ValueDisplay extends PNode {
        
        private JFormattedTextField _formattedTextField;
        
        public ValueDisplay( PSwingCanvas canvas ) {
            super();
            
            JLabel unitsLabel = new JLabel( UNITS_LABEL );
            unitsLabel.setFont( new Font( HAConstants.FONT_NAME, Font.PLAIN, 14 ) );
            
            _formattedTextField = new JFormattedTextField();
            _formattedTextField.setColumns( 3 );
            _formattedTextField.setHorizontalAlignment( JTextField.RIGHT );
            
            _formattedTextField.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    handleValueEntry();
                }
            } );
            _formattedTextField.addFocusListener( new FocusListener() {
                /* Selects the entire value text field when it gains focus. */
                public void focusGained( FocusEvent e ) {
                    _formattedTextField.selectAll();
                }
                /* Processes the text field when it loses focus. */
                public void focusLost( FocusEvent e ) {
                    try {
                        _formattedTextField.commitEdit();
                        handleValueEntry();
                    }
                    catch ( ParseException pe ) {
                        warnUser();
                        setValue( _wavelength ); // revert
                    }
                }
            } );
            
            // Layout
            JPanel panel = new JPanel();
            EasyGridBagLayout layout = new EasyGridBagLayout( panel );
            layout.setInsets( new Insets( 0, 3, 0, 0 ) );
            panel.setLayout( layout );
            layout.setAnchor( GridBagConstraints.WEST );
            int row = 0;
            int col = 0;
            layout.addComponent( _formattedTextField, row, col++ );
            layout.addComponent( unitsLabel, row, col );
            
            // Opacity
            panel.setOpaque( false );
            unitsLabel.setOpaque( false );
            
            // Piccolo wrapper
            PSwing pswing = new PSwing( canvas, panel );
            addChild( pswing );
        }
        
        public void setValue( double wavelength ) {
            String s = VALUE_FORMAT.format( wavelength );
            _formattedTextField.setText( s );
        }
        
        public double getValue() {
            String text = _formattedTextField.getText().toLowerCase();
            double wavelength = 0;
            try {
                wavelength = Double.parseDouble( text );
            }
            catch ( NumberFormatException nfe ) {
                warnUser();
                wavelength = _wavelength;
                this.setValue( wavelength );
            }
            return wavelength;
        }
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------

    /**
     * Adds a ChangeListener.
     *
     * @param listener the listener
     */
    public void addChangeListener( ChangeListener listener ) {
        _listenerList.add( ChangeListener.class, listener );
    }

    /**
     * Removes a ChangeListener.
     *
     * @param listener the listener
     */
    public void removeChangeListener( ChangeListener listener ) {
        _listenerList.remove( ChangeListener.class, listener );
    }

    /**
     * Fires a ChangeEvent.
     *
     * @param event the event
     */
    private void fireChangeEvent( ChangeEvent event ) {
        Object[] listeners = _listenerList.getListenerList();
        for( int i = 0; i < listeners.length; i += 2 ) {
            if( listeners[i] == ChangeListener.class ) {
                ( (ChangeListener)listeners[i + 1] ).stateChanged( event );
            }
        }
    }
}
