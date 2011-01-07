// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.view.manager;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.hydrogenatom.model.ExperimentModel;
import edu.colorado.phet.hydrogenatom.view.atom.ExperimentNode;
import edu.colorado.phet.hydrogenatom.view.manager.ModelViewManager.NodeFactory;
import edu.umd.cs.piccolo.PNode;

/**
 * ExperimentNodeFactory creates PNodes that display the representation
 * of the hydrogen atom when the simulation is in "Experiment" mode.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ExperimentNodeFactory extends NodeFactory {

    public ExperimentNodeFactory( PNode parent ) {
        super( ExperimentModel.class, parent );
    }

    public PNode createNode( ModelElement modelElement ) {
        assert( modelElement instanceof ExperimentModel );
        return new ExperimentNode( (ExperimentModel) modelElement );
    }
}
