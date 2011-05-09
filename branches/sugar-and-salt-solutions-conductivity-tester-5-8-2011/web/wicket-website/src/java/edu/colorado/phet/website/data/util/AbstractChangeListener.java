package edu.colorado.phet.website.data.util;

import org.hibernate.event.PostDeleteEvent;
import org.hibernate.event.PostInsertEvent;
import org.hibernate.event.PostUpdateEvent;

/**
 * Default implementation for IChangeListener, but provides access methods for checking changed properties, etc.
 */
public abstract class AbstractChangeListener implements IChangeListener {

    /*---------------------------------------------------------------------------*
    * default implementations
    *----------------------------------------------------------------------------*/

    public void onInsert( Object object, PostInsertEvent event ) {

    }

    public void onUpdate( Object object, PostUpdateEvent event ) {

    }

    public void onDelete( Object object, PostDeleteEvent event ) {

    }
}
