/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.shaper.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.shaper.model.FourierSeries;


/**
 * Rainbow
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class Rainbow extends GraphicLayerSet {

    private static final int WIDTH = 27;
    private static final int HEIGHT = 650;
    private static final int SPACING = 10;
    
    public Rainbow( Component component, FourierSeries fourierSeries ) {
        super( component );
        
        setIgnoreMouse( true );
        
        int numberOfHarmonics = fourierSeries.getNumberOfHarmonics();
        int x = 0;
        for ( int i = 0; i < numberOfHarmonics; i++ ) {
            Rectangle2D rectangle = new Rectangle2D.Double( x, 0, WIDTH, HEIGHT );
            Color color = HarmonicColors.getInstance().getColor( i );
            PhetShapeGraphic shapeGraphic = new PhetShapeGraphic( component );
            shapeGraphic.setShape( rectangle );
            shapeGraphic.setColor( color );
            addGraphic( shapeGraphic );
            x += WIDTH + SPACING;
        }
    }
}
