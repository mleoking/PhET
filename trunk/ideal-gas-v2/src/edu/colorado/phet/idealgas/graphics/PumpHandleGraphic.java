/**
 * Class: PumpHandleGraphic
 * Package: edu.colorado.phet.idealgas.graphics
 * Author: Another Guy
 * Date: Jan 14, 2004
 */
package edu.colorado.phet.idealgas.graphics;

//import edu.colorado.phet.controller.Config;
//import edu.colorado.phet.graphics.MovableImageGraphic;
import edu.colorado.phet.idealgas.model.Pump;
import edu.colorado.phet.idealgas.controller.IdealGasConfig;
import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.mousecontrols.Translatable;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.util.SimpleObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * This class is provided just to overide isInHotSpot to make the pump
 * handle work correctly. The behavior for controlling mouse-related
 * movement of the pump handle is all contained in BaseIdealGasApparatusPanel.
 * This isn't really the right way to do it, but it is how things were
 * done way back when the application was originally written.
 */
public class PumpHandleGraphic extends DefaultInteractiveGraphic /*implements SimpleObserver */{
//public class PumpHandleGraphic extends MovableImageGraphic {
    private Pump pump;
    private double lastYPumped;
    private double handleMinY;
    private double minDraggedY;
    private double lastYTracked;
    private Container container;
    private PumpHandleImage handleImage;

    public PumpHandleGraphic( Pump pump, Container container,
                              Image image, float x, float y,
                              float minX, float minY,
                              float maxX, float maxY ) {
        super( new PumpHandleImage( container ));
        this.container = container;
//        pump.addObserver( this );
        this.pump = pump;
        this.lastYTracked = y;
        handleImage = (PumpHandleImage)this.getGraphic();

        this.addCursorHandBehavior();
        this.addTranslationBehavior( new Translatable() {
            public void translate( double dx, double dy ) {
                PumpHandleGraphic.this.pump.moveHandle( dy );
                handleImage.translate( 0, dy );
            }
        } );
    }



    /**
     * The hot spot for this image is just the handle, and does not include the
     * rod part of the image, so we have to override the super class behavior
     *
     * @param p
     * @return
     */
    public boolean isInHotSpot( Point p ) {
        boolean result = p.getX() >= getLocationPoint2D().getX()
                         && p.getX() <= getLocationPoint2D().getX() + getImage().getWidth( null )
                         && p.getY() >= getLocationPoint2D().getY()
                         && p.getY() <= getLocationPoint2D().getY() + 10;
        return result;
    }

//    public void mousePressed( MouseEvent e ) {
//        super.mousePressed( e );
//        if( isSelected() ) {
//            getApparatusPanel().setCursor( Cursor.getPredefinedCursor( Cursor.N_RESIZE_CURSOR ) );
//        }
//    }
//
//    public void mouseDragged( MouseEvent event ) {
//        if( isSelected() ) {
//            handleMinY = event.getPoint().getY() < handleMinY ? event.getPoint().getY() : handleMinY;
//            double yDiff = event.getPoint().getY() - this.getDragStartPt().getY();
//            double xCurr = getLocationPoint2D().getX();
//            double yNew = this.getDragStartPt().getY() + yDiff;
//
//            Rectangle oldBounds = new Rectangle( (int)getLocationPoint2D().getX(),
//                                                 (int)getLocationPoint2D().getY(),
//                                                 ( (Image)getRep() ).getWidth( null ),
//                                                 ( (Image)getRep() ).getHeight( null ) );
//            setPosition( (float)xCurr, (float)yNew );
//            Rectangle newBounds = new Rectangle( (int)getLocationPoint2D().getX(),
//                                                 (int)getLocationPoint2D().getY(),
//                                                 ( (Image)getRep() ).getWidth( null ),
//                                                 ( (Image)getRep() ).getHeight( null ) );
//
//            // Determine if we should pump now. We do it if the mouse is moving down
//            minDraggedY = yNew < minDraggedY ? yNew : minDraggedY;
//            if( yNew > minDraggedY && yNew > lastYTracked && event.getPoint().getY() <= getMaxY() ) {
//                int numMolecules = (int)( yNew - lastYPumped ) / 2;
//                pump.pump( numMolecules );
//                lastYPumped = yNew;
//                lastYTracked = yNew;
//            }
//            else {
//                lastYTracked = yNew;
//            }
//
//            if( IdealGasConfig.fastPaint ) {
////            if( Config.fastPaint ) {
//                ( (JPanel)container ).repaint( oldBounds );
//                ( (JPanel)container ).repaint( newBounds );
//            }
//            else {
//                container.repaint();
//            }
//        }
//        super.mouseDragged( event );
//    }

    private static class PumpHandleImage extends PhetGraphic {

        public PumpHandleImage( Component component ) {
            super( component );
        }

        protected Rectangle determineBounds() {
            return null;
        }

        public void paint( Graphics2D g ) {
        }
    }
}
