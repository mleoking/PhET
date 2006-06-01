/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.view;

import edu.colorado.phet.mri.model.Dipole;
import edu.umd.cs.piccolo.PNode;

import java.util.HashMap;

/**
 * DipoleGraphicManager
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class DipoleGraphicManager implements Dipole.ClassListener {

    // The PNode on which all dipole graphics are placed
    private PNode canvas;
    // A map of dipoles to their graphics
    private HashMap dipoleToGraphicMap = new HashMap();

    public DipoleGraphicManager( PNode canvas ) {
        Dipole.addClassListener( this );
        this.canvas = canvas;
    }

    public void instanceCreated( Dipole dipole ) {
        DipoleGraphic dg = new DipoleGraphic( dipole );
        dipoleToGraphicMap.put( dipole, dg );
        canvas.addChild( dg );
    }

    public void instanceDestroyed( Dipole dipole ) {
        DipoleGraphic dg = (DipoleGraphic)dipoleToGraphicMap.get( dipole );
        if( dg == null ) {
            throw new IllegalArgumentException( "No graphic for dipole" );
        }
        canvas.removeChild( dg );
        dipoleToGraphicMap.remove( dipole );
    }
}
