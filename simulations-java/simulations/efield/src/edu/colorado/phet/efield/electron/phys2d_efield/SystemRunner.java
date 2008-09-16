// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.efield.electron.phys2d_efield;

public class SystemRunner {
    private System2D system;

    public SystemRunner( System2D system2d ) {
        system = system2d;
    }

    public synchronized void setSystem( System2D system2d ) {
        system = system2d;
    }

    public void step( double dt ) {
        system.iterate( dt );
    }

}
