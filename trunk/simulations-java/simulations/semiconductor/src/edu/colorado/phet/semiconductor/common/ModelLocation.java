/*, 2003.*/
package edu.colorado.phet.semiconductor.common;

import java.awt.*;
import java.util.ArrayList;

import edu.colorado.phet.semiconductor.macro.doping.ViewChangeListener;
import edu.colorado.phet.semiconductor.util.math.PhetVector;
import edu.colorado.phet.semiconductor.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.semiconductor.phetcommon.view.graphics.transforms.TransformListener;

/**
 * User: Sam Reid
 * Date: Mar 1, 2004
 * Time: 2:19:43 PM
 */
public class ModelLocation {
    PhetVector modelLocation;
    private Point viewLocation;
    ArrayList listeners = new ArrayList();

    public ModelLocation( double x, double y, final ModelViewTransform2D transform ) {
        this.modelLocation = new PhetVector( x, y );
        transform.addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2D mvt ) {
                viewLocation = transform.modelToView( modelLocation );
                for ( int i = 0; i < listeners.size(); i++ ) {
                    ViewChangeListener viewChangeListener = (ViewChangeListener) listeners.get( i );
                    Point viewLoc = getViewLocation();
                    viewChangeListener.viewCoordinateChanged( viewLoc.x, viewLoc.y );
                }
            }
        } );
    }

    public Point getViewLocation() {
        return new Point( viewLocation.x, viewLocation.y );
    }

    public void addViewChangeListener( ViewChangeListener vcl ) {
        listeners.add( vcl );
    }
}
