package edu.colorado.phet.glaciers.model;

import java.awt.geom.Point2D;


public class TracerFlag extends AbstractTool {

    private Glacier _glacier;
    
    public TracerFlag( Point2D position, Glacier glacier ) {
        super( position );
        _glacier = glacier;
    }
    
    public void cleanup() {
        super.cleanup();
    }

    //XXX should this be replaced with a GlacierListener?
    protected void handleTimeChanged() {
        //XXX calculate new position, call setPosition
    }

}
