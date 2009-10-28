/*
 * Copyright (c) 2008-2009, Piccolo2D project, http://piccolo2d.org
 * Copyright (c) 1998-2008, University of Maryland
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 * and the following disclaimer in the documentation and/or other materials provided with the
 * distribution.
 *
 * None of the name of the University of Maryland, the name of the Piccolo2D project, or the names of its
 * contributors may be used to endorse or promote products derived from this software without specific
 * prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.umd.cs.piccolox;

import java.awt.event.KeyListener;

import junit.framework.TestCase;

import edu.umd.cs.piccolo.PCanvas;

/**
 * Unit test for PFrame.
 */
public class PFrameTest extends TestCase {
    private PFrame frame;   

    public void testCanvasIsValidWithDefaultConstructor() {
        PFrame frame = new PFrame() {
            public void setVisible(boolean visible) {
                // why oh why is PFrame visible by default
            }
        };
        PCanvas canvas = frame.getCanvas();
        assertNotNull(canvas);
        assertNotNull(canvas.getLayer());
        assertNotNull(canvas.getCamera());
        assertSame(canvas.getLayer(), canvas.getCamera().getLayer(0));
    }

    public void testDefaultsToWindowed() {
        PFrame frame = new PFrame() {
            public void setVisible(boolean visible) {
                // why oh why is PFrame visible by default
            }
        };
        assertFalse(frame.isFullScreenMode());
    }

    public void testFullScreenModeInstallsEscapeListeners() {
        PFrame frame = new PFrame();        
        frame.setFullScreenMode(true);        
        

        KeyListener[] listeners = frame.getCanvas().getKeyListeners();
        assertEquals(1, listeners.length);

        KeyListener listener = listeners[0];
        assertNotNull(listener);
        frame.setVisible(false);
        frame.setFullScreenMode(false);
    }
}
