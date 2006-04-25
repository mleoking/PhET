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

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.quantum.model.Photon;
import edu.colorado.phet.quantum.model.PhotonEmissionListener;
import edu.colorado.phet.quantum.model.PhotonEmittedEvent;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

/**
 * MriModel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MriModel extends BaseModel {

    private Rectangle2D bounds = new Rectangle2D.Double( 0, 0, 1000, 700 );
    private GradientElectromagnet upperMagnet, lowerMagnet;
//    private Electromagnet upperMagnet, lowerMagnet;
    private GradientElectromagnet gradientMagnet;
    private ArrayList dipoles = new ArrayList();
    ArrayList photons = new ArrayList();
    private Sample sample;
    private DipoleOrientationAgent dipoleOrientationAgent;
    private SampleMaterial sampleMaterial;
    private RadiowaveSource radiowaveSource;

    /**
     * Constructor
     *
     * @param clock
     */
    public MriModel( IClock clock, Rectangle2D bounds, Sample sample ) {
        this.bounds = bounds;

        // Sample Chamber
        addModelElement( sample );

        // Magnets
        double magnetHeight = MriConfig.MAX_FADING_HEIGHT;
        Rectangle2D sampleChamberBounds = new Rectangle2D.Double( MriConfig.SAMPLE_CHAMBER_LOCATION.getX(),
                                                                  MriConfig.SAMPLE_CHAMBER_LOCATION.getY(),
                                                                  MriConfig.SAMPLE_CHAMBER_WIDTH,
                                                                  MriConfig.SAMPLE_CHAMBER_HEIGHT );
        Point2D upperMagnetLocation = new Point2D.Double( sampleChamberBounds.getX() + sampleChamberBounds.getWidth() / 2,
                                                          sampleChamberBounds.getY() - magnetHeight * 1.5 );
        upperMagnet = new GradientElectromagnet( upperMagnetLocation,
                                                 sampleChamberBounds.getWidth(),
                                                 magnetHeight,
                                                 clock,
                                                 new GradientElectromagnet.Constant() );
        addModelElement( upperMagnet );
        Point2D lowerMagnetLocation = new Point2D.Double( sampleChamberBounds.getX() + sampleChamberBounds.getWidth() / 2,
                                                          sampleChamberBounds.getY() + sampleChamberBounds.getHeight() + magnetHeight * 1.5 );
        lowerMagnet = new GradientElectromagnet( lowerMagnetLocation,
                                                 sampleChamberBounds.getWidth(),
                                                 magnetHeight, clock,
                                                 new GradientElectromagnet.Constant() );
        addModelElement( lowerMagnet );


        // Radiowave Source
        double length = MriConfig.SAMPLE_CHAMBER_WIDTH * 1.1;
        radiowaveSource = new RadiowaveSource( new Point2D.Double( MriConfig.SAMPLE_CHAMBER_LOCATION.getX() + MriConfig.SAMPLE_CHAMBER_WIDTH / 2,
                                                                   MriConfig.SAMPLE_CHAMBER_LOCATION.getY()
                                                                   + MriConfig.SAMPLE_CHAMBER_HEIGHT + 140 ),
                                               length,
                                               new Vector2D.Double( 0, -1 ) );
        addModelElement( radiowaveSource );
        radiowaveSource.setEnabled( true );
        radiowaveSource.addPhotonEmissionListener( new PhotonEmissionListener() {
            public void photonEmitted( PhotonEmittedEvent event ) {
                addModelElement( event.getPhoton() );
            }
        } );

        // Add the wave medium that the radio wave source emits into
        PlaneWaveMedium planeWaveMedium = new PlaneWaveMedium( radiowaveSource,
                                                               radiowaveSource.getPosition(),
                                                               radiowaveSource.getLength(),
                                                               800,
                                                               PlaneWaveMedium.NORTH,
                                                               10 );
        addModelElement( planeWaveMedium );

        // Create agent that will control the spin orientations of the dipoles
        dipoleOrientationAgent = new DipoleOrientationAgent( this );
        lowerMagnet.addChangeListener( dipoleOrientationAgent );

        // Create an agent that will detect photon-atom collisions
        addModelElement( new CollisionAgent( this ) );

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
        if( modelElement instanceof Photon ) {
            photons.add( modelElement );
        }
        listenerProxy.modelElementAdded( modelElement );
    }

    public void removeModelElement( ModelElement modelElement ) {
        super.removeModelElement( modelElement );

        if( modelElement instanceof Dipole ) {
            dipoles.remove( modelElement );
        }
        if( modelElement instanceof Photon ) {
            photons.remove( modelElement );
            ((Photon)modelElement).removeFromSystem();
        }
        listenerProxy.modelElementRemoved( modelElement );
    }

    public List getModelElements() {
        ArrayList modelElements = new ArrayList();
        for( int i = 0; i < numModelElements(); i++ ) {
            modelElements.add( modelElementAt( i ) );
        }
        return modelElements;
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

    public ArrayList getPhotons() {
        return photons;
    }

    public Sample getSample() {
        return sample;
    }

    public void setSample( Sample sample ) {
        this.sample = sample;
        addModelElement( sample );
    }

    public SampleMaterial getSampleMaterial() {
        return sampleMaterial;
    }

    public void setSampleMaterial( SampleMaterial currentSampleMaterial ) {
        this.sampleMaterial = currentSampleMaterial;
        listenerProxy.sampleMaterialChanged( sampleMaterial );
    }

    public RadiowaveSource getRadiowaveSource() {
        return radiowaveSource;
    }

    public GradientElectromagnet getGradientMagnet() {
        return gradientMagnet;
    }

    public void setGradientMagnet( GradientElectromagnet gradientMagnet ) {
        this.gradientMagnet = gradientMagnet;
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

    /**
     * Returns the total B field magnitude at a particular x location
     *
     * @param x
     * @return the total B field magnitude at the specified location
     */
    public double getTotalFieldStrengthAt( double x ) {
        double b = lowerMagnet.getFieldStrength();
        if( gradientMagnet != null ) {
            b += gradientMagnet.getFieldStrengthAt( x );
        }
        return b;
    }

    //----------------------------------------------------------------
    // Time dependent behavior
    //----------------------------------------------------------------

    protected void stepInTime( double dt ) {
        super.stepInTime( dt );

        List photons = getPhotons();
        for( int i = 0; i < photons.size(); i++ ) {
            Photon photon = (Photon)photons.get( i );
            if( !getBounds().contains( photon.getPosition() ) ) {
                removeModelElement( photon );
            }
        }
    }


    //----------------------------------------------------------------
    // Events and listeners
    //----------------------------------------------------------------
    private EventChannel modelEventChannel = new EventChannel( ChangeListener.class );
    private ChangeListener listenerProxy = (ChangeListener)modelEventChannel.getListenerProxy();

    public void addListener( ChangeListener listener ) {
        modelEventChannel.addListener( listener );
    }

    public void removeListener( ChangeListener listener ) {
        modelEventChannel.removeListener( listener );
    }

    public static interface ChangeListener extends EventListener {
        void modelElementAdded( ModelElement modelElement );

        void modelElementRemoved( ModelElement modelElement );

        void sampleMaterialChanged( SampleMaterial sampleMaterial );
    }

    public static abstract class ChangeAdapter implements ChangeListener {
        public void modelElementAdded( ModelElement modelElement ) {
        }

        public void modelElementRemoved( ModelElement modelElement ) {
        }

        public void sampleMaterialChanged( SampleMaterial sampleMaterial ) {
        }
    }

    //----------------------------------------------------------------
    // Debug & design methods
    //----------------------------------------------------------------
    public void setSpinDeterminationPolicy( DipoleOrientationAgent.SpinDeterminationPolicy policy ) {
        dipoleOrientationAgent.setPolicy( policy );
    }
}
