/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.nuclearphysics2.util;

import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import edu.colorado.phet.common.phetcommon.view.controls.StandardIconButton.CloseButton;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Resources;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * This class places a "Fire!" button on the parent node.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class FireButtonNode extends PhetPNode {

    private JButton _fireButton;

    public FireButtonNode() {
        ImageIcon fireButtonImage = new ImageIcon(NuclearPhysics2Resources.getImage("fire-button.png"));
        _fireButton = new JButton(fireButtonImage);
        _fireButton.setPressedIcon( new ImageIcon (NuclearPhysics2Resources.getImage("fire-button-down.png") ));
        PSwing closeButtonWrapper = new PSwing( _fireButton );
        closeButtonWrapper.addInputEventListener( new CursorHandler() );
        addChild( closeButtonWrapper );
    }

    public void addActionListener( ActionListener listener ) {
        _fireButton.addActionListener( listener );
    }

    public void removeActionListener( ActionListener listener ) {
        _fireButton.removeActionListener( listener );
    }
}
