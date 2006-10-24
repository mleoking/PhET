/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.view;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.molecularreactions.model.Bond;
import edu.colorado.phet.molecularreactions.util.ModelElementGraphicManager;
import edu.umd.cs.piccolo.PNode;

/**
 * BondGraphicFactory
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class BondGraphicFactory extends ModelElementGraphicManager.GraphicFactory {

    protected BondGraphicFactory( PNode layer ) {
        super( Bond.class, layer );
    }

    public PNode createGraphic( ModelElement modelElement ) {
        BondGraphic bondGraphic = null;
        if( modelElement instanceof Bond ) {
            bondGraphic = new BondGraphic( (Bond)modelElement );
        }
        return bondGraphic;
    }
}
