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
 * ImmediateUnionPaint
 *
 * @author Sam Reid
 * @version $Revision$
 */
public class ImmediateUnionPaint extends StoredRectRepainter {

    public ImmediateUnionPaint( JComponent component ) {
        super( component );
    }

    public void finishedUpdateCycle() {
        Rectangle union = super.getUnion();
        super.clearList();
        if( union != null ) {
            super.getComponent().paintImmediately( union );
        }
    }
}
