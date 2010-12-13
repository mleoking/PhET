package edu.colorado.phet.website.tests.redirections;

import java.net.HttpURLConnection;

public class Hit {
    private final int code;
    private final String location;
    private final Hit child;

    Hit( int code ) {
        this.code = code;
        this.location = null;
        this.child = null;
    }

    Hit( int code, String location ) {
        this.code = code;
        this.location = location;
        this.child = null;
    }

    Hit( int code, String location, Hit child ) {
        this.code = code;
        this.location = location;
        this.child = child;
    }

    public int getCode() {
        return code;
    }

    public String getLocation() {
        return location;
    }

    public Hit getChild() {
        return child;
    }

    public boolean is404() {
        if ( code == 404 ) {
            return true;
        }
        else if ( child == null ) {
            return false;
        }
        else {
            return child.is404();
        }
    }

    public boolean isOk() {
        if ( code == HttpURLConnection.HTTP_OK || code == HttpURLConnection.HTTP_NOT_MODIFIED ) {
            return true;
        }
        else if ( child == null ) {
            return false;
        }
        else {
            return child.isOk();
        }
    }

    @Override
    public String toString() {
        String ret = code + " ";
        if ( code == HttpURLConnection.HTTP_MOVED_PERM || code == HttpURLConnection.HTTP_MOVED_TEMP ) {
            ret += "[" + location + "] ";
        }
        if ( child != null ) {
            ret += child.toString();
        }
        return ret;
    }
}
