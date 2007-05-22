package edu.colorado.phet.energyskatepark.view.bargraphs;

import edu.colorado.phet.common.phetcommon.math.ModelViewTransform1D;
import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.common.piccolophet.nodes.ShadowHTMLNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Author: Sam Reid
 * May 22, 2007, 4:23:59 AM
 */
public class BarGraph extends PNode {
    private ShadowHTMLNode titleNode;
    private PNode backLayer = new PNode();
    private PNode barLayer = new PNode();
    private ModelViewTransform1D transform1D;
    private double barChartHeight;
    private double barWidth;
    private double dw;
    private double sep;
    private int dx = 10;
    private int dy = -10;
    private double topY;

    private XAxis xAxis;
    private PPath background;
    private YAxis yAxis;
    private Variable[] variables;


    public BarGraph( String title, ModelViewTransform1D transform1D ) {
        this.transform1D = transform1D;

        topY = 0;
        barChartHeight = 400;
        barWidth = 20;
        dw = 7;

        sep = barWidth + dw;

        titleNode = new ShadowHTMLNode( title );
        titleNode.setColor( Color.black );
        titleNode.setShadowColor( Color.blue );
        titleNode.setFont( getTitleFont() );
        addChild( backLayer );
        addChild( barLayer );
    }

    protected Font getTitleFont() {
        return new Font( "Lucida Sans", Font.BOLD, 18 );
    }

    public void setBarChartHeight( double baselineY ) {
        this.barChartHeight = baselineY;
        xAxis.setBarChartHeight( baselineY );
        yAxis.setBarChartHeight( baselineY );
        for( int i = 0; i < barLayer.getChildrenCount(); i++ ) {
            BarGraphic2D barGraphic2D = (BarGraphic2D)barLayer.getChild( i );
            barGraphic2D.setBarHeight( baselineY );
        }
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
    }

    public ModelViewTransform1D getTransform1D() {
        return new ModelViewTransform1D( transform1D );
    }

    public void setTransform1D( ModelViewTransform1D modelViewTransform1D ) {
        transform1D.setTransform( modelViewTransform1D );
    }

    public double getMaxDisplayableEnergy() {//todo: trace dependencies on this (nondynamic ones)
        return Math.abs( transform1D.viewToModelDifferential( (int)( barChartHeight - topY ) ) );
    }

    private void addBarGraphic( BarGraphic2D barGraphic ) {
        barLayer.addChild( barGraphic );
    }

    protected void finishInit( Variable[] variables ) {
        this.variables = variables;
        double w = variables.length * ( sep + dw ) - sep;
        background = new PPath( new Rectangle2D.Double( 0, topY, 2 + w, 1000 ) );
        background.setPaint( null );
        background.setStroke( new BasicStroke() );
        background.setStrokePaint( null );
        backLayer.addChild( background );
        xAxis = new XAxis();
        backLayer.addChild( xAxis );

        yAxis = new YAxis();
        backLayer.addChild( yAxis );
        for( int i = 0; i < variables.length; i++ ) {
            final Variable variable = variables[i];
            int x = (int)( i * sep + dw );
            System.out.println( "i=" + i + ", x = " + x );
            final BarGraphic2D barGraphic = new BarGraphic2D( variable.getName(), transform1D,
                                                              variable.getValue(), x, (int)barWidth,
                                                              (int)barChartHeight, dx, dy, variable.getColor(), new Font( "Lucida Sans", Font.BOLD, 14 ) );
            addBarGraphic( barGraphic );
        }
        backLayer.addChild( titleNode );
        addPropertyChangeListener( PNode.PROPERTY_VISIBLE, new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                update();
            }
        } );
    }

    public void update() {
        if( getVisible() ) {
            for( int i = 0; i < barLayer.getChildrenCount(); i++ ) {
                BarGraphic2D barGraphic2D = (BarGraphic2D)barLayer.getChild( i );
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

        public void setBarChartHeight( double baselineY ) {
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
            catch( RuntimeException re ) {
                re.printStackTrace();
                return new GeneralPath();
            }
        }

        public void setBarChartHeight( double baselineY ) {
            path.setPathTo( createShape() );
        }
    }
}
