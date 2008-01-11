package edu.colorado.phet.glaciers.model;

import java.awt.geom.Point2D;


public class BoreholeDrill extends AbstractTool {
    
    private final Glacier _glacier;

    public BoreholeDrill( Point2D position, Glacier glacier ) {
        super( position );
        _glacier = glacier;
    }
    
    public void cleanup() {
        super.cleanup();
    }
}
