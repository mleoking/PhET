/* Copyright 2004, University of Colorado */

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
 * UnionRepaint
 *
 * @author Sam Reid
 * @version $Revision$
 */
public class UnionRepaint extends StoredRectRepainter {

    public UnionRepaint( JComponent component ) {
        super( component );
    }

    public void finishedUpdateCycle() {
        Rectangle union = super.getUnion();
        super.clearList();
        super.getComponent().repaint( union );
    }
}
