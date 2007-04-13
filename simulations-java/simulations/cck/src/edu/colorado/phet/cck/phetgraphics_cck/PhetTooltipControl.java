/** Sam Reid*/
package edu.colorado.phet.cck.phetgraphics_cck;

import edu.colorado.phet.common_cck.view.ApparatusPanel;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

/**
 * User: Sam Reid
 * Date: Jul 17, 2004
 * Time: 6:33:13 PM
 * Copyright (c) Jul 17, 2004 by Sam Reid
 */
public class PhetTooltipControl implements MouseInputListener {
    JComponent component;
    String text;
    PhetTooltipGraphic textGraphic;
    private MouseEvent e;

    Timer timer = null;

    public PhetTooltipControl( ApparatusPanel component, String text ) {
        timer = new Timer( 1000, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                tick();
            }
        } );
        this.component = component;
        this.text = text;
        textGraphic = new PhetTooltipGraphic( component, text );
        component.addGraphic( textGraphic, ApparatusPanel.LAYER_TOP );
        timer.setRepeats( false );
        tick();
    }

    private void tick() {
        if( e == null ) {
            textGraphic.setVisible( false );
        }
        else {
            textGraphic.setPosition( (int)( e.getX() - textGraphic.getBounds().getWidth() * 1.5 ), e.getY() );
            textGraphic.setVisible( true );
        }
    }

    public void mouseClicked( MouseEvent e ) {
    }

    public void mouseEntered( MouseEvent e ) {
        this.e = e;
        timer.restart();
    }

    public void mouseExited( MouseEvent e ) {
        timer.stop();
        textGraphic.setVisible( false );
    }

    public void mousePressed( MouseEvent e ) {
    }

    public void mouseReleased( MouseEvent e ) {
    }

    public void mouseDragged( MouseEvent e ) {
        this.e = e;
    }

    public void mouseMoved( MouseEvent e ) {
        this.e = e;
    }

}