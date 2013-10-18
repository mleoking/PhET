// Copyright 2002-2013, University of Colorado
package edu.colorado.phet.fractions.research_october_2013;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.fractions.buildafraction.BuildAFractionModule;
import edu.colorado.phet.fractions.buildafraction.FractionLabModule;
import edu.colorado.phet.fractions.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractions.fractionmatcher.MatchingGameModule;
import edu.colorado.phet.fractions.fractionsintro.equalitylab.EqualityLabModule;
import edu.colorado.phet.fractions.fractionsintro.intro.FractionsIntroModule;
import edu.umd.cs.piccolo.util.PDebug;

/**
 * "Fractions Intro" PhET Application
 *
 * @author Sam Reid
 */
public class FractionsIntroStudyOctober2013Application extends PiccoloPhetApplication {

    //Global flag for whether this functionality should be enabled
    public static boolean recordRegressionData;
    public static boolean isReport = true;
    public static Report report = new Report();
    private final TextArea reportArea = new TextArea();

    public FractionsIntroStudyOctober2013Application( PhetApplicationConfig config ) {
        super( config );

        //Another way to do this would be to pass a FunctionInvoker to all the modules
        recordRegressionData = config.hasCommandLineArg( "-recordRegressionData" );
        addModule( new FractionsIntroModule() );
        final BooleanProperty audioEnabled = new BooleanProperty( true );
        addModule( new BuildAFractionModule( new BuildAFractionModel( new BooleanProperty( false ), audioEnabled ) ) );
        addModule( new EqualityLabModule() );
        addModule( new MatchingGameModule( config.isDev(), audioEnabled ) );
        addModule( new FractionLabModule( false ) );

        //Add developer menu items for debugging performance, see #3314

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

        if ( isReport ) {
            JFrame frame = new JFrame( "Report" );
            frame.setContentPane( new JScrollPane( reportArea ) );
            frame.setSize( 800, 600 );
            frame.setVisible( true );
            Timer t = new Timer( 1000, new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    report.update();
                    reportArea.setText( report.toString() );
                }
            } );
            t.start();
        }
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, "fractions", "fractions-intro", FractionsIntroStudyOctober2013Application.class );
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