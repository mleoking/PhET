package edu.colorado.phet.movingman;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.movingman.common.GraphicsState;
import edu.colorado.phet.movingman.common.math.RangeToRange;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Jul 5, 2003
 * Time: 5:34:02 PM
 * To change this template use Options | File Templates.
 */
public class WalkWayGraphic implements Graphic {
    int numTickMarks = 21;
    private double treex;
    private double housex;
    MovingManModule module;
    DecimalFormat format = new DecimalFormat( "##" );
    Font font = new Font( "dialog", 0, 20 );
    private BufferedImage tree;
    private BufferedImage house;
    private Stroke borderStroke = new BasicStroke( 1 );

//    Color textColor=Color.balc;
    public WalkWayGraphic( MovingManModule module, int numTickMarks ) throws IOException {
        this( module, numTickMarks, -10, 10 );
    }

    public WalkWayGraphic( MovingManModule module, int numTickMarks, double treex, double housex ) throws IOException {
        this.module = module;
        this.numTickMarks = numTickMarks;
        this.treex = treex;
        this.housex = housex;
        tree = ImageLoader.loadBufferedImage( "images/tree.gif" );
        house = ImageLoader.loadBufferedImage( "images/cottage.gif" );
    }

    public void setTreeX( double treex ) {
        this.treex = treex;
    }

    public void setHouseX( double housex ) {
        this.housex = housex;
    }

    GraphicsState state = new GraphicsState();

    public void paint( Graphics2D graphics2D ) {
        state.saveState( graphics2D );
//        graphics2D.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        RangeToRange transform = module.getManPositionTransform();
        double modelRange = transform.getInputWidth();
        double modelDX = modelRange / ( numTickMarks - 1 );
        graphics2D.setColor( Color.black );
        graphics2D.setFont( font );
        int height = 134;
//        if (height <= -10)
//        PhetVector vanishingPoint=new PhetVector();

//        Rectangle2D.Double rect=new Rectangle2D.Double(transform.evaluate(-10),transform);
        graphics2D.setColor( module.getPurple() );
        Rectangle rect = new Rectangle( 0, 0, module.getApparatusPanel().getWidth(), height + 30 );
//        graphics2D.fillRect( 0, 0, module.getApparatusPanel().getWidth(), height + 30 );
        graphics2D.fill( rect );
        graphics2D.setColor( Color.blue );
        graphics2D.setStroke( borderStroke );
//        graphics2D.draw( rect );
        graphics2D.drawLine( 0, rect.y + rect.height, rect.width, rect.y + rect.height );
        graphics2D.setColor( Color.black );

        for( int i = 0; i < numTickMarks; i++ ) {
            double modelx = transform.getLowInputPoint() + i * modelDX;
            int viewx = (int)transform.evaluate( modelx );
//            O.d("modelx="+modelx+", viewx="+viewx);

            Point dst = new Point( viewx, height - 20 );
            graphics2D.drawLine( viewx, height, dst.x, dst.y );

            String str = format.format( modelx );
            if( str.equals( "0" ) ) {
                str = "0 meters";
            }
            Rectangle2D bounds = font.getStringBounds( str, graphics2D.getFontRenderContext() );
            graphics2D.drawString( str, viewx - (int)( bounds.getWidth() / 2 ), height + (int)bounds.getHeight() );
        }
        //Tree at -10.
        int treex = (int)( transform.evaluate( this.treex ) - tree.getWidth() / 2 );
        int treey = 10;
        int housex = (int)( transform.evaluate( this.housex ) - house.getWidth() / 2 );
        int housey = 10;
        graphics2D.drawImage( tree, treex, treey, null );
        graphics2D.drawImage( house, housex, housey, null );
        state.restoreState( graphics2D );
    }
}
