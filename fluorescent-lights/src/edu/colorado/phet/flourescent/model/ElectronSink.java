/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.flourescent.model;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.mechanics.Body;

import java.util.*;
import java.awt.geom.Point2D;
import java.awt.geom.Line2D;

/**
 * ElectronSource
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ElectronSink extends Body implements ElectronSource.Listener {

    private Point2D p1;
    private Point2D p2;

    private BaseModel model;
    private List electrons = new ArrayList();
    private Line2D.Double line;

    /**
     * Absorbs electrons along a line between two points
     * @param model
     * @param p1 One endpoint of the line
     * @param p2 The other endpoint of the line
     */
    public ElectronSink( BaseModel model, Point2D p1, Point2D p2) {
        this.model = model;
        this.p1 = p1;
        this.p2 = p2;
        this.line = new Line2D.Double( p1, p2 );
    }

    /**
     * Removes electrons that have crossed the line defined by the electron sink
     * @param dt
     */
    public void stepInTime( double dt ) {
        super.stepInTime( dt );

        // Look for electrons that should be absorbed
        for( int i = 0; i < electrons.size(); i++ ) {
            Electron electron = (Electron)electrons.get( i );
            if( line.intersectsLine( electron.getPosition().getX(), electron.getPosition().getY(),
                                 electron.getPositionPrev().getX(), electron.getPositionPrev().getY() )) {
                model.removeModelElement( electron );
                listenerProxy.electronAbsorbed( new ElectronSinkEvent( this, electron ) );
            }
        }
    }

    //----------------------------------------------------------------
    // Events and listeners
    //----------------------------------------------------------------
    private EventChannel listenerChannel = new EventChannel( Listener.class );
    private Listener listenerProxy = (Listener)listenerChannel.getListenerProxy();

    public interface Listener extends EventListener {
        void electronAbsorbed( ElectronSinkEvent event );
    }

    public Point2D getCM() {
        return getPosition();
    }

    public double getMomentOfInertia() {
        return 0;
    }

    /**
     * Event class for the removal of electrons
     */
    public class ElectronSinkEvent extends EventObject {
        private Electron electron;

        public ElectronSinkEvent( Object source, Electron electron ) {
            super( source );
            this.electron = electron;
        }

        public Electron getElectron() {
            return electron;
        }
    }

    public void addListener( Listener listener ) {
        listenerChannel.addListener( listener );
    }

    public void removeListener( Listener listener ) {
        listenerChannel.removeListener( listener );
    }

    //-----------------------------------------------------------------
    // ElectronSource.Listener implementation
    //-----------------------------------------------------------------
    public void electronProduced( ElectronSource.ElectronSourceEvent event ) {
        electrons.add( event.getElectron() );
    }
}
