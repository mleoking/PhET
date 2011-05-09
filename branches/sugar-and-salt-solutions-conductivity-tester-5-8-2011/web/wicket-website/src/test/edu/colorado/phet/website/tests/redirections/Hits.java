package edu.colorado.phet.website.tests.redirections;

public class Hits {
    private Hit hit;
    private int count = 1;

    Hits( Hit hit ) {
        this.hit = hit;
    }

    public Hit getHit() {
        return hit;
    }

    public int getCount() {
        return count;
    }

    public void increment() {
        count++;
    }
}
