/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.view;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.graphics.shapes.Arrow;
import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.view.phetgraphics.*;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.model.FourierComponent;


/**
 * AmplitudeSlider
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class AmplitudeSlider extends GraphicLayerSet implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Layers
    private static final double TRACK_LAYER = 1;
    private static final double KNOB_LAYER = 2;
    private static final double VALUE_LAYER = 3;
    private static final double LABEL_LAYER = 4;

    // Label parameters
    private static final Color LABEL_COLOR = Color.BLACK;
    private static final Font LABEL_FONT = new Font( "Lucida Sans", Font.PLAIN, 12 );
    private static final Font LABEL_SUBSCRIPT_FONT = new Font( "Lucida Sans", Font.PLAIN, 12 );
    private static final int LABEL_Y_OFFSET = 45; // above the maximum height of the slider track
    
    // Value parameters
    private static final String VALUE_FORMAT = "0.00";
    private static final int VALUE_COLUMNS = 3;
    private static final Font VALUE_FONT = new Font( "Lucida Sans", Font.PLAIN, 12 );
    private static final int VALUE_Y_OFFSET = 25; // above the maximum height of the slider track

    // Knob parameters
    private static final Color KNOB_FILL_COLOR = Color.BLACK;
    private static final Color KNOB_HIGHLIGHT_COLOR = Color.RED;
    private static final Color KNOB_STROKE_COLOR = Color.BLACK;
    private static final Stroke KNOB_STROKE = new BasicStroke( 1f );
    private static final double ARROW_LENGTH = 15;
    private static final double ARROW_HEAD_HEIGHT = 10;
    private static final double ARROW_HEAD_WIDTH = 10;
    private static final double ARROW_TAIL_WIDTH = 3;
    private static final double ARROW_FRACTIONAL_HEAD_HEIGHT = 5;

    // Track parameters
    private static final Dimension DEFAULT_TRACK_SIZE = new Dimension( 40, 100 );
    private static final Color DEFAULT_TRACK_COLOR = Color.WHITE;
    private static final Stroke TRACK_STROKE = new BasicStroke( 1f );
    private static final Color TRACK_BORDER_COLOR = Color.BLACK;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private FourierComponent _fourierComponentModel;
    private Dimension _maxSize;
    private CompositePhetGraphic _labelGraphic;
    private PhetGraphic _valueGraphic;
    private JTextField _valueTextField;
    private NumberFormat _valueFormatter;
    private PhetShapeGraphic _trackGraphic;
    private Rectangle _trackRectangle;
    private Color _trackColor;
    private KnobGraphic _knobGraphic;
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param component the parent Component
     * @param fourierComponentModel the model that this slider controls
     */
    public AmplitudeSlider( Component component, FourierComponent fourierComponentModel ) {
        super( component );

        assert ( fourierComponentModel != null );
        _fourierComponentModel = fourierComponentModel;
        _fourierComponentModel.addObserver( this );

        _maxSize = new Dimension( DEFAULT_TRACK_SIZE );

        // Label (An)
        {          
            _labelGraphic = new CompositePhetGraphic( component );
            _labelGraphic.centerRegistrationPoint();
            _labelGraphic.setLocation( 0, 0 );
            
            PhetTextGraphic aGraphic = new PhetTextGraphic( component, LABEL_FONT, "A", LABEL_COLOR );
            aGraphic.setJustification( PhetTextGraphic.SOUTH_EAST );
            aGraphic.setLocation( 0, 0 );
            _labelGraphic.addGraphic( aGraphic );
            
            String subscript = String.valueOf( _fourierComponentModel.getOrder() + 1 );
            PhetTextGraphic subscriptGraphic = new PhetTextGraphic( component, LABEL_SUBSCRIPT_FONT, subscript, LABEL_COLOR );
            subscriptGraphic.setJustification( PhetTextGraphic.WEST );
            subscriptGraphic.setLocation( 0, 0);
            _labelGraphic.addGraphic( subscriptGraphic );
        }
        
        // Value
        {
            _valueFormatter = new DecimalFormat( VALUE_FORMAT );
            _valueTextField = new JTextField( _valueFormatter.format( 0.0 ) );
            _valueTextField.setFont( VALUE_FONT );
            _valueTextField.setColumns( VALUE_COLUMNS );
            _valueGraphic = PhetJComponent.newInstance( component, _valueTextField );
            _valueGraphic.centerRegistrationPoint();
            _valueGraphic.setLocation( 0, 0 );
        }

        // Slider Track
        {
            _trackRectangle = new Rectangle();
            _trackColor = DEFAULT_TRACK_COLOR;
            _trackGraphic = new PhetShapeGraphic( component );
            _trackGraphic.setLocation( 0, 0 );
            _trackGraphic.setShape( _trackRectangle );
            _trackGraphic.setPaint( _trackColor );
            _trackGraphic.setBorderColor( TRACK_BORDER_COLOR );
            _trackGraphic.setStroke( TRACK_STROKE );
        }

        // Slider Knob
        {
            _knobGraphic = new KnobGraphic( component );
            _knobGraphic.setLocation( 0, 0 );
        }

        // Interactivity
        {
            _labelGraphic.setIgnoreMouse( true );
            
            ValueEventListener valueListener = new ValueEventListener();
            _valueTextField.addActionListener( valueListener );
            _valueTextField.addFocusListener( valueListener );
            _valueTextField.addKeyListener( valueListener ); // HACK: unnecessary when ActionEvents and FocusEvents are fixed
            
            _trackGraphic.setIgnoreMouse( true );
            
            _knobGraphic.setCursorHand();
            _knobGraphic.addMouseInputListener( new KnobEventListener() );
        }

        addGraphic( _labelGraphic, LABEL_LAYER );
        addGraphic( _valueGraphic, VALUE_LAYER );
        addGraphic( _trackGraphic, TRACK_LAYER );
        addGraphic( _knobGraphic, KNOB_LAYER );

        // Enable antialiasing for all children.
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );

        update();
    }
    
    /**
     * Finalizes an instance of this type.
     * Call this method prior to releasing all references to an object of this type.
     */
    public void finalize() {
        _fourierComponentModel.removeObserver( this );
        _fourierComponentModel = null;
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the model that this slider controls.
     * 
     * @param fourierComponentModel
     */
    public void setModel( FourierComponent fourierComponentModel ) {
        if ( fourierComponentModel != _fourierComponentModel ) {
            _fourierComponentModel = fourierComponentModel;
            update();
        }
    }
    
    /**
     * Gets the model that this slider controls.
     * 
     * @return the model
     */
    public FourierComponent getModel() {
        return _fourierComponentModel;
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
        if ( width != _maxSize.width || height != _maxSize.height ) {
            _maxSize.setSize( width, height );
            update();
        }      
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
     * Sets the color of the slider track.
     * 
     * @param trackColor the track color
     */
    public void setTrackColor( Color trackColor ) {
        if ( ! trackColor.equals( _trackColor ) ) {
            _trackColor = trackColor;
            update();
        }
    }
    
    /**
     * Gets the color of the slider track.
     * 
     * @return the track color
     */
    public Color getTrackColor() {
        return _trackColor;
    }
    
    //----------------------------------------------------------------------------
    // User input processing
    //----------------------------------------------------------------------------
    
    /**
     * Processes the contents of the value field.
     * 
     * @return true if the value is valid, false otherwise
     */
    private boolean processUserInput() {
        boolean success = false;
        String stringValue = _valueTextField.getText();
        double amplitude = 0.0;
        try {
            Double doubleValue = new Double( stringValue );
            amplitude = doubleValue.doubleValue();
        }
        catch ( NumberFormatException nfe ) {
            showUserInputErrorDialog();
            update();
        }
        if ( amplitude > 1.0 || amplitude < -1.0 ) {
            showUserInputErrorDialog();
            update();
        }
        else {
            _fourierComponentModel.setAmplitude( amplitude );
            success = true;
        }
        return success;
    }
    
    /**
     * Displays a modal error dialog for invalid user inputs.
     */
    private void showUserInputErrorDialog() {
        String message = SimStrings.get( "AmplitudeSlider.inputErrorMessage" );
        JOptionPane op = new JOptionPane( message, JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION );
        op.createDialog( getComponent(), null ).show();
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    /**
     * Synchronizes the view with the model.
     */
    public void update() {
        
        double amplitude = _fourierComponentModel.getAmplitude();
        
        // Label location
        _labelGraphic.setLocation( 0, -( ( _maxSize.height / 2 ) + LABEL_Y_OFFSET ) );
        
        // Value
        _valueTextField.setText( _valueFormatter.format( amplitude ) );
        _valueGraphic.setLocation( 0, -( ( _maxSize.height / 2 ) + VALUE_Y_OFFSET ) );
        _valueGraphic.repaint(); // HACK: If this isn't here, then the first value displayed is out of sync.
        
        // Track size and color
        int trackWidth = _maxSize.width;
        int trackHeight = (int) Math.abs( ( _maxSize.height / 2 ) * amplitude );
        int trackX = -trackWidth / 2;
        int trackY = ( amplitude > 0 ) ? -trackHeight : 0;
        _trackRectangle.setBounds( trackX, trackY, trackWidth, trackHeight );
        _trackGraphic.setShape( _trackRectangle );
        _trackGraphic.setPaint( _trackColor );
        
        // Knob location
        int knobX = _knobGraphic.getX();
        int knobY = (int) -( ( _maxSize.height / 2 ) * _fourierComponentModel.getAmplitude() );
        _knobGraphic.setLocation( knobX, knobY );
        
        repaint();
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /**
     * KnobGraphic is the graphic for the slider's knob.
     */
    private class KnobGraphic extends CompositePhetGraphic {
        
        PhetShapeGraphic _topArrowGraphic;
        PhetShapeGraphic _bottomArrowGraphic;
        
        /**
         * Sole constructor.
         * 
         * @param component the parent Component
         */
        public KnobGraphic( Component component ) {
            super( component );
            
            Point2D tail = new Point2D.Double( 0, 0 );
            Point2D topTip = new Point2D.Double( 0, -ARROW_LENGTH );
            Point2D bottomTip = new Point2D.Double( 0, ARROW_LENGTH );
            Arrow topArrow = new Arrow( tail, topTip, ARROW_HEAD_HEIGHT, ARROW_HEAD_WIDTH, ARROW_TAIL_WIDTH, ARROW_FRACTIONAL_HEAD_HEIGHT, false );
            Arrow bottomArrow = new Arrow( tail, bottomTip, ARROW_HEAD_HEIGHT, ARROW_HEAD_WIDTH, ARROW_TAIL_WIDTH, ARROW_FRACTIONAL_HEAD_HEIGHT, false );
            
            _topArrowGraphic = new PhetShapeGraphic( getComponent() );
            _topArrowGraphic.setShape( topArrow.getShape() );
            _topArrowGraphic.setPaint( KNOB_FILL_COLOR );
            _topArrowGraphic.setBorderColor( KNOB_STROKE_COLOR );
            _topArrowGraphic.setStroke( KNOB_STROKE );
            addGraphic( _topArrowGraphic );
            
            _bottomArrowGraphic = new PhetShapeGraphic( getComponent() );
            _bottomArrowGraphic.setShape( bottomArrow.getShape() );
            _bottomArrowGraphic.setPaint( KNOB_FILL_COLOR );
            _bottomArrowGraphic.setBorderColor( KNOB_STROKE_COLOR );
            _bottomArrowGraphic.setStroke( KNOB_STROKE );
            addGraphic( _bottomArrowGraphic );
        }
        
        /**
         * Enables and disables highlighting of the knob.
         * 
         * @param enabled true or false
         */
        public void setHighlightEnabled( boolean enabled ) {
            if ( enabled ) {
                _topArrowGraphic.setPaint( KNOB_HIGHLIGHT_COLOR );
                _bottomArrowGraphic.setPaint( KNOB_HIGHLIGHT_COLOR );
            }
            else {
                _topArrowGraphic.setPaint( KNOB_FILL_COLOR );
                _bottomArrowGraphic.setPaint( KNOB_FILL_COLOR );
            }
        }
    }
    
    /**
     * KnobEventListener handles events related to the slider knob.
     */
    private class KnobEventListener extends MouseInputAdapter {
        
        private Point _somePoint;
        
        /**
         * Sole constructor.
         */
        public KnobEventListener() {
            super();
            _somePoint = new Point();
        }
        
        /**
         * Handles mouse drags.
         * 
         * @param event
         */
        public void mouseDragged( MouseEvent event ) {
            
            int mouseY = 0;
            try {
                AffineTransform transform = getNetTransform();
                transform.inverseTransform( event.getPoint(), _somePoint /* output */ );
                mouseY = (int) _somePoint.getY();
            }
            catch ( NoninvertibleTransformException e ) {
                e.printStackTrace();
            }
            
            double amplitude = (double) mouseY / ( _maxSize.height / 2.0 );
            amplitude = MathUtil.clamp( -1, amplitude, +1 );
            _fourierComponentModel.setAmplitude( -amplitude );
        }
        
        /**
         * Highlights the knob when the mouse cursor enters it.
         * 
         * @param event
         */
        public void mouseEntered( MouseEvent event ) {
            _knobGraphic.setHighlightEnabled( true );
        }
        
        /**
         * Unhighlights the knob when the mouse cursor exits it.
         * 
         * @param event
         */
        public void mouseExited( MouseEvent event ) {
            _knobGraphic.setHighlightEnabled( false );
        }
    }
    
    /**
     * ValueEventListener handles events related to the value input field.
     */
    private class ValueEventListener extends FocusAdapter implements ActionListener, KeyListener {
        
        /** Sole constructor. */
        public ValueEventListener() {}
        
        /**
         * Processes the input value when focus is lost.
         * 
         * @param event
         */
        public void focusLost( FocusEvent event ) {
            System.out.println( "HarmonicSlider.ValueEventListener.focusLost" );//DEBUG
            if ( ! event.isTemporary() ) {
                boolean success = processUserInput();
                if ( !success ) {
                    _valueTextField.requestFocus();
                }
            }
        }
        
        /**
         * Processes the input value when the user presses the Enter key.
         * 
         * @param event
         */
        public void actionPerformed( ActionEvent event ) {
            System.out.println( "HarmonicSlider.ValueEventListener.actionPerformed" );//DEBUG
            if ( event.getSource() == _valueTextField ) {
                processUserInput();
            }        
        }

        // HACK: KeyListener will be unnecessary when ActionEvents and FocusEvents are fixed.
        public void keyTyped( KeyEvent event ) {}

        public void keyPressed( KeyEvent event ) {}

        /**
         * Processes the input value when the user presses the Enter key.
         * 
         * @param event
         */
        public void keyReleased( KeyEvent event ) {
            if ( event.getKeyChar() == KeyEvent.VK_ENTER ) {
                processUserInput();
            }
        } 
    }
}
