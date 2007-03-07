/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.view.energy;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.molecularreactions.MRConfig;
import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.molecularreactions.modules.MRModule;
import edu.colorado.phet.molecularreactions.util.Resetable;
import edu.colorado.phet.molecularreactions.view.*;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * EnergyView
 * <p/>
 * A view of the MRModel that shows the potential energy of two individual molecules,
 * or their composite molecule. This is a fairly abstract view.
 * <p/>
 * The diagram below shows the basic layout of this view, with the names of fields
 * corresponding to its main elements
 * <p/>
 * -------------------------------------------
 * |                                         |
 * |                                         |
 * |           upperPane                     |
 * |                                         |
 * |                                         |
 * -------------------------------------------
 * |          curvePane                      |
 * |  .....................................  |
 * |  .                                   .  |
 * |  .                                   .  |
 * |  .                                   .  |
 * |  .                                   .  |
 * |  .       curveArea                   .  |
 * |  .                                   .  |
 * |  .                                   .  |
 * |  .                                   .  |
 * |  .                                   .  |
 * |  .                                   .  |
 * |  .                                   .  |
 * |  .                                   .  |
 * |  .....................................  |
 * |  .       legendPane                  .  |
 * |  .....................................  |
 * -------------------------------------------
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class EnergyView extends PNode implements SimpleObserver, Resetable {
    public static class State {
        CurvePane curvePane;
        UpperEnergyPane upperPane;
    }

    private volatile State state;
    private volatile MoleculeSeparationPane.MolecularPaneState molecularPaneState;
    private volatile MRModule module;
    private volatile MoleculeSeparationPane moleculeSeparationPane;

    public EnergyView() {
    }

    public boolean isInitialized() {
        return state != null;
    }

    public void initialize( MRModule module, Dimension upperPaneSize ) {
        state = new State();
        molecularPaneState = new MoleculeSeparationPane.MolecularPaneState();

        
        this.module = module;

        MRModel model = module.getMRModel();

        // The pane that has the molecules
        moleculeSeparationPane = new MoleculeSeparationPane(module, upperPaneSize, molecularPaneState );
        addChild( moleculeSeparationPane );

        // Add another pane on top of the molecule pane to display charts.
        // It's a reall hack, but this pane is made visible when another
        state.upperPane = new UpperEnergyPane(upperPaneSize);

        addChild( state.upperPane );

        // The pane that has the curve and cursor
        state.curvePane = new CurvePane(module, upperPaneSize, state);

        addChild( state.curvePane );

        // The graphic that shows the reaction mechanics. It appears below the profile pane.
        PPath legendNode = new PPath( new Rectangle2D.Double( 0, 0,
                                                              MRConfig.ENERGY_VIEW_REACTION_LEGEND_SIZE.width,
                                                              MRConfig.ENERGY_VIEW_REACTION_LEGEND_SIZE.height ) );
        legendNode.setPaint( MRConfig.ENERGY_PANE_BACKGROUND );
        legendNode.setStrokePaint( new Color( 0, 0, 0, 0 ) );
        legendNode.setOffset( 0, upperPaneSize.getHeight() + state.curvePane.getSize().getHeight() );
        ReactionGraphic reactionGraphic = new ReactionGraphic( model.getReaction(),
                                                               MRConfig.ENERGY_PANE_TEXT_COLOR,
                                                               module.getMRModel() );
        legendNode.addChild( reactionGraphic );
        reactionGraphic.setOffset( legendNode.getWidth() / 2, legendNode.getHeight() - 20 );
        addChild( legendNode );



        // Put a border around the energy view
        Rectangle2D bRect = new Rectangle2D.Double( 0, 0,
                                                    state.curvePane.getFullBounds().getWidth(),
                                                    state.curvePane.getFullBounds().getHeight() + legendNode.getFullBounds().getHeight() );
        PPath border = new PPath( bRect );
        border.setOffset( state.curvePane.getOffset() );
        addChild( border );

        // Listen for changes in the selected molecule and the molecule closest to it
        SelectedMoleculeListener selectedMoleculeListener = new SelectedMoleculeListener();
        model.addSelectedMoleculeTrackerListener( selectedMoleculeListener );
        model.addListener( selectedMoleculeListener );

        update();
    }

    public void reset() {

        molecularPaneState.selectedMolecule = null;
        molecularPaneState.nearestToSelectedMolecule = null;

        if( molecularPaneState.selectedMoleculeGraphic != null ) {
            molecularPaneState.moleculeLayer.removeChild( molecularPaneState.selectedMoleculeGraphic );
        }
        if( molecularPaneState.nearestToSelectedMoleculeGraphic != null ) {
            molecularPaneState.moleculeLayer.removeChild( molecularPaneState.nearestToSelectedMoleculeGraphic );
        }
        molecularPaneState.selectedMoleculeGraphic = null;
        molecularPaneState.nearestToSelectedMoleculeGraphic = null;

        // Listen for changes in the selected molecule and the molecule closest to it
        module.getMRModel().addSelectedMoleculeTrackerListener( new SelectedMoleculeListener() );
    }

    public void resetMolecularPaneState() {

    }

    /*
     *
     */
    public Dimension getUpperPaneSize() {
        return state.upperPane.getSize();
    }

    /*
     * Adds a pNode to the upper pane
     *
     * @param pNode
     */
    public void addToUpperPane( PNode pNode ) {
        state.upperPane.removeAllChildren();
        state.upperPane.addChild( pNode );
        state.upperPane.setVisible( true );
    }

    public void update() {
        moleculeSeparationPane.update( state );
    }

    /*
     * Removes a pNode from the upper pane
     *
     * @param pNode
     */
    public void removeFromUpperPane( PNode pNode ) {
        if( state.upperPane.getChildrenReference().contains( pNode ) ) {
            state.upperPane.removeChild( pNode );
            state.upperPane.setVisible( state.upperPane.getChildrenCount() != 0 );
        }
    }

    public void setManualControl( boolean manualControl ) {
        state.curvePane.setManualControlEnabled( manualControl );
    }

    public void setSeparationViewVisible( boolean hide ) {
        state.upperPane.setVisible( hide );
    }

    public void setProfileManipulable( boolean manipulable ) {
        state.curvePane.setProfileManipulable( manipulable );
    }

    public PNode getUpperPaneContents() {
        if (state.upperPane == null) return null;

        if ( state.upperPane.getChildrenCount() == 0) {
            return null;
        }
        
        return state.upperPane.getChild( 0 );
    }

    //--------------------------------------------------------------------------------------------------
    // Inner classes
    //--------------------------------------------------------------------------------------------------

    private class SelectedMoleculeListener extends MRModel.ModelListenerAdapter implements SelectedMoleculeTracker.Listener {

        public void moleculeBeingTrackedChanged( SimpleMolecule newTrackedMolecule,
                                                 SimpleMolecule prevTrackedMolecule ) {
            if( molecularPaneState.selectedMolecule != null ) {
                molecularPaneState.selectedMolecule.removeObserver( EnergyView.this );
            }

            molecularPaneState.selectedMolecule = newTrackedMolecule;
            if( molecularPaneState.selectedMoleculeGraphic != null
                && molecularPaneState.moleculeLayer.getChildrenReference().contains( molecularPaneState.selectedMoleculeGraphic ) ) {
                molecularPaneState.moleculeLayer.removeChild( molecularPaneState.selectedMoleculeGraphic );
            }

            if( newTrackedMolecule != null ) {
                molecularPaneState.selectedMoleculeGraphic = new EnergyMoleculeGraphic( newTrackedMolecule.getFullMolecule(),
                                                                     module.getMRModel().getEnergyProfile() );
                molecularPaneState.moleculeLayer.addChild( molecularPaneState.selectedMoleculeGraphic );
                newTrackedMolecule.addObserver( EnergyView.this );
                molecularPaneState.moleculePaneAxisNode.setVisible( true );
            }
            else {
                molecularPaneState.moleculePaneAxisNode.setVisible( false );

            }
            state.curvePane.setEnergyCursorVisible( molecularPaneState.selectedMolecule != null );
        }

        public void closestMoleculeChanged( SimpleMolecule newClosestMolecule,
                                            SimpleMolecule prevClosestMolecule ) {
            if( molecularPaneState.nearestToSelectedMolecule != null ) {
                molecularPaneState.nearestToSelectedMolecule.removeObserver( EnergyView.this );
            }

            molecularPaneState.nearestToSelectedMolecule = newClosestMolecule;
            if( molecularPaneState.nearestToSelectedMoleculeGraphic != null ) {
                molecularPaneState.moleculeLayer.removeChild( molecularPaneState.nearestToSelectedMoleculeGraphic );
            }
            molecularPaneState.nearestToSelectedMoleculeGraphic = new EnergyMoleculeGraphic( newClosestMolecule.getFullMolecule(),
                                                                          module.getMRModel().getEnergyProfile() );
            molecularPaneState.moleculeLayer.addChild( molecularPaneState.nearestToSelectedMoleculeGraphic );

            newClosestMolecule.addObserver( EnergyView.this );

            update();
        }


        public void notifyEnergyProfileChanged( EnergyProfile profile ) {
            if( molecularPaneState.selectedMoleculeGraphic != null ) {
                molecularPaneState.moleculeLayer.removeChild( molecularPaneState.selectedMoleculeGraphic );
                molecularPaneState.selectedMoleculeGraphic = new EnergyMoleculeGraphic( molecularPaneState.selectedMolecule.getFullMolecule(),
                                                                     profile );
                molecularPaneState.moleculeLayer.addChild( molecularPaneState.selectedMoleculeGraphic );
            }
            if( molecularPaneState.nearestToSelectedMoleculeGraphic != null ) {
                molecularPaneState.moleculeLayer.removeChild( molecularPaneState.nearestToSelectedMoleculeGraphic );
                molecularPaneState.nearestToSelectedMoleculeGraphic = new EnergyMoleculeGraphic( molecularPaneState.nearestToSelectedMolecule.getFullMolecule(),
                                                                              profile );
                molecularPaneState.moleculeLayer.addChild( molecularPaneState.nearestToSelectedMoleculeGraphic );
            }
        }
    }
}
