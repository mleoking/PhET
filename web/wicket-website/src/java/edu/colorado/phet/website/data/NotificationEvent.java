package edu.colorado.phet.website.data;

import java.io.Serializable;
import java.util.Date;

import edu.colorado.phet.website.data.util.IntId;
import edu.colorado.phet.website.notification.NotificationEventType;

public class NotificationEvent implements Serializable, IntId {
    private int id;
    private NotificationEventType type;
    private String data;
    private Date createdAt;

    @Override
    public int hashCode() {
        return ( id + 47 ) * ( id - 7 ) * ( id % 7 );
    }

    @Override
    public boolean equals( Object o ) {
        if( this == o ) {
            return true;
        }
        return ( o instanceof NotificationEvent ) && id == ( (NotificationEvent) o ).id;
    }

    @Override
    public String toString() {
        return getType().toString( getData() );
    }

    public NotificationEvent() {
    }

    public int getId() {
        return id;
    }

    public void setId( int id ) {
        this.id = id;
    }

    public NotificationEventType getType() {
        return type;
    }

    public void setType( NotificationEventType type ) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData( String data ) {
        this.data = data;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt( Date createdAt ) {
        this.createdAt = createdAt;
    }
}