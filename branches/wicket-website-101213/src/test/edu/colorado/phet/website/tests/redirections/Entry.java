package edu.colorado.phet.website.tests.redirections;

public class Entry implements Comparable {
    private Request request;
    private Hits hits;

    Entry( Request request, Hits hits ) {
        this.request = request;
        this.hits = hits;
    }

    public Request getRequest() {
        return request;
    }

    public Hits getHits() {
        return hits;
    }

    public int compareTo( Object o ) {
        if ( o instanceof Entry ) {
            return -( new Integer( hits.getCount() ).compareTo( ( (Entry) o ).getHits().getCount() ) );
        }
        else {
            return -1;
        }
    }
}
