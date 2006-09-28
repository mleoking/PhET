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
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.molecularreactions.view.EnergySimpleMoleculeGraphic;
import edu.colorado.phet.molecularreactions.view.EnergyMoleculeGraphic;
import edu.colorado.phet.molecularreactions.view.ReactionGraphic;
import edu.colorado.phet.molecularreactions.view.BondGraphic;
import edu.colorado.phet.molecularreactions.MRConfig;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;

/**
 * EnergyView
 * <p/>
 * A view of the MRModel that shows the potential energy of two individual molecules,
 * or their composite molecule. This is a fairly abstract view.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class EnergyView extends PNode implements SimpleObserver {

    private int width = 300;
    private Dimension moleculePaneSize = new Dimension( width, 150 );
    private Dimension curvePaneSize = new Dimension( width, 300 );
    private Color moleculePaneBackgroundColor = MRConfig.MOLECULE_PANE_BACKGROUND;
    private Color energyPaneBackgroundColor = Color.black;
    private Color curveColor = Color.cyan;

    private PNode moleculeLayer = new PNode();

    private SimpleMolecule selectedMolecule;
    private SimpleMolecule nearestToSelectedMolecule;
    private EnergyMoleculeGraphic selectedMoleculeGraphic;
    private EnergyMoleculeGraphic nearestToSelectedMoleculeGraphic;
    private MyBondGraphic bondGraphic;

    private EnergyCursor cursor;
    private Insets insets = new Insets( 20, 10, 10, 10 );
    private Dimension curveAreaSize;
    private MRModel model;

    /**
     *
     */
    public EnergyView( MRModel model ) {

        this.model = model;

        // The pane that has the molecules
        PPath moleculePane = createMoleculePane();
        addChild( createMoleculePane() );

        // The pane that has the curve and cursor
        PPath curvePane = createCurvePane( moleculePane, model );
        addChild( curvePane );

        // The graphic that shows the reaction mechanics
        PPath legendNode = new PPath( new Rectangle2D.Double( 0, 0, width, 50 ) );
        legendNode.setPaint( Color.black );
        legendNode.setOffset( 0, moleculePaneSize.getHeight() + curvePaneSize.getHeight() );
        ReactionGraphic reactionGraphic = new ReactionGraphic( model.getReaction(), Color.white );
        legendNode.addChild( reactionGraphic );
        reactionGraphic.setOffset( legendNode.getWidth() / 2, legendNode.getHeight() - 20 );
        addChild( legendNode );

        // Listen for changes in the selected molecule and the molecule closest to it
        model.addSelectedMoleculeTrackerListener( new SelectedMoleculeListener() );
        update();
    }

    /**
     * Creates the pane that has the energy curve and cursor
     *
     * @param moleculePane
     * @return a PNode
     */
    private PPath createCurvePane( PPath moleculePane, MRModel model ) {
        PNode curveLayer = new PNode();
        curveLayer.setOffset( insets.left, insets.top );
        PNode cursorLayer = new PNode();
        cursorLayer.setOffset( insets.left, insets.top );
        PPath curvePane = new PPath( new Rectangle2D.Double( 0,
                                                             0,
                                                             curvePaneSize.getWidth(),
                                                             curvePaneSize.getHeight() ) );
        curvePane.setOffset( 0, moleculePane.getHeight() );
        curvePane.setPaint( energyPaneBackgroundColor );

        curvePane.addChild( curveLayer );
        curvePane.addChild( cursorLayer );

        // Create the curve
        curveAreaSize = new Dimension( (int)curvePaneSize.getWidth() - insets.left - insets.right,
                                       (int)curvePaneSize.getHeight() - insets.top - insets.bottom );
        EnergyProfileGraphic energyProfileGraphic = new EnergyProfileGraphic( model.getEnergyProfile(),
                                                                              curveAreaSize,
                                                                              curveColor );
        curveLayer.addChild( energyProfileGraphic );

        // Create the line that shows total energy
        Rectangle2D curveAreaBounds = new Rectangle2D.Double( insets.left,
                                                              insets.top,
                                                              curveAreaSize.getWidth(),
                                                              curveAreaSize.getHeight());
        TotalEnergyLine totalEnergyLine = new TotalEnergyLine( curveAreaSize, model );
        curveLayer.addChild( totalEnergyLine );

        // Create the cursor
        cursor = new EnergyCursor( curveAreaSize.getHeight(), 0, curveAreaSize.getWidth(), model );
        cursor.setVisible( false );
        cursorLayer.addChild( cursor );

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
        moleculeLayer.setOffset( insets.left, 0 );
        moleculePane.addChild( moleculeLayer );
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
