/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.HTMLGraphic;
import edu.colorado.phet.common.view.util.RectangleUtils;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Feb 13, 2005
 * Time: 10:55:16 AM
 * Copyright (c) Feb 13, 2005 by Sam Reid
 */

public class SquareButton3D extends CompositePhetGraphic {
    private HTMLGraphic htmlGraphic;
    private Rectangle3DGraphic rectangle3DGraphic;
    private int zOrig = 7;
    private int z = zOrig;
    private Timer animationTimer;
    private ActionListener timer;
    private ArrayList states = new ArrayList();
    private ArrayList actionListeners = new ArrayList();
    private static int id = 0;

    public SquareButton3D( ApparatusPanel ap, Font font, String s ) {
//        super( new RepaintStrategy.FalseComponent( new RepaintStrategy.RepaintUnion( ap ) ) );
        super( ap );

        htmlGraphic = new HTMLGraphic( ap, font, s, Color.black );
        Rectangle bounds = new Rectangle( htmlGraphic.getBounds() );
        bounds = RectangleUtils.expand( bounds, 2, 2 );
        Stroke borderStroke = new BasicStroke( 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
//        Stroke borderStroke = new BasicStroke( 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
        final Color normalFaceColor = new Color( 200, 210, 255 );
        final Color highlightFaceColor = new Color( 225, 235, 255 );
        rectangle3DGraphic = new Rectangle3DGraphic( getComponent(), bounds, normalFaceColor, borderStroke, Color.red, Color.green, zOrig, zOrig, Color.black );
        addGraphic( rectangle3DGraphic );
        addGraphic( htmlGraphic );

        addMouseInputListener( new MouseInputAdapter() {
            // implements java.awt.event.MouseListener
            public void mousePressed( MouseEvent e ) {
                states.add( new PressIn() );
                animationTimer.start();

                ActionEvent event = new ActionEvent( SquareButton3D.this, id++, "cmd" );
                for( int i = 0; i < actionListeners.size(); i++ ) {
                    ActionListener actionListener = (ActionListener)actionListeners.get( i );
                    actionListener.actionPerformed( event );
                }
            }

            // implements java.awt.event.MouseListener
            public void mouseReleased( MouseEvent e ) {
                states.add( new PressOut() );
                animationTimer.start();
            }

            // implements java.awt.event.MouseListener
            public void mouseEntered( MouseEvent e ) {
                rectangle3DGraphic.setFacePaint( highlightFaceColor );
            }

            // implements java.awt.event.MouseListener
            public void mouseExited( MouseEvent e ) {
                rectangle3DGraphic.setFacePaint( normalFaceColor );
            }
        } );
        setCursorHand();
        timer = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                step();
            }
        };
        animationTimer = new Timer( 30, timer );
    }

    public void addActionListener( ActionListener actionListener ) {
        actionListeners.add( actionListener );
    }

    interface Step {
        boolean step();
    }

    class PressOut implements Step {

        public boolean step() {
            setZ( z + 1 );
            if( z >= zOrig ) {
                return true;
            }
            else {
                return false;
            }
        }
    }

    class PressIn implements Step {

        public boolean step() {
            setZ( z - 1 );
            if( z <= 0 ) {
                return true;
            }
            else {
                return false;
            }
        }
    }

    private void step() {
        if( states.size() == 0 ) {
            animationTimer.stop();
        }
        Step step = (Step)states.get( 0 );
        boolean fin = step.step();
        if( fin ) {
            states.remove( 0 );
        }

        if( states.size() == 0 ) {
            animationTimer.stop();
        }
    }

    public void setZ( int z ) {
        this.z = z;
        htmlGraphic.setLocation( zOrig - z, zOrig - z );
        Rectangle bounds = new Rectangle( htmlGraphic.getLocalBounds() );
        bounds = RectangleUtils.expand( bounds, 2, 2 );
        rectangle3DGraphic.setRectangle( bounds );
        rectangle3DGraphic.setDx( z );
        rectangle3DGraphic.setDy( z );
    }
}
