// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.control;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.buildamolecule.model.CollectionBox;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolox.swing.SwingLayoutNode;

/**
 * Allows the collection of a single molecule
 */
public class SingleCollectionBoxNode extends SwingLayoutNode {
    public SingleCollectionBoxNode( CollectionBox box ) {
        super( new GridBagLayout() );
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;

        // TODO: i18nize
        addChild( new HTMLNode( box.getMoleculeType().getMoleculeStructure().getMolecularFormulaHTMLFragment() + " (" + box.getMoleculeType().getCommonName() + ")" ) {{
            setFont( new PhetFont( 16, true ) );
        }}, c );

        c.gridy = 1;
        c.insets = new Insets( 3, 0, 0, 0 ); // some padding between the two
        addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, 160, 50 ), Color.BLACK ), c );
    }
}
