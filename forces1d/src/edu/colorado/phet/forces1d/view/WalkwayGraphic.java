package edu.colorado.phet.forces1d.view;

import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.util.GraphicsState;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.forces1d.Force1DModule;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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
public class WalkwayGraphic extends PhetGraphic {
//    private int numTickMarks = 21;
    private int numTickMarks = 6;
    private double treex;
    private double housex;
    private Force1DModule module;
    private DecimalFormat format = new DecimalFormat( "##" );
    private Font font = new Font( "Lucida Sans", Font.BOLD, 19 );
//    private Font font = new Font( "Lucida Sans", Font.BOLD, 20 );
    private BufferedImage tree;
    private BufferedImage house;
    private Stroke borderStroke = new BasicStroke( 1 );
    private Function.LinearFunction transform;
    private int floorHeight = 4;
    private int height;
    private Rectangle floor;

    public WalkwayGraphic( ApparatusPanel panel, Force1DModule module, int numTickMarks, Function.LinearFunction transform ) throws IOException {
        this( panel, module, numTickMarks, -10, 10, transform );
    }

    public WalkwayGraphic( ApparatusPanel panel, Force1DModule module, int numTickMarks, double treex, double housex, Function.LinearFunction transform ) throws IOException {
        super( panel );
        this.module = module;
        this.numTickMarks = numTickMarks;
        this.numTickMarks = 11;
        this.treex = treex;
        this.housex = housex;
        tree = ImageLoader.loadBufferedImage( "images/tree.gif" );
        house = ImageLoader.loadBufferedImage( "images/cottage.gif" );
        this.transform = transform;
        update();
        panel.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                setBoundsDirty();
                repaint();
            }

            public void componentShown( ComponentEvent e ) {
                setBoundsDirty();
                repaint();
            }
        } );
        setBoundsDirty();
        repaint();
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
        height = 134;

        Rectangle rect = determineBounds();
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
        getPlatformY();
//        floor = new Rectangle( 0, height - 20, module.getApparatusPanel().getWidth(), floorHeight );
        Color root = new Color( 100, 100, 255 );
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

    protected Rectangle determineBounds() {
        int cw = getComponent() == null ? 0 : getComponent().getWidth();
        return new Rectangle( 0, 0, cw, height + 30 );
    }

    public int getPlatformY() {
//        floor = new Rectangle( 0, height - 20, getComponent().getWidth(), floorHeight );
        floor = new Rectangle( 0, height - 20, getComponent().getWidth(), floorHeight );
        return floor.y;
    }

    public void setBounds( int x, int y, int width, int height ) {
        floor = new Rectangle( x, y, width, height );
        update();
    }
}
