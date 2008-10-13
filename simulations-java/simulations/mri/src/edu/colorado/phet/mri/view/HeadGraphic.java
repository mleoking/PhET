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

import java.awt.Dimension;

import edu.colorado.phet.common.piccolophet.util.PImageFactory;
import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.mri.MriResources;
import edu.colorado.phet.mri.model.Head;
import edu.umd.cs.piccolo.PNode;

/**
 * HeadGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class HeadGraphic extends PNode {

    public HeadGraphic() {
//        PNode headGraphic = PImageFactory.createPrimaryBorder( MriConfig.HEAD_IMAGE );
        PNode headGraphic = PImageFactory.create( MriResources.getImage( MriConfig.HEAD_IMAGE ),
                                                  new Dimension( (int)MriConfig.SAMPLE_CHAMBER_WIDTH,
                                                                 (int)MriConfig.SAMPLE_CHAMBER_WIDTH ) );
        addChild( headGraphic );
    }

    public HeadGraphic( Head head ) {
        PNode headGraphic = PImageFactory.create( MriResources.getImage( MriConfig.HEAD_IMAGE ),
                                                  new Dimension( (int)head.getBounds().getWidth(),
                                                                 (int)head.getBounds().getHeight() ) );
        addChild( headGraphic );
    }
}
