package edu.colorado.phet.buildamolecule.control;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;

import edu.colorado.phet.buildamolecule.BuildAMoleculeConstants;
import edu.colorado.phet.buildamolecule.model.CollectionBox;
import edu.colorado.phet.buildamolecule.model.MoleculeStructure;
import edu.colorado.phet.buildamolecule.view.BuildAMoleculeCanvas;
import edu.colorado.phet.buildamolecule.view.view3d.JmolDialogProperty;
import edu.colorado.phet.buildamolecule.view.view3d.ShowMolecule3DButtonNode;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.swing.SwingLayoutNode;

/**
 * Represents a generic collection box node which is decorated by additional header nodes (probably text describing what can be put in, what is in it,
 * etc.)
 */
public class CollectionBoxNode extends SwingLayoutNode {

    private final CollectionBox box;
    private final PNode boxNode = new PNode();
    private final PhetPPath blackBox;
    private final PNode moleculeLayer = new PNode();
    private final List<PNode> moleculeNodes = new LinkedList<PNode>();

    // show 3d elements
    private final JmolDialogProperty dialog = new JmolDialogProperty();
    private final PNode show3dButton;

    // stores nodes for each molecule
    private final Map<MoleculeStructure, PNode> moleculeNodeMap = new HashMap<MoleculeStructure, PNode>();

    private static final double MOLECULE_PADDING = 5;
    private Timer blinkTimer = null;

    private GridBagConstraints headerConstraints = new GridBagConstraints() {{
        gridx = 0;
        gridy = 0;
    }};

    public CollectionBoxNode( final Frame parentFrame, final BuildAMoleculeCanvas canvas, final CollectionBox box, final int headerQuantity ) {
        super( new GridBagLayout() );
        this.box = box;

        // grid bag layout and SwingLayoutNode for easier horizontal and vertical layout
        GridBagConstraints c = new GridBagConstraints() {{
            gridx = 0;
            gridy = headerQuantity;
        }};

        c.insets = new Insets( 3, 0, 0, 0 ); // some padding between the black box

        blackBox = new PhetPPath( new Rectangle2D.Double( 0, 0, 160, 50 ), BuildAMoleculeConstants.MOLECULE_COLLECTION_BOX_BACKGROUND ) {{
            canvas.addFullyLayedOutObserver( new SimpleObserver() {
                public void update() {
                    // we need to pass the collection box model coordinates, but here we have relative piccolo coordinates
                    // this requires getting local => global => view => model coordinates

                    // our bounds relative to the root Piccolo canvas
                    Rectangle2D globalBounds = getParent().localToGlobal( getFullBounds() );

                    // pull out the upper-left corner and dimension so we can transform them
                    Point2D upperLeftCorner = new Point2D.Double( globalBounds.getX(), globalBounds.getY() );
                    PDimension dimensions = new PDimension( globalBounds.getWidth(), globalBounds.getHeight() );

                    // transform the point and dimensions to world coordinates
                    canvas.getPhetRootNode().globalToWorld( upperLeftCorner );
                    canvas.getPhetRootNode().globalToWorld( dimensions );

                    // our bounds relative to our simulation (BAM) canvas. Will be filled in
                    Rectangle2D viewBounds = new Rectangle2D.Double( upperLeftCorner.getX(), upperLeftCorner.getY(), dimensions.getWidth(), dimensions.getHeight() );

                    // pass it the model bounds
                    box.setDropBounds( canvas.getModelViewTransform().viewToModel( viewBounds ).getBounds2D() );
                }
            } );
        }};
        boxNode.addChild( blackBox );
        boxNode.addChild( moleculeLayer );
        addChild( boxNode, c );

        // create our show 3D button, and have it change visibility based on the box quantity
        show3dButton = new ShowMolecule3DButtonNode( parentFrame, dialog, box.getMoleculeType() ) {{
//            box.addListener( new Adapter() {
//                {
//                    // update initial visibility
//                    updateVisibility();
//                }
//
//                private void updateVisibility() {
//                    setVisible( box.quantity.get() > 0 );
//                }
//
//                @Override public void onAddedMolecule( MoleculeStructure moleculeStructure ) {
//                    updateVisibility();
//                }
//
//                @Override public void onRemovedMolecule( MoleculeStructure moleculeStructure ) {
//                    updateVisibility();
//                }
//            } );
        }};

        updateBoxGraphics();

        box.addListener( new CollectionBox.Listener() {
            public void onAddedMolecule( MoleculeStructure moleculeStructure ) {
                addMolecule( moleculeStructure );
            }

            public void onRemovedMolecule( MoleculeStructure moleculeStructure ) {
                removeMolecule( moleculeStructure );
            }

            public void onAcceptedMoleculeCreation( MoleculeStructure moleculeStructure ) {
                blink();
            }
        } );
    }

    protected PNode getShow3dButton() {
        return show3dButton;
    }

    protected void addHeaderNode( PNode headerNode ) {
        addChild( headerNode, headerConstraints );
        headerConstraints.gridy += 1;
    }

    private void addMolecule( MoleculeStructure moleculeStructure ) {
        cancelBlinksInProgress();
        updateBoxGraphics();

        PNode pseudo3DNode = moleculeStructure.getMatchingCompleteMolecule().createPseudo3DNode();
        //pseudo3DNode.setOffset( moleculeNodes.size() * ( pseudo3DNode.getFullBounds().getWidth() + MOLECULE_PADDING ) - pseudo3DNode.getFullBounds().getX(), 0 ); // add it to the right
        moleculeLayer.addChild( pseudo3DNode );
        moleculeNodes.add( pseudo3DNode );
        moleculeNodeMap.put( moleculeStructure, pseudo3DNode );

        updateMoleculeLayout();

        centerMoleculesInBlackBox();
    }

    private void removeMolecule( MoleculeStructure moleculeStructure ) {
        cancelBlinksInProgress();
        updateBoxGraphics();

        PNode lastMoleculeNode = moleculeNodeMap.get( moleculeStructure );
        moleculeLayer.removeChild( lastMoleculeNode );
        moleculeNodes.remove( lastMoleculeNode );
        moleculeNodeMap.remove( moleculeStructure );

        updateMoleculeLayout();

        if ( box.quantity.get() > 0 ) {
            centerMoleculesInBlackBox();
        }
    }

    /**
     * Layout of molecules. Spaced horizontally with MOLECULE_PADDING, and vertically centered
     */
    private void updateMoleculeLayout() {
        double maxHeight = 0;
        for ( PNode moleculeNode : moleculeNodes ) {
            maxHeight = Math.max( maxHeight, moleculeNode.getFullBounds().getHeight() );
        }
        double x = 0;
        for ( PNode moleculeNode : moleculeNodes ) {
            moleculeNode.setOffset( x, ( maxHeight - moleculeNode.getFullBounds().getHeight() ) / 2 );
            x += moleculeNode.getFullBounds().getWidth() + MOLECULE_PADDING;
        }
    }

    /**
     * Sets up a blinking box to register that a molecule was created that can go into a box
     */
    private void blink() {
        double blinkLengthInSeconds = 1.3;

        // our delay between states
        int blinkDelayInMs = 100;

        // properties that we will use over time in our blinker
        final Property<Boolean> on = new Property<Boolean>( false ); // on/off state
        final Property<Integer> counts = new Property<Integer>( (int) ( blinkLengthInSeconds * 1000 / blinkDelayInMs ) ); // decrements to zero to stop the blinker

        cancelBlinksInProgress();

        blinkTimer = new Timer();
        blinkTimer.schedule( new TimerTask() {
                                 @Override
                                 public void run() {
                                     // decrement and check
                                     counts.set( counts.get() - 1 );
                                     assert ( counts.get() >= 0 );

                                     if ( counts.get() == 0 ) {
                                         // set up our normal graphics (border/background)
                                         updateBoxGraphics();

                                         // make sure we don't get called again
                                         blinkTimer.cancel();
                                         blinkTimer = null;
                                     }
                                     else {
                                         // toggle state
                                         on.set( !on.get() );

                                         // draw graphics
                                         if ( on.get() ) {
                                             blackBox.setPaint( BuildAMoleculeConstants.MOLECULE_COLLECTION_BOX_BACKGROUND_BLINK );
                                             blackBox.setStrokePaint( BuildAMoleculeConstants.MOLECULE_COLLECTION_BOX_BORDER_BLINK );
                                         }
                                         else {
                                             blackBox.setPaint( BuildAMoleculeConstants.MOLECULE_COLLECTION_BOX_BACKGROUND );
                                             blackBox.setStrokePaint( BuildAMoleculeConstants.MOLECULE_COLLECTION_BACKGROUND );
                                         }

                                         // make sure this paint happens immediately
                                         blackBox.repaint();
                                     }
                                 }
                             }, 0, blinkDelayInMs );
    }

    private void cancelBlinksInProgress() {
        // stop any previous blinking from happening. don't want double-blinking
        if ( blinkTimer != null ) {
            blinkTimer.cancel();
            blinkTimer = null;
        }
    }

    private void centerMoleculesInBlackBox() {
        PBounds blackBoxFullBounds = blackBox.getFullBounds();

        // for now, we scale the molecules up and down depending on their size
        moleculeLayer.setScale( 1 );
        double xScale = ( blackBoxFullBounds.getWidth() - 25 ) / moleculeLayer.getFullBounds().getWidth();
        double yScale = ( blackBoxFullBounds.getHeight() - 25 ) / moleculeLayer.getFullBounds().getHeight();
        moleculeLayer.setScale( Math.min( xScale, yScale ) );

        moleculeLayer.centerFullBoundsOnPoint(
                blackBoxFullBounds.getCenterX() - blackBoxFullBounds.getX(),
                blackBoxFullBounds.getCenterY() - blackBoxFullBounds.getY() );
    }

    private void updateBoxGraphics() {
        blackBox.setStroke( new BasicStroke( 4 ) );
        if ( box.isFull() ) {
            blackBox.setStrokePaint( BuildAMoleculeConstants.MOLECULE_COLLECTION_BOX_HIGHLIGHT );
        }
        else {
            blackBox.setStrokePaint( BuildAMoleculeConstants.MOLECULE_COLLECTION_BACKGROUND );
        }
    }
}
