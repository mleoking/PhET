// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.ManualGeneExpressionModule;
import edu.colorado.phet.geneexpressionbasics.mrnaproduction.MessengerRnaProductionModule;
import edu.colorado.phet.geneexpressionbasics.multiplecells.MultipleCellsModule;

import static edu.colorado.phet.geneexpressionbasics.GeneExpressionBasicsResources.Strings.*;

/**
 * Main application class for this simulation.
 *
 * @author John Blanco
 */
public class GeneExpressionBasicsApplication extends PiccoloPhetApplication {

    // Project name.
    public static final String NAME = "gene-expression-basics";

    // Resource loader.
    public static final PhetResources RESOURCES = new PhetResources( NAME );

    /**
     * Constructor.
     */
    public GeneExpressionBasicsApplication( PhetApplicationConfig config ) {
        super( config );

        // module(s)
        addModule( new ManualGeneExpressionModule( TAB__CELL_GENE_EXPRESSION ) );
        addModule( new MessengerRnaProductionModule( TAB__MESSENGER_RNA_PRODUCTION ) );
        addModule( new MultipleCellsModule( TAB__MULTIPLE_CELLS, getPhetFrame() ) );
    }

    /**
     * Main entry point for this simulation.
     */
    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, NAME, GeneExpressionBasicsApplication.class );
    }
}
