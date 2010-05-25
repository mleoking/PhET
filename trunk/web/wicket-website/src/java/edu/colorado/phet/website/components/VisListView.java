package edu.colorado.phet.website.components;

import java.util.List;

import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;

/**
 * A list view that hides empty lists. Do NOT use for lists that may have their lists change after initialization, this
 * is mainly a shortcut class
 *
 * @param <T>
 */
public abstract class VisListView<T> extends ListView<T> {

    public VisListView( String id ) {
        super( id );
    }

    public VisListView( String id, IModel<? extends List<? extends T>> model ) {
        super( id, model );
        if ( model.getObject().isEmpty() ) {
            setVisible( false );
        }
    }

    public VisListView( String id, List<? extends T> list ) {
        super( id, list );
        if ( list.isEmpty() ) {
            setVisible( false );
        }
    }
}
