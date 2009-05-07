/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.view.pedigree;

import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.LinkedList;

import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.defaults.NaturalSelectionDefaults;
import edu.colorado.phet.naturalselection.model.Bunny;
import edu.colorado.phet.naturalselection.model.Frenzy;
import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModel;
import edu.umd.cs.piccolo.PNode;

// TODO: create GenerationLayoutNode as superclass of this and GnerationChartNode, for shared behavior

/**
 * Shows generations of bunnies in the classical hereditary format with a "family tree".
 *
 * @author Jonathan Olson
 */
public class PedigreeNode extends PNode implements NaturalSelectionModel.NaturalSelectionModelListener {

    // model reference
    private NaturalSelectionModel model;

    // GenerationNode in order of display
    private LinkedList generations;

    // available horizontal width in the canvas area
    public double AVAILABLE_WIDTH = NaturalSelectionDefaults.GENERATION_CHART_SIZE.getWidth();

    // vertical space between generations
    private static final double GENERATION_VERTICAL_SPACER = 15.0;

    // initial offset at the top
    private static final double INITIAL_Y_OFFSET = 0;

    // incremented offset for positioning generations
    private double yoffset = INITIAL_Y_OFFSET;

    /**
     * Constructor
     *
     * @param model The natural selection model
     */
    public PedigreeNode( NaturalSelectionModel model ) {
        this.model = model;

        generations = new LinkedList();

        int minGeneration = 0;
        int maxGeneration = model.getGeneration();
        if ( maxGeneration - minGeneration > NaturalSelectionConstants.BUNNIES_DIE_WHEN_THEY_ARE_THIS_OLD ) {
            minGeneration = maxGeneration - NaturalSelectionConstants.BUNNIES_DIE_WHEN_THEY_ARE_THIS_OLD;
        }

        // add only the latest generations if we have at least 2 dead generations

        for ( int i = minGeneration; i <= maxGeneration; i++ ) {
            addGeneration( i );
        }

        model.addListener( this );
    }

    public void reset() {
        Iterator iter = generations.iterator();

        while ( iter.hasNext() ) {
            removeChild( (PNode) iter.next() );
        }

        generations = new LinkedList();

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
            ( (GenerationNode) generations.getLast() ).drawChildLines( gen );
        }

        // add this to the end of the generations list
        generations.add( gen );
    }

    /**
     * Gets rid of the oldest generation (visually), and moves the rest up vertically
     */
    public void popGeneration() {
        PNode oldGen = (PNode) generations.get( 0 );

        generations.remove( 0 );

        removeChild( oldGen );

        // TODO: run cleanup on all of oldGen's child nodes (for memory purposes)

        // the space to move the newer generations up
        double space = ( (PNode) generations.get( 0 ) ).getOffset().getY();

        //System.out.println( "Space: " + space );

        yoffset -= space;

        Iterator iter = generations.iterator();

        // move all of the newer generations up
        while ( iter.hasNext() ) {
            PNode gen = (PNode) iter.next();
            Point2D offset = gen.getOffset();
            gen.setOffset( offset.getX(), offset.getY() - space );
        }
    }

    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------

    public void onGenerationChange( int generation ) {
        if ( generation == 0 ) {
            // we need to reset this again, since the model has changed!
            reset();
            return;
        }

        if ( generation > NaturalSelectionConstants.BUNNIES_DIE_WHEN_THEY_ARE_THIS_OLD ) {
            popGeneration();
        }
        addGeneration( generation );
    }

    public void onNewBunny( Bunny bunny ) {
        if ( bunny == model.getRootMother() ) {
            reset();
        }
    }

    public void onClimateChange( int climate ) {

    }

    public void onSelectionFactorChange( int selectionFactor ) {

    }

    public void onFrenzyStart( Frenzy frenzy ) {

    }

}
