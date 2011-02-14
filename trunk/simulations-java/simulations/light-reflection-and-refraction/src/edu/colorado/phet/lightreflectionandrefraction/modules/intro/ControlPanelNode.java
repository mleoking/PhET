// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules.intro;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * ControlPanelNode displays another PNode centered on top of a background and border.  It can be used for floating control panels.
 *
 * @author Sam Reid
 */
public class ControlPanelNode extends PNode {
    public ControlPanelNode( final PNode content ) {
        this( content, 9, Color.lightGray, new BasicStroke( 2 ), Color.darkGray, 20, true );
    }

    public ControlPanelNode( final PNode content, final int inset, Color backgroundColor, BasicStroke borderStroke, Color borderColor, final int arc, boolean transparifySwing ) {
        //Make sure the background resizes when the content resizes
        final PhetPPath background = new PhetPPath( backgroundColor, borderStroke, borderColor ) {{
            final PropertyChangeListener updateSize = new PropertyChangeListener() {
                public void propertyChange( PropertyChangeEvent evt ) {
                    setPathTo( new RoundRectangle2D.Double( 0, 0, content.getFullBounds().width + inset * 2, content.getFullBounds().height + inset * 2, arc, arc ) );
                }
            };
            content.addPropertyChangeListener( PROPERTY_FULL_BOUNDS, updateSize );
            updateSize.propertyChange( null );
        }};
        content.setOffset( inset, inset );
        addChild( background );
        addChild( content );
        if ( transparifySwing ) {
            transparifySwing( this );
        }
    }

    /**
     * Make it so you can see through all the swing components, so the colors match up.
     *
     * @param node
     */
    private void transparifySwing( PNode node ) {
        for ( int i = 0; i < node.getChildrenCount(); i++ ) {
            PNode child = getChild( i );
            if ( child instanceof PSwing ) {
                SwingUtils.setBackgroundDeep( ( (PSwing) child ).getComponent(), new Color( 0, 0, 0, 0 ), new Class[] { JTextComponent.class, JTextField.class, JFormattedTextField.class }, false );
            }
            transparifySwing( child );
        }
    }
}