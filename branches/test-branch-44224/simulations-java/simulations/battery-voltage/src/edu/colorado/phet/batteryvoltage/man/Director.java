package edu.colorado.phet.batteryvoltage.man;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import edu.colorado.phet.batteryvoltage.Battery;
import edu.colorado.phet.batteryvoltage.ParticleMoveListener;
import edu.colorado.phet.batteryvoltage.common.phys2d.Particle;
import edu.colorado.phet.batteryvoltage.common.phys2d.PropagatingParticle;
import edu.colorado.phet.batteryvoltage.common.phys2d.System2D;
import edu.colorado.phet.batteryvoltage.man.voltListeners.VoltageListener;

public class Director implements VoltageListener, ParticleMoveListener {
    Vector carried;
    Vector targeted;
    int setVolts;
    Vector men;
    System2D sys;
    double middle;
    Hashtable sideTags = new Hashtable();

    public Director( System2D sys, Vector carried, Vector targeted, int setVolts, double middle ) {
        this.middle = middle;
        this.sys = sys;
        men = new Vector();
        this.setVolts = setVolts;
        this.carried = carried;
        this.targeted = targeted;
    }

    public void determineTag( Particle p ) {
        if ( p.getPosition().getX() > middle ) {
            putTag( p, true );
        }
        else {
            putTag( p, false );
        }
    }

    public int getTagCount( boolean tag ) {
        Boolean t = new Boolean( tag );
        Enumeration k = sideTags.keys();
        int num = 0;
        while ( k.hasMoreElements() ) {
            Object key = k.nextElement();
            if ( t.equals( sideTags.get( key ) ) ) {
                num++;
            }
        }
        return num;
    }

    public void initTags( Particle[] p ) {
        for ( int i = 0; i < p.length; i++ ) {
            determineTag( p[i] );
        }
    }

    public void putTag( Particle p, boolean right ) {
        sideTags.put( p, new Boolean( right ) );
    }

    public void addVoltMan( VoltMan vm ) {
        men.add( vm );
        vm.goHomeAndStayThere();
    }

    public int numMen() {
        return men.size();
    }

    public VoltMan manAt( int i ) {
        return (VoltMan) men.get( i );
    }

    public void particleMoved( Battery source, Particle p ) {
        if ( p != null ) {
            determineTag( p );
        }
        voltageChanged( setVolts, source );
    }

    public void voltageChanged( int value, Battery b ) //value=0 .. numElectrons.
    {
        this.setVolts = value;

        int rightCount = getTagCount( true );

        int currentInRight = rightCount;
        int desiredNumInRight = setVolts;

        int dv = currentInRight - desiredNumInRight;
        //util.Debug.traceln("Num right="+currentInRight+", desire="+desiredNumInRight+", numToMoveRight="+dv);
        //util.Debug.traceln("L="+leftCount+", R="+rightCount+", volts="+currentVolts+", voltsToAimFor="+voltsToAimFor+", dv="+dv);

        if ( dv == 0 ) {
            return;
        }
        boolean right = dv < 0;
        if ( right ) {
            int numNewToSendRight = Math.abs( dv );
            //util.Debug.traceln("DV="+dv+", Sending "+numNewToSendRight+" new men right.");
            int numSent = 0;
            for ( int i = 0; i < numMen(); i++ ) {
                if ( manAt( i ).isAvailable() ) {
                    PropagatingParticle get = nextLeftParticle( 0 );
                    targeted.add( get );
                    //util.Debug.traceln("Recruited man : "+i+"= "+manAt(i)+" to carry right particle: "+get);
                    if ( get == null ) {
                        break;
                    }
                    manAt( i ).carryElectronRight( get );
                    putTag( get, true );
                    numSent++;
                    if ( numSent >= numNewToSendRight ) {
                        break;
                    }
                }
            }
        }
        else {
            int numNewToSendLeft = Math.abs( dv );
            //util.Debug.traceln("Sending "+numNewToSendLeft+" new men left.");
            int numSent = 0;
            for ( int i = 0; i < numMen(); i++ ) {
                if ( manAt( i ).isAvailable() ) {
                    PropagatingParticle get = nextRightParticle( 0 );
                    targeted.add( get );
                    //util.Debug.traceln("Recruited man : "+i+"= "+manAt(i)+" to carry left particle: "+get);
                    if ( get == null ) {
                        break;
                    }
                    manAt( i ).carryElectronLeft( get );
                    putTag( get, false );
                    numSent++;
                    if ( numSent >= numNewToSendLeft ) {
                        break;
                    }
                }
            }
        }
    }

    public PropagatingParticle nextLeftParticle( int start ) {
        for ( int i = start; i < sys.numParticles(); i++ ) {
            PropagatingParticle p = (PropagatingParticle) sys.particleAt( i );
            if ( carried.contains( p ) || targeted.contains( p ) ) {
            }
            else {
                if ( p.getPosition().getX() < middle ) {
                    return p;
                }
            }
        }
        return null;
    }

    public PropagatingParticle nextRightParticle( int start ) {
        for ( int i = start; i < sys.numParticles(); i++ ) {
            PropagatingParticle p = (PropagatingParticle) sys.particleAt( i );
            if ( carried.contains( p ) || targeted.contains( p ) ) {
            }
            else {
                if ( p.getPosition().getX() > middle ) {
                    return p;
                }
            }
        }
        return null;
    }
}

/*
  1. Make sure the current carriers are going the right direction.
  2. If we need more carriers, recruit them.
  3. If we need less carriers, have some carriers return their cargo.
*/
