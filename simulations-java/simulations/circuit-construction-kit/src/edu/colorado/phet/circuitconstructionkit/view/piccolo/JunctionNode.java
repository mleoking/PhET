// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.view.piccolo;

import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.circuitconstructionkit.model.Circuit;
import edu.colorado.phet.circuitconstructionkit.model.CircuitListenerAdapter;
import edu.colorado.phet.circuitconstructionkit.model.Junction;
import edu.colorado.phet.circuitconstructionkit.model.components.Branch;
import edu.colorado.phet.circuitconstructionkit.model.components.Wire;
import edu.colorado.phet.circuitconstructionkit.view.CCKLookAndFeel;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Sep 15, 2006
 * Time: 9:40:58 PM
 */

public class JunctionNode extends PhetPNode {
    private CCKModel cckModel;
    private Junction junction;
    private PPath shapeNode;
    private PPath highlightNode;
    private CircuitInteractionModel circuitInteractionModel;

    public JunctionNode(final CCKModel cckModel, final Junction junction, final CircuitNode circuitNode, Component component) {
        this.cckModel = cckModel;
        this.junction = junction;
        this.circuitInteractionModel = new CircuitInteractionModel(cckModel);
        shapeNode = new PPath();
        Stroke shapeStroke = new BasicStroke(2);
        shapeNode.setStroke(shapeStroke);
        shapeNode.setPaint(Color.blue);
        highlightNode = new PPath();
        highlightNode.setStroke(new BasicStroke((float) (3 / 80.0)));
        highlightNode.setStrokePaint(Color.yellow);

        highlightNode.setPickable(false);
        highlightNode.setChildrenPickable(false);

        addChild(shapeNode);
        addChild(highlightNode);

        junction.addObserver(new SimpleObserver() {
            public void update() {
                JunctionNode.this.update();
            }
        });
        shapeNode.setStrokePaint(Color.red);
        addInputEventListener(new PBasicInputEventHandler() {
            public void mouseDragged(PInputEvent event) {
                circuitInteractionModel.dragJunction(junction, event.getPositionRelativeTo(JunctionNode.this));
            }

            public void mousePressed(PInputEvent event) {
                if (event.isControlDown()) {
                    junction.setSelected(!junction.isSelected());
                } else {
                    getCircuit().setSelection(junction);
                }
            }

            public void mouseReleased(PInputEvent event) {
                circuitInteractionModel.dropJunction(junction);
            }
        });
        addInputEventListener(new DynamicPopupMenuHandler(component, new DynamicPopupMenuHandler.JPopupMenuFactory() {
            public JPopupMenu createPopupMenu() {
                return new JunctionNodePopupMenu(cckModel, junction);
            }
        }));
        addInputEventListener(new CursorHandler());
        update();
        circuitNode.getCircuit().addCircuitListener(new CircuitListenerAdapter() {
            public void junctionsConnected(Junction a, Junction b, Junction newTarget) {
                update();
            }

            public void junctionsSplit(Junction old, Junction[] j) {
                update();
            }

            public void branchRemoved(Branch branch) {
                update();
            }

            public void junctionRemoved(Junction junction) {
                update();
            }
        });
    }

    private Stroke createStroke(double strokeWidth) {
        float scale = (float) 80.0;
        float[] dash = new float[]{3 / scale, 6 / scale};
        return new BasicStroke((float) strokeWidth, BasicStroke.CAP_SQUARE, BasicStroke.CAP_BUTT, 3, dash, 0);
    }

    private void update() {
        shapeNode.setPathTo(junction.getShape());
        double strokeWidthModelCoords = CCKModel.JUNCTION_GRAPHIC_STROKE_WIDTH;
        shapeNode.setStroke(createStroke(strokeWidthModelCoords * (isConnected() ? 1.2 : 2)));
        shapeNode.setStrokePaint(isConnected() ? Color.black : Color.red);
        shapeNode.setPaint(isConnectedTo2Wires() ? CCKLookAndFeel.COPPER : new Color(0, 0, 0, 0));

        highlightNode.setPathTo(junction.createCircle(CCKModel.JUNCTION_RADIUS * 1.6));
        highlightNode.setStroke(new BasicStroke((float) (3.0 / 80.0)));
        highlightNode.setVisible(junction.isSelected());
    }

    private boolean isConnectedTo2Wires() {
        if (!isConnected()) {
            return false;
        }
        Branch[] branches = getCircuit().getAdjacentBranches(junction);
        for (Branch branch : branches) {
            if (!(branch instanceof Wire)) {
                return false;
            }
        }
        return true;
    }

    private boolean isConnected() {
        return getCircuit().getNeighbors(junction).length > 1;
    }

    public Junction getJunction() {
        return junction;
    }

    private Circuit getCircuit() {
        return cckModel.getCircuit();
    }
}
