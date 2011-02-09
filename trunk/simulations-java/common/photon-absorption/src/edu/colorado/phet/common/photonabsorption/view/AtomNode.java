// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.photonabsorption.view;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.RoundGradientPaint;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.photonabsorption.model.atoms.Atom;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;


/**
 * Class that represents an atom in the view.
 * 
 * @author John Blanco
 */
public class AtomNode extends PNode {
    
    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------

    private final Atom atom;
    private final ModelViewTransform2D mvt;
    private PhetPPath highlightNode;

    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------
    
    public AtomNode( Atom atom, ModelViewTransform2D mvt ){
        
        this.atom = atom;
        this.mvt = mvt;

        // Create a gradient for giving the sphere a 3D effect.
        double transformedRadius = mvt.modelToViewDifferentialXDouble( atom.getRadius() );
        Color lightColor = Color.WHITE;
        Color darkColor;
        if (atom.getRepresentationColor() != Color.WHITE){
            darkColor = ColorUtils.darkerColor( atom.getRepresentationColor(), 0.1 );
        }
        else{
            darkColor = Color.LIGHT_GRAY;
        }

        int highlightWidth=13;
        final RoundGradientPaint baseGradientPaint =
                new RoundGradientPaint( -transformedRadius / 2, -transformedRadius / 2, lightColor,new Point2D.Double( transformedRadius / 2, transformedRadius / 2 ),darkColor );
        final RoundGradientPaint haloGradientPaint =
                new RoundGradientPaint( 0, 0, Color.yellow,new Point2D.Double( transformedRadius + highlightWidth, transformedRadius + highlightWidth ),new Color( 0, 0, 0, 0 ) );
        highlightNode = new PhetPPath(new Ellipse2D.Double( -transformedRadius-highlightWidth, -transformedRadius-highlightWidth,
                transformedRadius * 2+highlightWidth*2, transformedRadius * 2 +highlightWidth*2),
                                                haloGradientPaint );
        PhetPPath atomNode = new PhetPPath(new Ellipse2D.Double( -transformedRadius, -transformedRadius,
                transformedRadius * 2, transformedRadius * 2 ), baseGradientPaint );
        addChild( highlightNode );
        addChild( atomNode );
        atom.addObserver( new SimpleObserver() {
            public void update() {
                updatePosition();
            }
        });
        updatePosition();
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    /**
     * 
     */
    private void updatePosition() {
        setOffset( mvt.modelToViewDouble( atom.getPositionRef() ) );
    }

    // ------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------

    public void setHighlighted( boolean highlighted ) {
        highlightNode.setVisible(highlighted);
    }
}
