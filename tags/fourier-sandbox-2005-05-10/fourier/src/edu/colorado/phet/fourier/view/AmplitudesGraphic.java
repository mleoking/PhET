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

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.fourier.model.Harmonic;
import edu.colorado.phet.fourier.model.FourierSeries;


/**
 * AmplitudesGraphic
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class AmplitudesGraphic extends GraphicLayerSet implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Layers
    private static final double TITLE_LAYER = 1;
    private static final double OUTLINE_LAYER = 2;
    private static final double AXES_LAYER = 3;
    private static final double TICKS_LAYER = 4;
    private static final double LABELS_LAYER = 5;
    private static final double SLIDERS_LAYER = 6;
    
    // Title
    private static final Font TITLE_FONT = new Font( "Lucida Sans", Font.PLAIN, 20 );
    private static final Color TITLE_COLOR = Color.BLUE;
    
    // Outline
    private static final int OUTLINE_WIDTH = 600;
    private static final int OUTLINE_HEIGHT = 175;
    private static final Color OUTLINE_COLOR = Color.BLACK;
    
    // Axes
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
    private static final String X_MAX_LABEL = "1";
    private static final String X_MIN_LABEL = "-1";
    private static final String X_ZERO_LABEL = "0";
    private static final String Y_AXIS_LABEL = "n";

    // Sliders
    private static final int SLIDER_SPACING = 10; // space between sliders
        
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private FourierSeries _harmonicSeriesModel;
    private GraphicLayerSet _sliders;
    private int _previousNumberOfharmonics;
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------
    
    public AmplitudesGraphic( Component component, FourierSeries harmonicSeriesModel ) {
        super( component );
        
        _harmonicSeriesModel = harmonicSeriesModel;
        _harmonicSeriesModel.addObserver( this );

        // Title
        String title = SimStrings.get( "AmplitudesGraphic.title" );
        PhetTextGraphic titleGraphic = new PhetTextGraphic( component, TITLE_FONT, title, TITLE_COLOR );
        titleGraphic.centerRegistrationPoint();
        titleGraphic.rotate( -( Math.PI / 2 ) );
        titleGraphic.setLocation( -10, 0 );
        addGraphic( titleGraphic, TITLE_LAYER );
        
        // Outline box
        Rectangle outlineRectangle = new Rectangle( 0, -( OUTLINE_HEIGHT / 2 ), OUTLINE_WIDTH, OUTLINE_HEIGHT );
        PhetShapeGraphic outlineGraphic = new PhetShapeGraphic( component );
        outlineGraphic.setShape( outlineRectangle );
        outlineGraphic.setBorderColor( OUTLINE_COLOR );
        outlineGraphic.setStroke( new BasicStroke( 1f ) );
        outlineGraphic.setLocation( 0, 0 );
        addGraphic( outlineGraphic, OUTLINE_LAYER );

        // X Axis
        Line2D xAxisShape = new Line2D.Double( 0, 0, OUTLINE_WIDTH, 0 );
        PhetShapeGraphic xAxisGraphic = new PhetShapeGraphic( component );
        xAxisGraphic.setShape( xAxisShape );
        xAxisGraphic.setBorderColor( AXES_COLOR );
        xAxisGraphic.setStroke( AXES_STROKE );
        xAxisGraphic.setLocation( 0, 0 );
        addGraphic( xAxisGraphic, AXES_LAYER );
        
        // Y Axis
        Line2D yAxisShape = new Line2D.Double( 0, -( OUTLINE_HEIGHT / 2 ), 0, +( OUTLINE_HEIGHT / 2 ) );
        PhetShapeGraphic yAxisGraphic = new PhetShapeGraphic( component );
        yAxisGraphic.setShape( yAxisShape );
        yAxisGraphic.setBorderColor( AXES_COLOR );
        yAxisGraphic.setStroke( AXES_STROKE );
        yAxisGraphic.setLocation( 0, 0 );
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
        
        // X Axis labels
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
        
        // Y axis labels
        {
            PhetTextGraphic nLabelGraphic = new PhetTextGraphic( component, TICKS_LABEL_FONT, Y_AXIS_LABEL, TICKS_LABEL_COLOR );
            nLabelGraphic.setJustification( PhetTextGraphic.WEST );
            nLabelGraphic.setLocation( OUTLINE_WIDTH + 5, 0 );
            addGraphic( nLabelGraphic, LABELS_LAYER );
        }
        
        // Minor tick marks & horizontal lines
        {
            int numberOfMinorTicks = 18;
            double deltaY = ( OUTLINE_HEIGHT / 2 ) / ( numberOfMinorTicks / 2 );
            Shape minorTickShape = new Line2D.Double( -MINOR_TICK_LENGTH, 0, 0, 0 );
            Shape lineShape = new Line2D.Double( 0, 0, OUTLINE_WIDTH, 0 );
            
            for ( int i = 1; i <= ( numberOfMinorTicks / 2 ); i++ ) {

                int y = (int) ( i * deltaY );

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
                
                Color minorAxesColor = ( i == 5 ) ? MINOR_AXES_COLOR2 : MINOR_AXES_COLOR1;
                PhetShapeGraphic positiveLineGraphic = new PhetShapeGraphic( component );
                positiveLineGraphic.setShape( lineShape );
                positiveLineGraphic.setBorderColor( minorAxesColor );
                positiveLineGraphic.setStroke( MINOR_AXES_STROKE );
                positiveLineGraphic.setLocation( 0, -y );
                addGraphic( positiveLineGraphic, AXES_LAYER );
                
                PhetShapeGraphic negativeLineGraphic = new PhetShapeGraphic( component );
                negativeLineGraphic.setShape( lineShape );
                negativeLineGraphic.setBorderColor( minorAxesColor );
                negativeLineGraphic.setStroke( MINOR_AXES_STROKE );
                negativeLineGraphic.setLocation( 0, y );
                addGraphic( negativeLineGraphic, AXES_LAYER );
            }
        }

        // Amplitude sliders
        _sliders = new GraphicLayerSet( component );
        addGraphic( _sliders, SLIDERS_LAYER );
        
        // Interactivity
        {
            outlineGraphic.setIgnoreMouse( true );
            xAxisGraphic.setIgnoreMouse( true );
            yAxisGraphic.setIgnoreMouse( true );
        }
        
        _previousNumberOfharmonics = -1; // force update
        update();
    }
    
    public void finalize() {
        _harmonicSeriesModel.removeObserver( this );
        _harmonicSeriesModel = null;
    }

    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    public void update() {

        int numberOfHarmonics = _harmonicSeriesModel.getNumberOfHarmonics();
        
        if ( _previousNumberOfharmonics != numberOfHarmonics ) {
            
            _sliders.clear();
            
            int totalSpace = ( numberOfHarmonics + 1 ) * SLIDER_SPACING;
            int barWidth = ( OUTLINE_WIDTH - totalSpace ) / numberOfHarmonics;
            double deltaWavelength = ( VisibleColor.MAX_WAVELENGTH - VisibleColor.MIN_WAVELENGTH ) / ( numberOfHarmonics - 1 );

            for ( int i = 0; i < numberOfHarmonics; i++ ) {

                // Get the ith harmonic.
                Harmonic harmonic = _harmonicSeriesModel.getHarmonic( i );

                // Create a slider to control the harmonic's amplitude.
                HarmonicSlider slider = new HarmonicSlider( getComponent(), harmonic );
                _sliders.addGraphic( slider );

                // Slider color, from the visible spectrum.
                slider.setMaxSize( barWidth, OUTLINE_HEIGHT );
                double wavelength = VisibleColor.MAX_WAVELENGTH - ( i * deltaWavelength );
                Color trackColor = VisibleColor.wavelengthToColor( wavelength );
                slider.setTrackColor( trackColor );

                // Slider location.
                int x = ( ( i + 1 ) * SLIDER_SPACING ) + ( i * barWidth ) + ( barWidth / 2 );
                slider.setLocation( x, 0 );
            }

            _previousNumberOfharmonics = numberOfHarmonics;
        }
    }
}
