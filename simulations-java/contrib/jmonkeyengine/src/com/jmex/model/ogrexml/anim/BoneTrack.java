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

import java.io.Serializable;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;

/**
 * Contains a list of transforms and times for each keyframe.
 */
public final class BoneTrack implements Serializable{
    private static final long serialVersionUID = 1L;

    /**
     * Bone index in the skeleton which this track effects.
     */
    private final int targetBoneIndex;

    /**
     * Transforms and times for track.
     */
    private final Vector3f[] translations;
    private final Quaternion[] rotations;
    private final float[] times;

    // temp vectors for interpolation
    private final Vector3f tempV = new Vector3f();
    private final Quaternion tempQ = new Quaternion();

    public BoneTrack(int targetBoneIndex, float[] times, Vector3f[] translations, Quaternion[] rotations){
        this.targetBoneIndex = targetBoneIndex;

        if (times.length == 0)
            throw new RuntimeException("BoneTrack with no keyframes!");

        assert (times.length == translations.length) && (times.length == rotations.length);

        this.times = times;
        this.translations = translations;
        this.rotations = rotations;
    }

//    private static final float interpolateCubic(float v0, float v1, float v2, float v3, float x){
//        float p = (v3 - v2) - (v0 - v1);
//        float q = (v0 - v1) - p;
//        float r = v2 - v0;
//        float s = v1;
//
//        return p * x * x * x
//             + q * x * x
//             + r * x
//             + s;
//    }
//
//    public static final float interpolateCatmullRom(float v0, float v1, float v2, float v3, float x){
//        return 0.5f * ((-v0 + 3f*v1 -3f*v2 + v3)*x*x*x
//                    +  (2f*v0 -5f*v1 + 4f*v2 - v3)*x*x
//                    +  (-v0+v2)*x
//                    +  2f*v1);
//
//    }
//
//    private static final void interpolateCubic(Vector3f v0, Vector3f v1, Vector3f v2, Vector3f v3, float x, Vector3f store){
//        store.x = interpolateCubic(v0.x, v1.x, v2.x, v3.x, x);
//        store.y = interpolateCubic(v0.y, v1.y, v2.y, v3.y, x);
//        store.z = interpolateCubic(v0.z, v1.z, v2.z, v3.z, x);
//    }
//
//    private static final void interpolateCubic(Quaternion v0, Quaternion v1, Quaternion v2, Quaternion v3, float x, Quaternion store){
//        store.x = interpolateCubic(v0.x, v1.x, v2.x, v3.x, x);
//        store.y = interpolateCubic(v0.y, v1.y, v2.y, v3.y, x);
//        store.z = interpolateCubic(v0.z, v1.z, v2.z, v3.z, x);
//        store.w = interpolateCubic(v0.w, v1.w, v2.w, v3.w, x);
//        store.normalize();
//    }
//
    /**
     * Modify the bone which this track modifies in the skeleton to contain
     * the correct animation transforms for a given time.
     * The transforms can be interpolated in some method from the keyframes.
     */
    public void setTime(float time, Skeleton skeleton) {
        Bone target = skeleton.getBone(targetBoneIndex);

        // by default the mode is to clamp for times beyond the current timeline
        if (time < times[0]){
            target.setAnimTransforms(translations[0], rotations[0]);
        }else if (time > times[times.length-1]){
            target.setAnimTransforms(translations[translations.length-1],
                                     rotations[rotations.length-1]);
        } else{
            int startFrame = 0;
            int endFrame   = 0;
//            int prevFrame  = 0;
//            int nextFrame  = 0;

            for (int i = 0; i < times.length; i++){
                if (times[i] <= time){
                    startFrame = i;
                    endFrame   = i + 1;
                    //assert times[startFrame] < times[endFrame];
                }
            }

            float blend = (time - times[startFrame]) / (times[endFrame] - times[startFrame]);
            //blend = FastMath.clamp(blend, 0f, 1f);

//            prevFrame = Math.max(startFrame, 0);
//            nextFrame = Math.min(endFrame,   times.length-1);

//            interpolateCubic(rotations[prevFrame],
//                             rotations[startFrame],
//                             rotations[endFrame],
//                             rotations[nextFrame],
//                             blend,
//                             tempQ);
//
//            interpolateCubic(translations[prevFrame],
//                             translations[startFrame],
//                             translations[endFrame],
//                             translations[nextFrame],
//                             blend,
//                             tempV);

            tempQ.slerp(rotations[startFrame], rotations[endFrame], blend);
            tempV.interpolate(translations[startFrame], translations[endFrame], blend);

            target.setAnimTransforms(tempV, tempQ);
        }
    }

}
