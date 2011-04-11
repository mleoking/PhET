package edu.colorado.phet.buildamolecule.control;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.swing.SwingLayoutNode;

/**
 * Area that shows all of the collection boxes
 */
public class CollectionAreaNode extends PNode {
    private SwingLayoutNode layoutNode;

    public static final int CONTAINER_PADDING = 20;

    public CollectionAreaNode() {
        layoutNode = new SwingLayoutNode( new GridBagLayout() );

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets( 0, 0, 20, 0 );

        layoutNode.addChild( new HTMLNode( "<center>Your Molecule<br>Collection</center>" ) {{ // TODO: i18n
            setFont( new PhetFont( 22 ) );
        }}, c );

        c.insets = new Insets( 0, 0, 10, 0 );

        c.gridy = 1;
        layoutNode.addChild( new CollectionBoxNode(), c );
        c.gridy = 2;
        layoutNode.addChild( new CollectionBoxNode(), c );
        c.gridy = 3;
        layoutNode.addChild( new CollectionBoxNode(), c );

        layoutNode.translate( CONTAINER_PADDING, CONTAINER_PADDING );

        PPath background = PPath.createRectangle( 0, 0, (float) getPlacementWidth(), (float) getPlacementHeight() );

        background.setPaint( new Color( 202, 219, 42 ) );
        background.setStrokePaint( Color.BLACK );

        addChild( background );

        addChild( layoutNode );

    }

    public double getPlacementWidth() {
        return layoutNode.getContainer().getPreferredSize().getWidth() + CONTAINER_PADDING * 2;
    }

    public double getPlacementHeight() {
        return layoutNode.getContainer().getPreferredSize().getHeight() + CONTAINER_PADDING * 2;
    }
}
