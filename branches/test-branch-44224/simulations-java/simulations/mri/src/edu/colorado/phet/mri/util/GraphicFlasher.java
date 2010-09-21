/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.util;

import edu.umd.cs.piccolo.PNode;

import javax.swing.*;

/**
 * GraphicFlasher
 *
 * @author Ron LeMaster
 * @version $Revision$
 */

public class GraphicFlasher extends Thread {
    private int numFlashes = 5;
    private PNode graphic;

    public GraphicFlasher( PNode graphic ) {
        this.graphic = graphic;
    }

    public void run() {
        try {
            for( int i = 0; i < numFlashes; i++ ) {
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        graphic.setVisible( false );
                    }
                } );
                Thread.sleep( 100 );
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        graphic.setVisible( true );
                    }
                } );
                Thread.sleep( 100 );
            }
        }
        catch( InterruptedException e ) {
            e.printStackTrace();
        }
    }
}

