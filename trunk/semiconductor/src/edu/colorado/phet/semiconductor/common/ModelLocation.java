/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.semiconductor.common;

import edu.colorado.phet.common.math.PhetVector;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.graphics.transforms.TransformListener;
import edu.colorado.phet.semiconductor.macro.doping.ViewChangeListener;

import java.awt.*;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Mar 1, 2004
 * Time: 2:19:43 PM
 * Copyright (c) Mar 1, 2004 by Sam Reid
 */
public class ModelLocation {
    PhetVector modelLocation;
    private ModelViewTransform2D transform;
    private Point viewLocation;
    ArrayList listeners = new ArrayList();

    public ModelLocation( double x, double y, final ModelViewTransform2D transform ) {
        this.transform = transform;
        this.modelLocation = new PhetVector( x, y );
        transform.addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2D mvt ) {
                viewLocation = transform.modelToView( modelLocation );
                for( int i = 0; i < listeners.size(); i++ ) {
                    ViewChangeListener viewChangeListener = (ViewChangeListener)listeners.get( i );
                    Point viewLoc = getViewLocation();
                    viewChangeListener.viewCoordinateChanged( viewLoc.x, viewLoc.y );
                }
            }
        } );
    }

    public Point getViewLocation() {
        return new Point( viewLocation.x, viewLocation.y );
    }

    public PhetVector getModelLocation() {
        return modelLocation;
    }

    public void addViewChangeListener( ViewChangeListener vcl ) {
        listeners.add( vcl );
    }
}
