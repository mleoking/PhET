/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.view.pedigree;

import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.model.NaturalSelectionModel;
import edu.umd.cs.piccolo.PNode;

/**
 * Shows generations of bunnies in the classical hereditary format with a "family tree".
 *
 * @author Jonathan Olson
 */
public class PedigreeNode extends PNode implements NaturalSelectionModel.Listener {

    // model reference
    private NaturalSelectionModel model;

    // GenerationNode in order of display
    private LinkedList<GenerationNode> generations;

    // vertical space between generations
    private static final double GENERATION_VERTICAL_SPACER = 15.0;

    // initial offset at the top
    private static final double INITIAL_Y_OFFSET = 0;

    // incremented offset for positioning generations
    private double yoffset = INITIAL_Y_OFFSET;

    public List<Listener> listeners;

    /**
     * Constructor
     *
     * @param model The natural selection model
     */
    public PedigreeNode( NaturalSelectionModel model ) {
        this.model = model;

        listeners = new LinkedList<Listener>();

        generations = new LinkedList<GenerationNode>();

        int minGeneration = 0;
        int maxGeneration = model.getGeneration();
        if ( maxGeneration - minGeneration > NaturalSelectionConstants.getSettings().getBunniesDieWhenTheyAreThisOld() ) {
            minGeneration = maxGeneration - NaturalSelectionConstants.getSettings().getBunniesDieWhenTheyAreThisOld();
        }

        // add only the latest generations if we have at least 2 dead generations

        for ( int i = minGeneration; i <= maxGeneration; i++ ) {
            addGeneration( i );
        }

        model.addListener( this );
    }

    public void reset() {
        Iterator<GenerationNode> iter = generations.iterator();

        while ( iter.hasNext() ) {
            removeChild( iter.next() );
        }

        generations = new LinkedList<GenerationNode>();

        yoffset = INITIAL_Y_OFFSET;

        addGeneration( 0 );
    }

    /**
     * Add a generation onto the bottom
     *
     * @param genNumber The generation number to fetch and display
     */
    private void addGeneration( int genNumber ) {
        // create a GenerationNode display node
        GenerationNode gen = new GenerationNode( model, this, genNumber );

        // position it at the y offset
        gen.setOffset( 0, yoffset );

        // add its height + the spacer to the y offset
        yoffset += gen.getGenerationHeight() + GENERATION_VERTICAL_SPACER;

        // visually add it
        addChild( gen );

        // if there is a generation above, draw the lines on it to show the genetic relationships
        if ( generations.size() != 0 ) {
            ( generations.getLast() ).drawChildLines( gen );
        }

        // add this to the end of the generations list
        generations.add( gen );

        notifyGenerationAdded();
    }

    /**
     * Gets rid of the oldest generation (visually), and moves the rest up vertically
     */
    public void popGeneration() {
        PNode oldGen = generations.get( 0 );

        generations.remove( 0 );

        removeChild( oldGen );

        // TODO: run cleanup on all of oldGen's child nodes (for memory purposes)

        // the space to move the newer generations up
        double space = ( generations.get( 0 ) ).getOffset().getY();

        //System.out.println( "Space: " + space );

        yoffset -= space;

        Iterator<GenerationNode> iter = generations.iterator();

        // move all of the newer generations up
        while ( iter.hasNext() ) {
            PNode gen = iter.next();
            Point2D offset = gen.getOffset();
            gen.setOffset( offset.getX(), offset.getY() - space );
        }
    }

    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------

    public void onEvent( NaturalSelectionModel.Event event ) {
        if ( event.getType() == NaturalSelectionModel.Event.TYPE_NEW_GENERATION ) {
            //int generation = event.getModel().getGeneration();
            int generation = event.getNewGeneration();
            if ( generation == 0 ) {
                // we need to reset this again, since the model has changed!
                reset();
                return;
            }

            if ( generation > NaturalSelectionConstants.getSettings().getBunniesDieWhenTheyAreThisOld() ) {
                popGeneration();
            }
            addGeneration( generation );
        }
        if ( event.getType() == NaturalSelectionModel.Event.TYPE_NEW_BUNNY ) {
            if ( event.getNewBunny() == model.getRootMother() ) {
                reset();
            }
        }
    }


    public void notifyGenerationAdded() {
        for ( Listener listener : listeners ) {
            listener.onGenerationAdded();
        }
    }


    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    public static interface Listener {
        public void onGenerationAdded();
    }

}
