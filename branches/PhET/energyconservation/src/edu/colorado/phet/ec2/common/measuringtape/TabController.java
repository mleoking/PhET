/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.ec2.common.measuringtape;

import edu.colorado.phet.coreadditions.controllers.AbstractShape;
import edu.colorado.phet.coreadditions.controllers.MouseHandler;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;
import edu.colorado.phet.coreadditions.graphics2.Translatable;
import edu.colorado.phet.coreadditions.graphics2.TranslationController;

import java.awt.event.MouseEvent;

/**
 * User: Sam Reid
 * Date: Sep 16, 2003
 * Time: 11:39:19 PM
 * Copyright (c) Sep 16, 2003 by Sam Reid
 */
public class TabController implements MouseHandler {
    TranslationController tc;

    public TabController( AbstractShape shape, ModelViewTransform2d transform, final MeasuringTape tape ) {
        tc = new TranslationController( shape, new Translatable() {
            public void translate( double dx, double dy ) {
                tape.translateDestination( dx, dy );
            }
        }, transform );
    }

    public boolean canHandleMousePress( MouseEvent event ) {
        return tc.canHandleMousePress( event );
    }

    public void mousePressed( MouseEvent event ) {
        tc.mousePressed( event );
    }

    public void mouseDragged( MouseEvent event ) {
        tc.mouseDragged( event );
    }

    public void mouseReleased( MouseEvent event ) {
        tc.mouseReleased( event );
    }

    public void mouseEntered( MouseEvent event ) {
        tc.mouseEntered( event );
    }

    public void mouseExited( MouseEvent event ) {
        tc.mouseExited( event );
    }
}
