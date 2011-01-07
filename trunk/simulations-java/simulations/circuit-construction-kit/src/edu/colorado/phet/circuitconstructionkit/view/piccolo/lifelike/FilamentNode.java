// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.view.piccolo.lifelike;

import edu.colorado.phet.circuitconstructionkit.model.CurrentVoltListener;
import edu.colorado.phet.circuitconstructionkit.model.components.Branch;
import edu.colorado.phet.circuitconstructionkit.model.components.Filament;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.TotalBulbComponentNode;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PPaintContext;

import java.awt.*;
import java.awt.geom.Area;

/**
 * User: Sam Reid
 * Date: Sep 19, 2006
 * Time: 1:50:04 PM
 */

public class FilamentNode extends PhetPNode {
    private Filament filament;
    private TotalBulbComponentNode totalBulbComponentNode;
    private Stroke stroke = new BasicStroke(8 / 80.0f);
    private CurrentVoltListener currentListener = new CurrentVoltListener() {
        public void currentOrVoltageChanged(Branch branch) {
            update();
        }
    };
    private SimpleObserver filamentObserver = new SimpleObserver() {
        public void update() {
            FilamentNode.this.update();
        }
    };

    public FilamentNode(Filament filament, TotalBulbComponentNode totalBulbComponentNode) {
        this.filament = filament;
        this.totalBulbComponentNode = totalBulbComponentNode;
        totalBulbComponentNode.getBulb().addCurrentVoltListener(currentListener);
        filament.addObserver(filamentObserver);
        update();
        setPickable(false);
        setChildrenPickable(false);
    }

    protected void paint(PPaintContext paintContext) {
        Shape origClip = paintContext.getGraphics().getClip();
        Area area = new Area(origClip);
        area.subtract(new Area(totalBulbComponentNode.getClipShape(this)));
        paintContext.pushClip(area);
    }

    protected void paintAfterChildren(PPaintContext paintContext) {
        paintContext.popClip(null);
    }

    private void update() {
        double intensity = totalBulbComponentNode.getBulb().getIntensity();
        PPath path = new PhetPPath(filament.getPath(), stroke, new Color((float) (intensity), (float) (intensity * .4), (float) (intensity * .5)));
        removeAllChildren();
        addChild(path);
    }

    public Filament getFilament() {
        return filament;
    }

    public void delete() {
        totalBulbComponentNode.getBulb().removeCurrentVoltListener(currentListener);
        filament.removeObserver(filamentObserver);
    }
}
