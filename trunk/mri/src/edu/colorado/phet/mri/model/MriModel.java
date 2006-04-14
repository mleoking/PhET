/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.model;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.mri.MriConfig;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.util.EventListener;
import java.util.ArrayList;
import java.util.List;

/**
 * MriModel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MriModel extends BaseModel {

    private Rectangle2D bounds = new Rectangle2D.Double( 0, 0, 1000, 700 );
    private Electromagnet upperMagnet, lowerMagnet;
    private ArrayList dipoles = new ArrayList();
    private SampleChamber sampleChamber;
    private DipoleOrientationAgent dipoleOrientationAgent;
    private SampleMaterial sampleMaterial;

    /**
     * Constructor
     */
    /**
     * Constructor
     *
     * @param listener An MriModel.Listener. Null is allowed.
     */
    public MriModel( IClock clock, Listener listener ) {

        addListener( listener );

        // Sample Chamber
        sampleChamber = new SampleChamber( new Rectangle2D.Double( MriConfig.SAMPLE_CHAMBER_LOCATION.getX(),
                                                                   MriConfig.SAMPLE_CHAMBER_LOCATION.getY(),
                                                                   MriConfig.SAMPLE_CHAMBER_WIDTH,
                                                                   MriConfig.SAMPLE_CHAMBER_HEIGHT ) );
        addModelElement( sampleChamber );

        // Magnets
        double magnetHeight = MriConfig.MAX_FADING_HEIGHT;
        Point2D upperMagnetLocation = new Point2D.Double( sampleChamber.getBounds().getX() + sampleChamber.getBounds().getWidth() / 2,
                                                          sampleChamber.getBounds().getY() - magnetHeight * 1.5 );
        upperMagnet = new Electromagnet( upperMagnetLocation, sampleChamber.getBounds().getWidth(), magnetHeight, clock );
        addModelElement( upperMagnet );
        Point2D lowerMagnetLocation = new Point2D.Double( sampleChamber.getBounds().getX() + sampleChamber.getBounds().getWidth() / 2,
                                                          sampleChamber.getBounds().getY() + sampleChamber.getBounds().getHeight() + magnetHeight * 1.5 );
        lowerMagnet = new Electromagnet( lowerMagnetLocation, sampleChamber.getBounds().getWidth(), magnetHeight, clock );
        addModelElement( lowerMagnet );

        // Create agent that will control the spin orientations of the dipoles
        dipoleOrientationAgent = new DipoleOrientationAgent( this );
        lowerMagnet.addChangeListener( dipoleOrientationAgent );
        addModelElement( dipoleOrientationAgent );

        // Set initial conditions
        setInitialConditions();
    }

    private void setInitialConditions() {
        upperMagnet.setCurrent( MriConfig.InitialConditions.FADING_MAGNET_CURRENT );
        lowerMagnet.setCurrent( MriConfig.InitialConditions.FADING_MAGNET_CURRENT );
    }

    public void addModelElement( ModelElement modelElement ) {
        super.addModelElement( modelElement );

        if( modelElement instanceof Dipole ) {
            dipoles.add( modelElement );
        }
        listenerProxy.modelElementAdded( modelElement );
    }

    public void removeModelElement( ModelElement modelElement ) {
        super.removeModelElement( modelElement );

        if( modelElement instanceof Dipole ) {
            dipoles.remove( modelElement );
        }
        listenerProxy.modelElementRemoved( modelElement );
    }

    //----------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------

    public Rectangle2D getBounds() {
        return bounds;
    }

    public Electromagnet getUpperMagnet() {
        return upperMagnet;
    }

    public Electromagnet getLowerMagnet() {
        return lowerMagnet;
    }

    public List getDipoles() {
        return this.dipoles;
    }

    public SampleChamber getSampleChamber() {
        return sampleChamber;
    }

    public SampleMaterial getSampleMaterial() {
        return sampleMaterial;
    }

    public void setSampleMaterial( SampleMaterial currentSampleMaterial ) {
        this.sampleMaterial = currentSampleMaterial;
    }

    /**
     * Returns the energy difference between spin up and spin down of the current
     * sample material in the fading magnet field
     *
     * @return the energy split
     */
    public double getSpinEnergyDifference() {
        return getLowerMagnet().getFieldStrength() * getSampleMaterial().getMu();
    }

    //----------------------------------------------------------------
    // Events and listeners
    //----------------------------------------------------------------
    private EventChannel modelEventChannel = new EventChannel( Listener.class );
    private Listener listenerProxy = (Listener)modelEventChannel.getListenerProxy();

    public void addListener( Listener listener ) {
        modelEventChannel.addListener( listener );
    }

    public void removeListener( Listener listener ) {
        modelEventChannel.removeListener( listener );
    }

    public interface Listener extends EventListener {
        void modelElementAdded( ModelElement modelElement );

        void modelElementRemoved( ModelElement modelElement );
    }

    //----------------------------------------------------------------
    // Debug & design methods
    //----------------------------------------------------------------
    public void setSpinDeterminationPolicy( DipoleOrientationAgent.SpinDeterminationPolicy policy ) {
        dipoleOrientationAgent.setPolicy( policy );
    }
}
