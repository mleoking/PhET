/** Sam Reid*/
package edu.colorado.phet.common_cck.unittests;

import edu.colorado.phet.common_cck.view.graphics.transforms.ModelViewTransform2D;
import junit.framework.TestCase;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Jul 9, 2004
 * Time: 4:08:50 PM
 * Copyright (c) Jul 9, 2004 by Sam Reid
 */
public class TestUprightModelViewTransform2D extends TestCase {
    public void testModelToViewSimple() {
        Rectangle viewBounds = new Rectangle( 0, 0, 100, 100 );
        Rectangle2D.Double modelBounds = new Rectangle2D.Double( 0, 0, 1, 1 );
        ModelViewTransform2D transform = new ModelViewTransform2D( modelBounds, viewBounds, false );
        assertEquals( new Point( 0, 0 ), transform.modelToView( 0, 0 ) );
        assertEquals( new Point( 100, 100 ), transform.modelToView( 1, 1 ) );
        assertEquals( new Point( 50, 50 ), transform.modelToView( .5, .5 ) );
    }

    public void testOffCenterTx() {
        Rectangle viewBounds = new Rectangle( 10, 10, 20, 20 );
        Rectangle2D.Double modelBounds = new Rectangle2D.Double( -1, -1, 2, 2 );
        ModelViewTransform2D transform = new ModelViewTransform2D( modelBounds, viewBounds, false );
        Point result = transform.modelToView( 0, 0 );
        assertEquals( "Should be on-center.", new Point( 20, 20 ), result );
    }

    public void testToAffineTransform() {
        Rectangle viewBounds = new Rectangle( 10, 10, 20, 20 );
        Rectangle2D.Double modelBounds = new Rectangle2D.Double( -1, -1, 2, 2 );
        ModelViewTransform2D mvt2 = new ModelViewTransform2D( modelBounds, viewBounds, false );
        AffineTransform tx = mvt2.getAffineTransform();
        Point2D dst = tx.transform( new Point2D.Double( 0, 0 ), null );
        assertEquals( new Point2D.Double( 20, 20 ), dst );
    }

    public void testToAffineTransform2() {
        Rectangle viewBounds = new Rectangle( 0, 0, 100, 100 );
        Rectangle2D.Double modelBounds = new Rectangle2D.Double( 0, 0, 1, 1 );
        ModelViewTransform2D mvt2 = new ModelViewTransform2D( modelBounds, viewBounds, false );
        AffineTransform tx = mvt2.getAffineTransform();
        Point2D dst = tx.transform( new Point2D.Double( .5, .5 ), null );
        assertEquals( new Point2D.Double( 50, 50 ), dst );
    }

    public void testToAffineTransform3() {
        Rectangle viewBounds = new Rectangle( 50, 50, 50, 50 );
        Rectangle2D.Double modelBounds = new Rectangle2D.Double( 0, 0, 1, 1 );
        ModelViewTransform2D mvt2 = new ModelViewTransform2D( modelBounds, viewBounds, false );
        AffineTransform tx = mvt2.getAffineTransform();
        Point2D dst = tx.transform( new Point2D.Double( .5, .5 ), null );
        assertEquals( new Point2D.Double( 75, 75 ), dst );
    }

}
