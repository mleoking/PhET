package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * Piccolo node that represents a stratum (i.e. a rock and sediment layer) in
 * the view.
 */
public class StratumNode2 extends PNode {
	
    public StratumNode2( Stratum2 stratum, Color color, ModelViewTransform2D mvt ) {

        PhetPPath path = new PhetPPath( mvt.createTransformedShape(stratum.getShape()), color, new BasicStroke( 2 ), 
        		Color.black );
        addChild( path );
    }

    static Random random = new Random();

    public static void main( String[] args ) {
        JFrame frame = new JFrame();
        PhetPCanvas contentPane = new PhetPCanvas();

        Stratum2 stratum;
        ArrayList<Stratum2> strata = new ArrayList<Stratum2>();
        ArrayList<Color> colors = new ArrayList<Color>();
        stratum = new Stratum2(new Stratum2.LayerLine(15), new Stratum2.LayerLine(20));
        strata.add( stratum );
        colors.add(Color.red);
        stratum = new Stratum2(new Stratum2.LayerLine(10), stratum.getTopLine());
        strata.add( stratum );
        colors.add(Color.green);
        stratum = new Stratum2(new Stratum2.LayerLine(5), stratum.getTopLine());
        strata.add( stratum );
        colors.add(Color.blue);
        stratum = new Stratum2(new Stratum2.LayerLine(0), stratum.getTopLine());
        strata.add( stratum );
        colors.add(Color.cyan);
        
        for ( int i = 0; i < strata.size(); i++ ){
        	StratumNode2 stratumNode = new StratumNode2(strata.get(i), colors.get(i), 
        			new ModelViewTransform2D(new Rectangle2D.Double(0, 0, 1, 1), 
        			new Rectangle2D.Double(0, 0, 10, 10)));
        	contentPane.addWorldChild( stratumNode );
        }

        frame.setContentPane( contentPane );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( 1200, 600 );
        frame.setVisible( true );
    }
}
