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
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.molecularreactions.MRConfig;
import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.molecularreactions.model.reactions.A_BC_AB_C_Reaction;
import edu.colorado.phet.molecularreactions.modules.MRModule;
import edu.colorado.phet.molecularreactions.util.Resetable;
import edu.colorado.phet.molecularreactions.view.*;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

import java.awt.*;
import java.awt.geom.Point2D;
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

    private final Color moleculePaneBackgroundColor = MRConfig.MOLECULE_PANE_BACKGROUND;
    private final Insets curveAreaInsets = new Insets( 20, 30, 40, 10 );


    public static class State {
        Dimension upperPaneSize;
        Dimension curvePaneSize;
        Dimension curveAreaSize;

        Font labelFont;
        MRModule module;
        CurvePane curvePane;
        PPath upperPane;
    }

    private static class MolecularPaneState {
        PNode moleculeLayer;
        PNode moleculePaneAxisNode;
        SeparationIndicatorArrow separationIndicatorArrow;

        SimpleMolecule selectedMolecule;
        SimpleMolecule nearestToSelectedMolecule;
        EnergyMoleculeGraphic selectedMoleculeGraphic;
        EnergyMoleculeGraphic nearestToSelectedMoleculeGraphic;
    }

    private volatile State state;
    private volatile MolecularPaneState molecularPaneState;

    public EnergyView() {
    }

    public boolean isInitialized() {
        return state != null;
    }

    public void initialize( MRModule module, Dimension upperPaneSize ) {
        state = new State();
        molecularPaneState = new MolecularPaneState();

        state.upperPaneSize = upperPaneSize;
        state.curvePaneSize = new Dimension( upperPaneSize.width, (int)( MRConfig.ENERGY_VIEW_SIZE.getHeight() )
                                              - upperPaneSize.height
                                              - MRConfig.ENERGY_VIEW_REACTION_LEGEND_SIZE.height );
        state.module = module;
        MRModel model = module.getMRModel();

        // The pane that has the molecules
        PPath moleculePane = createMoleculePane( state.upperPaneSize, curveAreaInsets, moleculePaneBackgroundColor, molecularPaneState );
        addChild( moleculePane );

        // Add another pane on top of the molecule pane to display charts.
        // It's a reall hack, but this pane is made visible when another
        state.upperPane = new PPath( new Rectangle2D.Double( 0, 0,
                                                       upperPaneSize.getWidth(),
                                                       upperPaneSize.getHeight() ) );
        state.upperPane.setWidth( upperPaneSize.getWidth() );
        state.upperPane.setHeight( upperPaneSize.getHeight() );
        state.upperPane.setPaint( moleculePaneBackgroundColor );
        state.upperPane.setStroke( null );
        state.upperPane.setVisible( false );
        addChild( state.upperPane );

        // The graphic that shows the reaction mechanics. It appears below the profile pane.
        PPath legendNode = new PPath( new Rectangle2D.Double( 0, 0,
                                                              MRConfig.ENERGY_VIEW_REACTION_LEGEND_SIZE.width,
                                                              MRConfig.ENERGY_VIEW_REACTION_LEGEND_SIZE.height ) );
        legendNode.setPaint( MRConfig.ENERGY_PANE_BACKGROUND );
        legendNode.setStrokePaint( new Color( 0, 0, 0, 0 ) );
        legendNode.setOffset( 0, upperPaneSize.getHeight() + state.curvePaneSize.getHeight() );
        ReactionGraphic reactionGraphic = new ReactionGraphic( model.getReaction(),
                                                               MRConfig.ENERGY_PANE_TEXT_COLOR,
                                                               module.getMRModel() );
        legendNode.addChild( reactionGraphic );
        reactionGraphic.setOffset( legendNode.getWidth() / 2, legendNode.getHeight() - 20 );
        addChild( legendNode );

        // The pane that has the curve and cursor
        state.curvePane = new CurvePane(module.getMRModel(), upperPaneSize, state);

        addChild( state.curvePane );

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
        state.module.getMRModel().addSelectedMoleculeTrackerListener( new SelectedMoleculeListener() );
    }

    public void resetMolecularPaneState() {

    }

    /*
     *
     */
    public Dimension getUpperPaneSize() {
        return state.upperPaneSize;
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

    /*
     * Sets the visibility of the legend for the profile curve
     *
     * @param visible
     */
    public void setProfileLegendVisible( boolean visible ) {
        state.curvePane.setLegendVisible( visible );
    }

    /*
     * Sets the visibility of the total energy line
     *
     * @param visible
     */
    public void setTotalEnergyLineVisible( boolean visible ) {
        state.curvePane.setTotalEnergyLineVisible( visible );
    }

    /**
     * Creates the pane that shows the molecules being tracked
     *
     * todo: This pane should be a separate class, along with the update() method, below
     *
     * @return a PNode
     * @param curveAreaInsets
     * @param moleculePaneBackgroundColor
     * @param state
     */
    private static PPath createMoleculePane( Dimension upperPaneSize, Insets curveAreaInsets, Color moleculePaneBackgroundColor, MolecularPaneState state ) {
        PPath moleculePane = new PPath( new Rectangle2D.Double( 0, 0,
                                                                upperPaneSize.getWidth(),
                                                                upperPaneSize.getHeight() ) );
        moleculePane.setPaint( moleculePaneBackgroundColor );
        state.moleculeLayer = new PNode();
        state.moleculeLayer.setOffset( curveAreaInsets.left, 0 );
        moleculePane.addChild( state.moleculeLayer );

        // Axis: An arrow that shows separation of molecules and text label
        // They are grouped in a single node so that they can be made visible or
        // invisible as necessary
        state.moleculePaneAxisNode = new PNode();
        state.separationIndicatorArrow = new SeparationIndicatorArrow( Color.black );
        state.moleculePaneAxisNode.addChild( state.separationIndicatorArrow );
        PText siaLabel = new PText( SimStrings.get( "EnergyView.separation" ) );
        siaLabel.setFont( MRConfig.LABEL_FONT );
        siaLabel.rotate( -Math.PI / 2 );
        siaLabel.setOffset( curveAreaInsets.left / 2 - siaLabel.getFullBounds().getWidth() + 2,
                            moleculePane.getFullBounds().getHeight() / 2 + siaLabel.getFullBounds().getHeight() / 2 );
        state.moleculePaneAxisNode.addChild( siaLabel );
        state.moleculePaneAxisNode.setVisible( false );
        moleculePane.addChild( state.moleculePaneAxisNode );

        return moleculePane;
    }

    /**
     * Updates the positions of the molecule graphics in the upper pane. This was originaly supposed
     * to be on all the modules. That's why it's embedded in this class. Since it is only in the
     * SimpleMRModule now, it should be refactored into its own class
     */
    public void update() {
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
            A_BC_AB_C_Reaction reaction = (A_BC_AB_C_Reaction)state.module.getMRModel().getReaction();
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
            double xOffsetFromCenter = Math.min( state.curveAreaSize.getWidth() / 2 - xOffset, dx );
            double x = state.curveAreaSize.getWidth() / 2 + ( xOffsetFromCenter * direction );
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
            molecularPaneState.separationIndicatorArrow.setEndpoints( curveAreaInsets.left / 2 + 10, yMin,
                                                   curveAreaInsets.left / 2 + 10, yMax );

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

    public void setManualControl( boolean manualControl ) {
        state.curvePane.setManualControlEnabled( manualControl );
    }

    public void setSeparationViewVisible(boolean visible) {
        //upperPaneVisible = visible;
        
        System.out.println("Setting separation view is visible: " + visible);

        //upperPane.setVisible( visible );
    }

    public PPath getCurvePane() {
        return state.curvePane;
    }

    public void hideSelectedMolecule( boolean hide ) {
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
                                                                     state.module.getMRModel().getEnergyProfile() );
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
                                                                          state.module.getMRModel().getEnergyProfile() );
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
