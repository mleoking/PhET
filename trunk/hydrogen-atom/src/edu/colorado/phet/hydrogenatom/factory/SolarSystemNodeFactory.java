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

import edu.colorado.phet.hydrogenatom.model.IModelObject;
import edu.colorado.phet.hydrogenatom.model.SolarSystemModel;
import edu.colorado.phet.hydrogenatom.view.ModelViewManager.NodeFactory;
import edu.colorado.phet.hydrogenatom.view.atom.SolarSystemNode;
import edu.umd.cs.piccolo.PNode;


public class SolarSystemNodeFactory extends NodeFactory {

    public SolarSystemNodeFactory( PNode parent ) {
        super( SolarSystemModel.class, parent );
    }

    public PNode createNode( IModelObject modelObject ) {
        return new SolarSystemNode( (SolarSystemModel) modelObject );
    }
}

