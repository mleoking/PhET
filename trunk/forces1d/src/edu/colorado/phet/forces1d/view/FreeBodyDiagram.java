/* Copyright 2004, Sam Reid */
package edu.colorado.phet.forces1d.view;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.graphics.shapes.Arrow;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.RectangleUtils;
import edu.colorado.phet.forces1d.Force1DModule;
import edu.colorado.phet.forces1d.Force1DUtil;
import edu.colorado.phet.forces1d.common.HTMLGraphic;
import edu.colorado.phet.forces1d.model.Force1DModel;

import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Dec 16, 2004
 * Time: 5:57:05 PM
 * Copyright (c) Dec 16, 2004 by Sam Reid
 */

public class FreeBodyDiagram extends CompositePhetGraphic {
    private Force1DModule module;
    private PhetGraphic background;
    private AxesGraphic axes;
    private Rectangle rect;
    private Force1DModel model;

    private FreeBodyDiagram.ForceArrow mg;
    private FreeBodyDiagram.ForceArrow normal;
    private FreeBodyDiagram.ForceArrow appliedForce;
    private double yScale = 1.0 / 30.0;
    private double xScale = 0.1;
    private ForceArrow frictionForce;
    private ForceArrow netForce;
    private Force1DLookAndFeel laf;

    public FreeBodyDiagram( Force1DPanel component, Force1DModule module ) {
        super( component );
        this.model = module.getForceModel();
        this.module = module;
        rect = new Rectangle( 200, 150, 400, 400 );
        laf = component.getLookAndFeel();

        background = new PhetShapeGraphic( component, rect, Color.white, new BasicStroke( 1.0f ), Color.black );
        addGraphic( background );
        axes = new AxesGraphic( component );
        addGraphic( axes );
        axes.setVisible( false );

        mg = new ForceArrow( component, this, laf.getWeightColor(), "mg", new Vector2D.Double( 0, 80 ) );
        addForceArrow( mg );

        normal = new ForceArrow( component, this, laf.getNormalColor(), "N", new Vector2D.Double( 0, 80 ) );
        addForceArrow( normal );

//        appliedForce = new ForceArrow( component, this, laf.getAppliedForceColor(), "Fa", new Vector2D.Double() );
        appliedForce = new ForceArrow( component, this, laf.getAppliedForceColor(), "<html>F<sub>a</html>", new Vector2D.Double() );
        addForceArrow( appliedForce );

        frictionForce = new ForceArrow( component, this, laf.getFrictionForceColor(), "<html>F<sub>f</html>", new Vector2D.Double() );
        addForceArrow( frictionForce );

        netForce = new ForceArrow( component, this, laf.getNetForceColor(), "<html>F<sub>net</html>", new Vector2D.Double() );
        addForceArrow( netForce );

        netForce.setOrigin( 0, 30 );

        addMouseInputListener( new MouseInputAdapter() {
            public void mouseDragged( MouseEvent e ) {
                double x = e.getX();

                //set the applied force
                double dx = x - getCenter().getX();
                double appliedForceRequest = dx / xScale;
                model.setAppliedForce( appliedForceRequest );
            }
        } );
        PhetTextGraphic titleGraphic = new PhetTextGraphic( component, new Font( "Lucida Sans", Font.BOLD, 12 ), "Free Body Diagram", Color.blue, 20, 0 );
        addGraphic( titleGraphic );
        setCursorHand();
    }

    private void updateXForces() {

        Vector2D.Double af = new Vector2D.Double( model.getAppliedForce() * xScale, 0 );
        appliedForce.setVector( af );

        Vector2D.Double ff = new Vector2D.Double( model.getFrictionForce() * xScale, 0 );
        frictionForce.setVector( ff );

        AbstractVector2D net = af.getAddedInstance( ff );
        netForce.setVector( net );
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
            Font font = new Font( "Lucida Sans", Font.BOLD, 18 );
//            textGraphic = new PhetShadowTextGraphic( component, name, font, 0, 0, color, 1, 1, Color.black );
            textGraphic = new HTMLGraphic( component, name, font, color );
            addGraphic( textGraphic );
            setVector( v );
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
            if( lastArrow == null || !lastArrow.equals( arrow ) ) {
                shapeGraphic.setShape( sh );
            }
            lastArrow = arrow;

            Rectangle b = sh.getBounds();
            Point ctr = RectangleUtils.getCenter( b );
            if( v.getX() > 0 ) {
                this.textGraphic.setLocation( b.x + b.width + 5, ctr.y - textGraphic.getHeight() / 2 );
                textGraphic.setVisible( true );
            }
            else if( v.getX() < 0 ) {
                this.textGraphic.setLocation( b.x - textGraphic.getWidth() - 5, ctr.y - textGraphic.getHeight() / 2 );
                textGraphic.setVisible( true );
            }
            else if( v.getY() > 0 ) {
                this.textGraphic.setLocation( ctr.x - textGraphic.getWidth() / 2, b.y + b.height );
                textGraphic.setVisible( true );
//                System.out.println( name+", y>0" );
            }
            else if( v.getY() < 0 ) {
                this.textGraphic.setLocation( ctr.x - textGraphic.getWidth() / 2, b.y - textGraphic.getHeight() );
                textGraphic.setVisible( true );
            }
            else {
                textGraphic.setVisible( false );
            }
            if( v.getMagnitude() <= 0.05 ) {
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


            Font font = new Font( "Lucida Sans", Font.PLAIN, 16 );
//            xLabel = new PhetTextGraphic( component, font, "Fx", Color.black, 0, 0 );
//            xLabel = new HTMLGraphic( component, "<html>html<br>line2</html>", font, Color.black );
            xLabel = new HTMLGraphic( component, "<html>F<sub>x</html>", font, Color.black );
//            yLabel = new PhetTextGraphic( component, font, "Fy", Color.black, 0, 0 );
            yLabel = new HTMLGraphic( component, "<html>F<sub>y</html>", font, Color.black );
            addGraphic( xLabel );
            addGraphic( yLabel );

            update();
        }

        public void update() {

            Line2D.Double xLine = new Line2D.Double( rect.x, rect.y + rect.height / 2, rect.x + rect.width, rect.y + rect.height / 2 );
            Line2D.Double yLine = new Line2D.Double( rect.x + rect.width / 2, rect.y, rect.x + rect.width / 2, rect.y + rect.height );

            xAxis.setShape( xLine );
            yAxis.setShape( yLine );

            xLabel.setLocation( (int)xLine.getX2() - xLabel.getWidth(), (int)xLine.getY2() );
            yLabel.setLocation( (int)yLine.getX1() + 3, (int)yLine.getY1() );
        }
    }
}
