/**
 * Class: MeterStickGraphic
 * Package: edu.colorado.phet.sound.view
 * Author: Another Guy
 * Date: Aug 9, 2004
 */
package edu.colorado.phet.sound.view;

import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.mousecontrols.Translatable;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;

import java.awt.geom.Point2D;
import java.awt.*;
import java.awt.event.MouseEvent;

public class MeterStickGraphic extends DefaultInteractiveGraphic {
    private Component component;

    public MeterStickGraphic( Component component, PhetImageGraphic meterStickImg, Point2D.Double location ) {
        super( meterStickImg );
        this.component = component;
        this.addCursorHandBehavior();
        this.addTranslationBehavior( new ImageTranslator( meterStickImg, location ) );
    }

    public void mouseDragged( MouseEvent e ) {
        super.mouseDragged( e );
        component.repaint();
    }
    //    int cnt = 0;
    //    public void mouseDragged( MouseEvent e ) {
    //        if( cnt++ % 3 == 0 ) {
    //            super.mouseDragged( e );
    //        }
    //    }

    private class ImageTranslator implements Translatable {
        private PhetImageGraphic img;
        private Point2D.Double location;

        ImageTranslator( PhetImageGraphic img, Point2D.Double location ) {
            this.img = img;
            this.location = location;
            translate( 0, 0 );
        }

        public void translate( double dx, double dy ) {
            location.setLocation( location.getX() + dx, location.getY() + dy );
            img.setPosition( (int)location.getX(), (int)location.getY() );
        }
    }
}
