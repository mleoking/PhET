// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.reactionsandrates.view.energy;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.reactionsandrates.MRConfig;
import edu.colorado.phet.reactionsandrates.model.*;
import edu.colorado.phet.reactionsandrates.model.reactions.A_BC_AB_C_Reaction;
import edu.colorado.phet.reactionsandrates.modules.MRModule;
import edu.colorado.phet.reactionsandrates.view.EnergyMoleculeGraphic;
import edu.colorado.phet.reactionsandrates.view.SeparationIndicatorArrow;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class MoleculeSeparationPane extends PPath {
    private final MoleculeSelectionTracker tracker;

    private final PNode selectedMoleculeGraphic = new PNode();
    private final PNode nearestToSelectedMoleculeGraphic = new PNode();

    private Insets paneInsets = new Insets( 20, 30, 40, 10 );

    private PNode moleculePaneAxisNode;
    private SeparationIndicatorArrow separationIndicatorArrow;
    private CurvePane curvePane;

    private PNode moleculeLayer;
    private MoleculeGraphicController moleculeGraphicController;
    private MRModule module;
    private MoleculeSeparationPane.UpdatingClockListener updatingClockListener;

    public MoleculeSeparationPane( final MRModule module, Dimension upperPaneSize, CurvePane curvePane ) {
        super( new Rectangle2D.Double( 0, 0, upperPaneSize.getWidth(), upperPaneSize.getHeight() ) );

        this.module = module;

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
        PText siaLabel = new PText( MRConfig.RESOURCES.getLocalizedString( "EnergyView.separation" ) );
        siaLabel.setFont( MRConfig.LABEL_FONT );
        siaLabel.rotate( -Math.PI / 2 );
        siaLabel.setOffset( this.paneInsets.left / 2 - siaLabel.getFullBounds().getWidth() + 2,
                            this.getFullBounds().getHeight() / 2 + siaLabel.getFullBounds().getHeight() / 2 );
        this.moleculePaneAxisNode.addChild( siaLabel );
        this.moleculePaneAxisNode.setVisible( false );

        this.addChild( this.moleculePaneAxisNode );

        tracker = new MoleculeSelectionTracker( module );

        moleculeGraphicController = new MoleculeGraphicController( module );

        updatingClockListener = new UpdatingClockListener();

        moleculeLayer.addChild( selectedMoleculeGraphic );
        moleculeLayer.addChild( nearestToSelectedMoleculeGraphic );

        module.getClock().addClockListener( updatingClockListener );
    }

    public MoleculeSelectionTracker getTracker() {
        return tracker;
    }

    public void reset() {
        tracker.reset();
    }

    public void terminate() {
        tracker.terminate();

        module.getClock().removeClockListener( updatingClockListener );
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

    private PNode getGraphic( ModelElement element ) {
        return module.getGraphicsForModelElement( element );
    }

    private class MoleculeGraphicController implements SimpleObserver {
        private final MRModule module;
        private int direction;
        private double yMin, yMax;
        private Point2D.Double midPoint = new Point2D.Double( 0, 0 );

        public MoleculeGraphicController( MRModule module ) {
            this.module = module;
        }

        public void update() {
            updateDirection();
            updatePositions();
            updateMoleculeGraphics();
            updateSeparationArrow();
            updateEnergyCursor();
        }

        private boolean shouldDrawMoleculeOnTop( AbstractMolecule molecule ) {
            if( molecule == null ) {
                return false;
            }
            if( molecule.getClass() == MoleculeA.class ) {
                return true;
            }

            if( molecule.isComposite() ) {
                if( molecule.getComponentMolecules()[0].getClass() == MoleculeA.class ) {
                    return true;
                }

                if( molecule.getComponentMolecules()[1].getClass() == MoleculeA.class ) {
                    return true;
                }
            }

            return false;
        }

        private void addMoleculeGraphic( PNode node, AbstractMolecule element, boolean top ) {
            node.removeAllChildren();

            if( element != null ) {
                EnergyMoleculeGraphic graphic = new EnergyMoleculeGraphic( element.getFullMolecule(), module.getMRModel().getEnergyProfile() );
                graphic.translate( midPoint.getX(), top ? yMin : yMax );
                node.addChild( graphic );
            }
        }

        private void updateMoleculeGraphics() {
            boolean selectedTop = shouldDrawMoleculeOnTop( tracker.getSelectedMolecule() );
            addMoleculeGraphic( selectedMoleculeGraphic, tracker.getSelectedMolecule(), selectedTop );
            addMoleculeGraphic( nearestToSelectedMoleculeGraphic, tracker.getNearestToSelectedMolecule(), !selectedTop );
        }

        private void updateEnergyCursor() {
            // TODO: This doesn't belong here (depends on midPoint)
            // Set location of cursor
            curvePane.setEnergyCursorOffset( midPoint.getX() );
        }

        private void updateSeparationArrow() {
            // Set the size of the separation indicator arrow
            separationIndicatorArrow.setEndpoints( paneInsets.left / 2 + 10, yMin,
                                                   paneInsets.left / 2 + 10, yMax );
        }

        private void updateDirection() {
            if( tracker.isTracking() ) {
                SimpleMolecule freeMolecule = tracker.getFreeMolecule(),
                        boundMolecule = tracker.getBoundMolecule();

                assert freeMolecule != null;
                assert boundMolecule != null;

                // Figure out on which side of the centerline the molecules should appear
                // If the selected molecule is an A molecule and it's free, we're on the left
                if( tracker.getSelectedMolecule() instanceof MoleculeA && tracker.getSelectedMolecule() == freeMolecule ) {
                    direction = -1;
                }
                // If the selected molecule is an A molecule and it's bound, we're on the right
                else
                if( tracker.getSelectedMolecule() instanceof MoleculeA && tracker.getSelectedMolecule() == boundMolecule ) {
                    direction = 1;
                }
                // If the selected molecule is a C molecule and it's free, we're on the right
                else
                if( tracker.getSelectedMolecule() instanceof MoleculeC && tracker.getSelectedMolecule() == freeMolecule ) {
                    direction = 1;
                }
                // If the selected molecule is a C molecule and it's bound, we're on the left
                else
                if( tracker.getSelectedMolecule() instanceof MoleculeC && tracker.getSelectedMolecule() == boundMolecule ) {
                    direction = -1;
                }
                else {
                    throw new RuntimeException( "internal error" );
                }
            }
        }

        private SimpleMolecule getBMolecule( SimpleMolecule[] componentMolecules ) {
            for( int i = 0; i < componentMolecules.length; i++ ) {
                if( componentMolecules[i].getClass() == MoleculeB.class ) {
                    return componentMolecules[i];
                }
            }

            return null;
        }

        private SimpleMolecule getFreeOrBMolecule( SimpleMolecule molecule ) {
            if( molecule.isPartOfComposite() ) {
                molecule = getBMolecule( molecule.getFullMolecule().getComponentMolecules() );
            }
            else if( molecule.isComposite() ) {
                molecule = getBMolecule( molecule.getComponentMolecules() );
            }

            return molecule;
        }

        public SimpleMolecule getNearestComponentToSelectedMolecule() {
            return getFreeOrBMolecule( tracker.getNearestToSelectedMolecule() );
        }

        public SimpleMolecule getSelectedComponentMolecule() {
            return getFreeOrBMolecule( tracker.getSelectedMolecule() );
        }


        private double getDistanceBetweenTrackedMolecules() {
            return getSelectedComponentMolecule().getPosition().distance( getNearestComponentToSelectedMolecule().getPosition() );
        }

        private void updatePositions() {
            if( tracker.isTracking() ) {
                SimpleMolecule freeMolecule = tracker.getFreeMolecule(),
                        boundMolecule = tracker.getBoundMolecule();

                // Position the molecule graphics
                double cmDist = getDistanceBetweenTrackedMolecules();

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
                if( direction < 0 ) {
                    double maxX = curvePane.getIntersectionWithHorizontal( x );

                    x = Math.min( x, maxX );
                }

                midPoint = new Point2D.Double( x, y );

                yMin = midPoint.getY() - Math.min( cmDist, maxSeparation ) / 2;
                yMax = midPoint.getY() + Math.min( cmDist, maxSeparation ) / 2;
            }
        }
    }

    private class UpdatingClockListener extends ClockAdapter {
        public void clockTicked( ClockEvent clockEvent ) {
            update();
        }
    }
}
