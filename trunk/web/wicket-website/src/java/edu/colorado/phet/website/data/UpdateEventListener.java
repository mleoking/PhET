package edu.colorado.phet.website.data;

import org.hibernate.event.PostUpdateEvent;
import org.hibernate.event.PostUpdateEventListener;

public class UpdateEventListener implements PostUpdateEventListener {
    public void onPostUpdate( PostUpdateEvent event ) {
        if ( event.getEntity() instanceof UpdateListener ) {
            ( (UpdateListener) event.getEntity() ).onUpdate();
        }
    }
}
