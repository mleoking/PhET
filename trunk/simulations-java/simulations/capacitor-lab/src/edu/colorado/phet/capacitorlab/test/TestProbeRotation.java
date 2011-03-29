// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.test;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.capacitorlab.CLImages;
import edu.colorado.phet.capacitorlab.view.PlusNode;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Confirm that crosshairs of the E-Field Detector probe remain aligned with origin when the probe is rotated.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestProbeRotation extends JFrame {

    // field detector probe, rotated so that it's aligned with the pseudo-3D perspective.
    // origin at center of image crosshairs.
    private static class ProbeNode extends PhetPNode {
        public ProbeNode() {
            PImage imageNode = new PImage( CLImages.EFIELD_PROBE );
            addChild( imageNode );
            imageNode.scale( 0.65 ); // scale before setting offset!
            double x = -imageNode.getFullBoundsReference().getWidth() / 2;
            double y = -( 0.078 * imageNode.getFullBoundsReference().getHeight() ); // multiplier is dependent on where crosshairs appear in image file
            imageNode.setOffset( x, y );
        }
    }

    public static class TestCanvas extends PhetPCanvas {
        public TestCanvas() {
            setPreferredSize( new Dimension( 800, 600 ) );

            PlusNode plusNode = new PlusNode( 20, 2, Color.RED );
            ProbeNode probe1 = new ProbeNode();
            ProbeNode probe2 = new ProbeNode();

            getLayer().addChild( probe1 );
            getLayer().addChild( probe2 );
            getLayer().addChild( plusNode );

            plusNode.setOffset( 200, 200 );
            probe1.setOffset( plusNode.getOffset() );
            probe2.setOffset( plusNode.getOffset() );

            // rotate one of the probes, visually confirm that crosshairs are still aligned
            probe1.rotate( Math.toRadians( 45 ) );
        }
    }

    public TestProbeRotation() {
        super( TestProbeRotation.class.getName() );
        setContentPane( new TestCanvas() );
        pack();
    }

    public static void main( String[] args ) {
        JFrame frame = new TestProbeRotation();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        SwingUtils.centerWindowOnScreen( frame );
        frame.setVisible( true );
    }
}
