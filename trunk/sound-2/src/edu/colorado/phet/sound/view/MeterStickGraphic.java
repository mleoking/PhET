/**
 * Class: MeterStickGraphic
 * Package: edu.colorado.phet.sound.view
 * Author: Another Guy
 * Date: Aug 9, 2004
 */
package edu.colorado.phet.sound.view;

//import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
//import edu.colorado.phet.common.view.graphics.mousecontrols.Translatable;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.util.Translatable;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

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
