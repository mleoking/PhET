/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.control;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.defaults.NaturalSelectionDefaults;
import edu.colorado.phet.naturalselection.model.*;
import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModel;
import edu.colorado.phet.naturalselection.view.GenerationCountNode;
import edu.colorado.phet.naturalselection.view.MutationPendingNode;
import edu.colorado.phet.naturalselection.view.PedigreeNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * The piccolo canvas where the generation charts are drawn in. Allows changing between the charts
 *
 * @author Jonathan Olson
 */
public class GenerationChartCanvas extends PhetPCanvas {

    private NaturalSelectionModel model;
    private PedigreeNode pedigreeNode;

    private MutationPendingNode mutationPendingNode;
    private PNode rootNode;

    /**
     * Constructor
     *
     * @param model The natural selection model
     */
    public GenerationChartCanvas( NaturalSelectionModel model ) {
        // TODO: allow the generation chart to change size
        super( NaturalSelectionDefaults.GENERATION_CHART_SIZE );
        setPreferredSize( NaturalSelectionDefaults.GENERATION_CHART_SIZE );

        this.model = model;

        setBackground( NaturalSelectionConstants.COLOR_GENERATION_CHART );

        rootNode = new PNode();
        addWorldChild( rootNode );

        // add both of the charts

        pedigreeNode = new PedigreeNode( model );

        rootNode.addChild( pedigreeNode );

        pedigreeNode.setOffset( new Point2D.Double( 0, 30 ) );

        PText title = new PText( "Generation Chart" );
        title.setFont( new PhetFont( 16, true ) );
        title.translate( ( getPreferredSize().getWidth() - title.getWidth() ) / 2, 5 );
        rootNode.addChild( title );

        GenerationCountNode generationCount = new GenerationCountNode( model );
        generationCount.translate( getPreferredSize().getWidth() - generationCount.getTextWidth() - 20, 5 );
        rootNode.addChild( generationCount );

        mutationPendingNode = null;

        ColorGene.getInstance().addListener( new GeneListener() {
            public void onChangeDominantAllele( boolean primary ) {

            }

            public void onChangeDistribution( int primary, int secondary ) {

            }

            public void onChangeMutatable( boolean mutatable ) {
                handleMutationChange( ColorGene.getInstance(), mutatable );
            }
        } );

        TailGene.getInstance().addListener( new GeneListener() {
            public void onChangeDominantAllele( boolean primary ) {

            }

            public void onChangeDistribution( int primary, int secondary ) {

            }

            public void onChangeMutatable( boolean mutatable ) {
                handleMutationChange( TailGene.getInstance(), mutatable );
            }
        } );

        TeethGene.getInstance().addListener( new GeneListener() {
            public void onChangeDominantAllele( boolean primary ) {

            }

            public void onChangeDistribution( int primary, int secondary ) {

            }

            public void onChangeMutatable( boolean mutatable ) {
                handleMutationChange( TeethGene.getInstance(), mutatable );
            }
        } );

    }

    public void handleMutationChange( Gene gene, boolean mutatable ) {
        if ( mutatable ) {
            if ( mutationPendingNode != null ) {
                throw new RuntimeException( "mutationPendingNode should be null!!!" );
            }
            mutationPendingNode = new MutationPendingNode( gene );
            mutationPendingNode.translate( ( getPreferredSize().getWidth() - mutationPendingNode.getPlacementWidth() ) / 2, getPreferredSize().getHeight() - mutationPendingNode.getPlacementHeight() - 10 );
            rootNode.addChild( mutationPendingNode );
        }
        else {
            if ( mutationPendingNode != null ) {
                rootNode.removeChild( mutationPendingNode );
                mutationPendingNode = null;
            }
        }
    }

    public void reset() {
        pedigreeNode.reset();
    }

}
