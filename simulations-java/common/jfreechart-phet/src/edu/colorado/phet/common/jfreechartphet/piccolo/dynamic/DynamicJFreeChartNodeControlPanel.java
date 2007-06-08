package edu.colorado.phet.common.jfreechartphet.piccolo.dynamic;

import edu.umd.cs.piccolo.util.PDebug;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * Author: Sam Reid
 * Jun 8, 2007, 1:58:55 AM
 */
public class DynamicJFreeChartNodeControlPanel extends JPanel {
    public DynamicJFreeChartNodeControlPanel( final DynamicJFreeChartNode dynamicJFreeChartNode ) {

        JPanel panel = this;
        ButtonGroup buttonGroup = new ButtonGroup();
        panel.add( createButton( "JFreeChart", buttonGroup, true, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dynamicJFreeChartNode.setJFreeChartSeries();
            }
        } ) );
        panel.add( createButton( "Piccolo", buttonGroup, false, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dynamicJFreeChartNode.setPiccoloSeries();
            }
        } ) );
//        panel.add( createButton( "Piccolo (incremental)", buttonGroup, false, new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                dynamicJFreeChartNode.setViewFactory( new DynamicJFreeChartNode.SeriesViewFactory() {
//                    public SeriesView createSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
//                        return new IncrementalPPathSeriesView( dynamicJFreeChartNode, seriesData );
//                    }
//                } );
//            }
//        } ) );
        panel.add( createButton( "Piccolo (incremental)", buttonGroup, false, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dynamicJFreeChartNode.setViewFactory( new DynamicJFreeChartNode.SeriesViewFactory() {
                    public SeriesView createSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
                        return new BoundedPPathSeriesView( dynamicJFreeChartNode, seriesData );
                    }
                } );
            }
        } ) );
        panel.add( createButton( "Piccolo (incremental + immediate)", buttonGroup, false, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dynamicJFreeChartNode.setViewFactory( new DynamicJFreeChartNode.SeriesViewFactory() {
                    public SeriesView createSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
                        return new ImmediateBoundedPPathSeriesView( dynamicJFreeChartNode, seriesData );
                    }
                } );
            }
        } ) );
        panel.add( createButton( "Direct", buttonGroup, false, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dynamicJFreeChartNode.setBufferedSeries();
            }
        } ) );
        panel.add( createButton( "Direct (immediate)", buttonGroup, false, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dynamicJFreeChartNode.setBufferedImmediateSeries();
            }
        } ) );
        final JCheckBox jCheckBox = new JCheckBox( "Buffered", dynamicJFreeChartNode.isBuffered() );
        jCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dynamicJFreeChartNode.setBuffered( jCheckBox.isSelected() );
            }
        } );
        dynamicJFreeChartNode.addBufferedPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                jCheckBox.setSelected( dynamicJFreeChartNode.isBuffered() );
            }
        } );
        final JCheckBox debug = new JCheckBox( "Show Regions", PDebug.debugBounds );
        debug.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                PDebug.debugRegionManagement = debug.isSelected();
            }
        } );
        panel.add( jCheckBox );
        panel.add( debug );
    }

    private JComponent createButton( String text, ButtonGroup buttonGroup, boolean enabled, ActionListener actionListener ) {
        JRadioButton jRadioButton = new JRadioButton( text, enabled );
        buttonGroup.add( jRadioButton );
        jRadioButton.addActionListener( actionListener );
        if( enabled ) {
            actionListener.actionPerformed( null );
        }
        return jRadioButton;
    }

}
