package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.nodes.PImage;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Oct 4, 2006
 * Time: 11:36:24 PM
 * Copyright (c) Oct 4, 2006 by Sam Reid
 */

public class FlameNode extends PhetPNode {
    private Branch branch;
    private BufferedImage flameImage;

    public FlameNode( Branch branch ) {
        this.branch = branch;
        try {
            flameImage = ImageLoader.loadBufferedImage( "images/flame.gif" );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        branch.addFlameListener( new Branch.FlameListener() {
            public void flameStarted() {
                update();
            }

            public void flameFinished() {
                update();
            }
        } );
        update();
        setPickable( false );
        setChildrenPickable( false );
    }

    private void update() {
        removeAllChildren();
        if( branch.isOnFire() ) {
            addChild( new PImage( flameImage ) );
        }
    }

}
