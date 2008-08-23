/*  */
package edu.colorado.phet.forces1d.view;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.forces1d.phetcommon.math.AbstractVector2D;
import edu.colorado.phet.forces1d.phetcommon.math.Vector2D;
import edu.colorado.phet.forces1d.phetcommon.view.graphics.shapes.Arrow;
import edu.colorado.phet.forces1d.phetcommon.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.forces1d.phetcommon.view.phetgraphics.HTMLGraphic;
import edu.colorado.phet.forces1d.phetcommon.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.forces1d.phetcommon.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.forces1d.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.forces1d.Force1DResources;
import edu.colorado.phet.forces1d.Force1DUtil;
import edu.colorado.phet.forces1d.Forces1DModule;
import edu.colorado.phet.forces1d.model.Force1DModel;

/**
 * User: Sam Reid
 * Date: Dec 16, 2004
 * Time: 5:57:05 PM
 */

public class FreeBodyDiagram extends CompositePhetGraphic {
    private Forces1DModule module;
    private PhetGraphic background;
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

    private Force1DLookAndFeel laf;
    private boolean userClicked = false;


    public FreeBodyDiagram( JPanel component, Forces1DModule module ) {
        super( component );
        this.model = module.getForceModel();
        this.module = module;
        rect = new Rectangle( 200, 150, 400, 400 );
        laf = module.getForce1DLookAndFeel();

        background = new PhetShapeGraphic( component, rect, Color.white, new BasicStroke( 1.0f ), Color.black );
        addGraphic( background );
        axes = new AxesGraphic( component );
        addGraphic( axes );
        axes.setVisible( false );

        mg = new ForceArrow( component, this, laf.getWeightColor(), Force1DResources.get( "FreeBodyDiagram.gravity" ), new Vector2D.Double( 0, 80 ) );
        addForceArrow( mg );

        normal = new ForceArrow( component, this, laf.getNormalColor(), Force1DResources.get( "FreeBodyDiagram.normal" ), new Vector2D.Double( 0, 80 ) );
        addForceArrow( normal );

        appliedForce = new ForceArrow( component, this, laf.getAppliedForceColor(), Force1DResources.get( "FreeBodyDiagram.applied" ), new Vector2D.Double() );
        addForceArrow( appliedForce );

        frictionForce = new ForceArrow( component, this, laf.getFrictionForceColor(), Force1DResources.get( "FreeBodyDiagram.friction" ), new Vector2D.Double() );
        addForceArrow( frictionForce );

        netForce = new ForceArrow( component, this, laf.getNetForceColor(), Force1DResources.get( "FreeBodyDiagram.total" ), new Vector2D.Double() );
        addForceArrow( netForce );
        netForce.setOrigin( 0, -30 );

        wallForce = new ForceArrow( component, this, laf.getWallForceColor(), Force1DResources.get( "FreeBodyDiagram.wall" ), new Vector2D.Double() );
        addForceArrow( wallForce );
        wallForce.setOrigin( 0, -30 );

        MouseInputAdapter mia = new MouseInputAdapter() {
            // implements java.awt.event.MouseListener
            public void mousePressed( MouseEvent e ) {
                setForce( e.getPoint() );
                userClicked = true;
            }

            public void mouseDragged( MouseEvent e ) {
//                System.out.println( "dragged: e = " + e );
                setForce( e.getPoint() );
            }

            // implements java.awt.event.MouseListener
            public void mouseReleased( MouseEvent e ) {
//                System.out.println( "released: e = " + e );
                model.setAppliedForce( 0.0 );
            }
        };
        MouseInputListener listener = new ThresholdedDragAdapter( mia, 10, 0, 1000 );
        addMouseInputListener( listener );
        setCursorHand();
    }

    private void setForce( Point pt ) {
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
        addGraphic( forceArrow );
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

//    public static Color transparify( Color c, int alpha ) {
//        return new Color( c.getRed(), c.getGreen(), c.getBlue(), alpha );
//    }

    public static class ForceArrow extends CompositePhetGraphic {
        private PhetShapeGraphic shapeGraphic;
        private PhetGraphic textGraphic;
        private FreeBodyDiagram fbd;
        private double dx;
        private double dy;
        private String name;
        private Arrow lastArrow;

        public ForceArrow( Component component, FreeBodyDiagram fbd, Color color, String name, Vector2D.Double v ) {
            super( component );
            this.fbd = fbd;
            this.name = name;
            shapeGraphic = new PhetShapeGraphic( component, null, Force1DUtil.transparify( color, 150 ), new BasicStroke( 2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ), Force1DUtil.transparify( Color.black, 128 ) );
            addGraphic( shapeGraphic );
            Font font = new Font( PhetFont.getDefaultFontName(), Font.BOLD, 18 );
//            textGraphic = new PhetShadowTextGraphic( component, name, font, 0, 0, color, 1, 1, Color.black );
            textGraphic = new HTMLGraphic( component, font, name, color );
            addGraphic( textGraphic );
            setVector( v );
            setIgnoreMouse( true );
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
                shapeGraphic.setShape( sh );
            }
            lastArrow = arrow;

            Rectangle b = sh.getBounds();
            Point ctr = RectangleUtils.getCenter( b );
            if ( v.getX() > 0 ) {
                this.textGraphic.setLocation( b.x + b.width + 5, ctr.y - textGraphic.getHeight() / 2 );
                textGraphic.setVisible( true );
            }
            else if ( v.getX() < 0 ) {
                this.textGraphic.setLocation( b.x - textGraphic.getWidth() - 5, ctr.y - textGraphic.getHeight() / 2 );
                textGraphic.setVisible( true );
            }
            else if ( v.getY() > 0 ) {
                this.textGraphic.setLocation( ctr.x - textGraphic.getWidth() / 2, b.y + b.height );
                textGraphic.setVisible( true );
//                System.out.println( name+", y>0" );
            }
            else if ( v.getY() < 0 ) {
                this.textGraphic.setLocation( ctr.x - textGraphic.getWidth() / 2, b.y - textGraphic.getHeight() );
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

    public class AxesGraphic extends CompositePhetGraphic {
        private PhetShapeGraphic xAxis;
        private PhetShapeGraphic yAxis;
        //        private PhetTextGraphic xLabel;
        private PhetGraphic xLabel;
        private PhetGraphic yLabel;

        public AxesGraphic( Component component ) {
            super( component );

            Stroke stroke = new BasicStroke( 1.0f );
            Color color = Color.black;
            xAxis = new PhetShapeGraphic( component, null, stroke, color );
            yAxis = new PhetShapeGraphic( component, null, stroke, color );
            addGraphic( xAxis );
            addGraphic( yAxis );


            Font font = new Font( PhetFont.getDefaultFontName(), Font.PLAIN, 16 );
            xLabel = new HTMLGraphic( component, font, Force1DResources.get( "FreeBodyDiagram.fx" ), Color.black );
            yLabel = new HTMLGraphic( component, font, Force1DResources.get( "FreeBodyDiagram.fy" ), Color.black );
            addGraphic( xLabel );
            addGraphic( yLabel );

            update();
        }

        public void update() {

            Line2D.Double xLine = new Line2D.Double( rect.x, rect.y + rect.height / 2, rect.x + rect.width, rect.y + rect.height / 2 );
            Line2D.Double yLine = new Line2D.Double( rect.x + rect.width / 2, rect.y, rect.x + rect.width / 2, rect.y + rect.height );

            xAxis.setShape( xLine );
            yAxis.setShape( yLine );

            xLabel.setLocation( (int) xLine.getX2() - xLabel.getWidth(), (int) xLine.getY2() );
            yLabel.setLocation( (int) yLine.getX1() + 3, (int) yLine.getY1() );
        }
    }

    public boolean isUserClicked() {
        return userClicked;
    }

    public void resetUserClicked() {
        this.userClicked = false;
    }
}
