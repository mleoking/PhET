package edu.colorado.phet.densityjava.view.d3;

import com.acarter.iwave.jme.InteractiveWater;
import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Box;
import com.jme.scene.state.RenderState;
import edu.colorado.phet.densityjava.model.DensityModel;
import edu.colorado.phet.densityjava.model.Block;
import edu.colorado.phet.densityjava.model.Water;

import java.util.Random;

public interface WaterSurface {
    void simpleUpdate(float tpf);

    public class Motionless implements WaterSurface {
        public Motionless(final DensityModel model, final Water object, WaterSurfaceEnvironment waterSurfaceEnvironment) {
            final Box water = new Box("pool", new Vector3f((float) model.getSwimmingPool().getCenterX(), (float) model.getSwimmingPool().getCenterY(), (float) model.getSwimmingPool().getCenterZ()),
                    (float) object.getWidth() / 2, (float) object.getHeight() / 2, (float) object.getDepth() / 2);

            object.addListener(new DensityModel.RectangularObject.Adapter() {
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

    public class RippleSurface implements WaterSurface {
        private InteractiveWater water;
        private DensityModel model;

        public RippleSurface(final DensityModel model, WaterSurfaceEnvironment waterSurfaceEnvironment) {
            this.model = model;
            water = new InteractiveWater("Water", 100);
            model.getWater().addListener(new DensityModel.RectangularObject.Adapter() {
                public void modelChanged() {
                    water.setLocalTranslation(0, (float) model.getWater().getMaxY(), -(float) model.getSwimmingPool().getDepth());
                }
            });
            water.setLocalTranslation(0, (float) model.getWater().getMaxY(), -(float) model.getSwimmingPool().getDepth());
            water.setLocalScale(new Vector3f((float) model.getSwimmingPool().getWidth() / 100, 1 / 100f, (float) (model.getSwimmingPool().getDepth() / 100.0f)));
            water.setModelBound(new BoundingBox());
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

            if (water != null) {
                for (int i = 0; i < model.getBlockCount(); i++) {
                    Block block = model.getBlock(i);
                    if (block.getVerticalRange().contains(model.getWater().getMaxY()) && block.getSpeed() > 2) {
                        double xMin = block.getX();
                        double yMin = block.getZ();

                        double xMax = block.getMaxX();
                        double yMax = block.getMaxY();

                        int xIntMin = (int) (xMin * 100 / model.getSwimmingPool().getWidth());
                        int yIntMin = (int) (-yMin * 100 / model.getSwimmingPool().getDepth());

                        int xIntMax = (int) (xMax * 100 / model.getSwimmingPool().getWidth());
                        int yIntMax = (int) (-yMax * 100 / model.getSwimmingPool().getDepth());
//                    System.out.println("x = " + x + ", y=" + y + ", xInt=" + xInt + ", yInt=" + yInt+", vel="+block.getSpeed());
//                    System.out.println("xIntMin = " + xIntMin + ", xMAx=" + xIntMax + ", yintmin=" + yIntMin + ", yintmax=" + yIntMax);
                        Random random = new Random();
                        for (int x = xIntMin; x <= xIntMax; x++) {
                            for (int y = yIntMax; y <= yIntMin; y++) {
                                if (random.nextDouble() < 0.1)
                                    if (x == xIntMin || x == xIntMax || y == yIntMin || y == yIntMax)
                                        water.dabSomePaint(x, y);
                            }
                        }
                    }

                }
                water.update(tpf);
            }
        }
    }
}
