package edu.colorado.phet.ohm1d.phet_ohm1d.wire1d.paint;

import edu.colorado.phet.ohm1d.phet_ohm1d.paint.Painter;
import edu.colorado.phet.ohm1d.phet_ohm1d.paint.particle.ParticlePainter;
import edu.colorado.phet.ohm1d.phet_ohm1d.phys2d.DoublePoint;
import edu.colorado.phet.ohm1d.phet_ohm1d.phys2d.Particle;
import edu.colorado.phet.ohm1d.phet_ohm1d.wire1d.WireParticle;
import edu.colorado.phet.ohm1d.phet_ohm1d.wire1d.WirePatch;

import java.awt.*;

public class WireParticlePainter implements Painter {
    Particle temp = new Particle();
    ParticlePainter pp;
    WireParticle wp;

    public WireParticlePainter( WireParticle wp, ParticlePainter pp ) {
        this.pp = pp;
        this.wp = wp;
    }

    public void paint( Graphics2D g ) {
        WirePatch converter = wp.getWirePatch();
        if( converter == null ) {
            throw new RuntimeException( "Null wire patch." );
        }
        DoublePoint dp = converter.getPosition( wp.getPosition() );
        if( dp == null ) {
            throw new RuntimeException( "WirePatch returned a null position." );
            //return;
        }
        //util.Debug.traceln("Got position="+p.getPosition()+"->"+dp+"\n");
        temp.setPosition( dp );
        pp.paint( temp, g );
    }
}
