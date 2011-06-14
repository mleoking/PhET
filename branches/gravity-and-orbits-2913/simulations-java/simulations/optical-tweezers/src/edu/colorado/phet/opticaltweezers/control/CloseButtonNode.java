// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.opticaltweezers.control;

import java.awt.event.ActionListener;

import javax.swing.JButton;

import edu.colorado.phet.common.phetcommon.view.controls.StandardIconButton.CloseButton;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * CloseButtonNode is a node that displays the standard PhET close button.
 * ActionListeners can be registered with the close button.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class CloseButtonNode extends PhetPNode {

    private JButton _closeButton;

    public CloseButtonNode() {
        _closeButton = new CloseButton();
        PSwing closeButtonWrapper = new PSwing( _closeButton );
        closeButtonWrapper.addInputEventListener( new CursorHandler() );
        addChild( closeButtonWrapper );
    }

    public void addActionListener( ActionListener listener ) {
        _closeButton.addActionListener( listener );
    }

    public void removeActionListener( ActionListener listener ) {
        _closeButton.removeActionListener( listener );
    }
}
