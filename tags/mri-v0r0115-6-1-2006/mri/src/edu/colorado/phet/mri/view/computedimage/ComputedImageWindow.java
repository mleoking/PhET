/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.view.computedimage;

import edu.colorado.phet.common.util.PhetUtilities;
import edu.colorado.phet.mri.model.Detector;
import edu.colorado.phet.mri.model.SampleScannerB;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * ComputeImageWindow
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ComputedImageWindow extends JDialog {

    public ComputedImageWindow( Rectangle2D sampleBounds, SampleScannerB scanner, Detector detector ) throws HeadlessException {
//    public ComputedImageWindow( Rectangle2D sampleBounds, SampleScanner scanner, Detector detector ) throws HeadlessException {
        super( PhetUtilities.getPhetFrame(), false );
        setSize( (int)sampleBounds.getWidth() * 2, (int)sampleBounds.getHeight() * 2 );
        ComputedImageCanvas computedImageCanvas = new ComputedImageCanvas();
        setContentPane( computedImageCanvas );
        ComputedImagePNode node = new ComputedImagePNode( sampleBounds, scanner );
        computedImageCanvas.addScreenChild( node );

//        node.addSpot( new Point2D.Double( 200, 200), .6 );
    }
}
