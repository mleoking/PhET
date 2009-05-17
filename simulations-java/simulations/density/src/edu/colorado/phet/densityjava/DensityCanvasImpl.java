package edu.colorado.phet.densityjava;

import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.intersection.BoundingPickResults;
import com.jme.intersection.PickData;
import com.jme.intersection.PickResults;
import com.jme.light.PointLight;
import com.jme.math.Matrix3f;
import com.jme.math.Ray;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jmex.font3d.Font3D;
import com.jmex.font3d.Text3D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.densityjava.model.DensityModel;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Point2D;

public class DensityCanvasImpl extends BasicCanvasImpl {
    private DensityModel model;
    private DisplaySystem display;
    private Component component;
    private PickData picked = null;
    private Point2D pickPt;

    public DensityCanvasImpl(int width, int height, DisplaySystem display, Component component, DensityModel model) {
        super(width, height, display, component);
        this.model = model;
        this.display = display;
        this.component = component;
    }

    public void simpleSetup() {
        super.simpleSetup();
        cam.setLocation(new Vector3f(5, 7f, 9.5f));
        cam.setDirection(new Vector3f(-0.0f, -0.3f, -1f).normalize());
        cam.setUp(new Vector3f(0, 0.9f, -0.3f));

        rootNode.attachChild(getPoolNode(model.getSwimmingPool()));
        rootNode.attachChild(getWaterNode(model.getWater()));

        //Handle adding block nodes
        for (int i = 0; i < model.getBlockCount(); i++) {
            doAddBlock(model.getBlock(i));
        }
        model.addListener(new DensityModel.Adapter() {
            public void blockAdded(DensityModel.Block block) {
                doAddBlock(block);
            }
        });

        //Handle adding scale nodes
        for (int i = 0; i < model.getScaleCount(); i++) {
            doAddScale(model.getScale(i));
        }
        model.addListener(new DensityModel.Adapter() {
            public void scaleAdded(DensityModel.Scale scale) {
                doAddScale(scale);
            }
        });


        rootNode.attachChild(new CutawayEarthNode(model));
        rootNode.attachChild(new GrassNode(model));


        //For debugging locations
//        rootNode.attachChild(new Sphere("sphere", 10, 10, 0.5f));
//        rootNode.attachChild(new Sphere("sphere", new Vector3f((float) model.getSwimmingPool().getCenterX(), (float) model.getSwimmingPool().getCenterY(), (float) model.getSwimmingPool().getCenterZ()), 10, 10, 1));
//        rootNode.attachChild(new Sphere("sphere", new Vector3f(10, 0, 0), 10, 10, 1));
//        rootNode.attachChild(new Sphere("sphere", new Vector3f(10, 5, 0), 10, 10, 1));

//        FirstPersonHandler firstPersonHandler = new FirstPersonHandler(cam, 50, 1);
//        input.addToAttachedHandlers(firstPersonHandler);

        setupLight();

        addMouseHandling();
    }

    static class ScaleNode extends Node {
        public ScaleNode(DensityModel.Scale scale) {
            Quad quad = new Quad("name");
            quad.setLocalTranslation((float) (quad.getWidth() / 2 + scale.getX()), (float) (quad.getHeight() / 2 + scale.getY()), 0);
            attachChild(quad);

            Font3D font = new Font3D(new PhetFont(), 0.001f, false, true, false);
            Text3D tt = font.createText("120.0 kg", 12f, 0);
            tt.setRenderState(Text.getDefaultFontTextureState());
            tt.setLocalTranslation((float) (quad.getWidth() / 2 + scale.getX()), (float) (quad.getHeight() / 2 + scale.getY()), 0);
            float sx=0.3f;
            tt.setLocalScale(new Vector3f(sx, sx, 0.01f));
            attachChild(tt);

            //Todo: maybe performance and quality would be better to draw directly on the camera
            //in the approximate screen coordinates
//            Text text = new Text("my.text", "100.0 kg");
//            text.setLocalTranslation(5, 5, 0);
//
//            text.setCullHint(Spatial.CullHint.Never);
//            text.setRenderState(Text.getDefaultFontTextureState());
//            text.setRenderState(Text.getFontBlend());
//            text.setTextColor(ColorRGBA.gray);
//            attachChild(text);
//            attachChild(quad);
//            TextureState ts = renderer.createTextureState();
//            Texture t0 = TextureManager.loadTexture(getClass().getClassLoader().getResource(
//                    "density/images/wall.jpg"),
//                    Texture.MinificationFilter.Trilinear,
//                    Texture.MagnificationFilter.Bilinear);
//            t0.setWrap(Texture.WrapMode.Repeat);
//            ts.setTexture(t0);
        }
    }

    private void doAddScale(DensityModel.Scale scale) {
        System.out.println("DensityCanvasImpl.doAddScale");
        final ScaleNode child = new ScaleNode(scale);
        rootNode.attachChild(child);
        scale.addListener(new DensityModel.RectangularObject.Adapter() {
            public void blockRemoving() {
                rootNode.detachChild(child);
            }
        });
    }

    private void doAddBlock(DensityModel.Block block) {
        System.out.println("DensityCanvasImpl.doAddBlock");
        final RectNode child = new RectNode(block);
        rootNode.attachChild(child);
        block.addListener(new DensityModel.RectangularObject.Adapter() {
            public void blockRemoving() {
                rootNode.detachChild(child);
            }
        });
    }

    private Spatial getPoolNode(DensityModel.SwimmingPool swimmingPool) {
        Node poolNode = new Node();
        float r = (float) (255 / 255.0);
        float g = (float) (255 / 255.0);
        float b = (float) (255 / 255.0);
        MaterialState materialState = display.getRenderer().createMaterialState();
        float opacityAmount = 0.5f;
        materialState.setAmbient(new ColorRGBA(0, 0, 0, opacityAmount));
        materialState.setDiffuse(new ColorRGBA(r, g, b, opacityAmount));
        materialState.setSpecular(new ColorRGBA(1.0f, 1.0f, 1.0f, opacityAmount));
        materialState.setShininess(128.0f);
        materialState.setEmissive(new ColorRGBA(0.0f, 0.0f, 0.1f, opacityAmount));
        materialState.setEnabled(true);
        materialState.setMaterialFace(MaterialState.MaterialFace.FrontAndBack);
        poolNode.setRenderState(materialState);
        Quad quad = new Quad("pool back", (float) swimmingPool.getWidth(), (float) swimmingPool.getHeight());
        quad.setLocalTranslation((float) swimmingPool.getWidth() / 2, (float) swimmingPool.getHeight() / 2, (float) (-swimmingPool.getDepth() - 1E-2));
        poolNode.attachChild(quad);
        return poolNode;
    }

    private void addMouseHandling() {
        component.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                PickResults pickResults = getPickResults();
                for (int i = 0; i < pickResults.getNumber(); i++) {
                    if (i == 0 && pickResults.getPickData(i).getTargetMesh() instanceof ObjectBox) {
                        picked = pickResults.getPickData(i);
                        pickPt = ((ObjectBox) picked.getTargetMesh()).getObject().getPoint2D();
                    }
                }
            }

            public void mouseReleased(MouseEvent e) {
                if (picked != null && picked.getTargetMesh() instanceof ObjectBox) {
                    ObjectBox ob = (ObjectBox) picked.getTargetMesh();
                    ob.getObject().setDragging(false);
                }
                picked = null;
            }
        });
        component.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                if (getPickResults().getNumber() > 0) {
                    component.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    component.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
            }

            public void mouseDragged(MouseEvent e) {
                //To change body of implemented methods use File | Settings | File Templates.
                if (picked != null) {
                    double dist = picked.getDistance();
                    Ray initRay = picked.getRay();
                    Ray finalRay = getMouseRay();
                    Vector3f initDir = initRay.getDirection().normalize().mult((float) dist);
                    Vector3f finalDir = finalRay.getDirection().normalize().mult((float) dist);
                    Vector3f initPt = new Vector3f(initRay.getOrigin().getX() + initDir.getX(),
                            initRay.getOrigin().getY() + initDir.getY(),
                            initRay.getOrigin().getZ() + initDir.getZ());
                    Vector3f finalPt = new Vector3f(finalRay.getOrigin().getX() + finalDir.getX(),
                            finalRay.getOrigin().getY() + finalDir.getY(),
                            finalRay.getOrigin().getZ() + finalDir.getZ());
                    double dx = finalPt.getX() - initPt.getX();
                    double dy = finalPt.getY() - initPt.getY();
                    if (picked.getTargetMesh() instanceof ObjectBox) {
                        ObjectBox ob = (ObjectBox) picked.getTargetMesh();
                        ob.getObject().setPosition2D(pickPt.getX() + dx, pickPt.getY() + dy);
                        ob.getObject().setVelocity(0);
                        ob.getObject().setDragging(true);
                    }
                }
            }
        });
    }

    private PickResults getPickResults() {
        PickResults pickResults = new BoundingPickResults();
        pickResults.setCheckDistance(true);
        rootNode.findPick(getMouseRay(), pickResults);
        return pickResults;
    }

    private void setupLight() {
        LightState lightState = display.getRenderer().createLightState();
        lightState.setEnabled(true);

        rootNode.setRenderState(lightState);
        lightState.detachAll();

        // our light
        final PointLight light = new PointLight();
        light.setDiffuse(ColorRGBA.white);
        light.setSpecular(ColorRGBA.white);
        light.setLocation(new Vector3f(20.0f, 20.0f, 30.0f));
        light.setEnabled(true);

        // attach the light to a lightState
        lightState.attach(light);
    }

    private TriMesh getWaterNode(final DensityModel.Water object) {
        final Box water = new Box("pool", new Vector3f((float) model.getSwimmingPool().getCenterX(), (float) model.getSwimmingPool().getCenterY(), (float) model.getSwimmingPool().getCenterZ()),
                (float) object.getWidth() / 2, (float) object.getHeight() / 2, (float) object.getDepth() / 2);
        double a = object.getDistanceToTopOfPool() / 2;
        water.setLocalTranslation(0, -(float) a, 0);
        water.updateModelBound();
        object.addListener(new DensityModel.RectangularObject.Adapter() {
            public void modelChanged() {
                water.updateGeometry(new Vector3f((float) model.getSwimmingPool().getCenterX(), (float) model.getSwimmingPool().getCenterY(), (float) model.getSwimmingPool().getCenterZ()),
                        (float) object.getWidth() / 2, (float) object.getHeight() / 2, (float) object.getDepth() / 2);
                double a = object.getDistanceToTopOfPool() / 2;
                water.setLocalTranslation(0, -(float) a, 0);
                water.updateModelBound();
            }
        });

//        pool.setLocalTranslation(pool.getLocalTranslation().add((float) object.getWidth() / 2, (float) object.getHeight() / 2, -(float) object.getDepth()));

        // the sphere material taht will be modified to make the sphere
        // look opaque then transparent then opaque and so on
        MaterialState materialState = display.getRenderer().createMaterialState();
        float opacityAmount = 0.4f;
        materialState.setAmbient(new ColorRGBA(0.0f, 0.0f, 0.0f, opacityAmount));
        materialState.setDiffuse(new ColorRGBA(0.1f, 0.5f, 0.8f, opacityAmount));
        materialState.setSpecular(new ColorRGBA(1.0f, 1.0f, 1.0f, opacityAmount));
        materialState.setShininess(128.0f);
        materialState.setEmissive(new ColorRGBA(0.0f, 0.0f, 0.0f, opacityAmount));
        materialState.setEnabled(true);

        // IMPORTANT: this is used to handle the internal sphere faces when
        // setting them to transparent, try commenting this line to see what
        // happens
        materialState.setMaterialFace(MaterialState.MaterialFace.FrontAndBack);

        water.setRenderState(materialState);
        water.updateRenderState();

        // to handle transparency: a BlendState
        // an other tutorial will be made to deal with the possibilities of this
        // RenderState
        final BlendState alphaState = DisplaySystem.getDisplaySystem().getRenderer().createBlendState();
        alphaState.setBlendEnabled(true);
        alphaState.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
        alphaState.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
        alphaState.setTestEnabled(true);
        alphaState.setTestFunction(BlendState.TestFunction.GreaterThan);
        alphaState.setEnabled(true);

        water.setRenderState(alphaState);
        water.updateRenderState();

        // IMPORTANT: since the sphere will be transparent, place it
        // in the transparent render queue!
        water.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
        return water;
    }

    class ObjectBox extends Box {
        private DensityModel.Block object;

        public ObjectBox(DensityModel.Block object, String name, Vector3f vector3f, float v, float v1, float v2) {
            super(name, vector3f, v, v1, v2);
            this.object = object;
        }

        public DensityModel.Block getObject() {
            return object;
        }
    }

    private class RectNode extends Node {
        private DensityModel.RectangularObject object;

        private RectNode(final DensityModel.Block object) {
            this.object = object;

            final ObjectBox mesh = new ObjectBox(object, object.getName(), new Vector3f((float) object.getCenterX(), (float) object.getCenterY(), (float) object.getCenterZ()),
                    (float) object.getWidth() / 2, (float) object.getHeight() / 2, (float) object.getDepth() / 2);
            mesh.setLocalTranslation(mesh.getLocalTranslation().set(0, 0, -1E-2f));
            mesh.setModelBound(new BoundingBox());

            mesh.updateModelBound();

            MaterialState materialState = display.getRenderer().createMaterialState();
            float opacityAmount = 0.7f;
            float r = object.getFaceColor().getRed() / 255f;
            float g = object.getFaceColor().getGreen() / 255f;
            float b = object.getFaceColor().getBlue() / 255f;
            materialState.setAmbient(new ColorRGBA(0.2f, 0.2f, 0.1f, opacityAmount));
            materialState.setDiffuse(new ColorRGBA(r, g, b, opacityAmount));
            materialState.setSpecular(new ColorRGBA(1.0f, 1.0f, 1.0f, opacityAmount));
            materialState.setShininess(128.0f);
            materialState.setEmissive(new ColorRGBA(0.0f, 0.0f, 0.1f, opacityAmount));
            materialState.setEnabled(true);

            setRenderState(materialState);


            object.addListener(new DensityModel.RectangularObject.Adapter() {
                public void modelChanged() {
                    mesh.setCenter(new Vector3f((float) object.getCenterX(), (float) object.getCenterY(), (float) object.getCenterZ()));
                    mesh.updateGeometry();
                    mesh.updateModelBound();
                    mesh.updateRenderState();
                    //todo update dimensions
                }
            });

            TextureState ts = display.getRenderer().createTextureState();
            Texture t0 = TextureManager.loadTexture(getClass().getClassLoader().getResource(
                    "density/images/wall.jpg"),
                    Texture.MinificationFilter.Trilinear,
                    Texture.MagnificationFilter.Bilinear);
            t0.setWrap(Texture.WrapMode.Repeat);
            ts.setTexture(t0);

            setRenderState(ts);
            attachChild(mesh);//order matters for this call?
        }
    }

    private class CutawayEarthNode extends Node {
        public CutawayEarthNode(DensityModel model) {

            float r = (float) (201 / 255.0);
            float g = (float) (146 / 255.0);
            float b = (float) (81 / 255.0);
            MaterialState materialState = display.getRenderer().createMaterialState();
            float opacityAmount = 0.8f;
            materialState.setAmbient(new ColorRGBA(0.2f, 0.2f, 0.1f, opacityAmount));
            materialState.setDiffuse(new ColorRGBA(r, g, b, opacityAmount));
            materialState.setSpecular(new ColorRGBA(1.0f, 1.0f, 1.0f, opacityAmount));
            materialState.setShininess(128.0f);
            materialState.setEmissive(new ColorRGBA(0.0f, 0.0f, 0.1f, opacityAmount));
            materialState.setEnabled(true);

            setRenderState(materialState);


            float quadWidth = 5;
            float quadHeight = 10;
            final float poolHeight = (float) model.getSwimmingPool().getHeight();
            final float poolWidth = (float) model.getSwimmingPool().getWidth();
            {
                Quad quad = new Quad("cutaway left side", quadWidth, quadHeight);
                quad.setLocalTranslation(-quadWidth / 2, -quadHeight / 2 + poolHeight, 0);
                attachChild(quad);
            }
            {
                Quad quad = new Quad("cutaway right side", quadWidth, quadHeight);
                quad.setLocalTranslation(quadWidth / 2 + poolWidth, -quadHeight / 2 + poolHeight, 0);
                attachChild(quad);
            }
            {
                Quad quad = new Quad("cutaway lower", poolWidth, quadHeight);
                quad.setLocalTranslation(poolWidth / 2, -quadHeight / 2, 0);
//                final MaterialState state = renderer.createMaterialState();
//                quad.setRenderState(state);
                attachChild(quad);
            }
        }
    }

    private class GrassNode extends Node {
        public GrassNode(DensityModel model) {

            float r = (float) (90 / 255.0);
            float g = (float) (158 / 255.0);
            float b = (float) (48 / 255.0);
            MaterialState materialState = display.getRenderer().createMaterialState();
            float opacityAmount = 0.5f;
            materialState.setAmbient(new ColorRGBA(0, 0, 0, opacityAmount));
            materialState.setDiffuse(new ColorRGBA(r, g, b, opacityAmount));
            materialState.setSpecular(new ColorRGBA(1.0f, 1.0f, 1.0f, opacityAmount));
            materialState.setShininess(128.0f);
            materialState.setEmissive(new ColorRGBA(0.0f, 0.0f, 0.1f, opacityAmount));
            materialState.setEnabled(true);
            materialState.setMaterialFace(MaterialState.MaterialFace.FrontAndBack);
            setRenderState(materialState);


            float quadWidth = 5;
            float quadHeight = 10;
            final float poolHeight = (float) model.getSwimmingPool().getHeight();
            final float poolWidth = (float) model.getSwimmingPool().getWidth();
            final float poolDepth = (float) model.getSwimmingPool().getDepth();
            {
                Quad quad = new Quad("cutaway left side", quadWidth, quadHeight);
                Matrix3f m = new Matrix3f();
                m.fromAngleAxis((float) (Math.PI / 2), new Vector3f(1, 0, 0));

                quad.setLocalTranslation(-quadWidth / 2, poolHeight, -quadHeight / 2);
                quad.setLocalRotation(m);
                attachChild(quad);
            }
            {
                Quad quad = new Quad("cutaway right side", quadWidth, quadHeight);
                Matrix3f m = new Matrix3f();
                m.fromAngleAxis((float) (Math.PI / 2), new Vector3f(1, 0, 0));

                quad.setLocalTranslation(quadWidth / 2 + poolWidth, poolHeight, -quadHeight / 2);
                quad.setLocalRotation(m);
                attachChild(quad);
            }
            {
                Quad quad = new Quad("cutaway lower grass", poolWidth, quadHeight);
                quad.setLocalTranslation(poolWidth / 2, poolHeight, -poolDepth - quadHeight / 2);
                Matrix3f m = new Matrix3f();
                m.fromAngleAxis((float) (Math.PI / 2), new Vector3f(1, 0, 0));
                quad.setLocalRotation(m);
                attachChild(quad);
            }

            setModelBound(new BoundingBox());

            updateModelBound();

            updateRenderState();
        }
    }

}