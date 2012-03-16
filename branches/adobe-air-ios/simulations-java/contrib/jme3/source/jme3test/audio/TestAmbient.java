/*
 * Copyright (c) 2009-2010 jMonkeyEngine
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

package jme3test.audio;

import com.jme3.audio.AudioNode;
//import com.jme3.audio.PointAudioSource;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

public class TestAmbient extends AudioApp {

    private AudioNode river, nature, waves;
//    private PointAudioSource waves;
    private float time = 0;
    private float nextTime = 1;

    public static void main(String[] args){
        TestAmbient test = new TestAmbient();
        test.start();
    }

    @Override
    public void initAudioApp(){
        waves  = new AudioNode(audioRenderer, assetManager, "Sound/Environment/Ocean Waves.ogg", false);
        waves.setPositional(true);

        nature = new AudioNode(audioRenderer, assetManager, "Sound/Environment/Nature.ogg", true);
//        river  = new AudioSource(manager, "sounds/river.ogg");

//        float[] eax = new float[]
//            {15,	38.0f,	0.300f,	-1000,	-3300,	0,		1.49f,	0.54f,	1.00f,  -2560,	0.162f, 0.00f,0.00f,0.00f,	-229,	0.088f,		0.00f,0.00f,0.00f,	0.125f, 1.000f, 0.250f, 0.000f, -5.0f,  5000.0f,	250.0f, 0.00f,	0x3f }
//            ;
//
//        Environment env = new Environment(eax);
//        ar.setEnvironment(env);

        waves.setLocalTranslation(new Vector3f(4, -1, 30));
        waves.setMaxDistance(5);
        waves.setRefDistance(1);
        
        nature.setVolume(3);
        audioRenderer.playSourceInstance(waves);
        audioRenderer.playSource(nature);
    }

    @Override
    public void updateAudioApp(float tpf){
        time += tpf;

        if (time > nextTime){
            
            time = 0;
            nextTime = FastMath.nextRandomFloat() * 2 + 0.5f;
        }
    }

}
