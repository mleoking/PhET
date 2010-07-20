package edu.colorado.phet.movingman.view;

import edu.colorado.phet.common.phetcommon.model.MutableBoolean;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.movingman.LinearTransform;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

import java.awt.image.BufferedImage;

/**
 * This adds minimize/maximize buttons for enabling/disabling the wall.
 *
 * @author Sam Reid
 */
public class WallNode extends PlayAreaObjectNode {
    private MutableBoolean walls;
    private final PImage minimizeButton;
    private final PImage maximizeButton;

    public WallNode(BufferedImage image, LinearTransform transform, double x, final MutableBoolean walls, double offsetX, MutableBoolean positiveToTheRight) {
        super(image, transform, x, offsetX, positiveToTheRight);//so that the edge of the man touches the edge of the wall when they collide instead of overlapping
        this.walls = walls;

        {//close button
            minimizeButton = new PImage(PhetCommonResources.getImage(PhetCommonResources.IMAGE_CLOSE_BUTTON));
            minimizeButton.addInputEventListener(new CursorHandler());
            addChild(minimizeButton);
            minimizeButton.addInputEventListener(new PBasicInputEventHandler() {
                public void mouseReleased(PInputEvent event) {
                    walls.setValue(false);
                }
            });
        }

        { //restore button
            maximizeButton = new PImage(PhetCommonResources.getImage(PhetCommonResources.IMAGE_MAXIMIZE_BUTTON));
            maximizeButton.addInputEventListener(new CursorHandler());
            addChild(maximizeButton);
            maximizeButton.addInputEventListener(new PBasicInputEventHandler() {
                public void mouseReleased(PInputEvent event) {
                    walls.setValue(true);
                }
            });
        }
        walls.addObserver(new SimpleObserver() {
            public void update() {
                updateVisibility();
            }
        });
        updateVisibility();
    }

    private void updateVisibility() {
        wallNode.setVisible(walls.getValue());
        minimizeButton.setVisible(walls.getValue());
        maximizeButton.setVisible(!walls.getValue());
    }

}
