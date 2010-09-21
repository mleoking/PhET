/* Copyright 2005-2009, University of Colorado */

package edu.colorado.phet.opticalquantumcontrol.view;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetgraphics.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.HTMLGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.opticalquantumcontrol.OQCConstants;
import edu.colorado.phet.opticalquantumcontrol.OQCResources;
import edu.colorado.phet.opticalquantumcontrol.model.Harmonic;


/**
 * AmplitudeSlider is a slider for controlling harmonic amplitude.
 * The user can change the amplitude by (1) clicking anywhere above,
 * below or on the colored section of the slider, (2) click and drag
 * the knob, (3) typing a value into the text field, or (4) selecting
 * the text field and using the up/down arrow keys.
 * <br>
 * The clickable area (aka, the click zone) is indicated by a hand
 * cursor.  The knob is indicated by an "up down" cursor.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class AmplitudeSlider extends GraphicLayerSet implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Graphics layers
    private static final double CLICK_ZONE_LAYER = 1;
    private static final double TRACK_LAYER = 2;
    private static final double KNOB_LAYER = 3;
    private static final double VALUE_LAYER = 4;
    private static final double LABEL_LAYER = 5;

    private static final double MAX_AMPLITUDE = OQCConstants.MAX_HARMONIC_AMPLITUDE;
    
    // Text field (value) parameters
    private static final double VALUE_STEP = 0.01;
    private static final String VALUE_FORMAT = "0.00";
    private static final int VALUE_COLUMNS = 3;
    private static final Font VALUE_FONT = new PhetFont( Font.PLAIN, 12 );
    private static final int VALUE_Y_OFFSET = 17; // above the maximum height of the slider track
    
    // Label parameters
    private static final Color LABEL_COLOR = Color.BLACK;
    private static final Font LABEL_FONT = new PhetFont( Font.PLAIN, 12 );
    private static final int LABEL_Y_OFFSET = VALUE_Y_OFFSET + 18; // above the maximum height of the slider track

    // Knob parameters
    private static final Color KNOB_FILL_COLOR = Color.BLACK;

    // Track parameters
    private static final Dimension DEFAULT_TRACK_SIZE = new Dimension( 40, 100 );
    private static final Color DEFAULT_TRACK_COLOR = Color.WHITE;
    private static final Stroke TRACK_STROKE = new BasicStroke( 1f );
    private static final Color TRACK_BORDER_COLOR = Color.BLACK;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Harmonic _harmonic;
    private Dimension _maxSize;
    private HTMLGraphic _labelGraphic;
    private PhetGraphic _valueGraphic;
    private JTextField _valueTextField;
    private NumberFormat _valueFormatter;
    private PhetShapeGraphic _trackGraphic;
    private Rectangle _trackRectangle;
    private Color _trackColor;
    private PhetShapeGraphic _knobGraphic;
    private Rectangle _knobRectangle;
    private PhetShapeGraphic _clickZoneGraphic;
    private Rectangle _clickZoneRectangle;
    private Point _somePoint;
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param component the parent Component
     * @param harmonic the model that this slider controls
     */
    public AmplitudeSlider( Component component, Harmonic harmonic ) {
        super( component );

        // Enable antialiasing for all children.
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );

        // Model
        assert ( harmonic != null );
        _harmonic = harmonic;
        _harmonic.addObserver( this );

        // Misc initialization
        {
            _maxSize = new Dimension( DEFAULT_TRACK_SIZE );
            _somePoint = new Point();
        }

        // Label (An)
        {
            String subscript = String.valueOf( _harmonic.getOrder() + 1 );
            String label = "<html>A<sub>" + subscript + "</sub></html>";
            _labelGraphic = new HTMLGraphic( component, LABEL_FONT, label, LABEL_COLOR );
            _labelGraphic.centerRegistrationPoint();
            _labelGraphic.setLocation( 0, 0 ); // will be set in update
        }
        
        // Value (text field)
        {
            _valueFormatter = new DecimalFormat( VALUE_FORMAT );
            _valueTextField = new JTextField( _valueFormatter.format( 0.0 ) );
            _valueTextField.setFont( VALUE_FONT );
            _valueTextField.setColumns( VALUE_COLUMNS );
            _valueGraphic = PhetJComponent.newInstance( component, _valueTextField );
            _valueGraphic.setName( "AmplitudeSlider.value" );
            _valueGraphic.centerRegistrationPoint();
            _valueGraphic.setLocation( 0, 0 ); // will be set in update
        }

        // Click Zone
        {
           _clickZoneGraphic = new PhetShapeGraphic( component );
           _clickZoneRectangle = new Rectangle( 1, 1, _maxSize.width, _maxSize.height );
           _clickZoneGraphic.setShape( _clickZoneRectangle );
           _clickZoneGraphic.setName( "AmplitudeSlider.clickZone" );
           _clickZoneGraphic.setPaint( new Color( 0, 0, 0, 0 ) ); // transparent
           _clickZoneGraphic.centerRegistrationPoint();
           _clickZoneGraphic.setLocation( 0, 0 );
        }
        
        // Slider Track
        {
            _trackRectangle = new Rectangle();
            _trackColor = DEFAULT_TRACK_COLOR;
            _trackGraphic = new PhetShapeGraphic( component );
            _trackGraphic.setName( "AmplitudeSlider.track" );
            _trackGraphic.setShape( _trackRectangle );
            _trackGraphic.setPaint( _trackColor );
            _trackGraphic.setBorderColor( TRACK_BORDER_COLOR );
            _trackGraphic.setStroke( TRACK_STROKE );
            _trackGraphic.setLocation( 0, 0 );
        }

        // Slider Knob
        {
            _knobRectangle = new Rectangle( 1, 0, _maxSize.width + 1, 4 ); // account for track stroke
            _knobGraphic = new PhetShapeGraphic( component );
            _knobGraphic.setName( "AmplitudeSlider.knob" );
            _knobGraphic.setShape( _knobRectangle );
            _knobGraphic.setPaint( KNOB_FILL_COLOR );
            _knobGraphic.centerRegistrationPoint();
            _knobGraphic.setLocation( 0, 0 ); // will be set in update
        }
        
        // Interactivity
        {
            _labelGraphic.setIgnoreMouse( true );
            
            TextFieldEventListener textFieldListener = new TextFieldEventListener();
            _valueTextField.addActionListener( textFieldListener );
            _valueTextField.addFocusListener( textFieldListener );
            _valueTextField.addKeyListener( textFieldListener );
            
            _clickZoneGraphic.setCursorHand();
            _clickZoneGraphic.addMouseInputListener( new ClickZoneEventListener() );
            
            _knobGraphic.setCursorHand();
            _knobGraphic.addMouseInputListener( new KnobEventListener() );
            
            _trackGraphic.setCursorHand();
            _trackGraphic.addMouseInputListener( new TrackEventListener() );
        }

        addGraphic( _labelGraphic, LABEL_LAYER );
        addGraphic( _valueGraphic, VALUE_LAYER );
        addGraphic( _clickZoneGraphic, CLICK_ZONE_LAYER );
        addGraphic( _trackGraphic, TRACK_LAYER );
        addGraphic( _knobGraphic, KNOB_LAYER );
        
        update();
    }
    
    /**
     * Call this method prior to releasing all references to an object of this type.
     */
    public void cleanup() {
        _harmonic.removeObserver( this );
        _harmonic = null;
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the harmonic that this slider controls.
     * 
     * @param harmonic
     */
    public void setHarmonic( Harmonic harmonic ) {
        assert ( harmonic != null );
        if ( _harmonic != null ) {
            _harmonic.removeObserver( this );
        }
        _harmonic = harmonic;
        _harmonic.addObserver( this );
        update();
    }
    
    /**
     * Gets the harmonic that this slider controls.
     * 
     * @return the model
     */
    public Harmonic getHarmonic() {
        return _harmonic;
    }
    
    /**
     * Sets the maximum size of the slider track.
     * 
     * @param maxSize the maximum size
     */
    public void setMaxSize( Dimension maxSize ) {
        setMaxSize( maxSize );
    }
    
    /**
     * Sets the maximum size of the slider track.
     * 
     * @param width the width
     * @param height the height
     */
    public void setMaxSize( int width, int height ) {
        _maxSize.setSize( width, height );

        _clickZoneRectangle.setRect( 0, 0, _maxSize.width, _maxSize.height );
        _clickZoneGraphic.setShapeDirty();
        _clickZoneGraphic.centerRegistrationPoint();

        _knobRectangle.setRect( 1, 0, _maxSize.width + 1, 4 ); // account for track stroke
        _knobGraphic.setShapeDirty();
        _knobGraphic.centerRegistrationPoint();

        update();
    }
    
    /**
     * Gets the maximum size of the slider track.
     * 
     * @return the maximum size
     */
    public Dimension getMaxSize() {
        return new Dimension( _maxSize );
    }
    
    /**
     * Sets the color of the slider.
     * @param color
     */
    public void setColor( Color color ) {
        _trackGraphic.setPaint( color );
    }
    
    //----------------------------------------------------------------------------
    // User input processing
    //----------------------------------------------------------------------------
    
    /**
     * Processes the contents of the value text field.
     * 
     * @return true if the value is valid, false otherwise
     */
    private boolean processUserInput() {
        boolean success = true;
        String stringValue = _valueTextField.getText();
        double amplitude = 0.0;
        try {
            Double doubleValue = new Double( stringValue );
            amplitude = doubleValue.doubleValue();
        }
        catch ( NumberFormatException nfe ) {
            success = false;
            showUserInputErrorDialog();
            update();
        }
        if ( Math.abs( amplitude ) > MAX_AMPLITUDE ) {
            success = false;
            showUserInputErrorDialog();
            update();
        }
        else if ( amplitude != _harmonic.getAmplitude() ) {
            /*  
             * WORKAROUND:
             * The Game module may start a new game before we've updated the
             * slider, so update the slider here before changing the harmonic's
             * amplitude.
             */
            {
                Color color = HarmonicColors.getInstance().getColor( _harmonic );
                updateSlider( amplitude, color );
            }
            _harmonic.setAmplitude( amplitude );
        }
        return success;
    }
    
    /*
     * Displays a modal error dialog for invalid user inputs.
     */
    private void showUserInputErrorDialog() {
        String message = OQCResources.AMPLITUDE_ERROR_MESSAGE;
        JOptionPane op = new JOptionPane( message, JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION );
        op.createDialog( getComponent(), null ).setVisible( true );
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    /**
     * Synchronizes the view with the model.
     */
    public void update() {       
        double amplitude = _harmonic.getAmplitude();
        if ( amplitude == -0 ) { amplitude = 0; }  
        Color color = HarmonicColors.getInstance().getColor( _harmonic ); 
        updateSlider( amplitude, color );
    }
    
    /*
     * Updates the slider.
     * 
     * @param amplitude
     * @param color
     */
    private void updateSlider( double amplitude, Color color ) {
        
         // Label location
        _labelGraphic.setLocation( 0, -( ( _maxSize.height / 2 ) + LABEL_Y_OFFSET ) );
        
        // Value
        _valueTextField.setText( _valueFormatter.format( amplitude ) );
        _valueGraphic.setLocation( 0, -( ( _maxSize.height / 2 ) + VALUE_Y_OFFSET ) );
        
        // Track size
        int trackWidth = _maxSize.width;
        int trackHeight = (int) Math.abs( ( _maxSize.height / 2 ) * ( amplitude / MAX_AMPLITUDE ) );
        int trackX = -( trackWidth / 2 );
        int trackY = ( amplitude > 0 ) ? -trackHeight : 0;
        _trackRectangle.setBounds( trackX, trackY, trackWidth, trackHeight );
        _trackGraphic.setShapeDirty();
        _trackGraphic.setPaint( color );
        
        // Knob location
        int knobX = _knobGraphic.getX();
        int knobY = (int) -( ( _maxSize.height / 2 ) * ( amplitude / MAX_AMPLITUDE ) );
        _knobGraphic.setLocation( knobX, knobY );
        
        repaint();
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /*
     * ClickZoneEventListener handles mouse events related to the "click zone".
     */
    private class ClickZoneEventListener extends MouseInputAdapter {
        
        /* Sole constructor */
        public ClickZoneEventListener() {
            super();
        }
        
        /* Sets the harmonic's amplitude as the mouse is dragged. */
        public void mouseDragged( MouseEvent event ) {
            setAmplitude( event.getPoint() );
        }
        
        /* Sets the harmonic's amplitude when based on where the mouse is pressed. */
        public void mousePressed( MouseEvent event ) {
            setAmplitude( event.getPoint() );
        }
    }
    
    /*
     * KnobEventListener handles mouse events related to the knob.
     */
    private class KnobEventListener extends MouseInputAdapter {
        
        public KnobEventListener() {
            super();
        }
        
        /* Sets the harmonic's amplitude as the mouse is dragged. */
        public void mouseDragged( MouseEvent event ) {
            setAmplitude( event.getPoint() );
        }
    }
    
    /*
     * TrackEventListener handles mouse events related to the slider track.
     */
    private class TrackEventListener extends MouseInputAdapter {
        
        public TrackEventListener() {
            super();
        }
        
        /* Sets the harmonic's amplitude as the mouse is dragged. */
        public void mouseDragged( MouseEvent event ) {
            setAmplitude( event.getPoint() );
        }
        
        /* Sets the harmonic's ampltude when based on where the mouse is pressed. */
        public void mousePressed( MouseEvent event ) {
            setAmplitude( event.getPoint() );
        }
    }
    
    /* 
     * Sets the harmonic's amplitude based on the mouse location.
     */
    private void setAmplitude( Point mousePoint ) {
        double localY = 0;
        try {
            AffineTransform transform = getNetTransform();
            transform.inverseTransform( mousePoint, _somePoint /* output */ );
            localY = _somePoint.getY();
        }
        catch ( NoninvertibleTransformException e ) {
            e.printStackTrace();
        }
        localY = -localY; // +Y is up
        double amplitude = MAX_AMPLITUDE * ( localY / ( _maxSize.height / 2.0 ) );
        amplitude = MathUtil.clamp( -MAX_AMPLITUDE, amplitude, +MAX_AMPLITUDE );
        _harmonic.setAmplitude( amplitude );
    }
    
    /*
     * TextFieldEventListener handles key events related to the text field.
     */
    private class TextFieldEventListener extends KeyAdapter implements ActionListener, FocusListener {
        
        /* Sole constructor. */
        public TextFieldEventListener() {}
        
        /* Processes the input value when the user presses the Enter key. */
        public void actionPerformed( ActionEvent event ) {
            if ( event.getSource() == _valueTextField ) {
                processUserInput();
            }        
        }
        
        /* Selects the contents of the text field when focus is gained. */
        public void focusGained( FocusEvent event ) {
            _valueTextField.selectAll();
        }
        
        /* Processes the input value when focus is lost. */
        public void focusLost( FocusEvent event ) {
            if ( ! event.isTemporary() ) {
                boolean success = processUserInput();
                if ( !success ) {
                    _valueTextField.requestFocus();
                }
            }
        }
        
        /* Changes the amplitude value using the up/down arrow keys. */
        public void keyPressed( KeyEvent event ) {
            if ( event.getKeyCode() == KeyEvent.VK_UP ) {
                double amplitude = _harmonic.getAmplitude() + VALUE_STEP;
                if ( amplitude <= MAX_AMPLITUDE ) {
                    _harmonic.setAmplitude( amplitude );
                }
            }
            else if ( event.getKeyCode() == KeyEvent.VK_DOWN ) {
                double amplitude = _harmonic.getAmplitude() - VALUE_STEP;
                if ( amplitude >= -MAX_AMPLITUDE ) {
                    _harmonic.setAmplitude( amplitude ); 
                }
            }
        }
    }
}
