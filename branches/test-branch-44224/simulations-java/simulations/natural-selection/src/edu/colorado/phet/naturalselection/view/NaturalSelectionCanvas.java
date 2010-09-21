package edu.colorado.phet.naturalselection.view;

import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.defaults.NaturalSelectionDefaults;
import edu.colorado.phet.naturalselection.model.Gene;
import edu.colorado.phet.naturalselection.model.NaturalSelectionModel;
import edu.colorado.phet.naturalselection.persistence.NaturalSelectionConfig;
import edu.umd.cs.piccolo.PNode;

/**
 * Holds the main simulation area's canvas.
 *
 * @author Jonathan Olson
 */
public class NaturalSelectionCanvas extends PhetPCanvas {

    private NaturalSelectionModel model;

    private PNode rootNode;

    /**
     * Holds the piccolo node that has all of the sprites (bunnies, trees, shrubs, wolves, etc.)
     */
    public LandscapeNode landscapeNode;

    /**
     * The background node (background images, part of the environment)
     */
    public NaturalSelectionBackgroundNode backgroundNode;

    private MutationPendingNode mutationPendingNode = null;

    /**
     * Holds the "Add Friend" button that is displayed at the start of the simulation
     */
    private AddFriendNode addFriendNode;

    /**
     * Constructor
     *
     * @param model The natural selection model
     */
    public NaturalSelectionCanvas( NaturalSelectionModel model ) {

        super( NaturalSelectionDefaults.VIEW_SIZE );

        setWorldTransformStrategy( new ConstantTransformStrategy( new AffineTransform() ) );

        this.model = model;

        setBackground( NaturalSelectionConstants.COLOR_ACCESSIBLE_CONTROL_PANEL );

        rootNode = new PNode();
        addWorldChild( rootNode );

        backgroundNode = new NaturalSelectionBackgroundNode( this.model.getClimate() );
        rootNode.addChild( backgroundNode );

        landscapeNode = new LandscapeNode( model, model.getLandscape() );
        rootNode.addChild( landscapeNode );

        addFriendNode = new AddFriendNode( model );
        addFriendNode.setOffset( 75, 250 );
        rootNode.addChild( addFriendNode );
    }

    /**
     * Resets the play area
     */
    public void reset() {
        landscapeNode.reset();
        backgroundNode.reset();
        addFriendNode.setVisible( true );

        if ( mutationPendingNode != null ) {
            rootNode.removeChild( mutationPendingNode );
        }
        mutationPendingNode = null;
    }

    /**
     * Called when we are resized. This updates all of the play area layout
     */
    protected void updateLayout() {
        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() <= 0 || worldSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }

        // layout everything
        backgroundNode.updateLayout( getWidth(), getHeight() );
        landscapeNode.setSpriteTransform( backgroundNode.getBackgroundTransform( getWidth(), getHeight() ) );
        landscapeNode.updateLayout( getWidth(), getHeight() );
        addFriendNode.updateLayout( getWidth(), getHeight() );

        positionMutationPending();
    }


    public void handleMutationChange( Gene gene, boolean mutatable ) {
        if ( mutatable ) {
            if ( mutationPendingNode != null ) {
                throw new RuntimeException( "mutationPendingNode should be null!!!" );
            }
            mutationPendingNode = new MutationPendingNode( gene );
            positionMutationPending();
            rootNode.addChild( mutationPendingNode );
        }
        else {
            if ( mutationPendingNode != null ) {
                rootNode.removeChild( mutationPendingNode );
                mutationPendingNode = null;
            }
        }
    }

    private void positionMutationPending() {
        if ( mutationPendingNode == null ) {
            return;
        }
        mutationPendingNode.setOffset( ( getWidth() - mutationPendingNode.getPlacementWidth() ) / 2, getHeight() - mutationPendingNode.getPlacementHeight() - 10 );
    }

    public void load( NaturalSelectionConfig config ) {
        addFriendNode.setVisible( !config.isFriendAdded() );
    }

    public LandscapeNode getLandscapeNode() {
        return landscapeNode;
    }
}
