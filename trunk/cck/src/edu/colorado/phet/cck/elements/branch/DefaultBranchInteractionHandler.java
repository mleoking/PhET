package edu.colorado.phet.cck.elements.branch;

import edu.colorado.phet.cck.CCK2Module;
import edu.colorado.phet.cck.elements.InteractionHandler;
import edu.colorado.phet.cck.elements.circuit.Circuit;
import edu.colorado.phet.cck.elements.junction.Junction;
import edu.colorado.phet.coreadditions.graphics.DifferentialDragHandler;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

public class DefaultBranchInteractionHandler implements InteractionHandler {
    public static final double STICKY_DISTANCE = .3;
    protected Branch branch;
    protected Branch copy;
    ModelViewTransform2d transform;
    Circuit circuit;
    CCK2Module module;
    JPopupMenu menu;
    private JPopupMenu showMenuMenu;
    DifferentialDragHandler ddh;
    private JMenu mymenu;

    public DefaultBranchInteractionHandler(Branch branch, ModelViewTransform2d transform, Circuit circuit, CCK2Module module, JPopupMenu menu, JPopupMenu showMenuMenu) {
        this.branch = branch;
        this.transform = transform;
        this.circuit = circuit;
        this.module = module;
        this.menu = menu;
        this.showMenuMenu = showMenuMenu;
        mymenu = new JMenu("Resistor Right-Click Menu");
        mymenu.add(new JMenuItem("Delete"));
    }

    public void mouseDragged(MouseEvent event) {

        Point viewDX = ddh.getDifferentialLocationAndReset(event.getPoint());
        Point2D.Double modelDX = transform.viewToModelDifferential(viewDX);
        /**Check for vertex overlap.*/
        copy.translate(modelDX.x, modelDX.y);
        branch.setLocation(copy);

        Junction closestToStart = circuit.getClosestJunction(branch.getStartJunction());
        if (closestToStart != null && closestToStart.distance(copy.getStart()) < STICKY_DISTANCE) {
            branch.getStartJunction().setLocation(closestToStart.getX(), closestToStart.getY());
        }

        Junction closestToEnd = circuit.getClosestJunction(branch.getEndJunction());
        if (closestToEnd != null && closestToEnd.distance(copy.getEnd()) < STICKY_DISTANCE) {
            branch.getEndJunction().setLocation(closestToEnd.getX(), closestToEnd.getY());
        }

        module.relayoutElectrons(branch);
        Branch[] b = module.getCircuit().getAdjacentBranches(branch);
        for (int i = 0; i < b.length; i++) {
            Branch branch2 = b[i];
            module.relayoutElectrons(branch2);
        }
        module.branchMoved();
        module.repaint();
    }

    public void mouseReleased(MouseEvent event) {
        if (SwingUtilities.isRightMouseButton(event)) {
            menu.show(event.getComponent(), event.getX(), event.getY());
        }
        Junction closestToStart = circuit.getClosestJunction(branch.getStartJunction());
        if (closestToStart != null && closestToStart.distance(copy.getStart()) < STICKY_DISTANCE) {
            branch.getStartJunction().addConnection(closestToStart);
        }
        Junction closestToEnd = circuit.getClosestJunction(branch.getEndJunction());
        if (closestToEnd != null && closestToEnd.distance(copy.getEnd()) < STICKY_DISTANCE) {
            branch.getEndJunction().addConnection(closestToEnd);
        }
        module.repaint();
    }

    public JMenuBar getJMenuBar(MouseEvent event) {
        Window parent = SwingUtilities.windowForComponent(event.getComponent());
        JFrame jp = (JFrame) parent;
        JMenuBar jmb = jp.getJMenuBar();
        return jmb;
    }

    public void mouseEntered(MouseEvent event) {
        event.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

//            getJMenuBar(event).add(mymenu);
//            getJMenuBar(event).doLayout();
//            event.getComponent().get
//            showMenuMenu.show(event.getComponent(), event.getX(),event.getY());
//            showMenuMenu.setVisible(true);
    }

    public void mouseExited(MouseEvent event) {
        event.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
//            getJMenuBar(event).remove(mymenu);
    }

    public boolean canHandleMousePress(MouseEvent event) {
        return false;
    }

    public void mousePressed(MouseEvent event) {
        ddh = new DifferentialDragHandler(event.getPoint());
        copy = new BareBranch(circuit, branch);
        module.deselectAll();
        branch.setSelected(true);
//            show//TODO show menu.
    }

}

