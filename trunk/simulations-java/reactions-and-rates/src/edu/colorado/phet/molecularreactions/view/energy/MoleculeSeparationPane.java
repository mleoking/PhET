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
import edu.colorado.phet.molecularreactions.model.EnergyProfile;
import edu.colorado.phet.molecularreactions.model.reactions.A_BC_AB_C_Reaction;
import edu.colorado.phet.molecularreactions.MRConfig;
import edu.colorado.phet.molecularreactions.modules.MRModule;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.ClockAdapter;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;

public class MoleculeSeparationPane extends PPath {
    private final MoleculeSelectionTracker tracker;

    private EnergyMoleculeGraphic selectedMoleculeGraphic;
    private EnergyMoleculeGraphic nearestToSelectedMoleculeGraphic;

    private Insets paneInsets = new Insets( 20, 30, 40, 10 );

    private PNode moleculePaneAxisNode;
    private SeparationIndicatorArrow separationIndicatorArrow;
    private CurvePane curvePane;

    private PNode moleculeLayer;
    private MoleculeSeparationPane.MoleculeGraphicController moleculeGraphicController;

    public MoleculeSeparationPane( final MRModule module, Dimension upperPaneSize, CurvePane curvePane ) {
        super( new Rectangle2D.Double( 0, 0, upperPaneSize.getWidth(), upperPaneSize.getHeight() ) );

        this.curvePane = curvePane;

        this.setPaint( MRConfig.MOLECULE_PANE_BACKGROUND );

        this.moleculeLayer = new PNode();
        this.moleculeLayer.setOffset( this.paneInsets.left, 0 );

        addChild( this.moleculeLayer );

        // Axis: An arrow that shows separation of molecules and text label
        // They are grouped in a single node so that they can be made visible or
        // invisible as necessary
        this.moleculePaneAxisNode = new PNode();
        this.separationIndicatorArrow = new SeparationIndicatorArrow( Color.black );
        this.moleculePaneAxisNode.addChild( this.separationIndicatorArrow );
        PText siaLabel = new PText( SimStrings.get( "EnergyView.separation" ) );
        siaLabel.setFont( MRConfig.LABEL_FONT );
        siaLabel.rotate( -Math.PI / 2 );
        siaLabel.setOffset( this.paneInsets.left / 2 - siaLabel.getFullBounds().getWidth() + 2,
                            this.getFullBounds().getHeight() / 2 + siaLabel.getFullBounds().getHeight() / 2 );
        this.moleculePaneAxisNode.addChild( siaLabel );
        this.moleculePaneAxisNode.setVisible( false );

        this.addChild( this.moleculePaneAxisNode );

        tracker = new MoleculeSelectionTracker( module );

        moleculeGraphicController = new MoleculeGraphicController( module );

        tracker.addSelectionStateListener( moleculeGraphicController );

        module.getClock().addClockListener(
            new ClockAdapter() {
                public void clockTicked( ClockEvent clockEvent ) {
                    update();
                }
            }
        );
    }

    public MoleculeSelectionTracker getTracker() {
        return tracker;
    }

    public void reset() {
        tracker.reset();
    }
    
    private void removeNearestToSelectedMoleculeGraphic() {
        if( nearestToSelectedMoleculeGraphic != null ) {
            moleculeLayer.removeChild( nearestToSelectedMoleculeGraphic );

            nearestToSelectedMoleculeGraphic = null;
        }
    }

    private void removeSelectedMoleculeGraphic() {
        if( selectedMoleculeGraphic != null ) {
            moleculeLayer.removeChild( selectedMoleculeGraphic );

            selectedMoleculeGraphic = null;
        }
    }

    private void setNearestToSelectedGraphic( EnergyProfile newProfile ) {
        if (tracker.getNearestToSelectedMolecule() != null) {
            if (nearestToSelectedMoleculeGraphic == null) {
                nearestToSelectedMoleculeGraphic = new EnergyMoleculeGraphic( tracker.getNearestToSelectedMolecule().getFullMolecule(), newProfile );
                moleculeLayer.addChild( nearestToSelectedMoleculeGraphic );
            }
        }
        else {
            removeNearestToSelectedMoleculeGraphic();
        }
    }

    private void setSelectedGraphic( EnergyProfile newProfile ) {
        if (tracker.getSelectedMolecule() != null) {
            if (selectedMoleculeGraphic == null) {
                selectedMoleculeGraphic = new EnergyMoleculeGraphic( tracker.getSelectedMolecule().getFullMolecule(), newProfile );
                moleculeLayer.addChild( selectedMoleculeGraphic );
            }
        }
        else {
            removeSelectedMoleculeGraphic();
        }
    }

    /*
     * Updates the positions of the molecule graphics in the upper pane.
     */
    public void update() {
        moleculeGraphicController.update();

        updateEnergyCursorVisibility();

        updateMoleculePaneAxis();
    }

    private void updateEnergyCursorVisibility() {
        // TODO: This doesn't belong here
        curvePane.setEnergyCursorVisible( getTracker().getSelectedMolecule() != null );
    }

    private void updateMoleculePaneAxis() {
        moleculePaneAxisNode.setVisible( tracker.getSelectedMolecule() != null );
    }

    private class MoleculeGraphicController implements MoleculeSelectionTracker.MoleculeSelectionListener, SimpleObserver {
        private final MRModule module;
        private int direction;
        private double yMin, yMax;
        private Point2D.Double midPoint = new Point2D.Double(0, 0);

        public MoleculeGraphicController( MRModule module ) {
            this.module = module;
        }

        public void notifySelectedChanged( SimpleMolecule oldSelection, SimpleMolecule newSelection ) {
            removeSelectedMoleculeGraphic();
        }

        public void notifyClosestChanged( SimpleMolecule oldNearest, SimpleMolecule newNearest ) {
            removeNearestToSelectedMoleculeGraphic();
        }

        public void notifyEnergyProfileChanged( EnergyProfile newProfile ) {
            setSelectedGraphic( newProfile );
            setNearestToSelectedGraphic( newProfile );
        }

        public void update() {
            updateDirection();
            updatePositions();
            updateMoleculeGraphics();
            updateGraphicPositions();
            updateSeparationArrow();
            updateEnergyCursor();
        }

        private void updateGraphicPositions( ) {
            SimpleMolecule newFreeMolecule = tracker.getFreeMolecule();

            if (newFreeMolecule != null) {
                if (selectedMoleculeGraphic != null && nearestToSelectedMoleculeGraphic != null) {
                    // Set locatation of molecules. Use the *direction* variable we set above
                    // to determine which graphic should be on top
                    if( newFreeMolecule instanceof MoleculeC && newFreeMolecule == tracker.getSelectedMolecule() ) {
                        selectedMoleculeGraphic.setOffset( midPoint.getX(), yMax );
                        nearestToSelectedMoleculeGraphic.setOffset( midPoint.getX(), yMin );
                    }
                    else if( newFreeMolecule instanceof MoleculeC && newFreeMolecule == tracker.getNearestToSelectedMolecule() ) {
                        selectedMoleculeGraphic.setOffset( midPoint.getX(), yMax );
                        nearestToSelectedMoleculeGraphic.setOffset( midPoint.getX(), yMin );
                    }
                    else if( newFreeMolecule instanceof MoleculeA && newFreeMolecule == tracker.getSelectedMolecule() ) {
                        selectedMoleculeGraphic.setOffset( midPoint.getX(), yMin );
                        nearestToSelectedMoleculeGraphic.setOffset( midPoint.getX(), yMax );
                    }
                    else if( newFreeMolecule instanceof MoleculeA && newFreeMolecule == tracker.getNearestToSelectedMolecule() ) {
                        selectedMoleculeGraphic.setOffset( midPoint.getX(), yMin );
                        nearestToSelectedMoleculeGraphic.setOffset( midPoint.getX(), yMax );
                    }
                }
            }
        }

        private void updateSelectedGraphic() {
            setSelectedGraphic( module.getMRModel().getEnergyProfile() );
        }

        private void updateNearestToSelectedGraphic() {
            setNearestToSelectedGraphic( module.getMRModel().getEnergyProfile() );
        }

        private void updateMoleculeGraphics() {
            updateSelectedGraphic();
            updateNearestToSelectedGraphic();
        }

        private void updateEnergyCursor() {
            // TODO: This doesn't belong here (depends on midPoint)
            // set location of cursor
            curvePane.setEnergyCursorOffset( midPoint.getX() );
        }

        private void updateSeparationArrow() {
            // Set the size of the separation indicator arrow
            separationIndicatorArrow.setEndpoints( paneInsets.left / 2 + 10, yMin,
                                                   paneInsets.left / 2 + 10, yMax );
        }

        private void updateDirection() {
            if( tracker.isTracking() ) {
                SimpleMolecule freeMolecule  = tracker.getFreeMolecule(),
                               boundMolecule = tracker.getBoundMolecule();

                assert freeMolecule  != null;
                assert boundMolecule != null;

                // Figure out on which side of the centerline the molecules should appear
                // If the selected molecule is an A molecule and it's free, we're on the left
                if( tracker.getSelectedMolecule() instanceof MoleculeA && tracker.getSelectedMolecule() == freeMolecule ) {
                    direction = -1;
                }
                // If the selected molecule is an A molecule and it's bound, we're on the right
                else if( tracker.getSelectedMolecule() instanceof MoleculeA && tracker.getSelectedMolecule() == boundMolecule ) {
                    direction = 1;
                }
                // If the selected molecule is a C molecule and it's free, we're on the right
                else if( tracker.getSelectedMolecule() instanceof MoleculeC && tracker.getSelectedMolecule() == freeMolecule ) {
                    direction = 1;
                }
                // If the selected molecule is a C molecule and it's bound, we're on the left
                else if( tracker.getSelectedMolecule() instanceof MoleculeC && tracker.getSelectedMolecule() == boundMolecule ) {
                    direction = -1;
                }
                else {
                    throw new RuntimeException( "internal error" );
                }
            }
        }

        private void updatePositions() {
            if( tracker.isTracking() ) {
                SimpleMolecule freeMolecule  = tracker.getFreeMolecule(),
                               boundMolecule = tracker.getBoundMolecule();

                // Position the molecule graphics
                double cmDist = tracker.getSelectedMolecule().getPosition().distance( tracker.getNearestToSelectedMolecule().getPosition() );
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
                double separationAtFootOfHill = Math.min( tracker.getSelectedMolecule().getRadius(), tracker.getNearestToSelectedMolecule().getRadius() );

                // Scale the actual inter-molecular distance to the scale of the energy profile
                double r = ( reaction.getEnergyProfile().getThresholdWidth() / 2 ) / separationAtFootOfHill;
                double separationAtReaction = A_BC_AB_C_Reaction.getReactionOffset( freeMolecule, boundMolecule );
                double currentSeparation = freeMolecule.getPosition().distance( boundMolecule.getPosition() );
                double currentOverlap = separationAtFootOfHill - currentSeparation;
                double reactionOverlap = separationAtFootOfHill - separationAtReaction;
                double dr = currentOverlap / reactionOverlap * r;

                double dx = Math.max( ( edgeDist + separationAtFootOfHill ) * r, dr );
                double xOffsetFromCenter = Math.min( curvePane.getCurveAreaSize().getWidth() / 2 - xOffset, dx );
                double x = curvePane.getCurveAreaSize().getWidth() / 2 + ( xOffsetFromCenter * direction );
                double y = yOffset + maxSeparation / 2;

                // Do not allow the energy cursor to move beyond where it's
                // energetically allowed.
                //
                // Note: This is a hack implemented because the physics of the
                //       simulation are fudged.
                double maxX = curvePane.getIntersectionWithHorizontal( x );

                x = Math.min( x, maxX );

                midPoint = new Point2D.Double( x, y );

                yMin = midPoint.getY() - Math.min( cmDist, maxSeparation ) / 2;
                yMax = midPoint.getY() + Math.min( cmDist, maxSeparation ) / 2;
            }
            else {
            }
        }
    }
}
