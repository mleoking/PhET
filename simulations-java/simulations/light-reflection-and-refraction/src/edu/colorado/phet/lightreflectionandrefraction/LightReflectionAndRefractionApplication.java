// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.dialogs.ColorChooserFactory;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.lightreflectionandrefraction.modules.intro.IntroModule;
import edu.colorado.phet.lightreflectionandrefraction.modules.prisms.PrismsModule;
import edu.colorado.phet.lightreflectionandrefraction.view.MediumColorDialog;

/**
 * @author Sam Reid
 */
public class LightReflectionAndRefractionApplication extends PiccoloPhetApplication {
    private static final String NAME = "light-reflection-and-refraction";
    public static final PhetResources RESOURCES = new PhetResources( NAME );

    public LightReflectionAndRefractionApplication( PhetApplicationConfig config ) {
        super( config );
        final IntroModule introModule = new IntroModule();
//        addModule( introModule );
        addModule( new PrismsModule() );
        getPhetFrame().addMenu( new JMenu( "Developer" ) {{
            add( new JMenuItem( "Medium colors" ) {{
                addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        new MediumColorDialog( getPhetFrame(), introModule.getLRRModel() ).setVisible( true );
                    }
                } );
            }} );
            add( new JMenuItem( "Laser Color" ) {{
                addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        ColorChooserFactory.showDialog( "Laser Color", getPhetFrame(), introModule.getLRRModel().getLaser().color.getValue(), new ColorChooserFactory.Listener() {
                            public void colorChanged( Color color ) {
                                introModule.getLRRModel().getLaser().color.setValue( color );
                            }

                            public void ok( Color color ) {
                                introModule.getLRRModel().getLaser().color.setValue( color );
                            }

                            public void cancelled( Color originalColor ) {
                                introModule.getLRRModel().getLaser().color.setValue( originalColor );
                            }
                        } );
                    }
                } );
            }} );
        }} );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, NAME, LightReflectionAndRefractionApplication.class );
    }
}
