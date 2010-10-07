package edu.colorado.phet.buildanatom.view;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.buildanatom.model.Atom;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.colorado.phet.buildanatom.BuildAnAtomConstants;

/**
 * Shows a scale with a numeric readout of the atom weight.  Origin is the top left of the scale body (not the platform).
 *
 * @author Sam Reid
 */
public class MassIndicatorNode extends PNode {

    public MassIndicatorNode( final Atom atom ) {
        Rectangle2D.Double baseShape = new Rectangle2D.Double( 0, 0, 100, 30 );
        double stemWidth = 4;
        double stemHeight = 7;
        double platformHeight = 3;
        
        final PhetPPath base = new PhetPPath( baseShape, BuildAnAtomConstants.READOUT_BACKGROUND_COLOR, new BasicStroke( 1 ), Color.black );
        addChild( base );

        Rectangle2D.Double stemShape = new Rectangle2D.Double( baseShape.getCenterX() - stemWidth / 2, baseShape.getMinY() - stemHeight, stemWidth, stemHeight );
        PhetPPath stem = new PhetPPath( stemShape, BuildAnAtomConstants.READOUT_BACKGROUND_COLOR, new BasicStroke( 1 ), Color.black );
        addChild( stem );

        Rectangle.Double platform = new Rectangle2D.Double( baseShape.getX(), stemShape.getY() - platformHeight, baseShape.getWidth(), platformHeight );
        PhetPPath platformNode = new PhetPPath( platform, BuildAnAtomConstants.READOUT_BACKGROUND_COLOR, new BasicStroke( 1 ), Color.black );
        addChild( platformNode );

        final PText readoutPText = new PText() {{
            setFont( BuildAnAtomConstants.READOUT_FONT );
            setTextPaint( Color.red );
        }};
        addChild( readoutPText );

        SimpleObserver updateText = new SimpleObserver() {
            public void update() {
                readoutPText.setText( atom.getAtomicMassNumber() + "" );
                readoutPText.setOffset( base.getFullBounds().getCenterX() - readoutPText.getFullBounds().getWidth() / 2, base.getFullBounds().getCenterY() - readoutPText.getFullBounds().getHeight() / 2 );
            }
        };
        atom.addObserver( updateText );
        updateText.update();

        //from 9/30/2010 meeting
        //will students think the atom on the scale is an electron?
        //use small icon of orbits/cloud instead of cloud

        //TODO: copied from BuildAnAtomCanvas, should be factored out into something like ElectronShellNode
        PNode atomNode = new PNode();
        //Make it small enough so it looks to scale, but also so we don't have to indicate atomic substructure
        Stroke stroke = new BasicStroke( 1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 1.5f, 1.5f }, 0 );
        ModelViewTransform2D mvt = new ModelViewTransform2D( new Rectangle2D.Double( 0, 0, 10, 10 ), new Rectangle2D.Double( 0, 0, 1, 1 ), false );
        for ( Double shellRadius : atom.getElectronShellRadii() ) {
            Shape electronShellShape = mvt.createTransformedShape( new Ellipse2D.Double(
                    -shellRadius,
                    -shellRadius,
                    shellRadius * 2,
                    shellRadius * 2 ) );
            PNode electronShellNode = new PhetPPath( electronShellShape, stroke, Color.BLUE );
            atomNode.addChild( electronShellNode );
        }
        atomNode.setOffset( platform.getCenterX(), -atomNode.getFullBounds().getHeight() //set the atom on the scale
                                                   - 1 );//looks weird if shell sits on the scale, so have it float a little
        addChild( atomNode );
    }
}
