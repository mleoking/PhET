/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.semiconductor.test;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Feb 26, 2004
 * Time: 12:58:02 AM
 * Copyright (c) Feb 26, 2004 by Sam Reid
 */
public class TestClass {
    public static void main( String[] args ) {
        JFrame jf = new JFrame();
        JMenuBar jmb = new JMenuBar();
        JMenu file = new JMenu( "File" );
        file.add( new JMenuItem( "New Microworld" ) );
        file.add( new JMenuItem( "Close" ) );
        file.add( new JMenuItem( "Save" ) );
        file.addSeparator();
        file.add( new JMenuItem( "Exit" ) );
        jf.setJMenuBar( jmb );
        jmb.add( file );
        jf.pack();
        jf.setVisible( true );
    }
//    class Chromosome{
//        void runBehavior(){
//            if (seeEnemies())
//                turnAndRun();
//        }
//        void bodyBehavior(){
//            new Circle(BLUE).extend().addTo(new Rectangle(ORANGE));
//        }
//    }
}
