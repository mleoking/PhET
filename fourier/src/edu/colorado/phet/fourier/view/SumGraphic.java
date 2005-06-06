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
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.model.FourierSeries;
import edu.colorado.phet.fourier.model.Harmonic;


/**
 * SumGraphic
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class SumGraphic extends GraphicLayerSet implements SimpleObserver {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Layers
    private static final double TITLE_LAYER = 1;
    private static final double OUTLINE_LAYER = 2;
    private static final double AXES_LAYER = 3;
    private static final double TICKS_LAYER = 4;
    private static final double LABELS_LAYER = 5;
    private static final double WAVE_LAYER = 6;
    
    // Title
    private static final Font TITLE_FONT = new Font( "Lucida Sans", Font.PLAIN, 20 );
    private static final Color TITLE_COLOR = Color.BLUE;
    private static final int TITLE_X_OFFSET = -15; // from origin
    
    // Outline
    private static final int OUTLINE_WIDTH = 600;
    private static final int OUTLINE_HEIGHT = 175;
    
    // Axes parameters
    private static final Color AXES_COLOR = Color.BLACK;
    private static final Stroke AXES_STROKE = new BasicStroke( 1f );
    private static final String X_ZERO_LABEL = "0";
    private static final String AXES_LABEL_FORMAT = "0.00";
    
    // Tick marks
    private static final Font TICKS_LABEL_FONT = new Font( "Lucida Sans", Font.PLAIN, 16 );
    private static final Color TICKS_LABEL_COLOR = Color.BLACK;
    private static final int MAJOR_TICK_LENGTH = 10;
    private static final Color MAJOR_TICK_COLOR = Color.BLACK;
    private static final Stroke MAJOR_TICK_STROKE = new BasicStroke( 1f );
    private static final int MINOR_TICK_LENGTH = 5;
    private static final Color MINOR_TICK_COLOR = Color.BLACK;
    private static final Stroke MINOR_TICK_STROKE = new BasicStroke( 1f );
    private static final double MINOR_TICK_INTERVAL = 1.0;

    // Tick lines
    private static final Color TICK_LINE_COLOR1 = new Color( 0, 0, 0, 30 );
    private static final Color TICK_LINE_COLOR2 = new Color( 0, 0, 0, 100 );
    private static final Stroke TICK_LINE_STROKE = new BasicStroke( 1f );
    
    // Wave parameters
    private static final Color WAVE_COLOR = Color.BLACK;
    private static final Stroke WAVE_STROKE = new BasicStroke( 2f );
    private static final double DEFAULT_PHASE_ANGLE = 0.0;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private FourierSeries _fourierSeriesModel;
    private Rectangle _outlineRectangle;
    private PhetShapeGraphic _outlineGraphic;
    private PhetShapeGraphic _waveGraphic;
    private GeneralPath _wavePath;
    private int _previousNumberOfHarmonics;
    private int _waveType;
    private double _phaseAngle;
    private PhetTextGraphic _maxLabelGraphic, _minLabelGraphic;
    private NumberFormat _minMaxFormatter;
    private CompositePhetGraphic _minorTicksGraphic;
    private double[] _sums;
    private ArrayList _tickMarksList; // array of PhetShapeGraphic
    private ArrayList _tickLinesList; // array of PhetShapeGraphic
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------
    
    public SumGraphic( Component component, FourierSeries fourierSeriesModel ) {
        super( component );
        
        // Enable antialiasing
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );

        _fourierSeriesModel = fourierSeriesModel;
        _fourierSeriesModel.addObserver( this );
        
        // Title
        String title = SimStrings.get( "SumGraphic.title" );
        PhetTextGraphic titleGraphic = new PhetTextGraphic( component, TITLE_FONT, title, TITLE_COLOR );
        titleGraphic.centerRegistrationPoint();
        titleGraphic.rotate( -( Math.PI / 2 ) );
        titleGraphic.setLocation( TITLE_X_OFFSET, 0 );
        addGraphic( titleGraphic, TITLE_LAYER );
        
        _outlineGraphic = new PhetShapeGraphic( component );
        _outlineRectangle = new Rectangle( 0, -OUTLINE_HEIGHT/2, OUTLINE_WIDTH, OUTLINE_HEIGHT-1 );
        _outlineGraphic.setShape( _outlineRectangle );
        _outlineGraphic.setBorderColor( Color.BLACK );
        _outlineGraphic.setStroke( new BasicStroke( 1f ) );
        addGraphic( _outlineGraphic, OUTLINE_LAYER );
        
        // X axis
        PhetShapeGraphic xAxisGraphic = new PhetShapeGraphic( component );
        xAxisGraphic.setShape( new Line2D.Double( 0, 0, OUTLINE_WIDTH, 0 ) );
        xAxisGraphic.setBorderColor( AXES_COLOR );
        xAxisGraphic.setStroke( AXES_STROKE );
        addGraphic( xAxisGraphic, AXES_LAYER );
        
        // Y axis
        PhetShapeGraphic yAxisGraphic = new PhetShapeGraphic( component );
        yAxisGraphic.setShape( new Line2D.Double( OUTLINE_WIDTH/2, -OUTLINE_HEIGHT/2, OUTLINE_WIDTH/2, +OUTLINE_HEIGHT/2 ) );
        yAxisGraphic.setBorderColor( AXES_COLOR );
        yAxisGraphic.setStroke( AXES_STROKE );
        addGraphic( yAxisGraphic, AXES_LAYER );
        
        // Major tick marks
        {
            Shape majorTickShape = new Line2D.Double( -MAJOR_TICK_LENGTH, 0, 0, 0 );
            
            PhetShapeGraphic zeroTickGraphic = new PhetShapeGraphic( component );
            zeroTickGraphic.setShape( majorTickShape );
            zeroTickGraphic.setBorderColor( MAJOR_TICK_COLOR );
            zeroTickGraphic.setStroke( MAJOR_TICK_STROKE );
            zeroTickGraphic.setLocation( 0, 0 );
            addGraphic( zeroTickGraphic, TICKS_LAYER );
            
            PhetShapeGraphic maxTickGraphic = new PhetShapeGraphic( component );
            maxTickGraphic.setShape( majorTickShape );
            maxTickGraphic.setBorderColor( MAJOR_TICK_COLOR );
            maxTickGraphic.setStroke( MAJOR_TICK_STROKE );
            maxTickGraphic.setLocation( 0, -( OUTLINE_HEIGHT / 2 ) );
            addGraphic( maxTickGraphic, TICKS_LAYER );
           
            PhetShapeGraphic minTickGraphic = new PhetShapeGraphic( component );
            minTickGraphic.setShape( majorTickShape );
            minTickGraphic.setBorderColor( MAJOR_TICK_COLOR );
            minTickGraphic.setStroke( MAJOR_TICK_STROKE );
            minTickGraphic.setLocation( 0, +( OUTLINE_HEIGHT / 2 ) );
            addGraphic( minTickGraphic, TICKS_LAYER );
        }
        
        // Minor tick marks
        _minorTicksGraphic = new CompositePhetGraphic( component );
        addGraphic( _minorTicksGraphic, TICKS_LAYER );
        
        // X axis labels
        {
            int x = -12;
            
            PhetTextGraphic zerolabelGraphic = new PhetTextGraphic( component, TICKS_LABEL_FONT, X_ZERO_LABEL, TICKS_LABEL_COLOR );
            zerolabelGraphic.setJustification( PhetTextGraphic.EAST );
            zerolabelGraphic.setLocation( x, 0 );
            addGraphic( zerolabelGraphic, LABELS_LAYER );
            
            _maxLabelGraphic = new PhetTextGraphic( component, TICKS_LABEL_FONT, X_ZERO_LABEL, TICKS_LABEL_COLOR );
            _maxLabelGraphic.setJustification( PhetTextGraphic.EAST );
            _maxLabelGraphic.setLocation( x, -( OUTLINE_HEIGHT / 2 ) );
            addGraphic( _maxLabelGraphic, LABELS_LAYER );
            
            _minLabelGraphic = new PhetTextGraphic( component, TICKS_LABEL_FONT, X_ZERO_LABEL, TICKS_LABEL_COLOR );
            _minLabelGraphic.setJustification( PhetTextGraphic.EAST );
            _minLabelGraphic.setLocation( x, +( OUTLINE_HEIGHT / 2 ) );
            addGraphic( _minLabelGraphic, LABELS_LAYER );
        }
        
        // Wave
        _waveGraphic = new PhetShapeGraphic( component );
        _waveGraphic.setLocation( OUTLINE_WIDTH/2, 0 );
        _wavePath = new GeneralPath();
        _waveGraphic.setShape( _wavePath );
        _waveGraphic.setBorderColor( WAVE_COLOR );
        _waveGraphic.setStroke( WAVE_STROKE );
        addGraphic( _waveGraphic, WAVE_LAYER );
        
        // Interactivity
        this.setIgnoreMouse( true );
        
        _sums = new double[ OUTLINE_WIDTH + 1 ];
        _waveType = FourierConstants.WAVE_TYPE_SINE;
        _phaseAngle = DEFAULT_PHASE_ANGLE;
        _previousNumberOfHarmonics = -1; // force update
        _minMaxFormatter = new DecimalFormat( AXES_LABEL_FORMAT );
        _tickMarksList = new ArrayList();
        _tickLinesList = new ArrayList();
        
        update();
    }
    
    public void finalize() {
        _fourierSeriesModel.removeObserver( this );
        _fourierSeriesModel = null;
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setWaveType( int waveType ) {
       if ( waveType != _waveType ) {
           _waveType = waveType;
           update();
       }
    }
    
    public int getWaveType() {
        return _waveType;
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    // XXX this is quick-and-dirty, very inefficient
    public void update() {
        
        int numberOfHarmonics = _fourierSeriesModel.getNumberOfHarmonics();

        _wavePath.reset();

        // Sum the components at each X point.
        double maxSum = 0;
        for ( int i = 0; i < _sums.length; i++ ) {

            _sums[i] = 0;

            for ( int j = 0; j < numberOfHarmonics; j++ ) {

                Harmonic harmonic = (Harmonic) _fourierSeriesModel.getHarmonic( j );
                final double amplitude = harmonic.getAmplitude();
                final int numberOfCycles = harmonic.getOrder() + 1;
                final double deltaAngle = ( 2.0 * Math.PI * numberOfCycles ) / OUTLINE_WIDTH;
                final double startAngle = _phaseAngle - ( deltaAngle * ( OUTLINE_WIDTH / 2.0 ) );
                double angle = startAngle + ( i * deltaAngle );
                double radians = ( _waveType == FourierConstants.WAVE_TYPE_SINE ) ? Math.sin( angle ) : Math.cos( angle );
                _sums[i] += ( amplitude * radians );
            }

            if ( Math.abs( _sums[i] ) > maxSum ) {
                maxSum = Math.abs( _sums[i] );
            }
        }

        // Create the path, scaled so that it fills the viewport.
        for ( int i = 0; i < _sums.length; i++ ) {
            double x = -( ( OUTLINE_WIDTH / 2 ) - i );
            double y = ( _sums[i] / maxSum ) * ( OUTLINE_HEIGHT / 2.0 );
            if ( i == 0 ) {
                _wavePath.moveTo( (float) x, (float) -y ); // +Y is up
            }
            else {
                _wavePath.lineTo( (float) x, (float) -y ); // +Y is up
            }
        }
        
        // Set the min and max labels on the Y axis
        _maxLabelGraphic.setText( _minMaxFormatter.format( maxSum ) );
        _minLabelGraphic.setText( _minMaxFormatter.format( -maxSum ) );

        // Minor tick marks & horizontal lines
        {
            _minorTicksGraphic.clear();
              
            int numberOfMinorTicks = (int) ( maxSum / MINOR_TICK_INTERVAL );
            
            double deltaY = 0;
            if ( numberOfMinorTicks > 0 ) {
                double remainder = maxSum % MINOR_TICK_INTERVAL;
                double height = ( OUTLINE_HEIGHT - ( OUTLINE_HEIGHT * remainder / maxSum ) ) / 2.0;
                deltaY = height / numberOfMinorTicks;
            }
            
            Shape minorTickShape = new Line2D.Double( -MINOR_TICK_LENGTH, 0, 0, 0 );
            Shape lineShape = new Line2D.Double( 0, 0, OUTLINE_WIDTH, 0 );
            
            Component component = getComponent();
            
            for ( int i = 0; i < numberOfMinorTicks; i++ ) {

                int y = (int) ( ( i + 1 ) * deltaY );

                int index = 0;
                
                PhetShapeGraphic positiveTickGraphic = null;
                index = ( 2 * i );
                if ( index < _tickMarksList.size() ) {
                    positiveTickGraphic = ( PhetShapeGraphic ) _tickMarksList.get( index );
                }
                else {
                    positiveTickGraphic = new PhetShapeGraphic( component );
                    positiveTickGraphic.setShape( minorTickShape );
                    positiveTickGraphic.setBorderColor( MINOR_TICK_COLOR );
                    positiveTickGraphic.setStroke( MINOR_TICK_STROKE );
                    _tickMarksList.add( positiveTickGraphic );
                }
                positiveTickGraphic.setLocation( 0, -y );
                _minorTicksGraphic.addGraphic( positiveTickGraphic, TICKS_LAYER );

                PhetShapeGraphic negativeTickGraphic = null;
                index = ( 2 * i ) + 1;
                if ( index < _tickMarksList.size() ) {
                    negativeTickGraphic = ( PhetShapeGraphic ) _tickMarksList.get( index );
                }
                else {
                    negativeTickGraphic = new PhetShapeGraphic( component );
                    negativeTickGraphic.setShape( minorTickShape );
                    negativeTickGraphic.setBorderColor( MINOR_TICK_COLOR );
                    negativeTickGraphic.setStroke( MINOR_TICK_STROKE );
                    _tickMarksList.add( negativeTickGraphic );
                }
                negativeTickGraphic.setLocation( 0, y );
                _minorTicksGraphic.addGraphic( negativeTickGraphic, TICKS_LAYER );
                
                Color tickLineColor = ( ( i + 1 ) % 5 == 0 ) ? TICK_LINE_COLOR2 : TICK_LINE_COLOR1;
                
                PhetShapeGraphic positiveLineGraphic = null;
                index = ( 2 * i );
                if ( index < _tickLinesList.size() ) {
                    positiveLineGraphic = ( PhetShapeGraphic ) _tickLinesList.get( index );
                }
                else {
                    positiveLineGraphic = new PhetShapeGraphic( component );
                    positiveLineGraphic.setShape( lineShape );
                    positiveLineGraphic.setBorderColor( tickLineColor );
                    positiveLineGraphic.setStroke( TICK_LINE_STROKE );
                    _tickLinesList.add( positiveLineGraphic );
                }
                positiveLineGraphic.setLocation( 0, -y );
                _minorTicksGraphic.addGraphic( positiveLineGraphic, AXES_LAYER );
                
                PhetShapeGraphic negativeLineGraphic = null;
                index = ( 2 * i ) + 1;
                if ( index < _tickLinesList.size () ) {
                    negativeLineGraphic = ( PhetShapeGraphic ) _tickLinesList.get( index );
                }
                else {
                    negativeLineGraphic = new PhetShapeGraphic( component );
                    negativeLineGraphic.setShape( lineShape );
                    negativeLineGraphic.setBorderColor( tickLineColor );
                    negativeLineGraphic.setStroke( TICK_LINE_STROKE );
                    _tickLinesList.add( negativeLineGraphic );
                }
                negativeLineGraphic.setLocation( 0, y );
                _minorTicksGraphic.addGraphic( negativeLineGraphic, AXES_LAYER );
            }
        }
        
        repaint();
    }
}
