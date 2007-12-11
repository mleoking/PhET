package edu.colorado.phet.rotation.view;

import edu.colorado.phet.common.timeseries.ui.TimeseriesResources;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Created by: Sam
 * Dec 11, 2007 at 6:55:54 AM
 */
public class PauseNode extends PNode {
    public PauseNode() {
        PImage image = new PImage( TimeseriesResources.loadBufferedImage( "icons/java/media/Pause24.gif" ) );
        addChild( image );
    }
}
