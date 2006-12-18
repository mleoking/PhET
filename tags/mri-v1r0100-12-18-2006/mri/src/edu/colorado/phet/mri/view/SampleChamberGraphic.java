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

import edu.colorado.phet.mri.model.SampleChamber;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;

/**
 * SampleChamberGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SampleChamberGraphic extends PNode {
    public static final Color BACKGROUND = new Color( 200, 200, 255 );

    public SampleChamberGraphic( SampleChamber sampleChamber ) {
        PNode boundsGraphic = new PPath( sampleChamber.getBounds() );
        boundsGraphic.setPaint( BACKGROUND );
        addChild( boundsGraphic );
    }

}
