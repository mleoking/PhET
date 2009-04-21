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

public class GenerationChartNode extends PNode implements NaturalSelectionModel.NaturalSelectionModelListener {

    private NaturalSelectionModel model;

    private LinkedList generations;

    private static final double INITIAL_Y_OFFSET = 0;

    private double xoffset = 0;
    private double yoffset = INITIAL_Y_OFFSET;

    public GenerationChartNode( NaturalSelectionModel model ) {
        this.model = model;

        generations = new LinkedList();

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

    public void addGeneration( int generation ) {
        ArrayList bunnies = model.getBunnyGenerationList( generation );

        int size = bunnies.size();

        xoffset = 0;

        PNode genNode = new PNode();

        genNode.setOffset( 0, yoffset );

        double localyoffset = 0;

        addChild( genNode );

        for ( int idx = 0; idx < size; idx++ ) {
            Bunny bunny = (Bunny) bunnies.get( idx );

            GenerationBunnyNode bunnyNode = new GenerationBunnyNode( bunny );


            bunnyNode.scale( 0.1 );

            if ( xoffset + 25 > NaturalSelectionDefaults.chartSize.getWidth() ) {
                xoffset = 0;

                yoffset += 30;
                localyoffset += 30;
            }

            bunnyNode.setOffset( xoffset, localyoffset );

            genNode.addChild( bunnyNode );

            xoffset += 25;
        }

        generations.add( genNode );

        yoffset += 45;
    }

    public void popGeneration() {
        PNode oldGen = (PNode) generations.get( 0 );

        generations.remove( 0 );

        removeChild( oldGen );

        // TODO: run cleanup on all of oldGen's child nodes (for memory purposes)

        double space = ( (PNode) generations.get( 0 ) ).getOffset().getY();

        System.out.println( "Space: " + space );

        yoffset -= space;

        Iterator iter = generations.iterator();

        while ( iter.hasNext() ) {
            PNode gen = (PNode) iter.next();
            Point2D offset = gen.getOffset();
            gen.setOffset( offset.getX(), offset.getY() - space );
        }
    }

    public void onMonthChange( String monthName ) {

    }

    public void onGenerationChange( int generation ) {
        if ( generation > NaturalSelectionConstants.BUNNIES_DIE_WHEN_THEY_ARE_THIS_OLD ) {
            popGeneration();
        }
        addGeneration( generation );
    }

    public void onNewBunny( Bunny bunny ) {

    }

    public void onClimateChange( int climate ) {

    }

    public void onSelectionFactorChange( int selectionFactor ) {

    }
}