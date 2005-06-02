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
import java.util.ArrayList;

import edu.colorado.phet.chart.Chart;
import edu.colorado.phet.chart.LabelTable;
import edu.colorado.phet.chart.Range2D;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.FourierConfig;
import edu.colorado.phet.fourier.model.FourierSeries;
import edu.colorado.phet.fourier.model.Harmonic;
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
    private static final double TITLE_LAYER = 1;
    private static final double CHART_LAYER = 2;
    private static final double WAVES_LAYER = 3;

    // Title parameters
    private static final Font TITLE_FONT = new Font( "Lucida Sans", Font.PLAIN, 20 );
    private static final Color TITLE_COLOR = Color.BLUE;
    private static final int TITLE_X_OFFSET = -15; // from origin
    
    // X axis
    private static final double X_MIN = -0.5;
    private static final double X_MAX = +0.5;
    private static final Color X_AXIS_COLOR = Color.BLACK;
    private static final Stroke X_AXIS_STROKE = new BasicStroke( 2f );
    private static final double X_MAJOR_TICK_SPACING = 0.25;
    private static final Stroke X_MAJOR_TICK_STROKE = new BasicStroke( 1f );
    private static final Font X_MAJOR_TICK_FONT = new Font( "Lucida Sans", Font.BOLD, 12 );
    private static final Color X_MAJOR_TICK_COLOR = Color.BLACK;
    private static final Color X_MAJOR_GRIDLINE_COLOR = Color.BLACK;
    private static final Stroke X_MAJOR_GRIDLINE_STROKE = new BasicStroke( 1f );
    
    // Y axis
    private static final double Y_MIN = -FourierConfig.MAX_HARMONIC_AMPLITUDE;
    private static final double Y_MAX = +FourierConfig.MAX_HARMONIC_AMPLITUDE;
    private static final Color Y_AXIS_COLOR = Color.BLACK;
    private static final Stroke Y_AXIS_STROKE = new BasicStroke( 2f );
    private static final double Y_MAJOR_TICK_SPACING = 0.5;
    private static final double Y_MINOR_TICK_SPACING = 0.1;
    private static final Stroke Y_MAJOR_TICK_STROKE = new BasicStroke( 1f );
    private static final Stroke Y_MINOR_TICK_STROKE = new BasicStroke( 1f );
    private static final Font Y_MAJOR_TICK_FONT = new Font( "Lucida Sans", Font.BOLD, 12 );
    private static final Color Y_MAJOR_GRIDLINE_COLOR = Color.BLACK;
    private static final Color Y_MINOR_GRIDLINE_COLOR = new Color( 0, 0, 0, 60 );
    private static final Stroke Y_MAJOR_GRIDLINE_STROKE = new BasicStroke( 1f );
    private static final Stroke Y_MINOR_GRIDLINE_STROKE = new BasicStroke( 0.5f );
    
    // Chart parameters
    private static final Range2D CHART_RANGE = new Range2D( X_MIN, Y_MIN, X_MAX, Y_MAX );
    private static final Dimension CHART_SIZE = new Dimension( 600, 130 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private FourierSeries _fourierSeriesModel;
    private Chart _chartGraphic;
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

        // Model
        _fourierSeriesModel = fourierSeriesModel;
        _fourierSeriesModel.addObserver( this );
        
        // Title
        String title = SimStrings.get( "HarmonicsGraphic.title" );
        PhetTextGraphic titleGraphic = new PhetTextGraphic( component, TITLE_FONT, title, TITLE_COLOR );
        titleGraphic.centerRegistrationPoint();
        titleGraphic.rotate( -( Math.PI / 2 ) );
        titleGraphic.setLocation( TITLE_X_OFFSET, 0 );
        addGraphic( titleGraphic, TITLE_LAYER );
        
        // Chart
        {
            _chartGraphic = new Chart( component, CHART_RANGE, CHART_SIZE );
            addGraphic( _chartGraphic, CHART_LAYER );
            
            _chartGraphic.setLocation( 0, -( CHART_SIZE.height / 2 ) );

            // X axis
            {
                _chartGraphic.getXAxis().setStroke( X_AXIS_STROKE );
                _chartGraphic.getXAxis().setColor( X_AXIS_COLOR );

                // No ticks or labels on the axis
                _chartGraphic.getXAxis().setMajorTicksVisible( false );
                _chartGraphic.getXAxis().setMajorTickLabelsVisible( false );
                _chartGraphic.getXAxis().setMinorTicksVisible( false );
                _chartGraphic.getXAxis().setMinorTickLabelsVisible( false );
                
                // Major ticks with labels below the chart
                _chartGraphic.getHorizontalTicks().setMajorTicksVisible( true );
                _chartGraphic.getHorizontalTicks().setMajorTickLabelsVisible( true );
                _chartGraphic.getHorizontalTicks().setMajorTickSpacing( X_MAJOR_TICK_SPACING );
                _chartGraphic.getHorizontalTicks().setMajorTickStroke( X_MAJOR_TICK_STROKE );
                _chartGraphic.getHorizontalTicks().setMajorTickFont( X_MAJOR_TICK_FONT );
                LabelTable labelTable = new LabelTable();
                labelTable.put( -0.5,  new PhetTextGraphic( component, X_MAJOR_TICK_FONT, "-L/2", X_MAJOR_TICK_COLOR ) );
                labelTable.put( -0.25, new PhetTextGraphic( component, X_MAJOR_TICK_FONT, "-L/4", X_MAJOR_TICK_COLOR ) );
                labelTable.put( 0,     new PhetTextGraphic( component, X_MAJOR_TICK_FONT, "0", X_MAJOR_TICK_COLOR ) );
                labelTable.put( 0.25,  new PhetTextGraphic( component, X_MAJOR_TICK_FONT, "L/4", X_MAJOR_TICK_COLOR ) );
                labelTable.put( 0.5,   new PhetTextGraphic( component, X_MAJOR_TICK_FONT, "L/2", X_MAJOR_TICK_COLOR ) );
                _chartGraphic.getHorizontalTicks().setMajorLabels( labelTable );

                // Vertical gridlines for major ticks.
                _chartGraphic.getVerticalGridlines().setMajorGridlinesVisible( true );
                _chartGraphic.getVerticalGridlines().setMajorTickSpacing( X_MAJOR_TICK_SPACING );
                _chartGraphic.getVerticalGridlines().setMajorGridlinesColor( X_MAJOR_GRIDLINE_COLOR );
                _chartGraphic.getVerticalGridlines().setMajorGridlinesStroke( X_MAJOR_GRIDLINE_STROKE );
                
                // No vertical gridlines for minor ticks.
                _chartGraphic.getVerticalGridlines().setMinorGridlinesVisible( false );
            }
            
            // Y axis
            {
                _chartGraphic.getYAxis().setStroke( Y_AXIS_STROKE );
                _chartGraphic.getYAxis().setColor( Y_AXIS_COLOR );
                
                // No ticks or labels on the axis
                _chartGraphic.getYAxis().setMajorTicksVisible( false );
                _chartGraphic.getYAxis().setMajorTickLabelsVisible( false );
                _chartGraphic.getYAxis().setMinorTicksVisible( false );
                _chartGraphic.getYAxis().setMinorTickLabelsVisible( false );
                
                // Major ticks with labels to the left of the chart
                _chartGraphic.getVerticalTicks().setMajorTicksVisible( true );
                _chartGraphic.getVerticalTicks().setMajorTickLabelsVisible( true );
                _chartGraphic.getVerticalTicks().setMajorTickSpacing( Y_MAJOR_TICK_SPACING );
                _chartGraphic.getVerticalTicks().setMajorTickStroke( Y_MAJOR_TICK_STROKE );
                _chartGraphic.getVerticalTicks().setMajorTickFont( Y_MAJOR_TICK_FONT );

                // Horizontal gridlines for major ticks
                _chartGraphic.getHorizonalGridlines().setMajorGridlinesVisible( true );
                _chartGraphic.getHorizonalGridlines().setMajorTickSpacing( Y_MAJOR_TICK_SPACING );
                _chartGraphic.getHorizonalGridlines().setMajorGridlinesColor( Y_MAJOR_GRIDLINE_COLOR );
                _chartGraphic.getHorizonalGridlines().setMajorGridlinesStroke( Y_MAJOR_GRIDLINE_STROKE );

                // Horizontal gridlines for minor ticks
                _chartGraphic.getHorizonalGridlines().setMinorGridlinesVisible( true );
                _chartGraphic.getHorizonalGridlines().setMinorTickSpacing( Y_MINOR_TICK_SPACING );
                _chartGraphic.getHorizonalGridlines().setMinorGridlinesColor( Y_MINOR_GRIDLINE_COLOR );
                _chartGraphic.getHorizonalGridlines().setMinorGridlinesStroke( Y_MINOR_GRIDLINE_STROKE );
            }
        }
        
        // Waves
        _waveType = SineWaveGraphic.WAVE_TYPE_SINE;
        _wavesGraphic = new CompositePhetGraphic( component );
        addGraphic( _wavesGraphic, WAVES_LAYER );
        
        // Interactivity
        titleGraphic.setIgnoreMouse( true );
        // XXX others setIgnoreMouse ?
        
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
                waveGraphic.setViewportSize( CHART_SIZE.width, CHART_SIZE.height );
                waveGraphic.setLocation( CHART_SIZE.width / 2, 0 );
                waveGraphic.update();
                
                _wavesGraphic.addGraphic( waveGraphic );
            }
            
            repaint();
        }
    }
}
