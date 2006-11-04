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

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.molecularreactions.model.SimpleMolecule;
import edu.colorado.phet.molecularreactions.util.ModelElementGraphicManager;
import edu.colorado.phet.molecularreactions.view.SpatialSimpleMoleculeGraphic;
import edu.umd.cs.piccolo.PNode;

/**
 * SimpleMoleculeGraphicFactory
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SimpleMoleculeGraphicFactory extends ModelElementGraphicManager.GraphicFactory {

    public SimpleMoleculeGraphicFactory( PNode moleculeLayer ) {
        super( SimpleMolecule.class, moleculeLayer );
    }

    public PNode createGraphic( ModelElement modelElement ) {
        return new SpatialSimpleMoleculeGraphic( (SimpleMolecule)modelElement );
    }
}
