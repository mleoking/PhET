/** Sam Reid*/
package edu.colorado.phet.forces1d.view;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.graphics.shapes.Arrow;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShadowTextGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.forces1d.model.Force1DModel;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Nov 27, 2004
 * Time: 11:29:31 AM
 * Copyright (c) Nov 27, 2004 by Sam Reid
 */
public class ArrowSetGraphic extends CompositePhetGraphic {
    private Force1DPanel force1DPanel;
    private BlockGraphic blockGraphic;
    private Force1DModel model;
    private ModelViewTransform2D transform2D;
    private ForceArrowGraphic applied;
    private ForceArrowGraphic friction;
    private ForceArrowGraphic total;

    public static final double forceLengthScale = 12;
    private double arrowTailWidth = 30;
    private double arrowHeadHeight = 55;

    public ArrowSetGraphic( Force1DPanel force1DPanel, BlockGraphic blockGraphic, final Force1DModel model, ModelViewTransform2D transform2D ) {
        super( force1DPanel );
        this.force1DPanel = force1DPanel;
        this.blockGraphic = blockGraphic;
        this.model = model;
        this.transform2D = transform2D;

        Color appliedForceColor = new Color( 255, 0, 0, 128 );
        Color frictionForceColor = new Color( 0, 255, 0, 128 );
        applied = new ForceArrowGraphic( force1DPanel, "Applied Force", appliedForceColor, 0, new ForceComponent() {
            public double getForce() {
                return model.getAppliedForce();
            }
        } );
        friction = new ForceArrowGraphic( force1DPanel, "Friction Force", frictionForceColor, 0, new ForceComponent() {
            public double getForce() {
                return model.getStoredFrictionForceValue();
            }
        } );
        Color totalForceColor = new Color( 0, 0, 155, 128 );
        total = new ForceArrowGraphic( force1DPanel, "Total Force", totalForceColor, 60, new ForceComponent() {
            public double getForce() {
                return model.getTotalForce();
            }
        } );
        addGraphic( applied );
        addGraphic( friction );
        addGraphic( total );
    }

    static interface ForceComponent {
        double getForce();
    }

    class ForceArrowGraphic extends CompositePhetGraphic {
        private String name;
        private Color color;
        private int dy;
        private ForceComponent forceComponent;
        PhetShadowTextGraphic textGraphic;
        PhetShapeGraphic shapeGraphic;
        final Font font = new Font( "Lucida Sans", Font.BOLD, 13 );

        public ForceArrowGraphic( Component component, String name, Color color, int dy, ForceComponent forceComponent ) {
            super( component );
            this.name = name;
            this.color = color;
            this.dy = dy;
            this.forceComponent = forceComponent;
            textGraphic = new PhetShadowTextGraphic( component, name, font, 0, 0, Color.black, 1, 1, Color.yellow );
            shapeGraphic = new PhetShapeGraphic( component, null, color, new BasicStroke( 1 ), Color.black );

            addGraphic( shapeGraphic );
            addGraphic( textGraphic );
        }

        public void update() {
            double force = forceComponent.getForce();
            if( force == 0 ) {
                setVisible( false );
                return;
            }
            else {
                setVisible( true );
            }
            double viewLength = force * forceLengthScale;
            Point viewCtr = blockGraphic.getCenter();
            viewCtr.y += blockGraphic.computeDimension().height / 2;
            viewCtr.y -= dy;
            Point2D.Double tail = new Point2D.Double( viewCtr.x, viewCtr.y );
            Point2D tip = new Vector2D.Double( viewLength, 0 ).getDestination( tail );
            Arrow forceArrow = new Arrow( tail, tip, arrowHeadHeight, arrowHeadHeight, arrowTailWidth, 0.5, false );
            Shape forceArrowShape = forceArrow.getShape();
            shapeGraphic.setShape( forceArrowShape );
            Shape forceArrowBody = forceArrow.getTailShape();
            textGraphic.setLocation( forceArrowBody.getBounds().x, forceArrowBody.getBounds().y + textGraphic.getHeight() );
        }
    }

    private void updateForceArrows() {
        friction.update();
        applied.update();
        total.update();
        checkTextOverlap();
    }

    private void checkTextOverlap() {
        Rectangle f = friction.textGraphic.getBounds();
        Rectangle a = applied.textGraphic.getBounds();
        if( f.intersects( a ) ) {
            Rectangle intersection = f.intersection( a );
            int dx = intersection.width;
            int d = dx / 2 + 5;
            if( f.x < a.x ) {
                friction.textGraphic.setLocation( f.x - d, friction.textGraphic.getY() );
                applied.textGraphic.setLocation( a.x + d, applied.textGraphic.getY() );
            }
            else {
                friction.textGraphic.setLocation( f.x + d, friction.textGraphic.getY() );
                applied.textGraphic.setLocation( a.x - d, applied.textGraphic.getY() );
            }
        }
    }

    public void updateGraphics() {
        updateForceArrows();
    }
}
