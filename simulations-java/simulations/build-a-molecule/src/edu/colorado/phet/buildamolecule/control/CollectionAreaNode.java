// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.List;

import edu.colorado.phet.buildamolecule.BuildAMoleculeStrings;
import edu.colorado.phet.buildamolecule.model.CollectionBox;
import edu.colorado.phet.buildamolecule.model.Kit;
import edu.colorado.phet.buildamolecule.model.KitCollection;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.swing.SwingLayoutNode;

/**
 * Area that shows all of the collection boxes and a reset collection button
 */
public class CollectionAreaNode extends PNode {

    private List<CollectionBoxNode> collectionBoxNodes = new LinkedList<CollectionBoxNode>();

    /**
     * Creates a collection area (with collection boxes)
     *
     * @param collection           Our model
     * @param singleCollectionMode Whether we should use single or multiple molecule collection boxes
     * @param toModelBounds        Function to convert piccolo node bounds to model bounds
     */
    public CollectionAreaNode( final KitCollection collection, final boolean singleCollectionMode, Function1<PNode, Rectangle2D> toModelBounds ) {
        SwingLayoutNode layoutNode = new SwingLayoutNode( new GridBagLayout() );

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets( 0, 0, 15, 0 );

        final double maximumBoxWidth = singleCollectionMode ? SingleCollectionBoxNode.getMaxWidth() : MultipleCollectionBoxNode.getMaxWidth();
        final double maximumBoxHeight = singleCollectionMode ? SingleCollectionBoxNode.getMaxHeight() : MultipleCollectionBoxNode.getMaxHeight();

        // add nodes for all of our collection boxes.
        for ( CollectionBox collectionBox : collection.getCollectionBoxes() ) {
            final CollectionBoxNode collectionBoxNode = singleCollectionMode
                                                        ? new SingleCollectionBoxNode( collectionBox, toModelBounds )
                                                        : new MultipleCollectionBoxNode( collectionBox, toModelBounds );
            collectionBoxNodes.add( collectionBoxNode );

            // center box horizontally and put at bottom vertically in our holder
            collectionBoxNode.setOffset( ( maximumBoxWidth - collectionBoxNode.getFullBounds().getWidth() ) / 2,
                                         maximumBoxHeight - collectionBoxNode.getFullBounds().getHeight() );

            // enforce consistent bounds of the maximum size
            PNode collectionBoxHolder = new PNode() {{
                // invisible background. enforces SwingLayoutNode's correct positioning
                addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, maximumBoxWidth, maximumBoxHeight ) ) {{
                    setStroke( null ); // don't add any sort of border to mess up the bounds
                    setVisible( false );
                }} );

                addChild( collectionBoxNode );
            }};
            layoutNode.addChild( collectionBoxHolder, c );
            c.gridy += 1;
        }

        layoutNode.addChild( new HTMLImageButtonNode( BuildAMoleculeStrings.RESET_COLLECTION, Color.ORANGE ) {
                                 {
                                     // when clicked, empty collection boxes
                                     addActionListener( new ActionListener() {
                                         public void actionPerformed( ActionEvent e ) {
                                             for ( CollectionBox box : collection.getCollectionBoxes() ) {
                                                 box.clear();
                                             }
                                             for ( Kit kit : collection.getKits() ) {
                                                 kit.resetKit();
                                             }
                                         }
                                     } );

                                     // when any collection box quantity changes, re-update our visibility
                                     for ( CollectionBox box : collection.getCollectionBoxes() ) {
                                         box.quantity.addObserver( new SimpleObserver() {
                                             public void update() {
                                                 updateEnabled();
                                             }
                                         } );
                                     }
                                 }

                                 public void updateEnabled() {
                                     boolean enabled = false;
                                     for ( CollectionBox box : collection.getCollectionBoxes() ) {
                                         if ( box.quantity.get() > 0 ) {
                                             enabled = true;
                                         }
                                     }
                                     setEnabled( enabled );
                                 }

                                 @Override public void addPropertyChangeListener( PropertyChangeListener listener ) {
                                     // TODO can we get rid of this hack ?
                                 }
                             }, c );

        addChild( layoutNode );

    }

    public void updateCollectionBoxLocations() {
        for ( CollectionBoxNode collectionBoxNode : collectionBoxNodes ) {
            collectionBoxNode.updateLocation();
        }
    }
}
