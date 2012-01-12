// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.reactionsandrates.test;/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.reactionsandrates.view.Legend;

import javax.swing.*;
import java.awt.*;

/**
 * LegendTest
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class LegendTest extends JFrame {
    public LegendTest() throws HeadlessException {
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        JPanel jp = new JPanel( new BorderLayout() );
        jp.setPreferredSize( new Dimension( 300, 300 ) );
        setContentPane( jp );
//        jp.add( new Legend() );
        PhetPCanvas ppc = new PhetPCanvas( new Dimension( 200, 200 ) );
//
        ppc.setBackground( Color.white );
        ppc.setBounds( 0, 0, 200, 200 );
//        PPath pr = new PPath( new Rectangle2D.Double( 2,2,20,20));
//        pr.setPaint( Color.red );
//        ppc.addScreenChild( pr );

//        ppc.getLayer().addChild( pr );
//        getContentPane().add(  ppc );

//        getContentPane().add( jp2 );
//        getContentPane().add( ppc );
        getContentPane().add( new Legend() );

        pack();
        setVisible( true );
    }

    public static void main( String[] args ) {
        new LegendTest();
    }
}
