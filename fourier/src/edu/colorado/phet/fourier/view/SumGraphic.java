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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.fourier.model.HarmonicSeries;


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
    
    private static final double OUTLINE_LAYER = 1;
    private static final double LABELS_LAYER = 2;
    
    private static final int OUTLINE_WIDTH = 600;
    private static final int OUTLINE_HEIGHT = 175;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private HarmonicSeries _harmonicSeriesModel;
    private Rectangle _outlineRectangle;
    private PhetShapeGraphic _outlineGraphic;
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------
    
    public SumGraphic( Component component, HarmonicSeries harmonicSeriesModel ) {
        super( component );
        
        _harmonicSeriesModel = harmonicSeriesModel;
        _harmonicSeriesModel.addObserver( this );
        
        _outlineGraphic = new PhetShapeGraphic( component );
        _outlineRectangle = new Rectangle( 0, -OUTLINE_HEIGHT/2, OUTLINE_WIDTH, OUTLINE_HEIGHT );
        _outlineGraphic.setShape( _outlineRectangle );
        _outlineGraphic.setBorderColor( Color.BLACK );
        _outlineGraphic.setStroke( new BasicStroke( 1f ) );
        addGraphic( _outlineGraphic, OUTLINE_LAYER );
        
        // Interactivity
        _outlineGraphic.setIgnoreMouse( true );
        
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
//        System.out.println( "SumGraphic.update" );//XXX
    }
}
