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
import java.util.EventListener;
import java.util.ArrayList;
import java.util.Random;

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

    public Rectangle2D getBounds() {
        return bounds;
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

    public void setUpperMagnet( Electromagnet magnet ) {
//        setFadingMagnet( upperMagnet, magnet );
        if( upperMagnet != null ) {
            removeModelElement( upperMagnet );
        }
        upperMagnet = magnet;
        upperMagnet.addChangeListener( new FadingMagnetListener() );
        addModelElement( magnet );
    }

    public Electromagnet getUpperMagnet() {
        return upperMagnet;
    }

    public void setLowerMagnet( Electromagnet magnet ) {
        if( lowerMagnet != null ) {
            removeModelElement( lowerMagnet );
        }
        lowerMagnet = magnet;
        lowerMagnet.addChangeListener( new FadingMagnetListener() );
        addModelElement( magnet );
    }

    public Electromagnet getLowerMagnet() {
        return lowerMagnet;
    }

    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    private class FadingMagnetListener implements Electromagnet.ChangeListener {
        private Random random = new Random();

        /**
         * When the field changes, the number of dipoles with each spin changes
         * @param event
         */
        public void stateChanged( Electromagnet.ChangeEvent event ) {
            double fieldStrength = upperMagnet.getFieldStrength();
            double percentUp = 0.5 + ( 0.5 * fieldStrength / MriConfig.MAX_FADING_COIL_FIELD );
            for( int i = 0; i < dipoles.size(); i++ ) {
                Dipole dipole = (Dipole)dipoles.get( i );
                Spin spin = random.nextDouble() < percentUp ? Spin.UP : Spin.DOWN;
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
