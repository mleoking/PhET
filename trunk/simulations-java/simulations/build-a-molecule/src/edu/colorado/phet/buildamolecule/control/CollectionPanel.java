//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.control;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import edu.colorado.phet.buildamolecule.BuildAMoleculeConstants;
import edu.colorado.phet.buildamolecule.BuildAMoleculeStrings;
import edu.colorado.phet.buildamolecule.model.*;
import edu.colorado.phet.buildamolecule.model.CollectionList.Adapter;
import edu.colorado.phet.buildamolecule.model.CollectionList.Listener;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.swing.SwingLayoutNode;

import static edu.colorado.phet.buildamolecule.BuildAMoleculeConstants.STAGE_SIZE;
import static edu.colorado.phet.buildamolecule.BuildAMoleculeConstants.VIEW_PADDING;

/**
 * A panel that shows collection areas for different collections, and allows switching between those collections
 */
public class CollectionPanel extends PNode {

    public static final int CONTAINER_PADDING = 15;
    SwingLayoutNode layoutNode = new SwingLayoutNode( new GridBagLayout() );
    private final PNode collectionAreaHolder = new PNode();
    private final PNode backgroundHolder = new PNode();

    private final Map<KitCollection, CollectionAreaNode> collectionAreaMap = new HashMap<KitCollection, CollectionAreaNode>();
    private VoidFunction1<SimpleObserver> addCollectionAttachmentListener;

    /**
     * Constructs a collection area panel
     *
     * @param collectionList       List of collections to handle (mutable)
     * @param singleCollectionMode Whether we use single or multiple style collection boxes
     * @param addCollectionAttachmentListener
     *                             Function to add an attachment listener
     * @param toModelBounds        Function to compute model coordinates from a PNode in the view
     */
    public CollectionPanel( final CollectionList collectionList, final boolean singleCollectionMode, VoidFunction1<SimpleObserver> addCollectionAttachmentListener, final Function1<PNode, Rectangle2D> toModelBounds ) {
        this.addCollectionAttachmentListener = addCollectionAttachmentListener;

        // move it over so the background will have padding
        layoutNode.translate( CONTAINER_PADDING, CONTAINER_PADDING );

        // "Your Molecule Collection"
        layoutNode.addChild( new HTMLNode( BuildAMoleculeStrings.COLLECTION_AREA_YOUR_MOLECULE_COLLECTION ) {{
                                 setFont( new PhetFont( 22 ) );
                             }},
                             new GridBagConstraints() {{
                                 gridx = 0;
                                 gridy = 0;
                                 insets = new Insets( 0, 0, 5, 0 );
                             }}
        );

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
                                 insets = new Insets( 0, 0, 5, 0 );
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
                collectionAreaMap.put( collection, new CollectionAreaNode( collection, singleCollectionMode, toModelBounds ) );
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
            addCollectionAttachmentListener.apply( new SimpleObserver() {
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

    /**
     * Used to get the panel width so that we can construct the model (and thus kit) beforehand
     *
     * @param singleCollectionMode Whether we are on single (1st tab) or multiple (2nd tab) mode
     * @return Width of the entire collection panel
     */
    public static double getCollectionPanelModelWidth( boolean singleCollectionMode ) {
        // construct a dummy collection panel and check its width
        CollectionPanel collectionPanel = new CollectionPanel( new CollectionList( new KitCollection() {{
            addCollectionBox( new CollectionBox( MoleculeList.H2O, 1 ) ); // collection box so it gets the width correctly
        }}, new LayoutBounds( false, 0 ) ), singleCollectionMode, new VoidFunction1<SimpleObserver>() {  // TODO: why do we need the bounds here? simplify.
            public void apply( SimpleObserver simpleObserver ) {
            }
        }, new Function1<PNode, Rectangle2D>() {
            public Rectangle2D apply( PNode pNode ) {
                return null;
            }
        }
        );
        return BuildAMoleculeConstants.MODEL_VIEW_TRANSFORM.viewToModelDeltaX( collectionPanel.getFullBounds().getWidth() );
    }

    private double getPlacementWidth() {
        return layoutNode.getContainer().getPreferredSize().getWidth() + CONTAINER_PADDING * 2;
    }

    private double getPlacementHeight() {
        // how much height we need with proper padding
        double requiredHeight = layoutNode.getContainer().getPreferredSize().getHeight() + CONTAINER_PADDING * 2;

        // how much height we will take up to fit our vertical size perfectly
        double fixedHeight = STAGE_SIZE.getHeight() - VIEW_PADDING * 2; // we will have padding above and below

        if ( requiredHeight > fixedHeight ) {
            System.out.println( "Warning: collection panel is too tall. required: " + requiredHeight + ", but has: " + fixedHeight );
        }

        return fixedHeight;
    }
}
