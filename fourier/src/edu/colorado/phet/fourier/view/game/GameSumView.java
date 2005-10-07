/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.view.game;

import java.awt.*;

import edu.colorado.phet.chart.Range2D;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.FourierConfig;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.charts.FourierSumPlot;
import edu.colorado.phet.fourier.model.FourierSeries;
import edu.colorado.phet.fourier.view.discrete.DiscreteSumChart;


/**
 * GameSumView is the "Sum" view in the "Game" module.
 * It displays the sum of a Fourier series.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class GameSumView extends GraphicLayerSet implements SimpleObserver {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Layers
    private static final double BACKGROUND_LAYER = 1;
    private static final double TITLE_LAYER = 2;
    private static final double CHART_LAYER = 3;
    private static final double CONTROLS_LAYER = 4;

    // Background parameters
    private static final int MIN_HEIGHT = 150;
    private static final Dimension BACKGROUND_SIZE = new Dimension( 800, 216 );
    private static final Color BACKGROUND_COLOR = new Color( 215, 215, 215 );
    private static final Stroke BACKGROUND_STROKE = new BasicStroke( 1f );
    private static final Color BACKGROUND_BORDER_COLOR = Color.BLACK;
    
    // Title parameters
    private static final Font TITLE_FONT = new Font( FourierConfig.FONT_NAME, Font.PLAIN, 20 );
    private static final Color TITLE_COLOR = Color.BLUE;
    private static final Point TITLE_LOCATION = new Point( 40, 135 );
    
    // Chart parameters
    private static final double L = FourierConstants.L; // do not change!
    private static final double X_RANGE_START = ( L / 2 );
    private static final double X_RANGE_MIN = ( L / 4 );
    private static final double X_RANGE_MAX = ( 2 * L );
    private static final double Y_RANGE_START = FourierConfig.MAX_HARMONIC_AMPLITUDE;
    private static final double Y_RANGE_MIN = FourierConfig.MAX_HARMONIC_AMPLITUDE;
    private static final double Y_RANGE_MAX = 12.0;
    private static final Range2D CHART_RANGE = new Range2D( -X_RANGE_START, -Y_RANGE_START, X_RANGE_START, Y_RANGE_START );
    private static final Dimension CHART_SIZE = new Dimension( 540, 135 );
    
    // Wave parameters
    private static final Stroke SUM_STROKE = new BasicStroke( 1f );
    private static final Color SUM_COLOR = Color.BLACK;
    private static final double SUM_PIXELS_PER_POINT = 1;
    private static final Stroke PRESET_STROKE = new BasicStroke( 4f );
    private static final Color PRESET_COLOR = Color.LIGHT_GRAY;
    
    // Autoscaling
    private static final double AUTOSCALE_FACTOR = 1.25; // multiple max amplitude by this amount when autoscaling
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private FourierSeries _fourierSeries;
    private PhetShapeGraphic _backgroundGraphic;
    private PhetTextGraphic _titleGraphic;
    private PhetImageGraphic _minimizeButton;
    private GameSumChart _chartGraphic;
    private FourierSumPlot _sumPlot;
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------
    
    public GameSumView( Component component, FourierSeries fourierSeries ) {
        super( component );

        // Enable antialiasing for all children.
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
        
        // Model
        _fourierSeries = fourierSeries;
        _fourierSeries.addObserver( this );
        
        // Background
        _backgroundGraphic = new PhetShapeGraphic( component );
        _backgroundGraphic.setShape( new Rectangle( 0, 0, BACKGROUND_SIZE.width, BACKGROUND_SIZE.height ) );
        _backgroundGraphic.setPaint( BACKGROUND_COLOR );
        _backgroundGraphic.setStroke( BACKGROUND_STROKE );
        _backgroundGraphic.setBorderColor( BACKGROUND_BORDER_COLOR );
        addGraphic( _backgroundGraphic, BACKGROUND_LAYER );
        _backgroundGraphic.setLocation( 0, 0 );
        
        // Title
        String title = SimStrings.get( "GameSumView.title" );
        _titleGraphic = new PhetTextGraphic( component, TITLE_FONT, title, TITLE_COLOR );
        _titleGraphic.centerRegistrationPoint();
        _titleGraphic.rotate( -( Math.PI / 2 ) );
        _titleGraphic.setLocation( TITLE_LOCATION );
        addGraphic( _titleGraphic, TITLE_LAYER );
        
        // Chart
        {
            _chartGraphic = new GameSumChart( component, CHART_RANGE, CHART_SIZE );
            addGraphic( _chartGraphic, CHART_LAYER );
            _chartGraphic.setRegistrationPoint( 0, 0 );
            _chartGraphic.setLocation( 60, 50 );
            _chartGraphic.setXAxisTitle( "x (mm)" ); 
            
            // Sum plot
            _sumPlot = new FourierSumPlot( getComponent(), _chartGraphic, _fourierSeries );
            _sumPlot.setPeriod( L );
            _sumPlot.setPixelsPerPoint( SUM_PIXELS_PER_POINT );
            _sumPlot.setStroke( SUM_STROKE );
            _sumPlot.setBorderColor( SUM_COLOR );
            _chartGraphic.addDataSetGraphic( _sumPlot );
        }
        
        // Close button
        _minimizeButton = new PhetImageGraphic( component, FourierConstants.MINIMIZE_BUTTON_IMAGE );
        addGraphic( _minimizeButton, CONTROLS_LAYER );
        _minimizeButton.centerRegistrationPoint();
        _minimizeButton.setLocation( (_minimizeButton.getWidth()/2) + 10, _minimizeButton.getHeight()/2 + 5 );
        
        // Interactivity
        {
            _backgroundGraphic.setIgnoreMouse( true );
            _titleGraphic.setIgnoreMouse( true );
            _chartGraphic.setIgnoreMouse( true );
            
            _minimizeButton.setCursorHand();
        }
    }
    
    /**
     * Call this method prior to releasing all references to an object of this type.
     */
    public void cleanup() {
        _fourierSeries.removeObserver( this );
        _fourierSeries = null;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Gets a reference to the "minimize" button.
     * 
     * @return minimize button
     */
    public PhetImageGraphic getMinimizeButton() {
        return _minimizeButton;
    }
    
    /**
     * Sets the height of this graphic.
     * 
     * @param height
     */
    public void setHeight( int height ) {
        if ( height >= MIN_HEIGHT ) {
            _backgroundGraphic.setShape( new Rectangle( 0, 0, BACKGROUND_SIZE.width, height ) );
            _chartGraphic.setChartSize( CHART_SIZE.width, height - 75 );
            _titleGraphic.setLocation( TITLE_LOCATION.x, height / 2 );
            setBoundsDirty();
        }
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the view to match the model.
     */
    public void update() {
        if ( isVisible() ) {
            _sumPlot.updateDataSet();
            _chartGraphic.autoscaleY( _sumPlot.getMaxAmplitude() * AUTOSCALE_FACTOR );
        }
    }
}
