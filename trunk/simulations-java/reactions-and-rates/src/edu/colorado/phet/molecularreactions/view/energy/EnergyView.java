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
import edu.colorado.phet.piccolo.nodes.RegisterablePNode;
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
    private final Color energyPaneBackgroundColor = MRConfig.ENERGY_PANE_BACKGROUND;
    private final Color curveColor = MRConfig.POTENTIAL_ENERGY_COLOR;
    private final Insets curveAreaInsets = new Insets( 20, 30, 40, 10 );

    private static class State {
        Dimension upperPaneSize;
        Dimension curvePaneSize;

        SimpleMolecule selectedMolecule;
        SimpleMolecule nearestToSelectedMolecule;
        EnergyMoleculeGraphic selectedMoleculeGraphic;
        EnergyMoleculeGraphic nearestToSelectedMoleculeGraphic;

        EnergyCursor cursor;

        Dimension curveAreaSize;
        Font labelFont;
        PNode moleculePaneAxisNode;
        PNode moleculeLayer;
        SeparationIndicatorArrow separationIndicatorArrow;
        MRModule module;
        PPath curvePane;
        EnergyLine energyLine;
        PPath upperPane;
        EnergyProfileGraphic energyProfileGraphic;
    }

    private volatile State state;

    public EnergyView( MRModule module, int width, Dimension upperPaneSize ) {
        initialize( module, width, upperPaneSize );
    }

    private void initialize( MRModule module, int width, Dimension upperPaneSize ) {
        state = new State();

        state.upperPaneSize = upperPaneSize;
        state.curvePaneSize = new Dimension( width, (int)( MRConfig.ENERGY_VIEW_SIZE.getHeight() )
                                              - upperPaneSize.height
                                              - MRConfig.ENERGY_VIEW_REACTION_LEGEND_SIZE.height );
        state.module = module;
        MRModel model = module.getMRModel();

        // Set up the font for labels
        Font defaultFont = MRConfig.LABEL_FONT;
        state.labelFont = new Font( defaultFont.getName(), Font.BOLD, defaultFont.getSize() + 1 );

        // The pane that has the molecules
        PPath moleculePane = createMoleculePane();
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

//        upperPane.addPropertyChangeListener( new PropertyChangeListener() {
//
//            public void propertyChange( PropertyChangeEvent evt ) {
//                System.out.println( "upperPane = " + upperPane );
//            }
//        });
//        module.getSimulationPanel().addComponentListener( new ComponentAdapter() {
//            public void componentResized( ComponentEvent e ) {
//                System.out.println( "upperPane = " + upperPane );
//            }
//        } );

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
        state.curvePane = createCurvePane( moleculePane, model );
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

        state.selectedMolecule = null;
        state.nearestToSelectedMolecule = null;

        if( state.selectedMoleculeGraphic != null ) {
            state.moleculeLayer.removeChild( state.selectedMoleculeGraphic );
        }
        if( state.nearestToSelectedMoleculeGraphic != null ) {
            state.moleculeLayer.removeChild( state.nearestToSelectedMoleculeGraphic );
        }
        state.selectedMoleculeGraphic = null;
        state.nearestToSelectedMoleculeGraphic = null;

        // Listen for changes in the selected molecule and the molecule closest to it
        state.module.getMRModel().addSelectedMoleculeTrackerListener( new SelectedMoleculeListener() );
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
        state.energyLine.setLegendVisible( visible );
        state.energyProfileGraphic.setLegendVisible( visible );
    }

    /*
     * Sets the visibility of the total energy line
     *
     * @param visible
     */
    public void setTotalEnergyLineVisible( boolean visible ) {
        state.energyLine.setVisible( visible );
    }

    /*
     * Creates the pane that has the energy curve, cursor, and the total energy line
     *
     * @param moleculePane
     * @return a PNode
     */
    private PPath createCurvePane( PPath moleculePane, final MRModel model ) {
        final PNode totalEnergyLineLayer = new PNode();
        totalEnergyLineLayer.setOffset( curveAreaInsets.left, curveAreaInsets.top );
        final PNode curveLayer = new PNode();
        curveLayer.setOffset( curveAreaInsets.left, curveAreaInsets.top );
        PNode cursorLayer = new PNode();
        cursorLayer.setOffset( curveAreaInsets.left, curveAreaInsets.top );

        // the -1 adjusts for a stroke width issue between this pane and the chart pane.
        PPath curvePane = new PPath( new Rectangle2D.Double( 0,
                                                             0,
                                                             state.curvePaneSize.getWidth() - 1,
                                                             state.curvePaneSize.getHeight() ) );
        curvePane.setOffset( 0, moleculePane.getHeight() );
        curvePane.setPaint( energyPaneBackgroundColor );
        curvePane.setStrokePaint( new Color( 0, 0, 0, 0 ) );
        curvePane.addChild( totalEnergyLineLayer );
        curvePane.addChild( curveLayer );
        curvePane.addChild( cursorLayer );

        // Determine the size of the area where the curve will appear
        state.curveAreaSize = new Dimension( (int)state.curvePaneSize.getWidth() - curveAreaInsets.left - curveAreaInsets.right,
                                       (int)state.curvePaneSize.getHeight() - curveAreaInsets.top - curveAreaInsets.bottom );

        // Create the line that shows total energy, and a legend for it
        state.energyLine = new EnergyLine( state.curveAreaSize, model, state.module.getClock() );
        totalEnergyLineLayer.addChild( state.energyLine );

        // Create the curve, and add a listener to the model that will update the curve if the
        // model's energy profile changes
        createCurve( model, curveLayer );
        model.addListener( new MRModel.ModelListenerAdapter() {
            public void notifyEnergyProfileChanged( EnergyProfile profile ) {
                createCurve( model, curveLayer );
            }
        } );

        // Create the cursor
        state.cursor = new EnergyCursor( state.curveAreaSize.getHeight(), 0, state.curveAreaSize.getWidth(), model );
        state.cursor.setVisible( false );
        cursorLayer.addChild( state.cursor );

        // Add axes
        RegisterablePNode xAxis = new RegisterablePNode( new AxisNode( SimStrings.get( "EnergyView.ReactionCoordinate" ),
                                                                       200,
                                                                       MRConfig.ENERGY_PANE_TEXT_COLOR,
                                                                       AxisNode.HORIZONTAL,
                                                                       AxisNode.BOTTOM ) );
        xAxis.setRegistrationPoint( xAxis.getFullBounds().getWidth() / 2, 0 );
        xAxis.setOffset( curvePane.getFullBounds().getWidth() / 2 + curveAreaInsets.left / 2,
                         curvePane.getHeight() - 25 );
        curvePane.addChild( xAxis );

        RegisterablePNode yAxis = new RegisterablePNode( new AxisNode( "Energy", 200,
                                                                       MRConfig.ENERGY_PANE_TEXT_COLOR,
                                                                       AxisNode.VERTICAL,
                                                                       AxisNode.TOP ) );
        yAxis.setRegistrationPoint( yAxis.getFullBounds().getWidth() / 2,
                                    -yAxis.getFullBounds().getHeight() / 2 );
        yAxis.setOffset( curveAreaInsets.left / 2, curvePane.getFullBounds().getHeight() / 2 );
        curvePane.addChild( yAxis );

        return curvePane;
    }

    /*
     * Creates the curve graphic for the profile
     * @param model
     * @param curveLayer
     */
    private void createCurve( MRModel model, PNode curveLayer ) {
        if( state.energyProfileGraphic != null ) {
            curveLayer.removeChild( state.energyProfileGraphic );
        }
        state.energyProfileGraphic = new EnergyProfileGraphic( model.getEnergyProfile(),
                                                         state.curveAreaSize,
                                                         curveColor );
        curveLayer.addChild( state.energyProfileGraphic );
    }

    /**
     * Creates the pane that shows the molecules being tracked
     *
     * todo: This pane should be a separate class, along with the update() method, below
     *
     * @return a PNode
     */
    private PPath createMoleculePane() {
        PPath moleculePane = new PPath( new Rectangle2D.Double( 0, 0,
                                                                state.upperPaneSize.getWidth(),
                                                                state.upperPaneSize.getHeight() ) );
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
        siaLabel.setFont( state.labelFont );
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
        if( state.selectedMolecule != null && state.selectedMoleculeGraphic != null && state.nearestToSelectedMoleculeGraphic != null ) {

            // Which side of the profile the molecules show up on depends on their type

            // Identify which molecule is the free one, and which one is bound in a composite
            // There is an intermediate state in the model where the reaction has occured but
            // the agent that tracks the selected molecule and the nearest one to it has not been
            // updated yet. We have to handle that by returning without updating ourselves/
            SimpleMolecule boundMolecule = null;
            SimpleMolecule freeMolecule = null;
            if( state.selectedMolecule.isPartOfComposite() ) {
                boundMolecule = state.selectedMolecule;
                if( !state.nearestToSelectedMolecule.isPartOfComposite() ) {
                    freeMolecule = state.nearestToSelectedMolecule;
                }
                else {
                    return;
                }
            }
            else if( state.nearestToSelectedMolecule.isPartOfComposite() ) {
                boundMolecule = state.nearestToSelectedMolecule;
                if( !state.selectedMolecule.isPartOfComposite() ) {
                    freeMolecule = state.selectedMolecule;
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
            if( state.selectedMolecule instanceof MoleculeA && state.selectedMolecule == freeMolecule ) {
                direction = -1;
            }
            // If the selected molecule is an A molecule and it's bound, we're on the right
            else if( state.selectedMolecule instanceof MoleculeA && state.selectedMolecule == boundMolecule ) {
                direction = 1;
            }
            // If the selected molecule is a C molecule and it's free, we're on the right
            else if( state.selectedMolecule instanceof MoleculeC && state.selectedMolecule == freeMolecule ) {
                direction = 1;
            }
            // If the selected molecule is a C molecule and it's bound, we're on the left
            else if( state.selectedMolecule instanceof MoleculeC && state.selectedMolecule == boundMolecule ) {
                direction = -1;
            }
            else {
                throw new RuntimeException( "internal error" );
            }

            // Position the molecule graphics
            double cmDist = state.selectedMolecule.getPosition().distance( state.nearestToSelectedMolecule.getPosition() );
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
            double separationAtFootOfHill = Math.min( state.selectedMolecule.getRadius(), state.nearestToSelectedMolecule.getRadius() );

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
            double maxX = state.energyProfileGraphic.getIntersectionWithHorizontal( state.energyLine.getEnergyLineY(), x);

            x = Math.min(x, maxX);

            Point2D midPoint = new Point2D.Double( x,  y );

            double yMin = midPoint.getY() - Math.min( cmDist, maxSeparation ) / 2;
            double yMax = midPoint.getY() + Math.min( cmDist, maxSeparation ) / 2;

            // Set locatation of molecules. Use the *direction* variable we set above
            // to determine which graphic should be on top
            if( freeMolecule instanceof MoleculeC && freeMolecule == state.selectedMolecule ) {
                state.selectedMoleculeGraphic.setOffset( midPoint.getX(), yMax );
                state.nearestToSelectedMoleculeGraphic.setOffset( midPoint.getX(), yMin );
            }
            else if( freeMolecule instanceof MoleculeC && freeMolecule == state.nearestToSelectedMolecule ) {
                state.selectedMoleculeGraphic.setOffset( midPoint.getX(), yMax );
                state.nearestToSelectedMoleculeGraphic.setOffset( midPoint.getX(), yMin );
            }
            else if( freeMolecule instanceof MoleculeA && freeMolecule == state.selectedMolecule ) {
                state.selectedMoleculeGraphic.setOffset( midPoint.getX(), yMin );
                state.nearestToSelectedMoleculeGraphic.setOffset( midPoint.getX(), yMax );
            }
            else if( freeMolecule instanceof MoleculeA && freeMolecule == state.nearestToSelectedMolecule ) {
                state.selectedMoleculeGraphic.setOffset( midPoint.getX(), yMin );
                state.nearestToSelectedMoleculeGraphic.setOffset( midPoint.getX(), yMax );
            }

            // Set the size of the separation indicator arrow
            state.separationIndicatorArrow.setEndpoints( curveAreaInsets.left / 2 + 10, yMin,
                                                   curveAreaInsets.left / 2 + 10, yMax );

            // set location of cursor
            state.cursor.setOffset( midPoint.getX(), 0 );
        }
        else if( state.selectedMoleculeGraphic != null ) {
            state.selectedMoleculeGraphic.setOffset( 20, 20 );
        }
        else if( state.nearestToSelectedMoleculeGraphic != null ) {
            state.nearestToSelectedMoleculeGraphic.setOffset( 20, 50 );
        }
    }

    public void setManualControl( boolean manualControl ) {
        state.cursor.setManualControlEnabled( manualControl );
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
        state.energyProfileGraphic.setManipulable( manipulable );
    }

    public PNode getUpperPaneContents() {
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
            if( state.selectedMolecule != null ) {
                state.selectedMolecule.removeObserver( EnergyView.this );
            }

            state.selectedMolecule = newTrackedMolecule;
            if( state.selectedMoleculeGraphic != null
                && state.moleculeLayer.getChildrenReference().contains( state.selectedMoleculeGraphic ) ) {
                state.moleculeLayer.removeChild( state.selectedMoleculeGraphic );
            }

            if( newTrackedMolecule != null ) {
                state.selectedMoleculeGraphic = new EnergyMoleculeGraphic( newTrackedMolecule.getFullMolecule(),
                                                                     state.module.getMRModel().getEnergyProfile() );
                state.moleculeLayer.addChild( state.selectedMoleculeGraphic );
                newTrackedMolecule.addObserver( EnergyView.this );
                state.moleculePaneAxisNode.setVisible( true );
            }
            else {
                state.moleculePaneAxisNode.setVisible( false );

            }
            state.cursor.setVisible( state.selectedMolecule != null );
        }

        public void closestMoleculeChanged( SimpleMolecule newClosestMolecule,
                                            SimpleMolecule prevClosestMolecule ) {
            if( state.nearestToSelectedMolecule != null ) {
                state.nearestToSelectedMolecule.removeObserver( EnergyView.this );
            }

            state.nearestToSelectedMolecule = newClosestMolecule;
            if( state.nearestToSelectedMoleculeGraphic != null ) {
                state.moleculeLayer.removeChild( state.nearestToSelectedMoleculeGraphic );
            }
            state.nearestToSelectedMoleculeGraphic = new EnergyMoleculeGraphic( newClosestMolecule.getFullMolecule(),
                                                                          state.module.getMRModel().getEnergyProfile() );
            state.moleculeLayer.addChild( state.nearestToSelectedMoleculeGraphic );

            newClosestMolecule.addObserver( EnergyView.this );

            update();
        }


        public void notifyEnergyProfileChanged( EnergyProfile profile ) {
            if( state.selectedMoleculeGraphic != null ) {
                state.moleculeLayer.removeChild( state.selectedMoleculeGraphic );
                state.selectedMoleculeGraphic = new EnergyMoleculeGraphic( state.selectedMolecule.getFullMolecule(),
                                                                     profile );
                state.moleculeLayer.addChild( state.selectedMoleculeGraphic );
            }
            if( state.nearestToSelectedMoleculeGraphic != null ) {
                state.moleculeLayer.removeChild( state.nearestToSelectedMoleculeGraphic );
                state.nearestToSelectedMoleculeGraphic = new EnergyMoleculeGraphic( state.nearestToSelectedMolecule.getFullMolecule(),
                                                                              profile );
                state.moleculeLayer.addChild( state.nearestToSelectedMoleculeGraphic );
            }
        }
    }
}
