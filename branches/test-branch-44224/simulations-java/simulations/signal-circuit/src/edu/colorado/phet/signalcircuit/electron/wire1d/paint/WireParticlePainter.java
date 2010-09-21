package edu.colorado.phet.signalcircuit.electron.wire1d.paint;

import edu.colorado.phet.signalcircuit.electron.wire1d.WireParticle;
import edu.colorado.phet.signalcircuit.electron.wire1d.WirePatch;
import edu.colorado.phet.signalcircuit.electron.wire1d.WireSystem;
import edu.colorado.phet.signalcircuit.paint.Painter;
import edu.colorado.phet.signalcircuit.phys2d.DoublePoint;
import edu.colorado.phet.signalcircuit.phys2d.Particle;

import java.awt.*;

public class WireParticlePainter implements Painter {
    WireSystem ws;
    WirePatch converter;

    public WireParticlePainter( WireSystem ws, WirePatch converter ) {
        this.ws = ws;
        this.converter = converter;
    }

    public void paint( Graphics2D g ) {
        for( int i = 0; i < ws.numParticles(); i++ ) {
            paint( ws.particleAt( i ), g );
        }
    }

    public void paint( WireParticle p, Graphics2D g ) {
        DoublePoint dp = converter.getPosition( p.getPosition() );
        if( dp == null ) {
            return;
        }
        //edu.colorado.phet.util.Debug.traceln("Got position="+p.getPosition()+"->"+dp+"\n");
        Particle px = new Particle();
        px.setPosition( dp );
        p.getPainter().paint( px, g );
    }
}
