// Copyright 2002-2011, University of Colorado

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
    private final ColorControls colorControls;
    private final BeakerControls beakerControls;
    private final MagnifyingGlassControls magnifyingGlassControls;
    
    public MGPControlPanel( JFrame parentFrame, final MGPCanvas canvas, final MGPModel model, boolean dev ) {
        
        this.model = model;
        model.getMagnifyingGlass().addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateVisibility();
            }
        });
        
        weakAcidControls = new WeakAcidControls( parentFrame, model.getSolution() );
        moleculeControls = new MoleculeControls( model.getMagnifyingGlass(), canvas.getMagnifyingGlassNode() );
        dotControls = new DotControls( parentFrame, canvas.getMagnifyingGlassNode().getDotsNode() );
        imageControls = new ImageControls( canvas.getMagnifyingGlassNode().getImagesNode() );
        colorControls = new ColorControls( parentFrame, model.getSolution(), canvas.getMagnifyingGlassNode().getDotsNode(), canvas, dev );
        beakerControls = new BeakerControls( parentFrame, model.getBeaker() );
        magnifyingGlassControls = new MagnifyingGlassControls( model.getMagnifyingGlass() );
        
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
        if ( dev ) {
            layout.addComponent( dotControls, row++, column );
            layout.addComponent( imageControls, row++, column );
            layout.addComponent( colorControls, row++, column );
            layout.addComponent( beakerControls, row++, column );
            layout.addComponent( magnifyingGlassControls, row++, column );
        }
        
        addControlFullWidth( innerPanel );
        
        // default state
        updateVisibility();
    }
    
    private void updateVisibility() {
        dotControls.setVisible( model.getMagnifyingGlass().getMoleculeRepresentation() == MoleculeRepresentation.DOTS );
        imageControls.setVisible( model.getMagnifyingGlass().getMoleculeRepresentation() == MoleculeRepresentation.IMAGES );
    }
}
