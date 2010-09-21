/*
 * Copyright (c) 2003-2009 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


package com.jmex.model.ogrexml.anim;

import com.jme.math.Matrix4f;
import com.jme.math.Vector3f;
import java.util.Map;
import com.jme.scene.Controller;
import com.jme.scene.state.GLSLShaderObjectsState;
import com.jme.scene.state.RenderState;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Collection;

public class MeshAnimationController extends Controller implements Savable {

    private static final long serialVersionUID = -2412532346418342259L;

    /**
     * If true, hardware skinning will not be used even if supported.
     */
    private static final boolean forceSWskinning = true;

    /**
     * List of targets which this controller effects.
     */
    private OgreMesh[] targets;

    /**
     * Skeleton object must contain corresponding data for the targets' weight buffers.
     */
    private Skeleton skeleton;

    /**
     * List of animations, bone or vertex based.
     */
    private Map<String, Animation> animationMap;

    /**
     * The currently playing animation.
     */
    private Animation animation;
    private float time = 0f;

    /**
     * True if the mesh data should be reset to bind pose every frame.
     * This only refers to mesh data, the skeleton must always be reset to bind pose each frame.
     */
    private boolean resetToBindEveryFrame = false;

    /**
     * Frameskip LOD option
     */
    private int framesToSkip = 0;
    private int curFrame = 0;

    public MeshAnimationController(OgreMesh[] meshes,
                                   Skeleton skeleton,
                                   Map<String, Animation> anims){

        this.setRepeatType(RT_WRAP);
        this.skeleton = skeleton;
        this.animationMap = anims;
        this.targets = meshes;

        // find out which meshes need to have bind pose data saved
        for (int i = 0; i < targets.length; i++){
            // does this mesh has any pose/morph animation tracks?
            for (Animation anim : animationMap.values()){
                MeshAnimation manim = anim.getMeshAnimation();
                BoneAnimation banim = anim.getBoneAnimation();

                if (manim != null){
                    for (Track t : manim.getTracks()){
                        if (t.getTargetMeshIndex() == i){
                            targets[i].clearBindPose();
                            targets[i].saveCurrentToBindPose();
                            break;
                        }
                    }
                }

                if (banim != null && !isHardwareSkinning()){
                    targets[i].clearBindPose();
                    targets[i].saveCurrentToBindPose();
                    break;
                }
            }

            if (targets[i].getWeightBuffer() != null)
                targets[i].getWeightBuffer().initializeWeights();
        }

        if (isHardwareSkinning()){
            assignShaderLogic();
        }

        reset();
    }

    /**
     * Copy constructor. The mesh data has to be unique, and copied through OgreMesh.cloneFromMesh.
     * The rest is handled automatically by this call.
     */
    public MeshAnimationController(OgreMesh[] meshes, MeshAnimationController sourceControl){
        this.setRepeatType(RT_WRAP);
        this.skeleton = new Skeleton(sourceControl.skeleton);
        this.animationMap = sourceControl.animationMap;
        this.targets = meshes;

        if (isHardwareSkinning()){
            assignShaderLogic();
        }

        reset();
    }

    /**
     * Returns a bone with the specified name.
     * Use this method to gain access to the bone,
     * to manually control it's transforms.
     */
    public Bone getBone(String name){
        return skeleton.getBone(name);
    }

    /**
     * Sets the currently active animation.
     * Use the animation name "<bind>" to set the model into bind pose.
     *
     * @returns true if the animation has been successfuly set. False if no such animation exists.
     */
    public boolean setAnimation(String name){
        if (name.equals("<bind>")){
            reset();
            return true;
        }

        animation = animationMap.get(name);

        if (animation == null)
            return false;

        resetToBind();
        resetToBindEveryFrame = animation.hasMeshAnimation() || !isHardwareSkinning();

        time = 0;

        return true;
    }

    /**
     * Returns the length of the animation in seconds. Returns -1 if the animation is not defined.
     */
    public float getAnimationLength(String name){
        Animation anim = animationMap.get(name);

        if (anim == null)
            return -1;

        return anim.getLength();
    }

    /**
     * @return The name of the currently active animation
     */
    public String getActiveAnimation(){
        if (animation == null)
            return "<bind>";

        return animation.getName();
    }

    /**
     * @deprecated  The name of this method incorrectly implies that a List
     *              is returned.  Use the method getAnimationNames instead.
     * @see #getAnimationNames();
     */
    @Deprecated
    public Collection<String> getList(){
        return getAnimationNames();
    }

    /**
     * @return Collection of list of all animations that are defined
     */
    public Collection<String> getAnimationNames(){
        return animationMap.keySet();
    }

    /**
     * Enables frameskip LOD.
     * This technique is mostly only effective when software skinning is used.
     *
     * @param framesToSkip One frame will be played out of the framesToSkip number.
     */
    public void setFrameSkip(int framesToSkip){
        if (this.framesToSkip != framesToSkip)
            this.curFrame = 0;

        this.framesToSkip = framesToSkip;
    }

    /**
     * Sets the time of the animation.
     * If it's greater than getAnimationLength(getActiveAnimation()),
     * the time will be appropriately clamped/wraped depending on the repeatMode.
     */
    public void setTime(float time){
        this.time = time;
    }

    Skeleton getSkeleton(){
        return skeleton;
    }

    OgreMesh[] getMeshList(){
        return targets;
    }

    void reset(){
        resetToBind();
        skeleton.getRoot().reset();
        skeleton.getRoot().update();
        resetToBindEveryFrame = false;
        animation = null;
        time = 0;
    }

    void resetToBind(){
        for (int i = 0; i < targets.length; i++){
            if (targets[i].hasBindPose()){
                targets[i].restoreBindPose();
            }
        }
    }

    private void assignShaderLogic(){
        SkinningShaderLogic logic = new SkinningShaderLogic();
        for (OgreMesh target : targets){
            GLSLShaderObjectsState glsl = (GLSLShaderObjectsState) target.getRenderState(RenderState.RS_GLSL_SHADER_OBJECTS);
            if (glsl == null){
                glsl = BoneAnimationLoader.createSkinningShader(skeleton.getBoneCount(),
                                                                target.getWeightBuffer().maxWeightsPerVert);
                target.setRenderState(glsl);
            }
            glsl.setShaderDataLogic(logic);
        }
    }

    /**
     * @return True if hardware skinning will be used.
     */
    public boolean isHardwareSkinning(){
        return !forceSWskinning && GLSLShaderObjectsState.isSupported();
    }

    private void softwareSkinUpdate(OgreMesh mesh){
        Vector3f vt = new Vector3f();
        Vector3f nm = new Vector3f();
        Vector3f resultVert = new Vector3f();
        Vector3f resultNorm = new Vector3f();

        Matrix4f offsetMatrices[] = skeleton.computeSkinningMatrices();

        // NOTE: This code assumes the vertex buffer is in bind pose
        // resetToBind() has been called this frame
        FloatBuffer vb = mesh.getVertexBuffer();
        vb.rewind();

        FloatBuffer nb = mesh.getNormalBuffer();
        nb.rewind();

        // get boneIndexes and weights for mesh
        ByteBuffer ib = mesh.getWeightBuffer().indexes;
        FloatBuffer wb = mesh.getWeightBuffer().weights;
        int maxWeightsPerVert = mesh.getWeightBuffer().maxWeightsPerVert;
        int fourMinusMaxWeights = 4 - maxWeightsPerVert;
        ib.rewind();
        wb.rewind();

        // iterate vertices and apply skinning transform for each effecting bone
        for (int vert = 0; vert < mesh.getVertexCount(); vert++){
            vt.x = vb.get();
            vt.y = vb.get();
            vt.z = vb.get();
            nm.x = nb.get();
            nm.y = nb.get();
            nm.z = nb.get();
            resultVert.x = resultVert.y = resultVert.z = 0;
            resultNorm.x = resultNorm.y = resultNorm.z = 0;

            for (int w = 0; w < maxWeightsPerVert; w++){
                float weight = wb.get();
                Matrix4f mat = offsetMatrices[ib.get()];

                resultVert.x += (mat.m00 * vt.x + mat.m01 * vt.y + mat.m02 * vt.z + mat.m03) * weight;
                resultVert.y += (mat.m10 * vt.x + mat.m11 * vt.y + mat.m12 * vt.z + mat.m13) * weight;
                resultVert.z += (mat.m20 * vt.x + mat.m21 * vt.y + mat.m22 * vt.z + mat.m23) * weight;
//                temp.x = vertex.x;
//                temp.y = vertex.y;
//                temp.z = vertex.z;
//                mat.mult(temp, temp);
//                resultVert.x += temp.x * weight;
//                resultVert.y += temp.y * weight;
//                resultVert.z += temp.z * weight;

                resultNorm.x += (nm.x * mat.m00 + nm.y * mat.m01 + nm.z * mat.m02) * weight;
                resultNorm.y += (nm.x * mat.m10 + nm.y * mat.m11 + nm.z * mat.m12) * weight;
                resultNorm.z += (nm.x * mat.m20 + nm.y * mat.m21 + nm.z * mat.m22) * weight;

//                temp.set(normal);
                //mat.rotateVect(temp);
//                resultNorm.x += temp.x * weight;
//                resultNorm.y += temp.y * weight;
//                resultNorm.z += temp.z * weight;

                ib.position(ib.position()+fourMinusMaxWeights);
                wb.position(wb.position()+fourMinusMaxWeights);
            }

            // overwrite vertex with transformed pos
            vb.position(vb.position()-3);
            vb.put(resultVert.x).put(resultVert.y).put(resultVert.z);

            nb.position(nb.position()-3);
            nb.put(resultNorm.x).put(resultNorm.y).put(resultNorm.z);
        }

        vb.flip();
        nb.flip();

        mesh.setHasDirtyVertices(true);
        mesh.updateModelBound();
    }

    @Override
    public void update(float tpf) {
        if (!isActive() || animation == null)
            return;

        // do clamping/wrapping of time
        if (time < 0f){
            switch (getRepeatType()){
                case RT_CLAMP:
                    time = 0f;
                    break;
                case RT_CYCLE:
                    time = 0f;
                    break;
                case RT_WRAP:
                    time = animation.getLength() - time;
                    break;
            }
        }else if (time > animation.getLength()){
            switch (getRepeatType()){
                case RT_CLAMP:
                    time = animation.getLength();
                    break;
                case RT_CYCLE:
                    time = animation.getLength();
                    break;
                case RT_WRAP:
                    time = time - animation.getLength();
                    break;
            }
        }

        if (framesToSkip > 0){
            // check frame skipping
            curFrame++;

            if (curFrame != framesToSkip){
                time += tpf * getSpeed();
                return;
            }else{
                curFrame = 0;
            }
        }

        if (resetToBindEveryFrame)
            resetToBind();

        if (animation.hasBoneAnimation()){
            skeleton.getRoot().reset();
        }

        animation.setTime(time, targets, skeleton);

        if (animation.hasBoneAnimation()){
            skeleton.getRoot().update();

            if (!isHardwareSkinning()){
                // here update the targets verticles if no hardware skinning supported

                // if hardware skinning is supported, the matrices and weight buffer
                // will be sent by the SkinningShaderLogic object assigned to the shader
                for (int i = 0; i < targets.length; i++){
                    softwareSkinUpdate(targets[i]);
                }
            }
        }

        time += tpf * getSpeed();
    }


    public float getCurTime() { return time; }

    /**
     * Used only for Saving/Loading models (all parameters of the non-default
     * constructor are restored from the saved model, but the object must be
     * constructed beforehand)
     */
    public MeshAnimationController() {

    }

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule output = e.getCapsule(this);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(animationMap);
        oos.flush();
        oos.close();
        bos.close();
        // Convert the animation map to a byte array:
        byte[] data = bos.toByteArray();
        // Then save it as such
        output.write(data, "MeshAnimationControllerData", null);

        output.write(targets, "targets[]", null);
        output.write(skeleton, "skeleton", null);
    }

    @SuppressWarnings("unchecked")
    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule input = e.getCapsule(this);

        byte[] data = input.readByteArray("MeshAnimationControllerData", null);
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bis);
        try {
            animationMap = (Map<String, Animation>) ois.readObject();
        } catch (ClassNotFoundException e1) {
            throw new RuntimeException(e1);
        }

        Savable[] targetsAsSavable = input.readSavableArray("targets[]", null);
        skeleton = (Skeleton) input.readSavable("skeleton", null);

        targets = new OgreMesh[targetsAsSavable.length];
        int i = 0;
        for (Savable s : targetsAsSavable)
            targets[i++] = (OgreMesh) s;
    }
}
