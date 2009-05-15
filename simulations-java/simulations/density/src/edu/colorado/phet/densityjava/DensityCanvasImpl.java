package edu.colorado.phet.densityjava;

import com.jme.bounding.BoundingSphere;
import com.jme.intersection.BoundingPickResults;
import com.jme.intersection.PickData;
import com.jme.intersection.PickResults;
import com.jme.light.PointLight;
import com.jme.math.Ray;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Box;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.MaterialState;
import com.jme.system.DisplaySystem;
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
        super.simpleSetup();    //To change body of overridden methods use File | Settings | File Templates.
        cam.setLocation(cam.getLocation().add(0, 5, +10));
//        rootNode.attachChild(new RectNode(model.getSwimmingPool()));
        rootNode.attachChild(new RectNode(model.getBlock1()));
        rootNode.attachChild(new RectNode(model.getBlock2()));

        setupLight();

        // our sphere
        final TriMesh pool = getPoolNode(model.getSwimmingPool());
        rootNode.attachChild(pool);

        component.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                System.out.println("DensityCanvasImpl.mousePressed");

                PickResults pickResults = getPickResults();
                for (int i = 0; i < pickResults.getNumber(); i++) {
                    if (i == 0) {
                        picked = pickResults.getPickData(i);
                        ObjectBox ob = (ObjectBox) picked.getTargetMesh();
                        pickPt = ob.getObject().getPoint2D();
                    }
                    System.out.println("pickResults.getPickData(i).getTargetMesh().getName() = " + pickResults.getPickData(i).getTargetMesh().getName());
                }
            }

            public void mouseReleased(MouseEvent e) {
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
                    System.out.println("DensityCanvasImpl.mouseDragged on: " + picked);
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
//                    System.out.println("pickRay=" + initRay + ", finalRay=" + finalRay + ", dx = " + dx + ", dy=" + dy);
                        ob.getObject().setPosition2D(pickPt.getX() + dx, pickPt.getY() + dy);
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
        light.setLocation(new Vector3f(100.0f, 100.0f, 100.0f));
        light.setEnabled(true);

        // attach the light to a lightState
        lightState.attach(light);
    }

    private TriMesh getPoolNode(DensityModel.SwimmingPool object) {
        final TriMesh pool = new Box("pool", new Vector3f((float) model.getSwimmingPool().getCenterX(), (float) model.getSwimmingPool().getCenterY(), (float) model.getSwimmingPool().getCenterZ()), (float) object.getWidth(), (float) object.getHeight(), (float) object.getDepth());

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

        pool.setRenderState(materialState);
        pool.updateRenderState();

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

        pool.setRenderState(alphaState);
        pool.updateRenderState();

        // IMPORTANT: since the sphere will be transparent, place it
        // in the transparent render queue!
        pool.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
        return pool;
    }

    class ObjectBox extends Box {
        private DensityModel.RectangularObject object;

        public ObjectBox(DensityModel.RectangularObject object, String name, Vector3f vector3f, float v, float v1, float v2) {
            super(name, vector3f, v, v1, v2);
            this.object = object;
        }

        public DensityModel.RectangularObject getObject() {
            return object;
        }
    }

    private class RectNode extends Node {
        private DensityModel.RectangularObject object;

        private RectNode(final DensityModel.RectangularObject object) {
            this.object = object;

            final ObjectBox mesh = new ObjectBox(object, object.getName(), new Vector3f((float) object.getCenterX(), (float) object.getCenterY(), (float) object.getCenterZ()), (float) object.getWidth(), (float) object.getHeight(), (float) object.getDepth());
            mesh.setModelBound(new BoundingSphere());
            mesh.getLocalTranslation().set(-1, 0, 0);
            mesh.updateModelBound();

            MaterialState materialState = display.getRenderer().createMaterialState();
            float opacityAmount = 0.8f;
            materialState.setAmbient(new ColorRGBA(0.2f, 0.2f, 0.1f, opacityAmount));
            materialState.setDiffuse(new ColorRGBA(0.1f, 0.5f, 0.8f, opacityAmount));
            materialState.setSpecular(new ColorRGBA(1.0f, 1.0f, 1.0f, opacityAmount));
            materialState.setShininess(128.0f);
            materialState.setEmissive(new ColorRGBA(0.0f, 0.0f, 0.1f, opacityAmount));
            materialState.setEnabled(true);

            mesh.setRenderState(materialState);
            attachChild(mesh);

            object.addListener(new DensityModel.RectangularObject.Listener() {
                public void modelChanged() {
                    mesh.setCenter(new Vector3f((float) object.getCenterX(), (float) object.getCenterY(), (float) object.getCenterZ()));
                    mesh.updateGeometry();
                    mesh.updateModelBound();
                    //todo update dimensions
                }
            });

        }
    }
}