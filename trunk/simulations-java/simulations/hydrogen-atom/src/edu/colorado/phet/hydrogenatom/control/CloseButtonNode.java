/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.control;

import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * CloseButtonNode is a node that displays a close button.
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
    
    public static class CloseButton extends JButton {
        public CloseButton() {
            super();
            BufferedImage closeImage = PhetCommonResources.getImage( "buttons/closeButton.png" );
            Icon closeIcon = new ImageIcon( closeImage );
            setIcon( closeIcon );
            setOpaque( false );
            setMargin( new Insets( 0, 0, 0, 0 ) );
        }
    }
}
