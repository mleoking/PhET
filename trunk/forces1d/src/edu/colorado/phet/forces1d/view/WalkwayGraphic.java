package edu.colorado.phet.forces1d.view;

import edu.colorado.phet.common.math.LinearTransform1d;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.util.GraphicsState;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.forces1d.Forces1DModule;

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
public class WalkwayGraphic implements Graphic {
    private int numTickMarks = 21;
    private double treex;
    private double housex;
    private Forces1DModule module;
    private DecimalFormat format = new DecimalFormat( "##" );
    private Font font = new Font( "Lucida Sans", Font.PLAIN, 16 );
    private BufferedImage tree;
    private BufferedImage house;
    private Stroke borderStroke = new BasicStroke( 1 );
    private LinearTransform1d transform;
    private int floorHeight = 4;

    public WalkwayGraphic( Forces1DModule module, int numTickMarks, LinearTransform1d transform ) throws IOException {
        this( module, numTickMarks, -10, 10, transform );
    }

    public WalkwayGraphic( Forces1DModule module, int numTickMarks, double treex, double housex, LinearTransform1d transform ) throws IOException {
        this.module = module;
        this.numTickMarks = numTickMarks;
        this.treex = treex;
        this.housex = housex;
        tree = ImageLoader.loadBufferedImage( "images/tree.gif" );
        house = ImageLoader.loadBufferedImage( "images/cottage.gif" );
        this.transform = transform;
        update();
    }

    private void update() {
    }

    public void setTreeX( double treex ) {
        this.treex = treex;
    }

    public void setHouseX( double housex ) {
        this.housex = housex;
    }

    public void paint( Graphics2D graphics2D ) {
        GraphicsState graphicsState = new GraphicsState( graphics2D );
        graphics2D.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        double modelRange = transform.getInputRange();
        double modelDX = modelRange / ( numTickMarks - 1 );
        graphics2D.setColor( Color.black );
        graphics2D.setFont( font );
        int height = 134;

//        graphics2D.setColor( Color.blue );

        Rectangle rect = new Rectangle( 0, 0, module.getApparatusPanel().getWidth(), height + 30 );
//        Color lightBLue=new Color( 200,200,255);
        Color lightBLue = new Color( 150, 120, 255 );
        graphics2D.setPaint( new GradientPaint( rect.x, rect.y, lightBLue, rect.x, rect.y + rect.height, Color.white ) );
        graphics2D.fill( rect );
        graphics2D.setColor( Color.blue );
        graphics2D.setStroke( borderStroke );
        graphics2D.drawLine( 0, rect.y + rect.height, rect.width, rect.y + rect.height );
        graphics2D.setColor( Color.black );

        for( int i = 0; i < numTickMarks; i++ ) {
            double modelx = transform.getMinInput() + i * modelDX;
            int viewx = (int)transform.evaluate( modelx );

            Point dst = new Point( viewx, height - 20 );
            graphics2D.drawLine( viewx, height, dst.x, dst.y );

            String str = format.format( modelx );
            if( str.equals( "0" ) ) {
                str = "0 meters";
            }
            Rectangle2D bounds = font.getStringBounds( str, graphics2D.getFontRenderContext() );
            graphics2D.drawString( str, viewx - (int)( bounds.getWidth() / 2 ), height + (int)bounds.getHeight() );
        }

//        Line2D.Double floor=new Line2D.Double( );
        Rectangle floor = new Rectangle( 0, height - 20, module.getApparatusPanel().getWidth(), floorHeight );
//        graphics2D.setColor( Color.blue );
        Color root=new Color( 100,100,255);
        graphics2D.setPaint( new GradientPaint( floor.x, floor.y, root, floor.x, floor.y + floor.height, Color.white ) );
        graphics2D.fill( floor );
        //Tree at -10.
        int treex = (int)( transform.evaluate( this.treex ) - tree.getWidth() / 2 );
        int treey = 10;
        int housex = (int)( transform.evaluate( this.housex ) - house.getWidth() / 2 );
        int housey = 10;
        graphics2D.drawImage( tree, treex, treey, null );
        graphics2D.drawImage( house, housex, housey, null );
        graphicsState.restoreGraphics();
    }
}
