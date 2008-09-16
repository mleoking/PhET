// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.efield.gui.media;

import java.util.Vector;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.efield.core.SystemFactory;
import edu.colorado.phet.efield.gui.ParticlePainter;
import edu.colorado.phet.efield.gui.ParticlePanel;
import edu.colorado.phet.efield.phys2d_efield.SystemRunner;

public class MediaControl {

    public MediaControl( IClock clock, SystemRunner systemrunner, SystemFactory systemfactory, ParticlePanel particlepanel, ParticlePainter particlepainter ) {
        this.clock = clock;
        particlePainter = particlepainter;
        systemRunner = systemrunner;
        systemFactory = systemfactory;
        particlePanel = particlepanel;
        resettables = new Vector();
    }

    public void add( EFieldResettable EFieldResettable ) {
        resettables.add( EFieldResettable );
    }

    public void reset() {
        particlePanel.reset();
        particlePanel.repaint();
        for ( int i = 0; i < resettables.size(); i++ ) {
            ( (EFieldResettable) resettables.get( i ) ).fireResetAction( particlePanel );
        }
        clock.start();
    }

    public void pause() {
        clock.pause();
    }

    public void unpause() {
        clock.start();
    }

    SystemRunner systemRunner;
    SystemFactory systemFactory;
    ParticlePanel particlePanel;
    ParticlePainter particlePainter;
    Vector resettables;
    private IClock clock;
}
