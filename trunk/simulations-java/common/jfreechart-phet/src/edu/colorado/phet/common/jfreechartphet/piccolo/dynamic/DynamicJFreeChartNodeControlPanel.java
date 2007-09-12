/*
* The Physics Education Technology (PhET) project provides
* a suite of interactive educational simulations.
* Copyright (C) 2004-2006 University of Colorado.
*
* This program is free software; you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation; either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License along
* with this program; if not, write to the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
*
* For additional licensing options, please contact PhET at phethelp@colorado.edu
*/
package edu.colorado.phet.common.jfreechartphet.piccolo.dynamic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;

import edu.umd.cs.piccolo.util.PDebug;

/**
 * DISCLAIMER: This class is under development and not ready for general use.
 * Controls for setting and observing state for a DynamicJFreeChartNode
 *
 * @author Sam Reid
 */
public class DynamicJFreeChartNodeControlPanel extends JPanel {
    private DynamicJFreeChartNode dynamicJFreeChartNode;

    public DynamicJFreeChartNodeControlPanel( final DynamicJFreeChartNode dynamicJFreeChartNode ) {
        this.dynamicJFreeChartNode = dynamicJFreeChartNode;
        JPanel panel = this;
        ButtonGroup buttonGroup = new ButtonGroup();
        panel.add( createButton( "JFreeChart", buttonGroup, DynamicJFreeChartNode.RENDERER_JFREECHART ) );
        panel.add( createButton( "Piccolo", buttonGroup, DynamicJFreeChartNode.RENDERER_PICCOLO ) );
        panel.add( createButton( "Piccolo (incremental)", buttonGroup, DynamicJFreeChartNode.RENDERER_PICCOLO_INCREMENTAL ) );
        panel.add( createButton( "Piccolo (incremental + immediate)", buttonGroup, DynamicJFreeChartNode.RENDERER_PICCOLO_INCREMENTAL_IMMEDIATE ) );
        panel.add( createButton( "Direct", buttonGroup, DynamicJFreeChartNode.RENDERER_BUFFERED ) );
        panel.add( createButton( "Direct (immediate)", buttonGroup, DynamicJFreeChartNode.RENDERER_BUFFERED_IMMEDIATE ) );
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

    private JComponent createButton( String text, ButtonGroup buttonGroup, final SeriesViewFactory viewFactory ) {
        JRadioButton jRadioButton = new JRadioButton( text, dynamicJFreeChartNode.getViewFactory() == viewFactory );
        buttonGroup.add( jRadioButton );
        jRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dynamicJFreeChartNode.setViewFactory( viewFactory );
            }
        } );
        return jRadioButton;
    }

}
