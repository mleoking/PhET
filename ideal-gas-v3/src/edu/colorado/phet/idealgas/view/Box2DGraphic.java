/*
 * User: Ron LeMaster
 * Date: Oct 18, 2002
 * Time: 10:55:17 AM
 */
package edu.colorado.phet.idealgas.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.model.Box2D;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Box2DGraphic extends CompositePhetGraphic {

    //----------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------

    public static double s_thickness = 12;
//    public static double s_thickness = 4;
    private static Stroke s_defaultStroke = new BasicStroke( (float)s_thickness );
//    public static Color s_wallColor = Color.black;
    public static Color s_wallColor = new Color( 180, 180, 180 );
    private static Color s_openingColor;

    //----------------------------------------------------------------
    // Instance data and methods
    //----------------------------------------------------------------

    private Box2D box;
    private boolean graphicSelected;
    private int wallSpeedLimit = 3;
    private boolean leftWallHighlighted;
    private InternalBoxGraphic internalBoxGraphic;
    private Rectangle2D.Double mouseableArea = new Rectangle2D.Double();
    private TranslationListener translationListener;

    /**
     *
     * @param component
     * @param box
     */
    public Box2DGraphic( Component component, final Box2D box ) {
        super( component );

        this.box = box;
        internalBoxGraphic = new InternalBoxGraphic( component );

        this.setCursor( Cursor.getPredefinedCursor( Cursor.E_RESIZE_CURSOR ) );
        translationListener = new TranslationListener() {
                    public void translationOccurred( TranslationEvent translationEvent ) {
                        // Speed limit on wall
                        double dx = translationEvent.getDx();
                        dx = Math.max( -wallSpeedLimit, Math.min( dx, wallSpeedLimit ) );
                        double x = Math.min( Math.max( box.getMinX() + dx, 50 ), box.getMaxX() - box.getMinimumWidth() );
                        box.setBounds( x, box.getMinY(), box.getMaxX(), box.getMaxY() );

                        internalBoxGraphic.update();
                    }
                };
        this.addTranslationListener( translationListener );
    }

    public void setIgnoreMouse( boolean ignoreMouse ) {
        super.setIgnoreMouse( ignoreMouse );
        if( ignoreMouse ) {
            removeAllMouseInputListeners();
        }
    }

    public boolean isGraphicSelected() {
        return graphicSelected;
    }

    public void fireMousePressed( MouseEvent e ) {
        graphicSelected = true;
        super.fireMousePressed( e );
    }

    public void fireMouseReleased( MouseEvent e ) {
        this.graphicSelected = false;
        super.fireMouseReleased( e );
    }

    public void fireMouseEntered( MouseEvent e ) {
        super.fireMouseEntered( e );
        leftWallHighlighted = true;
        setBoundsDirty();
        repaint();
    }

    public void fireMouseExited( MouseEvent e ) {
        super.fireMouseExited( e );
        leftWallHighlighted = false;
        setBoundsDirty();
        repaint();
    }

    protected Rectangle determineBounds() {
        return internalBoxGraphic.getBounds();
    }

    protected PhetGraphic getHandler( Point p ) {
        if( getVisibilityFlag() && mouseableArea.contains( p ) ) {
            return this;
        }
        else {
            return null;
        }
    }

    /**
     * Removes the handle from the side of the box
     */
    public void removeAllMouseInputListeners() {
        super.removeAllMouseInputListeners();
        internalBoxGraphic.removeHandle();
    }

    public void paint( Graphics2D g2 ) {
        internalBoxGraphic.paint( g2 );
    }

    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    private class InternalBoxGraphic extends PhetShapeGraphic implements SimpleObserver {
        private Rectangle2D.Double rect = new Rectangle2D.Double();
        private Rectangle openingRect = new Rectangle();
        private BufferedImage wallHandle;
        private Point wallHandleLocation;
        private boolean isHandleEnabled = true;

        public InternalBoxGraphic( Component component ) {
            super( component, null, s_defaultStroke, s_wallColor );
            box.addObserver( this );
            this.setShape( mouseableArea );
            try {
                wallHandle = ImageLoader.loadBufferedImage( IdealGasConfig.IMAGE_DIRECTORY + "wall-handle.gif" );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
            update();
        }

        /**
         *
         */
        public void update() {
            rect.setRect( box.getMinX(),
                          box.getMinY(),
                          box.getMaxX() - box.getMinX(),
                          box.getMaxY() - box.getMinY() );
            mouseableArea.setRect( box.getMinX() - s_thickness,
                                   box.getMinY() - s_thickness / 2,
                                   s_thickness,
                                   box.getMaxY() - box.getMinY() + s_thickness );
            wallHandleLocation = new Point( (int)( box.getMinX() - wallHandle.getWidth() ),
                                            (int)( box.getMinY() + box.getHeight() + wallHandle.getHeight() ) / 2 );
            mouseableArea.add( new Rectangle( wallHandleLocation.x, wallHandleLocation.y,
                                              wallHandle.getWidth(), wallHandle.getHeight() ) );
            Point2D[] opening = box.getOpening();
            openingRect.setFrameFromDiagonal( opening[0].getX(), opening[0].getY(),
                                              opening[1].getX(), opening[1].getY() - ( s_thickness - 1 ) );
            super.setBoundsDirty();
            super.repaint();
        }

        /**
         *
         * @param g
         */
        public void paint( Graphics2D g ) {
            saveGraphicsState( g );

            // Draw the box before filling it, so the graphical interior of the box corresponds to the
            // model box. If we reversed the order, the stroke of the box would make the interior look
            // smaller than the model box.
            if( isHandleEnabled ) {
            g.drawImage( wallHandle, (int)wallHandleLocation.x, (int)wallHandleLocation.y,
                         wallHandle.getWidth(), wallHandle.getHeight(), null );
            }
            g.setStroke( s_defaultStroke );
            g.setColor( s_wallColor );
            g.draw( rect );

            s_openingColor = Color.black;
            g.setColor( s_openingColor );
            g.fill( openingRect );

            g.setColor( Color.black );
            g.fill( rect );

            if( leftWallHighlighted ) {
                g.setStroke( new BasicStroke( 1 ) );
                g.setColor( Color.red );
                g.draw( mouseableArea.getBounds() );
            }
            restoreGraphicsState();
        }

        public void removeHandle() {
            isHandleEnabled = false;
        }
    }
}
