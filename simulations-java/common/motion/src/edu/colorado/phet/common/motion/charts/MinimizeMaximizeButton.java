package edu.colorado.phet.common.motion.charts;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * @author Sam Reid
 */
public class MinimizeMaximizeButton extends PNode {
    private PImage minimizeButton;
    private PImage maximizeButton;
    private MutableBoolean maximized = new MutableBoolean(true);

    public MutableBoolean getMaximized() {
        return maximized;
    }

    public MinimizeMaximizeButton() {
        final int iconButtonInset = 2;
        {
            minimizeButton = new PImage(PhetCommonResources.getImage(PhetCommonResources.IMAGE_MINIMIZE_BUTTON)) {
                public void setVisible(boolean isVisible) {
                    super.setVisible(isVisible);
                    setPickable(isVisible);
                }
            };
            addChild(minimizeButton);
            minimizeButton.addInputEventListener(new CursorHandler());
            minimizeButton.addInputEventListener(new PBasicInputEventHandler() {
                public void mouseReleased(PInputEvent event) {
                    maximized.setValue(false);
                }
            });
//            SimpleObserver locationUpdate = new SimpleObserver() {
//                public void update() {
//                    minimizeButton.setOffset(viewDimension.getWidth() - minimizeButton.getFullBounds().getWidth() - iconButtonInset, iconButtonInset);
//                }
//            };
//            locationUpdate.update();
//            viewDimension.addObserver(locationUpdate);
        }
        {
            maximizeButton = new PImage(PhetCommonResources.getImage(PhetCommonResources.IMAGE_MAXIMIZE_BUTTON)) {
                public void setVisible(boolean isVisible) {
                    super.setVisible(isVisible);
                    setPickable(isVisible);
                }
            };
            addChild(maximizeButton);
            maximizeButton.addInputEventListener(new CursorHandler());
            maximizeButton.addInputEventListener(new PBasicInputEventHandler() {
                public void mouseReleased(PInputEvent event) {
                    maximized.setValue(true);
                }
            });
            
//            SimpleObserver locationUpdate = new SimpleObserver() {
//                public void update() {
//                    maximizeButton.setOffset(viewDimension.getWidth() - maximizeButton.getFullBounds().getWidth() - iconButtonInset, iconButtonInset);
//                }
//            };
//            locationUpdate.update();
//            viewDimension.addObserver(locationUpdate);
        }
        final SimpleObserver observer = new SimpleObserver() {
            public void update() {
                minimizeButton.setVisible(maximized.getValue());
                maximizeButton.setVisible(!maximized.getValue());
            }
        };
        maximized.addObserver(observer);
        observer.update();
        
    }
}
