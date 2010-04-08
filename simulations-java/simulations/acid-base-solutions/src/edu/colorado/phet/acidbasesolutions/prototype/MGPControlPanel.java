/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.prototype;

import java.awt.GridBagConstraints;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.acidbasesolutions.prototype.MagnifyingGlass.MoleculeRepresentation;
import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;

/**
 * Control panel for the Magnifying Glass prototype.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class MGPControlPanel extends ControlPanel {
    
    private final MGPModel model;
    
    private final WeakAcidControls weakAcidControls;
    private final MoleculeControls moleculeControls;
    private final DotControls dotControls;
    private final ImageControls imageControls;
    private final BeakerControls beakerControls;
    private final MagnifyingGlassControls magnifyingGlassControls;
    private final CanvasControls canvasControls;
    
    public MGPControlPanel( JFrame parentFrame, final MGPCanvas canvas, final MGPModel model ) {
        
        this.model = model;
        model.getMagnifyingGlass().addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateVisibility();
            }
        });
        
        weakAcidControls = new WeakAcidControls( parentFrame, model.getSolution() );
        moleculeControls = new MoleculeControls( model.getMagnifyingGlass() );
        dotControls = new DotControls( parentFrame, canvas.getMagnifyingGlassNode().getDotsNode() );
        imageControls = new ImageControls( canvas.getMagnifyingGlassNode().getImagesNode() );
        beakerControls = new BeakerControls( parentFrame, model.getBeaker() );
        magnifyingGlassControls = new MagnifyingGlassControls( model.getMagnifyingGlass() );
        canvasControls = new CanvasControls( parentFrame, canvas );
        
        JPanel innerPanel = new JPanel();
        addControl( innerPanel );
        
        // layout
        EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
        layout.setFill( GridBagConstraints.HORIZONTAL );
        innerPanel.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( weakAcidControls, row++, column );
        layout.addComponent( moleculeControls, row++, column );
        layout.addComponent( dotControls, row++, column );
        layout.addComponent( imageControls, row++, column );
        layout.addComponent( beakerControls, row++, column );
        layout.addComponent( magnifyingGlassControls, row++, column );
        layout.addComponent( canvasControls, row++, column );
        
        addControlFullWidth( innerPanel );
        
        // default state
        updateVisibility();
    }
    
    private void updateVisibility() {
        dotControls.setVisible( model.getMagnifyingGlass().getMoleculeRepresentation() == MoleculeRepresentation.DOTS );
        imageControls.setVisible( model.getMagnifyingGlass().getMoleculeRepresentation() == MoleculeRepresentation.IMAGES );
    }
}
