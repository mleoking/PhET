//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.tests;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import javax.swing.*;

import edu.colorado.phet.buildamolecule.control.CollectionAreaNode;
import edu.colorado.phet.buildamolecule.control.CollectionPanel;
import edu.colorado.phet.buildamolecule.control.GeneralLayoutNode;
import edu.colorado.phet.buildamolecule.control.GeneralLayoutNode.HorizontalAlignMethod.Align;
import edu.colorado.phet.buildamolecule.model.*;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.chemistry.model.Element.*;

/**
 * A few tests for GeneralLayoutNode
 */
public class GeneralLayoutTest {
    public static void main( String[] args ) {
//        PDebug.debugBounds = true;
        JFrame frame = new JFrame( "GeneralLayoutTest" );
        frame.setContentPane( new PhetPCanvas() {{
            Dimension size = new Dimension( 480, 900 );
            setPreferredSize( size );

            setWorldTransformStrategy( new CenteredStage( this, size ) );

            GeneralLayoutNode layoutNode = new GeneralLayoutNode() {{
                setOffset( 50, 10 );
                LayoutMethod method = new CompositeLayoutMethod( new VerticalLayoutMethod(), new HorizontalAlignMethod( Align.Centered ) );
                addChild( new LayoutElement( PhetPPath.createRectangle( 0, 0, 100, 50 ), method ) );
                addChild( new LayoutElement( PhetPPath.createRectangle( 0, 0, 50, 30 ), method ) );
                addChild( new LayoutElement( PhetPPath.createRectangle( 0, 0, 100, 30 ), method, 20, 0, 10, 0 ) );
                addChild( new LayoutElement( PhetPPath.createRectangle( 0, 0, 70, 50 ), method ) );
                addChild( new LayoutElement( PhetPPath.createRectangle( 0, 0, 70, 50 ),
                                             new CompositeLayoutMethod( new VerticalLayoutMethod(), new HorizontalAlignMethod( Align.Left ) ) {
                                                 @Override public void layout( LayoutElement element, int index, LayoutElement previousElement, LayoutProperties layoutProperties ) {
                                                     System.out.println( "maxWidth: " + layoutProperties.maxWidth );
                                                     super.layout( element, index, previousElement, layoutProperties );
                                                     System.out.println( "bounds: " + element.node.getFullBounds() );
                                                     System.out.println( "placement: " + element.node.getOffset() );
                                                 }
                                             } ) );
                addChild( new LayoutElement( PhetPPath.createRectangle( 0, 0, 70, 50 ),
                                             new CompositeLayoutMethod( new VerticalLayoutMethod(), new HorizontalAlignMethod( Align.Right ) ) ) );

                final IClock clock = new ConstantDtClock( 30 );
                final LayoutBounds bounds = new LayoutBounds( false, CollectionPanel.getCollectionPanelModelWidth( false ) );
                addChild( new CollectionAreaNode( new KitCollection() {{
                    addKit( new Kit( bounds,
                                     new Bucket( new PDimension( 400, 200 ), clock, H, 2 ),
                                     new Bucket( new PDimension( 450, 200 ), clock, O, 2 )
                    ) );

                    addKit( new Kit( bounds,
                                     new Bucket( new PDimension( 500, 200 ), clock, C, 2 ),
                                     new Bucket( new PDimension( 600, 200 ), clock, O, 4 ),
                                     new Bucket( new PDimension( 500, 200 ), clock, N, 2 )
                    ) );
                    addKit( new Kit( bounds,
                                     new Bucket( new PDimension( 600, 200 ), clock, H, 12 ),
                                     new Bucket( new PDimension( 600, 200 ), clock, O, 4 ),
                                     new Bucket( new PDimension( 500, 200 ), clock, N, 2 )
                    ) );
                    addCollectionBox( new CollectionBox( MoleculeList.CO2, 2 ) );
                    addCollectionBox( new CollectionBox( MoleculeList.O2, 2 ) );
                    addCollectionBox( new CollectionBox( MoleculeList.getMoleculeByName( "Trifluoroborane" ), 4 ) );
                    addCollectionBox( new CollectionBox( MoleculeList.NH3, 2 ) );
                }}, false, new Function1<PNode, Rectangle2D>() {
                    public Rectangle2D apply( PNode pNode ) {
                        return pNode.getFullBounds();
                    }
                }
                ), method );
            }};
//            addWorldChild( new PhetPPath( layoutNode.getFullBounds() ) {{
//                setStrokePaint( Color.BLUE );
//                setPaint( Color.RED.darker() );
//            }} );
            addWorldChild( layoutNode );
        }} );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.pack();
        frame.setVisible( true );
    }
}
