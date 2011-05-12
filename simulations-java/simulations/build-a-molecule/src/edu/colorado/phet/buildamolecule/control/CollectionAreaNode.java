// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.control;

import java.awt.*;

import edu.colorado.phet.buildamolecule.BuildAMoleculeConstants;
import edu.colorado.phet.buildamolecule.BuildAMoleculeStrings;
import edu.colorado.phet.buildamolecule.model.CollectionBox;
import edu.colorado.phet.buildamolecule.model.KitCollectionModel;
import edu.colorado.phet.buildamolecule.view.BuildAMoleculeCanvas;
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

    public static final int CONTAINER_PADDING = 15;

    /**
     * Creates a collection area (with collection boxes)
     *
     * @param parentFrame
     * @param canvas               The main canvas (the boxes need references to hook view => model coordinates)
     * @param model                Our model
     * @param singleCollectionMode Whether we should use single or multiple molecule collection boxes
     */
    public CollectionAreaNode( Frame parentFrame, BuildAMoleculeCanvas canvas, KitCollectionModel model, boolean singleCollectionMode ) {
        layoutNode = new SwingLayoutNode( new GridBagLayout() );

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets( 0, 0, 20, 0 );

        layoutNode.addChild( new HTMLNode( BuildAMoleculeStrings.COLLECTION_AREA_YOUR_MOLECULE_COLLECTION ) {{
                                 setFont( new PhetFont( 22 ) );
                             }}, c );

        c.insets = new Insets( 0, 0, 15, 0 );

        layoutNode.translate( CONTAINER_PADDING, CONTAINER_PADDING );

        // add nodes for all of our collection boxes.
        for ( CollectionBox collectionBox : model.getCollectionBoxes() ) {
            c.gridy += 1;
            if ( singleCollectionMode ) {
                layoutNode.addChild( new SingleCollectionBoxNode( parentFrame, canvas, collectionBox ), c );
            }
            else {
                layoutNode.addChild( new MultipleCollectionBoxNode( parentFrame, canvas, collectionBox ), c );
            }
        }

        c.insets = new Insets( 0, 0, 0, 0 );
        c.gridy++;
        layoutNode.addChild( new SoundOnOffNode(), c );

        PPath background = PPath.createRectangle( 0, 0, (float) getPlacementWidth(), (float) getPlacementHeight() );

        background.setPaint( BuildAMoleculeConstants.MOLECULE_COLLECTION_BACKGROUND );
        background.setStrokePaint( BuildAMoleculeConstants.MOLECULE_COLLECTION_BORDER );

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
