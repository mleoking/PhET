import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;


/**
 * Applet to illustrate the idea of lattice vectors.
 * Two draggable vectors are provided, and a &quot;grid&quot; of bases is
 * drawn according to these base vectors.
 * <p/>
 * The basis may be chosen from a list of simple geometric figures; to add to
 * these, override or edit {@link #makeBases(Lattice2D) makeBases()} to add
 * the basis to the list, and override or edit
 * {@link #makeLabels() makeLabels()} to supply a label for the basis.  The
 * position in the array of the new label should match that of the new basis.
 */
public class LatticeApplet extends BaseApplet {
    /*
         Info panel shape:
         __________
         L____L____|
         L____L____|
         L____L____|
         L____L____|
         L_________|
         L_________|
             ...
     */


    public void init() {
        super.init();

        setBackground( Color.white );
        setLayout( new BorderLayout() );
        lattice = new Lattice2D();
        reciprocal = new Lattice2D();
        reciprocal.setBasis( reciprocal.new PolygonBasis( 4, 1, Math.PI / 4 ) );
        updateReciprocal();
        vectorMoveListener = new VectorMoveListener();

        labels = makeLabels();
        bases = makeBases( lattice );

        Panel p = new Panel();
        Panel infoPanel = new Panel();
        Panel basisPanel = new Panel();


        p.setLayout( new GridLayout( 6 + bases.length, 1 ) );
        //p.setLayout(new FlowLayout());
        infoPanel.setLayout( new GridLayout( 4, 2 ) );
        basisPanel.setLayout( new FlowLayout() );

        /*infoPanel.add(new Label("|v1|:  "));
          infoPanel.add(mod1 = new Label());
          infoPanel.add(new Label("|v2|:  "));
          infoPanel.add(mod2 = new Label());
          infoPanel.add(new Label("ratio:  "));
          infoPanel.add(ratio = new Label());
          infoPanel.add(new Label("angle:  "));
          infoPanel.add(angle = new Label());*/

        mod1 = new Label();
        mod2 = new Label();
        ratio = new Label();
        angle = new Label();

        //	Get labels to normal size first
        updateLabels();

        addWithLabel( p, mod1, "|v1| =" );
        addWithLabel( p, mod2, "|v2| =" );
        addWithLabel( p, ratio, "ratio =" );
        addWithLabel( p, angle, "angle =" );

        addWithLabel( p, new Label(), "" );
        addWithLabel( p, new Label(), "Basis:" );


        cbg = new CheckboxGroup();
        boxes = new Checkbox[labels.length];
        for( int i = 0; i < boxes.length; i++ ) {
            boxes[i] = new Checkbox( labels[i], cbg, false );
            new MozillaWorkaround( boxes[i], cbg );
            p.add( boxes[i] );
        }
        cbg.setSelectedCheckbox( boxes[0] );

        //p.add(infoPanel);
        //p.add(basisPanel);
        Panel _p = new Panel();
        _p.setLayout( new BorderLayout() );
        _p.add( p, BorderLayout.NORTH );
        add( rightPanel = _p, BorderLayout.EAST );
    }


    /**
     * Helper method to add a component with a label.
     *
     * @param    root    The Panel to which the component and label should
     * be added
     * @param    c        The component to be added
     * @param    label    The text label for the component
     */
    public void addWithLabel( Panel root, Component c, String label ) {
        Container p = new Panel();
        //Panel p = new Panel();
        p.setLayout( new BorderLayout() );
        Insets i = p.getInsets();
        i.top = i.bottom = i.left = i.right = 1;

        p.add( new Label( label ), BorderLayout.WEST );
        p.add( c );
        root.add( p );
    }

    public void start() {
        super.start();

        for( int i = 0; i < boxes.length; i++ ) {
            boxes[i].addItemListener( this );
        }

        addMouseListener( vectorMoveListener );
        addMouseMotionListener( vectorMoveListener );
    }

    public void stop() {
        removeMouseMotionListener( vectorMoveListener );
        removeMouseListener( vectorMoveListener );

        for( int i = 0; i < boxes.length; i++ ) {
            boxes[i].removeItemListener( this );
        }

        super.stop();
    }


    /**
     * Calculates the width of the area available for the lattice display
     */
    public int displayWidth() {
        return getSize().width - rightPanel.getSize().width;
    }

    public void doPaint( Graphics g ) {
        int w = displayWidth();
        int h = getSize().height;

        g.setColor( Color.black );
        g.drawLine( w - 1, 0, w - 1, h );

        updateLabels();
        lattice.paint( g, w, h );


        int smallW = w / 3;
        int smallH = h / 3;

        Point pos = lastPos = chooseWindowPos( w, h, smallW, smallH );

        g.translate( pos.x, pos.y );
        g.setColor( new Color( 0xe0e0ff ) );
        g.fillRect( 0, 0, smallW, smallH );
        g.setColor( Color.black );
        g.drawRect( 0, 0, smallW, smallH );

        g.setClip( 0, 0, smallW, smallH );


        if( !reciprocal.vectors[0].isNaN() && !reciprocal.vectors[1].isNaN() ) {
            reciprocal.paint( g, smallW, smallH );
        }

        g.translate( -pos.x, -pos.y );
    }

    protected Point lastPos;

    public Point chooseWindowPos( int w, int h, int wndW, int wndH ) {
        int x1 = lattice.toScreenX( lattice.vectors[0].x, w );
        int y1 = lattice.toScreenY( lattice.vectors[0].y, h );
        int x2 = lattice.toScreenX( lattice.vectors[1].x, w );
        int y2 = lattice.toScreenY( lattice.vectors[1].y, h );

        //	Stick with last position if it's still OK
        if( lastPos != null ) {
            Rectangle r = new Rectangle( lastPos.x, lastPos.y, wndW, wndH );
            if( !r.contains( x1, y1 ) && !r.contains( x2, y2 ) ) {
                return lastPos;
            }
        }


        int bestX = w - wndW;
        int bestY = h - wndH;

        if( ( x1 < bestX || y1 < bestY ) && ( x2 < bestX || y2 < bestY ) ) {
            return new Point( bestX, bestY );
        }

        if( ( x1 < bestX || y1 > wndH ) && ( x2 < bestX || y2 > wndH ) ) {
            return new Point( bestX, 0 );
        }

        return new Point( 0, bestY );
    }


    /**
     * Updates the numerical information displayed to the right of the
     * applet (vector lengths, length ratio, angle between vectors).
     */
    public void updateLabels() {
        Vector2D v1 = lattice.vectors[0];
        Vector2D v2 = lattice.vectors[1];
        int size = 2 * lattice.basisSize;

        mod1.setText( toString( v1.modulus() / size ) );
        mod2.setText( toString( v2.modulus() / size ) );
        ratio.setText( toString( v1.modulus() / v2.modulus() ) );
        angle.setText( toString( 180 * ( v1.argument() - v2.argument() ) / Math.PI ) );
    }


    /**
     * Helper method to format a <code>double</code> for display in a text
     * label.
     */
    protected String toString( double d ) {
        String s = "" + d;
        if( s.length() > 5 ) {
            s = s.substring( 0, 5 );
            if( s.charAt( 4 ) == '.' ) {
                s = s.substring( 0, 4 );
            }
        }
        return s;
    }


    public void actionPerformed( ActionEvent ae ) {
        for( int i = 0; i < boxes.length; i++ ) {
            if( boxes[i].getState() ) {
                lattice.setBasis( bases[i] );
            }
        }
        refresh();
    }


    public void updateReciprocal() {
        Vector2D[] v = lattice.vectors;
        Vector2D[] r = reciprocal.vectors;

        double multiplier = 1000 / ( v[0].x * v[1].y - v[0].y * v[1].x );

        r[0] = new Vector2D( v[1].y, -v[1].x ).multiply( multiplier );
        r[1] = new Vector2D( -v[0].y, v[0].x ).multiply( multiplier );
    }


    /**
     * Returns an array of labels for all available bases.
     */
    public String[] makeLabels() {
        return new String[]{
                "Hexagon",
                "Square",
                "Triangle",
                "Circle",
                "Point",
        };
    }


    /**
     * Returns an array of all available bases.
     */
    public Lattice2D.Basis[] makeBases( Lattice2D l ) {
        int size = l.basisSize;
        return new Lattice2D.Basis[]{
                l.new PolygonBasis( 6, size ),
                l.new PolygonBasis( 4, size, Math.PI / 4 ),
                l.new PolygonBasis( 3, size, Math.PI / 6 ),
                l.new CircularBasis( size ),
                //l.new CircularBasis(1),
                l.new PolygonBasis( 4, 1, Math.PI / 4 ),
        };

    }


    /**
     * The lattice itself
     */
    protected Lattice2D lattice, reciprocal;

    /**
     * Listener for changes in the vectors.
     */
    protected VectorMoveListener vectorMoveListener;

    protected Label angle, mod1, mod2, ratio;

    /**
     * Names for all available bases
     */
    protected String[] labels;

    /**
     * A list of all available bases
     */
    protected Lattice2D.Basis[] bases;

    /**
     * A checkbox for each basis
     */
    protected Checkbox[] boxes;

    /**
     * Ensures that exactly one basis will be selected
     */
    protected CheckboxGroup cbg;

    /**
     * Container to hold all the GUI components.  Will be positioned to the
     * right of the applet using BorderLayout.
     */
    protected Panel rightPanel;


    /**
     * Listener to detect changes in the positions of the lattice vectors.
     */
    protected class VectorMoveListener extends MouseAdapter implements MouseMotionListener {
        public void mousePressed( MouseEvent me ) {
            int x = me.getX();
            int y = me.getY();
            selected = lattice.chooseVector( x, y, displayWidth(), getSize().height );
            update( x, y );
        }

        public void mouseReleased( MouseEvent me ) {
            selected = null;
        }

        public void mouseExited( MouseEvent me ) {
            //selected = null;
        }

        public void mouseMoved( MouseEvent me ) {
            update( me.getX(), me.getY() );
        }

        public void mouseDragged( MouseEvent me ) {
            update( me.getX(), me.getY() );
        }

        public void update( int x, int y ) {
            if( selected == null ) {
                return;
            }

            int w = displayWidth();
            int h = getSize().height;

            x = Math.max( x, 0 );
            y = Math.max( y, 0 );
            x = Math.min( x, w - 1 );
            y = Math.min( y, h - 1 );

            selected.set( lattice.fromScreenX( x, w ), lattice.fromScreenY( y, h ) );
            double modulus = selected.modulus();
            if( modulus < MIN_SPACING ) {
                double scale = MIN_SPACING / modulus;
                selected.setX( selected.getX() * scale );
                selected.setY( selected.getY() * scale );
            }
            updateReciprocal();
            refresh();
        }


        protected Vector2D selected = null;
    }


    /**    The minimum modulus (in pixels) permitted for the lattice vectors.
     This prevents the vectors from becoming too small to see, but does not
     restrict the lattices available, as the vectors may still be
     positioned arbitrarily close together.
     <p>
     (The one exception to this rule is that it is no longer to create the
     degnerate lattice which is collapsed onto zero dimensions.  This
     lattice is produced if and only if <b>v1</b> = <b>v2</b> = <b>0</b>.)
     */
    public static final double MIN_SPACING=20;
}