// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.nuclearphysics.util;

import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * This class places a button on the parent node.
 *
 * @author John Blanco
 */
public class PhetButtonNode extends PhetPNode {

    private JButton _button;

    public PhetButtonNode(String text) {
        _button = new JButton(text);
        _button.setFont( new PhetFont( Font.PLAIN, 14 ) );
        SwingUtils.fixButtonOpacity( _button );
        PSwing buttonWrapper = new PSwing( _button );
        buttonWrapper.addInputEventListener( new CursorHandler() );
        addChild( buttonWrapper );
    }

    public void addActionListener( ActionListener listener ) {
        _button.addActionListener( listener );
    }

    public void removeActionListener( ActionListener listener ) {
        _button.removeActionListener( listener );
    }
    
    public double getWidth(){
        return _button.getWidth();
    }

    public double getHeight(){
        return _button.getHeight();
    }
}
