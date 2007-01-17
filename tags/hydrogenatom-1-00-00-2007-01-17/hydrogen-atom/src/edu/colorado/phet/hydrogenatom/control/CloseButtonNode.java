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
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * CloseButtonNode is a node that displays a close button.
 * ActionListeners can be registered with the close button.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class CloseButtonNode extends PhetPNode {

    private JButton _closeButton;
    
    public CloseButtonNode( PSwingCanvas canvas ) {
        _closeButton = new CloseButton();
        PSwing closeButtonWrapper = new PSwing( canvas, _closeButton );
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
            try {
                BufferedImage closeImage = ImageLoader.loadBufferedImage( HAConstants.IMAGE_CLOSE_BUTTON );
                Icon closeIcon = new ImageIcon( closeImage );
                setIcon( closeIcon );
                setOpaque( false );
                setMargin( new Insets( 0, 0, 0, 0 ) );
            }
            catch ( IOException e ) {
                e.printStackTrace();
                setText( "X" );
            }
        }
    }
}
