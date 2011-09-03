// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.jme;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.umd.cs.piccolox.pswing.PSwingRepaintManager;

/**
 * Repaint manager that checks for HUDNode repaint flags and forwards events to the HUDNode. Compatible
 * with PSwingRepaintManager, since it needs to be a global instance.
 */
public class JMERepaintManager extends PSwingRepaintManager {
    @Override public void addDirtyRegion( JComponent component, int x, int y, int width, int height ) {
        // notify on any region for now
        notifyComponent( component );
        super.addDirtyRegion( component, x, y, width, height );
    }

    @Override public void addInvalidComponent( JComponent invalidComponent ) {
        // notify our component
        notifyComponent( invalidComponent );
        super.addInvalidComponent( invalidComponent );
    }

    private void notifyComponent( JComponent component ) {
        VoidFunction0 callback = (VoidFunction0) component.getClientProperty( HUDNode.ON_REPAINT_CALLBACK );
        if ( callback != null ) {
            callback.apply();
        }
        else {
            Container parent = component.getParent();
            if ( parent != null && parent instanceof JComponent ) {
                notifyComponent( (JComponent) parent );
            }
        }
    }
}
