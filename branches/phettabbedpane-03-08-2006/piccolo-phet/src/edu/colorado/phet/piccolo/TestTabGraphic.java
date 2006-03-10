package edu.colorado.phet.piccolo;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: reids
 * Date: Mar 9, 2006
 * Time: 5:21:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestTabGraphic {
    public static void main( String[] args ) {
        JFrame frame = new JFrame();
        PCanvas pCanvas = new PCanvas();
        PPath tabNode=new PPath( new Rectangle( 20,20));
        pCanvas.getLayer().addChild( tabNode );
        frame.setContentPane( pCanvas );
        frame.setSize( 200, 200 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        Color selectedTabColor = Color.blue;

//        float y1 = (float)tabNode.getFullBounds().getY() - 2;
//        float y2 = (float)( tabNode.getFullBounds().getY() + 6 );
        float y1=-2;
        float y2= 6;
        GradientPaint paint = new GradientPaint( 0, y1, selectedTabColor.brighter(), 0, y2, selectedTabColor );
        tabNode.setPaint( paint );

        frame.setVisible( true );
    }

}
