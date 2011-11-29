// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.ManualGeneExpressionModule;
import edu.colorado.phet.geneexpressionbasics.proteinlevelsincell.ProteinLevelsInCellModule;

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
        addModule( new ManualGeneExpressionModule( "Manual Gene Expression" ) );
        addModule( new ProteinLevelsInCellModule( "Protein Levels in a Cell" ) );
        addModule( new ManualGeneExpressionModule( "One Cell vs. Many" ) );
    }

    /**
     * Main entry point for this simulation.
     */
    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, NAME, GeneExpressionBasicsApplication.class );
    }
}
