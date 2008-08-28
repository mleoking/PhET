/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import edu.colorado.phet.glaciers.GlaciersConstants;
import edu.colorado.phet.glaciers.model.Valley;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * ValleyFloorWorkaroundNode is a workaround to clean up the view of the valley floor for x<0.
 * We don't have a model for the Valley for x<0.  We attempt to match the contour of the valley
 * floor shown in the image by using an array of sample points that were manually obtained from
 * the background image file.  This node draws a bit of the "underground" portion of the image
 * so that it matches our sample points more closely.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ValleyFloorWorkaroundNode extends PPath {

    public ValleyFloorWorkaroundNode( ModelViewTransform mvt ) {
        
        Point2D[] samplePoints = Valley.getNegativeXSamplePoints();
        
        GeneralPath path = new GeneralPath();
        Point2D pView = new Point2D.Double();
        for ( int i = 0; i < samplePoints.length; i++ ) {
            mvt.modelToView( samplePoints[i], pView );
            if ( i == 0 ) {
                path.moveTo( (float)pView.getX(), (float)pView.getY() );
            }
            else {
                path.lineTo( (float)pView.getX(), (float)pView.getY() );      
            }
        }
        path.closePath();
        
        setPathTo( path );
        setPaint( GlaciersConstants.UNDERGROUND_COLOR );
        setStroke( null );
    }
}
