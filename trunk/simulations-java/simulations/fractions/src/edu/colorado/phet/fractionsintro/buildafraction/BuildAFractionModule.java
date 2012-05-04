package edu.colorado.phet.fractionsintro.buildafraction;

import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.fractionsintro.FractionsIntroSimSharing.Components;
import edu.colorado.phet.fractionsintro.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractionsintro.buildafraction.view.BuildAFractionCanvas;
import edu.colorado.phet.fractionsintro.common.AbstractFractionsModule;

/**
 * Main module for "build a fraction" tab
 *
 * @author Sam Reid
 */
public class BuildAFractionModule extends AbstractFractionsModule {
    public BuildAFractionModule() {
        this( new BuildAFractionModel() );
    }

    private BuildAFractionModule( BuildAFractionModel model ) {
        super( Components.buildAFractionTab, "Build a Fraction", model.clock );
        setSimulationPanel( new BuildAFractionCanvas( model ) );
    }

    //Test main for launching this module in an application by itself for testing
    public static void main( String[] args ) {
        final ApplicationConstructor constructor = new ApplicationConstructor() {
            @Override public PhetApplication getApplication( PhetApplicationConfig c ) {
                return new PhetApplication( c ) {{addModule( new BuildAFractionModule() );}};
            }
        };
        new PhetApplicationLauncher().launchSim( args, "fractions", "fractions-intro", constructor );
    }
}