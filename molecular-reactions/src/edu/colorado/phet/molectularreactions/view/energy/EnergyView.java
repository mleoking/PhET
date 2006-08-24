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
import edu.colorado.phet.molecularreactions.model.MRModel;
import edu.colorado.phet.molecularreactions.model.SimpleMolecule;
import edu.colorado.phet.molecularreactions.model.Selectable;
import edu.colorado.phet.molecularreactions.model.PublishingModel;
import edu.colorado.phet.molecularreactions.view.EnergySimpleMoleculeGraphic;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;

/**
 * EnergyView
 * <p/>
 * A view of the MRModel that shows the potential energy of two individual molecules, or their composite
 * molecule. This is a fairly abstract view.
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
    private PNode selectedMoleculeGraphic;
    private PNode nearestToSelectedMoleculeGraphic;

    private PNode cursor;
    private Insets insets = new Insets( 20, 10, 10, 10 );

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
        PPath curvePane = new PPath( new Rectangle2D.Double( 0, 0,
                                                             curvePaneSize.getWidth(),
                                                             curvePaneSize.getHeight() ) );
        curvePane.setOffset( 0, moleculePane.getHeight() );
        curvePane.setPaint( energyPaneBackgroundColor );

        curvePane.addChild( curveLayer );
        curvePane.addChild( cursorLayer );

        // Create the curve
        Dimension curveAreaSize = new Dimension( (int)curvePaneSize.getWidth() - insets.left - insets.right,
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
        moleculePane.addChild( moleculeLayer );
        return moleculePane;
    }

    private void update() {
        if( selectedMoleculeGraphic != null && nearestToSelectedMoleculeGraphic != null ) {
            double dist = selectedMolecule.getPosition().distance( nearestToSelectedMolecule.getPosition() );
            double maxSeparation = 100;
            double yOffset = 20;
            double xOffset = 20;
            double x = Math.max( xOffset, curvePaneSize.getWidth() / 2 - dist );
            Point2D midPoint = new Point2D.Double( x, yOffset + maxSeparation / 2 );
            double yMin = midPoint.getY() - Math.min( dist, maxSeparation ) / 2;
            double yMax = midPoint.getY() + Math.min( dist, maxSeparation ) / 2;

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

    public void modelElementAdded( ModelElement element ) {
        if( element instanceof SimpleMolecule ) {
            SimpleMolecule molecule = (SimpleMolecule)element;
            molecule.addListener( this );
        }
    }

    public void modelElementRemoved( ModelElement element ) {
        if( element instanceof SimpleMolecule ) {
            SimpleMolecule molecule = (SimpleMolecule)element;
            molecule.removeListener( this );
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of SimpleMolecule.Listener
    //--------------------------------------------------------------------------------------------------

    public void selectionStatusChanged( SimpleMolecule molecule ) {
        if( molecule.getSelectionStatus() == Selectable.SELECTED ) {
            if( selectedMoleculeGraphic != null ) {
                moleculeLayer.removeChild( selectedMoleculeGraphic );
            }
            selectedMolecule = molecule;
            selectedMoleculeGraphic = new EnergySimpleMoleculeGraphic( molecule );
            moleculeLayer.addChild( selectedMoleculeGraphic );

        }
        else if( molecule.getSelectionStatus() == Selectable.NEAREST_TO_SELECTED ) {
            nearestToSelectedMolecule = molecule;
            if( nearestToSelectedMoleculeGraphic != null ) {
                moleculeLayer.removeChild( nearestToSelectedMoleculeGraphic );
            }
            nearestToSelectedMoleculeGraphic = new EnergySimpleMoleculeGraphic( molecule );
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
