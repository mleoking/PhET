package edu.colorado.phet.signalcircuit;

import edu.colorado.phet.signalcircuit.paint.Painter;
import edu.colorado.phet.signalcircuit.paint.animate.PointSource;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;

public class SwitchCover implements Painter, ActionListener, MouseListener, PointSource, SwitchListener {
    boolean cover;
    Switch cov;
    Switch uncov;
    JCheckBox source;
    Component repaint;

    public SwitchCover( Switch cov, Switch uncov, boolean cover, JCheckBox source, Component repaint ) {
        this.source = source;
        this.cov = cov;
        this.uncov = uncov;
        this.cover = cover;
        this.repaint = repaint;
    }

    public Point getPoint() {
        Point p = new Point();
        AffineTransform at = getSelectedSwitch().getPainter().getAffineTransform();
        at.transform( p, p );
        p.translate( 50, 50 );
        return p;
    }

    public void mouseClicked( MouseEvent me ) {
    }

    public void mouseReleased( MouseEvent me ) {
        if( cover ) {
            cov.mouseReleased( me );
        }
        else {
            uncov.mouseReleased( me );
        }
    }

    public void mouseEntered( MouseEvent me ) {
    }

    public void mouseExited( MouseEvent me ) {
    }

    public void mousePressed( MouseEvent me ) {
    }

    public void paint( Graphics2D g ) {
        if( cover ) {
            cov.paint( g );
        }
        else {
            uncov.paint( g );
        }
    }

    public Switch getSelectedSwitch() {
        if( cover ) {
            return cov;
        }
        else {
            return uncov;
        }
    }

    public Switch getUnselectedSwitch() {
        if( cover ) {
            return uncov;
        }
        else {
            return cov;
        }
    }

    public void setSwitchClosed( boolean c ) {
        getSelectedSwitch().setState( c );
        repaint.repaint();
    }

    public void actionPerformed( ActionEvent ae ) {
        boolean toDo = !source.isSelected();
        if( toDo == cover ) {
            return;
        }
        this.cover = toDo;
        getSelectedSwitch().setState( getUnselectedSwitch().getState() );
        repaint.repaint();
    }
}
