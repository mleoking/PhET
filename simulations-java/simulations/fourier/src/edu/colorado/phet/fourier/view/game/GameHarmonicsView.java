// Copyright 2002-2011, University of Colorado

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
import java.util.ArrayList;

import edu.colorado.phet.common.charts.Chart;
import edu.colorado.phet.common.charts.Range2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.FourierResources;
import edu.colorado.phet.fourier.charts.HarmonicPlot;
import edu.colorado.phet.fourier.event.HarmonicFocusEvent;
import edu.colorado.phet.fourier.event.HarmonicFocusListener;
import edu.colorado.phet.fourier.model.FourierSeries;
import edu.colorado.phet.fourier.model.Harmonic;
import edu.colorado.phet.fourier.view.HarmonicColors;
import edu.colorado.phet.fourier.view.discrete.DiscreteHarmonicsChart;


/**
 * GameHarmonicsView is the "Harmonics" view in the "Game" module.
 * It displays a collection of harmonic waveforms.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class GameHarmonicsView extends GraphicLayerSet implements HarmonicFocusListener {

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
    private static final Font TITLE_FONT = new PhetFont( Font.PLAIN, 20 );
    private static final Color TITLE_COLOR = Color.BLUE;
    private static final Point TITLE_LOCATION = new Point( 40, 115 );

    // Chart parameters
    private static final double L = FourierConstants.L;
    private static final double X_RANGE_START = ( L / 2 );
    private static final double X_RANGE_MIN = ( L / 4 );
    private static final double X_RANGE_MAX = ( 2 * L );
    private static final double Y_RANGE_START = FourierConstants.MAX_HARMONIC_AMPLITUDE + ( 0.05 * FourierConstants.MAX_HARMONIC_AMPLITUDE );
    private static final Range2D CHART_RANGE = new Range2D( -X_RANGE_START, -Y_RANGE_START, X_RANGE_START, Y_RANGE_START );
    private static final Dimension CHART_SIZE = new Dimension( 540, 135 );

    // Wave parameters
    private static final Stroke WAVE_NORMAL_STROKE = new BasicStroke( 1f );
    private static final Stroke WAVE_FOCUS_STROKE = new BasicStroke( 2f );
    private static final Stroke WAVE_DIMMED_STROKE = new BasicStroke( 0.5f );
    private static final Color WAVE_DIMMED_COLOR = Color.GRAY;
    private static final double[] PIXELS_PER_POINT = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private FourierSeries _fourierSeries;
    private PhetShapeGraphic _backgroundGraphic;
    private PhetTextGraphic _titleGraphic;
    private PhetImageGraphic _minimizeButton;
    private DiscreteHarmonicsChart _chartGraphic;
    private ArrayList _harmonicPlots; // array of HarmonicPlot

    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     *
     * @param component     the parent Component
     * @param fourierSeries the Fourier series that this view displays
     */
    public GameHarmonicsView( Component component, FourierSeries fourierSeries ) {
        super( component );

        // Enable antialiasing for all children.
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );

        // Model
        _fourierSeries = fourierSeries;

        // Background
        _backgroundGraphic = new PhetShapeGraphic( component );
        _backgroundGraphic.setShape( new Rectangle( 0, 0, BACKGROUND_SIZE.width, BACKGROUND_SIZE.height ) );
        _backgroundGraphic.setPaint( BACKGROUND_COLOR );
        _backgroundGraphic.setStroke( BACKGROUND_STROKE );
        _backgroundGraphic.setBorderColor( BACKGROUND_BORDER_COLOR );
        addGraphic( _backgroundGraphic, BACKGROUND_LAYER );
        _backgroundGraphic.setLocation( 0, 0 );

        // Title
        String title = FourierResources.getString( "GameHarmonicsView.title" );
        _titleGraphic = new PhetTextGraphic( component, TITLE_FONT, title, TITLE_COLOR );
        _titleGraphic.centerRegistrationPoint();
        _titleGraphic.rotate( -( Math.PI / 2 ) );
        _titleGraphic.setLocation( TITLE_LOCATION );
        addGraphic( _titleGraphic, TITLE_LAYER );

        // Chart
        {
            _chartGraphic = new DiscreteHarmonicsChart( component, CHART_RANGE, CHART_SIZE );
            addGraphic( _chartGraphic, CHART_LAYER );
            _chartGraphic.setRegistrationPoint( 0, 0 );
            _chartGraphic.setLocation( 60, 50 );
            _chartGraphic.setXAxisTitle( FourierResources.getString( "axis.x.units" ) );
        }

        // Harmonics
        _harmonicPlots = new ArrayList();
        for ( int i = _fourierSeries.getNumberOfHarmonics() - 1; i >= 0; i-- ) {

            HarmonicPlot harmonicPlot = new HarmonicPlot( component, _chartGraphic );
            harmonicPlot.setHarmonic( _fourierSeries.getHarmonic( i ) );
            harmonicPlot.setPeriod( L / ( i + 1 ) );
            harmonicPlot.setPixelsPerPoint( PIXELS_PER_POINT[i] );
            harmonicPlot.setStroke( WAVE_NORMAL_STROKE );
            harmonicPlot.setBorderColor( HarmonicColors.getInstance().getColor( i ) );
            harmonicPlot.setStartX( 0 );

            _harmonicPlots.add( harmonicPlot );
            _chartGraphic.addDataSetGraphic( harmonicPlot );
        }

        // Close button
        _minimizeButton = new PhetImageGraphic( component, FourierConstants.MINIMIZE_BUTTON_IMAGE );
        addGraphic( _minimizeButton, CONTROLS_LAYER );
        _minimizeButton.centerRegistrationPoint();
        _minimizeButton.setLocation( ( _minimizeButton.getWidth() / 2 ) + 10, _minimizeButton.getHeight() / 2 + 5 );

        // Interactivity
        {
            _backgroundGraphic.setIgnoreMouse( true );
            _titleGraphic.setIgnoreMouse( true );
            _chartGraphic.setIgnoreMouse( true );

            _minimizeButton.setCursorHand();
        }
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Gets the chart associated with this graphic.
     *
     * @return the chart
     */
    public Chart getChart() {
        return _chartGraphic;
    }

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

    /**
     * Forces an update of all the plots in the graph.
     */
    public void update() {
        for ( int i = 0; i < _harmonicPlots.size(); i++ ) {
            HarmonicPlot plot = (HarmonicPlot) _harmonicPlots.get( i );
            plot.update();
        }
    }

    //----------------------------------------------------------------------------
    // HarmonicFocusListener implementation
    //----------------------------------------------------------------------------

    /**
     * When a harmonic gains focus, grays out all harmonics except for the one with focus.
     */
    public void focusGained( HarmonicFocusEvent event ) {
        for ( int i = 0; i < _harmonicPlots.size(); i++ ) {
            HarmonicPlot harmonicGraphic = (HarmonicPlot) _harmonicPlots.get( i );
            if ( harmonicGraphic.getHarmonic() != event.getHarmonic() ) {
                harmonicGraphic.setBorderColor( WAVE_DIMMED_COLOR );
                harmonicGraphic.setStroke( WAVE_DIMMED_STROKE );
            }
            else {
                Harmonic harmonic = harmonicGraphic.getHarmonic();
                if ( harmonic != null ) {
                    Color harmonicColor = HarmonicColors.getInstance().getColor( harmonic );
                    harmonicGraphic.setBorderColor( harmonicColor );
                }
                harmonicGraphic.setStroke( WAVE_FOCUS_STROKE );
            }
        }
    }

    /**
     * When a harmonic loses focus, sets all harmonics to their assigned color.
     */
    public void focusLost( HarmonicFocusEvent event ) {
        for ( int i = 0; i < _harmonicPlots.size(); i++ ) {
            HarmonicPlot harmonicGraphic = (HarmonicPlot) _harmonicPlots.get( i );
            Harmonic harmonic = harmonicGraphic.getHarmonic();
            if ( harmonic != null ) {
                Color harmonicColor = HarmonicColors.getInstance().getColor( harmonic );
                harmonicGraphic.setBorderColor( harmonicColor );
            }
            harmonicGraphic.setStroke( WAVE_NORMAL_STROKE );
        }
    }
}
