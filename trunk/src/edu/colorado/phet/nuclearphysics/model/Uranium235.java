/**
 * Class: Uranium235
 * Package: edu.colorado.phet.nuclearphysics.model
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Uranium235 extends Nucleus {

    //
    // Statics
    //
    private static PotentialProfile potentialProfile = new PotentialProfile( 300, 400, 75 );

    //
    // Instance fields and methods
    //
    private ArrayList decayListeners = new ArrayList();
    private AlphaParticle[] alphaParticles = new AlphaParticle[4];

    public Uranium235( Point2D.Double position ) {
        super( position, 145, 92, potentialProfile );
        for( int i = 0; i < alphaParticles.length; i++ ) {
            alphaParticles[i] = new AlphaParticle( position, potentialProfile.getAlphaDecayX() / 3 );
        }
    }

    public AlphaParticle[] getAlphaParticles() {
        return alphaParticles;
    }

    public void addDecayListener( DecayListener listener ) {
        this.decayListeners.add( listener );
    }

    public void stepInTime( double dt ) {
        for( int j = 0; j < alphaParticles.length; j++ ) {
            AlphaParticle alphaParticle = alphaParticles[j];
            if( Math.abs( alphaParticle.getStatisticalLocationOffset().getX() ) + alphaParticle.getRadius()
                > Math.abs( potentialProfile.getAlphaDecayX() ) ) {
                try {
                    Thread.sleep( 1000 );
                }
                catch( InterruptedException e ) {
                    e.printStackTrace();
                }
                // Note: the production of decay products in this way is probably no longer the best
                // way to do things, now that there are alpha particles jumping around and causing the
                // decay in the first place. But this hacked up system still seems to work, so I'm
                // sticking with it for now.
                DecayProducts decayProducts = alphaDecay();
                // Give the new alpha particle the same offset as the old one
                decayProducts.getN2().setStatisticalLocationOffset( alphaParticle.getStatisticalLocationOffset() );
                for( int i = 0; i < decayListeners.size(); i++ ) {
                    DecayListener decayListener = (DecayListener)decayListeners.get( i );
                    decayListener.alphaDecay( decayProducts );
                }
                return;
            }
        }
        super.stepInTime( dt );
    }

    public DecayProducts alphaDecay() {
        int n1Protons = 2;
        int n1Neutrons = 2;
        double theta = Math.random() * Math.PI * 2;
        double separation = 100;
        double dx = separation * Math.cos( theta );
        double dy = separation * Math.sin( theta );
        Nucleus n1 = new DecayNucleus( new Point2D.Double( this.getLocation().getX() + dx,
                                                           this.getLocation().getY() + dy ),
                                       this.getNumProtons() - n1Protons,
                                       this.getNumNeutrons() - n1Neutrons, this.getPotentialProfile() );
        Nucleus n2 = new DecayNucleus( new Point2D.Double( this.getLocation().getX() - dx,
                                                           this.getLocation().getY() - dy ),
                                       n1Protons, n1Neutrons, this.getPotentialProfile() );
        DecayProducts products = new DecayProducts( this, n1, n2 );
        return products;
    }

    public DecayProducts decay() {
        // Create two new nuclei
        int n1Protons = 60;
        int n1Neutrons = 40;
        double theta = Math.random() * Math.PI * 2;
        double separation = 100;
        double dx = separation * Math.cos( theta );
        double dy = separation * Math.sin( theta );
        Nucleus n1 = new Nucleus( new Point2D.Double( this.getLocation().getX() - dx,
                                                      this.getLocation().getY() - dy ),
                                  n1Protons, n1Neutrons, this.getPotentialProfile() );
        Nucleus n2 = new Nucleus( new Point2D.Double( this.getLocation().getX() + dx,
                                                      this.getLocation().getY() + dy ),
                                  this.getNumProtons() - n1Protons,
                                  this.getNumNeutrons() - n1Neutrons, this.getPotentialProfile() );
        DecayProducts products = new DecayProducts( this, n1, n2 );
        return products;
    }

}
