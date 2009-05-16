package edu.colorado.phet.densityjava;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.densityjava.model.DensityModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;

import java.awt.geom.Point2D;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: May 15, 2009
 * Time: 9:15:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class DensityCanvas2D extends PhetPCanvas {
    static class BlockNode extends PNode {
        private DensityModel.RectangularObject rectangularObject;
        private ModelViewTransform2D modelViewTransform2D;
        private PhetPPath face;
        private PhetPPath topFace;
        private PhetPPath rightFace;

        public BlockNode(DensityModel.RectangularObject rectangularObject, ModelViewTransform2D modelViewTransform2D) {
            this.rectangularObject = rectangularObject;
            this.modelViewTransform2D = modelViewTransform2D;
            face = new PhetPPath(rectangularObject.getFaceColor());
            addChild(face);

            topFace = new PhetPPath(rectangularObject.getTopFaceColor());
            addChild(topFace);

            rightFace = new PhetPPath(rectangularObject.getRightFaceColor());
            addChild(rightFace);

            rectangularObject.addListener(new DensityModel.RectangularObject.Listener() {
                public void modelChanged() {
                    updateShapes();
                }
            });
            updateShapes();
        }

        private void updateShapes() {
            face.setPathTo(modelViewTransform2D.createTransformedShape(rectangularObject.getFrontFace()));
            topFace.setPathTo(modelViewTransform2D.createTransformedShape(rectangularObject.getTopFace()));
            rightFace.setPathTo(modelViewTransform2D.createTransformedShape(rectangularObject.getRightFace()));
        }
    }

    static class DraggableBlockNode extends BlockNode {

        public DraggableBlockNode(final DensityModel.RectangularObject rectangularObject, final ModelViewTransform2D modelViewTransform2D) {
            super(rectangularObject, modelViewTransform2D);
            addInputEventListener(new CursorHandler());
            addInputEventListener(new PBasicInputEventHandler() {
                @Override
                public void mouseDragged(PInputEvent event) {
                    super.mouseDragged(event);    //To change body of overridden methods use File | Settings | File Templates.
                    Point2D pt = new Point2D.Double(event.getDeltaRelativeTo(getParent()).getWidth(), event.getDeltaRelativeTo(getParent()).getHeight());
                    rectangularObject.translate(modelViewTransform2D.viewToModelDifferential(pt));
                }
            });
        }
    }

    public DensityCanvas2D(DensityModel model) {

        setWorldTransformStrategy(new CenteringBoxStrategy(this, new PDimension(800, 800)));
        ModelViewTransform2D modelViewTransform2D = new ModelViewTransform2D(new PBounds(-1, -1, 12, 12), new PBounds(0, 0, 800, 800), true);


        addWorldChild(new DraggableBlockNode(model.getBlock1(), modelViewTransform2D));
        addWorldChild(new DraggableBlockNode(model.getBlock2(), modelViewTransform2D));
        addWorldChild(new UndraggableBlockNode(model.getWater(), modelViewTransform2D));
    }

    private class UndraggableBlockNode extends BlockNode {
        public UndraggableBlockNode(DensityModel.RectangularObject object, ModelViewTransform2D modelViewTransform2D) {
            super(object, modelViewTransform2D);
            setPickable(false);
            setChildrenPickable(false);
        }
    }
}
