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

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ListIterator;

import javax.swing.JFrame;

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
        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                updateHelpItems();
            }
        } );
    }
    
    public void add( AbstractHelpItem helpItem ) {
        getLayer().addChild( helpItem );
    }
    
    public void remove( AbstractHelpItem helpItem ) {
        getLayer().removeChild( helpItem );
    }
    
    public void clear() {
        getLayer().removeAllChildren();
    }
    
    private void updateHelpItems() {
        if ( isVisible() ) {
            ListIterator i = getLayer().getChildrenIterator();
            while ( i.hasNext() ) {
                PNode child = (PNode) i.next();
                if ( child instanceof AbstractHelpItem ) {
                    AbstractHelpItem helpItem = (AbstractHelpItem) child;
                    helpItem.updatePosition();
                }
            }
        }
    }
}
