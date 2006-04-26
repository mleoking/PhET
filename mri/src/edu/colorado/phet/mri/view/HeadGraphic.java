/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.view;

import edu.umd.cs.piccolo.PNode;
import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.colorado.phet.mri.MriConfig;

import java.awt.*;

/**
 * HeadGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class HeadGraphic extends PNode {

    public HeadGraphic() {
//        PNode headGraphic = PImageFactory.create( MriConfig.HEAD_IMAGE );
        PNode headGraphic = PImageFactory.create( MriConfig.HEAD_IMAGE,
                                                  new Dimension( (int)MriConfig.SAMPLE_CHAMBER_WIDTH,
                                                                 (int)MriConfig.SAMPLE_CHAMBER_WIDTH ) );
        addChild( headGraphic );
    }
}
