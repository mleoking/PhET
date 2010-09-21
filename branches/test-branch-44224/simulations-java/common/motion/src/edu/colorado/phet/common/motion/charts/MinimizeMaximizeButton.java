package edu.colorado.phet.common.motion.charts;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.MutableBoolean;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.MinimizeMaximizeNode;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class MinimizeMaximizeButton extends PNode {
    private MinimizeMaximizeNode node;
    private MutableBoolean maximized;
    private final boolean defaultMaximizedValue;

    public MinimizeMaximizeButton(String title) {
       this( title, true );
    }
    public MinimizeMaximizeButton(String title, boolean maximizedValue ) {
       
        node = new MinimizeMaximizeNode(title, MinimizeMaximizeNode.BUTTON_RIGHT);
        addChild(node);
        
        SimpleObserver maximizedObserver = new SimpleObserver() {
            public void update() {
                node.setMaximized( maximized.getValue() );
            }
        };
        
        this.maximized = new MutableBoolean(maximizedValue);
        this.maximized.addObserver( maximizedObserver );
        maximizedObserver.update();
        
        this.defaultMaximizedValue = maximizedValue;
        node.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                maximized.setValue(node.isMaximized());
            }
        });
    }

    public MutableBoolean getMaximized() {
        return maximized;
    }
    
    public void reset() {
        maximized.setValue( defaultMaximizedValue );
    }
}
