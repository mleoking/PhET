/**
 * Class: BoxDoorGraphic
 * Package: edu.colorado.phet.idealgas.view
 * Author: Another Guy
 * Date: Sep 10, 2004
 */
package edu.colorado.phet.idealgas.view;

import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.mousecontrols.Translatable;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.idealgas.model.Box2D;

import java.awt.geom.Point2D;

public class BoxDoorGraphic extends DefaultInteractiveGraphic {
    private int x;
    private int y;
    private int minX;
    private int minY;
    private int maxX;
    private int maxY;
    private Box2D box;
    private PhetImageGraphic imageGraphic;

    public BoxDoorGraphic( PhetImageGraphic imageGraphic,
                           int x, int y, int minX, int minY, int maxX, int maxY,
                           Box2D box ) {
        super( imageGraphic );
        this.imageGraphic = imageGraphic;
        this.x = x;
        this.y = y;
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        this.box = box;

        this.addCursorHandBehavior();
        this.addTranslationBehavior( new DoorTranslator() );
    }

    private class DoorTranslator implements Translatable {
        private Point2D[] opening = new Point2D[2];

        public DoorTranslator() {
            translate( 0, 0 );
        }

        public void translate( double dx, double dy ) {
            x = (int)Math.min( maxX, Math.max( minX, x + dx ) );
            y = (int)Math.min( maxY, Math.max( minY, y + dy ) );
            imageGraphic.setPosition( x, y );

            opening[0] = new Point2D.Double( x + imageGraphic.getBounds().getWidth(),
                                             y + imageGraphic.getBounds().getHeight() + 2 );
            opening[1] = new Point2D.Double( box.getMaxX(),
                                             y + imageGraphic.getBounds().getHeight() + 2 );

            box.setOpening( opening );
        }
    }
}
