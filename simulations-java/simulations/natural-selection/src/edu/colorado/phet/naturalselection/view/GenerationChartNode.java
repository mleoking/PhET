/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.view;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.defaults.NaturalSelectionDefaults;
import edu.colorado.phet.naturalselection.model.Bunny;
import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModel;
import edu.umd.cs.piccolo.PNode;

/**
 * Displays a generation chart where each generation is separated out in a compact way (maximizes overall bunny size
 * compared to the heredity version)
 * <p/>
 * Only shows the latest generations (up to and including the last generation to die of old age)
 *
 * @author Jonathan Olson
 */
public class GenerationChartNode extends PNode implements NaturalSelectionModel.NaturalSelectionModelListener {
    // TODO: if keeping, significantly polish the visual display and add generation numbers

    // model reference
    private NaturalSelectionModel model;

    // list of the generations to show (piccolo nodes)
    private LinkedList generations;

    // initial Y offset for where the generations should start
    private static final double INITIAL_Y_OFFSET = 0;

    // offsets
    private double xoffset = 0;
    private double yoffset = INITIAL_Y_OFFSET;

    /**
     * Constructor
     *
     * @param model The natural selection model
     */
    public GenerationChartNode( NaturalSelectionModel model ) {
        this.model = model;

        generations = new LinkedList();

        // display only the latest generations
        int minGeneration = 0;
        int maxGeneration = model.getGeneration();
        if ( maxGeneration - minGeneration > NaturalSelectionConstants.BUNNIES_DIE_WHEN_THEY_ARE_THIS_OLD ) {
            minGeneration = maxGeneration - NaturalSelectionConstants.BUNNIES_DIE_WHEN_THEY_ARE_THIS_OLD;
        }

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
     * Append a generation visual onto the bottom
     *
     * @param generation The generation number to fetch and display
     */
    public void addGeneration( int generation ) {
        // get the bunnies from that generation
        ArrayList bunnies = model.getBunnyGenerationList( generation );

        int size = bunnies.size();

        xoffset = 0;

        PNode genNode = new PNode();

        genNode.setOffset( 0, yoffset );

        double localyoffset = 0;

        addChild( genNode );

        // for each bunny, create a GenerationBunnyNode and lay it out
        for ( int idx = 0; idx < size; idx++ ) {
            Bunny bunny = (Bunny) bunnies.get( idx );

            GenerationBunnyNode bunnyNode = new GenerationBunnyNode( bunny );


            bunnyNode.scale( 0.1 );

            // if we run out of room on the right, wrap it to the next line
            if ( xoffset + 25 > NaturalSelectionDefaults.GENERATION_CHART_SIZE.getWidth() ) {
                xoffset = 0;

                yoffset += 30;
                localyoffset += 30;
            }

            bunnyNode.setOffset( xoffset, localyoffset );

            genNode.addChild( bunnyNode );

            xoffset += 25;
        }

        generations.add( genNode );

        // add a y offset that is larger to signify a change in generation
        yoffset += 45;
    }

    /**
     * Gets rid of the oldest generation, and moves the others up
     */
    public void popGeneration() {
        PNode oldGen = (PNode) generations.get( 0 );

        generations.remove( 0 );

        removeChild( oldGen );

        // TODO: run cleanup on all of oldGen's child nodes (for memory purposes)

        // amount of space to move the newer generations up by.
        // TODO: include INITIAL_Y_OFFSET if actually continuing on this
        double space = ( (PNode) generations.get( 0 ) ).getOffset().getY();

        System.out.println( "Space: " + space );

        yoffset -= space;

        Iterator iter = generations.iterator();

        // move other generations up
        while ( iter.hasNext() ) {
            PNode gen = (PNode) iter.next();
            Point2D offset = gen.getOffset();
            gen.setOffset( offset.getX(), offset.getY() - space );
        }
    }

    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------

    public void onMonthChange( String monthName ) {

    }

    public void onGenerationChange( int generation ) {
        // if we have filled it (essentially), then remove the oldest generation
        if ( generation > NaturalSelectionConstants.BUNNIES_DIE_WHEN_THEY_ARE_THIS_OLD ) {
            popGeneration();
        }

        // add the newest generation
        addGeneration( generation );
    }

    public void onNewBunny( Bunny bunny ) {

    }

    public void onClimateChange( int climate ) {

    }

    public void onSelectionFactorChange( int selectionFactor ) {

    }
}