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
import java.awt.geom.Line2D;
import java.util.ArrayList;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.model.Harmonic;
import edu.colorado.phet.fourier.model.FourierSeries;
import edu.colorado.phet.fourier.util.FourierUtils;


/**
 * HarmonicsGraphic
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HarmonicsGraphic extends GraphicLayerSet implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Layers
    private static final double BACKGROUND_LAYER = 1;
    private static final double WAVES_LAYER = 2;

    // Title parameters
    private static final Font TITLE_FONT = new Font( "Lucida Sans", Font.PLAIN, 20 );
    private static final Color TITLE_COLOR = Color.BLUE;
    private static final int TITLE_X_OFFSET = -15; // from origin
    
    // Outline parameters
    private static final int OUTLINE_WIDTH = 600;
    private static final int OUTLINE_HEIGHT = 175;
    
    // Axes parameters
    private static final Color AXES_COLOR = Color.BLACK;
    private static final Stroke AXES_STROKE = new BasicStroke( 1f );
    private static final String X_MAX_LABEL = "1";
    private static final String X_MIN_LABEL = "-1";
    private static final String X_ZERO_LABEL = "0";
    
    // Tick marks
    private static final Font TICKS_LABEL_FONT = new Font( "Lucida Sans", Font.PLAIN, 16 );
    private static final Color TICKS_LABEL_COLOR = Color.BLACK;
    private static final int MAJOR_TICK_LENGTH = 10;
    private static final Color MAJOR_TICK_COLOR = Color.BLACK;
    private static final Stroke MAJOR_TICK_STROKE = new BasicStroke( 1f );
    private static final int MINOR_TICK_LENGTH = 5;
    private static final Color MINOR_TICK_COLOR = Color.BLACK;
    private static final Stroke MINOR_TICK_STROKE = new BasicStroke( 1f );

    // Tick lines
    private static final Color TICK_LINE_COLOR1 = new Color( 0, 0, 0, 30 );
    private static final Color TICK_LINE_COLOR2 = new Color( 0, 0, 0, 100 );
    private static final Stroke TICK_LINE_STROKE = new BasicStroke( 1f );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private FourierSeries _fourierSeriesModel;
    private CompositePhetGraphic _wavesGraphic;
    private ArrayList _wavesList; // array of SineWaveGraphic
    private int _previousNumberOfComponents;
    private int _waveType;
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------
    
    public HarmonicsGraphic( Component component, FourierSeries fourierSeriesModel ) {
        super( component );
        
        // Enable antialiasing
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );

        _fourierSeriesModel = fourierSeriesModel;
        _fourierSeriesModel.addObserver( this );
        
        // Static background graphics
        BackgroundGraphic backgroundGraphic = new BackgroundGraphic( component );
        backgroundGraphic.setLocation( 0, 0 );
        addGraphic( backgroundGraphic, BACKGROUND_LAYER );
        
        // Waves
        _waveType = SineWaveGraphic.WAVE_TYPE_SINE;
        _wavesGraphic = new CompositePhetGraphic( component );
        addGraphic( _wavesGraphic, WAVES_LAYER );
        
        // Interactivity
        backgroundGraphic.setIgnoreMouse( true );
        _wavesGraphic.setIgnoreMouse( true );
        
        _wavesList = new ArrayList();
        _previousNumberOfComponents = -1; // force update
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
           for ( int i = 0; i < _wavesList.size(); i++ ) {
               SineWaveGraphic wave = (SineWaveGraphic)_wavesList.get( i );
               wave.setWaveType( _waveType );
               wave.update();
           }
           repaint();
       }
    }
    
    public int getWaveType() {
        return _waveType;
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    public void update() {
        
        int numberOfHarmonics = _fourierSeriesModel.getNumberOfHarmonics();
        
        if ( _previousNumberOfComponents != numberOfHarmonics ) {
            
            _wavesGraphic.clear();
            
            for ( int i = 0; i < numberOfHarmonics; i++ ) {
                
                Harmonic harmonic = (Harmonic) _fourierSeriesModel.getHarmonic( i );
                
                double amplitude = harmonic.getAmplitude();
                if ( amplitude == 0 ) {
                    continue;
                }
                
                int numberOfCycles = harmonic.getOrder() + 1;
                Color color = FourierUtils.calculateHarmonicColor( i );
                
                SineWaveGraphic waveGraphic = null;
                if ( i < _wavesList.size() ) {
                    waveGraphic = ( SineWaveGraphic ) _wavesList.get( i );
                }
                else {   
                    waveGraphic = new SineWaveGraphic( getComponent() );
                    _wavesList.add( waveGraphic );
                }
                
                waveGraphic.setNumberOfCycles( numberOfCycles );
                waveGraphic.setAmplitude( amplitude );
                waveGraphic.setWaveType( _waveType );
                waveGraphic.setColor( color );
                waveGraphic.setViewportSize( OUTLINE_WIDTH, OUTLINE_HEIGHT );
                waveGraphic.setLocation( OUTLINE_WIDTH/2, 0 );
                waveGraphic.update();
                
                _wavesGraphic.addGraphic( waveGraphic );
            }
            
            repaint();
        }
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    private class BackgroundGraphic extends CompositePhetGraphic {
        
        // Layers
        private static final double TITLE_LAYER = 1;
        private static final double OUTLINE_LAYER = 2;
        private static final double AXES_LAYER = 3;
        private static final double TICKS_LAYER = 4;
        private static final double LABELS_LAYER = 5;
        
        public BackgroundGraphic( Component component ) {
            super( component );
            
            // Title
            String title = SimStrings.get( "HarmonicsGraphic.title" );
            PhetTextGraphic titleGraphic = new PhetTextGraphic( component, TITLE_FONT, title, TITLE_COLOR );
            titleGraphic.centerRegistrationPoint();
            titleGraphic.rotate( -( Math.PI / 2 ) );
            titleGraphic.setLocation( TITLE_X_OFFSET, 0 );
            addGraphic( titleGraphic, TITLE_LAYER );
            
            // Outline
            PhetShapeGraphic outlineGraphic = new PhetShapeGraphic( component );
            Rectangle outlineRectangle = new Rectangle( 0, -OUTLINE_HEIGHT/2, OUTLINE_WIDTH, OUTLINE_HEIGHT-1 );
            outlineGraphic.setShape( outlineRectangle );
            outlineGraphic.setBorderColor( Color.BLACK );
            outlineGraphic.setStroke( new BasicStroke( 1f ) );
            addGraphic( outlineGraphic, OUTLINE_LAYER );
            
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
            
            // Minor tick marks & horizontal lines
            {
                int numberOfMinorTicks = 18;
                double deltaY = ( OUTLINE_HEIGHT / 2 ) / ( numberOfMinorTicks / 2 );
                Shape minorTickShape = new Line2D.Double( -MINOR_TICK_LENGTH, 0, 0, 0 );
                Shape lineShape = new Line2D.Double( 0, 0, OUTLINE_WIDTH, 0 );
                
                for ( int i = 0; i < ( numberOfMinorTicks / 2 ); i++ ) {

                    int y = (int) ( ( i + 1 ) * deltaY );

                    PhetShapeGraphic positiveTickGraphic = new PhetShapeGraphic( component );
                    positiveTickGraphic.setShape( minorTickShape );
                    positiveTickGraphic.setBorderColor( MINOR_TICK_COLOR );
                    positiveTickGraphic.setStroke( MINOR_TICK_STROKE );
                    positiveTickGraphic.setLocation( 0, -y );
                    addGraphic( positiveTickGraphic, TICKS_LAYER );

                    PhetShapeGraphic negativeTickGraphic = new PhetShapeGraphic( component );
                    negativeTickGraphic.setShape( minorTickShape );
                    negativeTickGraphic.setBorderColor( MINOR_TICK_COLOR );
                    negativeTickGraphic.setStroke( MINOR_TICK_STROKE );
                    negativeTickGraphic.setLocation( 0, y );
                    addGraphic( negativeTickGraphic, TICKS_LAYER );
                    
                    Color tickLineColor = ( ( i + 1 ) == 5 ) ? TICK_LINE_COLOR2 : TICK_LINE_COLOR1;
                    
                    PhetShapeGraphic positiveLineGraphic = new PhetShapeGraphic( component );
                    positiveLineGraphic.setShape( lineShape );
                    positiveLineGraphic.setBorderColor( tickLineColor );
                    positiveLineGraphic.setStroke( TICK_LINE_STROKE );
                    positiveLineGraphic.setLocation( 0, -y );
                    addGraphic( positiveLineGraphic, AXES_LAYER );
                    
                    PhetShapeGraphic negativeLineGraphic = new PhetShapeGraphic( component );
                    negativeLineGraphic.setShape( lineShape );
                    negativeLineGraphic.setBorderColor( tickLineColor );
                    negativeLineGraphic.setStroke( TICK_LINE_STROKE );
                    negativeLineGraphic.setLocation( 0, y );
                    addGraphic( negativeLineGraphic, AXES_LAYER );
                }
            }
            
            // X axis labels
            {
                int x = -12;
                
                PhetTextGraphic zerolabelGraphic = new PhetTextGraphic( component, TICKS_LABEL_FONT, X_ZERO_LABEL, TICKS_LABEL_COLOR );
                zerolabelGraphic.setJustification( PhetTextGraphic.EAST );
                zerolabelGraphic.setLocation( x, 0 );
                addGraphic( zerolabelGraphic, LABELS_LAYER );
                
                PhetTextGraphic maxlabelGraphic = new PhetTextGraphic( component, TICKS_LABEL_FONT, X_MAX_LABEL, TICKS_LABEL_COLOR );
                maxlabelGraphic.setJustification( PhetTextGraphic.EAST );
                maxlabelGraphic.setLocation( x, -( OUTLINE_HEIGHT / 2 ) );
                addGraphic( maxlabelGraphic, LABELS_LAYER );
                
                PhetTextGraphic minLabelGraphic = new PhetTextGraphic( component, TICKS_LABEL_FONT, X_MIN_LABEL, TICKS_LABEL_COLOR );
                minLabelGraphic.setJustification( PhetTextGraphic.EAST );
                minLabelGraphic.setLocation( x, +( OUTLINE_HEIGHT / 2 ) );
                addGraphic( minLabelGraphic, LABELS_LAYER );
            }
        }
    }
}
