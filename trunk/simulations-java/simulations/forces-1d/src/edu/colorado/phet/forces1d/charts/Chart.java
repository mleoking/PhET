/**
 * Class: Chart
 * Package: edu.colorado.phet.chart
 * Author: Another Guy
 * Date: Sep 15, 2004
 */
package edu.colorado.phet.forces1d.charts;

//import edu.colorado.phet.common.view.graphics.Graphic;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.forces1d.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.forces1d.phetcommon.view.phetgraphics.HTMLGraphic;
import edu.colorado.phet.forces1d.phetcommon.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.forces1d.phetcommon.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.forces1d.phetcommon.view.util.GraphicsState;
import edu.colorado.phet.forces1d.phetcommon.view.util.RectangleUtils;

public class Chart extends PhetGraphic {
    private Component component;
    private Range2D range;
    private Rectangle viewBounds;

    private ArrayList dataSetGraphics = new ArrayList();
    private Axis xAxis;
    private Axis yAxis;
    private GridLineSet verticalGridlines;
    private GridLineSet horizonalGridlines;

    private TickMarkSet verticalTicks;
    private TickMarkSet horizontalTicks;

    private Paint background = Color.white;
    private Stroke outlineStroke = new BasicStroke( 1 );
    private Color outlineColor = Color.black;
    private ModelViewTransform2D transform;
    private ArrayList listeners = new ArrayList();
    private AbstractTitle title;

    public Chart( Component component, Range2D range, Rectangle viewBounds ) {
        super( component );
        this.component = component;
        this.range = range;
        this.viewBounds = viewBounds;
        this.xAxis = new Axis( this, AbstractGrid.HORIZONTAL );
        this.yAxis = new Axis( this, AbstractGrid.VERTICAL );
        this.verticalGridlines = new GridLineSet( this, AbstractGrid.VERTICAL );
        this.horizonalGridlines = new GridLineSet( this, AbstractGrid.HORIZONTAL );
        this.verticalTicks = new TickMarkSet( this, AbstractGrid.VERTICAL, 1, 2 );
        this.horizontalTicks = new TickMarkSet( this, AbstractGrid.HORIZONTAL, 1, 2 );
        this.transform = new ModelViewTransform2D( range.getBounds(), viewBounds );
    }

    public void setVerticalTitle( String title, Color color, Font font, int offsetX ) {
        setTitle( new VerticalTitle( this, title, font, color, offsetX ) );
    }

    public AbstractTitle getTitle() {
        return title;
    }

    private void setTitle( AbstractTitle title ) {
        this.title = title;
    }

    public void removeDataSetGraphic( DataSetGraphic dataSetGraphic ) {
        dataSetGraphics.remove( dataSetGraphic );
    }

    public boolean containsDataSetGraphic( DataSetGraphic dataSetGraphic ) {
        return dataSetGraphics.contains( dataSetGraphic );
    }

    public interface Listener {
        void transformChanged( Chart chart );
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public static abstract class AbstractTitle {
        private Chart chart;
        String title;
        Font font;
        Color color;

        public AbstractTitle( Chart chart, String title, Font font, Color color ) {
            this.chart = chart;
            this.title = title;
            this.font = font;
            this.color = color;
        }

        public abstract Rectangle getBounds();

        public abstract void paint( Graphics2D graphics2D );
    }

    public static class VerticalTitle extends AbstractTitle {
        private Chart chart;
        private int offsetX;
        private PhetGraphic graphic;

        public VerticalTitle( Chart chart, String title, Font font, Color color, int offsetX ) {
            super( chart, title, font, color );
            this.chart = chart;
            this.offsetX = offsetX;

            if ( title.startsWith( "<html>" ) ) {
                graphic = new HTMLGraphic( chart.getComponent(), font, title, color );
            }
            else {
                graphic = new PhetTextGraphic( chart.getComponent(), font, title, color, 0, 0 );
            }

        }

        public Rectangle getBounds() {
            Shape rect = graphic.getBounds();
//            Rectangle chartRect = chart.getViewBounds();
            Rectangle chartRect = chart.getViewBounds();
            Rectangle frame = chart.getVerticalTicks().getMajorTickTextBounds();
            if ( frame == null ) {
                frame = chart.getViewBounds();
            }
            AffineTransform at = new AffineTransform();
//            at.translate( chartRect.x - frame.width, chartRect.y + chartRect.height - chartRect.height / 2 + rect.getBounds().width / 2 );
            at.translate( chartRect.x - frame.width, chartRect.y + chartRect.height - chartRect.height / 2 + rect.getBounds().width / 2 );
            at.rotate( -Math.PI / 2 );
            Shape trf = at.createTransformedShape( rect );
            return trf.getBounds();
        }

        public void paint( Graphics2D g ) { //TODO this implementation is slow but correct.
            GraphicsState state = new GraphicsState( g );
            Rectangle rect = graphic.getBounds();
            Rectangle chartRect = chart.getViewBounds();
            Rectangle frame = chart.getVerticalTicks().getMajorTickTextBounds();
            if ( frame == null ) {
                frame = chart.getViewBounds();
            }
            frame.width = offsetX;//TODO this looks like a hack.
            g.translate( chartRect.x - frame.width, chartRect.y + chartRect.height - chartRect.height / 2 + rect.width / 2 );
            g.rotate( -Math.PI / 2 );
            graphic.paint( g );
            state.restoreGraphics();
        }
    }

    public static class TickMarkSet {
        private GridTicks majorTicks;
        private GridTicks minorTicks;

        public TickMarkSet( Chart chart, int orientation, double minorTickSpacing, double majorTickSpacing ) {
            minorTicks = new GridTicks( chart, orientation, new BasicStroke( 2 ), Color.black, minorTickSpacing );
            majorTicks = new GridTicks( chart, orientation, new BasicStroke( 2 ), Color.black, majorTickSpacing );
            minorTicks.setVisible( false );
        }

        public void paint( Graphics2D graphics2D ) {
            minorTicks.paint( graphics2D );
            majorTicks.paint( graphics2D );
        }

        public void setMajorTickSpacing( double majorTickSpacing ) {
            majorTicks.setSpacing( majorTickSpacing );
        }

        public void setMinorTickSpacing( double minorTickSpacing ) {
            minorTicks.setSpacing( minorTickSpacing );
        }

        public void setVisible( boolean visible ) {
            majorTicks.setVisible( visible );
            minorTicks.setVisible( visible );
        }

        public void setMajorGridlines( double[] lines ) {
            majorTicks.setGridlines( lines );
        }

        public void setMajorOffset( int dx, int dy ) {
            majorTicks.setOffset( dx, dy );
        }

        public Rectangle getMajorTickTextBounds() {
            return RectangleUtils.union( majorTicks.getTextBounds() );
        }
    }

    public TickMarkSet getVerticalTicks() {
        return verticalTicks;
    }

    public TickMarkSet getHorizontalTicks() {
        return horizontalTicks;
    }

    public static class GridTicks extends AbstractTicks {
        private int dx = 0;
        private int dy = 0;

        public GridTicks( Chart chart, int orientation, Stroke stroke, Color color, double tickSpacing ) {
            super( chart, orientation, stroke, color, tickSpacing );
        }

        public void setOffset( int dx, int dy ) {
            this.dx = dx;
            this.dy = dy;
        }

        public int getVerticalTickX() {
            Chart chart = getChart();
            return chart.transformX( chart.getRange().getMinX() ) + dx;
        }

        public int getHorizontalTickY() {
            Chart chart = getChart();
            return chart.transformY( chart.getRange().getMinY() ) + dy;
        }
    }

    public ModelViewTransform2D getModelViewTransform() {
        return transform;
    }

    public GridLineSet getVerticalGridlines() {
        return verticalGridlines;
    }

    public GridLineSet getHorizonalGridlines() {
        return horizonalGridlines;
    }

    public void setRange( Range2D range ) {
        this.range = range;
        transform.setModelBounds( range.getBounds() );
        fireTransformChanged();
        autorepaint();
    }

    public Range2D getDataRange() {
        if ( numDataSetGraphics() == 0 ) {
            return null;
        }
        Range2D range = ( (DataSetGraphic) dataSetGraphics.get( 0 ) ).getDataSet().getRange();
        for ( int i = 1; i < dataSetGraphics.size(); i++ ) {
            DataSetGraphic dataSetGraphic = (DataSetGraphic) dataSetGraphics.get( i );
            Range2D nextRange = dataSetGraphic.getDataSet().getRange();
            if ( range == null ) {
                range = nextRange;
            }
            else if ( nextRange != null ) {
                range = range.union( nextRange );
            }
        }
        return range;
    }

    private int numDataSetGraphics() {
        return dataSetGraphics.size();
    }


    public void setBackground( Paint background ) {
        this.background = background;
    }

    public void addDataSetGraphic( DataSetGraphic dataSetGraphic ) {
        if ( dataSetGraphic.getChart() == null || dataSetGraphic.getChart() == this ) {
            dataSetGraphic.setChart( this );
            dataSetGraphics.add( dataSetGraphic );
        }
        else {
            throw new RuntimeException( "DataSetGraphic was associated with the wrong Chart instance." );
        }
    }

    public Component getComponent() {
        return component;
    }

    /**
     * Takes a point in model coordinates and returns the corresponding view location.
     *
     * @param point
     * @return the Point in view coordinates.
     */
    public Point transform( Point2D point ) {
        if ( point == null ) {
            throw new RuntimeException( "Null point" );
        }

        return transform.modelToView( point );
    }

    public Point transform( double x, double y ) {
        return transform( new Point2D.Double( x, y ) );
    }

    public void paint( Graphics2D graphics2D ) {
        if ( isVisible() ) {

            super.saveGraphicsState( graphics2D );
            graphics2D.transform( getNetTransform() );

            //paint the background
            graphics2D.setPaint( background );
            graphics2D.fill( viewBounds );

            //paint the gridlines
            horizonalGridlines.paint( graphics2D );
            verticalGridlines.paint( graphics2D );

            horizontalTicks.paint( graphics2D );
            verticalTicks.paint( graphics2D );

            //paint the axes
            xAxis.paint( graphics2D );
            yAxis.paint( graphics2D );

            //paint the ornaments.

            //paint the datasets
            Shape clip = graphics2D.getClip();
            graphics2D.setClip( viewBounds );
            for ( int i = 0; i < dataSetGraphics.size(); i++ ) {
                DataSetGraphic dataSetGraphic = (DataSetGraphic) dataSetGraphics.get( i );
                dataSetGraphic.paint( graphics2D );
            }
            graphics2D.setClip( clip );
            graphics2D.setStroke( outlineStroke );
            graphics2D.setColor( outlineColor );
            graphics2D.draw( viewBounds );

            if ( title != null ) {
                title.paint( graphics2D );
            }
            super.restoreGraphicsState();
        }
    }

    public Axis getXAxis() {
        return xAxis;
    }

    public Axis getYAxis() {
        return yAxis;
    }

    public void setXAxis( Axis xAxis ) {
        this.xAxis = xAxis;
    }

    public void setYAxis( Axis yAxis ) {
        this.yAxis = yAxis;
    }

    public void setViewBounds( Rectangle viewBounds ) {
        Rectangle r = getVisibleBounds();
        this.viewBounds = viewBounds;
        transform.setViewBounds( viewBounds );
        fireTransformChanged();
        Rectangle r2 = getViewBounds();
        if ( r != null ) {
            component.repaint( r.x, r.y, r.width, r.height );
        }
        if ( r2 != null ) {
            component.repaint( r2.x, r2.y, r2.width, r2.height );
        }
//        repaint();
    }

    public Rectangle getVisibleBounds() {
        Rectangle r = getViewBounds();
        Rectangle vert = verticalTicks.getMajorTickTextBounds();
        Rectangle horiz = horizontalTicks.getMajorTickTextBounds();
        Rectangle union = RectangleUtils.union( new Rectangle[]{r, vert, horiz} );
        AffineTransform transform = getNetTransform();
        Rectangle visibleBounds = transform.createTransformedShape( union ).getBounds();
        return visibleBounds;
//        return union;
    }

    public void repaint() {
        Rectangle r = getViewBounds();
        Rectangle vert = verticalTicks.getMajorTickTextBounds();
        Rectangle horiz = horizontalTicks.getMajorTickTextBounds();
        Rectangle union = RectangleUtils.union( new Rectangle[]{r, vert, horiz} );
        component.repaint( union.x, union.y, union.width, union.height );
    }

    protected Rectangle determineBounds() {
        return getVisibleBounds();
    }

    private void fireTransformChanged() {
        for ( int i = 0; i < dataSetGraphics.size(); i++ ) {
            DataSetGraphic dataSetGraphic = (DataSetGraphic) dataSetGraphics.get( i );
            dataSetGraphic.transformChanged();
        }
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.transformChanged( this );
        }
    }


    public DataSetGraphic dataSetGraphicAt( int i ) {
        return (DataSetGraphic) dataSetGraphics.get( i );
    }

    public int transformY( double gridLineY ) {
        return transform( new Point2D.Double( 0, gridLineY ) ).y;
    }

    public int transformX( double gridLineX ) {
        return transform( new Point2D.Double( gridLineX, 0 ) ).x;
    }

    public Range2D getRange() {
        return range;
    }

    public Rectangle getViewBounds() {
        return viewBounds;
    }


}
