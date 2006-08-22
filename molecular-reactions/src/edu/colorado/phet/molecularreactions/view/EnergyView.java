/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.view;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.colorado.phet.common.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.molecularreactions.model.MRModel;
import edu.colorado.phet.molecularreactions.model.SimpleMolecule;
import edu.colorado.phet.molecularreactions.model.Selectable;
import edu.colorado.phet.molecularreactions.model.PublishingModel;
import edu.colorado.phet.piccolo.nodes.RegisterablePNode;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;

/**
 * EnergyView
 * <p/>
 * A view of the MRModel that shows the potential energy of two individual molecules, or their compound
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
    private Insets cursorInsets = new Insets( 20, 0, 10, 0 );

    /**
     *
     */
    public EnergyView( MRModel model ) {

        // The pane that has the molecules
        PPath moleculePane = createMoleculePane();
        addChild( createMoleculePane() );

        // The pane that has the curve and cursor
        PPath curvePane = createCurvePane( moleculePane );
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
    private PPath createCurvePane( PPath moleculePane ) {
        PNode curveLayer = new PNode();
        PNode cursorLayer = new PNode();
        PPath curvePane = new PPath( new Rectangle2D.Double( 0, 0,
                                                             curvePaneSize.getWidth(),
                                                             curvePaneSize.getHeight() ) );
        curvePane.setOffset( 0, moleculePane.getHeight() );
        curvePane.setPaint( energyPaneBackgroundColor );

        curvePane.addChild( curveLayer );
        curvePane.addChild( cursorLayer );

        // Create the curve
        EnergyCurve energyCurve = new EnergyCurve( curvePaneSize.getWidth(), curveColor );
        energyCurve.setLeftLevel( 250 );
        energyCurve.setRightLevel( 200 );
        energyCurve.setPeakLevel( 100 );
        curveLayer.addChild( energyCurve );

        // Create the cursor
        cursor = new Cursor( curvePane.getHeight() - cursorInsets.top - cursorInsets.bottom );
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
            cursor.setOffset( midPoint.getX(), cursorInsets.top );
        }
        else if( selectedMoleculeGraphic != null ) {
            selectedMoleculeGraphic.setOffset( 20, 20 );
        }
        else if( nearestToSelectedMoleculeGraphic != null ) {
            nearestToSelectedMoleculeGraphic.setOffset( 20, 50 );
        }
    }

    //--------------------------------------------------------------------------------------------------
    // The cursor
    //--------------------------------------------------------------------------------------------------
    private class Cursor extends RegisterablePNode {
        private double width = 10;

        Cursor( double height ) {
            Rectangle2D cursorShape = new Rectangle2D.Double( 0, 0, width, height );
            PPath cursorPPath = new PPath( cursorShape );
            cursorPPath.setStroke( new BasicStroke( 1 ) );
            cursorPPath.setStrokePaint( new Color( 200, 200, 200 ) );
            cursorPPath.setPaint( new Color( 200, 200, 200, 200 ) );
            addChild( cursorPPath );

            setRegistrationPoint( width / 2, 0 );
        }
    }

    //--------------------------------------------------------------------------------------------------
    // The curve
    //--------------------------------------------------------------------------------------------------

    private class EnergyCurve extends PPath {
        private double leftLevel;
        private double peakLevel;
        private double rightLevel;
        private double width;
        private Color color;

        EnergyCurve( double width, Color color ) {
            this.width = width;
            this.color = color;
            update();
        }

        private void update() {
            DoubleGeneralPath curve = new DoubleGeneralPath();

            double x1 = width * 0.4;
            double x2 = width * 0.5;
            double x3 = width * 0.6;
            curve.moveTo( 0, leftLevel );
            curve.lineTo( x1, leftLevel );

            curve.curveTo( x1 + ( x2 - x1 ) * 0.33, leftLevel,
                           x1 + ( x2 - x1 ) * 0.66, peakLevel,
                           x2, peakLevel );
            curve.curveTo( x2 + ( x3 - x2 ) * 0.33, peakLevel,
                           x2 + ( x3 - x2 ) * 0.66, rightLevel,
                           x3, rightLevel );
            curve.lineTo( width, rightLevel );

            setPathTo( curve.getGeneralPath() );
            setStrokePaint( color );
            setStroke( new BasicStroke( 3 ) );
        }

        void setLeftLevel( double leftLevel ) {
            this.leftLevel = leftLevel;
            update();
        }

        void setPeakLevel( double peakLevel ) {
            this.peakLevel = peakLevel;
            update();
        }

        void setRightLevel( double rightLevel ) {
            this.rightLevel = rightLevel;
            update();
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
