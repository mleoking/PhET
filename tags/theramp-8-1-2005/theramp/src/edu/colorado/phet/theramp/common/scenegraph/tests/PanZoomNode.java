package edu.colorado.phet.theramp.common.scenegraph.tests;

import edu.colorado.phet.theramp.common.scenegraph.GraphicLayerNode;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Jun 8, 2005
 * Time: 12:24:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class PanZoomNode extends GraphicLayerNode {
    public PanZoomNode() {
        setComposite( true );
//        addKeyHandler(new PanZoomNode.PanZoomKeyHandler());
    }

    public class PanZoomKeyHandler {
    }
}
