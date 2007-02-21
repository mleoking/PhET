/* Copyright 2007, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.opticaltweezers.view;

import javax.swing.JComponent;

import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;


public class PhetPSwing extends PSwing {

    public PhetPSwing( PSwingCanvas canvas, JComponent component ) {
        super( canvas, component );
    }
    
    public void setVisible( boolean b ) {
        super.setVisible( b );
        setPickable( b );
        setChildrenPickable( b );
    }
}
