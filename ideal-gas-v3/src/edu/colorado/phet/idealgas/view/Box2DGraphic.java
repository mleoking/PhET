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
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

public class Box2DGraphic extends DefaultInteractiveGraphic {

    //
    // Static fields and methods
    //
    public static double s_thickness = 4;
    private static Stroke s_defaultStroke = new BasicStroke( (float)s_thickness );
    private static Color s_defaultColor = Color.black;
    private static float s_leaningManStateChangeScaleFactor = 1.75F;

    private int stroke;
    private Area a1;
    private Area a2;
    private Box2D box;

    public Box2DGraphic( Component component, final Box2D box ) {
        super( null );

        this.box = box;
        InternalBoxGraphic internalBoxGraphic = new InternalBoxGraphic( component );
        setBoundedGraphic( internalBoxGraphic );

        this.addCursorHandBehavior();
        this.addTranslationBehavior( new Translatable() {
            public void translate( double dx, double dy ) {
                double x = Math.min( Math.max( box.getMinX() + dx, 50 ), box.getMaxX() - 50 );
                box.setBounds( x, box.getMinY(), box.getMaxX(), box.getMaxY() );
                // Compute the velocity of the wall
                //        if( !initWallMovement ) {
                //            initWallMovement = true;
                //
                //            lastMinX = (float)event.getPoint().getX();
                //            lastEventTime = event.getWhen();
                //            clockScaleFactor = PhysicalSystem.instance().getDt() / PhysicalSystem.instance().getWaitTime();
                //        }
                //        float dx = (float)event.getPoint().getX() - lastMinX;
                //        lastMinX = (float)event.getPoint().getX();
                //        long now = event.getWhen();
                //        long dt = now - lastEventTime;
                //        lastEventTime = now;
                //        if( dt > 0 ) {
                //            float vx = dx / ( dt * clockScaleFactor );
                //            Thread.yield();
                //            box.setLeftWallVelocity( vx * 2 );
                ////             We must yield so the PhysicalSystem thread can get the
                ////             update.
                //            Thread.yield();
            }
        } );
    }

    private class InternalBoxGraphic extends PhetShapeGraphic implements SimpleObserver {
        private Rectangle2D.Double rect = new Rectangle2D.Double();

        public InternalBoxGraphic( Component component ) {
            super( component, null, s_defaultStroke, s_defaultColor );
            box.addObserver( this );
            this.setShape( rect );
            update();
        }

        public void update() {
            rect.setRect( box.getMinX() - s_thickness / 2,
                          box.getMinY() - s_thickness / 2,
                          box.getMaxX() - box.getMinX(),
                          box.getMaxY() - box.getMinY() );
            super.setBoundsDirty();
            super.repaint();
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
