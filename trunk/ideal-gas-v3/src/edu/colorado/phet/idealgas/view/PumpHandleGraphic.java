/**
 * Class: PumpHandleGraphic
 * Package: edu.colorado.phet.idealgas.graphics
 * Author: Another Guy
 * Date: Jan 14, 2004
 */
package edu.colorado.phet.idealgas.view;

import edu.colorado.phet.common.view.graphics.Boundary;
import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.mousecontrols.Translatable;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.idealgas.model.Pump;

import java.awt.event.MouseEvent;

/**
 * This class is provided just to overide isInHotSpot to make the pump
 * handle work correctly. The behavior for controlling mouse-related
 * movement of the pump handle is all contained in BaseIdealGasApparatusPanel.
 * This isn't really the right way to do it, but it is how things were
 * done way back when the application was originally written.
 */
public class PumpHandleGraphic extends DefaultInteractiveGraphic {
    private Pump pump;
    private PhetImageGraphic image;
    private int lastYPumped;
    private int lastYTracked = Integer.MAX_VALUE;

    public PumpHandleGraphic( Pump pump, final PhetImageGraphic image, int x, int y,
                              int minX, int minY,
                              int maxX, int maxY ) {
        super( image );
        this.pump = pump;
        this.image = image;
        image.setPosition( x, y );

        Boundary hitArea = new Boundary() {
            public boolean contains( int x, int y ) {
                boolean result = x >= image.getBounds().getMinX() && x <= image.getBounds().getMaxX()
                                 && y >= image.getBounds().getMinY() && y <= image.getBounds().getMinY() + 10;
                return result;
            }
        };
        super.setBoundary( hitArea );
        this.addCursorHandBehavior();
        this.addTranslationBehavior( new PumpHandleTranslator( x, y, minX, minY, maxX, maxY ) );
        this.setGraphic( image );
    }

    public void mouseDragged( MouseEvent e ) {
        super.mouseDragged( e );

        try {
            Thread.sleep( 20 );
        }
        catch( InterruptedException e1 ) {
            e1.printStackTrace();
        }

        // Determine if we should pump now. We do it if the mouse is moving down
        int yNew = e.getY();
        if( yNew > lastYTracked ) {
            int numMolecules = ( yNew - lastYPumped ) / 2;
            pump.pump( numMolecules );
            lastYPumped = yNew;
        }
        lastYTracked = yNew;
    }

    private class PumpHandleTranslator implements Translatable {
        private int x;
        private int y;
        private int minX;
        private int minY;
        private int maxX;
        private int maxY;

        public PumpHandleTranslator( int x, int y, int minX, int minY, int maxX, int maxY ) {
            this.x = x;
            this.y = y;
            this.minX = minX;
            this.minY = minY;
            this.maxX = maxX;
            this.maxY = maxY;
        }

        public void translate( double dx, double dy ) {
            x = (int)Math.min( maxX, Math.max( minX, x + dx ) );
            y = (int)Math.min( maxY, Math.max( minY, y + dy ) );
            image.setPosition( x, y );
        }
    }
}
