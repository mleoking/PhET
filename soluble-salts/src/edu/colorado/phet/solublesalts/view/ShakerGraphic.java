/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.view;

import edu.colorado.phet.piccolo.PImageFactory;
import edu.colorado.phet.solublesalts.SolubleSaltsConfig;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * ShakerGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ShakerGraphic extends PNode {

    private PImage shakerImage;

    public ShakerGraphic() {
        shakerImage = PImageFactory.create( SolubleSaltsConfig.SHAKER_IMAGE_NAME );
        this.addChild( shakerImage );
    }
}
