// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ToneDelegator.java
package javasound.tone;


// Referenced classes of package srr.tone:
//            Tone

public class ToneDelegator extends Tone {

    public ToneDelegator( double v, double v1 ) {
        super( (float)v, (float)v1 );
    }

    public ToneDelegator( double v, double v1, boolean b ) {
        super( (float)v, (float)v1, b );
    }

    public void setPitch( double v ) {
        super.setPitch( (float)v );
    }

    public void start() {
        super.start();
    }

    public void stop() {
        super.stop();
    }

    public void setVolume( double v ) {
        super.setVolume( (float)v );
    }

    public void setMuted( boolean b ) {
        super.setMuted( b );
    }
}
