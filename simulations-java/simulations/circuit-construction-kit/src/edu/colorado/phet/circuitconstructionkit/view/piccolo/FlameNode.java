// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.view.piccolo;

import edu.colorado.phet.circuitconstructionkit.model.components.Branch;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.nodes.PImage;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Oct 4, 2006
 * Time: 11:36:24 PM
 */

public class FlameNode extends PhetPNode {
    private Branch branch;
    private BufferedImage flameImage;

    public FlameNode(Branch branch) {
        this.branch = branch;
        try {
            flameImage = ImageLoader.loadBufferedImage("circuit-construction-kit/images/flame.gif");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        branch.addFlameListener(new Branch.FlameListener() {
            public void flameStarted() {
                update();
            }

            public void flameFinished() {
                update();
            }
        });
        update();
        setPickable(false);
        setChildrenPickable(false);
    }

    private void update() {
        removeAllChildren();
        if (branch.isOnFire()) {
            addChild(new PImage(flameImage));
        }
    }

}
