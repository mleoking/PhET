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
import edu.colorado.phet.hydrogenatom.model.ExperimentModel;
import edu.colorado.phet.hydrogenatom.model.IModelObject;
import edu.colorado.phet.hydrogenatom.view.atom.ExperimentNode;
import edu.umd.cs.piccolo.PNode;


public class ExperimentNodeFactory extends NodeFactory {

    public ExperimentNodeFactory( PNode parent ) {
        super( ExperimentModel.class, parent );
    }

    public PNode createNode( IModelObject modelObject ) {
        return new ExperimentNode( (ExperimentModel) modelObject );
    }
}
