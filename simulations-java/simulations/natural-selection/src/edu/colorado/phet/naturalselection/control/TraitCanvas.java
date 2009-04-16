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

    public static Dimension canvasSize = new Dimension( 530, 300 );
    private BigVanillaBunny bunny;
    private MutationControlNode earsMutationNode;
    private MutationControlNode tailMutationNode;
    private MutationControlNode eyesMutationNode;
    private MutationControlNode teethMutationNode;
    private MutationControlNode colorMutationNode;

    public TraitCanvas() {
        super( canvasSize );

        rootNode = new PNode();
        addWorldChild( rootNode );

        bunny = new BigVanillaBunny();
        bunny.translate( 200, 150 );
        rootNode.addChild( bunny );

        PText traitsText = new PText( "Traits" );
        traitsText.setFont( new PhetFont( 16, true ) );
        traitsText.translate( 200 + ( 86 - traitsText.getWidth() ) / 2, 260 );
        rootNode.addChild( traitsText );

        earsMutationNode = new EarsMutationNode();
        earsMutationNode.translate( 30, 60 );
        drawConnectingLine( earsMutationNode );
        rootNode.addChild( earsMutationNode );

        tailMutationNode = new TailMutationNode();
        tailMutationNode.translate( 10, 210 );
        drawConnectingLine( tailMutationNode );
        rootNode.addChild( tailMutationNode );

        eyesMutationNode = new EyesMutationNode();
        eyesMutationNode.translate( 215, 40 );
        drawConnectingLine( eyesMutationNode );
        rootNode.addChild( eyesMutationNode );

        teethMutationNode = new TeethMutationNode();
        teethMutationNode.translate( 375, 85 );
        drawConnectingLine( teethMutationNode );
        rootNode.addChild( teethMutationNode );

        colorMutationNode = new ColorMutationNode();
        colorMutationNode.translate( 330, 210 );
        drawConnectingLine( colorMutationNode );
        rootNode.addChild( colorMutationNode );

        setPreferredSize( canvasSize );

        setBorder( null );

        setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
    }

    public void reset() {
        earsMutationNode.reset();
        tailMutationNode.reset();
        eyesMutationNode.reset();
        teethMutationNode.reset();
        colorMutationNode.reset();
    }

    private void drawConnectingLine( MutationControlNode mutationNode ) {
        PPath node = new PPath();

        node.setStroke( new BasicStroke( 1f ) );
        node.setStrokePaint( Color.BLACK );

        Point2D bunnySpot = mutationNode.getBunnyLocation( bunny );
        Point2D nodeCenter = mutationNode.getCenter();

        DoubleGeneralPath path = new DoubleGeneralPath();
        path.moveTo( bunnySpot.getX(), bunnySpot.getY() );
        path.lineTo( nodeCenter.getX(), nodeCenter.getY() );
        node.setPathTo( path.getGeneralPath() );

        rootNode.addChild( node );
    }

    public void updateLayout() {

    }
}
