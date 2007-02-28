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
import edu.colorado.phet.hydrogenatom.model.DeBroglieModel;
import edu.colorado.phet.hydrogenatom.view.ModelViewManager.NodeFactory;
import edu.colorado.phet.hydrogenatom.view.atom.DeBroglieNode;
import edu.umd.cs.piccolo.PNode;

/**
 * DeBroglieNodeFactory creates PNodes that display
 * the deBroglie model of the hydrogen atom.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DeBroglieNodeFactory extends NodeFactory {

    public DeBroglieNodeFactory( PNode parent ) {
        super( DeBroglieModel.class, parent );
    }

    public PNode createNode( ModelElement modelElement ) {
        assert( modelElement instanceof DeBroglieModel );
        return new DeBroglieNode( (DeBroglieModel) modelElement );
    }
}
