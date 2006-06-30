/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.nuclearphysics.controller;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.nuclearphysics.model.*;
import edu.colorado.phet.nuclearphysics.view.Kaboom;
import edu.colorado.phet.nuclearphysics.view.NeutronGraphic;
import edu.colorado.phet.nuclearphysics.view.PhysicalPanel;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * ChainReactionModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public abstract class ChainReactionModule extends NuclearPhysicsModule implements NeutronGun, FissionListener {
    protected static Random random = new Random();
    protected static int s_maxPlacementAttempts = 100;
    protected Neutron neutronToAdd;
    private ArrayList nuclei = new ArrayList();
    private ArrayList u235Nuclei = new ArrayList();
    private ArrayList u238Nuclei = new ArrayList();
    private ArrayList u239Nuclei = new ArrayList();
    private ArrayList neutrons = new ArrayList();
    protected double neutronLaunchAngle;
    protected Point2D.Double neutronLaunchPoint;

    // todo: push this down to the MultipleNucleusFissionModule
    protected Line2D.Double neutronPath;

    // The numberof U235 nuclei that are present when the neutron is fired;
    private int startingNumU235;

    /**
     *
     * @param name
     * @param clock
     */
    public ChainReactionModule( String name, IClock clock ) {
        super( name, clock );
    }

    protected void init() {
        // Have to do this to make sure resize events get reported properly with PhetTabbedPane
        getApparatusPanel().remove( getPhysicalPanel() );
        PhysicalPanel physicalPanel = new PhysicalPanel( getClock(), (NuclearPhysicsModel)getModel());
        setPhysicalPanel( physicalPanel );
        getApparatusPanel().add( physicalPanel );



        getPhysicalPanel().foo();

        

        // Add a listener to the model that will remove nuclei from our internal lists when they leave
        // the model.
        ((NuclearPhysicsModel)getModel()).addNucleusListener( new NuclearPhysicsModel.NucleusListener() {
            public void nucleusAdded( NuclearPhysicsModel.ChangeEvent event ) {
                // noop
            }

            public void nucleusRemoved( NuclearPhysicsModel.ChangeEvent event ) {
                if( event.getNucleus() instanceof Uranium238 ) {
                    u238Nuclei.remove( event.getNucleus() );
                }
                if( event.getNucleus() instanceof Uranium235 ) {
                    u235Nuclei.remove( event.getNucleus() );
                }
            }
        } );
    }

    public int getStartingNumU235() {
        return startingNumU235;
    }

    protected Line2D.Double getNeutronPath() {
        return neutronPath;
    }

    public ArrayList getNeutrons() {
        return neutrons;
    }

    public ArrayList getNuclei() {
        return nuclei;
    }

    public ArrayList getU235Nuclei() {
        return u235Nuclei;
    }

    public ArrayList getU238Nuclei() {
        return u238Nuclei;
    }

    public ArrayList getU239Nuclei() {
        return u239Nuclei;
    }

    public Nucleus addU235Nucleus() {
        Nucleus nucleus = null;
        Point2D.Double location = findLocationForNewNucleus();
        if( location != null ) {
            nucleus = new Uranium235( location, (NuclearPhysicsModel)getModel() );
            u235Nuclei.add( nucleus );
            addNucleus( nucleus );
        }
        return nucleus;
    }

    public void addU238Nucleus() {
        Point2D.Double location = findLocationForNewNucleus();
        if( location != null ) {
            Nucleus nucleus = new Uranium238( location, getModel() );
            u238Nuclei.add( nucleus );
            addNucleus( nucleus );
        }
    }

    public void addU239Nucleus( Uranium239 nucleus ) {
        u239Nuclei.add( nucleus );
        addNucleus( nucleus );
    }

    // todo: Push down to subclasses?
    protected void addNucleus( Nucleus nucleus ) {
        nuclei.add( nucleus );
        nucleus.addFissionListener( this );
        getModel().addModelElement( nucleus );
    }

    public void removeNucleus( Nucleus nucleus ) {
        nuclei.remove( nucleus );
        nucleus.removeFissionListener( this );
        getModel().removeModelElement( nucleus );
    }

    public void removeU235Nucleus( Uranium235 nucleus ) {
        u235Nuclei.remove( nucleus );
        removeNucleus( nucleus );
    }

    public void removeU238Nucleus( Uranium238 nucleus ) {
        u238Nuclei.remove( nucleus );
        removeNucleus( nucleus );
    }

    public void fireNeutron() {
        Neutron neutron = new Neutron( neutronLaunchPoint, neutronLaunchAngle );
        startingNumU235 = u235Nuclei.size();
        neutrons.add( neutron );
        addNeutron( neutron );
    }

    public void fission( FissionProducts products ) {
        // Remove the neutron and old nucleus
        getModel().removeModelElement( products.getInstigatingNeutron() );
        this.neutrons.remove( products.getInstigatingNeutron() );
        getModel().removeModelElement( products.getParent() );
        nuclei.remove( products.getParent() );

        // We know this must be a U235 nucleus
        u235Nuclei.remove( products.getParent() );

        // Add fission products
        addNucleus( products.getDaughter1() );
        addNucleus( products.getDaughter2() );
        nuclei.add( products.getDaughter1() );
        nuclei.add( products.getDaughter2() );

        Neutron[] neutronProducts = products.getNeutronProducts();
        for( int i = 0; i < neutronProducts.length; i++ ) {
            final NeutronGraphic npg = new NeutronGraphic( neutronProducts[i] );
            getModel().addModelElement( neutronProducts[i] );
            getPhysicalPanel().addGraphic( npg );
            neutrons.add( neutronProducts[i] );
            neutronProducts[i].addListener( new NuclearModelElement.Listener() {
                public void leavingSystem( NuclearModelElement nme ) {
                    getPhysicalPanel().removeGraphic( npg );
                }
            } );
        }

        // Add some pizzazz
        Kaboom kaboom = new Kaboom( products.getParent().getPosition(),
                                    25, 300, getPhysicalPanel(),
                                    getModel() );
        getPhysicalPanel().addGraphic( kaboom );
    }

    //----------------------------------------------------------------
    // Abstract methods
    //----------------------------------------------------------------

    public abstract void start();

    protected abstract void computeNeutronLaunchParams();

    // todo: push down to Multiple... subclass
    protected abstract Point2D.Double findLocationForNewNucleus();

    // TODO: clean up when refactoring is done. Push down to the Multiple... subclass
    public abstract void setContainmentEnabled( boolean b );

    public void stop() {
        // The class should be re-written so that everything is taken care
        // of by the nuclei list, and the others don't need to be iterated
        // here
        for( int i = nuclei.size() - 1; i >= 0;  i-- ) {
            removeNucleus( (Nucleus)nuclei.get( i ) );
        }
        for( int i = 0; i < u235Nuclei.size(); i++ ) {
            removeNucleus( (Nucleus)u235Nuclei.get( i ) );
        }
        for( int i = 0; i < u238Nuclei.size(); i++ ) {
            removeNucleus( (Nucleus)u238Nuclei.get( i ) );
        }
        for( int i = 0; i < u239Nuclei.size(); i++ ) {
            removeNucleus( (Nucleus)u239Nuclei.get( i ) );
        }
        for( int i = 0; i < neutrons.size(); i++ ) {
            Neutron neutron = (Neutron)neutrons.get( i );
            getModel().removeModelElement( neutron );
        }

        // todo: I think that the rest of these calls are redundant
        nuclei.clear();
        neutrons.clear();
        u239Nuclei.clear();
        u235Nuclei.clear();
        u238Nuclei.clear();


        List nuclearModelElements = ( (NuclearPhysicsModel)getModel() ).getNuclearModelElements();
        while( nuclearModelElements.size() > 0 ) {
            ModelElement modelElement = (ModelElement)nuclearModelElements.get( 0 );
            getModel().removeModelElement( modelElement );
        }
        return;
    }

    //----------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------
    public class GraphicRemover implements NuclearModelElement.Listener {
        private PhetGraphic graphic;

        public GraphicRemover( PhetGraphic graphic ) {
            this.graphic = graphic;
        }

        public void leavingSystem( NuclearModelElement nme ) {
            getPhysicalPanel().removeGraphic( graphic );
        }
    }
}
