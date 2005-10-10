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
import edu.colorado.phet.common.view.phetgraphics.*;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.charts.FourierSumPlot;
import edu.colorado.phet.fourier.model.FourierSeries;
import edu.colorado.phet.fourier.model.RandomFourierSeries;


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
    private static final Font TITLE_FONT = new Font( FourierConstants.FONT_NAME, Font.PLAIN, 20 );
    private static final Color TITLE_COLOR = Color.BLUE;
    private static final Point TITLE_LOCATION = new Point( 40, 135 );
    
    // Instruction parameters
    private static final Font INSTRUCTIONS_FONT = new Font( FourierConstants.FONT_NAME, Font.PLAIN, 22 );
    private static final Color INSTRUCTIONS_COLOR = Color.MAGENTA;
    private static final Point INSTRUCTIONS_LOCATION = new Point( 85, 10 );
    
    // Chart parameters
    private static final double L = FourierConstants.L; // do not change!
    private static final double X_RANGE_START = ( L / 2 );
    private static final double X_RANGE_MIN = ( L / 4 );
    private static final double X_RANGE_MAX = ( 2 * L );
    private static final double Y_RANGE_START = FourierConstants.MAX_HARMONIC_AMPLITUDE;
    private static final double Y_RANGE_MIN = FourierConstants.MAX_HARMONIC_AMPLITUDE;
    private static final double Y_RANGE_MAX = 12.0;
    private static final Range2D CHART_RANGE = new Range2D( -X_RANGE_START, -Y_RANGE_START, X_RANGE_START, Y_RANGE_START );
    private static final Dimension CHART_SIZE = new Dimension( 540, 135 );
    
    // Wave parameters
    private static final Stroke USER_SUM_STROKE = new BasicStroke( 1f );
    private static final Color USER_SUM_COLOR = Color.BLACK;
    private static final Stroke RANDOM_SUM_STROKE = new BasicStroke( 3f );
    private static final Color RANDOM_SUM_COLOR = Color.MAGENTA;
    private static final double PIXELS_PER_POINT = 1;
    
    // Autoscaling
    private static final double AUTOSCALE_FACTOR = 1.25; // multiple max amplitude by this amount when autoscaling
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private FourierSeries _userFourierSeries;
    private RandomFourierSeries _randomFourierSeries;
    private PhetShapeGraphic _backgroundGraphic;
    private PhetTextGraphic _titleGraphic;
    private PhetImageGraphic _minimizeButton;
    private GameSumChart _chartGraphic;
    private FourierSumPlot _userSumPlot;
    private FourierSumPlot _randomSumPlot;
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param component the parent Component
     * @param userFourierSeries the Fourier series constructed by the user
     * @param randomFourierSeries the Fourier series that is randomly generated
     */
    public GameSumView( Component component, FourierSeries userFourierSeries, RandomFourierSeries randomFourierSeries ) {
        super( component );

        // Enable antialiasing for all children.
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
        
        // Model
        _userFourierSeries = userFourierSeries;
        _userFourierSeries.addObserver( this );
        _randomFourierSeries = randomFourierSeries;
        _randomFourierSeries.addObserver( this );
        
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
        
        // Instructions
        HTMLGraphic instructions = new HTMLGraphic( component );
        instructions.setFont( INSTRUCTIONS_FONT );
        instructions.setColor( INSTRUCTIONS_COLOR );
        instructions.setHTML( SimStrings.get( "GameSumView.instructions" ) );
        instructions.setLocation( INSTRUCTIONS_LOCATION );
        addGraphic( instructions, TITLE_LAYER );
        
        // Chart
        {
            _chartGraphic = new GameSumChart( component, CHART_RANGE, CHART_SIZE );
            addGraphic( _chartGraphic, CHART_LAYER );
            _chartGraphic.setRegistrationPoint( 0, 0 );
            _chartGraphic.setLocation( 60, 50 );
            _chartGraphic.setXAxisTitle( "x (mm)" ); 
         
            // Random sum plot
            _randomSumPlot = new FourierSumPlot( getComponent(), _chartGraphic, _randomFourierSeries );
            _randomSumPlot.setPeriod( L );
            _randomSumPlot.setPixelsPerPoint( PIXELS_PER_POINT );
            _randomSumPlot.setStroke( RANDOM_SUM_STROKE );
            _randomSumPlot.setBorderColor( RANDOM_SUM_COLOR );
            _chartGraphic.addDataSetGraphic( _randomSumPlot );
            _chartGraphic.autoscaleY( _randomSumPlot.getMaxAmplitude() * AUTOSCALE_FACTOR );
            
            // User's sum plot
            _userSumPlot = new FourierSumPlot( getComponent(), _chartGraphic, _userFourierSeries );
            _userSumPlot.setPeriod( L );
            _userSumPlot.setPixelsPerPoint( PIXELS_PER_POINT );
            _userSumPlot.setStroke( USER_SUM_STROKE );
            _userSumPlot.setBorderColor( USER_SUM_COLOR );
            _chartGraphic.addDataSetGraphic( _userSumPlot );

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
        _userFourierSeries.removeObserver( this );
        _userFourierSeries = null;
        _randomFourierSeries.removeObserver( this );
        _randomFourierSeries = null;
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
            /* Update the plots to match their models.
             * Note: It would be more efficient to update only the data set 
             * that has changed, but we don't have that information.  So we 
             * update both data sets.  No one has complained about the 
             * performance of this. 
             */
            _userSumPlot.updateDataSet();
            _randomSumPlot.updateDataSet();
            
            // Auto scale the chart so that both graphs are fully visible.
            double maxY = Math.max( _userSumPlot.getMaxAmplitude(), _randomSumPlot.getMaxAmplitude() );
            _chartGraphic.autoscaleY( maxY * AUTOSCALE_FACTOR );
        }
    }
}
