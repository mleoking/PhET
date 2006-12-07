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
 * <p>
 * The model has a sample and two SimpleMagnets, one above the sample and
 * one below. It can also have additional magnets. In particular these are
 * GradienElectromagnets.
 * <p>
 * The model also contains a RadiowaveSource. The primary representation of
 * radio waves is with photons. There is a radio wave source that generates
 * photons.
 * <p>
 * A DipoleMonitor object keeps track of the dipoles in the model and their
 * spin states, and a DipolOrientationAgent is used to determine the desired
 * ratio of up to down spin dipoles.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MriModel extends BaseModel implements IDipoleMonitor {

    private Rectangle2D bounds;
    private SimpleMagnet upperMagnet;
    private SimpleMagnet lowerMagnet;
    private ArrayList magnets = new ArrayList();
    private ArrayList photons = new ArrayList();
    private Sample sample;
    private DipoleOrientationAgent dipoleOrientationAgent;
    private SampleMaterial sampleMaterial;
    private RadiowaveSource radiowaveSource;
    private DipoleMonitor dipoleMonitor;


    /**
     * Constructor
     *
     * @param clock
     */
    public MriModel( IClock clock, Rectangle2D bounds, Sample sample ) {

        this.dipoleMonitor = new DipoleMonitor( this );
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
                                                          sampleChamberBounds.getY() - magnetHeight * 1.0 );
        upperMagnet = new SimpleMagnet( upperMagnetLocation,
                                        sampleChamberBounds.getWidth(),
                                        magnetHeight,
                                        clock,
                                        GradientElectromagnet.HORIZONTAL );
        addModelElement( upperMagnet );
        Point2D lowerMagnetLocation = new Point2D.Double( sampleChamberBounds.getX() + sampleChamberBounds.getWidth() / 2,
                                                          sampleChamberBounds.getY() + sampleChamberBounds.getHeight() + magnetHeight * 2 );
        lowerMagnet = new SimpleMagnet( lowerMagnetLocation,
                                        sampleChamberBounds.getWidth(),
                                        magnetHeight,
                                        clock,
                                        GradientElectromagnet.HORIZONTAL );
        addModelElement( lowerMagnet );

        // Radiowave Source
        double length = MriConfig.SAMPLE_CHAMBER_WIDTH * 1.1;
        radiowaveSource = new RadiowaveSource( new Point2D.Double( MriConfig.SAMPLE_CHAMBER_LOCATION.getX() + MriConfig.SAMPLE_CHAMBER_WIDTH / 2,
                                                                   lowerMagnetLocation.getY() + lowerMagnet.getBounds().getHeight() ),
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

        // Add thermal noise
        ThermalNoise thermalNoise = new ThermalNoise( MriConfig.MEAN_THERMAL_NOISE_INJECTION_TIME, this );
        addModelElement( thermalNoise );

        // Create an agent that will detect photon-atom collisions
        addModelElement( new CollisionAgent( this ) );

        // Set initial conditions
        setInitialConditions();
    }

    private void setInitialConditions() {
        upperMagnet.setFieldStrength( MriConfig.InitialConditions.FADING_MAGNET_FIELD );
        lowerMagnet.setFieldStrength( MriConfig.InitialConditions.FADING_MAGNET_FIELD );
    }

    public void addModelElement( ModelElement modelElement ) {
        super.addModelElement( modelElement );

        if( modelElement instanceof Photon ) {
            photons.add( modelElement );
        }
        if( modelElement instanceof Electromagnet ) {
            Electromagnet magnet = (Electromagnet)modelElement;
            magnets.add( magnet );
            magnet.addChangeListener( new MagnetListener() );
        }
        listenerProxy.modelElementAdded( modelElement );
    }

    public void removeModelElement( ModelElement modelElement ) {
        super.removeModelElement( modelElement );
        if( modelElement instanceof Photon ) {
            photons.remove( modelElement );
            ( (Photon)modelElement ).removeFromSystem();
        }
        if( modelElement instanceof PlaneWaveMedium ) {
            ( (PlaneWaveMedium)modelElement ).leaveSystem();
        }
        if( modelElement instanceof Electromagnet ) {
            Electromagnet magnet = (Electromagnet)modelElement;
            magnets.remove( modelElement );
            magnet.removeChangeListener( new MagnetListener() );
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
        return dipoleMonitor.getDipoles();
    }

    public List getUpDipoles() {
        return dipoleMonitor.getUpDipoles();
    }

    public List getDownDipoles() {
        return dipoleMonitor.getDownDipoles();
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
     * @param p the point at which the field magnitude is to be evaluated
     * @return the total B field magnitude at the specified location
     */
    public double getTotalFieldStrengthAt( Point2D p ) {
        double b = 0;
        for( int i = 0; i < magnets.size(); i++ ) {
            GradientElectromagnet magnet = (GradientElectromagnet)magnets.get( i );
            b += magnet.getFieldStrengthAtAbsolute( p );
        }
        return b;
    }

    /**
     * Determines what fraction of the dipoles should be spin down, based on the field strength.
     *
     * @return
     */
    public double determineDesiredFractionDown() {
        double fieldStrength = getTotalFieldStrengthAt( new Point2D.Double() );
        double fractionDown = 0.5 + ( 0.5 * fieldStrength / MriConfig.MAX_FADING_COIL_FIELD );
//        double fractionDown = 1 - ( 0.5 + ( 0.5 * fieldStrength / MriConfig.MAX_FADING_COIL_FIELD ) );
        fractionDown *= MriConfig.MAX_SPIN_DOWN_FRACTION;

        System.out.println( "fractionDown = " + fractionDown );
        return fractionDown;
    }

    //----------------------------------------------------------------
    // Time dependent behavior
    //----------------------------------------------------------------

    protected void stepInTime( double dt ) {
        super.stepInTime( dt );

        List photons = getPhotons();
        for( int i = photons.size() - 1; i >= 0; i-- ) {
            Photon photon = (Photon)photons.get( i );
            if( !getBounds().contains( photon.getPosition() ) ) {
                removeModelElement( photon );
            }
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Inner classes
    //--------------------------------------------------------------------------------------------------
    private class MagnetListener implements Electromagnet.ChangeListener {
        public void stateChanged( Electromagnet.ChangeEvent event ) {
            listenerProxy.fieldChanged( MriModel.this );
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

        void fieldChanged( MriModel model );
    }

    public static abstract class ChangeAdapter implements ChangeListener {
        public void modelElementAdded( ModelElement modelElement ) {
        }

        public void modelElementRemoved( ModelElement modelElement ) {
        }

        public void sampleMaterialChanged( SampleMaterial sampleMaterial ) {
        }

        public void fieldChanged( MriModel model ) {
        }
    }

    //----------------------------------------------------------------
    // Debug & design methods
    //----------------------------------------------------------------
    public void setSpinDeterminationPolicy( DipoleOrientationAgent.SpinDeterminationPolicy policy ) {
        dipoleOrientationAgent.setPolicy( policy );
    }
}
