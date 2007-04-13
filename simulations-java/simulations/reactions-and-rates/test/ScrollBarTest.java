/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

import javax.swing.*;
import java.awt.*;

/**
 * ScrollBarTest
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ScrollBarTest {

    public static void main( String[] args ) {
        JFrame frame = new JFrame( );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE);
        frame.setSize( 500, 300 );

        JScrollBar jsb = new JScrollBar( JScrollBar.HORIZONTAL, 200 , 20, 0, 800);
        jsb.setPreferredSize( new Dimension( 400, 15 ) );

        frame.setContentPane( new JPanel() );
        frame.getContentPane().add(jsb );

        frame.setVisible( true );


    }
}
