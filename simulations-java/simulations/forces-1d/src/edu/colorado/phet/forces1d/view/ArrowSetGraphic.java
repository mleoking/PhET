package edu.colorado.phet.forces1d.view;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.forces1d.phetcommon.math.Vector2D;
import edu.colorado.phet.forces1d.phetcommon.view.graphics.shapes.Arrow;
import edu.colorado.phet.forces1d.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.forces1d.phetcommon.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.forces1d.phetcommon.view.phetgraphics.PhetShadowTextGraphic;
import edu.colorado.phet.forces1d.phetcommon.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.forces1d.Force1DResources;
import edu.colorado.phet.forces1d.Force1DUtil;
import edu.colorado.phet.forces1d.model.Force1DModel;

/**
 * User: Sam Reid
 * Date: Nov 27, 2004
 * Time: 11:29:31 AM
 */
public class ArrowSetGraphic extends CompositePhetGraphic {
    private Force1DPanel force1DPanel;
    private BlockGraphic blockGraphic;
    private Force1DModel model;
    private ModelViewTransform2D transform2D;
    private ForceArrowGraphic applied;
    private ForceArrowGraphic friction;
    private ForceArrowGraphic total;
    private ForceArrowGraphic wall;

    public static final double forceLengthScale = 0.5;
    private double arrowTailWidth = 30;
    private double arrowHeadHeight = 55;
    private Force1DLookAndFeel laf;

    public ArrowSetGraphic( Force1DPanel force1DPanel, BlockGraphic blockGraphic, final Force1DModel model, ModelViewTransform2D transform2D ) {
        super( force1DPanel );
        this.force1DPanel = force1DPanel;
        this.blockGraphic = blockGraphic;
        this.model = model;
        this.transform2D = transform2D;
        this.laf = force1DPanel.getLookAndFeel();

        applied = new ForceArrowGraphic( force1DPanel, Force1DResources.get( "ArrowSetGraphic.appliedForce" ), laf.getAppliedForceColor(), 0, new ForceComponent() {
            public double getForce() {
                return model.getAppliedForce();
            }
        } );
        friction = new ForceArrowGraphic( force1DPanel, Force1DResources.get( "ArrowSetGraphic.frictionForce" ), laf.getFrictionForceColor(), 0, new ForceComponent() {
            public double getForce() {
                return model.getStoredFrictionForceValue();
            }
        } );
        total = new ForceArrowGraphic( force1DPanel, Force1DResources.get( "ArrowSetGraphic.totalForce" ), laf.getNetForceColor(), 60, new ForceComponent() {
            public double getForce() {
                return model.getNetForce();
            }
        } );
        wall = new ForceArrowGraphic( force1DPanel, Force1DResources.get( "ArrowSetGraphic.wallForce" ), laf.getWallForceColor(), 60, new ForceComponent() {
            public double getForce() {
                return model.getWallForce();
            }
        } );
        addGraphic( applied );
        addGraphic( friction );
        addGraphic( total );
        addGraphic( wall );
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
        final Font font = new Font( PhetFont.getDefaultFontName(), Font.BOLD, 13 );
        private Arrow lastArrow;

        public ForceArrowGraphic( Component component, String name, Color color, int dy, ForceComponent forceComponent ) {
            super( component );
            this.name = name;
            color = Force1DUtil.transparify( color, 128 );
            this.color = color;
            this.dy = dy;
            this.forceComponent = forceComponent;
            textGraphic = new PhetShadowTextGraphic( component, font, name, Color.black, 1, 1, Color.yellow );
            shapeGraphic = new PhetShapeGraphic( component, null, color, new BasicStroke( 1 ), Color.black );
            addGraphic( shapeGraphic );
            addGraphic( textGraphic );
            update();
        }

        public void update() {
            double force = forceComponent.getForce();
//            System.out.println( "force: "+name+" = " + force );
            if ( force == 0 ) {
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
            if ( this.lastArrow == null || !this.lastArrow.equals( forceArrow ) ) {
                shapeGraphic.setShape( forceArrowShape );

                Shape forceArrowBody = forceArrow.getTailShape();
                double tgHeight = textGraphic.getHeight();
                double arrowHeight = forceArrowBody.getBounds().getHeight();
                double y = forceArrowBody.getBounds().getY() + arrowHeight / 2 - tgHeight / 2;
//                textGraphic.setLocation( forceArrowBody.getBounds().x, forceArrowBody.getBounds().y + textGraphic.getHeight()/2 );
                textGraphic.setLocation( forceArrowBody.getBounds().x, (int) y );
            }
            this.lastArrow = forceArrow;
        }

    }

    private void updateForceArrows() {
        friction.update();
        applied.update();
        wall.update();
        total.update();

        checkTextOverlap();
    }

    private void checkTextOverlap() {
        if ( friction.textGraphic.isVisible() && applied.textGraphic.isVisible() ) {
            Rectangle f = friction.textGraphic.getBounds();
            Rectangle a = applied.textGraphic.getBounds();
            if ( f.intersects( a ) ) {
                Rectangle intersection = f.intersection( a );
                int dx = intersection.width;
                int d = dx / 2 + 5;
                if ( f.x < a.x ) {
                    friction.textGraphic.setLocation( f.x - d, friction.textGraphic.getY() );
                    applied.textGraphic.setLocation( a.x + d, applied.textGraphic.getY() );
                }
                else {
                    friction.textGraphic.setLocation( f.x + d, friction.textGraphic.getY() );
                    applied.textGraphic.setLocation( a.x - d, applied.textGraphic.getY() );
                }
            }
        }
    }

    public void updateGraphics() {
        updateForceArrows();
    }
}
