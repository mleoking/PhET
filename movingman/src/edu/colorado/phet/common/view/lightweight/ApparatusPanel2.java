/** Sam Reid*/
package edu.colorado.phet.common.view.lightweight;

import edu.colorado.phet.common.view.ApparatusPanel;

/**
 * User: Sam Reid
 * Date: Sep 10, 2004
 * Time: 8:09:18 AM
 * Copyright (c) Sep 10, 2004 by Sam Reid
 */
public class ApparatusPanel2 extends ApparatusPanel {
    public void addLightweightGraphic( LightweightShapeGraphic shapeGraphic, int layer ) {
        HeavyweightGraphic hg = new HeavyweightGraphic( shapeGraphic, this );//this should be automated in ApparatusPanel.add(Lightweight...)
        addGraphic( hg, layer );
    }
}
