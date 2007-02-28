/**
 * Class: PumpHandleGraphic
 * Package: edu.colorado.phet.idealgas.graphics
 * Author: Another Guy
 * Date: Jan 14, 2004
 */
package edu.colorado.phet.idealgas.view;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.Clock;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.graphics.mousecontrols.translation.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.translation.TranslationListener;
import edu.colorado.phet.idealgas.model.Pump;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * The graphic for the pump handle and the shaft attached to it. Mouse
 * hits are only detected for points within the handle itself.
 */
public class PumpHandleGraphic extends CompositePhetGraphic {
    private Pump pump;
    private PhetImageGraphic image;
    private int lastYPumped;
    private int lastYTracked = Integer.MAX_VALUE;
    private Rectangle hitRect = new Rectangle();

    public PumpHandleGraphic( Component component, Pump pump, final PhetImageGraphic image, int x, int y,
                              int minX, int minY,
                              int maxX, int maxY ) {
        super( component );
        this.pump = pump;
        this.image = image;
        image.setLocation( x, y );
        updateHitRect();
        this.setCursor( Cursor.getPredefinedCursor( Cursor.N_RESIZE_CURSOR ) );
        addTranslationListener( new PumpHandleTranslator( x, y, minX, minY, maxX, maxY ) );
    }

    public void paint( Graphics2D g ) {
        image.paint( g );
    }

    public void fireMouseDragged( MouseEvent e ) {
        super.fireMouseDragged( e );

        // Determine if we should pump now. We do it if the mouse is moving down
        int yNew = e.getY();
        if( yNew > lastYTracked ) {
            int numMolecules = ( yNew - lastYPumped ) / 2;
            pump.pump( numMolecules );
            lastYPumped = yNew;

            // If the simulation is paused, unpause it
            IClock clock = PhetApplication.instance().getActiveModule().getClock();
            if( clock.isPaused() == true ) {
                clock.pause();
            }
        }
        lastYTracked = yNew;
    }

    private void updateHitRect() {
        hitRect.setRect( image.getLocation().x, image.getLocation().y, image.getWidth(), 20 );
    }

    protected Rectangle determineBounds() {
        return image.getBounds();
    }

    protected PhetGraphic getHandler( Point p ) {
        if( getVisibilityFlag() && hitRect.contains( p ) ) {
            return this;
        }
        else {
            return null;
        }
    }

    private class PumpHandleTranslator implements TranslationListener {
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
            PumpHandleGraphic.this.setLocation( image.getLocation() );
            updateHitRect();
        }
    }
}
