/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.view.factories;

import edu.colorado.phet.molecularreactions.util.ModelElementGraphicManager;
import edu.colorado.phet.molecularreactions.model.ProvisionalBond;
import edu.colorado.phet.molecularreactions.view.ProvisionalBondGraphic;
import edu.colorado.phet.common.model.ModelElement;
import edu.umd.cs.piccolo.PNode;

/**
 * ProvisionalBondFactory
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ProvisionalBondGraphicFactory extends ModelElementGraphicManager.GraphicFactory {

    public ProvisionalBondGraphicFactory( PNode layer ) {
        super( ProvisionalBond.class, layer );
    }

    public PNode createGraphic( ModelElement modelElement ) {
        return new ProvisionalBondGraphic( (ProvisionalBond)modelElement );
    }
}

