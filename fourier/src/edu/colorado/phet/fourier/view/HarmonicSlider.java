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

import javax.swing.JTextField;
import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.graphics.shapes.Arrow;
import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.view.phetgraphics.*;
import edu.colorado.phet.fourier.model.Harmonic;


/**
 * HarmonicAmplitudeSlider
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HarmonicSlider extends GraphicLayerSet implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Layers
    private static final double LABEL_LAYER = 1;
    private static final double VALUE_LAYER = 2;
    private static final double TRACK_LAYER = 3;
    private static final double KNOB_LAYER = 4;

    // Label
    private static final Font LABEL_FONT = new Font( "Lucida Sans", Font.PLAIN, 12 );
    private static final Color LABEL_COLOR = Color.BLACK;
    
    // Value
    private static final String VALUE_FORMAT = "0.00";
    private static final Font VALUE_FONT = new Font( "Lucida Sans", Font.PLAIN, 12 );
    
    // Knob
    private static final Color ARROW_FILL_COLOR = Color.BLACK;
    private static final Color ARROW_HIGHLIGHT_COLOR = Color.RED;
    private static final Color ARROW_STROKE_COLOR = Color.BLACK;
    private static final Stroke ARROW_STROKE = new BasicStroke( 1f );
    private static final double ARROW_LENGTH = 15;
    private static final double ARROW_HEAD_HEIGHT = 10;
    private static final double ARROW_HEAD_WIDTH = 10;
    private static final double ARROW_TAIL_WIDTH = 3;
    private static final double ARROW_FRACTIONAL_HEAD_HEIGHT = 5;

    // Track
    private static final int DEFAULT_TRACK_WIDTH = 40;
    private static final int DEFAULT_TRACK_HEIGHT = 100;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Harmonic _harmonicModel;
    private Dimension _maxSize;
    private Rectangle _trackRectangle;
    private Color _trackColor;
    private PhetTextGraphic _labelGraphic;
    private JTextField _valueTextField;
    private PhetGraphic _valueGraphic;
    private NumberFormat _valueFormatter;
    private PhetShapeGraphic _trackGraphic;
    private KnobGraphic _knobGraphic;
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------
    
    public HarmonicSlider( Component component, Harmonic harmonicModel ) {
        super( component );

        assert ( harmonicModel != null );
        _harmonicModel = harmonicModel;
        _harmonicModel.addObserver( this );

        _maxSize = new Dimension( DEFAULT_TRACK_WIDTH, DEFAULT_TRACK_HEIGHT );

        // Label
        {
            String text = "A" + ( _harmonicModel.getOrder() + 1 ); //XXX
            _labelGraphic = new PhetTextGraphic( component, LABEL_FONT, text, LABEL_COLOR );
            _labelGraphic.centerRegistrationPoint();
            _labelGraphic.setLocation( 0, 0 );
        }
        
        // Value
        {
            _valueFormatter = new DecimalFormat( VALUE_FORMAT );
            _valueTextField = new JTextField( _valueFormatter.format( 0.0 ) );
            _valueTextField.setFont( VALUE_FONT );
            _valueTextField.setColumns( 3 );
            _valueGraphic = PhetJComponent.newInstance( component, _valueTextField );
            _valueGraphic.centerRegistrationPoint();
            _valueGraphic.setLocation( 0, 0 );
        }

        // Slider track
        {
            _trackRectangle = new Rectangle();
            _trackColor = Color.RED;
            _trackGraphic = new PhetShapeGraphic( component );
            _trackGraphic.setLocation( 0, 0 );
            _trackGraphic.setShape( _trackRectangle );
            _trackGraphic.setPaint( _trackColor );
            _trackGraphic.setBorderColor( Color.BLACK );
            _trackGraphic.setStroke( new BasicStroke( 1f ) );
        }

        // Slider knob
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
    
    public void finalize() {
        _harmonicModel.removeObserver( this );
        _harmonicModel = null;
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setMaxSize( Dimension maxSize ) {
        setMaxSize( maxSize );
    }
    
    public void setMaxSize( int width, int height ) {
        if ( width != _maxSize.width || height != _maxSize.height ) {
            _maxSize.setSize( width, height );
            update();
        }      
    }
    
    public Dimension getMaxSize() {
        return new Dimension( _maxSize );
    }
    
    public void setTrackColor( Color barColor ) {
        if ( ! barColor.equals( _trackColor ) ) {
            _trackColor = barColor;
            update();
        }
    }
    
    public Color getTrackColor() {
        return _trackColor;
    }
    
    private boolean readValueTextField() {
        boolean success = false;
        String stringValue = _valueTextField.getText();
        double amplitude = 0.0;
        try {
            Double doubleValue = new Double( stringValue );
            amplitude = doubleValue.doubleValue();
        }
        catch ( NumberFormatException nfe ) {
            update();
        }
        if ( amplitude > 1.0 || amplitude < -1.0 ) {
            update();
        }
        else {
            _harmonicModel.setAmplitude( amplitude );
            success = true;
        }
        return success;
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    public void update() {
        
        double amplitude = _harmonicModel.getAmplitude();
        
        // Label location
        _labelGraphic.setLocation( 0, -( _maxSize.height/2 + 30 ) );
        
        // Value
        _valueTextField.setText( _valueFormatter.format( amplitude ) );
        _valueGraphic.setLocation( 0, -( _maxSize.height/2 + 25 ) );
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
        int knobY = (int) -( ( _maxSize.height / 2 ) * _harmonicModel.getAmplitude() );
        _knobGraphic.setLocation( knobX, knobY );
        
        repaint();
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    private class KnobGraphic extends CompositePhetGraphic {
        
        PhetShapeGraphic _topArrowGraphic;
        PhetShapeGraphic _bottomArrowGraphic;
        
        public KnobGraphic( Component component ) {
            super( component );
            
            Point2D tail = new Point2D.Double( 0, 0 );
            Point2D topTip = new Point2D.Double( 0, -ARROW_LENGTH );
            Point2D bottomTip = new Point2D.Double( 0, ARROW_LENGTH );
            Arrow topArrow = new Arrow( tail, topTip, ARROW_HEAD_HEIGHT, ARROW_HEAD_WIDTH, ARROW_TAIL_WIDTH, ARROW_FRACTIONAL_HEAD_HEIGHT, false );
            Arrow bottomArrow = new Arrow( tail, bottomTip, ARROW_HEAD_HEIGHT, ARROW_HEAD_WIDTH, ARROW_TAIL_WIDTH, ARROW_FRACTIONAL_HEAD_HEIGHT, false );
            
            _topArrowGraphic = new PhetShapeGraphic( getComponent() );
            _topArrowGraphic.setShape( topArrow.getShape() );
            _topArrowGraphic.setPaint( ARROW_FILL_COLOR );
            _topArrowGraphic.setBorderColor( ARROW_STROKE_COLOR );
            _topArrowGraphic.setStroke( ARROW_STROKE );
            addGraphic( _topArrowGraphic );
            
            _bottomArrowGraphic = new PhetShapeGraphic( getComponent() );
            _bottomArrowGraphic.setShape( bottomArrow.getShape() );
            _bottomArrowGraphic.setPaint( ARROW_FILL_COLOR );
            _bottomArrowGraphic.setBorderColor( ARROW_STROKE_COLOR );
            _bottomArrowGraphic.setStroke( ARROW_STROKE );
            addGraphic( _bottomArrowGraphic );
        }
        
        public void setHighlightEnabled( boolean enabled ) {
            if ( enabled ) {
                _topArrowGraphic.setPaint( ARROW_HIGHLIGHT_COLOR );
                _bottomArrowGraphic.setPaint( ARROW_HIGHLIGHT_COLOR );
            }
            else {
                _topArrowGraphic.setPaint( ARROW_FILL_COLOR );
                _bottomArrowGraphic.setPaint( ARROW_FILL_COLOR );
            }
        }
    }
    
    private class KnobEventListener extends MouseInputAdapter {
        
        private Point _somePoint;
        
        public KnobEventListener() {
            super();
            _somePoint = new Point();
        }
        
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
            _harmonicModel.setAmplitude( -amplitude );
        }
        
        public void mouseEntered( MouseEvent event ) {
            _knobGraphic.setHighlightEnabled( true );
        }
        
        public void mouseExited( MouseEvent event ) {
            _knobGraphic.setHighlightEnabled( false );
        }
    }
    
    private class ValueEventListener extends FocusAdapter implements ActionListener {
        
        public ValueEventListener() {}
        
        public void focusLost( FocusEvent event ) {
            System.out.println( "HarmonicSlider.ValueEventListener.focusLost" );//XXX
            if ( ! event.isTemporary() ) {
                boolean success = readValueTextField();
                if ( !success ) {
                    _valueTextField.requestFocus();
                }
            }
        }
        
        public void actionPerformed( ActionEvent event ) {
            System.out.println( "HarmonicSlider.ValueEventListener.actionPerformed" );//XXX
            if ( event.getSource() == _valueTextField ) {
                String stringValue = _valueTextField.getText();
                double amplitude = 0.0;
                try {
                    Double doubleValue = new Double( stringValue );
                    amplitude = doubleValue.doubleValue();
                }
                catch ( NumberFormatException nfe ) {
                    update();
                }
                if ( amplitude > 1.0 || amplitude < -1.0 ) {
                    update();
                }
                else {
                    _harmonicModel.setAmplitude( amplitude );
                }
            }        
        } 
    }
}
