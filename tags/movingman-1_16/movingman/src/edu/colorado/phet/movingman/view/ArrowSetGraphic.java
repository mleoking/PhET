/** Sam Reid*/
package edu.colorado.phet.movingman.view;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.graphics.shapes.Arrow;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShadowTextGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.movingman.MovingManModule;
import edu.colorado.phet.movingman.plots.MMPlotSuite;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Nov 27, 2004
 * Time: 11:29:31 AM
 * Copyright (c) Nov 27, 2004 by Sam Reid
 */
public class ArrowSetGraphic extends CompositePhetGraphic {

    private MovingManModule movingManModule;
    private ManGraphic manGraphic;
    private ForceArrowGraphic vel;
    private ForceArrowGraphic accel;

//    public static final double lengthScale = 10;
//    private LinearTransform1d transform1d;
    private double arrowTailWidth = 30;
    private double arrowHeadHeight = 55;
    private double v = 0;
    private double a = 0;

    public ArrowSetGraphic( MovingManModule movingManModule, MovingManApparatusPanel force1DPanel, ManGraphic manGraphic ) {
        super( force1DPanel );
        this.movingManModule = movingManModule;
        this.manGraphic = manGraphic;

        vel = new ForceArrowGraphic( force1DPanel, "Velocity", Color.red, 0, new ForceComponent() {
            public double getValue() {
                return getVelocity();
            }
        } );
        accel = new ForceArrowGraphic( force1DPanel, "Acceleration", Color.green, 0, new ForceComponent() {
            public double getValue() {
                return getAcceleration();
            }
        } );
        addGraphic( accel );
        addGraphic( vel );

        force1DPanel.getPlotSet().getVelocityPlotSuite().addListener( new MMPlotSuite.Listener() {
            public void plotVisibilityChanged() {
            }

            public void valueChanged( double value ) {
                v = value;
            }
        } );
        force1DPanel.getPlotSet().getAccelerationPlotSuite().addListener( new MMPlotSuite.Listener() {
            public void plotVisibilityChanged() {
            }

            public void valueChanged( double value ) {
                a = value;
            }
        } );
    }

    public void paint( Graphics2D g2 ) {
        super.paint( g2 );
    }

    private double getAcceleration() {
        if( Math.abs( a ) < 0.1 ) {
            return 0;
        }
        return a;
    }

    private double getVelocity() {
        if( Math.abs( v ) < 0.1 ) {
            return 0;
        }
        return v;
    }

    public void setShowAccelerationVector( boolean showAccelerationVector ) {
        accel.setVisible( showAccelerationVector );
    }

    public void setShowVelocityVector( boolean showVelocityVector ) {
        vel.setVisible( showVelocityVector );
    }

    static interface ForceComponent {
        double getValue();
    }

    class ForceArrowGraphic extends CompositePhetGraphic {
        private String name;
        private Color color;
        private int dy;
        private ForceComponent forceComponent;
        PhetShadowTextGraphic textGraphic;
        PhetShapeGraphic shapeGraphic;
        final Font font = new Font( "Lucida Sans", Font.BOLD, 13 );
        private Arrow lastArrow;

        public ForceArrowGraphic( Component component, String name, Color color, int dy, ForceComponent forceComponent ) {
            super( component );
            this.name = name;
            color = transparify( color, 128 );
            this.color = color;
            this.dy = dy;
            this.forceComponent = forceComponent;
            textGraphic = new PhetShadowTextGraphic( component, font, name, Color.black, 1, 1, Color.yellow );
            shapeGraphic = new PhetShapeGraphic( component, null, color, new BasicStroke( 1 ), Color.black );
            addGraphic( shapeGraphic );
            addGraphic( textGraphic );
            update();
        }

        private Color transparify( Color color, int alpha ) {
            return new Color( color.getRed(), color.getGreen(), color.getBlue(), alpha );
        }

        public void update() {
            double force = forceComponent.getValue();
//            System.out.println( "force: "+name+" = " + force );
            if( force == 0 ) {
                textGraphic.setVisible( false );
                shapeGraphic.setVisible( false );
                return;
            }
            else {
                shapeGraphic.setVisible( true );
                textGraphic.setVisible( true );
            }
            double viewLength = force * getLengthScale();
            Point viewCtr = manGraphic.getCenter();
            viewCtr.y += manGraphic.getBounds().height / 2;
            viewCtr.y -= dy;
            Point2D.Double tail = new Point2D.Double( viewCtr.x, viewCtr.y );
//            System.out.println( "tail = " + tail );
            Point2D tip = new Vector2D.Double( viewLength, 0 ).getDestination( tail );
            Arrow forceArrow = new Arrow( tail, tip, arrowHeadHeight, arrowHeadHeight, arrowTailWidth, 0.5, false );

            Shape forceArrowShape = forceArrow.getShape();
//            if( this.lastArrow == null || !this.lastArrow.equals( forceArrow ) ) {
            shapeGraphic.setShape( forceArrowShape );

            Shape forceArrowBody = forceArrow.getTailShape();
            double tgHeight = textGraphic.getHeight();
            double arrowHeight = forceArrowBody.getBounds().getHeight();
            double y = forceArrowBody.getBounds().getY() + arrowHeight / 2 - tgHeight / 2;
            textGraphic.setLocation( forceArrowBody.getBounds().x, (int)y );
            this.lastArrow = forceArrow;
            setBoundsDirty();
            autorepaint();
        }


    }

    private double getLengthScale() {
        if( manGraphic.getManTransform().getMinInput() < manGraphic.getManTransform().getMaxInput() ) {
            return 10;
        }
        else {
            return -10;
        }
    }

    private void updateForceArrows() {
        accel.update();
        vel.update();
        checkTextOverlap();
    }

    private void checkTextOverlap() {
        if( accel.textGraphic.isVisible() && vel.textGraphic.isVisible() ) {
            Rectangle f = accel.textGraphic.getBounds();
            Rectangle a = vel.textGraphic.getBounds();
            if( f.intersects( a ) ) {
                Rectangle intersection = f.intersection( a );
                int dx = intersection.width;
                int d = dx / 2 + 5;
                if( f.x < a.x ) {
                    accel.textGraphic.setLocation( f.x - d, accel.textGraphic.getY() );
                    vel.textGraphic.setLocation( a.x + d, vel.textGraphic.getY() );
                }
                else {
                    accel.textGraphic.setLocation( f.x + d, accel.textGraphic.getY() );
                    vel.textGraphic.setLocation( a.x - d, vel.textGraphic.getY() );
                }
            }
        }
    }

    public void updateGraphics() {
        updateForceArrows();
    }
}
