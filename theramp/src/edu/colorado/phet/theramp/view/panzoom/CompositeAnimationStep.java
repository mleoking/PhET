/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view.panzoom;

import edu.colorado.phet.common.view.ApparatusPanel2;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * User: Sam Reid
 * Date: May 6, 2005
 * Time: 3:15:45 PM
 * Copyright (c) May 6, 2005 by Sam Reid
 */
public class CompositeAnimationStep extends AnimationStep {
    ArrayList steps = new ArrayList();
    Hashtable table = new Hashtable();
    private ApparatusPanel2 rampPanel;

    public CompositeAnimationStep( ApparatusPanel2 rampPanel, int keycode ) {
        super( keycode );
        this.rampPanel = rampPanel;
    }

    public boolean step() {
        boolean done = true;
        for( int i = 0; i < steps.size(); i++ ) {
            AnimationStep as = (AnimationStep)steps.get( i );
            boolean t = as.step();
            if( t ) {
                done = false;
            }
        }
        rampPanel.paintImmediately( 0, 0, rampPanel.getWidth(), rampPanel.getHeight() );
        return done;
    }

    public void addAnimationStep( AnimationStep moving ) {
        for( int i = 0; i < steps.size(); i++ ) {
            AnimationStep animationStep = (AnimationStep)steps.get( i );
            if( animationStep.keycode == moving.keycode ) {
                return;
            }
        }
        steps.add( moving );
    }

    public void release( int keyCode ) {
        for( int i = 0; i < steps.size(); i++ ) {
            AnimationStep animationStep = (AnimationStep)steps.get( i );
            if( animationStep.keycode == keyCode ) {
                steps.remove( i );
                i--;
            }
        }
    }
}
