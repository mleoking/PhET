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

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.model.FourierComponent;
import edu.colorado.phet.fourier.model.FourierSeries;
import edu.colorado.phet.fourier.util.FourierUtils;


/**
 * ComponentsGraphic
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ComponentsGraphic extends GraphicLayerSet implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double TITLE_LAYER = 1;
    private static final double OUTLINE_LAYER = 2;
    private static final double LABELS_LAYER = 3;
    private static final double WAVES_LAYER = 4;
    
    // Title
    private static final Font TITLE_FONT = new Font( "Lucida Sans", Font.PLAIN, 20 );
    private static final Color TITLE_COLOR = Color.BLUE;
    private static final int TITLE_X_OFFSET = -15; // from origin
    
    private static final int OUTLINE_WIDTH = 600;
    private static final int OUTLINE_HEIGHT = 175;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private FourierSeries _fourierSeriesModel;
    private CompositePhetGraphic _wavesGraphic;
    private int _previousNumberOfComponents;
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------
    
    public ComponentsGraphic( Component component, FourierSeries fourierSeriesModel ) {
        super( component );
        
        _fourierSeriesModel = fourierSeriesModel;
        _fourierSeriesModel.addObserver( this );
        
        // Title
        String title = SimStrings.get( "ComponentsGraphic.title" );
        PhetTextGraphic titleGraphic = new PhetTextGraphic( component, TITLE_FONT, title, TITLE_COLOR );
        titleGraphic.centerRegistrationPoint();
        titleGraphic.rotate( -( Math.PI / 2 ) );
        titleGraphic.setLocation( TITLE_X_OFFSET, 0 );
        addGraphic( titleGraphic, TITLE_LAYER );
        
        // Outline
        PhetShapeGraphic outlineGraphic = new PhetShapeGraphic( component );
        Rectangle outlineRectangle = new Rectangle( 0, -OUTLINE_HEIGHT/2, OUTLINE_WIDTH, OUTLINE_HEIGHT );
        outlineGraphic.setShape( outlineRectangle );
        outlineGraphic.setBorderColor( Color.BLACK );
        outlineGraphic.setStroke( new BasicStroke( 1f ) );
        addGraphic( outlineGraphic, OUTLINE_LAYER );
        
        // Waves
        _wavesGraphic = new CompositePhetGraphic( component );
        addGraphic( _wavesGraphic, WAVES_LAYER );
        
        // Interactivity
        titleGraphic.setIgnoreMouse( true );
        outlineGraphic.setIgnoreMouse( true );
        _wavesGraphic.setIgnoreMouse( true );
        
        _previousNumberOfComponents = -1; // force update
        update();
    }
    
    public void finalize() {
        _fourierSeriesModel.removeObserver( this );
        _fourierSeriesModel = null;
    }

    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    public void update() {
        
        int numberOfComponents = _fourierSeriesModel.getNumberOfComponents();
        
        if ( _previousNumberOfComponents != numberOfComponents ) {
            
            _wavesGraphic.clear();
            
            for ( int i = 0; i < numberOfComponents; i++ ) {
                
                FourierComponent fourierComponent = (FourierComponent) _fourierSeriesModel.getComponent( i );
                double amplitude = fourierComponent.getAmplitude();
                Color color = FourierUtils.calculateColor( _fourierSeriesModel, i );
                
                SineWaveGraphic waveGraphic = new SineWaveGraphic( getComponent(), fourierComponent );
                waveGraphic.setColor( color );
                waveGraphic.setViewportSize( OUTLINE_WIDTH, OUTLINE_HEIGHT );
                waveGraphic.setLocation( OUTLINE_WIDTH/2, 0 );
                waveGraphic.update();
                
                _wavesGraphic.addGraphic( waveGraphic );
            }
            
            repaint();
        }
    }
}
