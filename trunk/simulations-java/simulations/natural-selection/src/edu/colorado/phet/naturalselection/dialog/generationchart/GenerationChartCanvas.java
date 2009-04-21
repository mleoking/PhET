package edu.colorado.phet.naturalselection.dialog.generationchart;

import java.awt.*;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModel;
import edu.colorado.phet.naturalselection.view.GenerationChartNode;
import edu.umd.cs.piccolo.PNode;

public class GenerationChartCanvas extends PhetPCanvas {

    public static final Dimension chartSize = new Dimension( 800, 600 );

    private NaturalSelectionModel model;

    public GenerationChartCanvas( NaturalSelectionModel model ) {


        super( chartSize );

        setPreferredSize( chartSize );

        this.model = model;

        setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        //setBorder( null );


        PNode rootNode = new PNode();
        addWorldChild( rootNode );

        //PNode bunny = new BunnyNode( ColorGene.WHITE_ALLELE, TeethGene.TEETH_REGULAR_ALLELE, TailGene.TAIL_SHORT_ALLELE );
        //rootNode.addChild( bunny );

        GenerationChartNode generationChartNode = new GenerationChartNode( model );
        rootNode.addChild( generationChartNode );
    }
}
