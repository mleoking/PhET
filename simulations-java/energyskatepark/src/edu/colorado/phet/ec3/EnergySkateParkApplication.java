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
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.ec3;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.lookandfeels.LookAndFeelMenu;

import javax.jnlp.UnavailableServiceException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class EnergySkateParkApplication extends PhetApplication {
    private static final String VERSION = "1.05.06";
    private EnergySkateParkModule module;
    public static double SIMULATION_TIME_DT = 0.03;

    public EnergySkateParkApplication( String[] args ) {
        super( args, EnergySkateParkStrings.getString( "energy.skate.park" ), EnergySkateParkStrings.getString( "energy.conservation" ),
               VERSION,
//               new EnergySkateParkDebugFrameSetup() );
new EnergySkateParkFrameSetup() );
        module = new EnergySkateParkModule( "Module", new SwingClock( 30, SIMULATION_TIME_DT ), getPhetFrame() );
        setModules( new Module[]{module} );
        getPhetFrame().addMenu( new EnergySkateParkOptionsMenu() );
        getPhetFrame().addMenu( new EnergySkateParkTestMenu( this, args ) );

        JMenuItem saveItem = new JMenuItem( "Save" );
        saveItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    module.save();
                }
                catch( UnavailableServiceException e1 ) {
                    e1.printStackTrace();
                }
                catch( IOException e1 ) {
                    e1.printStackTrace();
                }
            }
        } );

        JMenuItem openItem = new JMenuItem( "Open" );
        openItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    module.open();
                }
                catch( UnavailableServiceException e1 ) {
                    e1.printStackTrace();
                }
                catch( IOException e1 ) {
                    e1.printStackTrace();
                }
                catch( ClassNotFoundException e1 ) {
                    e1.printStackTrace();
                }
            }
        } );
        getPhetFrame().addFileMenuItem( openItem );
        getPhetFrame().addFileMenuItem( saveItem );

        getPhetFrame().addFileMenuSeparator();
        getPhetFrame().addMenu( new LookAndFeelMenu() );
    }

    public static void main( final String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                EnergySkateParkStrings.init( args, "localization/EnergySkateParkStrings" );
                new EC3LookAndFeel().initLookAndFeel();
                new EnergySkateParkApplication( args ).start();
            }
        } );
    }

    public EnergySkateParkModule getModule() {
        return module;
    }

    private void start() {
        super.startApplication();
        module.getPhetPCanvas().requestFocus();
        final EnergySkateParkSimulationPanel c = module.getEnergyConservationCanvas();
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher( new KeyEventDispatcher() {
            public boolean dispatchKeyEvent( KeyEvent e ) {
                if( !c.hasFocus() ) {
                    int id = e.getID();
                    switch( id ) {
                        case KeyEvent.KEY_PRESSED:
                            c.keyPressed( e );
                            break;
                        case KeyEvent.KEY_RELEASED:
                            c.keyReleased( e );
                            break;
                        case KeyEvent.KEY_TYPED:
                            c.keyTyped( e );
                            break;
                        default:
                            System.out.println( "unknown key event type" );
                            break;
                    }
                }
                return false;
            }
        } );
    }
}
