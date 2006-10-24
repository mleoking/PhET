/* Copyright 2004, Sam Reid */
package org.falstad;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * User: Sam Reid
 * Date: Feb 9, 2006
 * Time: 9:20:13 PM
 * Copyright (c) Feb 9, 2006 by Sam Reid
 */
class RippleFrame extends Frame
        implements ComponentListener, ActionListener, AdjustmentListener,
                   MouseMotionListener, MouseListener, ItemListener {

    Thread engine = null;

    Dimension winSize;
    Image dbimage;

    Random random;
    int gridSizeX;
    int gridSizeY;
    int gridSizeXY;
    int gw;
    int windowWidth = 50;
    int windowHeight = 50;
    int windowOffsetX = 0;
    int windowOffsetY = 0;
    int windowBottom = 0;
    int windowRight = 0;
    public static final int sourceRadius = 7;
    public static final double freqMult = .0233333;

    public String getAppletInfo() {
        return "Ripple by Paul Falstad";
    }

    Button blankButton;
    Button blankWallsButton;
    Button borderButton;
    Checkbox stoppedCheck;
    Checkbox fixedEndsCheck;
    Checkbox view3dCheck;
    Choice modeChooser;
    Choice sourceChooser;
    Choice setupChooser;
    Choice colorChooser;
    Vector setupList;
    RippleFrame.Setup setup;
    Scrollbar dampingBar;
    Scrollbar speedBar;
    Scrollbar freqBar;
    Scrollbar resBar;
    Scrollbar brightnessBar;
    Scrollbar auxBar;
    Label auxLabel;
    double dampcoef;
    double freqTimeZero;
    double movingSourcePos = 0;
    double brightMult = 1;
    static final double pi = 3.14159265358979323846;
    float func[];
    float funci[];
    float damp[];
    boolean walls[];
    boolean exceptional[];
    int medium[];
    RippleFrame.OscSource sources[];
    static final int MODE_SETFUNC = 0;
    static final int MODE_WALLS = 1;
    static final int MODE_MEDIUM = 2;
    static final int MODE_FUNCHOLD = 3;
    int dragX, dragY, dragStartX, dragStartY;
    int selectedSource = -1;
    int sourceIndex;
    int freqBarValue;
    boolean dragging;
    boolean dragClear;
    boolean dragSet;
    double t;
    int pause;
    MemoryImageSource imageSource;
    int pixels[];
    int sourceCount = -1;
    boolean sourcePlane = false;
    boolean sourceMoving = false;
    int sourceFreqCount = -1;
    int sourceWaveform = SWF_SIN;
    int auxFunction;
    Color wallColor, posColor, negColor, zeroColor,
            medColor, posMedColor, negMedColor, sourceColor;
    Color schemeColors[][];
    static final int mediumMax = 191;
    static final double mediumMaxIndex = .5;
    static final int SWF_SIN = 0;
    static final int SWF_SQUARE = 1;
    static final int SWF_PULSE = 2;
    static final int AUX_NONE = 0;
    static final int AUX_PHASE = 1;
    static final int AUX_FREQ = 2;
    static final int AUX_SPEED = 3;
    static final int SRC_NONE = 0;
    static final int SRC_1S1F = 1;
    static final int SRC_2S1F = 3;
    static final int SRC_2S2F = 4;
    static final int SRC_4S1F = 6;
    static final int SRC_1S1F_PULSE = 8;
    static final int SRC_1S1F_MOVING = 9;
    static final int SRC_1S1F_PLANE = 10;
    static final int SRC_2S1F_PLANE = 12;
    static final int SRC_1S1F_PLANE_PULSE = 14;
    static final int SRC_1S1F_PLANE_PHASE = 15;
    static final int SRC_6S1F = 16;
    static final int SRC_8S1F = 17;
    static final int SRC_10S1F = 18;
    static final int SRC_12S1F = 19;
    static final int SRC_16S1F = 20;
    static final int SRC_20S1F = 21;

    int getrand( int x ) {
        int q = random.nextInt();
        if( q < 0 ) {
            q = -q;
        }
        return q % x;
    }

    RippleCanvas cv;
    Ripple applet;

    RippleFrame( Ripple a ) {
        super( "Ripple Tank Applet v1.7c" );
        applet = a;
    }

    boolean useBufferedImage = false;

    public void init() {
        setupList = new Vector();
        RippleFrame.Setup s = new RippleFrame.SingleSourceSetup();
        while( s != null ) {
            setupList.addElement( s );
            s = s.createNext();
        }
        String os = System.getProperty( "os.name" );
        int res = 110;
        String jv = System.getProperty( "java.class.version" );
        double jvf = new Double( jv ).doubleValue();
        if( jvf >= 48 ) {
            useBufferedImage = true;
        }

        sources = new RippleFrame.OscSource[20];
        setLayout( new RippleLayout() );
        cv = new RippleCanvas( this );
        cv.addComponentListener( this );
        cv.addMouseMotionListener( this );
        cv.addMouseListener( this );
        add( cv );

        setupChooser = new Choice();
        int i;
        for( i = 0; i != setupList.size(); i++ ) {
            setupChooser.add( "Setup: " +
                              ( (RippleFrame.Setup)setupList.elementAt( i ) ).getName() );
        }
        setupChooser.addItemListener( this );
        add( setupChooser );

        //add(new Label("Source mode:", Label.CENTER));
        sourceChooser = new Choice();
        sourceChooser.add( "No Sources" );
        sourceChooser.add( "1 Src, 1 Freq" );
        sourceChooser.add( "1 Src, 2 Freq" );
        sourceChooser.add( "2 Src, 1 Freq" );
        sourceChooser.add( "2 Src, 2 Freq" );
        sourceChooser.add( "3 Src, 1 Freq" );
        sourceChooser.add( "4 Src, 1 Freq" );
        sourceChooser.add( "1 Src, 1 Freq (Square)" );
        sourceChooser.add( "1 Src, 1 Freq (Pulse)" );
        sourceChooser.add( "1 Moving Src" );
        sourceChooser.add( "1 Plane Src, 1 Freq" );
        sourceChooser.add( "1 Plane Src, 2 Freq" );
        sourceChooser.add( "2 Plane Src, 1 Freq" );
        sourceChooser.add( "2 Plane Src, 2 Freq" );
        sourceChooser.add( "1 Plane 1 Freq (Pulse)" );
        sourceChooser.add( "1 Plane 1 Freq w/Phase" );
        sourceChooser.add( "6 Src, 1 Freq" );
        sourceChooser.add( "8 Src, 1 Freq" );
        sourceChooser.add( "10 Src, 1 Freq" );
        sourceChooser.add( "12 Src, 1 Freq" );
        sourceChooser.add( "16 Src, 1 Freq" );
        sourceChooser.add( "20 Src, 1 Freq" );
        sourceChooser.select( SRC_1S1F );
        sourceChooser.addItemListener( this );
        add( sourceChooser );

        //add(new Label("Mouse mode:", Label.CENTER));
        modeChooser = new Choice();
        modeChooser.add( "Mouse = Edit Wave" );
        modeChooser.add( "Mouse = Edit Walls" );
        modeChooser.add( "Mouse = Edit Medium" );
        modeChooser.add( "Mouse = Hold Wave" );
        modeChooser.addItemListener( this );
        add( modeChooser );

        colorChooser = new Choice();
        colorChooser.addItemListener( this );
        add( colorChooser );

        add( blankButton = new Button( "Clear Waves" ) );
        blankButton.addActionListener( this );
        add( blankWallsButton = new Button( "Clear Walls" ) );
        blankWallsButton.addActionListener( this );
        add( borderButton = new Button( "Add Border" ) );
        borderButton.addActionListener( this );
        stoppedCheck = new Checkbox( "Stopped" );
        stoppedCheck.addItemListener( this );
        add( stoppedCheck );
        fixedEndsCheck = new Checkbox( "Fixed Edges", true );
        fixedEndsCheck.addItemListener( this );
        add( fixedEndsCheck );

        view3dCheck = new Checkbox( "3-D View" );
        view3dCheck.addItemListener( this );
        add( view3dCheck );

        add( new Label( "Simulation Speed", Label.CENTER ) );
        add( speedBar = new Scrollbar( Scrollbar.HORIZONTAL, 8, 1, 1, 100 ) );
        speedBar.addAdjustmentListener( this );

        add( new Label( "Resolution", Label.CENTER ) );
        add( resBar = new Scrollbar( Scrollbar.HORIZONTAL, res, 5, 5, 250 ) );
        resBar.addAdjustmentListener( this );
        setResolution();

        new Label( "Damping", Label.CENTER );
        dampingBar = new Scrollbar( Scrollbar.HORIZONTAL, 10, 1, 2, 100 );
        dampingBar.addAdjustmentListener( this );

        add( new Label( "Source Frequency", Label.CENTER ) );
        add( freqBar = new Scrollbar( Scrollbar.HORIZONTAL,
                                      freqBarValue = 15, 1, 1, 30 ) );
        freqBar.addAdjustmentListener( this );

        add( new Label( "Brightness", Label.CENTER ) );
        add( brightnessBar = new Scrollbar( Scrollbar.HORIZONTAL,
                                            27, 1, 1, 1200 ) );
        brightnessBar.addAdjustmentListener( this );

        add( auxLabel = new Label( "", Label.CENTER ) );
        add( auxBar = new Scrollbar( Scrollbar.HORIZONTAL,
                                     1, 1, 1, 30 ) );
        auxBar.addAdjustmentListener( this );

        add( new Label( "http://www.falstad.com" ) );

        schemeColors = new Color[20][8];

        try {
            String param = applet.getParameter( "PAUSE" );
            if( param != null ) {
                pause = Integer.parseInt( param );
            }
            param = applet.getParameter( "setup" );
            if( param != null ) {
                setupChooser.select( Integer.parseInt( param ) );
            }
            for( i = 0; i != 20; i++ ) {
                param = applet.getParameter( "colorScheme" + ( i + 1 ) );
                if( param == null ) {
                    break;
                }
                decodeColorScheme( i, param );
            }
        }
        catch( Exception e ) { }
        if( colorChooser.getItemCount() == 0 ) {
            addDefaultColorScheme();
        }
        doColor();
        random = new Random();
        setDamping();
        setup = (RippleFrame.Setup)setupList.elementAt( setupChooser.getSelectedIndex() );
        reinit();
        cv.setBackground( Color.black );
        cv.setForeground( Color.lightGray );

        resize( 700, 540 );
        handleResize();
        Dimension x = getSize();
        Dimension screen = getToolkit().getScreenSize();
        setLocation( ( screen.width - x.width ) / 2,
                     ( screen.height - x.height ) / 2 );
        show();
    }

    void reinit() {
        sourceCount = -1;
        System.out.print( "reinit " + gridSizeX + " " + gridSizeY + "\n" );
        gridSizeXY = gridSizeX * gridSizeY;
        gw = gridSizeY;
        func = new float[gridSizeXY];
        funci = new float[gridSizeXY];
        damp = new float[gridSizeXY];
        exceptional = new boolean[gridSizeXY];
        medium = new int[gridSizeXY];
        walls = new boolean[gridSizeXY];
        int i, j;
        for( i = 0; i != gridSizeXY; i++ ) {
            damp[i] = 1f; // (float) dampcoef;
        }
        for( i = 0; i != windowOffsetX; i++ ) {
            for( j = 0; j != gridSizeX; j++ ) {
                damp[i + j * gw] = damp[gridSizeX - 1 - i + gw * j] =
                damp[j + gw * i] = damp[j + ( gridSizeY - 1 - i ) * gw] =
                        (float)( .999 - ( windowOffsetX - i ) * .002 );
            }
        }
        doSetup();

    }

    void handleResize() {
        Dimension d = winSize = cv.getSize();
        if( winSize.width == 0 ) {
            return;
        }
        pixels = null;
        if( useBufferedImage ) {
            try {
                /* simulate the following code using reflection:
               dbimage = new BufferedImage(d.width, d.height,
               BufferedImage.TYPE_INT_RGB);
               DataBuffer db = (DataBuffer)(((BufferedImage)dbimage).
               getRaster().getDataBuffer());
               DataBufferInt dbi = (DataBufferInt) db;
               pixels = dbi.getData();
            */
                Class biclass = Class.forName( "java.awt.image.BufferedImage" );
                Class dbiclass = Class.forName( "java.awt.image.DataBufferInt" );
                Class rasclass = Class.forName( "java.awt.image.Raster" );
                Constructor cstr = biclass.getConstructor(
                        new Class[]{int.class, int.class, int.class} );
                dbimage = (Image)cstr.newInstance( new Object[]{
                        new Integer( d.width ), new Integer( d.height ),
                        new Integer( BufferedImage.TYPE_INT_RGB )} );
                Method m = biclass.getMethod( "getRaster", null );
                Object ras = m.invoke( dbimage, null );
                Object db = rasclass.getMethod( "getDataBuffer", null ).
                        invoke( ras, null );
                pixels = (int[])
                        dbiclass.getMethod( "getData", null ).invoke( db, null );
            }
            catch( Exception ee ) {
                // ee.printStackTrace();
                System.out.println( "BufferedImage failed" );
            }
        }
        if( pixels == null ) {
            pixels = new int[d.width * d.height];
            int i;
            for( i = 0; i != d.width * d.height; i++ ) {
                pixels[i] = 0xFF000000;
            }
            imageSource = new MemoryImageSource( d.width, d.height, pixels, 0,
                                                 d.width );
            imageSource.setAnimated( true );
            imageSource.setFullBufferUpdates( true );
            dbimage = cv.createImage( imageSource );
        }
    }

    public boolean handleEvent( Event ev ) {
        if( ev.id == Event.WINDOW_DESTROY ) {
            applet.destroyFrame();
            return true;
        }
        return super.handleEvent( ev );
    }

    void doBlank() {
        int x, y;
        // I set all the elements in the grid to 1e-10 instead of 0 because
        // if I set them to zero, then the simulation slows down for a
        // short time until the grid fills up again.  Don't ask me why!
        // I don't know.  This showed up when I started using floats
        // instead of doubles.
        for( x = 0; x != gridSizeXY; x++ ) {
            func[x] = funci[x] = 1e-10f;
        }
    }

    void doBlankWalls() {
        int x, y;
        for( x = 0; x != gridSizeXY; x++ ) {
            walls[x] = false;
            medium[x] = 0;
        }
        calcExceptions();
    }

    void doBorder() {
        int x, y;
        for( x = 0; x < gridSizeX; x++ ) {
            setWall( x, windowOffsetY );
            setWall( x, windowBottom );
        }
        for( y = 0; y < gridSizeY; y++ ) {
            setWall( windowOffsetX, y );
            setWall( windowRight, y );
        }
        calcExceptions();
    }

    void setWall( int x, int y ) {
        walls[x + gw * y] = true;
    }

    void setWall( int x, int y, boolean b ) {
        walls[x + gw * y] = b;
    }

    void setMedium( int x, int y, int q ) {
        medium[x + gw * y] = q;
    }

    void calcExceptions() {
        int x, y;
        // if walls are in place on border, need to extend that through
        // hidden area to avoid "leaks"
        for( x = 0; x != gridSizeX; x++ ) {
            for( y = 0; y < windowOffsetY; y++ ) {
                walls[x + gw * y] = walls[x + gw * windowOffsetY];
                walls[x + gw * ( gridSizeY - y - 1 )] =
                        walls[x + gw * ( gridSizeY - windowOffsetY - 1 )];
            }
        }
        for( y = 0; y < gridSizeY; y++ ) {
            for( x = 0; x < windowOffsetX; x++ ) {
                walls[x + gw * y] = walls[windowOffsetX + gw * y];
                walls[gridSizeX - x - 1 + gw * y] =
                        walls[gridSizeX - windowOffsetX - 1 + gw * y];
            }
        }
        // generate exceptional array, which is useful for doing
        // special handling of elements
        for( x = 1; x < gridSizeX - 1; x++ ) {
            for( y = 1; y < gridSizeY - 1; y++ ) {
                int gi = x + gw * y;
                exceptional[gi] =
                        walls[gi - 1] || walls[gi + 1] ||
                        walls[gi - gw] || walls[gi + gw] || walls[gi] ||
                        medium[gi] != medium[gi - 1] ||
                        medium[gi] != medium[gi + 1];
                if( ( x == 1 || x == gridSizeX - 2 ) &&
                    medium[gi] != medium[gridSizeX - 1 - x + gw * ( y + 1 )] ||
                                                                             medium[gi] != medium[gridSizeX - 1 - x + gw * ( y - 1 )] ) {
                    exceptional[gi] = true;
                }
            }
        }
        // put some extra exceptions at the corners to ensure tadd2, sinth,
        // etc get calculated
        exceptional[1 + gw] = exceptional[gridSizeX - 2 + gw] =
        exceptional[1 + ( gridSizeY - 2 ) * gw] =
        exceptional[gridSizeX - 2 + ( gridSizeY - 2 ) * gw] = true;
    }

    void centerString( Graphics g, String s, int y ) {
        FontMetrics fm = g.getFontMetrics();
        g.drawString( s, ( winSize.width - fm.stringWidth( s ) ) / 2, y );
    }

    public void paint( Graphics g ) {
        cv.repaint();
    }

    long lastTime = 0, lastFrameTime, secTime = 0;
    int frames = 0;
    int steps = 0;
    int framerate = 0, steprate = 0;

    boolean moveRight = true;
    boolean moveDown = true;

    public void updateRipple( Graphics realg ) {
        if( winSize == null || winSize.width == 0 ) {
            // this works around some weird bug in IE which causes the
            // applet to not show up properly sometimes.
            handleResize();
            return;
        }
        double tadd = 0;
        if( !stoppedCheck.getState() ) {
            int val = 5; //speedBar.getValue();
            tadd = val * .05;
        }
        int i, j;

        boolean stopFunc =
                dragging && selectedSource == -1 &&
                view3dCheck.getState() == false &&
                modeChooser.getSelectedIndex() == MODE_SETFUNC;
        if( stoppedCheck.getState() ) {
            stopFunc = true;
        }
        int iterCount = speedBar.getValue();
        if( !stopFunc ) {
            /*
           long sysTime = System.currentTimeMillis();
           if (sysTime-secTime >= 1000) {
           framerate = frames; steprate = steps;
           frames = 0; steps = 0;
           secTime = sysTime;
           }
           lastTime = sysTime;
           */
            int iter;
            int mxx = gridSizeX - 1;
            int mxy = gridSizeY - 1;
            for( iter = 0; iter != iterCount; iter++ ) {
                int jstart, jend, jinc;
                if( moveDown ) {
                    // we process the rows in alternate directions
                    // each time to avoid any directional bias.
                    jstart = 1;
                    jend = mxy;
                    jinc = 1;
                    moveDown = false;
                }
                else {
                    jstart = mxy - 1;
                    jend = 0;
                    jinc = -1;
                    moveDown = true;
                }
                moveRight = moveDown;
                float sinhalfth = 0;
                float sinth = 0;
                float scaleo = 0;
                int curMedium = -1;
                for( j = jstart; j != jend; j += jinc ) {
                    int istart, iend, iinc;
                    if( moveRight ) {
                        iinc = 1;
                        istart = 1;
                        iend = mxx;
                        moveRight = false;
                    }
                    else {
                        iinc = -1;
                        istart = mxx - 1;
                        iend = 0;
                        moveRight = true;
                    }
                    int gi = j * gw + istart;
                    int giEnd = j * gw + iend;
                    for( ; gi != giEnd; gi += iinc ) {
                        // calculate equilibrum point of this
                        // element's oscillation
                        float previ = func[gi - 1];
                        float nexti = func[gi + 1];
                        float prevj = func[gi - gw];
                        float nextj = func[gi + gw];
                        float basis = ( nexti + previ + nextj + prevj ) * .25f;
                        if( exceptional[gi] ) {
                            if( curMedium != medium[gi] ) {
                                curMedium = medium[gi];
                                double tadd2 = tadd * ( 1 -
                                                        ( mediumMaxIndex / mediumMax ) * curMedium );
                                sinhalfth = (float)
                                        Math.sin( tadd2 / 2 );
                                sinth = (float)
                                        ( Math.sin( tadd2 ) * dampcoef );
                                scaleo = (float)( 1 - Math.sqrt(
                                        4 * sinhalfth * sinhalfth - sinth * sinth ) );
                            }
                            if( walls[gi] ) {
                                continue;
                            }
                            int count = 4;
                            if( fixedEndsCheck.getState() ) {
                                if( walls[gi - 1] ) {
                                    previ = 0;
                                }
                                if( walls[gi + 1] ) {
                                    nexti = 0;
                                }
                                if( walls[gi - gw] ) {
                                    prevj = 0;
                                }
                                if( walls[gi + gw] ) {
                                    nextj = 0;
                                }
                            }
                            else {
                                if( walls[gi - 1] ) {
                                    previ = walls[gi + 1] ? func[gi] : func[gi + 1];
                                }
                                if( walls[gi + 1] ) {
                                    nexti = walls[gi - 1] ? func[gi] : func[gi - 1];
                                }
                                if( walls[gi - gw] ) {
                                    prevj = walls[gi + gw] ? func[gi] : func[gi + gw];
                                }
                                if( walls[gi + gw] ) {
                                    nextj = walls[gi - gw] ? func[gi] : func[gi - gw];
                                }
                            }
                            basis = ( nexti + previ + nextj + prevj ) * .25f;
                        }
                        // what we are doing here (aside from damping)
                        // is rotating the point (func[gi], funci[gi])
                        // an angle tadd about the point (basis, 0).
                        // Rather than call atan2/sin/cos, we use this
                        // faster method using some precomputed info.
                        float a = 0;
                        float b = 0;
                        if( damp[gi] == 1f ) {
                            a = func[gi] - basis;
                            b = funci[gi];
                        }
                        else {
                            a = ( func[gi] - basis ) * damp[gi];
                            b = funci[gi] * damp[gi];
                        }
                        func[gi] = basis + a * scaleo - b * sinth;
                        funci[gi] = b * scaleo + a * sinth;
                    }
                }
                t += tadd;
                if( sourceCount > 0 ) {
                    double w = freqBar.getValue() * ( t - freqTimeZero ) * freqMult;
                    double w2 = w;
                    boolean skip = false;
                    switch( auxFunction ) {
                        case AUX_FREQ:
                            w2 = auxBar.getValue() * t * freqMult;
                            break;
                        case AUX_PHASE:
                            w2 = w + ( auxBar.getValue() - 1 ) * ( pi / 29 );
                            break;
                    }
                    double v = 0;
                    double v2 = 0;
                    switch( sourceWaveform ) {
                        case SWF_SIN:
                            v = Math.cos( w );
                            if( sourceCount >= ( sourcePlane ? 4 : 2 ) ) {
                                v2 = Math.cos( w2 );
                            }
                            else if( sourceFreqCount == 2 ) {
                                v = ( v + Math.cos( w2 ) ) * .5;
                            }
                            break;
                        case SWF_SQUARE:
                            w %= pi * 2;
                            v = ( w < pi ) ? 1 : -1;
                            break;
                        case SWF_PULSE: {
                            w %= pi * 2;
                            double pulselen = pi / 4;
                            double pulselen2 = freqBar.getValue() * .2;
                            if( pulselen2 < pulselen ) {
                                pulselen = pulselen2;
                            }
                            v = ( w > pulselen ) ? 0 :
                                Math.sin( w * pi / pulselen );
                            if( w > pulselen * 2 ) {
                                skip = true;
                            }
                        }
                        break;
                    }
                    for( j = 0; j != sourceCount; j++ ) {
                        if( ( j % 2 ) == 0 ) {
                            sources[j].v = (float)( v * setup.sourceStrength() );
                        }
                        else {
                            sources[j].v = (float)( v2 * setup.sourceStrength() );
                        }
                    }
                    if( sourcePlane ) {
                        if( !skip ) {
                            for( j = 0; j != sourceCount / 2; j++ ) {
                                RippleFrame.OscSource src1 = sources[j * 2];
                                RippleFrame.OscSource src2 = sources[j * 2 + 1];
                                RippleFrame.OscSource src3 = sources[j];
                                drawPlaneSource( src1.x, src1.y,
                                                 src2.x, src2.y, src3.v, w );
                            }
                        }
                    }
                    else {
                        if( sourceMoving ) {
                            int sy;
                            movingSourcePos += tadd * .02 * auxBar.getValue();
                            double wm = movingSourcePos;
                            int h = windowHeight - 3;
                            wm %= h * 2;
                            sy = (int)wm;
                            if( sy > h ) {
                                sy = 2 * h - sy;
                            }
                            sy += windowOffsetY + 1;
                            sources[0].y = sy;
                        }
                        for( i = 0; i != sourceCount; i++ ) {
                            RippleFrame.OscSource src = sources[i];
                            func[src.x + gw * src.y] = src.v;
                            funci[src.x + gw * src.y] = 0;
                        }
                    }
                }
                setup.eachFrame();
                steps++;
                filterGrid();
            }
        }

        brightMult = Math.exp( brightnessBar.getValue() / 100. - 5. );
        if( view3dCheck.getState() ) {
            draw3dView();
        }
        else {
            draw2dView();
        }

        if( imageSource != null ) {
            imageSource.newPixels();
        }

        realg.drawImage( dbimage, 0, 0, this );

        /*frames++;
      realg.setColor(Color.white);
      realg.drawString("Framerate: " + framerate, 10, 10);
      realg.drawString("Steprate: " + steprate,  10, 30);
      lastFrameTime = lastTime;
      */

        if( !stoppedCheck.getState() ) {
            if( dragging && selectedSource == -1 &&
                modeChooser.getSelectedIndex() == MODE_FUNCHOLD ) {
                editFuncPoint( dragX, dragY );
            }
            cv.repaint( pause );
        }
    }

    // filter out high-frequency noise
    int filterCount;

    void filterGrid() {
        int x, y;
        if( fixedEndsCheck.getState() ) {
            return;
        }
        if( sourceCount > 0 && freqBarValue > 23 ) {
            return;
        }
        if( sourceFreqCount >= 2 && auxBar.getValue() > 23 ) {
            return;
        }
        if( ++filterCount < 10 ) {
            return;
        }
        filterCount = 0;
        for( y = windowOffsetY; y < windowBottom; y++ ) {
            for( x = windowOffsetX; x < windowRight; x++ ) {
                int gi = x + y * gw;
                if( walls[gi] ) {
                    continue;
                }
                if( func[gi - 1] < 0 && func[gi] > 0 && func[gi + 1] < 0 &&
                    !walls[gi + 1] && !walls[gi - 1] ) {
                    func[gi] = ( func[gi - 1] + func[gi + 1] ) / 2;
                }
                if( func[gi - gw] < 0 && func[gi] > 0 && func[gi + gw] < 0 &&
                    !walls[gi - gw] && !walls[gi + gw] ) {
                    func[gi] = ( func[gi - gw] + func[gi + gw] ) / 2;
                }
                if( func[gi - 1] > 0 && func[gi] < 0 && func[gi + 1] > 0 &&
                    !walls[gi + 1] && !walls[gi - 1] ) {
                    func[gi] = ( func[gi - 1] + func[gi + 1] ) / 2;
                }
                if( func[gi - gw] > 0 && func[gi] < 0 && func[gi + gw] > 0 &&
                    !walls[gi - gw] && !walls[gi + gw] ) {
                    func[gi] = ( func[gi - gw] + func[gi + gw] ) / 2;
                }
            }
        }
    }

    void plotPixel( int x, int y, int pix ) {
        if( x < 0 || x >= winSize.width ) {
            return;
        }
        try { pixels[x + y * winSize.width] = pix; } catch( Exception e ) {}
    }

    // draw a circle the slow and dirty way
    void plotSource( int n, int xx, int yy ) {
        int rad = sourceRadius;
        int j;
        int col = ( sourceColor.getRed() << 16 ) |
                  ( sourceColor.getGreen() << 8 ) |
                  ( sourceColor.getBlue() ) | 0xFF000000;
        if( n == selectedSource ) {
            col ^= 0xFFFFFF;
        }
        for( j = 0; j <= rad; j++ ) {
            int k = (int)( Math.sqrt( rad * rad - j * j ) + .5 );
            plotPixel( xx + j, yy + k, col );
            plotPixel( xx + k, yy + j, col );
            plotPixel( xx + j, yy - k, col );
            plotPixel( xx - k, yy + j, col );
            plotPixel( xx - j, yy + k, col );
            plotPixel( xx + k, yy - j, col );
            plotPixel( xx - j, yy - k, col );
            plotPixel( xx - k, yy - j, col );
            plotPixel( xx, yy + j, col );
            plotPixel( xx, yy - j, col );
            plotPixel( xx + j, yy, col );
            plotPixel( xx - j, yy, col );
        }
    }

    void draw2dView() {
        int ix = 0;
        int i, j, k, l;
        for( j = 0; j != windowHeight; j++ ) {
            ix = winSize.width * ( j * winSize.height / windowHeight );
            int j2 = j + windowOffsetY;
            int gi = j2 * gw + windowOffsetX;
            int y = j * winSize.height / windowHeight;
            int y2 = ( j + 1 ) * winSize.height / windowHeight;
            for( i = 0; i != windowWidth; i++, gi++ ) {
                int x = i * winSize.width / windowWidth;
                int x2 = ( i + 1 ) * winSize.width / windowWidth;
                int i2 = i + windowOffsetX;
                double dy = func[gi] * brightMult;
                if( dy < -1 ) {
                    dy = -1;
                }
                if( dy > 1 ) {
                    dy = 1;
                }
                int col = 0;
                int colR = 0, colG = 0, colB = 0;
                if( walls[gi] ) {
                    colR = wallColor.getRed();
                    colG = wallColor.getGreen();
                    colB = wallColor.getBlue();
                }
                else if( dy < 0 ) {
                    double d1 = -dy;
                    double d2 = 1 - d1;
                    double d3 = medium[gi] * ( 1 / 255.01 );
                    double d4 = 1 - d3;
                    double a1 = d1 * d4;
                    double a2 = d2 * d4;
                    double a3 = d1 * d3;
                    double a4 = d2 * d3;
                    colR = (int)( negColor.getRed() * a1 +
                                  zeroColor.getRed() * a2 +
                                  negMedColor.getRed() * a3 +
                                  medColor.getRed() * a4 );
                    colG = (int)( negColor.getGreen() * a1 +
                                  zeroColor.getGreen() * a2 +
                                  negMedColor.getGreen() * a3 +
                                  medColor.getGreen() * a4 );
                    colB = (int)( negColor.getBlue() * a1 +
                                  zeroColor.getBlue() * a2 +
                                  negMedColor.getBlue() * a3 +
                                  medColor.getBlue() * a4 );
                }
                else {
                    double d1 = dy;
                    double d2 = 1 - dy;
                    double d3 = medium[gi] * ( 1 / 255.01 );
                    double d4 = 1 - d3;
                    double a1 = d1 * d4;
                    double a2 = d2 * d4;
                    double a3 = d1 * d3;
                    double a4 = d2 * d3;
                    colR = (int)( posColor.getRed() * a1 +
                                  zeroColor.getRed() * a2 +
                                  posMedColor.getRed() * a3 +
                                  medColor.getRed() * a4 );
                    colG = (int)( posColor.getGreen() * a1 +
                                  zeroColor.getGreen() * a2 +
                                  posMedColor.getGreen() * a3 +
                                  medColor.getGreen() * a4 );
                    colB = (int)( posColor.getBlue() * a1 +
                                  zeroColor.getBlue() * a2 +
                                  posMedColor.getBlue() * a3 +
                                  medColor.getBlue() * a4 );
                }
                col = ( 255 << 24 ) | ( colR << 16 ) | ( colG << 8 ) | ( colB );
                for( k = 0; k != x2 - x; k++, ix++ ) {
                    for( l = 0; l != y2 - y; l++ ) {
                        pixels[ix + l * winSize.width] = col;
                    }
                }
            }
        }
        int intf = ( gridSizeY / 2 - windowOffsetY ) * winSize.height / windowHeight;
        for( i = 0; i != sourceCount; i++ ) {
            RippleFrame.OscSource src = sources[i];
            int xx = src.getScreenX();
            int yy = src.getScreenY();
            plotSource( i, xx, yy );
        }
    }

    double realxmx, realxmy, realymz, realzmy, realzmx, realymadd, realzmadd;
    double viewAngle = pi, viewAngleDragStart;
    double viewZoom = .775, viewZoomDragStart;
    double viewAngleCos = -1, viewAngleSin = 0;
    double viewHeight = -38, viewHeightDragStart;
    double scalex, scaley;
    int centerX3d, centerY3d;
    int xpoints[] = new int[4], ypoints[] = new int[4];
    final double viewDistance = 66;

    void map3d( double x, double y, double z, int xpoints[], int ypoints[], int pt ) {
        /*x *= aspectRatio;
      z *= -4;
      x *= 16./sampleCount; y *= 16./sampleCount;
      double realx = x*viewAngleCos + y*viewAngleSin; // range: [-10,10]
      double realy = z-viewHeight;
      double realz = y*viewAngleCos - x*viewAngleSin + viewDistance;*/
        double realx = realxmx * x + realxmy * y;
        double realy = realymz * z + realymadd;
        double realz = realzmx * x + realzmy * y + realzmadd;
        xpoints[pt] = centerX3d + (int)( realx / realz );
        ypoints[pt] = centerY3d - (int)( realy / realz );
    }

    double scaleMult;

    void scaleworld() {
        scalex = viewZoom * ( winSize.width / 4 ) * viewDistance / 8;
        scaley = -scalex;
        int y = (int)( scaley * viewHeight / viewDistance );
        /*centerX3d = winSize.x + winSize.width/2;
        centerY3d = winSize.y + winSize.height/2 - y;*/
        centerX3d = winSize.width / 2;
        centerY3d = winSize.height / 2 - y;
        scaleMult = 16. / ( windowWidth / 2 );
        realxmx = -viewAngleCos * scaleMult * scalex;
        realxmy = viewAngleSin * scaleMult * scalex;
        realymz = -brightMult * scaley;
        realzmy = viewAngleCos * scaleMult;
        realzmx = viewAngleSin * scaleMult;
        realymadd = -viewHeight * scaley;
        realzmadd = viewDistance;
    }

    void draw3dView() {
        int half = gridSizeX / 2;
        scaleworld();
        int x, y;
        int xdir, xstart, xend;
        int ydir, ystart, yend;
        int sc = windowRight - 1;

        // figure out what order to render the grid elements so that
        // the ones in front are rendered first.
        if( viewAngleCos > 0 ) {
            ystart = sc;
            yend = windowOffsetY - 1;
            ydir = -1;
        }
        else {
            ystart = windowOffsetY;
            yend = sc + 1;
            ydir = 1;
        }
        if( viewAngleSin < 0 ) {
            xstart = windowOffsetX;
            xend = sc + 1;
            xdir = 1;
        }
        else {
            xstart = sc;
            xend = windowOffsetX - 1;
            xdir = -1;
        }
        boolean xFirst = ( viewAngleSin * xdir < viewAngleCos * ydir );

        for( x = 0; x != winSize.width * winSize.height; x++ ) {
            pixels[x] = 0xFF000000;
        }
        /*double zval = 2.0/sampleCount;
        System.out.println(zval);
        if (sampleCount == 128)
        zval = .1;*/
        double zval = .1;
        double zval2 = zval * zval;

        for( x = xstart; x != xend; x += xdir ) {
            for( y = ystart; y != yend; y += ydir ) {
                if( !xFirst ) {
                    x = xstart;
                }
                for( ; x != xend; x += xdir ) {
                    int gi = x + gw * y;
                    map3d( x - half, y - half, func[gi], xpoints, ypoints, 0 );
                    map3d( x + 1 - half, y - half, func[gi + 1],
                           xpoints, ypoints, 1 );
                    map3d( x - half, y + 1 - half, func[gi + gw],
                           xpoints, ypoints, 2 );
                    map3d( x + 1 - half, y + 1 - half, func[gi + gw + 1],
                           xpoints, ypoints, 3 );
                    double qx = func[gi + 1] - func[gi];
                    double qy = func[gi + gw] - func[gi];
                    // calculate lighting
                    double normdot = ( qx + qy + zval ) * ( 1 / 1.73 ) /
                                     Math.sqrt( qx * qx + qy * qy + zval2 );
                    int col = computeColor( gi, normdot );
                    fillTriangle( xpoints[0], ypoints[0],
                                  xpoints[1], ypoints[1],
                                  xpoints[3], ypoints[3], col );
                    fillTriangle( xpoints[0], ypoints[0],
                                  xpoints[2], ypoints[2],
                                  xpoints[3], ypoints[3], col );
                    if( xFirst ) {
                        break;
                    }
                }
            }
            if( !xFirst ) {
                break;
            }
        }
    }

    int computeColor( int gix, double c ) {
        double h = func[gix] * brightMult;
        if( c < 0 ) {
            c = 0;
        }
        if( c > 1 ) {
            c = 1;
        }
        c = .5 + c * .5;
        double redness = ( h < 0 ) ? -h : 0;
        double grnness = ( h > 0 ) ? h : 0;
        if( redness > 1 ) {
            redness = 1;
        }
        if( grnness > 1 ) {
            grnness = 1;
        }
        if( grnness < 0 ) {
            grnness = 0;
        }
        if( redness < 0 ) {
            redness = 0;
        }
        double grayness = ( 1 - ( redness + grnness ) ) * c;
        double grayness2 = grayness;
        if( medium[gix] > 0 ) {
            double mm = 1 - ( medium[gix] * ( 1 / 255.01 ) );
            grayness2 *= mm;
        }
        double gray = .6;
        int ri = (int)( ( c * redness + gray * grayness2 ) * 255 );
        int gi = (int)( ( c * grnness + gray * grayness2 ) * 255 );
        int bi = (int)( ( gray * grayness ) * 255 );
        return 0xFF000000 | ( ri << 16 ) | ( gi << 8 ) | bi;
    }

    void fillTriangle( int x1, int y1, int x2, int y2, int x3, int y3,
                       int col ) {
        if( x1 > x2 ) {
            if( x2 > x3 ) {
                // x1 > x2 > x3
                int ay = interp( x1, y1, x3, y3, x2 );
                fillTriangle1( x3, y3, x2, y2, ay, col );
                fillTriangle1( x1, y1, x2, y2, ay, col );
            }
            else if( x1 > x3 ) {
                // x1 > x3 > x2
                int ay = interp( x1, y1, x2, y2, x3 );
                fillTriangle1( x2, y2, x3, y3, ay, col );
                fillTriangle1( x1, y1, x3, y3, ay, col );
            }
            else {
                // x3 > x1 > x2
                int ay = interp( x3, y3, x2, y2, x1 );
                fillTriangle1( x2, y2, x1, y1, ay, col );
                fillTriangle1( x3, y3, x1, y1, ay, col );
            }
        }
        else {
            if( x1 > x3 ) {
                // x2 > x1 > x3
                int ay = interp( x2, y2, x3, y3, x1 );
                fillTriangle1( x3, y3, x1, y1, ay, col );
                fillTriangle1( x2, y2, x1, y1, ay, col );
            }
            else if( x2 > x3 ) {
                // x2 > x3 > x1
                int ay = interp( x2, y2, x1, y1, x3 );
                fillTriangle1( x1, y1, x3, y3, ay, col );
                fillTriangle1( x2, y2, x3, y3, ay, col );
            }
            else {
                // x3 > x2 > x1
                int ay = interp( x3, y3, x1, y1, x2 );
                fillTriangle1( x1, y1, x2, y2, ay, col );
                fillTriangle1( x3, y3, x2, y2, ay, col );
            }
        }
    }

    int interp( int x1, int y1, int x2, int y2, int x ) {
        if( x1 == x2 ) {
            return y1;
        }
        if( x < x1 && x < x2 || x > x1 && x > x2 ) {
            System.out.print( "interp out of bounds\n" );
        }
        return (int)( y1 + ( (double)x - x1 ) * ( y2 - y1 ) / ( x2 - x1 ) );
    }

    void fillTriangle1( int x1, int y1, int x2, int y2, int y3, int col ) {
        // x2 == x3
        int dir = ( x1 > x2 ) ? -1 : 1;
        int x = x1;
        if( x < 0 ) {
            x = 0;
            if( x2 < 0 ) {
                return;
            }
        }
        if( x >= winSize.width ) {
            x = winSize.width - 1;
            if( x2 >= winSize.width ) {
                return;
            }
        }
        if( y2 > y3 ) {
            int q = y2;
            y2 = y3;
            y3 = q;
        }
        // y2 < y3
        while( x != x2 + dir ) {
            // XXX this could be speeded up
            int ya = interp( x1, y1, x2, y2, x );
            int yb = interp( x1, y1, x2, y3, x );
            if( ya < 0 ) {
                ya = 0;
            }
            if( yb >= winSize.height ) {
                yb = winSize.height - 1;
            }

            for( ; ya <= yb; ya++ ) {
                pixels[x + ya * winSize.width] = col;
            }
            x += dir;
            if( x < 0 || x >= winSize.width ) {
                return;
            }
        }
    }

    int abs( int x ) {
        return x < 0 ? -x : x;
    }

    void drawPlaneSource( int x1, int y1, int x2, int y2, float v, double w ) {
        if( y1 == y2 ) {
            if( x1 == windowOffsetX ) {
                x1 = 0;
            }
            if( x2 == windowOffsetX ) {
                x2 = 0;
            }
            if( x1 == windowOffsetX + windowWidth - 1 ) {
                x1 = gridSizeX - 1;
            }
            if( x2 == windowOffsetX + windowWidth - 1 ) {
                x2 = gridSizeX - 1;
            }
        }
        if( x1 == x2 ) {
            if( y1 == windowOffsetY ) {
                y1 = 0;
            }
            if( y2 == windowOffsetY ) {
                y2 = 0;
            }
            if( y1 == windowOffsetY + windowHeight - 1 ) {
                y1 = gridSizeY - 1;
            }
            if( y2 == windowOffsetY + windowHeight - 1 ) {
                y2 = gridSizeY - 1;
            }
        }

        /*double phase = 0;
      if (sourceChooser.getSelectedIndex() == SRC_1S1F_PLANE_PHASE)
      phase = (auxBar.getValue()-15)*3.8*freqBar.getValue()*freqMult;*/

        // need to draw a line from x1,y1 to x2,y2
        if( x1 == x2 && y1 == y2 ) {
            func[x1 + gw * y1] = v;
            funci[x1 + gw * y1] = 0;
        }
        else if( abs( y2 - y1 ) > abs( x2 - x1 ) ) {
            // y difference is greater, so we step along y's
            // from min to max y and calculate x for each step
            double sgn = sign( y2 - y1 );
            int x, y;
            for( y = y1; y != y2 + sgn; y += sgn ) {
                x = x1 + ( x2 - x1 ) * ( y - y1 ) / ( y2 - y1 );
                double ph = sgn * ( y - y1 ) / ( y2 - y1 );
                int gi = x + gw * y;
                func[gi] = setup.calcSourcePhase( ph, v, w );
                //(phase == 0) ? v :
                //  (float) (Math.sin(w+ph));
                funci[gi] = 0;
            }
        }
        else {
            // x difference is greater, so we step along x's
            // from min to max x and calculate y for each step
            double sgn = sign( x2 - x1 );
            int x, y;
            for( x = x1; x != x2 + sgn; x += sgn ) {
                y = y1 + ( y2 - y1 ) * ( x - x1 ) / ( x2 - x1 );
                double ph = sgn * ( x - x1 ) / ( x2 - x1 );
                int gi = x + gw * y;
                func[gi] = setup.calcSourcePhase( ph, v, w );
                // (phase == 0) ? v :
                // (float) (Math.sin(w+ph));
                funci[gi] = 0;
            }
        }
    }

    int sign( int x ) {
        return ( x < 0 ) ? -1 : ( x == 0 ) ? 0 : 1;
    }

    void edit( MouseEvent e ) {
        if( view3dCheck.getState() ) {
            return;
        }
        int x = e.getX();
        int y = e.getY();
        if( selectedSource != -1 ) {
            x = x * windowWidth / winSize.width;
            y = y * windowHeight / winSize.height;
            if( x >= 0 && y >= 0 && x < windowWidth && y < windowHeight ) {
                sources[selectedSource].x = x + windowOffsetX;
                sources[selectedSource].y = y + windowOffsetY;
            }
            return;
        }
        if( dragX == x && dragY == y ) {
            editFuncPoint( x, y );
        }
        else {
            // need to draw a line from old x,y to new x,y and
            // call editFuncPoint for each point on that line.  yuck.
            if( abs( y - dragY ) > abs( x - dragX ) ) {
                // y difference is greater, so we step along y's
                // from min to max y and calculate x for each step
                int x1 = ( y < dragY ) ? x : dragX;
                int y1 = ( y < dragY ) ? y : dragY;
                int x2 = ( y > dragY ) ? x : dragX;
                int y2 = ( y > dragY ) ? y : dragY;
                dragX = x;
                dragY = y;
                for( y = y1; y <= y2; y++ ) {
                    x = x1 + ( x2 - x1 ) * ( y - y1 ) / ( y2 - y1 );
                    editFuncPoint( x, y );
                }
            }
            else {
                // x difference is greater, so we step along x's
                // from min to max x and calculate y for each step
                int x1 = ( x < dragX ) ? x : dragX;
                int y1 = ( x < dragX ) ? y : dragY;
                int x2 = ( x > dragX ) ? x : dragX;
                int y2 = ( x > dragX ) ? y : dragY;
                dragX = x;
                dragY = y;
                for( x = x1; x <= x2; x++ ) {
                    y = y1 + ( y2 - y1 ) * ( x - x1 ) / ( x2 - x1 );
                    editFuncPoint( x, y );
                }
            }
        }
    }

    void editFuncPoint( int x, int y ) {
        int xp = x * windowWidth / winSize.width + windowOffsetX;
        int yp = y * windowHeight / winSize.height + windowOffsetY;
        int gi = xp + yp * gw;
        if( modeChooser.getSelectedIndex() == MODE_WALLS ) {
            if( !dragSet && !dragClear ) {
                dragClear = walls[gi];
                dragSet = !dragClear;
            }
            walls[gi] = dragSet;
            calcExceptions();
            func[gi] = funci[gi] = 0;
        }
        else if( modeChooser.getSelectedIndex() == MODE_MEDIUM ) {
            if( !dragSet && !dragClear ) {
                dragClear = medium[gi] > 0;
                dragSet = !dragClear;
            }
            medium[gi] = ( dragSet ) ? mediumMax : 0;
            calcExceptions();
        }
        else {
            if( !dragSet && !dragClear ) {
                dragClear = func[gi] > .1;
                dragSet = !dragClear;
            }
            func[gi] = ( dragSet ) ? 1 : -1;
            funci[gi] = 0;
        }
        cv.repaint( pause );
    }

    void selectSource( MouseEvent me ) {
        int x = me.getX();
        int y = me.getY();
        int i;
        for( i = 0; i != sourceCount; i++ ) {
            RippleFrame.OscSource src = sources[i];
            int sx = src.getScreenX();
            int sy = src.getScreenY();
            int r2 = ( sx - x ) * ( sx - x ) + ( sy - y ) * ( sy - y );
            if( sourceRadius * sourceRadius > r2 ) {
                selectedSource = i;
                return;
            }
        }
        selectedSource = -1;
    }

    void setDamping() {
        /*int i;
      double damper = dampingBar.getValue() * .00002;// was 5
      dampcoef = Math.exp(-damper);*/
        dampcoef = 1;
    }

    public void componentHidden( ComponentEvent e ) {
    }

    public void componentMoved( ComponentEvent e ) {
    }

    public void componentShown( ComponentEvent e ) {
        cv.repaint();
    }

    public void componentResized( ComponentEvent e ) {
        handleResize();
        cv.repaint( 100 );
    }

    public void actionPerformed( ActionEvent e ) {
        if( e.getSource() == blankButton ) {
            doBlank();
            cv.repaint();
        }
        if( e.getSource() == blankWallsButton ) {
            doBlankWalls();
            cv.repaint();
        }
        if( e.getSource() == borderButton ) {
            doBorder();
            cv.repaint();
        }
    }

    public void adjustmentValueChanged( AdjustmentEvent e ) {
        System.out.print( ( (Scrollbar)e.getSource() ).getValue() + "\n" );
        if( e.getSource() == resBar ) {
            setResolution();
            reinit();
        }
        if( e.getSource() == dampingBar ) {
            setDamping();
        }
        if( e.getSource() == brightnessBar ) {
            cv.repaint( pause );
        }
        if( e.getSource() == freqBar ) {
            setFreq();
        }
    }

    void setFreqBar( int x ) {
        freqBar.setValue( x );
        freqBarValue = x;
        freqTimeZero = 0;
    }

    void setFreq() {
        // adjust time zero to maintain continuity in the freq func
        // even though the frequency has changed.
        double oldfreq = freqBarValue * freqMult;
        freqBarValue = freqBar.getValue();
        double newfreq = freqBarValue * freqMult;
        double adj = newfreq - oldfreq;
        freqTimeZero = t - oldfreq * ( t - freqTimeZero ) / newfreq;
    }

    void setResolution() {
        windowWidth = windowHeight = resBar.getValue();
        windowOffsetX = windowOffsetY = 20;
        gridSizeX = windowWidth + windowOffsetX * 2;
        gridSizeY = windowHeight + windowOffsetY * 2;
        windowBottom = windowOffsetY + windowHeight - 1;
        windowRight = windowOffsetX + windowWidth - 1;
    }

    void setResolution( int x ) {
        resBar.setValue( x );
        setResolution();
        reinit();
    }

    public void mouseDragged( MouseEvent e ) {
        if( view3dCheck.getState() ) {
            view3dDrag( e );
        }
        if( !dragging ) {
            selectSource( e );
        }
        dragging = true;
        edit( e );
        cv.repaint( pause );
    }

    public void mouseMoved( MouseEvent e ) {
        if( dragging ) {
            return;
        }
        int x = e.getX();
        int y = e.getY();
        dragStartX = dragX = x;
        dragStartY = dragY = y;
        viewAngleDragStart = viewAngle;
        viewHeightDragStart = viewHeight;
        selectSource( e );
    }

    void view3dDrag( MouseEvent e ) {
        int x = e.getX();
        int y = e.getY();
        viewAngle = ( dragStartX - x ) / 40. + viewAngleDragStart;
        while( viewAngle < 0 ) {
            viewAngle += 2 * pi;
        }
        while( viewAngle >= 2 * pi ) {
            viewAngle -= 2 * pi;
        }
        viewAngleCos = Math.cos( viewAngle );
        viewAngleSin = Math.sin( viewAngle );
        viewHeight = ( dragStartY - y ) / 10. + viewHeightDragStart;

        /*viewZoom = (y-dragStartY)/40. + viewZoomDragStart;
      if (viewZoom < .1)
          viewZoom = .1;
          System.out.println(viewZoom);*/
        cv.repaint();
    }

    public void mouseClicked( MouseEvent e ) {
    }

    public void mouseEntered( MouseEvent e ) {
    }

    public void mouseExited( MouseEvent e ) {
    }

    public void mousePressed( MouseEvent e ) {
        mouseMoved( e );
        if( ( e.getModifiers() & MouseEvent.BUTTON1_MASK ) == 0 ) {
            return;
        }
        dragging = true;
        edit( e );
    }

    public void mouseReleased( MouseEvent e ) {
        if( ( e.getModifiers() & MouseEvent.BUTTON1_MASK ) == 0 ) {
            return;
        }
        dragging = false;
        dragSet = dragClear = false;
        cv.repaint();
    }

    public void itemStateChanged( ItemEvent e ) {
        if( e.getItemSelectable() == stoppedCheck ) {
            cv.repaint();
            return;
        }
        if( e.getItemSelectable() == sourceChooser ) {
            if( sourceChooser.getSelectedIndex() != sourceIndex ) {
                setSources();
            }
        }
        if( e.getItemSelectable() == setupChooser ) {
            doSetup();
        }
        if( e.getItemSelectable() == colorChooser ) {
            doColor();
        }
    }

    void doSetup() {
        t = 0;
        if( resBar.getValue() < 32 ) {
            setResolution( 32 );
        }
        doBlank();
        doBlankWalls();
        // don't use previous source positions, use defaults
        sourceCount = -1;
        sourceChooser.select( SRC_1S1F );
        dampingBar.setValue( 10 );
        setFreqBar( 5 );
        setBrightness( 10 );
        auxBar.setValue( 1 );
        fixedEndsCheck.setState( true );
        setup = (RippleFrame.Setup)
                setupList.elementAt( setupChooser.getSelectedIndex() );
        setup.select();
        setup.doSetupSources();
        calcExceptions();
        setDamping();
        //System.out.println("setup " + setupChooser.getSelectedIndex());
    }

    void setBrightness( int x ) {
        double m = x / 5.;
        m = ( Math.log( m ) + 5. ) * 100;
        brightnessBar.setValue( (int)m );
    }

    void doColor() {
        int cn = colorChooser.getSelectedIndex();
        wallColor = schemeColors[cn][0];
        posColor = schemeColors[cn][1];
        negColor = schemeColors[cn][2];
        zeroColor = schemeColors[cn][3];
        posMedColor = schemeColors[cn][4];
        negMedColor = schemeColors[cn][5];
        medColor = schemeColors[cn][6];
        sourceColor = schemeColors[cn][7];
    }

    void addDefaultColorScheme() {
        schemeColors[0][0] = new Color( 128, 128, 128 );
        schemeColors[0][1] = new Color( 0, 255, 0 );
        schemeColors[0][2] = new Color( 255, 0, 0 );
        schemeColors[0][3] = new Color( 0, 0, 0 );
        schemeColors[0][4] = new Color( 0, 255, 255 );
        schemeColors[0][5] = new Color( 255, 0, 255 );
        schemeColors[0][6] = new Color( 0, 0, 255 );
        schemeColors[0][7] = Color.blue;
        colorChooser.add( "Color Scheme 1" );
        colorChooser.hide();
    }

    void decodeColorScheme( int cn, String s ) {
        StringTokenizer st = new StringTokenizer( s );
        while( st.hasMoreTokens() ) {
            int i;
            for( i = 0; i != 8; i++ ) {
                schemeColors[cn][i] = Color.decode( st.nextToken() );
            }
        }
        colorChooser.add( "Color Scheme " + ( cn + 1 ) );
    }

    void addMedium() {
        int i, j;
        for( i = 0; i != gridSizeX; i++ ) {
            for( j = gridSizeY / 2; j != gridSizeY; j++ ) {
                medium[i + j * gw] = mediumMax;
            }
        }
    }

    void setSources() {
        sourceIndex = sourceChooser.getSelectedIndex();
        int oldSCount = sourceCount;
        boolean oldPlane = sourcePlane;
        sourceFreqCount = 1;
        sourcePlane = ( sourceChooser.getSelectedIndex() >= SRC_1S1F_PLANE &&
                        sourceChooser.getSelectedIndex() < SRC_6S1F );
        sourceMoving = false;
        sourceWaveform = SWF_SIN;
        sourceCount = 1;
        boolean phase = false;
        switch( sourceChooser.getSelectedIndex() ) {
            case 0:
                sourceCount = 0;
                break;
            case 2:
                sourceFreqCount = 2;
                break;
            case 3:
                sourceCount = 2;
                break;
            case 4:
                sourceCount = 2;
                sourceFreqCount = 2;
                break;
            case 5:
                sourceCount = 3;
                break;
            case 6:
                sourceCount = 4;
                break;
            case 7:
                sourceWaveform = SWF_SQUARE;
                break;
            case 8:
                sourceWaveform = SWF_PULSE;
                break;
            case 9:
                sourceMoving = true;
                break;
            case 11:
                sourceFreqCount = 2;
                break;
            case 12:
                sourceCount = 2;
                break;
            case 13:
                sourceCount = sourceFreqCount = 2;
                break;
            case 14:
                sourceWaveform = SWF_PULSE;
                break;
            case 15:
                phase = true;
                break;
            case 16:
                sourceCount = 6;
                break;
            case 17:
                sourceCount = 8;
                break;
            case 18:
                sourceCount = 10;
                break;
            case 19:
                sourceCount = 12;
                break;
            case 20:
                sourceCount = 16;
                break;
            case 21:
                sourceCount = 20;
                break;
        }
        if( sourceFreqCount >= 2 ) {
            auxFunction = AUX_FREQ;
            auxBar.setValue( freqBar.getValue() );
            if( sourceCount == 2 ) {
                auxLabel.setText( "Source 2 Frequency" );
            }
            else {
                auxLabel.setText( "2nd Frequency" );
            }
        }
        else if( sourceCount == 2 || sourceCount >= 4 || phase ) {
            auxFunction = AUX_PHASE;
            auxBar.setValue( 1 );
            auxLabel.setText( "Phase Difference" );
        }
        else if( sourceMoving ) {
            auxFunction = AUX_SPEED;
            auxBar.setValue( 7 );
            auxLabel.setText( "Source Speed" );
        }
        else {
            auxFunction = AUX_NONE;
            auxBar.hide();
            auxLabel.hide();
        }
        if( auxFunction != AUX_NONE ) {
            auxBar.show();
            auxLabel.show();
        }
        validate();

        if( sourcePlane ) {
            sourceCount *= 2;
            if( !( oldPlane && oldSCount == sourceCount ) ) {
                int x2 = windowOffsetX + windowWidth - 1;
                int y2 = windowOffsetY + windowHeight - 1;
                sources[0] = new RippleFrame.OscSource( windowOffsetX, windowOffsetY + 1 );
                sources[1] = new RippleFrame.OscSource( x2, windowOffsetY + 1 );
                sources[2] = new RippleFrame.OscSource( windowOffsetX, y2 );
                sources[3] = new RippleFrame.OscSource( x2, y2 );
            }
        }
        else if( !( oldSCount == sourceCount && !oldPlane ) ) {
            sources[0] = new RippleFrame.OscSource( gridSizeX / 2, windowOffsetY + 1 );
            sources[1] = new RippleFrame.OscSource( gridSizeX / 2, gridSizeY - windowOffsetY - 2 );
            sources[2] = new RippleFrame.OscSource( windowOffsetX + 1, gridSizeY / 2 );
            sources[3] = new RippleFrame.OscSource( gridSizeX - windowOffsetX - 2, gridSizeY / 2 );
            int i;
            for( i = 4; i < sourceCount; i++ ) {
                sources[i] = new RippleFrame.OscSource( windowOffsetX + 1 + i * 2, gridSizeY / 2 );
            }
        }
    }

    class OscSource {
        int x;
        int y;
        float v;

        OscSource( int xx, int yy ) {
            x = xx;
            y = yy;
        }

        int getScreenX() {
            return ( ( x - windowOffsetX ) * winSize.width + winSize.width / 2 )
                   / windowWidth;
        }

        int getScreenY() {
            return ( ( y - windowOffsetY ) * winSize.height + winSize.height / 2 )
                   / windowHeight;
        }
    }

    ;

    abstract class Setup {
        abstract String getName();

        abstract void select();

        void doSetupSources() {
            setSources();
        }

        void deselect() {
        }

        double sourceStrength() {
            return 1;
        }

        abstract RippleFrame.Setup createNext();

        void eachFrame() {
        }

        float calcSourcePhase( double ph, float v, double w ) {
            return v;
        }
    }

    ;

    class SingleSourceSetup extends RippleFrame.Setup {
        String getName() {
            return "Single Source";
        }

        void select() {
            setFreqBar( 15 );
            setBrightness( 27 );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.DoubleSourceSetup();
        }
    }

    class DoubleSourceSetup extends RippleFrame.Setup {
        String getName() {
            return "Two Sources";
        }

        void select() {
            setFreqBar( 15 );
            setBrightness( 19 );
        }

        void doSetupSources() {
            sourceChooser.select( SRC_2S1F );
            setSources();
            sources[0].y = gridSizeY / 2 - 8;
            sources[1].y = gridSizeY / 2 + 8;
            sources[0].x = sources[1].x = gridSizeX / 2;
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.QuadrupleSourceSetup();
        }
    }

    class QuadrupleSourceSetup extends RippleFrame.Setup {
        String getName() {
            return "Four Sources";
        }

        void select() {
            sourceChooser.select( SRC_4S1F );
            setFreqBar( 15 );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.SingleSlitSetup();
        }
    }

    class SingleSlitSetup extends RippleFrame.Setup {
        String getName() {
            return "Single Slit";
        }

        void select() {
            sourceChooser.select( SRC_1S1F_PLANE );
            int i;
            int x = gridSizeX / 2;
            int y = windowOffsetY + 4;
            for( i = 0; i != gridSizeX; i++ ) {
                setWall( i, y );
            }
            for( i = -4; i <= 4; i++ ) {
                setWall( x + i, y, false );
            }
            setBrightness( 7 );
            setFreqBar( 25 );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.DoubleSlitSetup();
        }
    }

    class DoubleSlitSetup extends RippleFrame.Setup {
        String getName() {
            return "Double Slit";
        }

        void select() {
            sourceChooser.select( SRC_1S1F_PLANE );
            int i;
            int x = gridSizeX / 2;
            int y = windowOffsetY + 4;
            for( i = 0; i != gridSizeX; i++ ) {
                setWall( i, y );
            }
            for( i = 0; i != 3; i++ ) {
                setWall( x - 5 - i, y, false );
                setWall( x + 5 + i, y, false );
            }
            brightnessBar.setValue( 488 );
            setFreqBar( 22 );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.TripleSlitSetup();
        }
    }

    class TripleSlitSetup extends RippleFrame.Setup {
        String getName() {
            return "Triple Slit";
        }

        void select() {
            sourceChooser.select( SRC_1S1F_PLANE );
            int i;
            int x = gridSizeX / 2;
            int y = windowOffsetY + 4;
            for( i = 0; i != gridSizeX; i++ ) {
                setWall( i, y );
            }
            for( i = -1; i <= 1; i++ ) {
                setWall( x - 12 + i, y, false );
                setWall( x + i, y, false );
                setWall( x + 12 + i, y, false );
            }
            setBrightness( 12 );
            setFreqBar( 22 );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.ObstacleSetup();
        }
    }

    class ObstacleSetup extends RippleFrame.Setup {
        String getName() {
            return "Obstacle";
        }

        void select() {
            int i;
            int x = gridSizeX / 2;
            int y = windowOffsetY + 6;
            for( i = -5; i <= 5; i++ ) {
                setWall( x + i, y );
            }
            setBrightness( 280 );
            setFreqBar( 20 );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.HalfPlaneSetup();
        }
    }

    class HalfPlaneSetup extends RippleFrame.Setup {
        String getName() {
            return "Half Plane";
        }

        void select() {
            sourceChooser.select( SRC_1S1F_PLANE );
            int x = windowOffsetX + windowWidth / 2;
            int i;
            for( i = windowWidth / 2; i < windowWidth; i++ ) {
                setWall( windowOffsetX + i, windowOffsetY + 3 );
            }
            setBrightness( 4 );
            setFreqBar( 25 );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.DipoleSourceSetup();
        }
    }

    class DipoleSourceSetup extends RippleFrame.Setup {
        String getName() {
            return "Dipole Source";
        }

        void doSetupSources() {
            sourceChooser.select( SRC_2S1F );
            setSources();
            sources[0].y = sources[1].y = gridSizeY / 2;
            sources[0].x = gridSizeX / 2 - 1;
            sources[1].x = gridSizeX / 2 + 1;
            auxBar.setValue( 29 );
            setFreqBar( 13 );
        }

        void select() {
            setBrightness( 33 );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.LateralQuadrupoleSetup();
        }
    }

    class LateralQuadrupoleSetup extends RippleFrame.Setup {
        String getName() {
            return "Lateral Quadrupole";
        }

        void doSetupSources() {
            sourceChooser.select( SRC_4S1F );
            setSources();
            sources[0].y = sources[2].y = gridSizeY / 2;
            sources[0].x = gridSizeX / 2 - 2;
            sources[2].x = gridSizeX / 2 + 2;
            sources[1].x = sources[3].x = gridSizeX / 2;
            sources[1].y = gridSizeY / 2 - 2;
            sources[3].y = gridSizeY / 2 + 2;
            setFreqBar( 13 );
            auxBar.setValue( 29 );
        }

        void select() {
            setBrightness( 33 );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.LinearQuadrupoleSetup();
        }
    }

    class LinearQuadrupoleSetup extends RippleFrame.Setup {
        String getName() {
            return "Linear Quadrupole";
        }

        void doSetupSources() {
            sourceChooser.select( SRC_4S1F );
            setSources();
            sources[0].y = sources[1].y = sources[2].y = sources[3].y =
                    gridSizeY / 2;
            sources[0].x = gridSizeX / 2 - 3;
            sources[2].x = gridSizeX / 2 + 3;
            sources[1].x = gridSizeX / 2 + 1;
            sources[3].x = gridSizeX / 2 - 1;
            auxBar.setValue( 29 );
            setFreqBar( 13 );
        }

        void select() {
            setBrightness( 33 );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.HexapoleSetup();
        }
    }

    class HexapoleSetup extends RippleFrame.Setup {
        String getName() {
            return "Hexapole";
        }

        void doSetupSources() {
            sourceChooser.select( SRC_6S1F );
            setSources();
            doMultipole( 6, 4 );
            setFreqBar( 22 );
            auxBar.setValue( 29 );
        }

        void doMultipole( int n, double dist ) {
            int i;
            for( i = 0; i != n; i++ ) {
                double xx = Math.round( dist * Math.cos( 2 * pi * i / n ) );
                double yy = Math.round( dist * Math.sin( 2 * pi * i / n ) );
                sources[i].x = gridSizeX / 2 + (int)xx;
                sources[i].y = gridSizeY / 2 + (int)yy;
            }
        }

        void select() {
            brightnessBar.setValue( 648 );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.OctupoleSetup();
        }
    }

    class OctupoleSetup extends RippleFrame.HexapoleSetup {
        String getName() {
            return "Octupole";
        }

        void doSetupSources() {
            sourceChooser.select( SRC_8S1F );
            setSources();
            doMultipole( 8, 4 );
            setFreqBar( 22 );
            auxBar.setValue( 29 );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.Multi12Setup();
        }
    }

    /*class Multi10Setup extends HexapoleSetup {
     String getName() { return "10-Pole"; }
     void doSetupSources() {
         sourceChooser.select(SRC_10S1F);
         setSources();
         doMultipole(10, 6);
         setFreqBar(22);
         auxBar.setValue(29);
     }
     Setup createNext() { return new Multi12Setup(); }
     }*/
    class Multi12Setup extends RippleFrame.HexapoleSetup {
        String getName() {
            return "12-Pole";
        }

        void doSetupSources() {
            sourceChooser.select( SRC_12S1F );
            setSources();
            doMultipole( 12, 6 );
            setFreqBar( 22 );
            auxBar.setValue( 29 );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.PlaneWaveSetup();
        }
    }

    /*class Multi16Setup extends HexapoleSetup {
     String getName() { return "16-Pole"; }
     void doSetupSources() {
         sourceChooser.select(SRC_16S1F);
         setSources();
         doMultipole(16, 8);
         setFreqBar(22);
         auxBar.setValue(29);
     }
     Setup createNext() { return new Multi20Setup(); }
     }
     class Multi20Setup extends HexapoleSetup {
     String getName() { return "20-Pole"; }
     void doSetupSources() {
         sourceChooser.select(SRC_20S1F);
         setSources();
         doMultipole(20, 10);
         setFreqBar(22);
         auxBar.setValue(29);
     }
     Setup createNext() { return new PlaneWaveSetup(); }
     }*/
    class PlaneWaveSetup extends RippleFrame.Setup {
        String getName() {
            return "Plane Wave";
        }

        void select() {
            sourceChooser.select( SRC_1S1F_PLANE );
            //setBrightness(7);
            setFreqBar( 15 );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.IntersectingPlaneWavesSetup();
        }
    }

    class IntersectingPlaneWavesSetup extends RippleFrame.Setup {
        String getName() {
            return "Intersecting Planes";
        }

        void select() {
            setBrightness( 4 );
            setFreqBar( 17 );
        }

        void doSetupSources() {
            sourceChooser.select( SRC_2S1F_PLANE );
            setSources();
            sources[0].y = sources[1].y = windowOffsetY;
            sources[0].x = windowOffsetX + 1;
            sources[2].x = sources[3].x = windowOffsetX;
            sources[2].y = windowOffsetY + 1;
            sources[3].y = windowOffsetY + windowHeight - 1;
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.PhasedArray1Setup();
        }
    }

    class PhasedArray1Setup extends RippleFrame.Setup {
        String getName() {
            return "Phased Array 1";
        }

        void select() {
            setBrightness( 5 );
            setFreqBar( 17 );
        }

        void doSetupSources() {
            sourceChooser.select( SRC_1S1F_PLANE_PHASE );
            setSources();
            sources[0].x = sources[1].x = gridSizeX / 2;
            sources[0].y = gridSizeY / 2 - 12;
            sources[1].y = gridSizeY / 2 + 12;
            auxBar.setValue( 5 );
        }

        float calcSourcePhase( double ph, float v, double w ) {
            ph *= ( auxBar.getValue() - 15 ) * 3.8 * freqBar.getValue() * freqMult;
            return (float)Math.sin( w + ph );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.PhasedArray2Setup();
        }
    }

    class PhasedArray2Setup extends RippleFrame.PhasedArray1Setup {
        String getName() {
            return "Phased Array 2";
        }

        void doSetupSources() {
            sourceChooser.select( SRC_1S1F_PLANE_PHASE );
            setSources();
            sources[0].x = sources[1].x = windowOffsetX + 1;
            sources[0].y = windowOffsetY + 1;
            sources[1].y = windowOffsetY + windowHeight - 2;
            auxBar.setValue( 5 );
        }

        float calcSourcePhase( double ph, float v, double w ) {
            double d = auxBar.getValue() * 2.5 / 30.;
            ph -= .5;
            ph = Math.sqrt( ph * ph + d * d );
            ph *= freqBar.getValue() * freqMult * 108;
            return (float)Math.sin( w + ph );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.PhasedArray3Setup();
        }
    }

    class PhasedArray3Setup extends RippleFrame.PhasedArray2Setup {
        String getName() {
            return "Phased Array 3";
        }

        float calcSourcePhase( double ph, float v, double w ) {
            double d = auxBar.getValue() * 2.5 / 30.;
            ph -= .5;
            ph = Math.sqrt( ph * ph + d * d );
            ph *= freqBar.getValue() * freqMult * 108;
            return (float)Math.sin( w - ph );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.DopplerSetup();
        }
    }

    class DopplerSetup extends RippleFrame.Setup {
        String getName() {
            return "Doppler Effect 1";
        }

        void select() {
            sourceChooser.select( SRC_1S1F_MOVING );
            setFreqBar( 13 );
            setBrightness( 20 );
            fixedEndsCheck.setState( false );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.Doppler2Setup();
        }
    }

    class Doppler2Setup extends RippleFrame.Setup {
        String getName() {
            return "Doppler Effect 2";
        }

        double wall;
        int dir;
        int waiting;

        void select() {
            wall = gridSizeY / 2.;
            dir = 1;
            waiting = 0;
            setFreqBar( 13 );
            setBrightness( 220 );
        }

        void doSetupSources() {
            sourceChooser.select( SRC_1S1F );
            setSources();
            sources[0].x = windowOffsetX + 1;
            sources[0].y = windowOffsetY + 1;
        }

        void eachFrame() {
            if( waiting > 0 ) {
                waiting--;
                return;
            }
            int w1 = (int)wall;
            wall += dir * .04;
            int w2 = (int)wall;
            if( w1 != w2 ) {
                int i;
                for( i = windowOffsetX + windowWidth / 3;
                     i <= gridSizeX - 1; i++ ) {
                    setWall( i, w1, false );
                    setWall( i, w2 );
                    int gi = i + w1 * gw;
                    if( w2 < w1 ) {
                        func[gi] = funci[gi] = 0;
                    }
                    else if( w1 > 1 ) {
                        func[gi] = func[gi - gw] / 2;
                        funci[gi] = funci[gi - gw] / 2;
                    }
                }
                int w3 = ( w2 - windowOffsetY ) / 2 + windowOffsetY;
                for( i = windowOffsetY; i < w3; i++ ) {
                    setWall( gridSizeX / 2, i );
                }
                setWall( gridSizeX / 2, i, false );
                calcExceptions();
            }
            if( w2 == windowOffsetY + windowHeight / 4 ||
                w2 == windowOffsetY + windowHeight * 3 / 4 ) {
                dir = -dir;
                waiting = 1000;
            }
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.SonicBoomSetup();
        }
    }

    class SonicBoomSetup extends RippleFrame.Setup {
        String getName() {
            return "Sonic Boom";
        }

        void select() {
            sourceChooser.select( SRC_1S1F_MOVING );
            setFreqBar( 13 );
            setBrightness( 20 );
            fixedEndsCheck.setState( false );
        }

        void doSetupSources() {
            setSources();
            auxBar.setValue( 30 );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.BigModeSetup();
        }
    }

    void setupMode( int x, int y, int sx, int sy, int nx, int ny ) {
        int i, j;
        for( i = 0; i != sx; i++ ) {
            for( j = 0; j != sy; j++ ) {
                int gi = i + x + gw * ( j + y );
                func[gi] = (float)
                        ( Math.sin( pi * nx * ( i + 1 ) / ( sx + 1 ) ) *
                          Math.sin( pi * ny * ( j + 1 ) / ( sy + 1 ) ) );
                funci[gi] = 0;
            }
        }
    }

    void setupAcousticMode( int x, int y, int sx, int sy, int nx, int ny ) {
        int i, j;
        for( i = 0; i != sx; i++ ) {
            for( j = 0; j != sy; j++ ) {
                int gi = i + x + gw * ( j + y );
                func[gi] = (float)
                        ( Math.cos( pi * nx * i / ( sx - 1 ) ) *
                          Math.cos( pi * ny * j / ( sy - 1 ) ) );
                funci[gi] = 0;
            }
        }
    }

    class BigModeSetup extends RippleFrame.Setup {
        String getName() {
            return "Big 1x1 Mode";
        }

        void select() {
            sourceChooser.select( SRC_NONE );
            int i;
            int n = windowWidth * 3 / 4;
            int x = windowOffsetX + windowWidth / 2 - n / 2;
            int y = windowOffsetY + windowHeight / 2 - n / 2;
            for( i = 0; i != n + 2; i++ ) {
                setWall( x + i - 1, y - 1 );
                setWall( x + i - 1, y + n );
                setWall( x - 1, y + i - 1 );
                setWall( x + n, y + i - 1 );
            }
            setupMode( x, y, n, n, 1, 1 );
            dampingBar.setValue( 1 );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.OneByOneModesSetup();
        }
    }

    class OneByOneModesSetup extends RippleFrame.Setup {
        String getName() {
            return "1x1 Modes";
        }

        void select() {
            sourceChooser.select( SRC_NONE );
            int i, j;
            int y = 1;
            int ny = 5;
            while( y + ny < windowHeight ) {
                int nx = ( ( y + ny ) * ( windowWidth - 8 ) / windowHeight ) + 6;
                int y1 = y + windowOffsetY;
                int x1 = windowOffsetX + 1;
                for( i = 0; i != nx + 2; i++ ) {
                    setWall( x1 + i - 1, y1 - 1 );
                    setWall( x1 + i - 1, y1 + ny );
                }
                for( j = 0; j != ny + 2; j++ ) {
                    setWall( x1 - 1, y1 + j - 1 );
                    setWall( x1 + nx, y1 + j - 1 );
                }
                setupMode( x1, y1, nx, ny, 1, 1 );
                y += ny + 1;
            }
            dampingBar.setValue( 1 );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.OneByNModesSetup();
        }
    }

    class OneByNModesSetup extends RippleFrame.Setup {
        String getName() {
            return "1xN Modes";
        }

        void select() {
            sourceChooser.select( SRC_NONE );
            int i, j;
            int y = 1;
            int ny = 5;
            int nx = windowWidth - 2;
            int mode = 1;
            while( y + ny < windowHeight ) {
                int y1 = y + windowOffsetY;
                int x1 = windowOffsetX + 1;
                for( i = 0; i != nx + 2; i++ ) {
                    setWall( x1 + i - 1, y1 - 1 );
                    setWall( x1 + i - 1, y1 + ny );
                }
                for( j = 0; j != ny + 2; j++ ) {
                    setWall( x1 - 1, y1 + j - 1 );
                    setWall( x1 + nx, y1 + j - 1 );
                }
                setupMode( x1, y1, nx, ny, mode, 1 );
                y += ny + 1;
                mode++;
            }
            dampingBar.setValue( 1 );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.NByNModesSetup();
        }
    }

    class NByNModesSetup extends RippleFrame.Setup {
        String getName() {
            return "NxN Modes";
        }

        void select() {
            sourceChooser.select( SRC_NONE );
            int i, j;
            int y = 1;
            int modex, modey;
            int maxmode = 3;
            if( resBar.getValue() >= 70 ) {
                maxmode++;
            }
            if( resBar.getValue() >= 100 ) {
                maxmode++;
            }
            int ny = windowHeight / maxmode - 2;
            int nx = windowWidth / maxmode - 2;
            for( modex = 1; modex <= maxmode; modex++ ) {
                for( modey = 1; modey <= maxmode; modey++ ) {
                    int x1 = windowOffsetX + 1 + ( ny + 1 ) * ( modey - 1 );
                    int y1 = windowOffsetY + 1 + ( nx + 1 ) * ( modex - 1 );
                    for( i = 0; i != nx + 2; i++ ) {
                        setWall( x1 + i - 1, y1 - 1 );
                        setWall( x1 + i - 1, y1 + ny );
                    }
                    for( j = 0; j != ny + 2; j++ ) {
                        setWall( x1 - 1, y1 + j - 1 );
                        setWall( x1 + nx, y1 + j - 1 );
                    }
                    setupMode( x1, y1, nx, ny, modex, modey );
                }
            }
            dampingBar.setValue( 1 );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.OneByNModeCombosSetup();
        }
    }

    class OneByNModeCombosSetup extends RippleFrame.Setup {
        String getName() {
            return "1xN Mode Combos";
        }

        void select() {
            sourceChooser.select( SRC_NONE );
            int i, j;
            int y = 1;
            int ny = 5;
            int nx = windowWidth - 2;
            while( y + ny < windowHeight ) {
                int mode1 = getrand( 12 ) + 1;
                int mode2;
                do {
                    mode2 = getrand( 12 ) + 1;
                }
                while( mode1 == mode2 );
                int y1 = y + windowOffsetY;
                int x1 = windowOffsetX + 1;
                for( i = 0; i != nx + 2; i++ ) {
                    setWall( x1 + i - 1, y1 - 1 );
                    setWall( x1 + i - 1, y1 + ny );
                }
                for( j = 0; j != ny + 2; j++ ) {
                    setWall( x1 - 1, y1 + j - 1 );
                    setWall( x1 + nx, y1 + j - 1 );
                }
                for( i = 0; i != nx; i++ ) {
                    for( j = 0; j != ny; j++ ) {
                        int gi = i + x1 + gw * ( j + y1 );
                        func[gi] = (float)
                                ( Math.sin( mode1 * pi * ( i + 1 ) / ( nx + 1 ) ) *
                                  Math.sin( pi * ( j + 1 ) / ( ny + 1 ) ) * .5 +
                                                                               Math.sin( mode2 * pi * ( i + 1 ) / ( nx + 1 ) ) *
                                                                               Math.sin( pi * ( j + 1 ) / ( ny + 1 ) ) * .5 );
                        funci[gi] = 0;
                    }
                }
                y += ny + 1;
            }
            dampingBar.setValue( 1 );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.NByNModeCombosSetup();
        }
    }

    class NByNModeCombosSetup extends RippleFrame.Setup {
        String getName() {
            return "NxN Mode Combos";
        }

        void select() {
            sourceChooser.select( SRC_NONE );
            int i, j;
            int y = 1;
            int maxmode = 3;
            if( resBar.getValue() >= 70 ) {
                maxmode++;
            }
            if( resBar.getValue() >= 100 ) {
                maxmode++;
            }
            int ny = windowHeight / maxmode - 2;
            int nx = windowWidth / maxmode - 2;
            int gx, gy;
            for( gx = 1; gx <= maxmode; gx++ ) {
                for( gy = 1; gy <= maxmode; gy++ ) {
                    int mode1x = getrand( 4 ) + 1;
                    int mode1y = getrand( 4 ) + 1;
                    int mode2x, mode2y;
                    do {
                        mode2x = getrand( 4 ) + 1;
                        mode2y = getrand( 4 ) + 1;
                    } while( mode1x == mode2x && mode1y == mode2y );
                    int x1 = windowOffsetX + 1 + ( ny + 1 ) * ( gx - 1 );
                    int y1 = windowOffsetY + 1 + ( nx + 1 ) * ( gy - 1 );
                    for( i = 0; i != nx + 2; i++ ) {
                        setWall( x1 + i - 1, y1 - 1 );
                        setWall( x1 + i - 1, y1 + ny );
                    }
                    for( j = 0; j != ny + 2; j++ ) {
                        setWall( x1 - 1, y1 + j - 1 );
                        setWall( x1 + nx, y1 + j - 1 );
                    }
                    for( i = 0; i != nx; i++ ) {
                        for( j = 0; j != ny; j++ ) {
                            int gi = i + x1 + gw * ( j + y1 );
                            func[gi] = (float)
                                    ( Math.sin( mode1x * pi * ( i + 1 ) / ( nx + 1 ) ) *
                                      Math.sin( mode1y * pi * ( j + 1 ) / ( ny + 1 ) ) * .5 +
                                                                                            Math.sin( mode2x * pi * ( i + 1 ) / ( nx + 1 ) ) *
                                                                                            Math.sin( mode2y * pi * ( j + 1 ) / ( ny + 1 ) ) * .5 );
                            funci[gi] = 0;
                        }
                    }
                }
            }
            dampingBar.setValue( 1 );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.ZeroByOneModesSetup();
        }
    }

    class ZeroByOneModesSetup extends RippleFrame.Setup {
        String getName() {
            return "0x1 Acoustic Modes";
        }

        void select() {
            fixedEndsCheck.setState( false );
            sourceChooser.select( SRC_NONE );
            int i, j;
            int y = 1;
            int ny = 5;
            while( y + ny < windowHeight ) {
                int nx = ( ( y + ny ) * ( windowWidth - 8 ) / windowHeight ) + 6;
                int y1 = y + windowOffsetY;
                int x1 = windowOffsetX + 1;
                for( i = 0; i != nx + 2; i++ ) {
                    setWall( x1 + i - 1, y1 - 1 );
                    setWall( x1 + i - 1, y1 + ny );
                }
                for( j = 0; j != ny + 2; j++ ) {
                    setWall( x1 - 1, y1 + j - 1 );
                    setWall( x1 + nx, y1 + j - 1 );
                }
                setupAcousticMode( x1, y1, nx, ny, 1, 0 );
                y += ny + 1;
            }
            dampingBar.setValue( 1 );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.ZeroByNModesSetup();
        }
    }

    class ZeroByNModesSetup extends RippleFrame.Setup {
        String getName() {
            return "0xN Acoustic Modes";
        }

        void select() {
            fixedEndsCheck.setState( false );
            sourceChooser.select( SRC_NONE );
            int i, j;
            int y = 1;
            int ny = 5;
            int nx = windowWidth - 2;
            int mode = 1;
            while( y + ny < windowHeight ) {
                int y1 = y + windowOffsetY;
                int x1 = windowOffsetX + 1;
                for( i = 0; i != nx + 2; i++ ) {
                    setWall( x1 + i - 1, y1 - 1 );
                    setWall( x1 + i - 1, y1 + ny );
                }
                for( j = 0; j != ny + 2; j++ ) {
                    setWall( x1 - 1, y1 + j - 1 );
                    setWall( x1 + nx, y1 + j - 1 );
                }
                setupAcousticMode( x1, y1, nx, ny, mode, 0 );
                y += ny + 1;
                mode++;
            }
            dampingBar.setValue( 1 );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.NByNAcoModesSetup();
        }
    }

    class NByNAcoModesSetup extends RippleFrame.Setup {
        String getName() {
            return "NxN Acoustic Modes";
        }

        void select() {
            fixedEndsCheck.setState( false );
            sourceChooser.select( SRC_NONE );
            int i, j;
            int y = 1;
            int modex, modey;
            int maxmode = 2;
            if( resBar.getValue() >= 70 ) {
                maxmode++;
            }
            // weird things start to happen if maxmode goes higher than 4
            int ny = windowHeight / ( maxmode + 1 ) - 2;
            int nx = windowWidth / ( maxmode + 1 ) - 2;
            for( modex = 0; modex <= maxmode; modex++ ) {
                for( modey = 0; modey <= maxmode; modey++ ) {
                    int x1 = windowOffsetX + 1 + ( ny + 1 ) * ( modey );
                    int y1 = windowOffsetY + 1 + ( nx + 1 ) * ( modex );
                    for( i = 0; i != nx + 2; i++ ) {
                        setWall( x1 + i - 1, y1 - 1 );
                        setWall( x1 + i - 1, y1 + ny );
                    }
                    for( j = 0; j != ny + 2; j++ ) {
                        setWall( x1 - 1, y1 + j - 1 );
                        setWall( x1 + nx, y1 + j - 1 );
                    }
                    setupAcousticMode( x1, y1, nx, ny, modex, modey );
                }
            }
            dampingBar.setValue( 1 );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.CoupledCavitiesSetup();
        }
    }

    class CoupledCavitiesSetup extends RippleFrame.Setup {
        String getName() {
            return "Coupled Cavities";
        }

        void select() {
            fixedEndsCheck.setState( false );
            sourceChooser.select( SRC_NONE );
            int i, j;
            int y = 1;
            int ny = 5;
            while( y + ny < windowHeight ) {
                int nx = 35;
                int y1 = y + windowOffsetY;
                int x1 = windowOffsetX + 1;
                for( i = 0; i != nx + 2; i++ ) {
                    setWall( x1 + i - 1, y1 - 1 );
                    setWall( x1 + i - 1, y1 + ny );
                }
                for( j = 0; j != ny + 2; j++ ) {
                    setWall( x1 - 1, y1 + j - 1 );
                    setWall( x1 + nx, y1 + j - 1 );
                }
                for( j = 0; j != 2; j++ ) {
                    setWall( x1 + nx / 2, y1 + j );
                    setWall( x1 + nx / 2, y1 + 4 - j );
                }
                setupAcousticMode( x1, y1, nx / 2, ny, 1, 0 );
                y += ny + 3;
            }
            dampingBar.setValue( 1 );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.BeatsSetup();
        }
    }

    class BeatsSetup extends RippleFrame.Setup {
        String getName() {
            return "Beats";
        }

        void doSetupSources() {
            sourceChooser.select( SRC_2S2F );
            setSources();
            auxBar.setValue( 24 );
            sources[0].y = sources[1].y = gridSizeY / 2;
            sources[0].x = gridSizeX / 2 - 2;
            sources[1].x = gridSizeX / 2 + 2;
        }

        void select() {
            setBrightness( 25 );
            setFreqBar( 18 );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.SlowMediumSetup();
        }
    }

    class SlowMediumSetup extends RippleFrame.Setup {
        String getName() {
            return "Slow Medium";
        }

        void select() {
            addMedium();
            setFreqBar( 10 );
            setBrightness( 33 );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.RefractionSetup();
        }
    }

    class RefractionSetup extends RippleFrame.Setup {
        String getName() {
            return "Refraction";
        }

        void doSetupSources() {
            sourceChooser.select( SRC_1S1F_PLANE_PULSE );
            setSources();
            sources[0].x = windowOffsetX;
            sources[0].y = windowOffsetY + windowHeight / 4;
            sources[1].x = windowOffsetX + windowWidth / 3;
            sources[1].y = windowOffsetY;
            addMedium();
            setFreqBar( 1 );
            setBrightness( 33 );
        }

        void select() {
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.InternalReflectionSetup();
        }
    }

    class InternalReflectionSetup extends RippleFrame.Setup {
        String getName() {
            return "Internal Reflection";
        }

        void doSetupSources() {
            sourceChooser.select( SRC_1S1F_PLANE_PULSE );
            setSources();
            sources[0].x = windowOffsetX;
            sources[0].y = windowOffsetY + windowHeight * 2 / 3;
            sources[1].x = windowOffsetX + windowWidth / 3;
            sources[1].y = windowOffsetY + windowHeight - 1;
            addMedium();
            setFreqBar( 1 );
            setBrightness( 33 );
        }

        void select() {
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.CoatingSetup();
        }
    }

    class CoatingSetup extends RippleFrame.Setup {
        String getName() {
            return "Anti-Reflective Coating";
        }

        void select() {
            sourceChooser.select( SRC_1S1F );
            addMedium();
            int i, j;
            // v2/c2 = 1-(mediumMaxIndex/mediumMax)*medium);
            // n = sqrt(v2/c2)
            double nmax = Math.sqrt( 1 - mediumMaxIndex );
            double nroot = Math.sqrt( nmax );
            int mm = (int)( ( 1 - nmax ) * mediumMax / mediumMaxIndex );
            for( i = 0; i != gridSizeX; i++ ) {
                for( j = gridSizeY / 2 - 4; j != gridSizeY / 2; j++ ) {
                    medium[i + j * gw] = mm;
                }
            }
            setFreqBar( 6 );
            setBrightness( 28 );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.ZonePlateEvenSetup();
        }
    }

    class ZonePlateEvenSetup extends RippleFrame.Setup {
        int zoneq;

        ZonePlateEvenSetup() {
            zoneq = 1;
        }

        String getName() {
            return "Zone Plate (Even)";
        }

        void doSetupSources() {
        }

        void select() {
            sourceChooser.select( SRC_1S1F_PLANE );
            setSources();
            if( resBar.getValue() < 42 ) {
                setResolution( 42 );
            }
            int i;
            // wavelength by default = 25, we want it to be 6
            int freq = 30;
            setFreqBar( freq );
            double halfwave = 25. / ( freq * 2 / 5 );
            int y = sources[0].y + 1;
            int dy = windowOffsetY + windowHeight / 2 - y;
            int dy2 = dy * dy;
            int cx = gridSizeX / 2;
            for( i = 0; i != windowWidth; i++ ) {
                int x = windowOffsetX + i;
                int dx = cx - x;
                double dist = Math.sqrt( dx * dx + dy * dy );
                dist = ( dist - dy );
                int zone = (int)( dist / halfwave );
                setWall( x, y, ( ( zone & 1 ) == zoneq ) );
                setWall( windowOffsetX, y );
                setWall( windowOffsetX + windowWidth - 1, y );
            }
            setBrightness( zoneq == 1 ? 4 : 7 );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.ZonePlateOddSetup();
        }
    }

    class ZonePlateOddSetup extends RippleFrame.ZonePlateEvenSetup {
        ZonePlateOddSetup() {
            zoneq = 0;
        }

        String getName() {
            return "Zone Plate (Odd)";
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.CircleSetup();
        }
    }

    class CircleSetup extends RippleFrame.Setup {
        CircleSetup() {
            circle = true;
        }

        boolean circle;

        String getName() {
            return "Circle";
        }

        void doSetupSources() {
        }

        void select() {
            int i;
            int dx = windowWidth / 2 - 2;
            double a2 = dx * dx;
            double b2 = a2 / 2;
            if( circle ) {
                b2 = a2;
            }
            int cx = windowWidth / 2 + windowOffsetX;
            int cy = windowHeight / 2 + windowOffsetY;
            int ly = -1;
            for( i = 0; i <= dx; i++ ) {
                double y = Math.sqrt( ( 1 - i * i / a2 ) * b2 );
                int yi = (int)( y + 1.5 );
                if( i == dx ) {
                    yi = 0;
                }
                if( ly == -1 ) {
                    ly = yi;
                }
                for( ; ly >= yi; ly-- ) {
                    setWall( cx + i, cy + ly );
                    setWall( cx - i, cy + ly );
                    setWall( cx + i, cy - ly );
                    setWall( cx - i, cy - ly );
                    //setWall(cx-ly, cx+i);
                    //setWall(cx+ly, cx+i);
                }
                ly = yi;
            }
            int c = (int)( Math.sqrt( a2 - b2 ) );
            //walls[cx+c][cy] = walls[cx-c][cy] = true;
            //walls[cx][cy+c] = true;
            sourceChooser.select( SRC_1S1F_PULSE );
            setSources();
            sources[0].x = cx - c;
            sources[0].y = cy;
            setFreqBar( 1 );
            setBrightness( 16 );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.EllipseSetup();
        }
    }

    class EllipseSetup extends RippleFrame.CircleSetup {
        EllipseSetup() {
            circle = false;
        }

        String getName() {
            return "Ellipse";
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.ResonantCavitiesSetup();
        }
    }

    class ResonantCavitiesSetup extends RippleFrame.Setup {
        String getName() {
            return "Resonant Cavities 1";
        }

        void select() {
            sourceChooser.select( SRC_1S1F_PLANE );
            int i, j;
            int x = 1;
            int nx = 5;
            int y1 = windowOffsetY + 11;
            while( x + nx < windowWidth ) {
                int ny = ( ( x + nx ) * ( windowHeight - 18 ) / windowWidth ) + 6;
                int x1 = x + windowOffsetX;
                for( i = 0; i != ny + 2; i++ ) {
                    setWall( x1 - 1, y1 + i - 1 );
                    setWall( x1 + nx, y1 + i - 1 );
                }
                for( j = 0; j != nx + 2; j++ ) {
                    setWall( x1 + j - 1, y1 - 1 );
                    setWall( x1 + j - 1, y1 + ny );
                }
                setWall( x1 + nx / 2, y1 - 1, false );
                x += nx + 1;
            }
            for( ; x < windowWidth; x++ ) {
                setWall( x + windowOffsetX, y1 - 1 );
            }
            setBrightness( 30 );
            setFreqBar( 15 );
        }

        double sourceStrength() {
            return .1;
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.ResonantCavities2Setup();
        }
    }

    class ResonantCavities2Setup extends RippleFrame.Setup {
        String getName() {
            return "Resonant Cavities 2";
        }

        void select() {
            sourceChooser.select( SRC_1S1F_PLANE );
            int i, j;
            int x = 1;
            int nx = 5;
            int y1 = windowOffsetY + 11;
            int ny = 5;
            while( x + nx < windowWidth ) {
                int x1 = x + windowOffsetX;
                for( i = 0; i != ny + 2; i++ ) {
                    setWall( x1 - 1, y1 + i - 1 );
                    setWall( x1 + nx, y1 + i - 1 );
                }
                for( j = 0; j != nx + 2; j++ ) {
                    setWall( x1 + j - 1, y1 + ny );
                }
                x += nx + 1;
                ny++;
            }
            for( ; x < windowWidth; x++ ) {
                setWall( x + windowOffsetX, y1 - 1 );
            }
            setBrightness( 30 );
            setFreqBar( 16 );
        }

        double sourceStrength() {
            return .03;
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.RoomResonanceSetup();
        }
    }

    class RoomResonanceSetup extends RippleFrame.Setup {
        String getName() {
            return "Room Resonance";
        }

        void select() {
            sourceChooser.select( SRC_4S1F );
            setSources();
            int i, j;
            int modex, modey;
            int ny = 17;
            int nx = 17;
            for( modex = 1; modex <= 2; modex++ ) {
                for( modey = 1; modey <= 2; modey++ ) {
                    int x1 = windowOffsetX + 1 + ( ny + 1 ) * ( modey - 1 );
                    int y1 = windowOffsetY + 1 + ( nx + 1 ) * ( modex - 1 );
                    for( i = 0; i != nx + 2; i++ ) {
                        setWall( x1 + i - 1, y1 - 1 );
                        setWall( x1 + i - 1, y1 + ny );
                    }
                    for( j = 0; j != ny + 2; j++ ) {
                        setWall( x1 - 1, y1 + j - 1 );
                        setWall( x1 + nx, y1 + j - 1 );
                    }
                }
            }
            sources[0].x = sources[2].x = windowOffsetX + 2;
            sources[0].y = sources[1].y = windowOffsetY + 2;
            sources[1].x = sources[3].x = windowOffsetX + 1 + nx + ( nx + 1 ) / 2;
            sources[2].y = sources[3].y = windowOffsetY + 1 + ny + ( ny + 1 ) / 2;
            fixedEndsCheck.setState( false );
            dampingBar.setValue( 10 );
            setBrightness( 3 );
        }

        void doSetupSources() {
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.Waveguides1Setup();
        }
    }

    class Waveguides1Setup extends RippleFrame.Setup {
        String getName() {
            return "Waveguides 1";
        }

        void select() {
            sourceChooser.select( SRC_1S1F_PLANE );
            int i, j;
            int x = 1;
            int nx = 3;
            int y1 = windowOffsetY + 3;
            int ny = windowHeight - 2;
            while( x + nx < windowWidth ) {
                int x1 = x + windowOffsetX;
                for( i = 0; i != ny; i++ ) {
                    setWall( x1 - 1, y1 + i - 1 );
                    setWall( x1 + nx, y1 + i - 1 );
                }
                nx++;
                x += nx;
            }
            for( ; x < windowWidth; x++ ) {
                setWall( x + windowOffsetX, y1 - 1 );
            }
            setBrightness( 6 );
            setFreqBar( 14 );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.Waveguides2Setup();
        }
    }

    class Waveguides2Setup extends RippleFrame.Waveguides1Setup {
        String getName() {
            return "Waveguides 2";
        }

        void select() {
            super.select();
            setFreqBar( 8 );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.Waveguides3Setup();
        }
    }

    class Waveguides3Setup extends RippleFrame.Setup {
        String getName() {
            return "Waveguides 3";
        }

        void select() {
            sourceChooser.select( SRC_1S1F_PLANE );
            int i, j;
            int x = 1;
            int nx = 8;
            int y1 = windowOffsetY + 3;
            int ny = windowHeight - 2;
            for( x = 1; x < windowWidth; x++ ) {
                setWall( x + windowOffsetX, y1 - 1 );
            }
            x = 1;
            j = 0;
            while( x + nx < windowWidth && j < nx ) {
                int x1 = x + windowOffsetX;
                for( i = 0; i != ny; i++ ) {
                    setWall( x1 - 1, y1 + i - 1 );
                    setWall( x1 + nx, y1 + i - 1 );
                }
                setWall( x1 + j++, y1 - 1, false );
                x += nx + 1;
            }
            setBrightness( 89 );
            setFreqBar( 16 );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.Waveguides4Setup();
        }
    }

    class Waveguides4Setup extends RippleFrame.Waveguides3Setup {
        String getName() {
            return "Waveguides 4";
        }

        void select() {
            super.select();
            setBrightness( 29 );
            setFreqBar( 20 );
            fixedEndsCheck.setState( false );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.Waveguides5Setup();
        }
    }

    class Waveguides5Setup extends RippleFrame.Waveguides3Setup {
        String getName() {
            return "Waveguides 5";
        }

        void select() {
            sourceChooser.select( SRC_1S1F_PLANE );
            int i;
            int x = 1;
            int nx = 8;
            int y1 = windowOffsetY + 2;
            int ny = windowHeight - 1;
            x = 1;
            while( x + nx < windowWidth ) {
                int x1 = x + windowOffsetX;
                for( i = 0; i != ny; i++ ) {
                    setWall( x1 - 1, y1 + i - 1 );
                    setWall( x1 + nx, y1 + i - 1 );
                }
                x += nx + 1;
            }
            setBrightness( 9 );
            setFreqBar( 22 );
        }

        void eachFrame() {
            int y = windowOffsetY + 1;
            int nx = 8;
            int x = 1;
            int g = 1;
            while( x + nx < windowWidth ) {
                int x1 = x + windowOffsetX;
                int j;
                int n1 = 1;
                int n2 = 1;
                switch( g ) {
                    case 1:
                        n1 = n2 = 1;
                        break;
                    case 2:
                        n1 = n2 = 2;
                        break;
                    case 3:
                        n1 = 1;
                        n2 = 2;
                        break;
                    case 4:
                        n1 = n2 = 3;
                        break;
                    case 5:
                        n1 = 1;
                        n2 = 3;
                        break;
                    case 6:
                        n1 = 2;
                        n2 = 3;
                        break;
                    default:
                        n1 = n2 = 0;
                        break;
                }
                for( j = 0; j != nx; j++ ) {
                    func[x1 + j + gw * y] *= .5 *
                                             ( Math.sin( pi * n1 * ( j + 1 ) / ( nx + 1 ) ) +
                                               Math.sin( pi * n2 * ( j + 1 ) / ( nx + 1 ) ) );
                }
                x += nx + 1;
                g++;
            }
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.ParabolicMirror1Setup();
        }
    }

    /*class HornSetup extends Setup {
     String getName() { return "Horn"; }
     void select() {
         if (resBar.getValue() < 76)
         setResolution(76);
         fixedEndsCheck.setState(false);
         setFreqBar(3);
         int i;
         int cx = windowOffsetX+windowWidth/2;
         int yy = windowHeight/2;
         int oj = 0;
         double lmult = Math.log(windowWidth/2-2)*1.2;
         System.out.println(yy + " " + lmult);
         for (i = 0; i < yy; i++) {
         int j = (int) (Math.exp(i*lmult/yy));
         System.out.println(i + " " +j);
         //int j = i*((windowWidth-5)/2)/yy;
         while (oj <= j) {
             walls[cx+oj][windowOffsetY+i] =
             walls[cx-oj][windowOffsetY+i] = true;
             oj++;
         }
         oj = j;
         }
         setBrightness(12);
     }
     Setup createNext() { return new ParabolicMirror1Setup(); }
     }*/
    class ParabolicMirror1Setup extends RippleFrame.Setup {
        String getName() {
            return "Parabolic Mirror 1";
        }

        void select() {
            if( resBar.getValue() < 50 ) {
                setResolution( 50 );
            }
            int i;
            int cx = windowWidth / 2 + windowOffsetX;
            int lx = 0;
            int dy = windowHeight / 2;
            int cy = windowHeight + windowOffsetY - 2;
            int dx = windowWidth / 2 - 2;
            double c = dx * dx * .5 / dy;
            if( c > 20 ) {
                c = 20;
            }
            for( i = 0; i <= dy; i++ ) {
                double x = Math.sqrt( 2 * c * i );
                int xi = (int)( x + 1.5 );
                for( ; lx <= xi; lx++ ) {
                    setWall( cx - lx, cy - i );
                    setWall( cx + lx, cy - i );
                }
                lx = xi;
            }
            setSources();
            sources[0].x = cx;
            sources[0].y = (int)( cy - 1 - c / 2 );
            setBrightness( 18 );
        }

        void doSetupSources() {
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.ParabolicMirror2Setup();
        }
    }

    class ParabolicMirror2Setup extends RippleFrame.ParabolicMirror1Setup {
        String getName() {
            return "Parabolic Mirror 2";
        }

        void doSetupSources() {
            sourceChooser.select( SRC_1S1F_PLANE );
            brightnessBar.setValue( 370 );
            setFreqBar( 15 );
            setSources();
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.SoundDuctSetup();
        }
    }

    class SoundDuctSetup extends RippleFrame.Setup {
        String getName() {
            return "Sound Duct";
        }

        void select() {
            sourceChooser.select( SRC_1S1F_PULSE );
            fixedEndsCheck.setState( false );
            int i;
            int cx = windowOffsetX + windowWidth / 2;
            for( i = 0; i != windowHeight - 12; i++ ) {
                setWall( cx - 3, i + windowOffsetY + 6 );
                setWall( cx + 3, i + windowOffsetY + 6 );
            }
            setFreqBar( 1 );
            setBrightness( 60 );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.BaffledPistonSetup();
        }
    }

    class BaffledPistonSetup extends RippleFrame.Setup {
        String getName() {
            return "Baffled Piston";
        }

        void select() {
            sourceChooser.select( SRC_1S1F_PLANE );
            fixedEndsCheck.setState( false );
            int i;
            for( i = 0; i != gridSizeY; i++ ) {
                setWall( windowOffsetX + 2, i );
            }
            for( i = 0; i <= 11; i++ ) {
                setWall( windowOffsetX, i + gridSizeY / 2 - 5 );
                if( i != 0 && i != 11 ) {
                    setWall( windowOffsetX + 2, i + gridSizeY / 2 - 5, false );
                }
            }
            setWall( windowOffsetX + 1, gridSizeY / 2 - 5 );
            setWall( windowOffsetX + 1, gridSizeY / 2 + 6 );
            setFreqBar( 24 );
            setSources();
            sources[0].x = sources[1].x = windowOffsetX + 1;
            sources[0].y = gridSizeY / 2 - 4;
            sources[1].y = gridSizeY / 2 + 5;
            setBrightness( 18 );
        }

        void doSetupSources() {
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.LowPassFilter1Setup();
        }
    }

    class LowPassFilter1Setup extends RippleFrame.Setup {
        String getName() {
            return "Low-Pass Filter 1";
        }

        void select() {
            if( resBar.getValue() < 43 ) {
                setResolution( 43 );
            }
            fixedEndsCheck.setState( false );
            int i, j;
            for( i = 0; i != windowWidth; i++ ) {
                setWall( i + windowOffsetX, windowOffsetY + 9 );
            }
            int cx = gridSizeX / 2;
            for( i = 1; i <= 4; i++ ) {
                for( j = -7; j <= 7; j++ ) {
                    setWall( cx + j, windowOffsetY + 9 * i );
                }
            }
            for( i = 0; i <= 4; i++ ) {
                for( j = -4; j <= 4; j++ ) {
                    setWall( cx + j, windowOffsetY + 9 * i, false );
                }
            }
            for( i = 0; i != 27; i++ ) {
                setWall( cx + 7, windowOffsetY + 9 + i );
                setWall( cx - 7, windowOffsetY + 9 + i );
            }
            setBrightness( 38 );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.LowPassFilter2Setup();
        }
    }

    class LowPassFilter2Setup extends RippleFrame.LowPassFilter1Setup {
        String getName() {
            return "Low-Pass Filter 2";
        }

        void select() {
            super.select();
            setFreqBar( 17 );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.HighPassFilter1Setup();
        }
    }

    class HighPassFilter1Setup extends RippleFrame.Setup {
        String getName() {
            return "High-Pass Filter 1";
        }

        void select() {
            if( resBar.getValue() < 43 ) {
                setResolution( 43 );
            }
            fixedEndsCheck.setState( false );
            int i, j;
            for( i = 0; i != windowWidth; i++ ) {
                for( j = 0; j <= 25; j += 5 ) {
                    setWall( i + windowOffsetX, windowOffsetY + 9 + j );
                }
            }
            int cx = gridSizeX / 2;
            for( i = 0; i <= 25; i += 5 ) {
                for( j = -4; j <= 4; j++ ) {
                    setWall( cx + j, windowOffsetY + 9 + i, false );
                }
            }
            setBrightness( 62 );
            // by default we show a freq high enough to be passed
            setFreqBar( 17 );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.HighPassFilter2Setup();
        }
    }

    class HighPassFilter2Setup extends RippleFrame.HighPassFilter1Setup {
        String getName() {
            return "High-Pass Filter 2";
        }

        void select() {
            super.select();
            setFreqBar( 7 );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.BandStopFilter1Setup();
        }
    }

    class BandStopFilter1Setup extends RippleFrame.Setup {
        String getName() {
            return "Band-Stop Filter 1";
        }

        void select() {
            if( resBar.getValue() < 43 ) {
                setResolution( 43 );
            }
            fixedEndsCheck.setState( false );
            int i, j, k;
            for( i = 0; i != windowWidth; i++ ) {
                setWall( i + windowOffsetX, windowOffsetY + 9 );
            }
            int cx = gridSizeX / 2;
            for( i = 1; i <= 2; i++ ) {
                for( j = -11; j <= 11; j++ ) {
                    if( j > -5 && j < 5 ) {
                        continue;
                    }
                    setWall( cx + j, windowOffsetY + 9 + 9 * i );
                }
            }
            for( i = 0; i <= 1; i++ ) {
                for( j = -4; j <= 4; j++ ) {
                    setWall( cx + j, windowOffsetY + 9 + i * 26, false );
                }
            }
            for( i = 0; i <= 18; i++ ) {
                setWall( cx + 11, windowOffsetY + 9 + i );
                setWall( cx - 11, windowOffsetY + 9 + i );
            }
            for( i = 0; i != 3; i++ ) {
                for( j = 0; j != 3; j++ ) {
                    for( k = 9; k <= 18; k += 9 ) {
                        setWall( cx + 5 + i, windowOffsetY + k + j );
                        setWall( cx + 5 + i, windowOffsetY + 9 + k - j );
                        setWall( cx - 5 - i, windowOffsetY + k + j );
                        setWall( cx - 5 - i, windowOffsetY + 9 + k - j );
                    }
                }
            }
            setBrightness( 38 );
            setFreqBar( 2 );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.BandStopFilter2Setup();
        }
    }

    class BandStopFilter2Setup extends RippleFrame.BandStopFilter1Setup {
        String getName() {
            return "Band-Stop Filter 2";
        }

        void select() {
            super.select();
            setFreqBar( 10 );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.BandStopFilter3Setup();
        }
    }

    class BandStopFilter3Setup extends RippleFrame.BandStopFilter1Setup {
        String getName() {
            return "Band-Stop Filter 3";
        }

        void select() {
            super.select();
            // at this frequency it doesn't pass
            setFreqBar( 4 );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.PlanarConvexLensSetup();
        }
    }

    class PlanarConvexLensSetup extends RippleFrame.Setup {
        String getName() {
            return "Planar Convex Lens";
        }

        void select() {
            if( resBar.getValue() < 42 ) {
                setResolution( 42 );
            }
            sourceChooser.select( SRC_1S1F_PLANE );
            // need small wavelengths here to remove diffraction effects
            int i, j;
            int cx = gridSizeX / 2;
            int cy = windowHeight / 8 + windowOffsetY;
            int x0 = windowWidth / 3 - 2;
            int y0 = 5;
            double r = ( .75 * windowHeight ) * .5;
            double h = r - y0;
            double r2 = r * r;
            if( x0 > r ) {
                x0 = (int)r;
            }
            for( i = 0; i <= x0; i++ ) {
                int y = 2 + (int)( Math.sqrt( r2 - i * i ) - h + .5 );
                for( ; y >= 0; y-- ) {
                    setMedium( cx + i, cy + y, mediumMax / 2 );
                    setMedium( cx - i, cy + y, mediumMax / 2 );
                }
            }
            setFreqBar( 19 );
            setBrightness( 6 );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.BiconvexLensSetup();
        }
    }

    class BiconvexLensSetup extends RippleFrame.Setup {
        String getName() {
            return "Biconvex Lens";
        }

        void select() {
            if( resBar.getValue() < 50 ) {
                setResolution( 50 );
            }
            setSources();
            int i, j;
            int cx = gridSizeX / 2;
            int cy = gridSizeY / 2;
            int x0 = windowWidth / 3 - 2;
            int y0 = 10;
            double r = ( .75 * .5 * windowHeight ) * .5;
            double h = r - y0;
            double r2 = r * r;
            if( x0 > r ) {
                x0 = (int)r;
            }
            for( i = 0; i <= x0; i++ ) {
                int y = 1 + (int)( Math.sqrt( r2 - i * i ) - h + .5 );
                for( ; y >= 0; y-- ) {
                    setMedium( cx + i, cy + y, mediumMax / 2 );
                    setMedium( cx - i, cy + y, mediumMax / 2 );
                    setMedium( cx + i, cy - y, mediumMax / 2 );
                    setMedium( cx - i, cy - y, mediumMax / 2 );
                }
            }
            setFreqBar( 19 );
            setBrightness( 66 );
            sources[0].y = cy - ( 2 + 2 * (int)r );
        }

        void doSetupSources() {
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.PlanarConcaveSetup();
        }
    }

    class PlanarConcaveSetup extends RippleFrame.Setup {
        String getName() {
            return "Planar Concave Lens";
        }

        void select() {
            sourceChooser.select( SRC_1S1F_PLANE );
            int i, j;
            int cx = gridSizeX / 2;
            int cy = windowHeight / 8 + windowOffsetY;
            int x0 = windowWidth / 5;
            int y0 = 5;
            double r = ( .25 * windowHeight ) * .5;
            double h = r - y0;
            double r2 = r * r;
            if( x0 > r ) {
                x0 = (int)r;
            }
            for( i = 0; i <= x0; i++ ) {
                int y = y0 + 2 - (int)( Math.sqrt( r2 - i * i ) - h + .5 );
                for( ; y >= 0; y-- ) {
                    setMedium( cx + i, cy + y, mediumMax / 2 );
                    setMedium( cx - i, cy + y, mediumMax / 2 );
                }
            }
            for( i = 0; i != windowWidth; i++ ) {
                if( medium[windowOffsetX + i + gw * cy] == 0 ) {
                    setWall( windowOffsetX + i, cy );
                }
            }
            setFreqBar( 19 );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.CircularPrismSetup();
        }
    }

    class CircularPrismSetup extends RippleFrame.Setup {
        String getName() {
            return "Circular Prism";
        }

        void select() {
            sourceChooser.select( SRC_1S1F_PLANE );
            int i, j;
            int cx = gridSizeX / 2;
            int cy = gridSizeY / 2;
            int x0 = windowWidth / 3 - 2;
            int y0 = x0;
            double r = ( x0 * x0 + y0 * y0 ) / ( 2. * y0 );
            double h = r - y0;
            double r2 = r * r;
            for( i = 0; i < x0; i++ ) {
                int y = (int)( Math.sqrt( r2 - i * i ) - h + .5 );
                for( ; y >= 0; y-- ) {
                    setMedium( cx + i, cy + y, mediumMax );
                    setMedium( cx - i, cy + y, mediumMax );
                    setMedium( cx + i, cy - y, mediumMax );
                    setMedium( cx - i, cy - y, mediumMax );
                }
            }
            for( i = 0; i != windowWidth; i++ ) {
                if( medium[windowOffsetX + i + gw * cy] == 0 ) {
                    setWall( windowOffsetX + i, cy );
                }
            }
            setFreqBar( 9 );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.RightAnglePrismSetup();
        }
    }

    class RightAnglePrismSetup extends RippleFrame.Setup {
        String getName() {
            return "Right-Angle Prism";
        }

        void select() {
            if( resBar.getValue() < 42 ) {
                setResolution( 42 );
            }
            sourceChooser.select( SRC_1S1F_PLANE );
            int i, j;
            int cx = gridSizeX / 2;
            int cy = gridSizeY / 2;
            int x0 = windowWidth / 4;
            int y0 = x0;
            for( i = -x0; i < x0; i++ ) {
                for( j = -y0; j <= i; j++ ) {
                    setMedium( cx + i, cy + j, mediumMax );
                }
            }
            for( i = 0; i != windowWidth; i++ ) {
                if( medium[windowOffsetX + i + gw * ( cy - y0 )] == 0 ) {
                    setWall( windowOffsetX + i, cy - y0 );
                }
            }
            setFreqBar( 11 );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.PorroPrismSetup();
        }
    }

    class PorroPrismSetup extends RippleFrame.Setup {
        String getName() {
            return "Porro Prism";
        }

        void select() {
            if( resBar.getValue() < 42 ) {
                setResolution( 42 );
            }
            sourceChooser.select( SRC_1S1F_PLANE );
            setSources();
            int i, j;
            int cx = gridSizeX / 2;
            sources[1].x = cx - 1;
            int x0 = windowWidth / 2;
            int y0 = x0;
            int cy = gridSizeY / 2 - y0 / 2;
            for( i = -x0; i < x0; i++ ) {
                int j2 = y0 + 1 - ( ( i < 0 ) ? -i : i );
                for( j = 0; j <= j2; j++ ) {
                    setMedium( cx + i, cy + j, mediumMax );
                }
            }
            for( i = 0; i != cy; i++ ) {
                if( medium[cx + gw * ( i + windowOffsetY )] == 0 ) {
                    setWall( cx, i + windowOffsetY );
                }
            }
            setFreqBar( 11 );
        }

        void doSetupSources() {
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.ScatteringSetup();
        }
    }

    class ScatteringSetup extends RippleFrame.Setup {
        String getName() {
            return "Scattering";
        }

        void select() {
            sourceChooser.select( SRC_1S1F_PLANE_PULSE );
            int cx = gridSizeX / 2;
            int cy = gridSizeY / 2;
            setWall( cx, cy );
            setFreqBar( 1 );
            dampingBar.setValue( 40 );
            setBrightness( 52 );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.LloydsMirrorSetup();
        }
    }

    class LloydsMirrorSetup extends RippleFrame.Setup {
        String getName() {
            return "Lloyd's Mirror";
        }

        void select() {
            setSources();
            sources[0].x = windowOffsetX;
            sources[0].y = windowOffsetY + windowHeight * 3 / 4;
            setBrightness( 75 );
            setFreqBar( 23 );
            int i;
            for( i = 0; i != windowWidth; i++ ) {
                setWall( i + windowOffsetX, windowOffsetY + windowHeight - 1 );
            }
        }

        void doSetupSources() {
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.TempGradient1();
        }
    }

    class TempGradient1 extends RippleFrame.Setup {
        String getName() {
            return "Temperature Gradient 1";
        }

        void select() {
            int i, j;
            int j1 = windowOffsetY + windowHeight / 2;
            int j2 = windowOffsetY + windowHeight * 3 / 4;
            int j3 = windowOffsetY + windowHeight * 7 / 8;
            for( j = 0; j != gridSizeY; j++ ) {
                int m;
                if( j < j1 ) {
                    m = 0;
                }
                else if( j > j2 ) {
                    m = mediumMax;
                }
                else {
                    m = mediumMax * ( j - j1 ) / ( j2 - j1 );
                }
                for( i = 0; i != gridSizeX; i++ ) {
                    setMedium( i, j, m );
                }
            }
            for( i = j3; i < windowOffsetY + windowHeight; i++ ) {
                setWall( gridSizeX / 2, i );
            }
            setBrightness( 33 );
        }

        void doSetupSources() {
            setSources();
            sources[0].x = windowOffsetX + 2;
            sources[0].y = windowOffsetY + windowHeight - 2;
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.TempGradient2();
        }
    }

    class TempGradient2 extends RippleFrame.Setup {
        String getName() {
            return "Temperature Gradient 2";
        }

        void select() {
            int i, j;
            int j1 = windowOffsetY + windowHeight / 2 - windowHeight / 8;
            int j2 = windowOffsetY + windowHeight / 2 + windowHeight / 8;
            for( j = 0; j != gridSizeY; j++ ) {
                int m;
                if( j < j1 ) {
                    m = mediumMax;
                }
                else if( j > j2 ) {
                    m = 0;
                }
                else {
                    m = mediumMax * ( j2 - j ) / ( j2 - j1 );
                }
                for( i = 0; i != gridSizeX; i++ ) {
                    setMedium( i, j, m );
                }
            }
            setBrightness( 31 );
        }

        void doSetupSources() {
            setSources();
            sources[0].x = windowOffsetX + 2;
            sources[0].y = windowOffsetY + windowHeight / 4;
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.TempGradient3();
        }
    }

    class TempGradient3 extends RippleFrame.Setup {
        String getName() {
            return "Temperature Gradient 3";
        }

        void select() {
            int i, j;
            int j1 = windowOffsetY + windowHeight / 2 - windowHeight / 5;
            int j2 = windowOffsetY + windowHeight / 2 + windowHeight / 5;
            int j3 = gridSizeY / 2;
            for( j = 0; j != gridSizeY; j++ ) {
                int m;
                if( j < j1 || j > j2 ) {
                    m = mediumMax;
                }
                else if( j > j3 ) {
                    m = mediumMax * ( j - j3 ) / ( j2 - j3 );
                }
                else {
                    m = mediumMax * ( j3 - j ) / ( j3 - j1 );
                }
                for( i = 0; i != gridSizeX; i++ ) {
                    setMedium( i, j, m );
                }
            }
            setBrightness( 31 );
        }

        void doSetupSources() {
            setSources();
            sources[0].x = windowOffsetX + 2;
            sources[0].y = windowOffsetY + windowHeight / 4;
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.TempGradient4();
        }
    }

    class TempGradient4 extends RippleFrame.TempGradient3 {
        String getName() {
            return "Temperature Gradient 4";
        }

        void select() {
            int i, j;
            int j1 = windowOffsetY + windowHeight / 2 - windowHeight / 5;
            int j2 = windowOffsetY + windowHeight / 2 + windowHeight / 5;
            int j3 = gridSizeY / 2;
            for( j = 0; j != gridSizeY; j++ ) {
                int m;
                if( j < j1 || j > j2 ) {
                    m = 0;
                }
                else if( j > j3 ) {
                    m = mediumMax * ( j2 - j ) / ( j2 - j3 );
                }
                else {
                    m = mediumMax * ( j - j1 ) / ( j3 - j1 );
                }
                for( i = 0; i != gridSizeX; i++ ) {
                    setMedium( i, j, m );
                }
            }
            setBrightness( 31 );
        }

        RippleFrame.Setup createNext() {
            return new RippleFrame.DispersionSetup();
        }
    }

    class DispersionSetup extends RippleFrame.Setup {
        String getName() {
            return "Dispersion";
        }

        void select() {
            sourceChooser.select( SRC_2S2F );
            int i, j;
            for( i = 0; i != gridSizeY; i++ ) {
                setWall( gridSizeX / 2, i );
            }
            for( i = 0; i != gridSizeX; i++ ) {
                for( j = 0; j != gridSizeY; j++ ) {
                    setMedium( i, j, mediumMax / 3 );
                }
            }
            fixedEndsCheck.setState( false );
            setBrightness( 16 );
        }

        void doSetupSources() {
            setSources();
            sources[0].x = gridSizeX / 2 - 2;
            sources[1].x = gridSizeX / 2 + 2;
            sources[0].y = sources[1].y = windowOffsetY + 1;
            setFreqBar( 7 );
            auxBar.setValue( 30 );
        }

        RippleFrame.Setup createNext() {
            return null;
        }
    }
}
