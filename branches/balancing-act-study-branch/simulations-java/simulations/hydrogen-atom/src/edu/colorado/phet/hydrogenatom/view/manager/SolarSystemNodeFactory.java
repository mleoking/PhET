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
import edu.colorado.phet.hydrogenatom.model.SolarSystemModel;
import edu.colorado.phet.hydrogenatom.view.atom.SolarSystemNode;
import edu.colorado.phet.hydrogenatom.view.manager.ModelViewManager.NodeFactory;
import edu.umd.cs.piccolo.PNode;

/**
 * SolarSystemNodeFactory creates PNodes that display
 * the "solar system" model of the hydrogen atom.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class SolarSystemNodeFactory extends NodeFactory {

    public SolarSystemNodeFactory( PNode parent ) {
        super( SolarSystemModel.class, parent );
    }

    public PNode createNode( ModelElement modelElement ) {
        assert( modelElement instanceof SolarSystemModel );
        return new SolarSystemNode( (SolarSystemModel) modelElement );
    }
}

