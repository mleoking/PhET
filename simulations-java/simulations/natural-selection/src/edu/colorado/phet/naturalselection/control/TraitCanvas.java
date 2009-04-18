package edu.colorado.phet.naturalselection.control;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.view.*;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

public class TraitCanvas extends PhetPCanvas {

    private PNode rootNode;

    public static Dimension canvasSize = new Dimension( 460, 300 );
    private BigVanillaBunny bunny;
    public TraitControlNode tailTraitNode;
    public TraitControlNode teethTraitNode;
    public TraitControlNode colorTraitNode;

    public TraitCanvas() {
        super( canvasSize );

        rootNode = new PNode();
        addWorldChild( rootNode );

        bunny = new BigVanillaBunny();
        bunny.translate( 180, 150 );
        rootNode.addChild( bunny );

        PText traitsText = new PText( "Traits" );
        traitsText.setFont( new PhetFont( 16, true ) );
        traitsText.translate( 200 + ( 86 - traitsText.getWidth() ) / 2, 260 );
        rootNode.addChild( traitsText );

        tailTraitNode = new TailTraitNode();
        tailTraitNode.translate( 10, 190 );
        drawConnectingLine( tailTraitNode );
        rootNode.addChild( tailTraitNode );

        teethTraitNode = new TeethTraitNode();
        teethTraitNode.translate( 240, 40 );
        drawConnectingLine( teethTraitNode );
        rootNode.addChild( teethTraitNode );

        colorTraitNode = new ColorTraitNode();
        colorTraitNode.translate( 300, 190 );
        drawConnectingLine( colorTraitNode );
        rootNode.addChild( colorTraitNode );

        setPreferredSize( canvasSize );

        setBorder( null );

        setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
    }

    public void reset() {
        tailTraitNode.reset();
        teethTraitNode.reset();
        colorTraitNode.reset();
    }

    private void drawConnectingLine( TraitControlNode traitNode ) {
        PPath node = new PPath();

        node.setStroke( new BasicStroke( 1f ) );
        node.setStrokePaint( Color.BLACK );

        Point2D bunnySpot = traitNode.getBunnyLocation( bunny );
        Point2D nodeCenter = traitNode.getCenter();

        DoubleGeneralPath path = new DoubleGeneralPath();
        path.moveTo( bunnySpot.getX(), bunnySpot.getY() );
        path.lineTo( nodeCenter.getX(), nodeCenter.getY() );
        node.setPathTo( path.getGeneralPath() );

        rootNode.addChild( node );
    }

    public void updateLayout() {

    }
}
