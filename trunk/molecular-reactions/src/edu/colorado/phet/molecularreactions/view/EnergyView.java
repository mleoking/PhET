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
    private Dimension size = new Dimension( 300, 300 );
    private Color backgroundColor = Color.black;
    private Color curveColor = Color.cyan;

    private PNode moleculeLayer = new PNode();
    private PNode curveLayer = new PNode();
    private PNode cursorLayer = new PNode();

    private SimpleMolecule selectedMolecule;
    private SimpleMolecule nearestToSelectedMolecule;
    private PNode selectedMoleculeGraphic;
    private PNode nearestToSelectedMoleculeGraphic;

    /**
     *
     */
    public EnergyView( MRModel model) {
        PPath background = new PPath( new Rectangle2D.Double( 0, 0,
                                                              size.getWidth(),
                                                              size.getHeight() ) );
        background.setPaint( backgroundColor );

        addChild( background );
        addChild( moleculeLayer );
        addChild( curveLayer );
        addChild( cursorLayer );

        EnergyCurve energyCurve = new EnergyCurve( size.getWidth(), curveColor );
        energyCurve.setLeftLevel( 250 );
        energyCurve.setRightLevel( 200 );
        energyCurve.setPeakLevel( 100 );

        curveLayer.addChild( energyCurve );

        model.addListener( this );
        model.addModelElement( new MoleculeTracker() );
        update();
    }

    private void update() {
        if( selectedMoleculeGraphic != null && nearestToSelectedMoleculeGraphic != null) {
            double dist = selectedMolecule.getPosition().distance( nearestToSelectedMolecule.getPosition() );
            double maxSeparation = 100;
            double yOffset = 20;
            double xOffset = 20;
            double x = Math.max( xOffset, size.getWidth() / 2 - dist );
            Point2D midPoint = new Point2D.Double( x, yOffset + maxSeparation / 2 );
            double yMin = midPoint.getY() - Math.min( dist, maxSeparation ) / 2;
            double yMax = midPoint.getY() + Math.min( dist, maxSeparation ) / 2;
            selectedMoleculeGraphic.setOffset( midPoint.getX(), yMax );
            nearestToSelectedMoleculeGraphic.setOffset( midPoint.getX(), yMin );
        }
        else if( selectedMoleculeGraphic != null ) {
            selectedMoleculeGraphic.setOffset( 20, 20 );
        }
        else if( nearestToSelectedMoleculeGraphic != null ) {
            nearestToSelectedMoleculeGraphic.setOffset( 20, 50 );
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

        public EnergyCurve( double width, Color color ) {
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

        public void setLeftLevel( double leftLevel ) {
            this.leftLevel = leftLevel;
            update();
        }

        public void setPeakLevel( double peakLevel ) {
            this.peakLevel = peakLevel;
            update();
        }

        public void setRightLevel( double rightLevel ) {
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
