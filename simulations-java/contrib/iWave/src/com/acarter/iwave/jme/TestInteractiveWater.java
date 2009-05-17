/**
 * Copyright (c) 2008, Andrew Carter
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are 
 * permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list of 
 * conditions and the following disclaimer. Redistributions in binary form must reproduce 
 * the above copyright notice, this list of conditions and the following disclaimer in 
 * the documentation and/or other materials provided with the distribution. Neither the 
 * name of Andrew Carter nor the names of contributors may be used to endorse or promote 
 * products derived from this software without specific prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES 
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, 
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT 
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR 
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.acarter.iwave.jme;


import com.acarter.iwave.jme.InteractiveWater.PaintMode;
import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.light.PointLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.shape.Box;
import com.jme.scene.state.MaterialState;

/**
 * @author Carter
 * 
 */
public class TestInteractiveWater extends SimpleGame {
	
	protected InteractiveWater water = null;

	/**
	 * Entry point for the test,
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		TestInteractiveWater app = new TestInteractiveWater();
		
		app.setConfigShowMode(ConfigShowMode.AlwaysShow);
		app.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.app.BaseSimpleGame#simpleInitGame()
	 */
	@Override
	protected void simpleInitGame() {
		
	    display.setTitle("Interactive Water");

		// first get rid off all previous lights
		lightState.detachAll();
		lightState.setGlobalAmbient(new ColorRGBA(0.1f, 0.1f, 0.1f, 1));
		PointLight pl = new PointLight();
		pl.setAmbient(new ColorRGBA(0.4f, 0.4f, 0.4f, 1));
		pl.setDiffuse(new ColorRGBA(0.9f, 0.9f, 0.9f, 1));
		pl.setSpecular(new ColorRGBA(0.9f, 0.9f, 0.9f, 1));
		pl.setLocation(new Vector3f(200, 500, 500));
		pl.setAttenuate(false);
		pl.setConstant(1f);
		pl.setLinear(0f);
		pl.setQuadratic(0f);
		pl.setEnabled(true);
		lightState.attach(pl);

        KeyBindingManager manager = KeyBindingManager.getKeyBindingManager();
        manager.set("SPACE", KeyInput.KEY_SPACE);
        
	    water = new InteractiveWater("Water", 100);
	    water.setModelBound(new BoundingBox()); 
	    water.updateModelBound(); 
	    
	    MaterialState ms = display.getRenderer().createMaterialState();
	    ms.setAmbient(new ColorRGBA(0.2f, 0.3f, 0.5f, 1.0f));
	    ms.setDiffuse(new ColorRGBA(0.4f, 0.5f, 0.8f, 1.0f));
	    ms.setSpecular(new ColorRGBA(0.4f, 0.5f, 0.5f, 1.0f));
	    ms.setShininess(60.0f);
	    water.setRenderState(ms);
	    water.updateRenderState();
	    
	    rootNode.attachChild(water);
	    
	    Box obstruction = new Box("box", new Vector3f(), 0.5f, 0.5f, 0.5f);
	    obstruction.getLocalScale().set(6, 5, 20);
	    obstruction.getLocalTranslation().set(30, 0, 50);
	    rootNode.attachChild(obstruction);
	    
	    water.setPaintMode(PaintMode.PAINT_OBSTRUCTION);
	    
	    for(int x = 28; x < 32; x++)
	    	for(int y = 41; y < 59; y++)
	    		water.dabSomePaint(x, y);
	    
	    cam.setLocation(cam.getLocation().set(0, 50, 0));
	    cam.lookAt(new Vector3f(50, 0, 50), Vector3f.UNIT_Y);
	}
	
	@Override
	protected void simpleUpdate() {

        KeyBindingManager manager = KeyBindingManager.getKeyBindingManager();

        if (manager.isValidCommand("SPACE", false)) {

    	    water.setPaintMode(PaintMode.PAINT_SOURCE);
        	water.dabSomePaint(50, 50);
        }
        
		water.update(tpf);
	}

}
