/*
 * User: Ron LeMaster
 * Date: Oct 18, 2002
 * Time: 10:55:17 AM
 */
package edu.colorado.phet.idealgas.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.mousecontrols.Translatable;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.idealgas.model.Box2D;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Box2DGraphic extends DefaultInteractiveGraphic {

    public static double s_thickness = 4;
    private static Stroke s_defaultStroke = new BasicStroke( (float)s_thickness );
    private static Color s_defaultColor = Color.black;
    private static float s_leaningManStateChangeScaleFactor = 1.75F;
    private Box2D box;
    private boolean initWallMovement;

    public Box2DGraphic( Component component, final Box2D box ) {
        super( null );

        this.box = box;
        InternalBoxGraphic internalBoxGraphic = new InternalBoxGraphic( component );
        setBoundedGraphic( internalBoxGraphic );

        this.addCursorHandBehavior();
        this.addTranslationBehavior( new Translatable() {
            public void translate( double dx, double dy ) {
                double x = Math.min( Math.max( box.getMinX() + dx, 50 ), box.getMaxX() - box.getMinimumWidth() );
                box.setBounds( x, box.getMinY(), box.getMaxX(), box.getMaxY() );
            }
        } );
    }

    public void mouseEntered( MouseEvent e ) {
        super.mouseEntered( e );
    }

    private class InternalBoxGraphic extends PhetShapeGraphic implements SimpleObserver {
        private Rectangle2D.Double rect = new Rectangle2D.Double();
        private Rectangle2D.Double mouseableArea = new Rectangle2D.Double();
        private Rectangle openingRect = new Rectangle();

        public InternalBoxGraphic( Component component ) {
            super( component, null, s_defaultStroke, s_defaultColor );
            box.addObserver( this );
            this.setShape( mouseableArea );
            update();
        }

        public void update() {
            rect.setRect( box.getMinX() - s_thickness / 2,
                          box.getMinY() - s_thickness / 2,
                          box.getMaxX() - box.getMinX() + s_thickness,
                          box.getMaxY() - box.getMinY() + s_thickness );
            mouseableArea.setRect( box.getMinX() - s_thickness,
                                   box.getMinY() - s_thickness,
                                   s_thickness,
                                   box.getMaxY() - box.getMinY() + s_thickness );
            Point2D[] opening = box.getOpening();
            openingRect.setFrameFromDiagonal( opening[0].getX(), opening[0].getY(),
                                              opening[1].getX(), opening[1].getY() - ( s_thickness - 1 ) );
            super.setBoundsDirty();
            super.repaint();
        }

        public void paint( Graphics2D g ) {
            saveGraphicsState( g );
            g.setStroke( s_defaultStroke );
            g.setColor( s_defaultColor );
            g.draw( rect );
            g.setColor( Color.white );
            g.fill( openingRect );
            restoreGraphicsState();
        }
    }

    //
    //    private double lastPressure = 0;
    //
    //    /**
    //     * Notes state change in the box the receiver is observing. Changes
    //     * the position of the leaning man if the pressure in the box has
    //     * changed sufficiently
    //     *
    //     * @param o
    //     * @param arg
    //     */
    //        public void update( Observable o, Object arg ) {
    //            this.setPosition( (CollidableBody)o );
    //            if( o instanceof PressureSensingBox ) {
    //                PressureSensingBox box = (PressureSensingBox)o;
    //                double newPressure = box.getPressure();
    //                if( newPressure > lastPressure * s_leaningManStateChangeScaleFactor ) {
    //                    getIdealGasApparatusPanel().moveLeaner( 1 );
    //                    lastPressure = box.getPressure();
    //                }
    //                else if( newPressure < lastPressure / s_leaningManStateChangeScaleFactor ) {
    //                    getIdealGasApparatusPanel().moveLeaner( -1 );
    //                    lastPressure = box.getPressure();
    //                }
    //            }
    //        }

}
