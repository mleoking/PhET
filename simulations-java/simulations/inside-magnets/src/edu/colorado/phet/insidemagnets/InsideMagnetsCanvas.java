// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.insidemagnets;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * @author Sam Reid
 */
public class InsideMagnetsCanvas extends PhetPCanvas {
    protected final ModelViewTransform2D transform;
    public static final PDimension STAGE_SIZE = new PDimension( 1008, 680 );
    private final PNode rootNode;

    public static Color BACKGROUND = new Color( 232, 242, 152 );
    public static Color FOREGROUND = Color.black;
    public static final Font CONTROL_FONT = new PhetFont( 18, true );

    public InsideMagnetsCanvas( final InsideMagnetsModule module ) {
        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, STAGE_SIZE ) );
        double modelWidth = module.getInsideMagnetsModel().getLatticeWidth();
        double modelHeight = modelWidth / STAGE_SIZE.getWidth() * STAGE_SIZE.getHeight();

        transform = new ModelViewTransform2D( new Rectangle2D.Double( 0, -2, modelWidth, modelHeight ), new Rectangle2D.Double( 0, 0, STAGE_SIZE.width * 0.8, STAGE_SIZE.height * 0.8 ), true );
        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );
        setBackground( Color.black );

        setBorder( null );

        addChild( new LatticeView( transform, module.getInsideMagnetsModel() ) );
        addChild( new NetMagnetizationField( transform, module.getInsideMagnetsModel() ) {{
            module.getShowMagnetizationProperty().addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( module.getShowMagnetizationProperty().get() );
                }
            } );
        }} );

        final PNode controlPanelNode = new PNode() {{ //swing border looks truncated in pswing, so draw our own in piccolo
            final PSwing controlPanelPSwing = new PSwing( new InsideMagnetsControlPanel( module ) );
            addChild( controlPanelPSwing );
            addChild( new PhetPPath( new RoundRectangle2D.Double( 0, 0, controlPanelPSwing.getFullBounds().getWidth(), controlPanelPSwing.getFullBounds().getHeight(), 10, 10 ), new BasicStroke( 1 ), Color.darkGray ) );
            setOffset( STAGE_SIZE.getWidth() - getFullBounds().getWidth() - 2, STAGE_SIZE.getHeight() / 2 - getFullBounds().getHeight() / 2 );
        }};
        addChild( controlPanelNode );

        //Reset all button
        addChild( new ButtonNode( "Reset all", (int) ( CONTROL_FONT.getSize() * 1.3 ), FOREGROUND, BACKGROUND ) {{
            setOffset( controlPanelNode.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, controlPanelNode.getFullBounds().getMaxY() + 20 );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.resetAll();
                }
            } );
        }} );
    }

    protected void addChild( PNode node ) {
        rootNode.addChild( node );
    }

    protected void removeChild( PNode node ) {
        rootNode.removeChild( node );
    }
}
