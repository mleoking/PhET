/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view.basicgraphics.repaint;

import javax.swing.*;
import java.awt.*;

/**
 * InvokeLaterImmediatePaint
 *
 * @author Sam Reid
 * @version $Revision$
 */
public class InvokeLaterImmediatePaint extends StoredRectRepainter {

    public InvokeLaterImmediatePaint( JComponent component ) {
        super( component );
    }

    public void finishedUpdateCycle() {
        final Rectangle union = super.getUnion();
        super.clearList();
        final JComponent component = super.getComponent();
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                component.paintImmediately( union );
            }
        } );
    }
}
