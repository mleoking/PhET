/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.plots;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.BufferedPhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ZoomControlNode;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.colorado.phet.energyskatepark.EnergySkateParkModule;
import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;
import edu.colorado.phet.energyskatepark.common.SavedGraph;
import edu.colorado.phet.energyskatepark.model.Body;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkModel;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkLookAndFeel;
import edu.colorado.phet.energyskatepark.view.piccolo.EnergySkateParkLegend;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.pswing.PSwing;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Nov 6, 2005
 * Time: 8:05:15 PM
 */

public class EnergyPositionPlot extends BufferedPhetPCanvas {
    private JFreeChart chart;
    private XYSeriesCollection dataset;
    private EnergySkateParkModule module;

    private PImage image;
    private ChartRenderingInfo info = new ChartRenderingInfo();

    private PPath verticalBar = new PPath( new Line2D.Double( 0, 0, 0, 500 ) );
    private static final int COUNT_MOD = 10;

    private EnergySkateParkLegend legend;
    private int saveCount = 1;

    private EnergyType ke;
    private EnergyType pe;
    private EnergyType thermal;
    private EnergyType total;
    private JPanel southPanel;
    private ZoomControlNode verticalZoom;
    private ZoomControlNode horizontalZoom;
    private PSwing southPSwing;
    private PNode dataLayer = new PNode();

    public EnergyPositionPlot( EnergySkateParkModule module ) {
        setBackground( EnergySkateParkLookAndFeel.backgroundColor );
        this.module = module;
        ke = new EnergyType( EnergyPositionPlot.this.module, EnergySkateParkStrings.getString( "energy.kinetic" ), EnergyPositionPlot.this.module.getEnergyLookAndFeel().getKEColor(), this ) {
            public double getValue() {
                return getBody().getKineticEnergy();
            }
        };
        pe = new EnergyType( EnergyPositionPlot.this.module, EnergySkateParkStrings.getString( "energy.potential" ), EnergyPositionPlot.this.module.getEnergyLookAndFeel().getPEColor(), this ) {
            public double getValue() {
                return getBody().getPotentialEnergy();
            }
        };
        thermal = new EnergyType( EnergyPositionPlot.this.module, EnergySkateParkStrings.getString( "energy.thermal" ), EnergyPositionPlot.this.module.getEnergyLookAndFeel().getThermalEnergyColor(), this ) {
            public double getValue() {
                return getBody().getThermalEnergy();
            }
        };
        total = new EnergyType( module, EnergySkateParkStrings.getString( "energy.total" ), EnergyPositionPlot.this.module.getEnergyLookAndFeel().getTotalEnergyColor(), this ) {
            public double getValue() {
                return getBody().getTotalEnergy();
            }
        };

        module.getEnergySkateParkModel().addEnergyModelListener( new EnergySkateParkModel.EnergyModelListenerAdapter() {
            public void preStep() {
                update();
            }
        } );
        module.getTimeSeriesModel().addPlaybackTimeChangeListener( new TimeSeriesModel.PlaybackTimeListener() {
            public void timeChanged() {
                update();
            }
        } );
        dataset = new XYSeriesCollection( new XYSeries( new Integer( 0 ) ) );
        chart = createChart( new Range2D( -2, -7000 / 10.0, 17, 7000 ), dataset, EnergySkateParkStrings.getString( "plots.energy-vs-position" ) );
        setLayout( new BorderLayout() );

        southPanel = new JPanel();

        JButton clear = new JButton( EnergySkateParkStrings.getString( "time.clear" ) );
        clear.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                reset();
            }
        } );

        JButton copy = new JButton( EnergySkateParkStrings.getString( "plots.copy" ) );
        copy.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                copyChart();
            }
        } );

        JPanel showPanel = new JPanel( new GridLayout( 2, 2 ) );
        showPanel.add( ke.createCheckBox() );
        showPanel.add( pe.createCheckBox() );
        showPanel.add( thermal.createCheckBox() );
        showPanel.add( total.createCheckBox() );

        southPanel.add( copy );
        southPanel.add( clear );
        southPanel.add( showPanel );

        southPSwing = new PSwing( southPanel );
        addScreenChild( southPSwing );

        chart.setAntiAlias( true );

        image = new PImage( new BufferedImage( 10, 10, BufferedImage.TYPE_INT_RGB ) );
        addScreenChild( image );

        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                updateGraphics();
            }

            public void componentShown( ComponentEvent e ) {
                updateGraphics();
            }
        } );
        verticalBar.setStroke( new BasicStroke( 1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1, new float[]{10, 3}, 0 ) );
        verticalBar.setStrokePaint( Color.black );
        addScreenChild( verticalBar );
        legend = new EnergySkateParkLegend( module );
        legend.addTotalEnergyEntry();
        legend.setFont( new PhetFont( 12 ) );
        addScreenChild( dataLayer );
        addScreenChild( legend );

        verticalZoom = new VerticalZoomControl( chart.getXYPlot().getRangeAxis() ) {
            protected void updateZoom() {
                super.updateZoom();
                chartRangeChanged();
            }
        };

        horizontalZoom = new DefaultZoomControl( ZoomControlNode.HORIZONTAL, chart.getXYPlot().getDomainAxis() ) {
            protected void updateZoom() {
                double min = Math.min( 0, -getZoom() * 10 );
                double max = Math.max( 15, getZoom() * 10 + 15 );
                setZoomInEnabled( max - min > 15 );
                setZoomOutEnabled( max - min < 500 );
                getAxis().setRange( min, max );
                chartRangeChanged();
            }
        };
        addScreenChild( verticalZoom );
        addScreenChild( horizontalZoom );
        relayout();
        updateGraphics();
        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                relayout();
            }

            public void componentShown( ComponentEvent e ) {
                relayout();
            }
        } );
    }

    private void relayout() {
        horizontalZoom.setOffset( 0, getHeight() - horizontalZoom.getFullBounds().getHeight() );
        verticalZoom.setOffset( horizontalZoom.getFullBounds().getX(), horizontalZoom.getFullBounds().getY() - verticalZoom.getFullBounds().getHeight() );
        southPSwing.setOffset( getWidth() - southPSwing.getFullBounds().getWidth(), getHeight() - southPSwing.getFullBounds().getHeight() );
    }

    public JPanel getSouthPanel() {
        return southPanel;
    }

    private void copyChart() {
        getPhetRootNode().invalidateFullBounds();
        BufferedImage copy = new BufferedImage( getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB );
        horizontalZoom.setVisible( false );
        verticalZoom.setVisible( false );
        paintComponent( copy.createGraphics() );
        BufferedImage c2 = new BufferedImage( copy.getWidth( null ), copy.getHeight( null ) - southPanel.getHeight(), BufferedImage.TYPE_INT_RGB );//trim the south part.
        c2.createGraphics().drawImage( copy, new AffineTransform(), null );
        String energyVsPosition = EnergySkateParkStrings.getString( "plots.energy-vs-position-save" );
        SavedGraph savedGraph = new SavedGraph( module.getPhetFrame(), energyVsPosition + saveCount + ")", c2 );
        savedGraph.setVisible( true );
        saveCount++;

        horizontalZoom.setVisible( true );
        verticalZoom.setVisible( true );
    }

    private void updateGraphics() {
        if( getWidth() > 0 && getHeight() > 0 ) {
            image.setImage( chart.createBufferedImage( getWidth(), getChartHeight(), info ) );
        }
        reset();
        legend.setOffset( getWidth() - legend.getFullBounds().getWidth() - 5, 5 + toImageLocation( 0, chart.getXYPlot().getRangeAxis().getRange().getUpperBound() ).getY() );
    }

    private int getChartHeight() {
        return getHeight() - southPanel.getHeight();
    }

    private static JFreeChart createChart( Range2D range, XYDataset dataset, String title ) {
        JFreeChart chart = ChartFactory.createScatterPlot( title,
                                                           EnergySkateParkStrings.getString("plots.position.meters"), // x-axis label
                                                           EnergySkateParkStrings.getString("plots.energy-vs-time.energy"), // y-axis label
                                                           dataset, PlotOrientation.VERTICAL, false, true, false );
        chart.setBackgroundPaint( EnergySkateParkLookAndFeel.backgroundColor );

        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint( Color.white );
        plot.getDomainAxis().setRange( range.getMinX(), range.getMaxX() );
        plot.getRangeAxis().setRange( range.getMinY(), range.getMaxY() );
        plot.setRangeCrosshairVisible( true );
        return chart;
    }

    public void reset() {
        while( dataLayer.getChildrenCount() > 0 ) {
            FadeDot fadeDot = (FadeDot)dataLayer.getChild( 0 );
            removeFadeDot( fadeDot );
        }

    }

    private void removeFadeDot( FadeDot fadeDot ) {
        dataLayer.removeChild( fadeDot );
    }

    int count = 0;

    public void update() {
        count++;
        if( !isActive() ) {
            return;
        }
        if( module.getEnergySkateParkModel().getNumBodies() > 0 ) {
            Body body = module.getEnergySkateParkModel().getBody( 0 );
            double x = toImageLocation( body.getX(), 0 ).getX();
            verticalBar.setPathTo( new Line2D.Double( x, 0, x, getHeight() ) );

            addFadeDot( body.getX(), thermal );
            addFadeDot( body.getX(), pe );
            addFadeDot( body.getX(), total );
            addFadeDot( body.getX(), ke );
        }
        if( count % COUNT_MOD == 0 ) {
            fadeDots();
        }
    }

    private void fadeDots() {
        for( int i = 0; i < dataLayer.getChildrenCount(); i++ ) {
            FadeDot fadeDot = (FadeDot)dataLayer.getChild( i );
            fadeDot.fade();
            if( fadeDot.isFullyFaded() ) {
                dataLayer.removeChild( fadeDot );
                i--;
            }
        }
    }

    private boolean isActive() {
        return SwingUtilities.getWindowAncestor( this ) != null && SwingUtilities.getWindowAncestor( this ).isVisible();
    }

    private void addFadeDot( double x, EnergyType energyType ) {
        if( energyType.isVisible() && inBounds( x, energyType.getValue() ) ) {
            FadeDot path = new FadeDot( energyType, toImageLocation( x, energyType.getValue() ) );
            dataLayer.addChild( path );
        }
    }

    private boolean inBounds( double x, double y ) {
        return chart.getXYPlot().getDomainAxis().getRange().contains( offsetData( x, y ).getX() ) && chart.getXYPlot().getRangeAxis().getRange().contains( offsetData( x, y ).getY() );
    }

    static class FadeDot extends PPath {
        private Color origColor;
        private double age;
        private double dAge = 1.3 * COUNT_MOD;
        private Color fadeColor;
        private String name;

        public FadeDot( EnergyType energyType, Point2D loc ) {
            super( new Ellipse2D.Double( -3, -3, 6, 6 ), null );
            this.name = energyType.getName();
            setPaint( energyType.getColor() );
            setOffset( loc );
            this.origColor = energyType.getColor();
        }

        public String getName() {
            return name;
        }

        public void fade() {
            age += dAge;
            int fadeAlpha = (int)( 255 - age );
            if( fadeAlpha < 0 ) {
                fadeAlpha = 0;
            }
            Color fadeColor = new Color( origColor.getRed(), origColor.getGreen(), origColor.getBlue(),
                                         fadeAlpha );
            if( !fadeColor.equals( this.fadeColor ) ) {
                setPaint( fadeColor );
                this.fadeColor = fadeColor;
            }


        }

        public boolean isFullyFaded() {
            return fadeColor.getAlpha() <= 0;
        }
    }

    //todo: this accounts for an offset in the main chart
    private Point2D offsetData( double x, double y ) {
        return new Point2D.Double( x - 1, y );
    }

    public Point2D toImageLocation( double x, double y ) {
        x = offsetData( x, y ).getX();
        y = offsetData( x, y ).getY();
        Rectangle2D dataArea = info.getPlotInfo().getDataArea();
        if( dataArea == null ) {
            throw new RuntimeException( "Null data area" );
//            return new Point2D.Double( );
        }
        dataArea = new Rectangle2D.Double( dataArea.getX(), dataArea.getY(), dataArea.getWidth(), dataArea.getHeight() );
        double x1 = chart.getXYPlot().getDomainAxisForDataset( 0 ).valueToJava2D( x, dataArea, chart.getXYPlot().getDomainAxisEdge() );
        double y1 = chart.getXYPlot().getRangeAxisForDataset( 0 ).valueToJava2D( y, dataArea, chart.getXYPlot().getRangeAxisEdge() );
        return new Point2D.Double( x1, y1 );
    }

    public void chartRangeChanged() {
        updateGraphics();
    }

    static abstract class EnergyType {
        private EnergySkateParkModule module;
        String name;
        private Color color;
        private EnergyPositionPlot energyPositionPlot;
        boolean visible = true;

        public EnergyType( EnergySkateParkModule module, String name, Color color, EnergyPositionPlot energyPositionPlot ) {
            this.module = module;
            this.name = name;
            this.color = color;
            this.energyPositionPlot = energyPositionPlot;
        }

        public JCheckBox createCheckBox() {
            final JCheckBox checkBox = new JCheckBox( name, true );
            checkBox.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    visible = checkBox.isSelected();
                    energyPositionPlot.reset();
                }
            } );
            return checkBox;
        }

        public abstract double getValue();

        public Body getBody() {
            return module.getEnergySkateParkModel().getBody( 0 );
        }

        public EnergySkateParkModel getModel() {
            return module.getEnergySkateParkModel();
        }

        public Color getColor() {
            return color;
        }

        public String getName() {
            return name;
        }

        public boolean isVisible() {
            return visible;
        }
    }

}
