// Copyright 2002-2012, University of Colorado

/*  */
package edu.colorado.phet.forces1d.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.vector.AbstractVector2D;
import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.forces1d.Force1DResources;
import edu.colorado.phet.forces1d.Force1DUtil;
import edu.colorado.phet.forces1d.Forces1DModule;
import edu.colorado.phet.forces1d.model.Force1DModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * User: Sam Reid
 * Date: Dec 16, 2004
 * Time: 5:57:05 PM
 */

public class FreeBodyDiagramNode extends PNode {
    private AxesGraphic axes;
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

    private double width = 400;
    private double height = 400;
    private PhetPPath background;

    public FreeBodyDiagramNode( Forces1DModule module ) {
        this.model = module.getForceModel();
        Force1DLookAndFeel laf = module.getForce1DLookAndFeel();

        background = new PhetPPath( new Rectangle2D.Double( 0, 0, width, height ), Color.white, new BasicStroke( 1 ), Color.black );
        background.addInputEventListener( new CursorHandler() );
        addChild( background );
        axes = new AxesGraphic();
        addChild( axes );
        axes.setVisible( false );

        mg = new ForceArrow( this, laf.getWeightColor(), Force1DResources.get( "FreeBodyDiagram.gravity" ), new MutableVector2D( 0, 80 ) );
        addForceArrow( mg );

        normal = new ForceArrow( this, laf.getNormalColor(), Force1DResources.get( "FreeBodyDiagram.normal" ), new MutableVector2D( 0, 80 ) );
        addForceArrow( normal );

        appliedForce = new ForceArrow( this, laf.getAppliedForceColor(), Force1DResources.get( "FreeBodyDiagram.applied" ), new MutableVector2D() );
        addForceArrow( appliedForce );

        frictionForce = new ForceArrow( this, laf.getFrictionForceColor(), Force1DResources.get( "FreeBodyDiagram.friction" ), new MutableVector2D() );
        addForceArrow( frictionForce );

        netForce = new ForceArrow( this, laf.getNetForceColor(), Force1DResources.get( "FreeBodyDiagram.total" ), new MutableVector2D() );
//        addForceArrow( netForce );//net force not shown in FBD
        netForce.setOrigin( 0, -30 );

        wallForce = new ForceArrow( this, laf.getWallForceColor(), Force1DResources.get( "FreeBodyDiagram.wall" ), new MutableVector2D() );
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

        MutableVector2D af = new MutableVector2D( model.getAppliedForce() * xScale, 0 );
        appliedForce.setVector( af );

        MutableVector2D ff = new MutableVector2D( model.getFrictionForce() * xScale, 0 );
        frictionForce.setVector( ff );

        MutableVector2D net = new MutableVector2D( model.getNetForce() * xScale, 0 );
        netForce.setVector( net );

        MutableVector2D wf = new MutableVector2D( model.getWallForce() * xScale, 0 );
        wallForce.setVector( wf );
    }

    private void updateMG() {
        double gravity = model.getGravity();
        double mass = model.getBlock().getMass();

        MutableVector2D m = new MutableVector2D( 0, gravity * mass * yScale );
        Vector2D n = m.times( -1 );
        mg.setVector( m );
        normal.setVector( n );
    }

    public void addForceArrow( ForceArrow forceArrow ) {
        addChild( forceArrow );
    }

    private Point2D getCenter() {
        return new Point2D.Double( width / 2, height / 2 );
    }

    public void updateAll() {
        updateXForces();
        updateMG();
        axes.update();
        background.setPathTo( new Rectangle2D.Double( 0, 0, width, height ) );
    }

    public void setSize( int width, int height ) {
        this.width = width;
        this.height = height;
        updateAll();
    }

    public void setUserClicked( boolean b ) {
        this.userClicked = b;
    }

    public void setAppliedForce( double v ) {
        model.setAppliedForce( v );
    }

    public static class ForceArrow extends PNode {
        private PhetPPath shapeGraphic;
        private HTMLNode textGraphic;
        private FreeBodyDiagramNode fbd;
        private double dx;
        private double dy;
        private Arrow lastArrow;

        public ForceArrow( FreeBodyDiagramNode fbd, Color color, String name, MutableVector2D v ) {
            this.fbd = fbd;
            shapeGraphic = new PhetPPath( Force1DUtil.transparify( color, 150 ), new BasicStroke( 2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ), Force1DUtil.transparify( Color.black, 128 ) );
            addChild( shapeGraphic );
            Font font = new Font( PhetFont.getDefaultFontName(), Font.BOLD, 18 );
            textGraphic = new HTMLNode( name, color, font );
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
            if ( v.magnitude() <= 0.05 ) {
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
            xLabel = new HTMLNode( Force1DResources.get( "FreeBodyDiagram.fx" ), Color.black, font );
            yLabel = new HTMLNode( Force1DResources.get( "FreeBodyDiagram.fy" ), Color.black, font );
            addChild( xLabel );
            addChild( yLabel );

            update();
        }

        public void update() {

            Line2D.Double xLine = new Line2D.Double( 0, height / 2, width, height / 2 );
            Line2D.Double yLine = new Line2D.Double( width / 2, 0, width / 2, height );

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
