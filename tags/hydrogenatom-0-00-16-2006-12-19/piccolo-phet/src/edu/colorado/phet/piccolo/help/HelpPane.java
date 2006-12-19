/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.piccolo.help;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ListIterator;

import javax.swing.JFrame;

import edu.colorado.phet.common.application.PhetApplication;
import edu.umd.cs.piccolo.PNode;


/**
 * HelpPane is an extension of PGlassPane that provides methods
 * for managing help items on the glass pane.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HelpPane extends GlassPaneCanvas {

    /**
     * Constructor.
     * 
     * @param parentFrame we serve as the glass pane for this frame
     */
    public HelpPane( JFrame parentFrame ) {
        super( parentFrame );
        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                updateHelpItems();
            }
        } );
    }
    
    /**
     * Adds a help item.
     * 
     * @param helpItem
     */
    public void add( AbstractHelpItem helpItem ) {
        getLayer().addChild( helpItem );
    }
    
    /**
     * Removes a help item.
     * 
     * @param helpItem
     */
    public void remove( AbstractHelpItem helpItem ) {
        getLayer().removeChild( helpItem );
    }
    
    /**
     * Removes all help items.
     * Other nodes on the help pane are not affected.
     */
    public void clear() {
        ListIterator i = getLayer().getChildrenIterator();
        while ( i.hasNext() ) {
            PNode child = (PNode) i.next();
            if ( child instanceof AbstractHelpItem ) {
                getLayer().removeChild( child );
            }
        }
    }
    
    /**
     * Updates all help items.
     * Other nodes on the help pane are not affected.
     */
    public void updateHelpItems() {
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
    
    /**
     * Shows and hides the help pane.
     * When the help pane is invisible, its help items are disabled
     * so that they don't consume resources tracking the position
     * and visibility of their target objects.
     * 
     * @param visible
     */
    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        ListIterator i = getLayer().getChildrenIterator();
        while ( i.hasNext() ) {
            PNode child = (PNode) i.next();
            if ( child instanceof AbstractHelpItem ) {
                AbstractHelpItem helpItem = (AbstractHelpItem) child;
                helpItem.setEnabled( visible );
            }
        }
    }
}
