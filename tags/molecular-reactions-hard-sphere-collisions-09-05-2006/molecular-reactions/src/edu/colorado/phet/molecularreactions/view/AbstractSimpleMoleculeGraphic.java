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
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.piccolo.nodes.RegisterablePNode;

import java.util.HashMap;
import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * AbstractSimpleMoleculeGraphic
 * <p>
 * Base class used in the spatial and energy views for the graphics for simple molecules
 * <p>
 * The radius of the molecule is not used for the radius of the disk used in the graphic. Rather,
 * the disk is smaller, so that the bonds between molecules can be shown as a line. This may need
 * to change so that collisions look more natural on the screen.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
abstract public class AbstractSimpleMoleculeGraphic extends PNode implements SimpleObserver, SimpleMolecule.Listener {

    //--------------------------------------------------------------------------------------------------
    // Class fields and methods
    //--------------------------------------------------------------------------------------------------

    private static double BOND_OFFSET = 0;
//    private static double BOND_OFFSET = 2;
    private static HashMap moleculeTypeToColor = new HashMap();
    private static Color moleculeAColor = new Color( 240, 240, 0 );
    private static Color moleculeBColor = new Color( 0, 200, 0 );
    private static Color moleculeCColor = new Color( 160, 0, 160 );
    private static Color defaultMoleculeColor = new Color( 100, 100, 100 );

    static {
        AbstractSimpleMoleculeGraphic.moleculeTypeToColor.put( MoleculeA.class, AbstractSimpleMoleculeGraphic.moleculeAColor );
        AbstractSimpleMoleculeGraphic.moleculeTypeToColor.put( MoleculeB.class, AbstractSimpleMoleculeGraphic.moleculeBColor );
        AbstractSimpleMoleculeGraphic.moleculeTypeToColor.put( MoleculeC.class, AbstractSimpleMoleculeGraphic.moleculeCColor );
    }

    private static Color getColor( SimpleMolecule molecule ) {
        Color color = (Color)AbstractSimpleMoleculeGraphic.moleculeTypeToColor.get( molecule.getClass() );
        if( color == null ) {
            color = AbstractSimpleMoleculeGraphic.defaultMoleculeColor;
        }
        return color;
    }

    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------

    private static Stroke defaultStroke = new BasicStroke( 1 );
    private static Stroke selectedStroke = new BasicStroke( 2 );
    private static Stroke nearestToSelectedStroke = new BasicStroke( 2 );

    private static Paint defaultStrokePaint = Color.black;
    private static Paint selectedStrokePaint = Color.red;
    private static Paint nearestToSelectedStrokePaint = new Color( 255, 0, 255 );
    private SimpleMolecule molecule;
    private PPath pPath;

    /**
     *
     * @param molecule
     */
    public AbstractSimpleMoleculeGraphic( SimpleMolecule molecule ) {
        this.molecule = molecule;
        molecule.addObserver( this );
        molecule.addListener( this );

        double radius = molecule.getRadius() - BOND_OFFSET;
        Shape s = new Ellipse2D.Double( -radius,
                                        -radius,
                                        radius * 2,
                                        radius * 2 );
        pPath = new PPath( s, AbstractSimpleMoleculeGraphic.defaultStroke );
        pPath.setPaint( AbstractSimpleMoleculeGraphic.getColor( molecule ) );
        pPath.setStrokePaint( AbstractSimpleMoleculeGraphic.defaultStrokePaint );
        addChild( pPath );
        update();
    }

    public SimpleMolecule getMolecule() {
        return molecule;
    }
    //--------------------------------------------------------------------------------------------------
    // Abstract methods
    //--------------------------------------------------------------------------------------------------

    abstract public void update();

    //--------------------------------------------------------------------------------------------------
    // Implementation of SimpleMolecule.Listener
    //--------------------------------------------------------------------------------------------------

    public void selectionStatusChanged( SimpleMolecule molecule ) {
        if( molecule.getSelectionStatus() == Selectable.SELECTED ) {
            pPath.setStroke( AbstractSimpleMoleculeGraphic.selectedStroke );
            pPath.setStrokePaint( AbstractSimpleMoleculeGraphic.selectedStrokePaint );
        }
        else if( molecule.getSelectionStatus() == Selectable.NEAREST_TO_SELECTED ) {
            pPath.setStroke( AbstractSimpleMoleculeGraphic.nearestToSelectedStroke );
            pPath.setStrokePaint( AbstractSimpleMoleculeGraphic.nearestToSelectedStrokePaint );
        }
        else {
            pPath.setStroke( AbstractSimpleMoleculeGraphic.defaultStroke );
            pPath.setStrokePaint( AbstractSimpleMoleculeGraphic.defaultStrokePaint );
        }
    }
}
