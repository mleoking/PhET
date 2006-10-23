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

import edu.colorado.phet.hydrogenatom.factory.ModelViewManager.NodeFactory;
import edu.colorado.phet.hydrogenatom.model.IModelObject;
import edu.colorado.phet.hydrogenatom.model.SchrodingerModel;
import edu.colorado.phet.hydrogenatom.view.atom.SchrodingerNode;
import edu.umd.cs.piccolo.PNode;


public class SchrodingerNodeFactory extends NodeFactory {

    public SchrodingerNodeFactory( PNode parent ) {
        super( SchrodingerModel.class, parent );
    }

    public PNode createNode( IModelObject modelObject ) {
        return new SchrodingerNode( (SchrodingerModel) modelObject );
    }
}
