/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.elements.branch;

import edu.colorado.phet.cck.CCK2Module;
import edu.colorado.phet.cck.common.DifferentialDragHandler;
import edu.colorado.phet.cck.elements.circuit.Circuit;
import edu.colorado.phet.cck.elements.junction.Junction;
import edu.colorado.phet.cck.selection.SelectionListener;
import edu.colorado.phet.common.view.graphics.InteractiveGraphic;
import edu.colorado.phet.common.view.graphics.bounds.Boundary;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;

/**
 * User: Sam Reid
 * Date: Aug 23, 2003
 * Time: 3:15:26 PM
 * Copyright (c) Aug 23, 2003 by Sam Reid
 */
public class BranchGraphic implements BranchObserver, SelectionListener, AbstractBranchGraphic, Boundary {
    private Circuit circuit;
    private ModelViewTransform2D transform;
    Branch branch;
    private Color color;
    private Stroke stroke;
    private CCK2Module module;
    Point start;
    Point end;
    Shape viewShape = null;
    JPopupMenu menu;
    DefaultBranchInteractionHandler interactionHandler;
    private boolean selected;
    private Color highlightColor;
    private Stroke highlightStroke;
    private boolean showIndex = false;

    public BranchGraphic(final Circuit circuit, ModelViewTransform2D transform, final Branch branch, Color color, Stroke stroke, CCK2Module module, Color highlightColor, Stroke highlightStroke) {
        this.circuit = circuit;
        this.transform = transform;
        this.branch = branch;
        this.color = color;
        this.stroke = stroke;
        this.module = module;
        this.highlightColor = highlightColor;
        this.highlightStroke = highlightStroke;
        transform.addTransformListener(this);
        branch.addObserver(this);
        update();

        menu = new JPopupMenu();
        JMenuItem delete = new JMenuItem("Delete");
        delete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                circuit.removeBranch(branch);
            }
        });

        JPopupMenu jm = new JPopupMenu();
        menu.add(delete);
        interactionHandler = new DefaultBranchInteractionHandler(branch, transform, circuit, module, this.menu, jm);
    }

    public void update() {
        start = transform.modelToView(branch.getX1(), branch.getY1());
        end = transform.modelToView(branch.getX2(), branch.getY2());
        this.viewShape = stroke.createStrokedShape(new Line2D.Double(start.x, start.y, end.x, end.y));
    }

    final Font showFont = new Font("Lucida Sans", Font.BOLD, 16);

    public void paint(Graphics2D g) {
        if (start != null) {
            if (selected) {
                g.setColor(highlightColor);
                g.setStroke(highlightStroke);
                g.drawLine(start.x, start.y, end.x, end.y);
            }
            g.setColor(color);
            g.setStroke(stroke);
//            g.drawLine(start.x, start.y, end.x, end.y);
            g.fill(viewShape);
//            g.drawString("Length="+branch.getLength(),start.x,start.y);
//        addGraphic(new Graphic() {
//            public void paint(Graphics2D g) {
            if (showIndex) {
                g.setFont(showFont);
                g.setColor(Color.blue);
                String dir = "[" + branch.getId() + "]";//, "+branch.getCurrentGuessString();
                g.drawString(dir, start.x + 40, start.y);
            }
//            }
//        }, 100);
        }
    }

    public void transformChanged(ModelViewTransform2D mvt) {
        update();
    }

    public boolean canHandleMousePress(MouseEvent event) {
        return viewShape != null && viewShape.contains(event.getPoint());
    }

    public void mousePressed(MouseEvent event) {
        interactionHandler.mousePressed(event);
    }

    DifferentialDragHandler ddh;

    public void mouseDragged(MouseEvent event) {
        interactionHandler.mouseDragged(event);
        return;
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent event) {
        interactionHandler.mouseReleased(event);
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent event) {
        event.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public void mouseExited(MouseEvent event) {
        event.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    public void junctionMoved(Branch branch2, Junction junction) {
        update();
    }

    public void currentOrVoltageChanged(Branch branch2) {
    }

    public void selectionChanged(boolean sel) {
        this.selected = sel;
    }

    public Shape getStartWireShape() {
        return viewShape;
    }

    public Shape getEndWireShape() {
        return viewShape;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setWireColor(Color color) {
        this.color = color;
    }

    public InteractiveGraphic getMainBranchGraphic() {
        return this;
    }

    public boolean contains(int x, int y) {
        return viewShape != null && viewShape.contains(x, y);
    }

}
