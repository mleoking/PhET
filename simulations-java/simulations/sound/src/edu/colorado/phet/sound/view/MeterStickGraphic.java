/**
 * Class: MeterStickGraphic
 * Package: edu.colorado.phet.sound.view
 * Author: Another Guy
 * Date: Aug 9, 2004
 */
package edu.colorado.phet.sound.view;

//import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
//import edu.colorado.phet.common.view.graphics.mousecontrols.Translatable;

import java.awt.Component;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetgraphics.view.graphics.mousecontrols.translation.TranslationEvent;
import edu.colorado.phet.common.phetgraphics.view.graphics.mousecontrols.translation.TranslationListener;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetImageGraphic;

public class MeterStickGraphic extends CompositePhetGraphic {
    private Component component;

    public MeterStickGraphic( Component component, PhetImageGraphic meterStickImg, Point2D.Double location ) {
        this.component = component;
        addGraphic( meterStickImg );
        this.setCursorHand();
        this.addTranslationListener( new ImageTranslator( meterStickImg, location ) );
    }

    private class ImageTranslator implements TranslationListener {
        private PhetImageGraphic img;
        private Point2D.Double location;

        ImageTranslator( PhetImageGraphic img, Point2D.Double location ) {
            this.img = img;
            this.location = location;
        }

        public void translationOccurred( TranslationEvent event ) {
            location.setLocation( location.getX() + event.getDx(), location.getY() + event.getDy() );
            img.setLocation( (int)location.getX(), (int)location.getY() );
        }
    }
}
