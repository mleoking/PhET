/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.elements.junction;

import edu.colorado.phet.cck.CCK2Module;
import edu.colorado.phet.cck.elements.branch.DefaultBranchInteractionHandler;
import edu.colorado.phet.cck.selection.SelectionListener;
import edu.colorado.phet.common.view.graphics.InteractiveGraphic;
import edu.colorado.phet.common.view.graphics.bounds.Boundary;
import edu.colorado.phet.common.view.graphics.transforms.TransformListener;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.math.PhetVector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

/**
 * Looks hollow for disconnected, different for connected.
 *  */
public class JunctionGraphic implements InteractiveGraphic, TransformListener,Boundary {

    private Junction junction;
    CCK2Module module;
    private int radius;
    private Color color;
    private Point viewPoint;
    private Stroke stroke;
    private int numConnections;
    private JPopupMenu menu;
    private Color filledColor;
    private boolean showConnectionCount = false;
//    private boolean showConnectionCount = true;
    private boolean selected;
    private int dr = 4;
    private Color highlight = Color.yellow;
    private boolean showID = false;

    public JunctionGraphic(final Junction junction, CCK2Module module, int radius, Color color, Stroke stroke, Color filledColor) {
        this.junction = junction;
        this.module = module;
        this.radius = radius;
        this.color = color;
        this.stroke = stroke;
        this.filledColor = filledColor;

        module.getTransform().addTransformListener(this);
        junction.addObserver(new JunctionObserver() {
            public void locationChanged(Junction junction2) {
                update();
            }

            public void connectivityChanged() {
                update();
            }
        });

        menu = new JPopupMenu();
        JMenuItem splitJunction = new JMenuItem("Split Junction");
        splitJunction.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                junction.getBranch().getCircuit().splitJunction(junction);
            }
        });
        menu.add(splitJunction);
        junction.addSelectionListener(new SelectionListener() {
            public void selectionChanged(boolean sel) {
                selected = sel;
            }
        });
        update();
    }

    public void setRadius(int radius) {
        this.radius = radius;
        update();
    }

    public boolean canHandleMousePress(MouseEvent event) {
        if (viewPoint == null)
            return false;
        double dist = event.getPoint().distance(viewPoint);
        return dist <= radius;
    }

    public void mousePressed(MouseEvent event) {
        module.deselectAll();
        junction.setSelected(true);
        event.getComponent().repaint();
    }

    public void mouseDragged(MouseEvent event) {
        Point2D.Double modelMousePoint = module.getTransform().viewToModel(event.getX(), event.getY());
        //see if any other points in range.
        Junction closest = junction.getBranch().getCircuit().getClosestJunction(junction, modelMousePoint.x, modelMousePoint.y);
        if (closest != null && closest.distance(new PhetVector(modelMousePoint.x, modelMousePoint.y)) < DefaultBranchInteractionHandler.STICKY_DISTANCE) {
//            junction.setLocationWithoutUpdate(closest.getX(), closest.getY());
            junction.setLocation(closest.getX(), closest.getY());
        } else {
            junction.setLocation(modelMousePoint.x, modelMousePoint.y);
//            junction.setLocationWithoutUpdate(modelMousePoint.x, modelMousePoint.y);
        }

        fireDragHappened();
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void fireDragHappened(){
        module.relayoutElectrons(this.junction);
        junction.fireLocationChanged();
        module.junctionDragged();
        module.repaint();
    }

    public void mouseReleased(MouseEvent event) {
        //check for a close location
        Point2D.Double modelMousePoint = module.getTransform().viewToModel(event.getX(), event.getY());
        Junction closest = junction.getBranch().getCircuit().getClosestJunction(junction, modelMousePoint.x, modelMousePoint.y);

        if (closest != null && closest.distance(new PhetVector(modelMousePoint.x, modelMousePoint.y)) < DefaultBranchInteractionHandler.STICKY_DISTANCE) {
            junction.setLocation(closest.getX(), closest.getY());
            junction.addConnection(closest);
            module.getApparatusPanel().repaint();
        }
        if (SwingUtilities.isRightMouseButton(event)) {
            menu.show(event.getComponent(), event.getX(), event.getY());
        }
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent event) {
        event.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public void mouseExited(MouseEvent event) {
        event.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    public void paint(Graphics2D g) {
        if (viewPoint == null)
            return;
        if (numConnections >= 1) {
            g.setColor(filledColor);
            g.fillOval(viewPoint.x - radius, viewPoint.y - radius, radius * 2, radius * 2);
        } else {
            g.setColor(color);
            g.setStroke(stroke);

            //should do something else if connected.
            g.drawOval(viewPoint.x - radius, viewPoint.y - radius, radius * 2, radius * 2);
        }
        if (selected) {
            g.setColor(highlight);
            g.setStroke(stroke);
            g.drawOval(viewPoint.x - radius - dr, viewPoint.y - radius - dr, 2 * (radius + dr), 2 * (radius + dr));
        }
        if (showConnectionCount) {
            g.setFont(new Font("dialog", 0, 12));
            g.setColor(Color.black);
            g.drawString("I=" + junction.getIndex(), viewPoint.x, viewPoint.y - 20);

            g.setColor(Color.red);
            g.drawString("NumConnections=" + numConnections, viewPoint.x, viewPoint.y);
            g.drawString("Conn=" + junction.connections.toString(), viewPoint.x, viewPoint.y + 40);
        }
        if (showID) {
            g.setFont(new Font("dialog", Font.BOLD, 18));
            g.setColor(Color.black);
            g.drawString("J=" + junction.getIndex(), viewPoint.x, viewPoint.y);
        }
    }

    public void transformChanged(ModelViewTransform2D ModelViewTransform2D) {
        update();
    }

    public void update() {
        Point2D.Double modelPoint = null;
        modelPoint = new Point2D.Double(junction.getX(), junction.getY());
        viewPoint = module.getTransform().modelToView(modelPoint);
        numConnections = junction.numConnections();
        module.getApparatusPanel().repaint();
    }

    public int getY() {
        return viewPoint.y;
    }

    public int getX() {
        return viewPoint.x;
    }

    public boolean contains(int x, int y) {
        if (viewPoint == null)
            return false;
        double dist = new Point(x,y).distance(viewPoint);
        return dist <= radius;
    }
}
