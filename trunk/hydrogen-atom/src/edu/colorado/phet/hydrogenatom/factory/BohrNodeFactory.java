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

import edu.colorado.phet.hydrogenatom.model.BohrModel;
import edu.colorado.phet.hydrogenatom.model.IModelObject;
import edu.colorado.phet.hydrogenatom.view.ModelViewManager.NodeFactory;
import edu.colorado.phet.hydrogenatom.view.atom.BohrNode;
import edu.umd.cs.piccolo.PNode;


public class BohrNodeFactory extends NodeFactory {

    public BohrNodeFactory( PNode parent ) {
        super( BohrModel.class, parent );
    }

    public PNode createNode( IModelObject modelObject ) {
        return new BohrNode( (BohrModel) modelObject );
    }
}
