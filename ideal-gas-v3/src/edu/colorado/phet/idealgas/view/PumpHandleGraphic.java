/**
 * Class: PumpHandleGraphic
 * Package: edu.colorado.phet.idealgas.graphics
 * Author: Another Guy
 * Date: Jan 14, 2004
 */
package edu.colorado.phet.idealgas.view;

import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.idealgas.model.Pump;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

/**
 * This class is provided just to overide isInHotSpot to make the pump
* handle work correctly. The behavior for controlling mouse-related
 * movement of the pump handle is all contained in BaseIdealGasApparatusPanel.
 * This isn't really the right way to do it, but it is how things were
 * done way back when the application was originally written.
 */
public class PumpHandleGraphic extends PhetGraphic {
    private Pump pump;
    private PhetImageGraphic image;
    private int lastYPumped;
    private int lastYTracked = Integer.MAX_VALUE;
    private boolean handleHighlighted;
    private Stroke highlightStroke = new BasicStroke( 1 );
    private Rectangle2D highlightRect = new Rectangle2D.Double();

    public PumpHandleGraphic( Component component, Pump pump, final PhetImageGraphic image, int x, int y,
                              int minX, int minY,
                              int maxX, int maxY ) {
        super( component );
        this.pump = pump;
        this.image = image;
        image.setLocation( x, y );
//        image.setPosition( x, y );

//        Boundary hitArea = new Boundary() {
//            public boolean contains( int x, int y ) {
//                boolean result = x >= image.getBounds().getMinX() && x <= image.getBounds().getMaxX()
//                                 && y >= image.getBounds().getMinY() && y <= image.getBounds().getMinY() + 10;
//                return result;
//            }
//        };
//        super.setBoundary( hitArea );
        this.setCursor( Cursor.getPredefinedCursor( Cursor.N_RESIZE_CURSOR ) );
//        this.addCursorBehavior( Cursor.getPredefinedCursor( Cursor.N_RESIZE_CURSOR ) );
        addTranslationListener( new PumpHandleTranslator( x, y, minX, minY, maxX, maxY ) );
//        this.addTranslationBehavior( new PumpHandleTranslator( x, y, minX, minY, maxX, maxY ) );
//        this.setGraphic( image );
    }

    public void paint( Graphics2D g ) {
//        super.paint( g );
        image.paint( g );
    }

    public void fireMouseEntered( MouseEvent e ) {
        super.fireMouseEntered( e );
        handleHighlighted = true;
    }

    public void fireMouseExited( MouseEvent e ) {
        super.fireMouseExited( e );
        handleHighlighted = false;
    }

    public void fireMouseDragged( MouseEvent e ) {
        super.fireMouseDragged( e );

        // Determine if we should pump now. We do it if the mouse is moving down
        int yNew = e.getY();
        if( yNew > lastYTracked ) {
            int numMolecules = ( yNew - lastYPumped ) / 2;
            pump.pump( numMolecules );
            lastYPumped = yNew;
        }
        lastYTracked = yNew;
    }

    protected Rectangle determineBounds() {
        return image.getBounds();
    }

    private class PumpHandleTranslator implements TranslationListener {
//    private class PumpHandleTranslator implements Translatable {
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

        public void translationOccurred( TranslationEvent translationEvent ) {
            double dx = translationEvent.getDx();
            double dy = translationEvent.getDy();
            x = (int)Math.min( maxX, Math.max( minX, x + dx ) );
            y = (int)Math.min( maxY, Math.max( minY, y + dy ) );
            image.setLocation( x, y );
        }
    }
}
