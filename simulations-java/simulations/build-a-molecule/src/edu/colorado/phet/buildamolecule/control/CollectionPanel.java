//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.control;

import java.awt.*;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import edu.colorado.phet.buildamolecule.BuildAMoleculeConstants;
import edu.colorado.phet.buildamolecule.BuildAMoleculeStrings;
import edu.colorado.phet.buildamolecule.model.CollectionList;
import edu.colorado.phet.buildamolecule.model.CollectionList.Adapter;
import edu.colorado.phet.buildamolecule.model.CollectionList.Listener;
import edu.colorado.phet.buildamolecule.model.KitCollection;
import edu.colorado.phet.buildamolecule.view.MoleculeCollectingCanvas;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.swing.SwingLayoutNode;

/**
 * A panel that shows collection areas for different collections, and allows switching between those collections
 */
public class CollectionPanel extends PNode {

    public static final int CONTAINER_PADDING = 15;
    SwingLayoutNode layoutNode = new SwingLayoutNode( new GridBagLayout() );
    private final PNode collectionAreaHolder = new PNode();
    private final PNode backgroundHolder = new PNode();

    private final Map<KitCollection, CollectionAreaNode> collectionAreaMap = new HashMap<KitCollection, CollectionAreaNode>();
    private MoleculeCollectingCanvas canvas;

    public CollectionPanel( final Frame parentFrame, final MoleculeCollectingCanvas canvas, final CollectionList collectionList, final boolean singleCollectionMode ) {
        this.canvas = canvas;

        // move it over so the background will have padding
        layoutNode.translate( CONTAINER_PADDING, CONTAINER_PADDING );

        // "Your Molecule Collection"
        layoutNode.addChild( new HTMLNode( BuildAMoleculeStrings.COLLECTION_AREA_YOUR_MOLECULE_COLLECTION ) {{
                                 setFont( new PhetFont( 22 ) );
                             }},
                             new GridBagConstraints() {{
                                 gridx = 0;
                                 gridy = 0;
                                 insets = new Insets( 0, 0, 15, 0 );
                             }}
        );

        ;


        // "Collection X" with arrows
        layoutNode.addChild( new NextPreviousNavigationNode( new PText() {{
                                 collectionList.currentCollection.addObserver( new SimpleObserver() {
                                     public void update() {
                                         setFont( new PhetFont( 16, true ) );
                                         setText( "Collection " + ( collectionList.getCurrentIndex() + 1 ) );
                                     }
                                 } );
                             }}, Color.YELLOW, Color.BLACK, 14, 18 ) {
                                 {

                                     // update when the collection stuff might change. TODO simplify this
                                     final SimpleObserver updater = new SimpleObserver() {
                                         public void update() {
                                             hasNext.set( collectionList.hasNextCollection() );
                                             hasPrevious.set( collectionList.hasPreviousCollection() );
                                         }
                                     };
                                     collectionList.currentCollection.addObserver( updater );
                                     collectionList.addListener( new Listener() {
                                         public void addedCollection( KitCollection collection ) {
                                             updater.update();
                                         }

                                         public void removedCollection( KitCollection collection ) {
                                             updater.update();
                                         }
                                     } );
                                 }

                                 @Override protected void next() {
                                     collectionList.switchToNextCollection();
                                 }

                                 @Override protected void previous() {
                                     collectionList.switchToPreviousCollection();
                                 }

                                 @Override public void addPropertyChangeListener( PropertyChangeListener listener ) {
                                     // TODO can we get rid of this hack ?
                                 }

                             },
                             new GridBagConstraints() {{
                                 gridx = 0;
                                 gridy = GridBagConstraints.RELATIVE;
                                 insets = new Insets( 0, 0, 10, 0 );
                             }}
        );

        // all of the collection boxes themselves
        layoutNode.addChild( collectionAreaHolder,
                             new GridBagConstraints() {{
                                 gridx = 0;
                                 gridy = GridBagConstraints.RELATIVE;
                                 insets = new Insets( 0, 0, 15, 0 );
                             }} );

        // sound on/off
        layoutNode.addChild( new SoundOnOffNode(), new GridBagConstraints() {{
            gridx = 0;
            gridy = GridBagConstraints.RELATIVE;
            insets = new Insets( 0, 0, 0, 0 );
        }} );


        // add our two layers: background and controls
        addChild( backgroundHolder );
        addChild( layoutNode );

        // anonymous function here, so we don't create a bunch of fields
        final VoidFunction1<KitCollection> createCollectionNode = new VoidFunction1<KitCollection>() {
            public void apply( KitCollection collection ) {
                collectionAreaMap.put( collection, new CollectionAreaNode( parentFrame, canvas, collection, singleCollectionMode ) );
            }
        };

        // create nodes for all current collections
        for ( KitCollection collection : collectionList.getCollections() ) {
            createCollectionNode.apply( collection );
        }

        // if a new collection is added, create one for it
        collectionList.addListener( new Adapter() {
            public void addedCollection( KitCollection collection ) {
                createCollectionNode.apply( collection );
            }
        } );

        // use the current collection
        useCollection( collectionList.currentCollection.get() );

        collectionList.currentCollection.addObserver( new ChangeObserver<KitCollection>() {
            public void update( KitCollection newCollection, KitCollection oldCollection ) {
                useCollection( newCollection );
            }
        } );
    }

    public void useCollection( KitCollection collection ) {
        // swap out the inner collection area
        collectionAreaHolder.removeAllChildren();
        final CollectionAreaNode collectionAreaNode = collectionAreaMap.get( collection );
        collectionAreaHolder.addChild( collectionAreaNode );

        // if we are hooked up, update the box locations. otherwise, listen to the canvas for when it is
        if ( getParent() != null ) {
            collectionAreaNode.updateCollectionBoxLocations();
        }
        else {
            // we need to listen for this because the update needs to use canvas' global/local/view coordinate transformations
            canvas.addCollectionAttachmentListener( new SimpleObserver() {
                public void update() {
                    collectionAreaNode.updateCollectionBoxLocations();
                }
            } );
        }

        /*---------------------------------------------------------------------------*
        * draw new background
        *----------------------------------------------------------------------------*/
        backgroundHolder.removeAllChildren();
        PPath background = PPath.createRectangle( 0, 0, (float) getPlacementWidth(), (float) getPlacementHeight() );
        background.setPaint( BuildAMoleculeConstants.MOLECULE_COLLECTION_BACKGROUND );
        background.setStrokePaint( BuildAMoleculeConstants.MOLECULE_COLLECTION_BORDER );
        backgroundHolder.addChild( background );
    }

    private double getPlacementWidth() {
        return layoutNode.getContainer().getPreferredSize().getWidth() + CONTAINER_PADDING * 2;
    }

    private double getPlacementHeight() {
        return layoutNode.getContainer().getPreferredSize().getHeight() + CONTAINER_PADDING * 2;
    }
}
