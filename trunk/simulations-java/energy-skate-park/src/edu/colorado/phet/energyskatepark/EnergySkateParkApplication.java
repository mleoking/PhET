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

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.energyskatepark.model.physics.TestPhysics1D;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkSimulationPanel;
import edu.colorado.phet.energyskatepark.view.EC3LookAndFeel;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkTestMenu;
//import edu.colorado.phet.lookandfeels.LookAndFeelMenu;

import javax.jnlp.UnavailableServiceException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class EnergySkateParkApplication extends PhetApplication {
    private static final String VERSION = "1.05.12";
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
//        getPhetFrame().addMenu( new LookAndFeelMenu() );
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

    public static void mainESP( final String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                EnergySkateParkStrings.init( args, "localization/EnergySkateParkStrings" );
                new EC3LookAndFeel().initLookAndFeel();
                new EnergySkateParkApplication( args ).start();
            }
        } );
    }

    public static void main( final String[] args ) {
        mainESP( args );
    }

    public static void mainTestPhysics1D( final String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                new EC3LookAndFeel().initLookAndFeel();
                TestPhysics1D.main( args );
            }
        } );
    }
    public static class EnergySkateParkDebugFrameSetup implements FrameSetup {

        public void initialize( JFrame frame ) {
            frame.setSize( Toolkit.getDefaultToolkit().getScreenSize().width - EnergySkateParkModule.energyFrameWidth,
                           Toolkit.getDefaultToolkit().getScreenSize().height - 100 - EnergySkateParkModule.chartFrameHeight //for debug
            );
            frame.setLocation( 0, 0 );
        }

    }
    public static class EnergySkateParkFrameSetup implements FrameSetup {
        public void initialize( JFrame frame ) {
            if( Toolkit.getDefaultToolkit().getScreenSize().height <= 768 ) {
                new MaxExtent( new TopCenter( Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height - 100 ) ).initialize( frame );
            }
            else {
                Toolkit tk = Toolkit.getDefaultToolkit();
                int x = 0;
                int y = 0;
                frame.setLocation( x, y );
                frame.setSize( tk.getScreenSize().width - 200, tk.getScreenSize().height - 200 );
            }
//        new CenteredWithInsets(50,50).initialize( frame );
        }
    }

}
