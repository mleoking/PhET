package edu.colorado.phet.rotation.tests;

import edu.colorado.phet.piccolo.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;

public class TestDynamicJFreeChartNodeTree extends TestDynamicJFreeChartNode {
    private PNode root;
    private boolean constructed = false;

    public TestDynamicJFreeChartNodeTree() {
        getPhetPCanvas().removeScreenChild( getDynamicJFreeChartNode() );
        root = new PhetPPath( new Rectangle( 0, 0, 10, 10 ), Color.blue );

        root.addChild( getDynamicJFreeChartNode() );

        getPhetPCanvas().addScreenChild( root );
        constructed = true;
    }

    protected void relayout() {
        super.relayout();
        if( constructed ) {
            root.setOffset( 50, 50 );
            getDynamicJFreeChartNode().setOffset( 10, 10 );
            getDynamicJFreeChartNode().setBounds( 0, 0, 500, 500 );
        }
    }

    public static void main( String[] args ) {
        new TestDynamicJFreeChartNodeTree().start();
    }
}
