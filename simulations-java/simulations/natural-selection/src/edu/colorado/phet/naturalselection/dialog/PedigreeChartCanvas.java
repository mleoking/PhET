package edu.colorado.phet.naturalselection.dialog;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.naturalselection.NaturalSelectionApplication;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.defaults.NaturalSelectionDefaults;
import edu.colorado.phet.naturalselection.model.Bunny;
import edu.colorado.phet.naturalselection.view.PedigreeNode;
import edu.umd.cs.piccolo.PNode;

/**
 * The piccolo canvas where the pedigree chart is drawn in.
 *
 * @author Jonathan Olson
 */
public class PedigreeChartCanvas extends PhetPCanvas {

    private PedigreeNode pedigreeNode;

    private static final double PEDIGREE_TOP_PADDING = 5.0;

    public PedigreeChartCanvas() {
        super.setWorldTransformStrategy( new ConstantTransformStrategy( new AffineTransform() ) );

        setPreferredSize( NaturalSelectionDefaults.GENERATION_CHART_SIZE );

        setBackground( NaturalSelectionApplication.accessibleColor( NaturalSelectionConstants.COLOR_GENERATION_CHART ) );

        PNode rootNode = new PNode();
        addWorldChild( rootNode );

        pedigreeNode = new PedigreeNode();

        rootNode.addChild( pedigreeNode );

        pedigreeNode.setOffset( new Point2D.Double( 0, PEDIGREE_TOP_PADDING ) );

        setCenterPoint( 0 );

    }

    public void setCenterPoint( double x ) {
        pedigreeNode.setOffset( new Point2D.Double( getWidth() / 2 - x, PEDIGREE_TOP_PADDING ) );
    }

    public void reset() {
        pedigreeNode.reset();
    }

    private Bunny lastDisplayedBunny = null;

    public void displayBunny( Bunny bunny ) {
        pedigreeNode.displayBunny( bunny );
        lastDisplayedBunny = bunny;
    }

    public Bunny getLastDisplayedBunny() {
        return lastDisplayedBunny;
    }

    @Override
    protected void updateLayout() {
        //System.out.println( "Width: " + getSize().getWidth() );
        //System.out.println( "Height: " + getSize().getHeight() );
    }

    public PedigreeNode getPedigreeNode() {
        return pedigreeNode;
    }
}
