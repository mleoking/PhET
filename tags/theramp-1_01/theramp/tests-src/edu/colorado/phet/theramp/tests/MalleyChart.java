//package edu.colorado.phet.theramp.tests;
//
//import edu.colorado.phet.chart.Chart;
//import edu.colorado.phet.chart.DataSetGraphic;
//import edu.colorado.phet.chart.LabelTable;
//import edu.colorado.phet.chart.Range2D;
//import edu.colorado.phet.common.view.ApparatusPanel;
//import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
//import edu.colorado.phet.common.view.phetgraphics.PhetShadowTextGraphic;
//import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
//import edu.colorado.phet.common.view.phetgraphics.ShadowHTMLGraphic;
//
//import javax.swing.*;
//import java.awt.*;
//
//
///**
// * I'm having some problems with the charts package.
// * <p/>
// * I'm trying to set up a graph with the following properties:
// * - X axis range is 1 to 11
// * - Y axis range is -4/PI to +4/PI
// * - X axis has no ticks, labels or gridlines
// * - Y axis has major ticks (labeled) at 0.5 intervals
// * - Y axis has minor ticks (unlabeled) at 0.1 intervals
// * - no data set
// * <p/>
// * Attached is the code that I've written and a screenshot.
// * <p/>
// * Questions:
// * (1) How do I get the X axis ticks & labels (2,4,6,8,10) at the bottom
// * of the graph to disappear?
// * (2) How do I get the Y axis ticks & major labels to appear?
// * (3) Why is the horizontal line at Y=0 so bold/fat, and how can I change
// * it?
// */
//
//public class MalleyChart extends GraphicLayerSet {
//
//    // Chart parameters
//    private static final double CHART_LAYER = 1;
//    private static final Range2D CHART_RANGE = new Range2D( 1, -4 / Math.PI, 11, +4 / Math.PI );
//    private static final Dimension CHART_SIZE = new Dimension( 650, 175 );
//    public static final double Y_MAJOR_TICK_SPACING = 0.5;
//    private static final double Y_MINOR_TICK_SPACING = 0.1;
//    private static final Stroke Y_MAJOR_TICK_STROKE = new BasicStroke( 1f );
//    private static final Stroke Y_MINOR_TICK_STROKE = new BasicStroke( 1f );
//    private static final Font Y_MAJOR_TICK_FONT = new Font( "Lucida Sans", Font.BOLD, 12 );
//    private static final Color Y_MAJOR_GRIDLINE_COLOR = Color.BLACK;
//    private static final Color Y_MINOR_GRIDLINE_COLOR = new Color( 0, 0, 0, 30 );
//    private static final Stroke Y_MAJOR_GRIDLINE_STROKE = new BasicStroke( 1f );
//    private static final Stroke Y_MINOR_GRIDLINE_STROKE = new BasicStroke( 0.5f );
//
//    private Chart _chartGraphic;
//
//    public MalleyChart( Component component ) {
//
//        //.........
//
//        // Chart
//        {
//            _chartGraphic = new Chart( component, CHART_RANGE, CHART_SIZE );
//            addGraphic( _chartGraphic, CHART_LAYER );
//
//            _chartGraphic.setLocation( 0, -( CHART_SIZE.height / 2 ) );
//
//            // X axis has no ticks, labels or gridlines
//            _chartGraphic.getXAxis().setMajorTicksVisible( false );
//            _chartGraphic.getXAxis().setMinorTicksVisible( false );
//            _chartGraphic.getXAxis().setMajorTickLabelsVisible( false );
//            _chartGraphic.getXAxis().setMinorTickLabelsVisible( false );
//            _chartGraphic.getVerticalGridlines().setMinorGridlinesVisible( false );
//            _chartGraphic.getVerticalGridlines().setMajorGridlinesVisible( false );
//
//            // Y axis, major ticks with labels
//            _chartGraphic.getYAxis().setMajorTicksVisible( true );
//            _chartGraphic.getYAxis().setMajorTickLabelsVisible( true );
//            _chartGraphic.getYAxis().setMajorTickStroke( Y_MAJOR_TICK_STROKE );
//            _chartGraphic.getYAxis().setMajorTickFont( Y_MAJOR_TICK_FONT );
//
//            // Y axis, minor ticks with no labels
//            _chartGraphic.getYAxis().setMinorTicksVisible( true );
//            _chartGraphic.getYAxis().setMajorTickLabelsVisible( false );
//            _chartGraphic.getYAxis().setMinorTickStroke( Y_MINOR_TICK_STROKE );
//
//            // Y axis, major gridlines
//            _chartGraphic.getHorizonalGridlines().setMajorGridlinesVisible( true );
//            _chartGraphic.getHorizonalGridlines().setMajorTickSpacing( Y_MAJOR_TICK_SPACING );
//            _chartGraphic.getHorizonalGridlines().setMajorGridlinesColor( Y_MAJOR_GRIDLINE_COLOR );
//            _chartGraphic.getHorizonalGridlines().setMajorGridlinesStroke( Y_MAJOR_GRIDLINE_STROKE );
//
//            // Y axis, minor gridlines
//            _chartGraphic.getHorizonalGridlines().setMinorGridlinesVisible( true );
//            _chartGraphic.getHorizonalGridlines().setMinorTickSpacing( Y_MINOR_TICK_SPACING );
//            _chartGraphic.getHorizonalGridlines().setMinorGridlinesColor( Y_MINOR_GRIDLINE_COLOR );
//            _chartGraphic.getHorizonalGridlines().setMinorGridlinesStroke( Y_MINOR_GRIDLINE_STROKE );
//
//            //* (1) How do I get the X axis ticks & labels (2,4,6,8,10) at the bottom of the graph to disappear?
//            _chartGraphic.getHorizontalTicks().setVisible( false );
//
//            //* (2) How do I get the Y axis ticks & major labels to appear?
//            _chartGraphic.getVerticalTicks().setVisible( true );
//            _chartGraphic.getVerticalTicks().setMajorTickSpacing( Y_MAJOR_TICK_SPACING );
//
//            LabelTable verticalLabelTable = new LabelTable();
//            verticalLabelTable.put( 0, new PhetShadowTextGraphic( component, new Font( "Lucida Sans", Font.PLAIN, 14 ), "Zero (0.0)", Color.red, 1, 1, Color.black ) );
//            _chartGraphic.getVerticalTicks().setMajorLabels( verticalLabelTable );
//            _chartGraphic.getVerticalGridlines().setVisible( false );
//
//            //to disable the labeled grid lines, if you want.
//            _chartGraphic.getHorizonalGridlines().setMajorGridlinesVisible( false );
//
//            //* (3) Why is the horizontal line at Y=0 so bold/fat, and how can I change it?
//            _chartGraphic.getXAxis().setStroke( new BasicStroke( 1 ) );
//
//            _chartGraphic.getHorizontalTicks().setVisible( true );
//            _chartGraphic.getHorizontalTicks().setMajorTickSpacing( 1 );
//            LabelTable labelTable = new LabelTable();
//            labelTable.put( 3, new PhetTextGraphic( component, new Font( "Lucida Sans", Font.PLAIN, 28 ), "Three", Color.blue ) );
//            labelTable.put( 6, new PhetTextGraphic( component, new Font( "Lucida Sans", Font.PLAIN, 28 ), "L/6", Color.blue ) );
//            labelTable.put( 9, new ShadowHTMLGraphic( component, "<html>9<sup>TM</html>", new Font( "Lucida Sans", Font.BOLD, 16 ), Color.blue, 1, 1, Color.black ) );
//            _chartGraphic.getHorizontalTicks().setMajorLabels( labelTable );
//        }
//
//        //.......
//    }
//
//    //......
//    public static void main( String[] args ) {
//        ApparatusPanel apparatusPanel = new ApparatusPanel();
//
//        MalleyChart mc = new MalleyChart( apparatusPanel );
////        mc._chartGraphic.addDataSetGraphic( createSineCurve(mc._chartGraphic));
//        apparatusPanel.addGraphic( mc );
//        mc.setLocation( 50, 200 );
//
//        JFrame frame = new JFrame();
//        frame.setContentPane( apparatusPanel );
//        frame.setSize( 800, 800 );
//        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
//        frame.setVisible( true );
//    }
//
//    private static DataSetGraphic createSineCurve( ApparatusPanel apparatusPanel ) {
//        //11 data sets
//
//        //each 1000 data points.
////        DataSetGraphic dataSetGraphic=new LinePlot( apparatusPanel, );
//        return null;
//    }
//}
