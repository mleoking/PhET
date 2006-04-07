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
import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.mri.MriConfig;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.util.EventListener;
import java.util.ArrayList;
import java.util.Random;
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

    /**
     * Constructor
     */
    public MriModel() {
        this( null );
    }

    /**
     * Constructor
     *
     * @param listener
     */
    public MriModel( Listener listener ) {

        addListener( listener );

        // Sample Chamber
        sampleChamber = new SampleChamber( new Rectangle2D.Double( MriConfig.sampleChamberLocation.getX(),
                                                                   MriConfig.sampleChamberLocation.getY(),
                                                                   MriConfig.sampleChamberWidth,
                                                                   MriConfig.sampleChamberHeight ) );
        addModelElement( sampleChamber );

        // Magnets
        double magnetHeight = 50;
        Point2D upperMagnetLocation = new Point2D.Double( sampleChamber.getBounds().getX() + sampleChamber.getBounds().getWidth() / 2,
                                                          sampleChamber.getBounds().getY() - magnetHeight * 1.5 );
        upperMagnet = new Electromagnet( upperMagnetLocation,
                                                       sampleChamber.getBounds().getWidth(), magnetHeight );
        addModelElement( upperMagnet );
        Point2D lowerMagnetLocation = new Point2D.Double( sampleChamber.getBounds().getX() + sampleChamber.getBounds().getWidth() / 2,
                                                          sampleChamber.getBounds().getY() + sampleChamber.getBounds().getHeight() + magnetHeight * 1.5 );
        lowerMagnet = new Electromagnet( lowerMagnetLocation,
                                                       sampleChamber.getBounds().getWidth(), magnetHeight );
        lowerMagnet.addChangeListener( new DipoleOrientationAgent() );
        addModelElement( lowerMagnet );
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

    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    /**
     * Sets the spins of dipoles based on the field strength of the fading magnet
     */
    private class DipoleOrientationAgent implements Electromagnet.ChangeListener {
        private Random random = new Random();
        private double maxUpPFraction = 0.9;

        /**
         * When the field changes, the number of dipoles with each spin changes
         * @param event
         */
        public void stateChanged( Electromagnet.ChangeEvent event ) {
            double fieldStrength = upperMagnet.getFieldStrength();
            double fractionUp = 0.5 + ( 0.5 * fieldStrength / MriConfig.MAX_FADING_COIL_FIELD );
            fractionUp *= maxUpPFraction;
            for( int i = 0; i < dipoles.size(); i++ ) {
                Dipole dipole = (Dipole)dipoles.get( i );

                Spin spin = ((double)i) / dipoles.size() < fractionUp ? Spin.UP : Spin.DOWN;
//                Spin spin = random.nextDouble() < fractionUp ? Spin.UP : Spin.DOWN;
                dipole.setSpin( spin );
            }
        }
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
}
