/*  */
package edu.colorado.phet.movingman.motion;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * User: Sam Reid
 * Date: Dec 16, 2004
 * Time: 5:57:05 PM
 */

public class FreeBodyDiagramNode extends PNode {
    private AxesGraphic axes;
    private Rectangle rect;

    private ForceArrow mg;
    private ForceArrow normal;
    private ForceArrow appliedForce;
    private ForceArrow wallForce;
    private ForceArrow frictionForce;
    private ForceArrow netForce;

    private double scale = 1.0 / 20.0;
    private boolean userClicked = false;
    private IFBDObject f;

    public static interface IFBDObject {
        void startRecording();

        void setAppliedForce( double v );

        void addModelElement( ModelElement modelElement );

        double getViewAngle();

        Vector2D.Double getAppliedForce();

        Vector2D.Double getFrictionForce();

        Vector2D.Double getTotalForce();

        Vector2D.Double getWallForce();

        Vector2D.Double getGravityForce();

        Vector2D.Double getNormalForce();
    }

    public FreeBodyDiagramNode( final IFBDObject f ) {
        this.f = f;
        rect = new Rectangle( 0, 0, 200, 200 );
        RampLookAndFeel laf = new RampLookAndFeel();

        PPath background = new PPath( rect );
        background.setPaint( Color.white );
        background.setStroke( new BasicStroke( 1.0f ) );
        background.setStrokePaint( Color.black );
        addChild( background );
        axes = new AxesGraphic();
        addChild( axes );

        mg = new ForceArrow( this, laf.getWeightColor(), ( TheRampStrings.getString( "force.subscript.gravity" ) ), new Vector2D.Double( 0, 80 ) );
        addForceArrow( mg );

        normal = new ForceArrow( this, laf.getNormalColor(), ( TheRampStrings.getString( "force.subscript.normal" ) ), new Vector2D.Double( 0, 80 ) );
        addForceArrow( normal );

        appliedForce = new ForceArrow( this, laf.getAppliedForceColor(), ( TheRampStrings.getString( "force.subscript.applied" ) ), new Vector2D.Double() );
        addForceArrow( appliedForce );

        frictionForce = new ForceArrow( this, laf.getFrictionForceColor(), ( TheRampStrings.getString( "force.subscript.friction" ) ), new Vector2D.Double() );
        addForceArrow( frictionForce );

        netForce = new ForceArrow( this, laf.getNetForceColor(), ( TheRampStrings.getString( "force.subscript.net" ) ), new Vector2D.Double() );
        addForceArrow( netForce );
        netForce.setVerticalOffset( -30 );

        wallForce = new ForceArrow( this, laf.getWallForceColor(), ( TheRampStrings.getString( "force.subscript.wall" ) ), new Vector2D.Double() );
        addForceArrow( wallForce );
        wallForce.setVerticalOffset( -30 );

        PBasicInputEventHandler mia = new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent e ) {
                f.startRecording();
                applyForce( e.getPositionRelativeTo( FreeBodyDiagramNode.this ) );
                userClicked = true;
            }

            public void mouseDragged( PInputEvent e ) {
                applyForce( e.getPositionRelativeTo( FreeBodyDiagramNode.this ) );
            }

            public void mouseReleased( PInputEvent e ) {
                f.setAppliedForce( 0.0 );
            }
        };
        ThresholdedPDragAdapter listener = new ThresholdedPDragAdapter( mia, 10, 0, 1000 );
        addInputEventListener( listener );
        addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );
        updateAll();
        f.addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                updateAll();
            }
        } );

        PText title = new PText( TheRampStrings.getString( "display.free-body-diagram" ) );
        addChild( title );
    }

    private void applyForce( Point2D pt ) {
        double x = pt.getX();

        //set the applied force
        double dx = x - getCenter().getX();
        double appliedForceRequest = dx / scale;
        f.setAppliedForce( appliedForceRequest );
    }

    private void updateXForces() {
        appliedForce.setVector( new Vector2D.Double( f.getAppliedForce().getScaledInstance( scale ) ) );
        frictionForce.setVector( new Vector2D.Double( f.getFrictionForce().getScaledInstance( scale ) ) );
        netForce.setVector( new Vector2D.Double( f.getTotalForce().getScaledInstance( scale ) ) );
        wallForce.setVector( new Vector2D.Double( f.getWallForce().getScaledInstance( scale ) ) );
    }

    private void updateMG() {
        mg.setVector( f.getGravityForce().getScaledInstance( scale ) );
        normal.setVector( f.getNormalForce().getScaledInstance( scale ) );
    }

    public void addForceArrow( ForceArrow forceArrow ) {
        addChild( forceArrow );
    }

    private Point2D getCenter() {
        return RectangleUtils.getCenter( rect );
    }

    public void updateAll() {
        updateXForces();
        updateMG();
        axes.update();
    }

    public void setBounds( int x, int y, int width, int height ) {
        rect.setBounds( x, y, width, height );
        updateAll();
    }

    public static class ForceArrow extends PNode {
        private PPath shapeGraphic;
        private HTMLNode textGraphic;
        private FreeBodyDiagramNode fbd;
        private Arrow lastArrow;
        private double verticalOffset = 0;

        public ForceArrow( FreeBodyDiagramNode freeBodyDiagramNode, Color color, String name, Vector2D.Double vector ) {
            this.fbd = freeBodyDiagramNode;
            shapeGraphic = new PPath();
            shapeGraphic.setStroke( new BasicStroke( 1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) );
            shapeGraphic.setPaint( color );
            shapeGraphic.setStrokePaint( Color.black );
            addChild( shapeGraphic );
            Font font = new PhetFont( Font.BOLD, 16 );
            textGraphic = new HTMLNode( name, font, color );
            addChild( textGraphic );
            setVector( vector );
            setPickable( false );
            setChildrenPickable( false );
        }

        public void setVerticalOffset( double verticalOffset ) {
            this.verticalOffset = verticalOffset;
        }

        public void setVector( AbstractVector2D v ) {
            double viewAngle = fbd.f.getViewAngle();

            Point2D origin = Vector2D.Double.parseAngleAndMagnitude( verticalOffset, viewAngle ).getNormalVector().
                    getNormalVector().getNormalVector().getDestination( fbd.getCenter() );
            Arrow arrow = new Arrow( origin, v.getDestination( origin ), 20, 20, 8, 0.5, true );
            Shape sh = arrow.getShape();
            if ( lastArrow == null || !lastArrow.equals( arrow ) ) {
                shapeGraphic.setPathTo( sh );
            }
            lastArrow = arrow;

            Rectangle b = sh.getBounds();
            Point ctr = RectangleUtils.getCenter( b );
            if ( v.getX() > 0 ) {
                this.textGraphic.setOffset( b.x + b.width + 5, ctr.y - textGraphic.getHeight() / 2 );
                textGraphic.setVisible( true );
            }
            else if ( v.getX() < 0 ) {
                this.textGraphic.setOffset( b.x - textGraphic.getWidth() - 5, ctr.y - textGraphic.getHeight() / 2 );
                textGraphic.setVisible( true );
            }
            else if ( v.getY() > 0 ) {
                this.textGraphic.setOffset( ctr.x - textGraphic.getWidth() / 2, b.y + b.height );
                textGraphic.setVisible( true );
//                System.out.println( name+", y>0" );
            }
            else if ( v.getY() < 0 ) {
                this.textGraphic.setOffset( ctr.x - textGraphic.getWidth() / 2, b.y - textGraphic.getHeight() );
                textGraphic.setVisible( true );
            }
            else {
                textGraphic.setVisible( false );
            }
            if ( v.getMagnitude() <= 0.05 ) {
                setVisible( false );
            }
            else {
                setVisible( true );
            }
        }
    }

    public class AxesGraphic extends PNode {
        private PPath xAxis;
        private PPath yAxis;
        private Line2D.Double yLine = null;
        private Line2D.Double xLine = null;

        public AxesGraphic() {
            Stroke stroke = new BasicStroke( 1.0f );
            Color color = Color.black;
            xAxis = new PPath();
            xAxis.setStroke( stroke );
            xAxis.setStrokePaint( color );

            yAxis = new PPath();
            yAxis.setStroke( stroke );
            yAxis.setStrokePaint( color );

            addChild( xAxis );
            addChild( yAxis );

            update();
        }

        public void update() {
            Line2D.Double xLine = new Line2D.Double( rect.x, rect.y + rect.height / 2, rect.x + rect.width, rect.y + rect.height / 2 );
            Line2D.Double yLine = new Line2D.Double( rect.x + rect.width / 2, rect.y, rect.x + rect.width / 2, rect.y + rect.height );
            if ( this.xLine == null || !xLine.equals( this.xLine ) ) {
                this.xLine = xLine;
                xAxis.setPathTo( xLine );
            }
            if ( this.yLine == null || !yLine.equals( this.yLine ) ) {
                this.yLine = yLine;
                yAxis.setPathTo( yLine );
            }
        }
    }

    public boolean isUserClicked() {
        return userClicked;
    }

    public void resetUserClicked() {
        this.userClicked = false;
    }
}
