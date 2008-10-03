/*
* The Physics Education Technology (PhET) project provides
* a suite of interactive educational simulations.
* Copyright (C) 2004-2006 University of Colorado.
*
* This program is free software; you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation; either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License along
* with this program; if not, write to the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
*
* For additional licensing options, please contact PhET at phethelp@colorado.edu
*/

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author:samreid $
 * Revision : $Revision:13739 $
 * Date modified : $Date:2007-03-14 07:21:39 -0600 (Wed, 14 Mar 2007) $
 */
package edu.colorado.phet.energyskatepark;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.util.logging.JFrameLogger;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.energyskatepark.serialization.EnergySkateParkIO;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkFrameSetup;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkLookAndFeel;
import edu.colorado.phet.energyskatepark.view.swing.EnergySkateParkTestMenu;
import edu.colorado.phet.energyskatepark.view.swing.EnergySkateParkTrackMenu;

public class EnergySkateParkApplication extends PhetApplication {
    private EnergySkateParkModule module;
    public static double SIMULATION_TIME_DT = 0.03;
    public static final boolean IGNORE_THERMAL_DEFAULT = false;

    public EnergySkateParkApplication( String[] args ) {
        this( args, new EnergySkateParkOptions() );
    }

    public EnergySkateParkApplication( String[] args, EnergySkateParkOptions options ) {
        super( args, EnergySkateParkStrings.getString( "energy-skate-park.name" ), EnergySkateParkStrings.getString( "energy-skate-park.description" ),
               PhetApplicationConfig.getVersion( "energy-skate-park" ).formatForTitleBar(),
//               new EnergySkateParkDebugFrameSetup() );
new EnergySkateParkFrameSetup() );
        module = new EnergySkateParkModule( "Module", new ConstantDtClock( 30, SIMULATION_TIME_DT ), getPhetFrame(), options );
        setModules( new Module[]{module} );
        getPhetFrame().addMenu( new EnergySkateParkOptionsMenu( module ) );
        getPhetFrame().addMenu( new EnergySkateParkTestMenu( this ) );
        getPhetFrame().addMenu( new EnergySkateParkTrackMenu( this ) );

        JMenuItem saveItem = new JMenuItem( EnergySkateParkStrings.getString( "file-menu.save" ) + "..." );
        saveItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    EnergySkateParkIO.save( module );
                }
                catch( Exception e1 ) {
                    e1.printStackTrace();
                }
            }
        } );

        JMenuItem openItem = new JMenuItem( EnergySkateParkStrings.getString( "file-menu.open" ) + "..." );
        openItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    EnergySkateParkIO.open( module );
                }
                catch( Exception e1 ) {
                    e1.printStackTrace();
                }
            }
        } );
        getPhetFrame().addFileMenuItem( openItem );
        getPhetFrame().addFileMenuItem( saveItem );

        getPhetFrame().addFileMenuSeparator();
    }

    public EnergySkateParkModule getModule() {
        return module;
    }

    private void start() {
        super.startApplication();
        module.getPhetPCanvas().requestFocus();
    }

    public static void main( final String[] args ) throws InvocationTargetException, InterruptedException {
        SwingUtilities.invokeAndWait( new Runnable() {
            public void run() {
//                JFrameLogger logger = new JFrameLogger( "Localization Log" );
//                logger.log( "log started at "+new Date() );
//                PhetResources.setLogger( logger );

                EnergySkateParkOptions skateParkOptions = parseOptions( args );

                main( args,skateParkOptions );

//                logger.setVisible( true );

            }
        } );


    }

    public static void main(String[] args, EnergySkateParkOptions skateParkOptions ) {
        new EnergySkateParkLookAndFeel().initLookAndFeel();
        new EnergySkateParkApplication( args, skateParkOptions ).start();
    }

    private static EnergySkateParkOptions parseOptions( String[] args ) {
        //todo: not yet implemented
        return new EnergySkateParkOptions();
    }

}
