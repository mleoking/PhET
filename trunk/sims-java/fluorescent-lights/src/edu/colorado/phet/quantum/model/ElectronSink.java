/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.quantum.model;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.util.EventChannel;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.*;

/**
 * ElectronSource
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ElectronSink implements ModelElement, ElectronSource.ElectronProductionListener {

    private BaseModel model;
    private Line2D.Double line;
    private List electrons = new ArrayList();
    // A utility list for electrons that are to be removed at each time step
    private ArrayList removeList = new ArrayList();

    /**
     * Absorbs electrons along a line between two points
     *
     * @param model
     * @param p1    One endpoint of the line
     * @param p2    The other endpoint of the line
     */
    public ElectronSink( BaseModel model, Point2D p1, Point2D p2 ) {
        this.model = model;
        this.line = new Line2D.Double( p1, p2 );
    }

    /**
     * Removes electrons that have crossed the line defined by the electron sink
     *
     * @param dt
     */
    public void stepInTime( double dt ) {

        // Look for electrons that should be absorbed
        for( int i = 0; i < electrons.size(); i++ ) {
            Electron electron = (Electron)electrons.get( i );
            if( line.intersectsLine( electron.getPosition().getX(), electron.getPosition().getY(),
                                     electron.getPositionPrev().getX(), electron.getPositionPrev().getY() ) ) {
                model.removeModelElement( electron );
                electronAbsorptionListenerProxy.electronAbsorbed( new ElectronAbsorptionEvent( this, electron ) );
                removeList.add( electron );
            }
        }
        for( Iterator iterator = removeList.iterator(); iterator.hasNext(); ) {
            Electron electron = (Electron)iterator.next();
            electron.leaveSystem();
        }
        electrons.removeAll( removeList );
        removeList.clear();
    }

    //----------------------------------------------------------------
    // Events and listeners
    //----------------------------------------------------------------
    private EventChannel listenerChannel = new EventChannel( ElectronAbsorptionListener.class );
    private ElectronAbsorptionListener electronAbsorptionListenerProxy = (ElectronAbsorptionListener)listenerChannel.getListenerProxy();

    public interface ElectronAbsorptionListener extends EventListener {
        void electronAbsorbed( ElectronAbsorptionEvent event );
    }

    /**
     * Event class for the removal of electrons
     */
    public class ElectronAbsorptionEvent extends EventObject {
        private Electron electron;

        public ElectronAbsorptionEvent( Object source, Electron electron ) {
            super( source );
            this.electron = electron;
        }

        public Electron getElectron() {
            return electron;
        }
    }

    public void addListener( ElectronAbsorptionListener electronAbsorptionListener ) {
        listenerChannel.addListener( electronAbsorptionListener );
    }

    public void removeListener( ElectronAbsorptionListener electronAbsorptionListener ) {
        listenerChannel.removeListener( electronAbsorptionListener );
    }

    //-----------------------------------------------------------------
    // ElectronSource.StateChangeListener implementation
    //-----------------------------------------------------------------
    public void electronProduced( ElectronSource.ElectronProductionEvent event ) {
        final Electron electron = event.getElectron();
        electrons.add( electron );
        electron.addChangeListener( new ElectronRemover() );
    }

    private class ElectronRemover extends Electron.ChangeListenerAdapter {
        public void leftSystem( Electron.ChangeEvent changeEvent ) {
            electrons.remove( changeEvent.getElectron() );
        }
    }
}
