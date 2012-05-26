// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;

import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.fractionsintro.buildafraction.BuildAFractionModule;
import edu.colorado.phet.fractionsintro.equalitylab.EqualityLabModule;
import edu.colorado.phet.fractionsintro.intro.FractionsIntroModule;
import edu.colorado.phet.fractionsintro.matchinggame.MatchingGameModule;
import edu.umd.cs.piccolo.util.PDebug;

/**
 * "Fractions Intro" PhET Application
 *
 * @author Sam Reid
 */
public class FractionsIntroApplication extends PiccoloPhetApplication {

    //Global flag for whether this functionality should be enabled
    public static boolean recordRegressionData;

    public FractionsIntroApplication( PhetApplicationConfig config ) {
        super( config );

        //Another way to do this would be to pass a FunctionInvoker to all the modules
        recordRegressionData = config.hasCommandLineArg( "-recordRegressionData" );
        addModule( new FractionsIntroModule() );
        addModule( new BuildAFractionModule() );
        addModule( new EqualityLabModule() );
        addModule( new MatchingGameModule( config.isDev() ) );

        //Add developer menu items for debugging performance, see #3314

//        PDebug.debugRegionManagement = true;
//        PDebug.debugBounds = true;
        getPhetFrame().getDeveloperMenu().add( new JCheckBoxMenuItem( "PDebug.regionManagement", PDebug.debugRegionManagement ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( final ActionEvent e ) {
                    PDebug.debugRegionManagement = isSelected();
                }
            } );
        }} );

        getPhetFrame().getDeveloperMenu().add( new JCheckBoxMenuItem( "PDebug.debugFullBounds", PDebug.debugFullBounds ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( final ActionEvent e ) {
                    PDebug.debugFullBounds = isSelected();
                }
            } );
        }} );

        getPhetFrame().getDeveloperMenu().add( new JCheckBoxMenuItem( "PDebug.debugBounds", PDebug.debugBounds ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( final ActionEvent e ) {
                    PDebug.debugBounds = isSelected();
                }
            } );
        }} );

        getPhetFrame().getDeveloperMenu().add( new JCheckBoxMenuItem( "PDebug.debugPaintCalls", PDebug.debugPaintCalls ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( final ActionEvent e ) {
                    PDebug.debugPaintCalls = isSelected();
                }
            } );
        }} );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, "fractions", "fractions-intro", FractionsIntroApplication.class );
    }

    //Utility method for testing a single module
    public static void runModule( String[] args, final Module module ) {
        final ApplicationConstructor constructor = new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig c ) {
                return new PhetApplication( c ) {{addModule( module );}};
            }
        };
        new PhetApplicationLauncher().launchSim( args, "fractions", "fractions-intro", constructor );
    }
}