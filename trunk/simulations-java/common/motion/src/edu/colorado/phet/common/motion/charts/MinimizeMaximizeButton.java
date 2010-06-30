package edu.colorado.phet.common.motion.charts;

import edu.colorado.phet.common.piccolophet.nodes.MinimizeMaximizeNode;
import edu.umd.cs.piccolo.PNode;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author Sam Reid
 */
public class MinimizeMaximizeButton extends PNode {
    private MinimizeMaximizeNode node;
    private MutableBoolean maximized = new MutableBoolean(true);

    public MinimizeMaximizeButton(String title) {
        node = new MinimizeMaximizeNode(title, MinimizeMaximizeNode.BUTTON_RIGHT);
        node.setMaximized(maximized.getValue());
        node.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                maximized.setValue(node.isMaximized());
            }
        });
        addChild(node);
    }

    public MutableBoolean getMaximized() {
        return maximized;
    }
}
