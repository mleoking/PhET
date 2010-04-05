/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.prototype;

import java.awt.BorderLayout;
import java.awt.Dimension;
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
    
    private final BeakerControls beakerControls;
    private final MagnifyingGlassControls magnifyingGlassControls;
    private final WeakAcidControls weakAcidControls;
    private final MoleculeCountPanel moleculeCountPanel;
    private final CanvasControls canvasControls;
    
    public ControlPanel( JFrame parentFrame, final PCanvas canvas, ProtoModel model ) {
        
        beakerControls = new BeakerControls( parentFrame, model.getBeaker() );
        magnifyingGlassControls = new MagnifyingGlassControls( model.getMagnifyingGlass() );
        weakAcidControls = new WeakAcidControls( parentFrame, model.getSolution() );
        moleculeCountPanel = new MoleculeCountPanel();
        canvasControls = new CanvasControls( parentFrame, canvas );
        
        JPanel innerPanel = new JPanel();
        add( innerPanel );
        
        EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
        layout.setFill( GridBagConstraints.HORIZONTAL );
        innerPanel.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( beakerControls, row++, column );
        layout.addComponent( magnifyingGlassControls, row++, column );
        layout.addComponent( weakAcidControls, row++, column );
        layout.addComponent( moleculeCountPanel, row++, column );
        layout.addComponent( canvasControls, row++, column );
    }
    
    public static void main( String[] args ) {
        JFrame frame = new JFrame();
        // model
        ProtoModel model = new ProtoModel();
        // view
        PhetPCanvas canvas = new ProtoCanvas( model );
        // control
        ControlPanel controlPanel = new ControlPanel( frame, canvas, model );
        // layout
        JPanel mainPanel = new JPanel( new BorderLayout() );
        mainPanel.add( canvas, BorderLayout.CENTER );
        mainPanel.add( controlPanel, BorderLayout.EAST );
        // frame
        frame.setContentPane( mainPanel );
        frame.setSize( new Dimension( 1024, 768 ) );
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }

}
