package org.jmol;

/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2000-2005  The Jmol Development Team
 *
 * Contact: jmol-developers@lists.sf.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 *  02111-1307  USA.
 */

import java.applet.Applet;
import java.awt.*;
import java.util.Arrays;
import java.util.Map;

import javax.swing.*;

import org.jmol.adapter.smarter.SmarterJmolAdapter;
import org.jmol.api.JmolCallbackListener;
import org.jmol.api.JmolStatusListener;
import org.jmol.api.JmolViewer;
import org.openscience.jmol.app.jmolpanel.AppConsole;

/**
 * A example of integrating the Jmol viewer into a java application, with optional console.
 * <p/>
 * <p>I compiled/ran this code directly in the examples directory by doing:
 * <pre>
 * javac -classpath ../Jmol.jar Integration.java
 * java -cp .:../Jmol.jar Integration
 * </pre>
 *
 * @author Miguel <miguel@jmol.org>
 */

public class Integration {

    /*
    * Demonstrates a simple way to include an optional console along with the applet.
    *
    */
    public static void main( String[] argv ) {
        JFrame frame = new JFrame( "Hello" );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( 410, 700 );
        Container contentPane = frame.getContentPane();
        JmolPanel jmolPanel = new JmolPanel();
        jmolPanel.setPreferredSize( new Dimension( 400, 400 ) );

        // main panel -- Jmol panel on top

        JPanel panel = new JPanel();
        panel.setLayout( new BorderLayout() );
        panel.add( jmolPanel );

        // main panel -- console panel on bottom

        JPanel panel2 = new JPanel();
        panel2.setLayout( new BorderLayout() );
        panel2.setPreferredSize( new Dimension( 400, 300 ) );
        AppConsole console = new AppConsole( jmolPanel.viewer, panel2,
                                             "History State Clear" );

        // You can use a different JmolStatusListener or JmolCallbackListener interface
        // if you want to, but AppConsole itself should take care of any console-related callbacks
        //jmolPanel.viewer.setJmolCallbackListener( console );
        jmolPanel.viewer.setJmolCallbackListener( new JmolCallbackListener() {
            public void setCallbackFunction( String s, String s1 ) {
                System.out.println( "callback: setCallbackFunction: " + s + ", " + s1 );
            }

            public void notifyCallback( int i, Object[] objects ) {
                System.out.println( "callback: notifyCallback: " + i + ", " + Arrays.toString( objects ) );
            }

            public boolean notifyEnabled( int i ) {
                System.out.println( "callback: notifyEnabled: " + i );
                return true;
            }
        } );

        panel.add( "South", panel2 );

        contentPane.add( panel );
        frame.setVisible( true );

        // sample start-up script

        //String strError = jmolPanel.viewer.openFile( "http://chemapps.stolaf.edu/jmol/docs/examples-11/data/caffeine.xyz" );
        String strError = jmolPanel.viewer.openStringInline( strXyzHOH );
        //viewer.openStringInline(strXyzHOH);
        //if ( strError == null ) { jmolPanel.viewer.evalString( strScript ); }
        //else { Logger.error( strError ); }

        //jmolPanel.viewer.script( "wireframe off; spacefill on;" ); // space fill
        //jmolPanel.viewer.script( "wireframe 0.2; spacefill 25%" ); // ball and stick
        //jmolPanel.viewer.script( "wireframe off; spacefill 25%" ); // no bonds
        jmolPanel.viewer.script( "wireframe off; spacefill 50%" ); // no bonds
        // set bonds on
        // also available: spacefill ionic


    }

    final static String strXyzHOH = "3\n"
                                    + "water\n"
                                    + "O  0.0 0.0 0.0\n"
                                    + "H  0.76923955 -0.59357141 0.0\n"
                                    + "H -0.76923955 -0.59357141 0.0\n";

    final static String strScript = "delay; move 360 0 0 0 0 0 0 0 4;";

    static class JmolPanel extends JPanel {

        JmolViewer viewer;

        private final Dimension currentSize = new Dimension();
        private final Rectangle rectClip = new Rectangle(); // ignored by Jmol

        JmolPanel() {
            viewer = JmolViewer.allocateViewer( this, new SmarterJmolAdapter(),
                                                null, null, null, null, new JmolStatusListener() {
                        public String eval( String s ) {
                            return null;
                        }

                        public float[][] functionXY( String s, int i, int i1 ) {
                            return new float[0][];
                        }

                        public float[][][] functionXYZ( String s, int i, int i1, int i2 ) {
                            return new float[0][][];
                        }

                        public String createImage( String s, String s1, Object o, int i ) {
                            return null;
                        }

                        public Map<String, Applet> getRegistryInfo() {
                            return null;
                        }

                        public void showUrl( String s ) {
                        }

                        public void setCallbackFunction( String s, String s1 ) {
                        }

                        public void notifyCallback( int i, Object[] objects ) {
                        }

                        public boolean notifyEnabled( int i ) {
                            return false;
                        }
                    } );
        }

        @Override
        public void paint( Graphics g ) {
            getSize( currentSize );
            g.getClipBounds( rectClip );
            viewer.renderScreenImage( g, currentSize, rectClip );
        }
    }

}
