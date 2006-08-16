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

import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;
import edu.colorado.phet.common.view.ModelSlider;

import javax.swing.*;

/**
 * MyPSwing
 * <p>
 * An extension of PSwing that properly handles ModelSliders.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MyPSwing extends PSwing {

    public MyPSwing( PSwingCanvas canvas, JComponent component ) {
        super( canvas, component );
    }

    protected boolean isBufferValid() {
        JComponent component = getComponent();
        if( component instanceof ModelSlider ) {
            return false;
        }
        else {
            return super.isBufferValid();
        }
    }
}