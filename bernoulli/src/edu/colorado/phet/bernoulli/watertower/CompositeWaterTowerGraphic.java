package edu.colorado.phet.bernoulli.watertower;

import edu.colorado.phet.bernoulli.valves.HorizontalValveGraphic;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.CompositeGraphic;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Aug 21, 2003
 * Time: 5:43:00 AM
 * Copyright (c) Aug 21, 2003 by Sam Reid
 */
public class CompositeWaterTowerGraphic extends CompositeGraphic {
    public CompositeWaterTowerGraphic( WaterTower tower, ModelViewTransform2d transform, ApparatusPanel target, Color backgroundColor ) {
        WaterTowerGraphic toweritself = new WaterTowerGraphic( tower, transform, target, backgroundColor );
//        VerticalValveGraphic vg = toweritself.getBottomValveGraphic();
        HorizontalValveGraphic hg = toweritself.getRightValveGraphic();
        addGraphic( toweritself, 0 );
//        addGraphic(vg, 1);
        addGraphic( hg, 2 );
    }
}
