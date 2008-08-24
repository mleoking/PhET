/*  */
package edu.colorado.phet.forces1d.view;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.forces1d.Force1DResources;
import edu.colorado.phet.forces1d.Force1DUtil;
import edu.colorado.phet.forces1d.Forces1DModule;
import edu.colorado.phet.forces1d.model.Force1DModel;
import edu.colorado.phet.forces1d.phetcommon.math.AbstractVector2D;
import edu.colorado.phet.forces1d.phetcommon.math.Vector2D;
import edu.colorado.phet.forces1d.phetcommon.view.graphics.shapes.Arrow;
import edu.colorado.phet.forces1d.phetcommon.view.util.RectangleUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * User: Sam Reid
 * Date: Dec 16, 2004
 * Time: 5:57:05 PM
 */

public class FreeBodyDiagramNode extends PNode {
    private AxesGraphic axes;
    private Rectangle rect;
    private Force1DModel model;

    private ForceArrow mg;
    private ForceArrow normal;
    private ForceArrow appliedForce;
    private ForceArrow wallForce;
    private ForceArrow frictionForce;
    private ForceArrow netForce;

    private double yScale = 1.0 / 40.0;
    private double xScale = 0.1;

    private boolean userClicked = false;

    public FreeBodyDiagramNode( Forces1DModule module ) {
        this.model = module.getForceModel();
        rect = new Rectangle( 200, 150, 400, 400 );
        Force1DLookAndFeel laf = module.getForce1DLookAndFeel();

        PhetPPath background = new PhetPPath( rect, Color.white, new BasicStroke( 1 ), Color.black );
        background.addInputEventListener( new CursorHandler() );
        addChild( background );
        axes = new AxesGraphic();
        addChild( axes );
        axes.setVisible( false );

        mg = new ForceArrow( this, laf.getWeightColor(), Force1DResources.get( "FreeBodyDiagram.gravity" ), new Vector2D.Double( 0, 80 ) );
        addForceArrow( mg );

        normal = new ForceArrow( this, laf.getNormalColor(), Force1DResources.get( "FreeBodyDiagram.normal" ), new Vector2D.Double( 0, 80 ) );
        addForceArrow( normal );

        appliedForce = new ForceArrow( this, laf.getAppliedForceColor(), Force1DResources.get( "FreeBodyDiagram.applied" ), new Vector2D.Double() );
        addForceArrow( appliedForce );

        frictionForce = new ForceArrow( this, laf.getFrictionForceColor(), Force1DResources.get( "FreeBodyDiagram.friction" ), new Vector2D.Double() );
        addForceArrow( frictionForce );

        netForce = new ForceArrow( this, laf.getNetForceColor(), Force1DResources.get( "FreeBodyDiagram.total" ), new Vector2D.Double() );
        addForceArrow( netForce );
        netForce.setOrigin( 0, -30 );

        wallForce = new ForceArrow( this, laf.getWallForceColor(), Force1DResources.get( "FreeBodyDiagram.wall" ), new Vector2D.Double() );
        addForceArrow( wallForce );
        wallForce.setOrigin( 0, -30 );

        addInputEventListener( new CursorHandler() );
    }

    public void setForce( Point pt ) {
        double x = pt.getX();

        //set the applied force
        double dx = x - getCenter().getX();
        double appliedForceRequest = dx / xScale;
        model.setAppliedForce( appliedForceRequest );
    }

    private void updateXForces() {

        Vector2D.Double af = new Vector2D.Double( model.getAppliedForce() * xScale, 0 );
        appliedForce.setVector( af );

        Vector2D.Double ff = new Vector2D.Double( model.getFrictionForce() * xScale, 0 );
        frictionForce.setVector( ff );

        AbstractVector2D net = new Vector2D.Double( model.getNetForce() * xScale, 0 );
        netForce.setVector( net );

        Vector2D.Double wf = new Vector2D.Double( model.getWallForce() * xScale, 0 );
        wallForce.setVector( wf );
    }

    private void updateMG() {
        double gravity = model.getGravity();
        double mass = model.getBlock().getMass();

        Vector2D.Double m = new Vector2D.Double( 0, gravity * mass * yScale );
        AbstractVector2D n = m.getScaledInstance( -1 );
        mg.setVector( m );
        normal.setVector( n );
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

    public void setUserClicked( boolean b ) {
        this.userClicked = b;
    }

    public static class ForceArrow extends PNode {
        private PhetPPath shapeGraphic;
        private HTMLNode textGraphic;
        private FreeBodyDiagramNode fbd;
        private double dx;
        private double dy;
        private Arrow lastArrow;

        public ForceArrow( FreeBodyDiagramNode fbd, Color color, String name, Vector2D.Double v ) {
            this.fbd = fbd;
            shapeGraphic = new PhetPPath( Force1DUtil.transparify( color, 150 ), new BasicStroke( 2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ), Force1DUtil.transparify( Color.black, 128 ) );
            addChild( shapeGraphic );
            Font font = new Font( PhetFont.getDefaultFontName(), Font.BOLD, 18 );
            textGraphic = new HTMLNode( name, font, color );
            addChild( textGraphic );
            setVector( v );
            setPickable( false );
            setChildrenPickable( false );
        }

        public void setOrigin( double x, double y ) {
            this.dx = x;
            this.dy = y;
        }

        public void setVector( AbstractVector2D v ) {
            Point2D origin = fbd.getCenter();
            origin = new Point2D.Double( origin.getX() + dx, origin.getY() + dy );
            Arrow arrow = new Arrow( origin, v.getDestination( origin ), 30, 30, 10, 0.5, true );
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
        private PNode xLabel;
        private PNode yLabel;

        public AxesGraphic() {
            super();

            Stroke stroke = new BasicStroke( 1.0f );
            Color color = Color.black;
            xAxis = new PhetPPath( stroke, color );
            yAxis = new PhetPPath( stroke, color );
            addChild( xAxis );
            addChild( yAxis );


            Font font = new Font( PhetFont.getDefaultFontName(), Font.PLAIN, 16 );
            xLabel = new HTMLNode( Force1DResources.get( "FreeBodyDiagram.fx" ), font, Color.black );
            yLabel = new HTMLNode( Force1DResources.get( "FreeBodyDiagram.fy" ), font, Color.black );
            addChild( xLabel );
            addChild( yLabel );

            update();
        }

        public void update() {

            Line2D.Double xLine = new Line2D.Double( rect.x, rect.y + rect.height / 2, rect.x + rect.width, rect.y + rect.height / 2 );
            Line2D.Double yLine = new Line2D.Double( rect.x + rect.width / 2, rect.y, rect.x + rect.width / 2, rect.y + rect.height );

            xAxis.setPathTo( xLine );
            yAxis.setPathTo( yLine );

            xLabel.setOffset( (int) xLine.getX2() - xLabel.getWidth(), (int) xLine.getY2() );
            yLabel.setOffset( (int) yLine.getX1() + 3, (int) yLine.getY1() );
        }
    }

    public boolean isUserClicked() {
        return userClicked;
    }

    public void resetUserClicked() {
        this.userClicked = false;
    }
}
