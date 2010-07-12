/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.greenhouse.model.AtomicBond;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;


/**
 * Class that represents an atomic bond in the view.
 * 
 * @author John Blanco
 */
public class AtomicBondNode extends PNode {
    
    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------
    
    // Constants that control the width of the bond representation with
    // with respect to the average atom radius.
    private static double BOND_WIDTH_PROPORTION_SINGLE = 0.3;
    private static double BOND_WIDTH_PROPORTION_DOUBLE = 0.1;
    private static double BOND_WIDTH_PROPORTION_TRIPLE = 0.05;
    
    private static Color BOND_COLOR = new Color(0, 200, 0);

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------

    private final AtomicBond atomicBond;
    private final ModelViewTransform2D mvt;
    private final double averageAtomRadius;
    
    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------
    
    public AtomicBondNode( AtomicBond atomicBond, ModelViewTransform2D mvt ){
        assert atomicBond.getBondCount() > 0 && atomicBond.getBondCount() <=3;  // Only single through triple bonds currently supported.
        this.atomicBond = atomicBond;
        this.mvt = mvt;
        
        // Listen to the bond for changes that may require an update of the
        // representation.
        atomicBond.addObserver( new SimpleObserver() {
            public void update() {
                updateRepresentation();
            }
        });
        
        // Calculate the width to use for the bond representation(s).
        averageAtomRadius = (atomicBond.getAtom1().getRadius() + atomicBond.getAtom2().getRadius()) / 2;
        
        // Create the initial representation.
        updateRepresentation();
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    private void updateRepresentation() {
        removeAllChildren();  // Clear out any previous representations.
        float bondWidth;
        switch (atomicBond.getBondCount()){
        case 1:
            // Single bond, so connect it from the center of one atom to the
            // center of the other.
            Point2D transformedPt1 = mvt.modelToViewDouble( atomicBond.getAtom1().getPosition() );
            Point2D transformedPt2 = mvt.modelToViewDouble( atomicBond.getAtom2().getPosition() );
            bondWidth = (float)(BOND_WIDTH_PROPORTION_SINGLE * averageAtomRadius);
            PPath bond = new PhetPPath(new BasicStroke(bondWidth), BOND_COLOR);
            bond.setPathTo( new Line2D.Double( transformedPt1, transformedPt2 ) );
            addChild(bond);
            break;
            
        case 2:
            // Double bond.
            // TODO: This is a temporary version to support horizontal molecules,
            // needs to be generalized.
            double transformedRadius = mvt.modelToViewDifferentialXDouble( Math.min( atomicBond.getAtom1().getRadius(),
                    atomicBond.getAtom2().getRadius() ) );
            Point2D p1 = mvt.modelToViewDouble( atomicBond.getAtom1().getPosition() );
            Point2D p2 = mvt.modelToViewDouble( atomicBond.getAtom2().getPosition() );
            bondWidth = (float)(BOND_WIDTH_PROPORTION_DOUBLE * averageAtomRadius);
            PPath bond1 = new PhetPPath(new BasicStroke(bondWidth), BOND_COLOR);
            bond1.setPathTo( new Line2D.Double( p1.getX(), p1.getY() + transformedRadius / 3, p2.getX(), p2.getY() + transformedRadius / 3) );
            PPath bond2 = new PhetPPath(new BasicStroke(bondWidth), BOND_COLOR);
            bond2.setPathTo( new Line2D.Double( p1.getX(), p1.getY() - transformedRadius / 3, p2.getX(), p2.getY() - transformedRadius / 3) );
            addChild(bond1);
            addChild(bond2);
            break;
            
        case 3:
            // TODO
            break;
            
        default:
            System.err.println(getClass().getName() + " - Error: Can't represent bond number, value = " + atomicBond.getBondCount());
            assert false;
            break;
        }
    }

    // ------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------
}
