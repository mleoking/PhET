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

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Demonstration of usage and behavior of JFreeChartNode nested in a piccolo scene graph.
 * @author Sam Reid
 */
public class TestDynamicJFreeChartNodeTree extends TestDynamicJFreeChartNode {
    private PNode root;
    private boolean constructed = false;
    private PText text;

    public TestDynamicJFreeChartNodeTree() {
        getPhetPCanvas().removeScreenChild( getDynamicJFreeChartNode() );
        root = new PhetPPath( new Rectangle( 0, 0, 10, 10 ), Color.blue );

        root.addChild( getDynamicJFreeChartNode() );

        getPhetPCanvas().addScreenChild( root );
        getPhetPCanvas().addKeyListener( new KeyAdapter() {
            public void keyPressed( KeyEvent e ) {
                if( e.getKeyCode() == KeyEvent.VK_PAGE_UP ) {
                    root.scale( 1.1 );
                }
                else if( e.getKeyCode() == KeyEvent.VK_PAGE_DOWN ) {
                    root.scale( 1.0 / 1.1 );
                }
            }
        } );
        constructed = true;

        text = new PText( "Page Up/Down to scale" );
        getPhetPCanvas().addScreenChild( text );
    }

    protected void relayout() {
        super.relayout();
        if( constructed ) {
            root.setOffset( 50, 50 );
            text.setOffset( 0, super.getPSwing().getFullBounds().getMaxY() );
            getDynamicJFreeChartNode().setOffset( 10, 10 );
            getDynamicJFreeChartNode().setBounds( 0, 0, 500, 500 );
        }
    }

    public static void main( String[] args ) {
        new TestDynamicJFreeChartNodeTree().start();
    }
}
