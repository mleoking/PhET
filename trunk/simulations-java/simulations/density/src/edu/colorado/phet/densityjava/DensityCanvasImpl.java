package edu.colorado.phet.densityjava;

import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.system.DisplaySystem;
import edu.colorado.phet.densityjava.model.DensityModel;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: May 15, 2009
 * Time: 10:54:05 AM
 * To change this template use File | Settings | File Templates.
 */
public class DensityCanvasImpl extends BasicCanvasImpl {
    public DensityCanvasImpl(int width, int height, DisplaySystem display, Component component, DensityModel model) {
        super(width, height, display, component);
        RectNode rectNode = new RectNode(model.getSwimmingPool());
        rootNode.attachChild(rectNode);
    }

    private class RectNode extends Node {
        private DensityModel.RectangularObject object;

        private RectNode(DensityModel.RectangularObject object) {
            this.object = object;
            Box box = new Box();
        }
    }
}
