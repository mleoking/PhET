package edu.colorado.phet.website.data.util;

import org.hibernate.event.PostDeleteEvent;
import org.hibernate.event.PostInsertEvent;
import org.hibernate.event.PostUpdateEvent;

/**
 * For listening to changes to the particular type of data
 * <p/>
 * Congregates various important post-events from hibernate
 *
 * @param <E> The DAO type
 */
public interface IChangeListener<E> {

    public void onInsert( E object, PostInsertEvent event );

    public void onUpdate( E object, PostUpdateEvent event );

    public void onDelete( E object, PostDeleteEvent event );
}
