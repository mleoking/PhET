/* Copyright 2004, Sam Reid */
package edu.colorado.phet.travoltage;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.application.PhetAboutDialog;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.view.util.SwingUtils;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Jun 30, 2006
 * Time: 11:20:53 PM
 * Copyright (c) Jun 30, 2006 by Sam Reid
 */

public class TravoltageApplication extends PhetApplication {
    private static final String VERSION = "1.04";
    private JDialog dialog;

    public TravoltageApplication( String[] args ) {
        super( args, SimStrings.get( "TravoltageApplication.title" ), SimStrings.get( "TravoltageApplication.description" ), VERSION, new TravoltageFrameSetup() );
        addModule( new TravoltageModule() );
    }

    public void showAboutDialog() {
        if( dialog == null ) {
            //dialog = new TravoltageAboutDialog( this );
            dialog = new PhetAboutDialog(this);
            
            SwingUtils.centerWindowOnScreen( dialog );
        }
        dialog.show();
    }

    public static class TravoltageFrameSetup implements FrameSetup {
        public void initialize( JFrame frame ) {
            new FrameSetup.CenteredWithSize( 800, 700 ).initialize( frame );
        }
    }

    public static void main( final String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                SimStrings.getInstance().init( args, "localization/TravoltageStrings" );
                new TravoltageLookAndFeel().initLookAndFeel();
                new TravoltageApplication( args ).startApplication();
            }
        } );
    }
}
