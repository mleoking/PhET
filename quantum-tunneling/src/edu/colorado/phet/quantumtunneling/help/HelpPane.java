/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.help;

import java.awt.Component;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;


/**
 * HelpPane
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HelpPane extends PGlassPane {

    public HelpPane( JFrame parentFrame ) {
        super( parentFrame );
    }
    
    public void add( PNode helpItem ) {
        getLayer().addChild( helpItem );
    }
    
    public void remove( PNode helpItem ) {
        getLayer().removeChild( helpItem );
    }
    
    public void clear() {
        getLayer().removeAllChildren();
    }
}
