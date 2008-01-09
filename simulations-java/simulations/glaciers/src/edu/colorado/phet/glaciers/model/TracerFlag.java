package edu.colorado.phet.glaciers.model;

import java.awt.geom.Point2D;


public class TracerFlag extends AbstractTool {

    public TracerFlag( Point2D position ) {
        super( position );
    }
    
    public void cleanup() {
        super.cleanup();
    }
    
    protected void handlePositionChanged() {
        //XXX
    }

    protected void handleClockTimeChanged() {
        //XXX calculate new position, call setPosition
    }

}
