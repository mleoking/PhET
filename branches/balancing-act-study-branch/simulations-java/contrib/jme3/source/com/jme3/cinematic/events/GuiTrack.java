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
package com.jme3.cinematic.events;

import com.jme3.animation.LoopMode;
import com.jme3.app.Application;
import com.jme3.cinematic.Cinematic;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import de.lessvoid.nifty.Nifty;
import java.io.IOException;

/**
 *
 * @author Nehon
 */
public class GuiTrack extends AbstractCinematicEvent {

    protected String screen;
    protected Nifty nifty;

    public GuiTrack() {
    }

    public GuiTrack(String screen) {
        this.screen = screen;
    }

    public GuiTrack(String screen, float initialDuration) {
        super(initialDuration);
        this.screen = screen;

    }

    public GuiTrack(String screen, LoopMode loopMode) {
        super(loopMode);
        this.screen = screen;
    }

    public GuiTrack(String screen, float initialDuration, LoopMode loopMode) {
        super(initialDuration, loopMode);
        this.screen = screen;
    }

    @Override
    public void initEvent(Application app, Cinematic cinematic) {
        nifty = cinematic.getNifty();
    }

    @Override
    public void onPlay() {
        nifty.gotoScreen(screen);
    }

    @Override
    public void onStop() {
        nifty.gotoScreen("");
    }

    @Override
    public void onPause() {
    }

    public void setNifty(Nifty nifty) {
        this.nifty = nifty;
    }

    public void setScreen(String screen) {
        this.screen = screen;
    }

    @Override
    public void onUpdate(float tpf) {
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(screen, "screen", "");
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule ic = im.getCapsule(this);
        screen = ic.readString("screen", "");
    }
}
