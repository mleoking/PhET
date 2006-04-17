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
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.quantum.model.PhotonEmissionListener;
import edu.colorado.phet.quantum.model.PhotonEmittedEvent;
import edu.colorado.phet.quantum.model.PhotonAtomCollisonExpert;
import edu.colorado.phet.quantum.model.Photon;

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
    ArrayList photons = new ArrayList( );
    private SampleChamber sampleChamber;
    private DipoleOrientationAgent dipoleOrientationAgent;
    private SampleMaterial sampleMaterial;
    private RadiowaveSource radiowaveSource;

    /**
     * Constructor
     *
     * @param clock
     */
    public MriModel( IClock clock ) {

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

        // Radiowave Source
        radiowaveSource = new RadiowaveSource( new Point2D.Double( MriConfig.SAMPLE_CHAMBER_LOCATION.getX() + MriConfig.SAMPLE_CHAMBER_WIDTH / 2,
                                                                   MriConfig.SAMPLE_CHAMBER_LOCATION.getY()
                                                                   + MriConfig.SAMPLE_CHAMBER_HEIGHT + 140 ),
                                               MriConfig.SAMPLE_CHAMBER_WIDTH,
                                               new Vector2D.Double( 0, -1 ));
        addModelElement( radiowaveSource );
        radiowaveSource.setEnabled( true );
        radiowaveSource.addPhotonEmissionListener( new PhotonEmissionListener() {
            public void photonEmitted( PhotonEmittedEvent event ) {
                addModelElement( event.getPhoton() );
            }
        } );

        // Create agent that will control the spin orientations of the dipoles
        dipoleOrientationAgent = new DipoleOrientationAgent( this );
        lowerMagnet.addChangeListener( dipoleOrientationAgent );
//        addModelElement( dipoleOrientationAgent );

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
            photons.add( modelElement);
        }
        listenerProxy.modelElementAdded( modelElement );
    }

    public void removeModelElement( ModelElement modelElement ) {
        super.removeModelElement( modelElement );

        if( modelElement instanceof Dipole ) {
            dipoles.remove( modelElement );
        }
        if( modelElement instanceof Photon ) {
            photons.remove( modelElement);
        }
        listenerProxy.modelElementRemoved( modelElement );
    }

    public List getModelElements() {
        ArrayList modelElements = new ArrayList( );
        for( int i = 0; i < numModelElements(); i++ ) {
            modelElements.add( modelElementAt( i ));
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

    public SampleChamber getSampleChamber() {
        return sampleChamber;
    }

    public SampleMaterial getSampleMaterial() {
        return sampleMaterial;
    }

    public void setSampleMaterial( SampleMaterial currentSampleMaterial ) {
        this.sampleMaterial = currentSampleMaterial;
        listenerProxy.sampleMaterialChanged( sampleMaterial );
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
    // Time dependent behavior
    //----------------------------------------------------------------

    protected void stepInTime( double dt ) {
        super.stepInTime( dt );
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
