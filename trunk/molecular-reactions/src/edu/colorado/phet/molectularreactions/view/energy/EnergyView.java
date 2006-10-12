/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molectularreactions.view.energy;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.molecularreactions.view.*;
import edu.colorado.phet.molecularreactions.MRConfig;
import edu.colorado.phet.piccolo.nodes.RegisterablePNode;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;

/**
 * EnergyView
 * <p/>
 * A view of the MRModel that shows the potential energy of two individual molecules,
 * or their composite molecule. This is a fairly abstract view.
 * <p/>
 * -------------------------------------------
 * |                                         |
 * |                                         |
 * |           moleculePane                  |
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
public class EnergyView extends PNode implements SimpleObserver {

    private int width = 300;
    private Dimension moleculePaneSize = new Dimension( width, 150 );
    private Dimension curvePaneSize = new Dimension( width, 310 );
    private Color moleculePaneBackgroundColor = MRConfig.MOLECULE_PANE_BACKGROUND;
    private Color energyPaneBackgroundColor = Color.black;
    private Color curveColor = Color.cyan;

    private SimpleMolecule selectedMolecule;
    private SimpleMolecule nearestToSelectedMolecule;
    private EnergyMoleculeGraphic selectedMoleculeGraphic;
    private EnergyMoleculeGraphic nearestToSelectedMoleculeGraphic;
    private MyBondGraphic bondGraphic;

    private EnergyCursor cursor;
    private Insets curveAreaInsets = new Insets( 20, 30, 40, 10 );
    private Dimension curveAreaSize;
    private MRModel model;
//    private ResizingArrow separationIndicatorArrow;
    private Font axisFont = UIManager.getFont( "Label.font" );
    private PNode moleculePaneAxisNode;
    private PNode moleculeLayer;
    private ResizingArrow separationIndicatorArrow;

    /**
     *
     */
    public EnergyView( MRModel model ) {

        this.model = model;

        // The pane that has the molecules
        PPath moleculePane = createMoleculePane();
        addChild( createMoleculePane() );

        // The graphic that shows the reaction mechanics
        PPath legendNode = new PPath( new Rectangle2D.Double( 0, 0, width, 40 ) );
        legendNode.setPaint( Color.black );
        legendNode.setOffset( 0, moleculePaneSize.getHeight() + curvePaneSize.getHeight() );
        ReactionGraphic reactionGraphic = new ReactionGraphic( model.getReaction(), Color.white );
        legendNode.addChild( reactionGraphic );
        reactionGraphic.setOffset( legendNode.getWidth() / 2, legendNode.getHeight() - 10 );
        addChild( legendNode );

        // The pane that has the curve and cursor
        addChild( createCurvePane( moleculePane, model ) );

        // Listen for changes in the selected molecule and the molecule closest to it
        model.addSelectedMoleculeTrackerListener( new SelectedMoleculeListener() );
        update();
    }

    public void reset() {
        selectedMolecule = null;
        nearestToSelectedMolecule = null;

        selectedMoleculeGraphic = null;
        nearestToSelectedMoleculeGraphic = null;
    }


    /**
     * Creates the pane that has the energy curve and cursor
     *
     * @param moleculePane
     * @return a PNode
     */
    private PPath createCurvePane( PPath moleculePane, MRModel model ) {
        PNode curveLayer = new PNode();
        curveLayer.setOffset( curveAreaInsets.left, curveAreaInsets.top );
        PNode cursorLayer = new PNode();
        cursorLayer.setOffset( curveAreaInsets.left, curveAreaInsets.top );
        PPath curvePane = new PPath( new Rectangle2D.Double( 0,
                                                             0,
                                                             curvePaneSize.getWidth(),
                                                             curvePaneSize.getHeight() ) );
        curvePane.setOffset( 0, moleculePane.getHeight() );
        curvePane.setPaint( energyPaneBackgroundColor );
        curvePane.addChild( curveLayer );
        curvePane.addChild( cursorLayer );

        // Create the curve
        curveAreaSize = new Dimension( (int)curvePaneSize.getWidth() - curveAreaInsets.left - curveAreaInsets.right,
                                       (int)curvePaneSize.getHeight() - curveAreaInsets.top - curveAreaInsets.bottom );
        EnergyProfileGraphic energyProfileGraphic = new EnergyProfileGraphic( model.getEnergyProfile(),
                                                                              curveAreaSize,
                                                                              curveColor );
        curveLayer.addChild( energyProfileGraphic );

        // Create the line that shows total energy
        TotalEnergyLine totalEnergyLine = new TotalEnergyLine( curveAreaSize, model );
        curveLayer.addChild( totalEnergyLine );

        // Create the cursor
        cursor = new EnergyCursor( curveAreaSize.getHeight(), 0, curveAreaSize.getWidth(), model );
        cursor.setVisible( false );
        cursorLayer.addChild( cursor );

        // Add axes
        RegisterablePNode xAxis = new RegisterablePNode( new AxisNode( SimStrings.get( "EnergyView.ReactionCoordinate" ),
                                                                       200,
                                                                       Color.white,
                                                                       AxisNode.HORIZONTAL,
                                                                       AxisNode.BOTTOM ) );
        xAxis.setRegistrationPoint( xAxis.getFullBounds().getWidth() / 2, 0 );
        xAxis.setOffset( curvePane.getFullBounds().getWidth() / 2 + curveAreaInsets.left / 2,
                         curvePane.getHeight() - 25 );
        curvePane.addChild( xAxis );

        RegisterablePNode yAxis = new RegisterablePNode( new AxisNode( "Energy", 200, Color.white, AxisNode.VERTICAL, AxisNode.TOP ) );
        yAxis.setRegistrationPoint( yAxis.getFullBounds().getWidth() / 2,
                                    -yAxis.getFullBounds().getHeight() / 2 );
        yAxis.setOffset( curveAreaInsets.left / 2, curvePane.getFullBounds().getHeight() / 2 );
        curvePane.addChild( yAxis );

        return curvePane;
    }

    /**
     * Creates the pane that shows the molecules
     *
     * @return a PNode
     */
    private PPath createMoleculePane() {
        PPath moleculePane = new PPath( new Rectangle2D.Double( 0, 0,
                                                                moleculePaneSize.getWidth(),
                                                                moleculePaneSize.getHeight() ) );
        moleculePane.setPaint( moleculePaneBackgroundColor );
        moleculeLayer = new PNode();
        moleculeLayer.setOffset( curveAreaInsets.left, 0 );
        moleculePane.addChild( moleculeLayer );


        // Axis: An arrow that shows separation of molecules and text label
        // They are grouped in a single node so that they can be made visible or
        // invisible as necessary
        moleculePaneAxisNode = new PNode();
        separationIndicatorArrow = new ResizingArrow( Color.black );
        moleculePaneAxisNode.addChild( separationIndicatorArrow );
        PText siaLabel = new PText( SimStrings.get( "EnergyView.separation" ) );
        siaLabel.setFont( axisFont );
        siaLabel.rotate( -Math.PI / 2 );
        siaLabel.setOffset( curveAreaInsets.left / 2 - siaLabel.getFullBounds().getWidth() + 2,
                            moleculePane.getFullBounds().getHeight() / 2 + siaLabel.getFullBounds().getHeight() / 2 );
        moleculePaneAxisNode.addChild( siaLabel );
        moleculePaneAxisNode.setVisible( false );
        moleculePane.addChild( moleculePaneAxisNode );

        return moleculePane;
    }

    /**
     * Updates the positions of the graphics
     */
    public void update() {
        if( selectedMoleculeGraphic != null && nearestToSelectedMoleculeGraphic != null ) {

            // Which side of the profile the molecules show up on depends on their type

            // Identify which molecule is the free one, and which one is bound in a composite
            SimpleMolecule boundMolecule = null;
            SimpleMolecule freeMolecule = null;
            if( selectedMolecule.isPartOfComposite() ) {
                boundMolecule = selectedMolecule;
                freeMolecule = nearestToSelectedMolecule;
            }
            else if( nearestToSelectedMolecule.isPartOfComposite() ) {
                boundMolecule = nearestToSelectedMolecule;
                freeMolecule = selectedMolecule;
            }
            else {
                System.out.println( "selectedMolecule = " + selectedMolecule );
                System.out.println( "nearestToSelectedMolecule = " + nearestToSelectedMolecule );
                throw new RuntimeException( "internal error" );
            }

            // Figure out on which side of the centerline the molecules should appear
            int direction = 0;
            // If the selected molecule is an A molecule and it's free, we're on the left
            if( selectedMolecule instanceof MoleculeA && selectedMolecule == freeMolecule ) {
                direction = -1;
            }
            // If the selected molecule is an A molecule and it's bound, we're on the right
            else if( selectedMolecule instanceof MoleculeA && selectedMolecule == boundMolecule ) {
                direction = 1;
            }
            // If the selected molecule is a C molecule and it's free, we're on the right
            else if( selectedMolecule instanceof MoleculeC && selectedMolecule == freeMolecule ) {
                direction = 1;
            }
            // If the selected molecule is a C molecule and it's bound, we're on the left
            else if( selectedMolecule instanceof MoleculeC && selectedMolecule == boundMolecule ) {
                direction = -1;
            }
            else {
                throw new RuntimeException( "internal error" );
            }

            // Position the molecule graphics
            double cmDist = selectedMolecule.getPosition().distance( nearestToSelectedMolecule.getPosition() );
            double edgeDist = model.getReaction().getCollisionDistance( freeMolecule, boundMolecule.getParentComposite() );
            // In the middle of the reaction, the collision distance is underfined
            if( Double.isNaN( edgeDist ) ) {
//                edgeDist = model.getReaction().getCollisionDistance( freeMolecule, boundMolecule.getParentComposite() );
                edgeDist = 0;
            }
//            double edgeDist = cmDist - selectedMolecule.getRadius() - nearestToSelectedMolecule.getRadius();
            double maxSeparation = 80;
            double yOffset = 35;
            double xOffset = 20;

            double xOffsetFromCenter = Math.min( curveAreaSize.getWidth() / 2 - xOffset, edgeDist );
            double x = curveAreaSize.getWidth() / 2 + ( xOffsetFromCenter * direction );
            Point2D midPoint = new Point2D.Double( x, yOffset + maxSeparation / 2 );
            double yMin = midPoint.getY() - Math.min( cmDist, maxSeparation ) / 2;
            double yMax = midPoint.getY() + Math.min( cmDist, maxSeparation ) / 2;

            // Set locatation of molecules. Use the *direction* variable we set above
            // to determine which graphic should be on top
            if( freeMolecule instanceof MoleculeC && freeMolecule == selectedMolecule ) {
                selectedMoleculeGraphic.setOffset( midPoint.getX(), yMax );
                nearestToSelectedMoleculeGraphic.setOffset( midPoint.getX(), yMin );
            }
            else if( freeMolecule instanceof MoleculeC && freeMolecule == nearestToSelectedMolecule ) {
                selectedMoleculeGraphic.setOffset( midPoint.getX(), yMin );
                nearestToSelectedMoleculeGraphic.setOffset( midPoint.getX(), yMax );
            }
            else if( freeMolecule instanceof MoleculeA && freeMolecule == selectedMolecule ) {
                selectedMoleculeGraphic.setOffset( midPoint.getX(), yMin );
                nearestToSelectedMoleculeGraphic.setOffset( midPoint.getX(), yMax );
            }
            else if( freeMolecule instanceof MoleculeA && freeMolecule == nearestToSelectedMolecule ) {
                selectedMoleculeGraphic.setOffset( midPoint.getX(), yMax );
                nearestToSelectedMoleculeGraphic.setOffset( midPoint.getX(), yMin );
            }

            // Set the size of the separation indicator arrow
            separationIndicatorArrow.setEndpoints( curveAreaInsets.left / 2 + 10, yMin,
                                                   curveAreaInsets.left / 2 + 10, yMax );
//            separationIndicatorArrow.setEndpoints( curveAreaInsets.left / 2, midPoint.getY() - edgeDist / 2,
//                                                   curveAreaInsets.left / 2, midPoint.getY() + edgeDist / 2 );

            // Set the location of the bond graphic
            if( bondGraphic != null ) {
                bondGraphic.update( boundMolecule );
            }

            // set location of cursor
            cursor.setOffset( midPoint.getX(), 0 );
        }
        else if( selectedMoleculeGraphic != null ) {
            selectedMoleculeGraphic.setOffset( 20, 20 );
        }
        else if( nearestToSelectedMoleculeGraphic != null ) {
            nearestToSelectedMoleculeGraphic.setOffset( 20, 50 );
        }
    }

    /**
     * Sets the graphics that are shown to be those for the selected molecule and
     * the nearestToSelectedMolecule. If one of them is part of a composite, the
     * graphic for the composite is used.
     *
     * @param molecule
     */
    private void selectionStatusChanged( SimpleMolecule molecule ) {
        if( molecule.getSelectionStatus() == Selectable.SELECTED ) {
            if( selectedMoleculeGraphic != null ) {
                moleculeLayer.removeChild( selectedMoleculeGraphic );
            }
            selectedMolecule = molecule;
            selectedMoleculeGraphic = new EnergyMoleculeGraphic( molecule );
            moleculeLayer.addChild( selectedMoleculeGraphic );

            molecule.addObserver( new SimpleObserver() {
                public void update() {
                    EnergyView.this.update();
                }
            } );
        }
        update();
    }

    public void setManualControl( boolean manualControl ) {
        cursor.setManualControlEnabled( manualControl );
    }

    private class SelectedMoleculeListener implements SelectedMoleculeTracker.Listener {
        public void moleculeBeingTrackedChanged( SimpleMolecule newTrackedMolecule,
                                                 SimpleMolecule prevTrackedMolecule ) {
            if( selectedMolecule != null ) {
                selectedMolecule.removeObserver( EnergyView.this );
            }
            selectedMolecule = newTrackedMolecule;
            if( selectedMoleculeGraphic != null ) {
                moleculeLayer.removeChild( selectedMoleculeGraphic );
            }

            if( newTrackedMolecule != null ) {
                selectedMoleculeGraphic = new EnergyMoleculeGraphic( newTrackedMolecule );
                moleculeLayer.addChild( selectedMoleculeGraphic );
                newTrackedMolecule.addObserver( EnergyView.this );
//                if( bondGraphic != null ) {
//                    removeChild( bondGraphic );
//                }
//                if( newTrackedMolecule.isPartOfComposite() ) {
//                    bondGraphic = new MyBondGraphic( selectedMoleculeGraphic );
//                    addChild( bondGraphic );
//                }
                moleculePaneAxisNode.setVisible( true );
            }
            else {
                moleculePaneAxisNode.setVisible( false );
            }

            cursor.setVisible( selectedMolecule != null );
        }

        public void closestMoleculeChanged( SimpleMolecule newClosestMolecule,
                                            SimpleMolecule prevClosestMolecule ) {
            if( nearestToSelectedMolecule != null ) {
                nearestToSelectedMolecule.removeObserver( EnergyView.this );
            }
            nearestToSelectedMolecule = newClosestMolecule;
            if( nearestToSelectedMoleculeGraphic != null ) {
                moleculeLayer.removeChild( nearestToSelectedMoleculeGraphic );
            }
            nearestToSelectedMoleculeGraphic = new EnergyMoleculeGraphic( newClosestMolecule );
            moleculeLayer.addChild( nearestToSelectedMoleculeGraphic );

            newClosestMolecule.addObserver( EnergyView.this );
//            if( bondGraphic != null ) {
//                removeChild( bondGraphic );
//            }
//            if( nearestToSelectedMolecule.isPartOfComposite() ) {
//                bondGraphic = new MyBondGraphic( nearestToSelectedMoleculeGraphic );
//                addChild( bondGraphic );
//            }

            update();
        }
    }

    private class MyBondGraphic extends BondGraphic {
        private EnergyMoleculeGraphic emg;

        public MyBondGraphic( EnergyMoleculeGraphic emg ) {
            super( new Bond( selectedMolecule, nearestToSelectedMolecule ) );
            this.emg = emg;
        }

        public void update( SimpleMolecule sm ) {
        }
    }
}
