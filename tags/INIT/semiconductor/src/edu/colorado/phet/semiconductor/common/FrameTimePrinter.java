/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.semiconductor.common;

import edu.colorado.phet.common.model.simpleobservable.SimpleObserver;

public class FrameTimePrinter implements SimpleObserver {
    private int count = 0;
    private FrameRate rate;
    int mod = 100;

    public FrameTimePrinter( FrameRate rate, int mod ) {
        this.rate = rate;
        this.mod = mod;
    }

    public void update() {
        count++;
        if( count % mod == 0 ) {
            double fps = rate.getFrameTimeMillis();
            System.out.println( "Frame Time=" + fps + ", FPS=" + rate.getFps() );
        }
    }

}