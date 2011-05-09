// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.view.piccolo;

import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.circuitconstructionkit.model.components.Branch;
import edu.colorado.phet.circuitconstructionkit.model.components.Bulb;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.lifelike.BulbComponentNode;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.lifelike.FilamentNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PAffineTransform;

import javax.swing.*;
import java.awt.*;

/*
To alter the bulb size,
 
 See CCKPiccoloModule's Bulb.setHeightScale( 0.5*0.75 );
 BulbComponentNode.scale
 and BulbNode's brighty-line stroke width scale
 */

public class TotalBulbComponentNode extends BranchNode {
    private Bulb bulb;
    private FilamentNode filamentNode;
    private BulbComponentNode bulbComponentNode;

    public TotalBulbComponentNode(CCKModel cckModel, Bulb bulb, JComponent component, CCKModule module) {
        this.bulb = bulb;
        filamentNode = new FilamentNode(bulb.getFilament(), this);
        bulbComponentNode = new BulbComponentNode(cckModel, bulb, component, module);
        addChild(bulbComponentNode);
        addChild(filamentNode);
    }

    protected void removeFilamentNode() {
        removeChild(filamentNode);
    }

    public BulbComponentNode getBulbComponentNode() {
        return bulbComponentNode;
    }

    public Branch getBranch() {
        return bulb;
    }

    public void delete() {
        filamentNode.delete();
        bulbComponentNode.delete();
    }

    public Shape getClipShape(PNode frame) {
        Shape conductorShape = getBulbComponentNode().getBulbNode().getCoverShapeOnFilamentSide();
        PAffineTransform a = getBulbComponentNode().getBulbNode().getLocalToGlobalTransform(null);
        PAffineTransform b = frame.getGlobalToLocalTransform(null);
        conductorShape = a.createTransformedShape(conductorShape);
        conductorShape = b.createTransformedShape(conductorShape);
        return conductorShape;
    }

    public Bulb getBulb() {
        return bulb;
    }
}
