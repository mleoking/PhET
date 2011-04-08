// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.barchart;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearSlider;
import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.ShadowHTMLNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * This class is used by Energy Skate Park (and possibly other sims).  See the main() below for a runnable example.
 * It is still under development and subject to change.
 *
 * @author Sam Reid
 */
public class BarChartNode extends PNode {
    private ShadowHTMLNode titleNode;
    private PNode frontLayer = new PNode();
    private PNode barLayer = new PNode();
    private double barChartHeight;
    private double barWidth;
    private double dw;
    private double sep;
    private double topY;

    private XAxis xAxis;
    private PPath background;
    private YAxis yAxis;
    private Variable[] variables;
    private double scale = 1.0;
    private PNode backLayer = new PNode();
    private VerticalShadowHTMLNode verticalLabelNode;

    public BarChartNode( String title, double scale, Paint backgroundColor ) {
        this( title, scale, backgroundColor, 400 );
    }

    public BarChartNode( String title, double scale, Paint backgroundColor, double barChartHeight ) {
        this.scale = scale;
        topY = 0;
        this.barChartHeight = barChartHeight;
        barWidth = 20;
        dw = 7;
        sep = barWidth + dw;
        titleNode = new ShadowHTMLNode( title );
        titleNode.setColor( Color.black );
        titleNode.setShadowColor( Color.blue );
        titleNode.setFont( getTitleFont() );
        PhetPPath titleBackground = new PhetPPath( titleNode.getFullBounds(), backgroundColor );
        frontLayer.addChild( titleBackground );
        verticalLabelNode = new VerticalShadowHTMLNode( new PhetFont(), "", Color.red, Color.black );
        verticalLabelNode.setOffset( -20, 150 );
        frontLayer.addChild( verticalLabelNode );

        addChild( backLayer );
        addChild( barLayer );
        addChild( frontLayer );
    }

    protected Font getTitleFont() {
        return new PhetFont( Font.BOLD, 18 );
    }

    public void setVerticalAxisLabelShadowVisible( boolean b ) {
        verticalLabelNode.setShadowVisible( b );
    }

    public static class Variable {
        private String name;
        private double value;
        private Color color;

        public Variable( String name, double value, Color color ) {
            this.name = name;
            this.value = value;
            this.color = color;
        }

        public String getName() {
            return name;
        }

        public double getValue() {
            return value;
        }

        public Color getColor() {
            return color;
        }

        public void setValue( double modelValue ) {
            this.value = modelValue;
        }
    }

    public double getBarScale() {
        return scale;
    }

    public void setBarScale( double scale ) {
        this.scale = scale;
        for ( int i = 0; i < barLayer.getChildrenCount(); i++ ) {
            BarNode barGraphic2D = (BarNode) barLayer.getChild( i );
            barGraphic2D.setBarScale( scale );
        }
        update();
    }

    private void addBarGraphic( BarNode barGraphic ) {
        barLayer.addChild( barGraphic );
    }

    /**
     * This part of the BarChartNode needs refactoring; should be able to create the chart, then add variables incrementally.
     *
     * @param variables
     */
    public void init( Variable[] variables ) {
        this.variables = variables;
        double w = variables.length * ( sep + dw ) - sep;
        background = new PPath( new Rectangle2D.Double( 0, topY, 2 + w, 1 ) );
        background.setPaint( null );
        background.setStroke( new BasicStroke() );
        background.setStrokePaint( null );
        frontLayer.addChild( background );
        xAxis = new XAxis();
        backLayer.addChild( xAxis );

        yAxis = new YAxis();
        frontLayer.addChild( yAxis );
        for ( int i = 0; i < variables.length; i++ ) {
            final Variable variable = variables[i];
            int x = (int) ( i * sep + dw );
            final BarNode barGraphic = new BarNode( variable.getName(), scale,
                                                    variable.getValue(), x, (int) barWidth,
                                                    (int) barChartHeight, variable.getColor(), new PhetFont( Font.BOLD, 14 ) );
            addBarGraphic( barGraphic );
        }
        frontLayer.addChild( titleNode );
        addPropertyChangeListener( PNode.PROPERTY_VISIBLE, new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                update();
            }
        } );
        update();
    }

    public void setBarChartHeight( double barChartHeight ) {
        this.barChartHeight = barChartHeight;
        yAxis.updateBarChartHeight();
    }

    public void update() {
        if ( getVisible() ) {
            for ( int i = 0; i < barLayer.getChildrenCount(); i++ ) {
                BarNode barGraphic2D = (BarNode) barLayer.getChild( i );
                barGraphic2D.setValue( variables[i].getValue() );
            }
        }
    }

    private class XAxis extends PNode {
        private PPath path;

        public XAxis() {
            path = new PPath( createLinePath() );
            addChild( path );
            path.setStrokePaint( new Color( 255, 150, 150 ) );
            path.setStroke( new BasicStroke( 3 ) );
        }

        private Line2D.Double createLinePath() {
            return new Line2D.Double( 0, barChartHeight, background.getFullBounds().getWidth(), barChartHeight );
        }

        public void setBarChartHeight() {
            path.setPathTo( createLinePath() );
        }
    }

    private class YAxis extends PNode {
        private PPath path;

        public YAxis() {
            GeneralPath shape = createShape();
            path = new PPath( shape );
            path.setPaint( Color.black );
            addChild( path );
        }

        private GeneralPath createShape() {
            Point2D origin = new Point2D.Double( 0, barChartHeight );
            Point2D dst = new Point2D.Double( 0, topY + 25 );
            try {
                return new Arrow( origin, dst, 8, 8, 3 ).getShape();
            }
            catch ( RuntimeException re ) {
                re.printStackTrace();
                return new GeneralPath();
            }
        }

        public void updateBarChartHeight() {
            path.setPathTo( createShape() );
        }
    }

    public void setVerticalAxisLabel( String text, Color color ) {
        verticalLabelNode.setText( text );
        verticalLabelNode.setForeground( color );
    }

    public static void main( String[] args ) {
        JFrame frame = new JFrame( "Test bar graph" );
        JPanel contentPanel = new JPanel( new BorderLayout() );
        JPanel controlPanel = new VerticalLayoutPanel();
        PhetPCanvas phetPCanvas = new PhetPCanvas();
        final BarChartNode barGraph = new BarChartNode( "bar graph", 400, Color.white );
        final Variable variable = new Variable( "var 2", 0.5, Color.green );
        barGraph.init( new Variable[] {
                new Variable( "var 1", 0, Color.blue ),
                variable,
                new Variable( "var 3", 0.9, Color.red )
        } );
        barGraph.setVerticalAxisLabel( "Vertical Axis", Color.blue );
        phetPCanvas.addScreenChild( barGraph );
        frame.setContentPane( contentPanel );
        contentPanel.add( phetPCanvas, BorderLayout.CENTER );

        final LinearSlider linearSlider = new LinearSlider( -1, 1 );
        linearSlider.setModelValue( 0.0 );
        linearSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                variable.setValue( linearSlider.getModelValue() );
                barGraph.update();
            }
        } );
        contentPanel.add( controlPanel, BorderLayout.SOUTH );

        final LinearSlider scale = new LinearSlider( 0, 200 );
        scale.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                barGraph.setBarScale( scale.getModelValue() );
            }
        } );

        controlPanel.add( linearSlider );
        controlPanel.add( scale );

        frame.setSize( 400, 600 );
        frame.setVisible( true );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }

}
