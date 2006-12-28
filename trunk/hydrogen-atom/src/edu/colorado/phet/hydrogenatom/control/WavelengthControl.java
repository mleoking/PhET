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
import java.awt.event.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
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

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SpectrumImageFactory;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.event.ConstrainedDragHandler;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventListener;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PComposite;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * WavelengthControl is a control used for setting wavelength.
 * It handles visible wavelengths, plus optional UV and IR wavelengths.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class WavelengthControl extends PhetPNode {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final int SPECTRUM_WIDTH = 200;
    private static final int SPECTRUM_HEIGHT = 25;
    
    private static final Dimension KNOB_SIZE = new Dimension( 20, 20 );
    private static final Stroke KNOB_STROKE = new BasicStroke( 1f );
    private static final Color KNOB_STROKE_COLOR = Color.WHITE;
    
    private static final DecimalFormat VALUE_FORMAT = new DecimalFormat( "0" );
    private static final double VALUE_Y_OFFSET = 2;
    
    private static final String UNITS_LABEL = "nm";
    
    private static final double CURSOR_WIDTH = 3;
    private static final Stroke CURSOR_STROKE = new BasicStroke( 1f );
    private static final Color CURSOR_COLOR = Color.BLACK;
    
    private static final String UV_STRING = "UV";
    private static final String IR_STRING = "IR";
    private static final Color UV_TRACK_COLOR = Color.LIGHT_GRAY;
    private static final Color UV_LABEL_COLOR = Color.BLACK;
    private static final Color IR_TRACK_COLOR = UV_TRACK_COLOR;
    private static final Color IR_LABEL_COLOR = UV_LABEL_COLOR;
    
    private static final int TEXT_FIELD_COLUMNS = 3;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // needed for wrapping Swing components with PSwing
    private PSwingCanvas _canvas;
    // control's range, in nanometers
    private double _minWavelength, _maxWavelength;
    // colors used to represent UV and IR wavelengths
    private Color _uvColor, _irColor;
    // slider knob, what the user drags
    private Knob _knob;
    // track that the knob moves along
    private Track _track;
    // editable value displayed above the track
    private ValueDisplay _valueDisplay;
    // cursor that appears in the track, directly above the knob
    private Cursor _cursor;
    // handles dragging of the knob
    private ConstrainedDragHandler _dragHandler;
    // for notification of listeners
    private EventListenerList _listenerList;
    // the current wavelength value displayed by this control
    private double _wavelength;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Creates a wavelength control for the visible spectrum.
     * 
     * @param canvas used by PSwing to wrap Swing components
     */
    public WavelengthControl( PSwingCanvas canvas ) {
        this( canvas, VisibleColor.MIN_WAVELENGTH, VisibleColor.MAX_WAVELENGTH, 
                UV_TRACK_COLOR, UV_LABEL_COLOR,
                IR_TRACK_COLOR, IR_LABEL_COLOR );
    }
    
    /**
     * Creates a wavelength control for a specified range of wavelengths.
     * Default colors are used for UV and IR ranges.
     * 
     * @param canvas used by PSwing to wrap Swing components
     * @param minWavelength minimum wavelength, in nanometers
     * @param maxWavelength maximum wavelength, in nanometers
     */
    public WavelengthControl( PSwingCanvas canvas, double minWavelength, double maxWavelength ) {
        this( canvas, minWavelength, maxWavelength, 
                UV_TRACK_COLOR, UV_LABEL_COLOR,
                IR_TRACK_COLOR, IR_LABEL_COLOR );
    }
    
    /**
     * Creates a wavelength control for a specified range of wavelengths.
     * Specified colors are used for UV and IR ranges.
     * 
     * @param canvas used by PSwing to wrap Swing components
     * @param minWavelength minimum wavelength, in nanometers
     * @param maxWavelength maximum wavelength, in nanometers
     * @param uvTrackColor color used for UV label
     * @param uvLabelColor color used for UV track
     * @param irTrackColor color used for IR label
     * @param irLabelColor color used for IR track
     * @throws IllegalArgumentException if minWavelength >= maxWavelength
     * @throws UnsupportedOperationException if the entire visible spectrum is not included in wavelength range
     */
    public WavelengthControl( PSwingCanvas canvas, 
            double minWavelength, double maxWavelength, 
            Color uvTrackColor, Color uvLabelColor, 
            Color irTrackColor, Color irLabelColor ) {
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
        _uvColor = uvTrackColor;
        _irColor = irTrackColor;
        _listenerList = new EventListenerList();
        
        _knob = new Knob( KNOB_SIZE.width, KNOB_SIZE.height );
        _track = new Track( minWavelength, maxWavelength, uvTrackColor, uvLabelColor, irTrackColor, irLabelColor );
        _valueDisplay = new ValueDisplay( _canvas );
        _cursor = new Cursor( CURSOR_WIDTH, _track.getFullBounds().getHeight() );
        
        /* 
         * Put a border around the track.
         * We don't stroke the track itself because stroking the track will affect its bounds, 
         * and will thus affect the drag handle behavior.
         */
        PPath trackBorder = new PPath();
        trackBorder.setPathTo( new Rectangle2D.Double( 0, 0, _track.getFullBounds().getWidth(), _track.getFullBounds().getHeight() ) );
        trackBorder.setStroke( new BasicStroke( 1f ) );
        trackBorder.setStrokePaint( Color.BLACK );
        
        addChild( _track );
        addChild( trackBorder );
        addChild( _valueDisplay );
        addChild( _cursor );
        addChild( _knob );
        
        // Track position never changes and defines the origin.
        _track.setOffset( 0, 0 );
        
        // Track interactivity
        {
            _track.addInputEventListener( new CursorHandler() );
            _track.addInputEventListener( new PBasicInputEventHandler() {

                public void mousePressed( PInputEvent event ) {
                    handleTrackClick( event.getPositionRelativeTo( _track ) );
                }
            } );
        }
        
        // Knob interactivity
        {
            _dragHandler = new ConstrainedDragHandler() {
                public void mouseDragged( PInputEvent event ) {
                    super.mouseDragged( event );
                    handleKnobDrag();
                }
            };
            _dragHandler.setVerticalLockEnabled( true );
            _dragHandler.setTreatAsPointEnabled( true );
            updateDragBounds();
            _knob.addInputEventListener( _dragHandler );
            _knob.addInputEventListener( new CursorHandler() );

            // Adjust the knob's drag bounds if this control's bounds are changed.
            addPropertyChangeListener( new PropertyChangeListener() {
                public void propertyChange( PropertyChangeEvent event ) {
                    if ( PNode.PROPERTY_FULL_BOUNDS.equals( event.getPropertyName() ) ) {
                        updateDragBounds();
                    }
                }
            } );
        }
        
        // Default state
        setWavelength( _minWavelength );
    }
    
    //----------------------------------------------------------------------------
    // Mutators
    //----------------------------------------------------------------------------
    
    /**
     * Sets the wavelength.
     * 
     * @param wavelength wavelength in nanometers
     * @throws IllegalArgumentException if wavelength is outside of min/max range
     */
    public void setWavelength( double wavelength ) {
       
        if ( wavelength < _minWavelength || wavelength > _maxWavelength ) {
            throw new IllegalArgumentException( "wavelength out of range: " + wavelength );
        }
        
        if ( wavelength != _wavelength ) {
            _wavelength = wavelength;
            updateUI();
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
    
    /**
     * Gets the color that corresponds to a specified wavelength.
     * 
     * @param wavelength the wavelength, in nanometers
     * @return Color
     */
    public Color getWavelengthColor( double wavelength ) {
        Color color = null;
        if ( wavelength < VisibleColor.MIN_WAVELENGTH ) {
            color = _uvColor;
        }
        else if ( wavelength > VisibleColor.MAX_WAVELENGTH ) {
            color = _irColor;
        }
        else {
            color = VisibleColor.wavelengthToColor( wavelength );
        }
        return color;
    }
    
    /**
     * Gets the color that corresponds to the selected wavelength.
     * 
     * @return Color
     */
    public Color getWavelengthColor( ) {
        return getWavelengthColor( _wavelength );
    }
    
    /**
     * Sets the foreground and background colors of the 
     * text field used to display the current value.
     * 
     * @param color
     */
    public void setTextFieldColors( Color foreground, Color background ) {
        _valueDisplay.getFormattedTextField().setForeground( foreground );
        _valueDisplay.getFormattedTextField().setBackground( background );
    }
    
    /**
     * Sets the font of the text field used to display the current value.
     * 
     * @param font
     */
    public void setTextFieldFont( Font font ) {
        _valueDisplay.getFormattedTextField().setFont( font );
        _valueDisplay.computeBounds();
        updateUI();
    }
    
    /**
     * Set the number of columns in the editable text field.
     * @param columns
     */
    public void setTextFieldColumns( int columns ) {
        _valueDisplay.getFormattedTextField().setColumns( columns );
        _valueDisplay.computeBounds();
        updateUI();
    }
    
    /**
     * Sets the foreground color of the units label that appears to
     * the right of the text field that displays the current value.
     * The background is transparent.
     * 
     * @param color
     */
    public void setUnitsForeground( Color color ) {
        _valueDisplay.getUnitsLabel().setForeground( color );
    }
    
    /**
     * Sets the font of the units label that appears to
     * the right of the text field that displays the current value.
     * 
     * @param font
     */
    public void setUnitsFont( Font font ) {
        _valueDisplay.getUnitsLabel().setFont( font );
        _valueDisplay.computeBounds();
        updateUI();
    }
    
    /**
     * Sets the color of the cursor that appears above the slider knob.
     * 
     * @param color
     */
    public void setCursorColor( Color color ) {
        _cursor.setStrokePaint( color );
    }
    
    /**
     * Sets the stroke used to outline the slider knob.
     * @return
     */
    public void setKnobStroke( Stroke stroke ) {
        _knob.setStroke( stroke );
    }
    
    /**
     * Sets the color used to stroke the slider knob.
     * @return
     */
    public void setKnobStrokeColor( Color strokeColor ) {
        _knob.setStrokePaint( strokeColor );
    }
    
    /**
     * Adds a listener to the knob, so we tell when we're dragging it.
     * @param listener
     */
    public void addKnobListener( PInputEventListener listener ) {
        _knob.addInputEventListener( listener );
    }
    
    //----------------------------------------------------------------------------
    // Private methods
    //----------------------------------------------------------------------------
    
    /*
     * Calculates the wavelength that corresponds to the knob position.
     */
    private double calculateWavelength() {
        final double bandwidth = _maxWavelength - _minWavelength;
        PBounds trackBounds = _track.getFullBounds();
        PBounds knobBounds = _knob.getFullBounds();
        final double trackX = trackBounds.getX();
        final double trackWidth = trackBounds.getWidth();
        final double knobTipX = knobBounds.getX() + ( knobBounds.getWidth() / 2 );
        final double wavelength = _minWavelength + ( ( ( knobTipX - trackX ) / trackWidth ) * bandwidth );
        return wavelength;
    }
    
    /*
     * Handles dragging of the knob.
     */
    private void handleKnobDrag() {
        double wavelength = calculateWavelength();
        wavelength = MathUtil.clamp( _minWavelength, wavelength, _maxWavelength );
        setWavelength( wavelength );
    }
    
    /*
     * Handles entry of values in the text field.
     */
    private void handleTextEntry() {
        final double wavelength = _valueDisplay.getValue();
        if ( wavelength >= _minWavelength && wavelength <= _maxWavelength ) {
            setWavelength( wavelength );
        }
        else {
            warnUser();
            _valueDisplay.setValue( _wavelength ); // revert
            _valueDisplay.selectAll();
        }
    }
    
    /*
     * Handles a mouse click on the track.
     */
    private void handleTrackClick( Point2D trackPoint ) {
        final double bandwidth = _maxWavelength - _minWavelength;
        final double trackWidth = _track.getFullBounds().getWidth();
        final double wavelength = _minWavelength + ( ( trackPoint.getX() / trackWidth ) * bandwidth );
        setWavelength( wavelength );
    }
    
    /*
     * Updates the UI to match a the current wavelength.
     */
    private void updateUI() {
        
        final double bandwidth = _maxWavelength - _minWavelength;
        
        PBounds trackBounds = _track.getFullBounds();
        final double knobWidth = _knob.getFullBounds().getWidth();
        final double cursorWidth = _cursor.getFullBounds().getWidth();
        final double valueDisplayWidth = _valueDisplay.getFullBounds().getWidth();
        final double valueDisplayHeight = _valueDisplay.getFullBounds().getHeight();
        
        // Knob color
        Color wavelengthColor = getWavelengthColor();
        _knob.setPaint( wavelengthColor );
        _cursor.setPaint( wavelengthColor );
    
        // Knob position: below the track with tip positioned at wavelength
        final double trackX = trackBounds.getX();
        final double trackWidth = trackBounds.getWidth();
        final double knobX = trackX + ( trackWidth * ( ( _wavelength - _minWavelength ) / bandwidth ) ) - ( knobWidth / 2 );
        final double knobY = trackBounds.getHeight();
        _knob.setOffset( knobX, knobY );
    
        // Value display: above the track, centered above the knob
        _valueDisplay.setValue( _wavelength );
        final double valueX = knobX + ( knobWidth / 2 ) - ( valueDisplayWidth / 2 );
        final double valueY = -( valueDisplayHeight + VALUE_Y_OFFSET );
        _valueDisplay.setOffset( valueX, valueY );

        // Cursor position: inside the track, centered above the knob
        final double cursorX = knobX + ( knobWidth / 2 ) - ( cursorWidth / 2 );
        final double cursorY = 0;
        _cursor.setOffset( cursorX, cursorY );
    }
    
    /**
     * Updates drag bounds for the knob.
     * <p>
     * HACK: This is public because of a problem with ConstrainedDragHandler.
     * ConstrainedDragHandler works in global coordinates, but WavelengthControl
     * has no way of knowing when its global position has changed. 
     * So you will need to call this method explicitly if you do something
     * to change the global position (eg, call setOffset on some ancestor node).
     */
    public void updateDragBounds() {
        PBounds trackGFB = _track.getGlobalFullBounds();
        PBounds knobGFB = _knob.getGlobalFullBounds();
        Rectangle2D dragBounds = new Rectangle2D.Double( trackGFB.getX() - (knobGFB.getWidth()/2), trackGFB.getY(), trackGFB.getWidth(), trackGFB.getHeight() );
        _dragHandler.setDragBounds( dragBounds );
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
        
        /* Constructor */
        public Knob( float width, float height ) {
            super();
            
            GeneralPath path = new GeneralPath();
            path.moveTo( 0.5f * width, 0f ); // tip of the knob
            path.lineTo( width, 0.3f * height );
            path.lineTo( width, 1f * height );
            path.lineTo( 0f, 1f * height );
            path.lineTo( 0f, 0.3f * height );
            path.closePath();
            setPathTo( path );
            
            setStroke( KNOB_STROKE );
            setPaint( KNOB_STROKE_COLOR );
        }
    }
    
    /*
     * The track that the slider knob moves in.
     */
    private static class Track extends PComposite {
        
        /* Constructor */
        public Track( double minWavelength, double maxWavelength,  
                Color uvTrackColor, Color uvLabelColor, 
                Color irTrackColor, Color irLabelColor ) {
            super();
            
            /* Portion of the track that represents visible wavelengths */
            Image spectrumImage = SpectrumImageFactory.createHorizontalSpectrum( SPECTRUM_WIDTH, SPECTRUM_HEIGHT );
            PImage spectrumTrack = new PImage( spectrumImage );
            final double spectrumTrackWidth = spectrumTrack.getFullBounds().getWidth();
            final double spectrumTrackHeight = spectrumTrack.getFullBounds().getHeight();
            
            final double visibleBandwidth = VisibleColor.MAX_WAVELENGTH - VisibleColor.MIN_WAVELENGTH;
            final double uvBandwidth = VisibleColor.MIN_WAVELENGTH - minWavelength;
            final double irBandwith = maxWavelength - VisibleColor.MAX_WAVELENGTH;
            final double uvTrackWidth = ( uvBandwidth / visibleBandwidth ) * spectrumTrackWidth;
            final double irTrackWidth = ( irBandwith / visibleBandwidth ) * spectrumTrackWidth;
            
            final boolean hasUV = ( uvTrackWidth > 0 );
            final boolean hasIR = ( irTrackWidth > 0 );
            
            /* Portion of the track that represents ultra-violet (UV) wavelengths */
            PPath uvTrack = null;
            PText uvLabel = null;
            if ( hasUV ) {
                
                uvTrack = new PPath();
                uvTrack.setPathTo( new Rectangle.Double( 0, 0, uvTrackWidth, spectrumTrackHeight ) );
                uvTrack.setPaint( uvTrackColor );
                uvTrack.setStroke( null );
                
                uvLabel = new PText( UV_STRING );
                uvLabel.setTextPaint( uvLabelColor );
            }
            
            /* Portion of the track that represents infra-red (IR) wavelengths */
            PPath irTrack = null;
            PText irLabel = null;
            if ( hasIR ) {
                irTrack = new PPath();
                irTrack.setPathTo( new Rectangle.Double( 0, 0, irTrackWidth, spectrumTrackHeight ) );
                irTrack.setPaint( irTrackColor );
                irTrack.setStroke( null );

                irLabel = new PText( IR_STRING );
                irLabel.setTextPaint( irLabelColor );
            }
            
            // Layering
            if ( hasUV ) {
                addChild( uvTrack );
                addChild( uvLabel );
            }
            if ( hasIR ) {
                addChild( irTrack );
                addChild( irLabel );
            }
            addChild( spectrumTrack );

            // Positioning
            if ( !hasUV ) {
                // If there is no UV, the spectrum is at the far left.
                spectrumTrack.setOffset( 0, 0 );
            }
            else {
                // UV track is at the far left
                uvTrack.setOffset( 0, 0 );
                // UV label is centered in the UV track
                uvLabel.setOffset( uvTrack.getFullBounds().getX() + ( ( uvTrack.getFullBounds().getWidth() -  uvLabel.getFullBounds().getWidth() ) / 2 ), 
                        ( uvTrack.getFullBounds().getHeight() - uvLabel.getFullBounds().getHeight() ) / 2 );
                // Remove the UV label if the UV track isn't wide enough to contain it
                if ( uvTrack.getFullBounds().getWidth() < uvLabel.getFullBounds().getWidth() ) {
                    removeChild( uvLabel );
                }
                // Spectrum track is to the right of the UV track
                spectrumTrack.setOffset( uvTrack.getFullBounds().getX() + uvTrack.getFullBounds().getWidth(), 0 );
            }

            if ( hasIR ) {
                // IR track is to the right of the spectrum track
                irTrack.setOffset( spectrumTrack.getFullBounds().getX() + spectrumTrack.getFullBounds().getWidth(), 0 );
                // IR label is centered in the UV track
                irLabel.setOffset( irTrack.getFullBounds().getX() + ( ( irTrack.getFullBounds().getWidth() -  irLabel.getFullBounds().getWidth() ) / 2 ), 
                        ( irTrack.getFullBounds().getHeight() - irLabel.getFullBounds().getHeight() ) / 2 );
                // Remove the IR label if the IR track isn't wide enough to contain it
                if ( irTrack.getFullBounds().getWidth() < irLabel.getFullBounds().getWidth() ) {
                    removeChild( irLabel );
                }
            }
        }
    }
    
    /*
     * Displays and edits the wavelength value as text.
     */
    private class ValueDisplay extends PNode {
        
        private JFormattedTextField _formattedTextField;
        private JLabel _unitsLabel;
        private PSwing _pswing;
        
        /* Constructor */
        public ValueDisplay( PSwingCanvas canvas ) {
            super();
            
            /* units label, appears to the right of the text field */
            _unitsLabel = new JLabel( UNITS_LABEL );
            
            /* editable text field */
            _formattedTextField = new JFormattedTextField();
            _formattedTextField.setColumns( TEXT_FIELD_COLUMNS );
            _formattedTextField.setHorizontalAlignment( JTextField.RIGHT );
            
            // text entry
            _formattedTextField.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    handleTextEntry();
                }
            } );
            
            // focus
            _formattedTextField.addFocusListener( new FocusListener() {
                /* Selects the entire value text field when it gains focus. */
                public void focusGained( FocusEvent e ) {
                    _formattedTextField.selectAll();
                }
                /* Processes the text field when it loses focus. */
                public void focusLost( FocusEvent e ) {
                    try {
                        _formattedTextField.commitEdit();
                        handleTextEntry();
                    }
                    catch ( ParseException pe ) {
                        warnUser();
                        setValue( _wavelength ); // revert
                    }
                }
            } );
            
            // up/down arrow keys
            _formattedTextField.addKeyListener( new KeyAdapter() { 
                public void keyPressed( KeyEvent event ) {
                    if ( event.getKeyCode() == KeyEvent.VK_UP ) {
                        setWavelength( _wavelength + 1 );
                    }
                    else if ( event.getKeyCode() == KeyEvent.VK_DOWN ) {
                        setWavelength( _wavelength - 1 );
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
            layout.addComponent( _unitsLabel, row, col );
            
            // Opacity
            panel.setOpaque( false );
            _unitsLabel.setOpaque( false );
            
            // Piccolo wrapper
            _pswing = new PSwing( canvas, panel );
            addChild( _pswing );
        }
        
        /* Sets the value displayed by the text field. */
        public void setValue( double wavelength ) {
            String s = VALUE_FORMAT.format( wavelength );
            _formattedTextField.setText( s );
        }
        
        /* Gets the value displayed by the text field. */
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
        
        /* Gets a reference to the units JLabel, for setting its properties. */
        public JLabel getUnitsLabel() {
            return _unitsLabel;
        }
        
        /* Gets a reference to the formatted text field, for setting its properties. */
        public JFormattedTextField getFormattedTextField() {
            return _formattedTextField;
        }
        
        /* Selects the entire text field */
        public void selectAll() {
            _formattedTextField.selectAll();
        }
        
        /* Call this after doing something that changes the size of a Swing component */
        public void computeBounds() {
            _pswing.computeBounds();
        }
    }
    
    /* 
     * Rectangular "cursor" that appears in the track directly above the knob.
     */
    private static class Cursor extends PPath {
        
        /* Constructor */
        public Cursor( double width, double height ) {
            super();
            setPathTo( new Rectangle2D.Double( 0, 0, width, height ) );
            setStroke( CURSOR_STROKE );
            setStrokePaint( CURSOR_COLOR );
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
