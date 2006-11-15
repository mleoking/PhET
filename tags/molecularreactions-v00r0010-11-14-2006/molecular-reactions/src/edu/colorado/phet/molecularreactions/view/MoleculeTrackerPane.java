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

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.molecularreactions.MRConfig;
import edu.colorado.phet.molecularreactions.model.MoleculeA;
import edu.colorado.phet.molecularreactions.model.MoleculeC;
import edu.colorado.phet.molecularreactions.model.SelectedMoleculeTracker;
import edu.colorado.phet.molecularreactions.model.SimpleMolecule;
import edu.colorado.phet.molecularreactions.modules.MRModule;
import edu.colorado.phet.molecularreactions.view.energy.EnergyCursor;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * MoleculeTrackerPane
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MoleculeTrackerPane extends PNode implements SimpleObserver {

    private int width = 300;
    private Dimension moleculePaneSize = new Dimension( width, 150 );
    private Color moleculePaneBackgroundColor = MRConfig.MOLECULE_PANE_BACKGROUND;

    private SimpleMolecule selectedMolecule;
    private SimpleMolecule nearestToSelectedMolecule;
    private EnergyMoleculeGraphic selectedMoleculeGraphic;
    private EnergyMoleculeGraphic nearestToSelectedMoleculeGraphic;

    private Insets curveAreaInsets = new Insets( 20, 30, 40, 10 );
    private Font axisFont = UIManager.getFont( "Label.font" );
    private PNode moleculePaneAxisNode;
    private PNode moleculeLayer;
    private SeparationIndicatorArrow separationIndicatorArrow;
    private EnergyCursor cursor;
    private MRModule module;
    private Dimension curveAreaSize;


    public MoleculeTrackerPane( MRModule module, Dimension size, Insets insets, Color background ){
        moleculePaneSize = size;
        curveAreaInsets = insets;
        this.module = module;
        curveAreaSize = new Dimension( (int)moleculePaneSize.getWidth() - insets.left - insets.right,
                                       (int)moleculePaneSize.getHeight() - insets.top - insets.bottom );
        addChild( createMoleculePane() );

        module.getMRModel().addSelectedMoleculeTrackerListener( new SelectedMoleculeListener() );
        update();        
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
        separationIndicatorArrow = new SeparationIndicatorArrow( Color.black );
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
            double edgeDist = module.getMRModel().getReaction().getDistanceToCollision( freeMolecule, boundMolecule.getParentComposite() );
            // In the middle of the reaction, the collision distance is underfined
            if( Double.isNaN( edgeDist ) ) {
//                edgeDist = model.getReaction().getDistanceToCollision( freeMolecule, boundMolecule.getParentComposite() );
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
//            if( bondGraphic != null ) {
//                bondGraphic.update( boundMolecule );
//            }

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
    // Inner classes
    //--------------------------------------------------------------------------------------------------

    private class SelectedMoleculeListener implements SelectedMoleculeTracker.Listener {
        public void moleculeBeingTrackedChanged( SimpleMolecule newTrackedMolecule,
                                                 SimpleMolecule prevTrackedMolecule ) {
            if( selectedMolecule != null ) {
                selectedMolecule.removeObserver( MoleculeTrackerPane.this );
            }
            selectedMolecule = newTrackedMolecule;
            if( selectedMoleculeGraphic != null ) {
                moleculeLayer.removeChild( selectedMoleculeGraphic );
            }

            if( newTrackedMolecule != null ) {
                selectedMoleculeGraphic = new EnergyMoleculeGraphic( newTrackedMolecule );
                moleculeLayer.addChild( selectedMoleculeGraphic );
                newTrackedMolecule.addObserver( MoleculeTrackerPane.this );
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
                nearestToSelectedMolecule.removeObserver( MoleculeTrackerPane.this );
            }
            nearestToSelectedMolecule = newClosestMolecule;
            if( nearestToSelectedMoleculeGraphic != null ) {
                moleculeLayer.removeChild( nearestToSelectedMoleculeGraphic );
            }
            nearestToSelectedMoleculeGraphic = new EnergyMoleculeGraphic( newClosestMolecule );
            moleculeLayer.addChild( nearestToSelectedMoleculeGraphic );

            newClosestMolecule.addObserver( MoleculeTrackerPane.this );
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
}
