/* Copyright 2004, Sam Reid */
package edu.colorado.phet.forces1d.view;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.graphics.shapes.Arrow;
import edu.colorado.phet.common.view.phetgraphics.*;
import edu.colorado.phet.common.view.util.RectangleUtils;
import edu.colorado.phet.forces1d.Force1DModule;
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
    double yScale = 1.0 / 30.0;
    double xScale = 3.0 / 1.0;
    private ForceArrow frictionForce;
    private ForceArrow netForce;

    public static Color transparify(Color c, int alpha) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
    }

    public FreeBodyDiagram(Component component, Force1DModule module) {
        super(component);
        this.model = module.getForceModel();
        this.module = module;
        rect = new Rectangle(200, 150, 400, 400);

        background = new PhetShapeGraphic(component, rect, Color.white, new BasicStroke(1.0f), Color.black);
        addGraphic(background);
        axes = new AxesGraphic(component);
        addGraphic(axes);
        int alpha = 128;
        mg = new ForceArrow(component, this, Color.red, "mg", new Vector2D.Double(0, 80));
        addForceArrow(mg);

        normal = new ForceArrow(component, this, Color.green, "N", new Vector2D.Double(0, 80));
        addForceArrow(normal);

        appliedForce = new ForceArrow(component, this, Color.blue, "Fa", new Vector2D.Double());
        addForceArrow(appliedForce);

        frictionForce = new ForceArrow(component, this, Color.orange, "Ff", new Vector2D.Double());
        addForceArrow(frictionForce);

        netForce = new ForceArrow(component, this, Color.gray, "Fnet", new Vector2D.Double());
        addForceArrow(netForce);

        netForce.setOrigin(0, 30);
//        model.addListener( new Force1DModel.Listener() {
//            public void appliedForceChanged() {
//                updateXForces();
//            }
//
//            public void gravityChanged() {
//                updateMG();
//                updateXForces();
//            }
//        } );
//        model.getBlock().addListener( new Block.Listener() {
//            public void positionChanged() {
//                updateXForces();//friction could have reduced the net force to zero
//            }
//
//            public void propertyChanged() {
//                updateMG();
//                updateXForces();
//            }
//        } );
        addMouseInputListener(new MouseInputAdapter() {
            // implements java.awt.event.MouseMotionListener
            public void mouseDragged(MouseEvent e) {
                double x = e.getX();

                //set the applied force
                double dx = x - getCenter().getX();
                double appliedForceRequest = dx / xScale;
                model.setAppliedForce(appliedForceRequest);
            }
        });
    }

    private void updateXForces() {

        Vector2D.Double af = new Vector2D.Double(model.getAppliedForce() * xScale, 0);
        appliedForce.setVector(af);

        Vector2D.Double ff = new Vector2D.Double(model.getFrictionForce() * xScale, 0);
        frictionForce.setVector(ff);

        AbstractVector2D net = af.getAddedInstance(ff);
        netForce.setVector(net);
    }

    private void updateMG() {
        double gravity = model.getGravity();
        double mass = model.getBlock().getMass();

        Vector2D.Double m = new Vector2D.Double(0, gravity * mass * yScale);
        AbstractVector2D n = m.getScaledInstance(-1);
        mg.setVector(m);
        normal.setVector(n);
    }

    public void addForceArrow(ForceArrow forceArrow) {
        addGraphic(forceArrow);
    }

    private Point2D getCenter() {
        return RectangleUtils.getCenter(rect);
    }

    public void updateAll() {
        updateXForces();
        updateMG();
    }

    public static class ForceArrow extends CompositePhetGraphic {
        private PhetShapeGraphic shapeGraphic;
        private PhetShadowTextGraphic textGraphic;
        private FreeBodyDiagram fbd;
        private double dx;
        private double dy;
        private String name;

        public ForceArrow(Component component, FreeBodyDiagram fbd, Color color, String name, Vector2D.Double v) {
            super(component);
            this.fbd = fbd;
            this.name = name;
            shapeGraphic = new PhetShapeGraphic(component, null, transparify(color, 150), new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND), transparify(Color.black, 128));
            addGraphic(shapeGraphic);
            Font font = new Font("Lucida Sans", Font.BOLD, 18);
            textGraphic = new PhetShadowTextGraphic(component, name, font, 0, 0, color, 1, 1, Color.black);
            addGraphic(textGraphic);
            setVector(v);
        }

        public void setOrigin(double x, double y) {
            this.dx = x;
            this.dy = y;
        }

        public void setVector(AbstractVector2D v) {
            Point2D origin = fbd.getCenter();
            origin = new Point2D.Double(origin.getX() + dx, origin.getY() + dy);
            Arrow arrow = new Arrow(origin, v.getDestination(origin), 30, 30, 10, 0.5, true);
            Shape sh = arrow.getShape();
            shapeGraphic.setShape(sh);

            Rectangle b = sh.getBounds();
            Point ctr = RectangleUtils.getCenter(b);
            if (v.getX() > 0) {
                this.textGraphic.setLocation(b.x + b.width + 5, ctr.y - textGraphic.getHeight() / 2);
                textGraphic.setVisible(true);
            } else if (v.getX() < 0) {
                this.textGraphic.setLocation(b.x - textGraphic.getWidth() - 5, ctr.y - textGraphic.getHeight() / 2);
                textGraphic.setVisible(true);
            } else if (v.getY() > 0) {
                this.textGraphic.setLocation(ctr.x - textGraphic.getWidth() / 2, b.y + b.height);
                textGraphic.setVisible(true);
//                System.out.println( name+", y>0" );
            } else if (v.getY() < 0) {
                this.textGraphic.setLocation(ctr.x - textGraphic.getWidth() / 2, b.y - textGraphic.getHeight());
                textGraphic.setVisible(true);
            } else {
                textGraphic.setVisible(false);
            }
            if (v.getMagnitude() <= 0.05) {
                textGraphic.setVisible(false);
            }
        }
    }

    public class AxesGraphic extends CompositePhetGraphic {

        public AxesGraphic(Component component) {
            super(component);
            Line2D.Double xLine = new Line2D.Double(rect.x, rect.y + rect.height / 2, rect.x + rect.width, rect.y + rect.height / 2);
            Line2D.Double yLine = new Line2D.Double(rect.x + rect.width / 2, rect.y, rect.x + rect.width / 2, rect.y + rect.height);
            Stroke stroke = new BasicStroke(2.0f);
            Color color = Color.black;
            PhetShapeGraphic xAxis = new PhetShapeGraphic(component, xLine, stroke, color);
            PhetShapeGraphic yAxis = new PhetShapeGraphic(component, yLine, stroke, color);
            addGraphic(xAxis);
            addGraphic(yAxis);


            Font font = new Font("Lucida Sans", Font.BOLD, 18);
            PhetTextGraphic xLabel = new PhetTextGraphic(component, font, "Fx", Color.black, 0, 0);
            PhetTextGraphic yLabel = new PhetTextGraphic(component, font, "Fy", Color.black, 0, 0);
            addGraphic(xLabel);
            addGraphic(yLabel);

            xLabel.setLocation((int) xLine.getX2() - xLabel.getWidth(), (int) xLine.getY2());
            yLabel.setLocation((int) yLine.getX1() + 3, (int) yLine.getY1());
        }
    }
}
