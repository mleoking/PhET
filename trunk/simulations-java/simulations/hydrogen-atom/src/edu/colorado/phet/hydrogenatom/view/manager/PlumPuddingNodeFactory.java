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
import edu.colorado.phet.hydrogenatom.model.PlumPuddingModel;
import edu.colorado.phet.hydrogenatom.view.atom.PlumPuddingNode;
import edu.colorado.phet.hydrogenatom.view.manager.ModelViewManager.NodeFactory;
import edu.umd.cs.piccolo.PNode;

/**
 * PlumPuddingNodeFactory creates PNodes that display
 * the "plum pudding" model of the hydrogen atom.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PlumPuddingNodeFactory extends NodeFactory {

    public PlumPuddingNodeFactory( PNode parent ) {
        super( PlumPuddingModel.class, parent );
    }

    public PNode createNode( ModelElement modelElement ) {
        assert( modelElement instanceof PlumPuddingModel );
        return new PlumPuddingNode( (PlumPuddingModel) modelElement );
    }
}
