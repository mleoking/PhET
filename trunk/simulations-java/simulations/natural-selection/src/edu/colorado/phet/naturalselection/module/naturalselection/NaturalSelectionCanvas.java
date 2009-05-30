/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.module.naturalselection;

import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.defaults.NaturalSelectionDefaults;
import edu.colorado.phet.naturalselection.model.Gene;
import edu.colorado.phet.naturalselection.view.*;
import edu.umd.cs.piccolo.PNode;

/**
 * Holds the main simulation area's canvas.
 *
 * @author Jonathan Olson
 */
public class NaturalSelectionCanvas extends PhetPCanvas {

    /**
     * The pixel-level from the top of the "horizon", where the 3d bunny positions would appear if inifinitely far away
     */
    public static final double HORIZON = 120.0;

    private NaturalSelectionModel model;

    private PNode rootNode;

    /**
     * Holds the piccolo node that has all of the sprites (bunnies, trees, shrubs, wolves, etc.)
     */
    public SpriteHandler bunnies;

    /**
     * The background node (background images, part of the environment)
     */
    public NaturalSelectionBackgroundNode backgroundNode;

    private MutationPendingNode mutationPendingNode = null;

    private AddFriendNode addFriendNode;

    private FrenzyNode frenzyNode;

    /**
     * Constructor
     *
     * @param model The natural selection model
     */
    public NaturalSelectionCanvas( NaturalSelectionModel model ) {

        super( NaturalSelectionDefaults.VIEW_SIZE );

        setWorldTransformStrategy( new ConstantTransformStrategy( new AffineTransform() ) );

        this.model = model;

        setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        setBorder( null );

        rootNode = new PNode();
        addWorldChild( rootNode );

        backgroundNode = new NaturalSelectionBackgroundNode( this.model.getClimate() );
        rootNode.addChild( backgroundNode );

        bunnies = new SpriteHandler();
        rootNode.addChild( bunnies );

        addFriendNode = new AddFriendNode( model );
        addFriendNode.setOffset( 75, 250 );
        rootNode.addChild( addFriendNode );

    }

    public void reset() {
        bunnies.reset();
        backgroundNode.reset();
        addFriendNode.setVisible( true );

        if ( mutationPendingNode != null ) {
            rootNode.removeChild( mutationPendingNode );
        }
        mutationPendingNode = null;
    }

    protected void updateLayout() {

        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() <= 0 || worldSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }
        else if ( NaturalSelectionConstants.DEBUG_CANVAS_UPDATE_LAYOUT ) {
            System.out.println( "NaturalSelectionCanvas.updateLayout worldSize=" + worldSize );//XXX
        }

        // layout everything

        backgroundNode.updateLayout( getWidth(), getHeight() );

        bunnies.setSpriteTransform( backgroundNode.getBackgroundTransform( getWidth(), getHeight() ) );

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
}
