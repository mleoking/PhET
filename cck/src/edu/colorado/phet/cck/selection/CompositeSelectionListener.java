/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.selection;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Oct 26, 2003
 * Time: 12:26:28 AM
 * Copyright (c) Oct 26, 2003 by Sam Reid
 */
public class CompositeSelectionListener implements SelectionListener {
    ArrayList list = new ArrayList();

    public void addSelectionListener( SelectionListener sl ) {
        list.add( sl );
    }

    public void removeSelectionListener( SelectionListener sl ) {
        list.remove( sl );
    }

    public void selectionChanged( boolean sel ) {
        for( int i = 0; i < list.size(); i++ ) {
            SelectionListener selectionListener = (SelectionListener)list.get( i );
            selectionListener.selectionChanged( sel );
        }
    }

}
