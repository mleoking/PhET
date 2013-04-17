// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * Piccolo node that represents a stratum (i.e. a rock and sediment layer) in
 * the view.
 */
public class StratumNode extends PNode {

	private static final Stroke OUTLINE_STROKE = new BasicStroke( 2 );
	
	private ModelViewTransform2D _mvt;
	private Stratum _stratum;
	private PhetPPath _mainStratumNode;
	
    public StratumNode( Stratum stratum, Color color, ModelViewTransform2D mvt ) {

    	_mvt = mvt;
    	_stratum = stratum;
    	
        _mainStratumNode = new PhetPPath( mvt.createTransformedShape(_stratum.getShape()), color, OUTLINE_STROKE, 
        		Color.black );
        addChild( _mainStratumNode );
    }

    public static void main( String[] args ) {
        JFrame frame = new JFrame();
        PhetPCanvas contentPane = new PhetPCanvas();

        Stratum stratum;
        ArrayList<Stratum> strata = new ArrayList<Stratum>();
        ArrayList<Color> colors = new ArrayList<Color>();
        stratum = new Stratum(new Stratum.LayerLine(15), new Stratum.LayerLine(20));
        strata.add( stratum );
        colors.add(Color.red);
        stratum = new Stratum(new Stratum.LayerLine(10), stratum.getTopLine());
        strata.add( stratum );
        colors.add(Color.green);
        stratum = new Stratum(new Stratum.LayerLine(5), stratum.getTopLine());
        strata.add( stratum );
        colors.add(Color.blue);
        stratum = new Stratum(new Stratum.LayerLine(0), stratum.getTopLine());
        strata.add( stratum );
        colors.add(Color.cyan);
        
        for ( int i = 0; i < strata.size(); i++ ){
        	StratumNode stratumNode = new StratumNode(strata.get(i), colors.get(i), 
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
