/**
 * Class: TestModule
 * Package: edu.colorado.phet.nuclearphysics.modules
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.controller;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.nuclearphysics.model.Neutron;
import edu.colorado.phet.nuclearphysics.model.Nucleus;
import edu.colorado.phet.nuclearphysics.view.NucleusGraphic;
import edu.colorado.phet.nuclearphysics.view.OneNucleusModule;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Random;

public class FissionModule extends OneNucleusModule {
    private static Random random = new Random();

    public FissionModule( AbstractClock clock ) {
        super( "Fission", clock );
        super.addControlPanelElement( new FissionControlPanel( this ) );
        getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                checkForFission();
            }
        } );
    }

    static int cnt = 0;

    public void fireNeutron() {

        double theta = random.nextDouble() * Math.PI * 2;
        double r = 200;
        double x = r * Math.cos( theta );
        double y = r * Math.sin( theta );

        Neutron neutron = new Neutron( new Point2D.Double( x, y ) );
        neutron.setVelocity( ( new Vector2D( (float)-x, (float)-y ) ).normalize().multiply( 5f ) );
        super.addNeutron( neutron );
    }

    private void checkForFission() {
        for( int i = 0; i < getModel().numModelElements(); i++ ) {
            ModelElement me = getModel().modelElementAt( i );
            if( me instanceof Neutron ) {
                for( int j = 0; j < getModel().numModelElements(); j++ ) {
                    ModelElement me2 = getModel().modelElementAt( j );
                    if( me2 instanceof Nucleus ) {
                        if( ( (Neutron)me ).getLocation().distanceSq( ( (Nucleus)me2 ).getLocation() )
                            < ( (Nucleus)me2 ).getRadius() * ( (Nucleus)me2 ).getRadius() ) {
                            fission( (Nucleus)me2, (Neutron)me );
                        }
                    }
                }
            }
        }
    }

    private void fission( Nucleus nucleus, Neutron neutron ) {
        // Remove the neutron and old nucleus
        getModel().removeModelElement( neutron );
        getModel().removeModelElement( nucleus );
        List graphics = (List)NucleusGraphic.getGraphicForNucleus( nucleus );
        for( int i = 0; i < graphics.size(); i++ ) {
            NucleusGraphic ng = (NucleusGraphic)graphics.get( i );
            this.getPotentialProfilePanel().removeGraphic( ng );
            this.getPhysicalPanel().removeGraphic( ng );

        }

        // create fission products
        Nucleus n1 = new Nucleus( new Point2D.Double( nucleus.getLocation().getX() - 50, nucleus.getLocation().getX() - 50 ),
                                  nucleus.getNumProtons() / 2, nucleus.getNumNeutrons() / 2, nucleus.getPotentialProfile() );
        Nucleus n2 = new Nucleus( new Point2D.Double( nucleus.getLocation().getX() + 50, nucleus.getLocation().getX() + 50 ),
                                  nucleus.getNumProtons() / 2, nucleus.getNumNeutrons() / 2, nucleus.getPotentialProfile() );
        super.addNeucleus( n1 );
        super.addNeucleus( n2 );
    }

}
