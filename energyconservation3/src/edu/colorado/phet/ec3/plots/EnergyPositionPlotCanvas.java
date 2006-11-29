/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.plots;

import edu.colorado.phet.ec3.EC3Legend;
import edu.colorado.phet.ec3.EnergySkateParkModule;
import edu.colorado.phet.ec3.EnergySkateParkStrings;
import edu.colorado.phet.ec3.common.LucidaSansFont;
import edu.colorado.phet.ec3.common.SavedGraph;
import edu.colorado.phet.ec3.model.Body;
import edu.colorado.phet.ec3.model.EnergySkateParkModel;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
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
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Nov 6, 2005
 * Time: 8:05:15 PM
 * Copyright (c) Nov 6, 2005 by Sam Reid
 */

public class EnergyPositionPlotCanvas extends PhetPCanvas {
    private JFreeChart chart;
    private ArrayList peDots = new ArrayList();
    private XYSeriesCollection dataset;
    private EnergySkateParkModule module;

    private PImage image;
    private ChartRenderingInfo info = new ChartRenderingInfo();

    private PPath verticalBar = new PPath( new Line2D.Double( 0, 0, 0, 500 ) );
    private static final int COUNT_MOD = 10;

    private EC3Legend legend;
    private int saveCount = 1;

    private EnergyType ke;
    private EnergyType pe;
    private EnergyType thermal;
    private EnergyType total;
    private JPanel southPanel;

    public void chartRangeChanged() {
        updateGraphics();
    }

    static abstract class EnergyType {
        private EnergySkateParkModule module;
        String name;
        private Color color;
        private EnergyPositionPlotCanvas energyPositionPlotCanvas;
        boolean visible = true;

        public EnergyType( EnergySkateParkModule module, String name, Color color, EnergyPositionPlotCanvas energyPositionPlotCanvas ) {
            this.module = module;
            this.name = name;
            this.color = color;
            this.energyPositionPlotCanvas = energyPositionPlotCanvas;
        }

        public JCheckBox createCheckBox() {
            final JCheckBox checkBox = new JCheckBox( name, true );
            checkBox.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    visible = checkBox.isSelected();
                    energyPositionPlotCanvas.reset();
                }
            } );
            return checkBox;
        }

        public abstract double getValue();

        public Body getBody() {
            return module.getEnergyConservationModel().bodyAt( 0 );
        }

        public EnergySkateParkModel getModel() {
            return module.getEnergyConservationModel();
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

    public EnergyPositionPlotCanvas( EnergySkateParkModule ec3Module ) {
        super( new Dimension( 100, 100 ) );
        this.module = ec3Module;
        ke = new EnergyType( module, EnergySkateParkStrings.getString( "kinetic" ), module.getEnergyLookAndFeel().getKEColor(), this ) {
            public double getValue() {
                return getBody().getKineticEnergy();
            }
        };
        pe = new EnergyType( module, EnergySkateParkStrings.getString( "potential" ), module.getEnergyLookAndFeel().getPEColor(), this ) {
            public double getValue() {
                return getBody().getPotentialEnergy();
            }
        };
        thermal = new EnergyType( module, EnergySkateParkStrings.getString( "thermal" ), module.getEnergyLookAndFeel().getThermalEnergyColor(), this ) {
            public double getValue() {
                return getBody().getThermalEnergy();
            }
        };
        total = new EnergyType( ec3Module, EnergySkateParkStrings.getString( "total" ), module.getEnergyLookAndFeel().getTotalEnergyColor(), this ) {
            public double getValue() {
                return getBody().getTotalEnergy();
            }
        };


        ec3Module.getEnergyConservationModel().addEnergyModelListener( new EnergySkateParkModel.EnergyModelListenerAdapter() {
            public void preStep( double dt ) {
                update();
            }
        } );
        dataset = createDataset();
        chart = createChart( new Range2D( -2, -7000 / 10.0, 17, 7000 ), dataset, EnergySkateParkStrings.getString( "energy.vs.position" ) );
        setLayout( new BorderLayout() );

        southPanel = new JPanel();

        JButton clear = new JButton( EnergySkateParkStrings.getString( "clear" ) );
        clear.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                reset();
            }
        } );


        JButton copy = new JButton( EnergySkateParkStrings.getString( "copy" ) );
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

        add( southPanel, BorderLayout.SOUTH );
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
        legend = new EC3Legend( ec3Module );
        legend.addTotalEnergyEntry();
        legend.setFont( new LucidaSansFont( 12 ) );
        addScreenChild( legend );

        ZoomPanel zoomPanel = new ZoomPanel( chart, this );
        ZoomPanelPSwing pSwing = new ZoomPanelPSwing( this, zoomPanel );
        addScreenChild( pSwing );

        updateGraphics();
    }

    public JPanel getSouthPanel() {
        return southPanel;
    }

    private void copyChart() {
//        Image copy = super.getLayer().toImage( image.getImage().getWidth( null ), image.getImage().getHeight( null ), Color.white );
        Image copy = super.getLayer().toImage();
        BufferedImage c2 = new BufferedImage( copy.getWidth( null ), copy.getHeight( null ) - southPanel.getHeight(), BufferedImage.TYPE_INT_RGB );//trim the south part.
        c2.createGraphics().drawImage( copy, new AffineTransform(), null );
        String energyVsPosition = EnergySkateParkStrings.getString( "energy.vs.position.save" );
        SavedGraph savedGraph = new SavedGraph( energyVsPosition + saveCount + ")", c2 );
        savedGraph.setVisible( true );
        saveCount++;
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
                                                           "Position", // x-axis label
                                                           "Energy", // y-axis label
                                                           dataset, PlotOrientation.VERTICAL, false, true, false );
        chart.setBackgroundPaint( new Color( 240, 220, 210 ) );

        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint( Color.white );
        plot.getDomainAxis().setRange( range.getMinX(), range.getMaxX() );
        plot.getRangeAxis().setRange( range.getMinY(), range.getMaxY() );
        plot.setRangeCrosshairVisible( true );
        return chart;
    }

    private static XYSeriesCollection createDataset() {
        XYSeries xySeries = new XYSeries( new Integer( 0 ) );
        return new XYSeriesCollection( xySeries );
    }

    public void reset() {
        while( peDots.size() > 0 ) {
            FadeDot fadeDot = (FadeDot)peDots.get( 0 );
            removeFadeDot( fadeDot );
        }
    }

    private void removeFadeDot( FadeDot fadeDot ) {
        peDots.remove( fadeDot );
        getPhetRootNode().removeChild( fadeDot );
    }

    int count = 0;

    private void update() {
//        updateGraphics();
        count++;
        if( !isActive() ) {
            return;
        }
        if( module.getEnergyConservationModel().numBodies() > 0 ) {
            Body body = module.getEnergyConservationModel().bodyAt( 0 );
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
        for( int i = 0; i < peDots.size(); i++ ) {
            FadeDot fadeDot = (FadeDot)peDots.get( i );
            fadeDot.fade();
            if( fadeDot.isFullyFaded() ) {
                peDots.remove( i );
                getPhetRootNode().removeChild( fadeDot );
                i--;
            }
        }
    }

    private boolean isActive() {
        return SwingUtilities.getWindowAncestor( this ) != null && SwingUtilities.getWindowAncestor( this ).isVisible();
    }

    private void addFadeDot( double x, EnergyType energyType ) {
        if( energyType.isVisible() ) {
            FadeDot path = new FadeDot( energyType, toImageLocation( x, energyType.getValue() ) );
            addScreenChild( path );
            peDots.add( path );
        }
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

    public Point2D toImageLocation( double x, double y ) {
        x--;//todo: this accounts for an offset in the main chart
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
}
