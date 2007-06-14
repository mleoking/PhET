/* Copyright 2003-2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/phetcommon/tests-src/edu/colorado/phet/common/tests/TestModelSlider.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.7 $
 * Date modified : $Date: 2006/01/03 23:37:21 $
 */
package edu.colorado.phet.common.tests;

import edu.colorado.phet.common.view.ModelSlider;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * User: Sam Reid
 * Date: Dec 12, 2004
 * Time: 8:32:49 PM
 * Copyright (c) Dec 12, 2004 by Sam Reid
 */

public class TestModelSlider {
    public static void main( String[] args ) {
        final ModelSlider ms = new ModelSlider( "Model Slider", "Newtons", 2, 4, 3 );
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setContentPane( ms );
        frame.pack();
        frame.setVisible( true );
        ms.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double value = ms.getValue();
                System.out.println( "value = " + value );
            }
        } );
    }
}