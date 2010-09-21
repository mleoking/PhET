package edu.colorado.phet.balloons;

import edu.colorado.phet.balloons.common.paint.Painter;
import edu.colorado.phet.balloons.common.phys2d.DoublePoint;
import edu.colorado.phet.balloons.common.phys2d.PropagatingParticle;

public class Charge extends PropagatingParticle {
    private Painter p;
    private int level;
    private boolean neutral = true;
    private Charge partner;
    private DoublePoint initPos;

    public void setInitialPosition( DoublePoint initPos ) {
        this.initPos = initPos;
    }

    public DoublePoint getInitialPosition() {
        return initPos;
    }

    public void setPartner( Charge partner ) {
        this.partner = partner;
    }

    public void setNeutral( boolean neutral ) {
        this.neutral = neutral;
        if ( partner != null ) {
            partner.setNeutral( neutral );
        }
    }

    public void setPainter( Painter p, int level ) {
        this.p = p;
        this.level = level;
    }

    public boolean addsToNeutral() {
        return neutral;
    }
//      public Charge(Painter p,int level)
//      {
//  	this.level=level;
//  	this.p=p;

    //      }

    public int getLevel() {
        return level;
    }

    public Painter getPainter() {
        return p;
    }
}
