// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * ControlPanelNode displays another PNode centered on top of a background and border.  It can be used for floating control panels.
 *
 * @author Sam Reid
 */
public class ControlPanelNode extends PNode {
    public ControlPanelNode( final PNode content ) {
        this( content, new Color( 238, 238, 238 ), new BasicStroke( 2 ), Color.gray );
    }

    public ControlPanelNode( final PNode content, Color backgroundColor, BasicStroke borderStroke, Color borderColor ) {
        this( content, backgroundColor, borderStroke, borderColor, 9 );
    }

    public ControlPanelNode( final PNode content, Color backgroundColor, BasicStroke borderStroke, Color borderColor, final int inset ) {
        this( content, backgroundColor, borderStroke, borderColor, inset, 20, true );
    }

    public ControlPanelNode( final PNode content, Color backgroundColor, BasicStroke borderStroke, Color borderColor, final int inset, final int arc, boolean transparifySwing ) {
        //Make sure the background resizes when the content resizes
        final PhetPPath background = new PhetPPath( backgroundColor, borderStroke, borderColor ) {{
            final PropertyChangeListener updateSize = new PropertyChangeListener() {
                public void propertyChange( PropertyChangeEvent evt ) {
                    final PBounds layoutSize = getControlPanelBounds( content );
                    setPathTo( new RoundRectangle2D.Double( 0, 0, layoutSize.width + inset * 2, layoutSize.height + inset * 2, arc, arc ) );
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
     * Determine the bounds of the control panel.  This implementation uses the bounds of the content.
     *
     * @param content the content PNode in this ControlPanelNode
     * @return the PBounds which the ControlPanelNode should occupy
     */
    protected PBounds getControlPanelBounds( PNode content ) {
        return content.getFullBounds();
    }

    /**
     * Make it so you can see through all the swing components, so the panel background color shows through.
     *
     * @param node
     */
    private void transparifySwing( PNode node ) {
        for ( int i = 0; i < node.getChildrenCount(); i++ ) {
            PNode child = node.getChild( i );
            if ( child instanceof PSwing ) {
                SwingUtils.setBackgroundDeep( ( (PSwing) child ).getComponent(), new Color( 0, 0, 0, 0 ),
                                              new Class[] { JTextComponent.class,
                                                      JComboBox.class//have to ignore this one or the drop down button color changes (usually for the worse)
                                              }, false );
            }
            transparifySwing( child );
        }
    }
}