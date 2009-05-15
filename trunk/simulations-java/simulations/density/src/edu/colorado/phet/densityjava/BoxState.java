package edu.colorado.phet.densityjava;

import com.jme.bounding.BoundingBox;
import com.jme.input.MouseInput;
import com.jme.intersection.PickData;
import com.jme.intersection.TrianglePickResults;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Ray;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.util.geom.BufferUtils;

import java.nio.FloatBuffer;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: May 12, 2009
 * Time: 9:32:27 PM
 * To change this template use File | Settings | File Templates.
 */
class BoxState {

    private Quaternion rotQuat;
    private double angle = 0;
    private Vector3f axis;
    private com.jme.scene.shape.Box box;

    private com.jme.scene.Point pointSelection;
    Spatial maggie;
    private com.jme.scene.Line[] selection;


    Node rootNode;
    DisplaySystem display;

    BoxState(DisplaySystem display, Node rootNode) {
        this.display = display;
        this.rootNode = rootNode;
    }

    private void clearPreviousSelections() {
        if (selection != null) {
            for (com.jme.scene.Line line : selection) {
                rootNode.detachChild(line);
            }
        }
    }

    private void createSelectionTriangles(int number) {
        clearPreviousSelections();
        selection = new com.jme.scene.Line[number];
        for (int i = 0; i < selection.length; i++) {
            selection[i] = new com.jme.scene.Line("selected triangle" + i, new Vector3f[4],
                    null, new ColorRGBA[4], null);
            selection[i].setSolidColor(new ColorRGBA(0, 1, 0, 1));
            selection[i].setLineWidth(5);
            selection[i].setAntialiased(true);
            selection[i].setMode(com.jme.scene.Line.Mode.Connected);

            ZBufferState zbs = display.getRenderer().createZBufferState();
            zbs.setFunction(ZBufferState.TestFunction.Always);
            selection[i].setRenderState(zbs);
            selection[i].setLightCombineMode(Spatial.LightCombineMode.Off);

            rootNode.attachChild(selection[i]);
        }

        rootNode.updateGeometricState(0, true);
        rootNode.updateRenderState();
    }

    TrianglePickResults results = new TrianglePickResults() {

        public void processPick() {

            // initialize selection triangles, this can go across multiple
            // target
            // meshes.
            int total = 0;
            for (int i = 0; i < getNumber(); i++) {
                total += getPickData(i).getTargetTris().size();
            }
            createSelectionTriangles(total);
            if (getNumber() > 0) {
                int previous = 0;
                for (int num = 0; num < getNumber(); num++) {
                    PickData pData = getPickData(num);
                    java.util.List<Integer> tris = pData.getTargetTris();
                    TriMesh mesh = (TriMesh) pData.getTargetMesh();

                    for (int i = 0; i < tris.size(); i++) {
                        int triIndex = tris.get(i);
                        Vector3f[] vec = new Vector3f[3];
                        mesh.getTriangle(triIndex, vec);
                        FloatBuffer buff = selection[i + previous]
                                .getVertexBuffer();

                        for (Vector3f v : vec) {
                            v.multLocal(mesh.getWorldScale());
                            mesh.getWorldRotation().mult(v, v);
                            v.addLocal(mesh.getWorldTranslation());
                        }

                        BufferUtils.setInBuffer(vec[0], buff, 0);
                        BufferUtils.setInBuffer(vec[1], buff, 1);
                        BufferUtils.setInBuffer(vec[2], buff, 2);
                        BufferUtils.setInBuffer(vec[0], buff, 3);

                        if (num == 0 && i == 0) {
                            selection[i + previous]
                                    .setSolidColor(new ColorRGBA(1, 0, 0, 1));
                            Vector3f loc = new Vector3f();
                            pData.getRay().intersectWhere(vec[0], vec[1],
                                    vec[2], loc);
                            BufferUtils.setInBuffer(loc, pointSelection
                                    .getVertexBuffer(), 0);
                        }
                    }

                    previous = tris.size();
                }
            }
        }
    };


    public void init(Node rootNode) {
        // Normal Scene setup stuff...
        rotQuat = new Quaternion();
        axis = new Vector3f(1, 1, 0.5f);
        axis.normalizeLocal();

        Vector3f max = new Vector3f(5, 5, 5);
        Vector3f min = new Vector3f(-5, -5, -5);

        box = new com.jme.scene.shape.Box("Box", min, max);
        box.setModelBound(new BoundingBox());
        box.updateModelBound();
        box.setLocalTranslation(new Vector3f(0, 0, -10));
        box.setRenderQueueMode(com.jme.renderer.Renderer.QUEUE_SKIP);
        rootNode.attachChild(box);


        results.setCheckDistance(true);

        pointSelection = new com.jme.scene.Point("selected triangle", new Vector3f[1], null,
                new ColorRGBA[1], null);
        pointSelection.setSolidColor(new ColorRGBA(1, 0, 0, 1));
        pointSelection.setPointSize(10);
        pointSelection.setAntialiased(true);
        ZBufferState zbs = display.getRenderer().createZBufferState();
        zbs.setFunction(ZBufferState.TestFunction.Always);
        pointSelection.setRenderState(zbs);
        pointSelection.setLightCombineMode(Spatial.LightCombineMode.Off);

        rootNode.attachChild(pointSelection);
    }

    public void update(double tpf, Ray mouseRay) {

        // Code for rotating the box... no surprises here.
        if (tpf < 1) {
            angle = angle + (tpf * 25);
            if (angle > 360) {
                angle = 0;
            }
        }
        rotQuat.fromAngleNormalAxis((float) (angle * FastMath.DEG_TO_RAD), axis);
        box.setLocalRotation(rotQuat);

//            if (startTime > System.currentTimeMillis()) {
//                fps++;
//            } else {
//                long timeUsed = 5000 + (startTime - System.currentTimeMillis());
//                startTime = System.currentTimeMillis() + 5000;
//                fps = 0;
//            }


//            final Ray mouseRay = getMouseRay();

        // Is button 0 down? Button 0 is left click
        if (MouseInput.get().isButtonDown(0)) {
            results.clear();
            box.calculatePick(mouseRay, results);
        }
//        System.out.println("box=" + box.getLocalTranslation() + ", srrMouse=" + srrMouse.getHotSpotPosition() + ", srrMouse.world=" + worldCoords);

        mouseRay.setDirection(mouseRay.getDirection().mult(10.0f));//so z=-10
        Vector3f dst = mouseRay.getOrigin().add(mouseRay.getDirection());
        Vector3f newV = new Vector3f(dst.x, dst.y, -10);
        box.setLocalTranslation(newV);
    }
}
