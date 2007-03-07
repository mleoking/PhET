/* Copyright 2007, University of Colorado */
package edu.colorado.phet.molecularreactions.view.energy;

import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.PNode;
import edu.colorado.phet.molecularreactions.view.SeparationIndicatorArrow;
import edu.colorado.phet.molecularreactions.view.EnergyMoleculeGraphic;
import edu.colorado.phet.molecularreactions.model.SimpleMolecule;
import edu.colorado.phet.molecularreactions.model.MoleculeA;
import edu.colorado.phet.molecularreactions.model.MoleculeC;
import edu.colorado.phet.molecularreactions.model.reactions.A_BC_AB_C_Reaction;
import edu.colorado.phet.molecularreactions.MRConfig;
import edu.colorado.phet.molecularreactions.modules.MRModule;
import edu.colorado.phet.common.view.util.SimStrings;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;

public class MoleculeSeparationPane extends PPath {
    private final MolecularPaneState molecularPaneState;
    private final MRModule module;

    public MoleculeSeparationPane(MRModule module, Dimension upperPaneSize, MolecularPaneState state ) {
        super( new Rectangle2D.Double( 0, 0,
               upperPaneSize.getWidth(),
               upperPaneSize.getHeight() ) );

        this.module = module;
        this.molecularPaneState  = state;

        this.setPaint( MRConfig.MOLECULE_PANE_BACKGROUND );
        state.moleculeLayer = new PNode();
        state.moleculeLayer.setOffset( state.paneInsets.left, 0 );
        this.addChild( state.moleculeLayer );

        // Axis: An arrow that shows separation of molecules and text label
        // They are grouped in a single node so that they can be made visible or
        // invisible as necessary
        state.moleculePaneAxisNode = new PNode();
        state.separationIndicatorArrow = new SeparationIndicatorArrow( Color.black );
        state.moleculePaneAxisNode.addChild( state.separationIndicatorArrow );
        PText siaLabel = new PText( SimStrings.get( "EnergyView.separation" ) );
        siaLabel.setFont( MRConfig.LABEL_FONT );
        siaLabel.rotate( -Math.PI / 2 );
        siaLabel.setOffset( state.paneInsets.left / 2 - siaLabel.getFullBounds().getWidth() + 2,
                            this.getFullBounds().getHeight() / 2 + siaLabel.getFullBounds().getHeight() / 2 );
        state.moleculePaneAxisNode.addChild( siaLabel );
        state.moleculePaneAxisNode.setVisible( false );

        this.addChild( state.moleculePaneAxisNode );
    }

    /*
     * Updates the positions of the molecule graphics in the upper pane. This was originaly supposed
     * to be on all the modules. That's why it's embedded in this class. Since it is only in the
     * SimpleMRModule now, it should be refactored into its own class
     */
    public void update( EnergyView.State state ) {
        if( molecularPaneState.selectedMolecule != null && molecularPaneState.selectedMoleculeGraphic != null && molecularPaneState.nearestToSelectedMoleculeGraphic != null ) {

            // Which side of the profile the molecules show up on depends on their type

            // Identify which molecule is the free one, and which one is bound in a composite
            // There is an intermediate state in the model where the reaction has occured but
            // the agent that tracks the selected molecule and the nearest one to it has not been
            // updated yet. We have to handle that by returning without updating ourselves/
            SimpleMolecule boundMolecule = null;
            SimpleMolecule freeMolecule = null;
            if( molecularPaneState.selectedMolecule.isPartOfComposite() ) {
                boundMolecule = molecularPaneState.selectedMolecule;
                if( !molecularPaneState.nearestToSelectedMolecule.isPartOfComposite() ) {
                    freeMolecule = molecularPaneState.nearestToSelectedMolecule;
                }
                else {
                    return;
                }
            }
            else if( molecularPaneState.nearestToSelectedMolecule.isPartOfComposite() ) {
                boundMolecule = molecularPaneState.nearestToSelectedMolecule;
                if( !molecularPaneState.selectedMolecule.isPartOfComposite() ) {
                    freeMolecule = molecularPaneState.selectedMolecule;
                }
                else {
                    return;
                }
            }
            else {
                // If neither molecule is part of a composite, then the selected molecule might
                // have just gone through a reaction and is now on its own, but the rest of the
                // model hasn't gone through the time step so the SelectedMoleculeTracker hasn't
                // had a chance to change the nearestToSelectedMolecule. The easiest thing to do
                // in this case is take a pass for now...
                return;
            }

            // Figure out on which side of the centerline the molecules should appear
            int direction = 0;
            // If the selected molecule is an A molecule and it's free, we're on the left
            if( molecularPaneState.selectedMolecule instanceof MoleculeA && molecularPaneState.selectedMolecule == freeMolecule ) {
                direction = -1;
            }
            // If the selected molecule is an A molecule and it's bound, we're on the right
            else if( molecularPaneState.selectedMolecule instanceof MoleculeA && molecularPaneState.selectedMolecule == boundMolecule ) {
                direction = 1;
            }
            // If the selected molecule is a C molecule and it's free, we're on the right
            else if( molecularPaneState.selectedMolecule instanceof MoleculeC && molecularPaneState.selectedMolecule == freeMolecule ) {
                direction = 1;
            }
            // If the selected molecule is a C molecule and it's bound, we're on the left
            else if( molecularPaneState.selectedMolecule instanceof MoleculeC && molecularPaneState.selectedMolecule == boundMolecule ) {
                direction = -1;
            }
            else {
                throw new RuntimeException( "internal error" );
            }

            // Position the molecule graphics
            double cmDist = molecularPaneState.selectedMolecule.getPosition().distance( molecularPaneState.nearestToSelectedMolecule.getPosition() );
            A_BC_AB_C_Reaction reaction = (A_BC_AB_C_Reaction)module.getMRModel().getReaction();
            double edgeDist = reaction.getDistanceToCollision( freeMolecule, boundMolecule.getParentComposite() );

            // In the middle of the reaction, the collision distance is underfined
            if( Double.isNaN( edgeDist ) ) {
                edgeDist = 0;
            }
            double maxSeparation = 80;
            double yOffset = 35;
            double xOffset = 20;

            // The distance between the molecule's CMs when they first come into contact
            double separationAtFootOfHill = Math.min( molecularPaneState.selectedMolecule.getRadius(), molecularPaneState.nearestToSelectedMolecule.getRadius() );

            // Scale the actual inter-molecular distance to the scale of the energy profile
            double r = ( reaction.getEnergyProfile().getThresholdWidth() / 2 ) / separationAtFootOfHill;
            double separationAtReaction = A_BC_AB_C_Reaction.getReactionOffset( freeMolecule, boundMolecule );
            double currentSeparation = freeMolecule.getPosition().distance( boundMolecule.getPosition() );
            double currentOverlap = separationAtFootOfHill - currentSeparation;
            double reactionOverlap = separationAtFootOfHill - separationAtReaction;
            double dr = currentOverlap / reactionOverlap * r;

            double dx = Math.max( ( edgeDist + separationAtFootOfHill ) * r, dr );
            double xOffsetFromCenter = Math.min( state.curvePane.getCurveAreaSize().getWidth() / 2 - xOffset, dx );
            double x = state.curvePane.getCurveAreaSize().getWidth() / 2 + ( xOffsetFromCenter * direction );
            double y = yOffset + maxSeparation / 2;

            // Do not allow the energy cursor to move beyond where it's
            // energetically allowed.
            // Note: This is a hack implemented because the physics of the
            //       simulation are fudged.
            double maxX = state.curvePane.getIntersectionWithHorizontal( x );

            x = Math.min(x, maxX);

            Point2D midPoint = new Point2D.Double( x,  y );

            double yMin = midPoint.getY() - Math.min( cmDist, maxSeparation ) / 2;
            double yMax = midPoint.getY() + Math.min( cmDist, maxSeparation ) / 2;

            // Set locatation of molecules. Use the *direction* variable we set above
            // to determine which graphic should be on top
            if( freeMolecule instanceof MoleculeC && freeMolecule == molecularPaneState.selectedMolecule ) {
                molecularPaneState.selectedMoleculeGraphic.setOffset( midPoint.getX(), yMax );
                molecularPaneState.nearestToSelectedMoleculeGraphic.setOffset( midPoint.getX(), yMin );
            }
            else if( freeMolecule instanceof MoleculeC && freeMolecule == molecularPaneState.nearestToSelectedMolecule ) {
                molecularPaneState.selectedMoleculeGraphic.setOffset( midPoint.getX(), yMax );
                molecularPaneState.nearestToSelectedMoleculeGraphic.setOffset( midPoint.getX(), yMin );
            }
            else if( freeMolecule instanceof MoleculeA && freeMolecule == molecularPaneState.selectedMolecule ) {
                molecularPaneState.selectedMoleculeGraphic.setOffset( midPoint.getX(), yMin );
                molecularPaneState.nearestToSelectedMoleculeGraphic.setOffset( midPoint.getX(), yMax );
            }
            else if( freeMolecule instanceof MoleculeA && freeMolecule == molecularPaneState.nearestToSelectedMolecule ) {
                molecularPaneState.selectedMoleculeGraphic.setOffset( midPoint.getX(), yMin );
                molecularPaneState.nearestToSelectedMoleculeGraphic.setOffset( midPoint.getX(), yMax );
            }

            // Set the size of the separation indicator arrow
            molecularPaneState.separationIndicatorArrow.setEndpoints( molecularPaneState.paneInsets.left / 2 + 10, yMin,
                                                   molecularPaneState.paneInsets.left / 2 + 10, yMax );

            // set location of cursor
            state.curvePane.setEnergyCursorOffset( midPoint.getX() );
        }
        else if( molecularPaneState.selectedMoleculeGraphic != null ) {
            molecularPaneState.selectedMoleculeGraphic.setOffset( 20, 20 );
        }
        else if( molecularPaneState.nearestToSelectedMoleculeGraphic != null ) {
            molecularPaneState.nearestToSelectedMoleculeGraphic.setOffset( 20, 50 );
        }
    }

    static class MolecularPaneState {
        Insets paneInsets = new Insets( 20, 30, 40, 10 );

        PNode moleculeLayer;
        PNode moleculePaneAxisNode;
        SeparationIndicatorArrow separationIndicatorArrow;

        SimpleMolecule selectedMolecule;
        SimpleMolecule nearestToSelectedMolecule;
        EnergyMoleculeGraphic selectedMoleculeGraphic;
        EnergyMoleculeGraphic nearestToSelectedMoleculeGraphic;
    }
}
