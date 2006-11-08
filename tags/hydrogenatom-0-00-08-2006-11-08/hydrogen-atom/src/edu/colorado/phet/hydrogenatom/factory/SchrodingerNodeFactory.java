/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.factory;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.hydrogenatom.model.SchrodingerModel;
import edu.colorado.phet.hydrogenatom.view.ModelViewManager.NodeFactory;
import edu.colorado.phet.hydrogenatom.view.atom.SchrodingerNode;
import edu.umd.cs.piccolo.PNode;

/**
 * SchrodingerNodeFactory creates PNodes that display
 * the Schrodinger model of the hydrogen atom.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class SchrodingerNodeFactory extends NodeFactory {

    public SchrodingerNodeFactory( PNode parent ) {
        super( SchrodingerModel.class, parent );
    }

    public PNode createNode( ModelElement modelElement ) {
        assert( modelElement instanceof SchrodingerModel );
        return new SchrodingerNode( (SchrodingerModel) modelElement );
    }
}
