/** Sam Reid*/
package edu.colorado.phet.forces1d.view;

import edu.colorado.phet.common.view.graphics.InteractiveGraphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.forces1d.model.Block;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * User: Sam Reid
 * Date: Nov 13, 2004
 * Time: 10:27:14 AM
 * Copyright (c) Nov 13, 2004 by Sam Reid
 */
public class BlockGraphic extends CompositePhetGraphic implements InteractiveGraphic {
    private Block block;
    private ModelViewTransform2D transform2D;

    public BlockGraphic( Force1DPanel panel, Block block, ModelViewTransform2D transform2D ) {
        super( panel );
        this.block = block;
        this.transform2D = transform2D;
        block.addListener( new Block.Listener() {
            public void changed() {
            }
        } );
        addGraphic( new PhetShapeGraphic( panel, new Rectangle( 50, 150, 100, 100 ), Color.blue ) );
//        addGraphic( new PhetShapeGraphic( panel, new Rectangle( 300, 300, 50, 50 ), Color.red ) );
    }

    public void mouseClicked( MouseEvent e ) {
    }

    public void mousePressed( MouseEvent e ) {
    }

    public void mouseReleased( MouseEvent e ) {
    }

    public void mouseEntered( MouseEvent e ) {
    }

    public void mouseExited( MouseEvent e ) {
    }

    public void mouseDragged( MouseEvent e ) {
    }

    public void mouseMoved( MouseEvent e ) {
    }
}
