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

public class BoxDoorGraphic extends DefaultInteractiveGraphic {
    private int x;
    private int y;
    private int minX;
    private int minY;
    private int maxX;
    private int maxY;
    private PhetImageGraphic imageGraphic;

    public BoxDoorGraphic( PhetImageGraphic imageGraphic, int x, int y, int minX, int minY, int maxX, int maxY ) {
        super( imageGraphic );
        this.imageGraphic = imageGraphic;
        this.x = x;
        this.y = y;
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;

        this.addCursorHandBehavior();
        this.addTranslationBehavior( new DoorTranslator() );
    }

    private class DoorTranslator implements Translatable {
        public DoorTranslator() {
            translate( 0, 0 );
        }

        public void translate( double dx, double dy ) {
            x = (int)Math.min( maxX, Math.max( minX, x + dx ) );
            y = (int)Math.min( maxY, Math.max( minY, y + dy ) );
            imageGraphic.setPosition( x, y );
        }
    }
}
