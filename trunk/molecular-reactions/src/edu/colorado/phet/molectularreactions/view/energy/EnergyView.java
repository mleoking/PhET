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
import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.molecularreactions.view.EnergySimpleMoleculeGraphic;
import edu.colorado.phet.molecularreactions.view.EnergyMoleculeGraphic;

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
public class EnergyView extends PNode implements PublishingModel.ModelListener, SimpleMolecule.Listener {

    private int width = 300;
    private Dimension moleculePaneSize = new Dimension( 150, width );
    private Dimension curvePaneSize = new Dimension( 300, width );
    private Color moleculePaneBackgroundColor = new Color( 240, 230, 180 );
    private Color energyPaneBackgroundColor = Color.black;
    private Color curveColor = Color.cyan;

    private PNode moleculeLayer = new PNode();

    private SimpleMolecule selectedMolecule;
    private SimpleMolecule nearestToSelectedMolecule;
//    private EnergySimpleMoleculeGraphic selectedMoleculeGraphic;
    private EnergyMoleculeGraphic selectedMoleculeGraphic;
//    private EnergySimpleMoleculeGraphic nearestToSelectedMoleculeGraphic;
    private EnergyMoleculeGraphic nearestToSelectedMoleculeGraphic;

    private PNode cursor;
    private Insets insets = new Insets( 20, 10, 10, 10 );
    private Dimension curveAreaSize;

    /**
     *
     */
    public EnergyView( MRModel model ) {

        // The pane that has the molecules
        PPath moleculePane = createMoleculePane();
        addChild( createMoleculePane() );

        // The pane that has the curve and cursor
        PPath curvePane = createCurvePane( moleculePane, model );
        addChild( curvePane );

        model.addListener( this );
        model.addModelElement( new MoleculeTracker() );
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

        // Create the cursor
        cursor = new EnergyCursor( curveAreaSize.getHeight() );
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
                                                                moleculePaneSize.getHeight(),
                                                                moleculePaneSize.getWidth() ) );
        moleculePane.setPaint( moleculePaneBackgroundColor );
        moleculeLayer.setOffset( insets.left, 0 );
        moleculePane.addChild( moleculeLayer );
        return moleculePane;
    }

    /**
     * Updates the positions of the graphics
     */
    private void update() {
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
                throw new RuntimeException( "internal error");
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

            // Position the molecule graphicsw
            double cmDist = selectedMolecule.getPosition().distance( nearestToSelectedMolecule.getPosition() );
            double edgeDist = cmDist - selectedMolecule.getRadius() - nearestToSelectedMolecule.getRadius();
            double maxSeparation = 100;
            double yOffset = 20;
            double xOffset = 20;

            double xOffsetFromCenter = Math.min( curveAreaSize.getWidth() / 2 - xOffset, edgeDist );
            double x = curveAreaSize.getWidth() / 2 + ( xOffsetFromCenter * direction );
            Point2D midPoint = new Point2D.Double( x, yOffset + maxSeparation / 2 );
            double yMin = midPoint.getY() - Math.min( cmDist, maxSeparation ) / 2;
            double yMax = midPoint.getY() + Math.min( cmDist, maxSeparation ) / 2;

            // set locatation of molecules
            selectedMoleculeGraphic.setOffset( midPoint.getX(), yMax );
            nearestToSelectedMoleculeGraphic.setOffset( midPoint.getX(), yMin );

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

    //--------------------------------------------------------------------------------------------------
    // Implementation of MRModel.Listener
    //--------------------------------------------------------------------------------------------------

    public void modelElementAdded
            ( ModelElement
                    element ) {
        if( element instanceof SimpleMolecule ) {
            SimpleMolecule molecule = (SimpleMolecule)element;
            molecule.addListener( this );
        }
    }

    public void modelElementRemoved
            ( ModelElement
                    element ) {
        if( element instanceof SimpleMolecule ) {
            SimpleMolecule molecule = (SimpleMolecule)element;
            molecule.removeListener( this );
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of SimpleMolecule.Listener
    //--------------------------------------------------------------------------------------------------

    /**
     * Sets the graphics that are shown to be those for the selected molecule and
     * the nearestToSelectedMolecule. If one of them is part of a composite, the
     * graphic for the composite is used.
     *
     * @param molecule
     */
    public void selectionStatusChanged
            ( SimpleMolecule
                    molecule ) {
        if( molecule.getSelectionStatus() == Selectable.SELECTED ) {
            if( selectedMoleculeGraphic != null ) {
                moleculeLayer.removeChild( selectedMoleculeGraphic );
            }
            selectedMolecule = molecule;
            selectedMoleculeGraphic = new EnergyMoleculeGraphic( molecule );
//            selectedMoleculeGraphic = new EnergySimpleMoleculeGraphic( molecule );
            moleculeLayer.addChild( selectedMoleculeGraphic );

        }
        else if( molecule.getSelectionStatus() == Selectable.NEAREST_TO_SELECTED ) {
            nearestToSelectedMolecule = molecule;
            if( nearestToSelectedMoleculeGraphic != null ) {
                moleculeLayer.removeChild( nearestToSelectedMoleculeGraphic );
            }
            nearestToSelectedMoleculeGraphic = new EnergyMoleculeGraphic( molecule );
//            nearestToSelectedMoleculeGraphic = new EnergySimpleMoleculeGraphic( molecule );
            moleculeLayer.addChild( nearestToSelectedMoleculeGraphic );
        }
        update();
    }

    //--------------------------------------------------------------------------------------------------
    // Model element to track distance between molecules of interest
    //--------------------------------------------------------------------------------------------------
    private class MoleculeTracker implements ModelElement {

        public void stepInTime( double dt ) {
            update();
        }
    }
}
