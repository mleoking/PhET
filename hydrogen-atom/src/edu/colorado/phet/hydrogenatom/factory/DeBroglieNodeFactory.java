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

import edu.colorado.phet.hydrogenatom.model.DeBroglieModel;
import edu.colorado.phet.hydrogenatom.model.IModelObject;
import edu.colorado.phet.hydrogenatom.view.ModelViewManager.NodeFactory;
import edu.colorado.phet.hydrogenatom.view.atom.DeBroglieNode;
import edu.umd.cs.piccolo.PNode;


public class DeBroglieNodeFactory extends NodeFactory {

    public DeBroglieNodeFactory( PNode parent ) {
        super( DeBroglieModel.class, parent );
    }

    public PNode createNode( IModelObject modelObject ) {
        return new DeBroglieNode( (DeBroglieModel) modelObject );
    }
}
