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
import edu.colorado.phet.molecularreactions.model.CompositeMolecule;
import edu.colorado.phet.molecularreactions.util.ModelElementGraphicManager;
import edu.colorado.phet.molecularreactions.view.CompositeMoleculeGraphic;
import edu.umd.cs.piccolo.PNode;

/**
 * CompositeMoleculeGraphicFactory
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CompositeMoleculeGraphicFactory extends ModelElementGraphicManager.GraphicFactory {

    public CompositeMoleculeGraphicFactory( PNode layer ) {
        super( CompositeMolecule.class, layer );
    }

    public PNode createGraphic( ModelElement modelElement ) {
        if( modelElement instanceof CompositeMolecule ) {
            return new CompositeMoleculeGraphic( (CompositeMolecule)modelElement );
        }
        else {
            return null;
        }
    }
}
