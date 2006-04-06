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
import edu.colorado.phet.mri.model.MriModel;
import edu.colorado.phet.mri.model.SampleChamber;
import edu.colorado.phet.mri.model.Electromagnet;
import edu.colorado.phet.common.model.ModelElement;
import edu.umd.cs.piccolo.PNode;

import java.util.HashMap;
import java.awt.geom.Rectangle2D;

/**
 * DipoleGraphicManager
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class GraphicManager implements MriModel.Listener {

    // The PNode on which all dipole graphics are placed
    private PNode canvas;

    // A map of dipoles to their graphics
    private HashMap modelElementToGraphicMap = new HashMap();

    public GraphicManager( PNode canvas ) {
        this.canvas = canvas;
    }

    public void modelElementAdded( ModelElement modelElement ) {

        PNode graphic = null;
        if( modelElement instanceof Dipole ) {
            graphic = new DipoleGraphic( (Dipole)modelElement );
        }
        if( modelElement instanceof SampleChamber ) {
            graphic = new SampleChamberGraphic( (SampleChamber)modelElement );
        }
        if( modelElement instanceof Electromagnet ) {
            graphic = new ElectromagnetGraphic( (Electromagnet)modelElement );
        }

        if( graphic != null ) {
            modelElementToGraphicMap.put( modelElement, graphic );
            canvas.addChild( graphic );
        }
    }

    public void modelElementRemoved( ModelElement modelElement ) {
        PNode graphic = (PNode)modelElementToGraphicMap.get( modelElement );
        if( graphic != null ) {
            canvas.removeChild( graphic );
            modelElementToGraphicMap.remove( modelElement );
        }

    }
}
