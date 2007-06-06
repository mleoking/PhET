/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: C:/Java/cvs/root/SelfDrivenParticles/phetcommon/src/edu/colorado/phet/common/view/graphics/mousecontrols/BringToFront.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: Sam Reid $
 * Revision : $Revision: 1.1.1.1 $
 * Date modified : $Date: 2005/08/10 08:22:02 $
 */
package edu.colorado.phet.common.view.graphics.mousecontrols;

import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;

import javax.swing.event.MouseInputListener;
import java.awt.event.MouseEvent;

/**
 * BringToFront
 *
 * @author ?
 * @version $Revision: 1.1.1.1 $
 */
public class BringToFront implements MouseInputListener {
    private GraphicLayerSet graphicTree;
    private PhetGraphic target;

    public BringToFront( GraphicLayerSet graphicTree, PhetGraphic target ) {
        this.graphicTree = graphicTree;
        this.target = target;
    }

    public void mouseClicked( MouseEvent e ) {
    }

    public void mousePressed( MouseEvent e ) {
        graphicTree.moveToTop( target );
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
