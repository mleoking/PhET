//// Copyright 2002-2011, University of Colorado
//package edu.colorado.phet.fractions.intro.matchinggame.view;
//
//import java.awt.Color;
//
//import org.jfree.chart.ChartFactory;
//import org.jfree.chart.JFreeChart;
//import org.jfree.chart.labels.PieToolTipGenerator;
//import org.jfree.chart.plot.PiePlot3D;
//import org.jfree.data.general.DefaultPieDataset;
//import org.jfree.data.general.PieDataset;
//
//import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
//import edu.colorado.phet.fractions.intro.intro.model.Fraction;
//import edu.umd.cs.piccolo.nodes.PImage;
//
///**
// * @author Sam Reid
// */
//public class Pie3DNode extends RepresentationNode {
//    public Pie3DNode( final ModelViewTransform transform, Fraction fraction ) {
//        super( transform, fraction );
//        final JFreeChart plot = ChartFactory.createPieChart3D( "", new DefaultPieDataset() {{
//            setValue( "a", 10 );
//            setValue( "b", 10 );
//            setValue( "c", 10 );
//        }}, false, false, false );
//        PiePlot3D p = (PiePlot3D) plot.getPlot();
//        p.setCircular( true );
//        p.setSectionPaint( "a", Color.yellow );
//        p.setSectionPaint( "b", Color.yellow );
//        p.setSectionPaint( "c", Color.yellow );
////        p.setStartAngle( 290 );
//        p.setToolTipGenerator( null );
//        addChild( new PImage( plot.createBufferedImage( 100, 100 ) ) {{
//        }} );
//    }
//}