// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.List;

import edu.colorado.phet.buildamolecule.model.CollectionBox;
import edu.colorado.phet.buildamolecule.model.Kit;
import edu.colorado.phet.buildamolecule.model.KitCollection;
import edu.colorado.phet.buildamolecule.view.BuildAMoleculeCanvas;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
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
     * @param canvas               The main canvas (the boxes need references to hook view => model coordinates)
     * @param collection           Our model
     * @param singleCollectionMode Whether we should use single or multiple molecule collection boxes
     */
    public CollectionAreaNode( BuildAMoleculeCanvas canvas, final KitCollection collection, boolean singleCollectionMode ) {
        SwingLayoutNode layoutNode = new SwingLayoutNode( new GridBagLayout() );

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets( 0, 0, 15, 0 );

        // add nodes for all of our collection boxes.
        for ( CollectionBox collectionBox : collection.getCollectionBoxes() ) {
            CollectionBoxNode collectionBoxNode = singleCollectionMode
                                                  ? new SingleCollectionBoxNode( canvas, collectionBox )
                                                  : new MultipleCollectionBoxNode( canvas, collectionBox );
            layoutNode.addChild( collectionBoxNode, c );
            collectionBoxNodes.add( collectionBoxNode );
            c.gridy += 1;
        }

        // TODO: i18n
        layoutNode.addChild( new HTMLImageButtonNode( "Reset Collection", Color.ORANGE ) {
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
