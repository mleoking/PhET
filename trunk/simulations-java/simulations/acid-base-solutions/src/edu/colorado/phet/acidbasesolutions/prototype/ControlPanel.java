/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.prototype;

import java.awt.Color;
import java.awt.GridBagConstraints;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PCanvas;

/**
 * Control panel for the Magnifying Glass View prototype.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class ControlPanel extends JPanel {
    
    private final MagnifyingGlassControls magnifyingGlassControls;
    private final BeakerControls beakerControls;
    private final MoleculeCountPanel moleculeCountPanel;
    private final CanvasControls canvasControls;
    
    public ControlPanel( JFrame parentFrame, final PCanvas canvas, ProtoModel model ) {
        
        magnifyingGlassControls = new MagnifyingGlassControls( model.getMagnifyingGlass() );
        beakerControls = new BeakerControls( model.getBeaker() );
        moleculeCountPanel = new MoleculeCountPanel();
        canvasControls = new CanvasControls( parentFrame, canvas );
        
        JPanel innerPanel = new JPanel();
        add( innerPanel );
        
        EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
        layout.setFill( GridBagConstraints.HORIZONTAL );
        innerPanel.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( magnifyingGlassControls, row++, column );
        layout.addComponent( beakerControls, row++, column );
        layout.addComponent( moleculeCountPanel, row++, column );
        layout.addComponent( canvasControls, row++, column );
    }
    
    public static void main( String[] args ) {
        ProtoModel model = new ProtoModel();
        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setBackground( Color.RED );
        JFrame frame = new JFrame();
        frame.setContentPane( new ControlPanel( frame, canvas, model ) );
        frame.pack();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }

}
