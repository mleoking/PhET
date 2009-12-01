package edu.colorado.phet.densityjava.view.d3;

import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Box;
import com.jme.scene.state.RenderState;
import edu.colorado.phet.densityjava.model.DensityModel;
import edu.colorado.phet.densityjava.model.Block;
import edu.colorado.phet.densityjava.model.Water;
import edu.colorado.phet.densityjava.model.RectangularObject;

import java.util.Random;

public interface WaterSurface {
    void simpleUpdate(float tpf);

    public class Motionless implements WaterSurface {
        public Motionless(final DensityModel model, final Water object, WaterSurfaceEnvironment waterSurfaceEnvironment) {
            final Box water = new Box("pool", new Vector3f((float) model.getSwimmingPool().getCenterX(), (float) model.getSwimmingPool().getCenterY(), (float) model.getSwimmingPool().getCenterZ()),
                    (float) object.getWidth() / 2, (float) object.getHeight() / 2, (float) object.getDepth() / 2);

            object.addListener(new RectangularObject.Adapter() {
                public void modelChanged() {

                    {//for the box
                        water.updateGeometry(new Vector3f((float) model.getSwimmingPool().getCenterX(), (float) model.getSwimmingPool().getCenterY(), (float) model.getSwimmingPool().getCenterZ()),
                                (float) object.getWidth() / 2, (float) object.getHeight() / 2, (float) object.getDepth() / 2);
                        double a = object.getDistanceToTopOfPool() / 2;
                        water.setLocalTranslation(0, -(float) a, 0);
                        water.updateModelBound();
                    }
//                {//for the quad
//                    final float w = (float) model.getSwimmingPool().getWidth();
//                    final float h = (float) model.getWater().getMaxY();
//                    water.updateGeometry(w, h);//for the quad
//                    water.setLocalTranslation((float) model.getSwimmingPool().getX() + w / 2, (float) model.getSwimmingPool().getY() + h / 2 + 2E-2f, 0);
//                }

                }
            });

            //                water.updateGeometry(new Vector3f((float) model.getSwimmingPool().getCenterX(), (float) model.getSwimmingPool().getCenterY(), (float) model.getSwimmingPool().getCenterZ()),
//                        (float) object.getWidth() / 2, (float) object.getHeight() / 2, (float) object.getDepth() / 2);
            double a = object.getDistanceToTopOfPool() / 2;
            water.setLocalTranslation(0, -(float) a, 0);
            water.updateModelBound();

            // the sphere material taht will be modified to make the sphere
            // look opaque then transparent then opaque and so on
            water.setRenderState(waterSurfaceEnvironment.getWaterMaterial());
            water.updateRenderState();

            // to handle transparency: a BlendState
            // an other tutorial will be made to deal with the possibilities of this
            // RenderState
            water.setRenderState(waterSurfaceEnvironment.getBlendState());
            water.updateRenderState();

            // IMPORTANT: since the sphere will be transparent, place it
            // in the transparent render queue!
            water.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
            waterSurfaceEnvironment.attachChild(water);
        }

        public void simpleUpdate(float tpf) {
        }
    }

    static interface WaterSurfaceEnvironment {

        RenderState getWaterMaterial();

        RenderState getBlendState();

        void attachChild(Spatial spatial);
    }

}
