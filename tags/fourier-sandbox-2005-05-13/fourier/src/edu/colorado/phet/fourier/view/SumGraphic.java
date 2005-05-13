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

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.model.FourierComponent;
import edu.colorado.phet.fourier.model.FourierSeries;
import edu.colorado.phet.fourier.util.FourierUtils;


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
    private static final Color MINOR_AXES_COLOR1 = new Color( 0, 0, 0, 30 );
    private static final Color MINOR_AXES_COLOR2 = new Color( 0, 0, 0, 100 );
    private static final Stroke MINOR_AXES_STROKE = new BasicStroke( 1f );
    private static final Font TICKS_LABEL_FONT = new Font( "Lucida Sans", Font.PLAIN, 16 );
    private static final Color TICKS_LABEL_COLOR = Color.BLACK;
    private static final int MAJOR_TICK_LENGTH = 10;
    private static final Color MAJOR_TICK_COLOR = Color.BLACK;
    private static final Stroke MAJOR_TICK_STROKE = new BasicStroke( 1f );
    private static final int MINOR_TICK_LENGTH = 5;
    private static final Color MINOR_TICK_COLOR = Color.BLACK;
    private static final Stroke MINOR_TICK_STROKE = new BasicStroke( 1f );
    private static final String X_ZERO_LABEL = "0";
    private static final String AXES_LABEL_FORMAT = "0.00";
    
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
    private int _previousNumberOfComponents;
    private int _waveType;
    private double _phaseAngle;
    private PhetTextGraphic _maxLabelGraphic, _minLabelGraphic;
    private NumberFormat _minMaxFormatter;
    
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
        _outlineGraphic.setIgnoreMouse( true );
        //XXX others ignore mouse?
        
        _waveType = SineWaveGraphic.WAVE_TYPE_SINE;
        _phaseAngle = DEFAULT_PHASE_ANGLE;
        _previousNumberOfComponents = -1; // force update
        _minMaxFormatter = new DecimalFormat( AXES_LABEL_FORMAT );
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
        
        int numberOfComponents = _fourierSeriesModel.getNumberOfComponents();
        int width = OUTLINE_WIDTH;
        int height = OUTLINE_HEIGHT;

        _wavePath.reset();

        // Sum the components at each X point.
        double maxSum = 0;
        double[] sums = new double[width + 1];
        for ( int i = 0; i < sums.length; i++ ) {

            sums[i] = 0;

            for ( int j = 0; j < numberOfComponents; j++ ) {

                FourierComponent fourierComponent = (FourierComponent) _fourierSeriesModel.getComponent( j );
                final double amplitude = fourierComponent.getAmplitude();
                final int numberOfCycles = fourierComponent.getOrder() + 1;
                final double deltaAngle = ( 2.0 * Math.PI * numberOfCycles ) / width;
                final double startAngle = _phaseAngle - ( deltaAngle * ( width / 2.0 ) );
                double angle = startAngle + ( i * deltaAngle );
                double radians = ( _waveType == SineWaveGraphic.WAVE_TYPE_SINE ) ? Math.sin( angle ) : Math.cos( angle );
                sums[i] += ( amplitude * radians );
            }

            if ( Math.abs( sums[i] ) > maxSum ) {
                maxSum = Math.abs( sums[i] );
            }
        }

        // Create the path, scaled so that it fills the viewport.
        for ( int i = 0; i < sums.length; i++ ) {
            double x = -( width / 2 - i );
            double y = ( sums[i] / maxSum ) * ( height / 2.0 );
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

        repaint();
    }
}
