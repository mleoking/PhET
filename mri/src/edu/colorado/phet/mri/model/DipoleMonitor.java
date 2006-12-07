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

import edu.colorado.phet.common.model.ModelElement;

import java.util.ArrayList;
import java.util.List;

/**
 * DipoleMonitor
 * <p>
 * An agent that keeps track of the dipoles in the model, including keeping track of which
 * ones have spin up and which have spin down.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class DipoleMonitor extends MriModel.ChangeAdapter implements Dipole.ChangeListener, IDipoleMonitor {

    private List dipoles = new ArrayList();
    private List upDipoles = new ArrayList();
    private List downDipoles = new ArrayList();

    public DipoleMonitor( MriModel mriModel ) {
        mriModel.addListener( this );
    }

    public void modelElementAdded( ModelElement modelElement ) {
        if( modelElement instanceof Dipole ) {
            Dipole dipole = (Dipole)modelElement;
            dipoles.add( dipole );
            List spinList = getSpinList( dipole );
            spinList.add( dipole );
            dipole.addChangeListener( this );
        }
    }

    public void modelElementRemoved( ModelElement modelElement ) {
        if( modelElement instanceof Dipole ) {
            Dipole dipole = (Dipole)modelElement;
            dipoles.remove( dipole );
            getSpinList( dipole ).remove( dipole );
            dipole.removeChangeListener( this );
        }
    }

    public void spinChanged( Dipole.ChangeEvent event ) {
        Dipole dipole = (Dipole)event.getDipole();
        upDipoles.remove( dipole );
        downDipoles.remove( dipole );
        getSpinList( dipole ).add( dipole );
    }

    private List getSpinList( Dipole dipole ) {
        List spinList = dipole.getSpin() == Spin.DOWN ? downDipoles : upDipoles;
        return spinList;
    }

    public List getDipoles() {
        return dipoles;
    }

    public List getUpDipoles() {
        return upDipoles;
    }

    public List getDownDipoles() {
        return downDipoles;
    }
}
