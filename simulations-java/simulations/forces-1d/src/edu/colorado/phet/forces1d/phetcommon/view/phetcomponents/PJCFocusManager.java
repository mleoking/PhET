/*  */
package edu.colorado.phet.forces1d.phetcommon.view.phetcomponents;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

import javax.swing.*;

/**
 * Uses setNextFocusableComponent to handle focus.  Doesn't support removal yet.  A later version should use FocusTraversalPolicy.
 */

public class PJCFocusManager implements PhetJComponentManager.Listener {
    private ArrayList list = new ArrayList();
    private FocusTraversalPolicy policy = new PJCFocusManager.MyPolicy();

    public void phetJComponentCreated( PhetJComponent phetJComponent ) {
        if ( list.size() >= 1 ) {
            PhetJComponent prev = (PhetJComponent) list.get( list.size() - 1 );
            prev.getSourceComponent().setNextFocusableComponent( phetJComponent.getSourceComponent() );

            PhetJComponent first = (PhetJComponent) list.get( 0 );
            phetJComponent.getSourceComponent().setNextFocusableComponent( first.getSourceComponent() );
        }
        list.add( phetJComponent );
    }

    public class MyPolicy extends SortingFocusTraversalPolicy {
        public MyPolicy() {
            super( new PJCFocusManager.MyComparator() );
        }
    }

    public class MyComparator implements Comparator {
        public int compare( Object o1, Object o2 ) {
            if ( o1 instanceof JComponent && o2 instanceof JComponent ) {
                int d1 = indexOf( (JComponent) o1 );
                int d2 = indexOf( (JComponent) o2 );
                return Double.compare( d1, d2 );
            }
            return 0;
        }
    }

    private int indexOf( JComponent jComponent ) {
        for ( int i = 0; i < list.size(); i++ ) {
            PhetJComponent phetJComponent = (PhetJComponent) list.get( i );
            if ( phetJComponent.getSourceComponent() == jComponent ) {
                return i;
            }
        }
        return -1;
    }
}
