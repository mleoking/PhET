// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.reactionsandrates.view.factories;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.reactionsandrates.model.Bond;
import edu.colorado.phet.reactionsandrates.util.ModelElementGraphicManager;
import edu.colorado.phet.reactionsandrates.view.BondGraphic;
import edu.umd.cs.piccolo.PNode;

/**
 * BondGraphicFactory
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class BondGraphicFactory extends ModelElementGraphicManager.GraphicFactory {

    public BondGraphicFactory( PNode layer ) {
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
