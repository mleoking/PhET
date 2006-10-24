/* Copyright 2004, Sam Reid */
package org.falstad;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.MemoryImageSource;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Random;

/**
 * User: Sam Reid
 * Date: Feb 9, 2006
 * Time: 9:14:04 PM
 * Copyright (c) Feb 9, 2006 by Sam Reid
 */
class QuantumBoxFrame extends Frame
        implements ComponentListener, ActionListener, AdjustmentListener,
                   MouseMotionListener, MouseListener, ItemListener {

    Thread engine = null;

    Dimension winSize;
    Image dbimage;
    static final int panePad = 4;
    Button groundButton;
    Button blankButton;
    Button normalizeButton;
    Button maximizeButton;
    Checkbox stoppedCheck;
    CheckboxMenuItem eCheckItem;
    CheckboxMenuItem xCheckItem;
    CheckboxMenuItem pCheckItem;
    CheckboxMenuItem statesCheckItem;
    CheckboxMenuItem expectCheckItem;
    CheckboxMenuItem uncertaintyCheckItem;
    CheckboxMenuItem probCheckItem;
    CheckboxMenuItem probPhaseCheckItem;
    CheckboxMenuItem magPhaseCheckItem;
    CheckboxMenuItem alwaysNormItem;
    CheckboxMenuItem alwaysMaxItem;
    Menu waveFunctionMenu;
    MenuItem measureEItem;
    MenuItem exitItem;

    Choice mouseChooser;
    Scrollbar speedBar;
    Scrollbar forceBar;
    Scrollbar resBar;
    Scrollbar pZoomBar;
    Scrollbar phasorBar;
    Scrollbar aspectBar;
    Scrollbar brightnessBar;
    QuantumBoxFrame.View viewPotential, viewX, viewP, viewStates,
            viewCurrent, viewXMap, viewPMap, viewStatesMap;
    QuantumBoxFrame.View viewList[];
    int viewCount;
    boolean showModeDEBUG;
    boolean editingFunc;
    boolean dragStop;

    QuantumBoxFrame.PhaseColor phaseColors[][];
    QuantumBoxFrame.PhaseColor whitePhaseColor;
    Color grayLevels[];
    static final int phaseColorCount = 50;

    int selectedCoefX, selectedCoefY;
    int selectedGridX, selectedGridY;
    int selectedPaneHandle;
    double selectedGridFunc;
    static final int SEL_NONE = 0;
    static final int SEL_POTENTIAL = 1;
    static final int SEL_X = 2;
    static final int SEL_P = 3;
    static final int SEL_STATES = 4;
    static final int SEL_HANDLE = 5;
    static final int MOUSE_EIGEN = 0;
    static final int MOUSE_GAUSS = 1;
    static final int MOUSE_GAUSSP = 2;
    static final int MOUSE_SQUARE = 3;
    static final int MOUSE_CIRCLE = 4;
    int selection;
    int dragX, dragY;
    int dragStartX, dragStartY;
    boolean dragSet, dragClear;
    double magDragStart;
    boolean dragging;

    int pause;
    QuantumBoxCanvas cv;
    QuantumBox applet;
    boolean useBufferedImage = false;

    long lastTime;
    int floorValues[];

    double lastGaussWx = -.03;
    double lastGaussWy = -.03;

    QuantumBoxFrame( QuantumBox a ) {
        super( "Quantum 2-D Box Applet v1.5" );
        applet = a;
    }


    public String getAppletInfo() {
        return "QuantumBox by Paul Falstad";
    }

    public void init() {
        String jv = System.getProperty( "java.class.version" );
        double jvf = new Double( jv ).doubleValue();
        if( jvf >= 48 ) {
            useBufferedImage = true;
        }

        selectedCoefX = selectedCoefY = -1;
        setLayout( new QuantumBoxLayout() );
        cv = new QuantumBoxCanvas( this );
        cv.addComponentListener( this );
        cv.addMouseMotionListener( this );
        cv.addMouseListener( this );
        add( cv );

        MenuBar mb = new MenuBar();
        Menu m = new Menu( "File" );
        mb.add( m );
        m.add( exitItem = getMenuItem( "Exit" ) );
        m = new Menu( "View" );
        mb.add( m );
        m.add( eCheckItem = getCheckItem( "Energy" ) );
        eCheckItem.setState( true );
        m.add( xCheckItem = getCheckItem( "Position" ) );
        xCheckItem.setState( true );
        xCheckItem.disable();
        m.add( pCheckItem = getCheckItem( "Momentum" ) );
        m.add( statesCheckItem = getCheckItem( "State Phasors" ) );
        statesCheckItem.setState( true );
        m.addSeparator();
        m.add( expectCheckItem = getCheckItem( "Expectation Values" ) );
        expectCheckItem.setState( true );
        m.add( uncertaintyCheckItem = getCheckItem( "Uncertainties" ) );
        Menu m2 = waveFunctionMenu = new Menu( "Wave Function" );
        m.add( m2 );
        m2.add( probCheckItem = getCheckItem( "Probability" ) );
        m2.add( probPhaseCheckItem = getCheckItem( "Probability + Phase" ) );
        m2.add( magPhaseCheckItem = getCheckItem( "Magnitude + Phase" ) );
        magPhaseCheckItem.setState( true );

        m = new Menu( "Measure" );
        mb.add( m );
        m.add( measureEItem = getMenuItem( "Measure Energy" ) );
        setMenuBar( mb );

        m = new Menu( "Options" );
        mb.add( m );
        m.add( alwaysNormItem = getCheckItem( "Always Normalize" ) );
        m.add( alwaysMaxItem = getCheckItem( "Always Maximize" ) );
        alwaysMaxItem.setState( true );
        setMenuBar( mb );

        mouseChooser = new Choice();
        mouseChooser.add( "Mouse = Set Eigenstate" );
        mouseChooser.add( "Mouse = Create Gaussian" );
        mouseChooser.add( "Mouse = Gaussian w/ Momentum" );
        mouseChooser.add( "Mouse = Create Square" );
        mouseChooser.add( "Mouse = Create Circle" );
        mouseChooser.addItemListener( this );
        add( mouseChooser );
        mouseChooser.select( MOUSE_GAUSS );

        add( blankButton = new Button( "Clear" ) );
        blankButton.addActionListener( this );
        add( normalizeButton = new Button( "Normalize" ) );
        normalizeButton.addActionListener( this );
        add( maximizeButton = new Button( "Maximize" ) );
        maximizeButton.addActionListener( this );
        add( groundButton = new Button( "Ground State" ) );
        groundButton.addActionListener( this );

        stoppedCheck = new Checkbox( "Stopped" );
        stoppedCheck.addItemListener( this );
        add( stoppedCheck );

        add( new Label( "Simulation Speed", Label.CENTER ) );
        add( speedBar = new Scrollbar( Scrollbar.HORIZONTAL, 109, 1, 1, 300 ) );
        speedBar.addAdjustmentListener( this );

        add( new Label( "Brightness", Label.CENTER ) );
        add( brightnessBar =
                new Scrollbar( Scrollbar.HORIZONTAL, 1100, 1, 700, 2000 ) );
        brightnessBar.addAdjustmentListener( this );

        add( new Label( "Resolution", Label.CENTER ) );
        add( resBar = new Scrollbar( Scrollbar.HORIZONTAL,
                                     6, 1, 4, 9 ) );
        resBar.addAdjustmentListener( this );

        add( new Label( "Momentum Zoom", Label.CENTER ) );
        add( pZoomBar = new Scrollbar( Scrollbar.HORIZONTAL,
                                       1, 1, 5, 100 ) );
        pZoomBar.addAdjustmentListener( this );

        add( new Label( "Phasor Count", Label.CENTER ) );
        add( phasorBar = new Scrollbar( Scrollbar.HORIZONTAL,
                                        10, 1, 5, 30 ) );
        phasorBar.addAdjustmentListener( this );

        add( new Label( "Aspect Ratio", Label.CENTER ) );
        add( aspectBar = new Scrollbar( Scrollbar.HORIZONTAL,
                                        10, 1, 5, 31 ) );
        aspectBar.addAdjustmentListener( this );

        setResolution();

        try {
            String param = applet.getParameter( "PAUSE" );
            if( param != null ) {
                pause = Integer.parseInt( param );
            }
        }
        catch( Exception e ) { }

        phaseColors = new QuantumBoxFrame.PhaseColor[8][phaseColorCount + 1];
        for( int i = 0; i != 8; i++ ) {
            for( int j = 0; j <= phaseColorCount; j++ ) {
                double ang = Math.atan( j / (double)phaseColorCount );
                phaseColors[i][j] = genPhaseColor( i, ang );
            }
        }
        whitePhaseColor = new QuantumBoxFrame.PhaseColor( 1, 1, 1 );
        grayLevels = new Color[256];
        for( int i = 0; i != 256; i++ ) {
            grayLevels[i] = new Color( i, i, i );
        }

        random = new Random();
        reinit();
        cv.setBackground( Color.black );
        cv.setForeground( Color.lightGray );

        resize( 640, 600 );
        handleResize();
        Dimension x = getSize();
        Dimension screen = getToolkit().getScreenSize();
        setLocation( ( screen.width - x.width ) / 2,
                     ( screen.height - x.height ) / 2 );
        show();
    }

    MenuItem getMenuItem( String s ) {
        MenuItem mi = new MenuItem( s );
        mi.addActionListener( this );
        return mi;
    }

    CheckboxMenuItem getCheckItem( String s ) {
        CheckboxMenuItem mi = new CheckboxMenuItem( s );
        mi.addItemListener( this );
        return mi;
    }

    QuantumBoxFrame.PhaseColor genPhaseColor( int sec, double ang ) {
        // convert to 0 .. 2*pi angle
        ang += sec * pi / 4;
        // convert to 0 .. 6
        ang *= 3 / pi;
        int hsec = (int)ang;
        double a2 = ang % 1;
        double a3 = 1. - a2;
        QuantumBoxFrame.PhaseColor c = null;
        switch( hsec ) {
            case 6:
            case 0:
                c = new QuantumBoxFrame.PhaseColor( 1, a2, 0 );
                break;
            case 1:
                c = new QuantumBoxFrame.PhaseColor( a3, 1, 0 );
                break;
            case 2:
                c = new QuantumBoxFrame.PhaseColor( 0, 1, a2 );
                break;
            case 3:
                c = new QuantumBoxFrame.PhaseColor( 0, a3, 1 );
                break;
            case 4:
                c = new QuantumBoxFrame.PhaseColor( a2, 0, 1 );
                break;
            case 5:
                c = new QuantumBoxFrame.PhaseColor( 1, 0, a3 );
                break;
        }
        return c;
    }


    void handleResize() {
        Dimension d = winSize = cv.getSize();
        if( winSize.width == 0 ) {
            return;
        }
        dbimage = createImage( d.width, d.height );
        setupDisplay();
    }

    void setupDisplay() {
        if( winSize == null ) {
            return;
        }
        int potsize = ( viewPotential == null ) ? 50 : viewPotential.height;
        int statesize = ( viewStates == null ) ? 150 : viewStates.height;
        viewPotential = viewStates = viewX = viewP = null;
        viewList = new QuantumBoxFrame.View[10];
        int i = 0;
        if( eCheckItem.getState() ) {
            viewList[i++] = viewPotential = new QuantumBoxFrame.View();
        }
        if( xCheckItem.getState() ) {
            viewList[i++] = viewX = new QuantumBoxFrame.View();
        }
        if( pCheckItem.getState() ) {
            viewList[i++] = viewP = new QuantumBoxFrame.View();
        }
        if( statesCheckItem.getState() ) {
            viewList[i++] = viewStates = new QuantumBoxFrame.View();
        }
        viewCount = i;
        int sizenum = viewCount;
        int toth = winSize.height;

        // preserve size of potential and state panes if possible
        if( potsize > 0 && viewPotential != null ) {
            sizenum--;
            toth -= potsize;
        }
        if( statesize > 0 && viewStates != null ) {
            sizenum--;
            toth -= statesize;
        }
        toth -= panePad * 2 * ( viewCount - 1 );
        int cury = 0;
        for( i = 0; i != viewCount; i++ ) {
            QuantumBoxFrame.View v = viewList[i];
            int h = ( sizenum == 0 ) ? toth : toth / sizenum;
            if( v == viewPotential && potsize > 0 ) {
                h = potsize;
            }
            else if( v == viewStates && statesize > 0 ) {
                h = statesize;
            }
            v.paneY = cury;
            if( cury > 0 ) {
                cury += panePad;
            }
            v.x = 0;
            v.width = winSize.width;
            v.y = cury;
            v.height = h;
            cury += h + panePad;
        }
        setSubViews();
    }

    void setSubViews() {
        viewXMap = viewPMap = null;
        viewStatesMap = null;
        if( viewStates != null ) {
            viewStatesMap = new QuantumBoxFrame.View( viewStates );
            viewStatesMap.x = ( winSize.width - viewStatesMap.height ) / 2;
            viewStatesMap.width -= viewStatesMap.x * 2;
        }
        if( viewX != null ) {
            viewXMap = new QuantumBoxFrame.View( viewX );
            processMap( viewXMap, aspectRatio );
        }
        if( viewP != null ) {
            viewPMap = new QuantumBoxFrame.View( viewP );
            processMap( viewPMap, 1 / aspectRatio );
        }
        floorValues = null;
    }

    void processMap( QuantumBoxFrame.View v, double ar ) {
        double a = v.width / (double)v.height;
        int w, h;
        if( ar > a ) {
            w = v.width - 2;
            h = (int)( w / ar );
        }
        else {
            h = v.height - 2;
            w = (int)( h * ar );
        }
        v.x += ( v.width - w ) / 2 + 1;
        v.y += ( v.height - h ) / 2 + 1;
        v.width = w;
        v.height = h;
        v.pixels = null;
        if( useBufferedImage ) {
            try {
                /* simulate the following code using reflection:
               dbimage = new BufferedImage(d.width, d.height,
               BufferedImage.TYPE_INT_RGB);
               DataBuffer db = (DataBuffer)(((BufferedImage)memimage).
               getRaster().getDataBuffer());
               DataBufferInt dbi = (DataBufferInt) db;
               pixels = dbi.getData();
            */
                Class biclass = Class.forName( "java.awt.image.BufferedImage" );
                Class dbiclass = Class.forName( "java.awt.image.DataBufferInt" );
                Class rasclass = Class.forName( "java.awt.image.Raster" );
                Constructor cstr = biclass.getConstructor(
                        new Class[]{int.class, int.class, int.class} );
                v.memimage = (Image)cstr.newInstance( new Object[]{
                        new Integer( v.width ), new Integer( v.height ),
                        new Integer( 1 )} ); // BufferedImage.TYPE_INT_RGB)});
                Method m = biclass.getMethod( "getRaster", null );
                Object ras = m.invoke( v.memimage, null );
                Object db = rasclass.getMethod( "getDataBuffer", null ).
                        invoke( ras, null );
                v.pixels = (int[])
                        dbiclass.getMethod( "getData", null ).invoke( db, null );
            }
            catch( Exception ee ) {
                // ee.printStackTrace();
                System.out.println( "BufferedImage failed" );
            }
        }
        if( v.pixels == null ) {
            v.pixels = new int[v.width * v.height];
            int i;
            for( i = 0; i != v.width * v.height; i++ ) {
                v.pixels[i] = 0xFF000000;
            }
            v.imageSource = new MemoryImageSource( v.width, v.height,
                                                   v.pixels, 0, v.width );
            v.imageSource.setAnimated( true );
            v.imageSource.setFullBufferUpdates( true );
            v.memimage = cv.createImage( v.imageSource );
        }
    }


    int getPanelHeight() {
        return winSize.height / 3;
    }

    void centerString( Graphics g, String s, int y ) {
        FontMetrics fm = g.getFontMetrics();
        g.drawString( s, ( winSize.width - fm.stringWidth( s ) ) / 2, y );
    }

    public void paint( Graphics g ) {
        cv.repaint();
    }


    public void updateQuantumBox( Graphics realg ) {
        Graphics g = dbimage.getGraphics();
        if( winSize == null || winSize.width == 0 ) {
            handleResize();
            return;
        }
        if( !stoppedCheck.getState() && !dragging ) {
            increaseTime( 4.6 );
        }
        else {
            lastTime = 0;
        }
        Color gray2 = fillBackground( g );
        drawViewBounds( g );
        if( dragStop ) {
            resetTime();
        }
        if( !editingFunc ) {
            updatePhases();
        }
        if( viewXMap != null ) {
            double brightmult = Math.exp( brightnessBar.getValue() / 200. - 5 );
            updateMapView( g, viewXMap, func, funci, maxTerms, brightmult );
        }
        else {
            pfuncr = null;
            pfunci = null;
        }
        if( viewStatesMap != null ) {
            // draw frequency grid
            drawFrequencyGrid( g, gray2 );
        }

        drawXMap( g );

        realg.drawImage( dbimage, 0, 0, this );
        if( !stoppedCheck.getState() ) {
            cv.repaint( pause );
        }
    }

    private void drawXMap( Graphics g ) {
        if( selectedCoefX != -1 && viewXMap != null ) {
            g.setColor( Color.yellow );
            for( int i = 0; i != selectedCoefX; i++ ) {
                for( int j = 0; j != selectedCoefY; j++ ) {
                    int x1 = viewXMap.x + viewXMap.width * i / selectedCoefX;
                    int x0 = viewXMap.width / selectedCoefX;
                    int y1 = viewXMap.y + viewXMap.height * j / selectedCoefY;
                    int y0 = viewXMap.height / selectedCoefY;
                    g.drawOval( x1 + x0 * 20 / 100, y1 + y0 * 20 / 100,
                                x0 * 60 / 100, y0 * 60 / 100 );
                }
            }
        }
    }

    private void drawFrequencyGrid( Graphics g, Color gray2 ) {
        int termWidth = getTermWidth();
        int stateSize = termWidth;
        int ss2 = termWidth / 2;
        for( int i = 1; i <= maxDispTerms; i++ ) {
            for( int j = 1; j <= maxDispTerms; j++ ) {
                int x = viewStatesMap.x + ( i - 1 ) * termWidth + ss2;
                int y = viewStatesMap.y + ( j - 1 ) * termWidth + ss2;
                boolean yel = ( selectedCoefX != -1 &&
                                elevels[selectedCoefX][selectedCoefY] == elevels[i][j] );
                g.setColor( yel ? Color.yellow :
                            ( magcoef[i][j] == 0 ) ? gray2 : Color.white );
                g.drawOval( x - ss2, y - ss2, stateSize, stateSize );
                int xa = (int)( magcoef[i][j] * phasecoefcos[i][j] * ss2 );
                int ya = (int)( -magcoef[i][j] * phasecoefsin[i][j] * ss2 );
                g.drawLine( x, y, x + xa, y + ya );
                g.drawLine( x + xa - 1, y + ya, x + xa + 1, y + ya );
                g.drawLine( x + xa, y + ya - 1, x + xa, y + ya + 1 );
            }
        }
        g.setColor( Color.white );
        if( viewStatesMap.x > termWidth * 3 / 2 && aspectRatio == 1 ) {
            int x = winSize.width - stateSize;
            int y = viewStatesMap.y + viewStatesMap.height / 2;
            double tcos = Math.cos( -elevels[1][1] * t / 2 + pi / 2 );
            double tsin = Math.sin( -elevels[1][1] * t / 2 + pi / 2 );
            int xa = (int)( tcos * ss2 );
            int ya = (int)( -tsin * ss2 );
            g.drawOval( x - ss2, y - ss2, stateSize, stateSize );
            g.drawLine( x, y, x + xa, y + ya );
            g.drawLine( x + xa - 1, y + ya, x + xa + 1, y + ya );
            g.drawLine( x + xa, y + ya - 1, x + xa, y + ya + 1 );
        }
    }

    private Color fillBackground( Graphics g ) {
        Color gray2 = new Color( 127, 127, 127 );
        g.setColor( cv.getBackground() );
        g.fillRect( 0, 0, winSize.width, winSize.height );
        return gray2;
    }

    private void drawViewBounds( Graphics g ) {
        for( int i = 1; i != viewCount; i++ ) {
            g.setColor( i == selectedPaneHandle ? Color.yellow : Color.gray );
            g.drawLine( 0, viewList[i].paneY,
                        winSize.width, viewList[i].paneY );
        }
    }

    void updateMapView( Graphics g, QuantumBoxFrame.View vmap,
                        float arrayr[][], float arrayi[][],
                        int res, double brightmult ) {
        g.setColor( Color.white );
        g.drawRect( vmap.x - 1, vmap.y - 1, vmap.width + 2, vmap.height + 2 );
        double maxsq = 0;
        double expectx = 0, expectx2 = 0;
        double expecty = 0, expecty2 = 0;
        double tot = 0;
        int zero = res / 2;
        int x, y;
        for( y = 0; y <= res; y++ ) {
            for( x = 0; x <= res; x++ ) {
                double dr = arrayr[x][y];
                double di = arrayi[x][y];
                double dy = dr * dr + di * di;
                if( dy > maxsq ) {
                    maxsq = dy;
                }
                int dev = x - zero;
                expectx += dy * dev;
                expectx2 += dy * dev * dev;
                dev = y - zero;
                expecty += dy * dev;
                expecty2 += dy * dev * dev;
                tot += dy;
            }
        }
        /*System.out.print(tot + " " + arrayr[0][0] + " " +
        magcoef[1][1] + "\n");*/
        expectx /= tot;
        expectx2 /= tot;
        expecty /= tot;
        expecty2 /= tot;
        double maxnm = Math.sqrt( maxsq );
        double uncertx = Math.sqrt( expectx2 - expectx * expectx );
        double uncerty = Math.sqrt( expecty2 - expecty * expecty );
        double bestscale = 0;
        if( probCheckItem.getState() || probPhaseCheckItem.getState() ) {
            bestscale = 1 / maxsq;
        }
        else {
            bestscale = 1 / maxnm;
        }
        vmap.scale *= 1.1;
        // XXX
        //System.out.print(view.scale + " " + bestscale + "\n");
        //if (view.scale > bestscale || view.scale == 0)
        vmap.scale = bestscale;
        if( vmap.scale > 1e8 ) {
            vmap.scale = 1e8;
        }
        int res1 = res + 1;
        for( y = 0; y <= res; y++ ) {
            for( x = 0; x <= res; x++ ) {
                double fr = arrayr[x][y];
                double fi = arrayi[x][y];
                double fv = ( fr * fr + fi * fi );
                if( magPhaseCheckItem.getState() ) {
                    fv = Math.sqrt( fv );
                }
                fv *= 255 * vmap.scale * brightmult;
                QuantumBoxFrame.PhaseColor c = getPhaseColor( fr, fi );
                if( fv > 255 ) {
                    fv = 255;
                }
                int cr = (int)( c.r * fv );
                int cg = (int)( c.g * fv );
                int cb = (int)( c.b * fv );
                int col = ( 255 << 24 ) | ( cr << 16 ) | ( cg << 8 ) | cb;
                int x1 = x * vmap.width / res1;
                int y1 = y * vmap.height / res1;
                int x2 = ( x + 1 ) * vmap.width / res1;
                int y2 = ( y + 1 ) * vmap.height / res1;
                int ix = x1 + y1 * vmap.width;
                int k, l;
                for( k = 0; k != x2 - x1; k++, ix++ ) {
                    for( l = 0; l != y2 - y1; l++ ) {
                        vmap.pixels[ix + l * vmap.width] = col;
                    }
                }
            }
        }
        if( vmap.imageSource != null ) {
            vmap.imageSource.newPixels();
        }
        g.drawImage( vmap.memimage, vmap.x, vmap.y, null );
        if( uncertaintyCheckItem.getState() ) {
            g.setColor( Color.blue );
            int xx1 =
                    (int)( vmap.width * ( expectx + zero - uncertx + .5 ) / res1 + vmap.x );
            int xx2 =
                    (int)( vmap.width * ( expectx + zero + uncertx + .5 ) / res1 + vmap.x );
            int yy1 =
                    (int)( vmap.height * ( expecty + zero - uncerty + .5 ) / res1 + vmap.y );
            int yy2 =
                    (int)( vmap.height * ( expecty + zero + uncerty + .5 ) / res1 + vmap.y );
            g.drawRect( xx1, yy1, xx2 - xx1, yy2 - yy1 );
        }
        if( expectCheckItem.getState() ) {
            g.setColor( Color.red );
            int xx = (int)( vmap.width * ( expectx + zero + .5 ) / res1 + vmap.x );
            g.drawLine( xx, vmap.y, xx, vmap.y + vmap.height );
            int yy = (int)( vmap.height * ( expecty + zero + .5 ) / res1 + vmap.y );
            g.drawLine( vmap.x, yy, vmap.x + vmap.width, yy );
        }
    }


    QuantumBoxFrame.PhaseColor getPhaseColor( double x, double y ) {
        int sector = 0;
        double val = 0;
        if( probCheckItem.getState() ) {
            return whitePhaseColor;
        }
        if( x == 0 && y == 0 ) {
            return phaseColors[0][0];
        }
        if( y >= 0 ) {
            if( x >= 0 ) {
                if( x >= y ) {
                    sector = 0;
                    val = y / x;
                }
                else {
                    sector = 1;
                    val = 1 - x / y;
                }
            }
            else {
                if( -x <= y ) {
                    sector = 2;
                    val = -x / y;
                }
                else {
                    sector = 3;
                    val = 1 + y / x;
                }
            }
        }
        else {
            if( x <= 0 ) {
                if( y >= x ) {
                    sector = 4;
                    val = y / x;
                }
                else {
                    sector = 5;
                    val = 1 - x / y;
                }
            }
            else {
                if( -y >= x ) {
                    sector = 6;
                    val = -x / y;
                }
                else {
                    sector = 7;
                    val = 1 + y / x;
                }
            }
        }
        return phaseColors[sector][(int)( val * phaseColorCount )];
    }

    int getTermWidth() {
        return viewStatesMap.height / maxDispTerms;
    }

    void edit( MouseEvent e ) {
        if( selection == SEL_NONE ) {
            return;
        }
        int x = e.getX();
        int y = e.getY();
        switch( selection ) {
            case SEL_HANDLE:
                editHandle( y );
                break;
            case SEL_STATES:
                editMag( x, y );
                break;
            case SEL_POTENTIAL:
                findStateByEnergy( y );
                enterSelectedState();
                break;
            case SEL_X:
                editX( x, y );
                break;
            case SEL_P:
                editP( x, y );
                break;
        }
    }

    void editHandle( int y ) {
        int dy = y - viewList[selectedPaneHandle].paneY;
        QuantumBoxFrame.View upper = viewList[selectedPaneHandle - 1];
        QuantumBoxFrame.View lower = viewList[selectedPaneHandle];
        int minheight = 10;
        if( upper.height + dy < minheight || lower.height - dy < minheight ) {
            return;
        }
        upper.height += dy;
        lower.height -= dy;
        lower.y += dy;
        lower.paneY += dy;
        cv.repaint( pause );
        setSubViews();
    }

    void editMag( int x, int y ) {
        if( selectedCoefX == -1 ) {
            return;
        }
        int stateSize = getTermWidth(); // XXX
        int ss2 = stateSize / 2;
        int x0 = stateSize * ( selectedCoefX - 1 ) + ss2 + viewStatesMap.x;
        int y0 = stateSize * ( selectedCoefY - 1 ) + ss2 + viewStatesMap.y;
        x -= x0;
        y -= y0;
        double mag = Math.sqrt( x * x + y * y ) / ss2;
        double ang = Math.atan2( -y, x );
        double ang0 = ( -elevels[selectedCoefX][selectedCoefY] * t ) % ( 2 * pi );
        if( mag > 10 ) {
            mag = 0;
        }
        if( mag > 1 ) {
            mag = 1;
        }
        magcoef[selectedCoefX][selectedCoefY] = mag;
        phasecoefadj[selectedCoefX][selectedCoefY] = ( ang - ang0 ) % ( 2 * pi );
        if( phasecoefadj[selectedCoefX][selectedCoefY] > pi ) {
            phasecoefadj[selectedCoefX][selectedCoefY] -= 2 * pi;
        }
        if( alwaysNormItem.getState() ) {
            normalize();
        }
        cv.repaint( pause );
    }

    void editMagClick() {
        if( selectedCoefX == -1 ) {
            return;
        }
        if( magDragStart < .5 ) {
            magcoef[selectedCoefX][selectedCoefY] = 1;
        }
        else {
            magcoef[selectedCoefX][selectedCoefY] = 0;
        }
        phasecoefadj[selectedCoefX][selectedCoefY] = 0;
        cv.repaint( pause );
    }

    void editX( int x, int y ) {
        int oldgx = selectedGridX;
        int oldgy = selectedGridY;
        switch( mouseChooser.getSelectedIndex() ) {
            case MOUSE_GAUSS:
                editXGauss( x, y );
                return;
            case MOUSE_GAUSSP:
                editXGaussP( x, y );
                return;
            case MOUSE_SQUARE:
                editXSquare( x, y );
                return;
            case MOUSE_CIRCLE:
                editXCircle( x, y );
                return;
            case MOUSE_EIGEN:
                findGridPoint2D( viewXMap, x, y );
                editXEigen();
                return;
        }
        findGridPoint2D( viewXMap, x, y );
        int x1 = oldgx;
        int y1 = oldgy;
        int x2 = selectedGridX;
        int y2 = selectedGridY;
        // need to draw a line from x1,y1 to x2,y2
        if( x1 == x2 && y1 == y2 ) {
            editFuncPoint( x2, y2, 1 );
        }
        else if( abs( y2 - y1 ) > abs( x2 - x1 ) ) {
            // y difference is greater, so we step along y's
            // from min to max y and calculate x for each step
            int sgn = sign( y2 - y1 );
            for( y = y1; y != y2 + sgn; y += sgn ) {
                x = x1 + ( x2 - x1 ) * ( y - y1 ) / ( y2 - y1 );
                editFuncPoint( x, y, 1 );
            }
        }
        else {
            // x difference is greater, so we step along x's
            // from min to max x and calculate y for each step
            int sgn = sign( x2 - x1 );
            for( x = x1; x != x2 + sgn; x += sgn ) {
                y = y1 + ( y2 - y1 ) * ( x - x1 ) / ( x2 - x1 );
                editFuncPoint( x, y, 1 );
            }
        }
        transform( false );
    }

    void editP( int x, int y ) {
        switch( mouseChooser.getSelectedIndex() ) {
            case MOUSE_GAUSS:
                editPGauss( x, y );
                return;
            case MOUSE_EIGEN:
                findGridPoint2D( viewPMap, x, y );
                editPEigen( x, y );
                return;
        }
    }

    int sign( int x ) {
        return ( x < 0 ) ? -1 : ( x == 0 ) ? 0 : 1;
    }

    int abs( int x ) {
        return x < 0 ? -x : x;
    }

    void editFuncPoint( int x, int y, double v ) {
        if( !dragSet && !dragClear ) {
            dragClear = func[x][y] > .1;
            dragSet = !dragClear;
        }
        func[x][y] = ( dragSet ) ? (float)v : 0f;
        dragStop = editingFunc = true;
        cv.repaint( pause );
    }

    void editXEigen() {
        int i, j;
        for( i = 0; i != maxTerms; i++ ) {
            for( j = 0; j != maxTerms; j++ ) {
                func[i][j] = 0;
            }
        }
        func[selectedGridX][selectedGridY] = 1;
        transform( true );
    }

    void editPEigen( int x, int y ) {
        int i, j;
        getMomentumCoords( viewPMap, x, y );
        for( i = 0; i <= maxTerms; i++ ) {
            for( j = 0; j <= maxTerms; j++ ) {
                int x1 = i - maxTerms / 2;
                int y1 = j - maxTerms / 2;
                double cx = Math.cos( momentumX * x1 );
                double cy = Math.cos( momentumY * y1 );
                double sx = Math.sin( momentumX * x1 );
                double sy = Math.sin( momentumY * y1 );
                func[i][j] = (float)( cx * cy - sx * sy );
                funci[i][j] = (float)( cx * sy + cy * sx );
            }
        }
        transform( false );
    }


    void editXGauss( int x, int y ) {
        int i, j;
        int gx = x - dragX + 5;
        int gy = y - dragY + 5;
        double wx = 1 / ( abs( gx ) + .0001 );
        double wy = 1 / ( abs( gy ) + .0001 );
        wx = -wx * wx * 10;
        wy = -wy * wy * 10;
        lastGaussWx = wx;
        lastGaussWy = wy;
        double rm = 32. / sampleCount;
        rm *= rm;
        wx *= aspectRatio * aspectRatio;
        for( i = 0; i != maxTerms; i++ ) {
            for( j = 0; j != maxTerms; j++ ) {
                int x1 = i - selectedGridX;
                int y1 = j - selectedGridY;
                func[i][j] = (float)Math.exp( rm * ( wx * x1 * x1 + wy * y1 * y1 ) );
            }
        }
        transform( true );
    }

    void editXGaussP( int x, int y ) {
        int i, j;
        getMomentumCoords( viewXMap,
                           x - dragX + viewXMap.x + viewXMap.width / 2,
                           y - dragY + viewXMap.y + viewXMap.height / 2 );
        double wx = lastGaussWx;
        double wy = lastGaussWy;
        wx *= aspectRatio * aspectRatio;
        double rm = 32. / sampleCount;
        rm *= rm;
        for( i = 0; i <= maxTerms; i++ ) {
            for( j = 0; j <= maxTerms; j++ ) {
                int x1 = i - selectedGridX;
                int y1 = j - selectedGridY;
                double n = Math.exp( rm * ( wx * x1 * x1 + wy * y1 * y1 ) );
                double cx = Math.cos( momentumX * x1 );
                double cy = Math.cos( momentumY * y1 );
                double sx = Math.sin( momentumX * x1 );
                double sy = Math.sin( momentumY * y1 );
                func[i][j] = (float)( n * ( cx * cy - sx * sy ) );
                funci[i][j] = (float)( n * ( cx * sy + cy * sx ) );
            }
        }
        transform( false );
    }

    void editXSquare( int x, int y ) {
        doBlank();
        int res1 = maxTerms + 1;
        int x1 = ( x - viewXMap.x ) * res1 / viewXMap.width;
        int y1 = ( y - viewXMap.y ) * res1 / viewXMap.height;
        int x2 = ( dragX - viewXMap.x ) * res1 / viewXMap.width;
        int y2 = ( dragY - viewXMap.y ) * res1 / viewXMap.height;
        if( x2 < x1 ) {
            int q = x1;
            x1 = x2;
            x2 = q;
        }
        if( y2 < y1 ) {
            int q = y1;
            y1 = y2;
            y2 = q;
        }
        int i, j;
        for( i = x1; i <= x2; i++ ) {
            for( j = y1; j <= y2; j++ ) {
                func[i][j] = 1;
            }
        }
        transform( true );
    }

    void editXCircle( int x, int y ) {
        doBlank();
        int res1 = maxTerms + 1;
        int x1 = ( x - viewXMap.x ) * res1 / viewXMap.width;
        int y1 = ( y - viewXMap.y ) * res1 / viewXMap.height;
        int cx = ( dragX - viewXMap.x ) * res1 / viewXMap.width;
        int cy = ( dragY - viewXMap.y ) * res1 / viewXMap.height;
        double rx = 1. / abs( x1 - cx );
        double ry = 1. / abs( y1 - cy );
        rx *= rx;
        ry *= ry;
        int i, j;
        for( i = 1; i != sampleCount; i++ ) {
            for( j = 1; j != sampleCount; j++ ) {
                if( ( i - cx ) * ( i - cx ) * rx + ( j - cy ) * ( j - cy ) * ry <= 1 ) {
                    func[i][j] = 1;
                }
            }
        }
        transform( true );
    }

    double momentumX, momentumY;

    void getMomentumCoords( QuantumBoxFrame.View v, int x, int y ) {
        int pres = maxTerms * 2;
        int s0 = ( 101 - pZoomBar.getValue() ) * pres / 100;
        momentumX =
                ( ( ( x - v.x - 1 ) * s0 / ( v.width - 2 ) ) - s0 / 2 ) *
                pi / maxTerms;
        momentumY =
                ( ( ( y - v.y - 1 ) * s0 / ( v.height - 2 ) ) - s0 / 2 ) *
                pi / maxTerms;
        double maxp = 2.6; // determined by trial and error
        if( momentumX > maxp ) {
            momentumX = maxp;
        }
        if( momentumY > maxp ) {
            momentumY = maxp;
        }
        if( momentumX < -maxp ) {
            momentumX = -maxp;
        }
        if( momentumY < -maxp ) {
            momentumY = -maxp;
        }
    }

    void editPGauss( int x, int y ) {
        int i, j;
        int gx = x - dragX;
        int gy = y - dragY;
        /*double wx = (abs(gx)+.0001);
      double wy = (abs(gy)+.0001);
      wx = -wx*wx/100;
      wy = -wy*wy/100;*/
        double wx = aspectRatio / ( abs( gx ) + .0001 );
        double wy = 1 / ( abs( gy ) + .0001 );
        wx = -wx * wx * 10;
        wy = -wy * wy * 10;
        getMomentumCoords( viewPMap, dragX, dragY );
        for( i = 0; i <= maxTerms; i++ ) {
            for( j = 0; j <= maxTerms; j++ ) {
                int x1 = i - maxTerms / 2;
                int y1 = j - maxTerms / 2;
                double n = Math.exp( wx * x1 * x1 + wy * y1 * y1 );
                double cx = Math.cos( momentumX * x1 );
                double cy = Math.cos( momentumY * y1 );
                double sx = Math.sin( momentumX * x1 );
                double sy = Math.sin( momentumY * y1 );
                func[i][j] = (float)( n * ( cx * cy - sx * sy ) );
                funci[i][j] = (float)( n * ( cx * sy + cy * sx ) );
            }
        }
        transform( false );
    }

    public void componentHidden( ComponentEvent e ) {
    }

    public void componentMoved( ComponentEvent e ) {
    }

    public void componentShown( ComponentEvent e ) {
        cv.repaint( pause );
    }

    public void componentResized( ComponentEvent e ) {
        handleResize();
        cv.repaint( pause );
    }

    public void actionPerformed( ActionEvent e ) {
        if( e.getSource() == exitItem ) {
            applet.destroyFrame();
            return;
        }
        cv.repaint();
        if( e.getSource() == groundButton ) {
            doGround();
        }
        if( e.getSource() == blankButton ) {
            doBlank();
        }
        if( e.getSource() == normalizeButton ) {
            normalize();
        }
        if( e.getSource() == maximizeButton ) {
            maximize();
        }
    }

    public void adjustmentValueChanged( AdjustmentEvent e ) {
        System.out.print( ( (Scrollbar)e.getSource() ).getValue() + "\n" );
        if( e.getSource() == resBar ) {
            setResolution();
        }
        if( e.getSource() == aspectBar ) {
            setResolution();
        }
        if( e.getSource() == phasorBar ) {
            maxDispTerms = phasorBar.getValue();
        }
        cv.repaint( pause );
    }

    public boolean handleEvent( Event ev ) {
        if( ev.id == Event.WINDOW_DESTROY ) {
            applet.destroyFrame();
            return true;
        }
        return super.handleEvent( ev );
    }

    void findGridPoint2D( QuantumBoxFrame.View v, int mx, int my ) {
        int res1 = maxTerms + 1;
        selectedGridX = ( mx - v.x ) * res1 / v.width;
        selectedGridY = ( my - v.y ) * res1 / v.height;
        int f = 1;
        if( selectedGridX < f ) {
            selectedGridX = f;
        }
        if( selectedGridY < f ) {
            selectedGridY = f;
        }
        if( selectedGridX > sampleCount - f ) {
            selectedGridX = sampleCount - f;
        }
        if( selectedGridY > sampleCount - f ) {
            selectedGridY = sampleCount - f;
        }
        selectedGridFunc = func[selectedGridX][selectedGridY];
    }

    public void mouseDragged( MouseEvent e ) {
        dragging = true;
        edit( e );
    }

    public void mouseMoved( MouseEvent e ) {
        if( dragging ) {
            return;
        }
        int x = e.getX();
        int y = e.getY();
        dragX = x;
        dragY = y;
        int oldCoefX = selectedCoefX;
        int oldCoefY = selectedCoefY;
        selectedCoefX = -1;
        selectedCoefY = -1;
        selectedPaneHandle = -1;
        selection = 0;
        int i;
        for( i = 1; i != viewCount; i++ ) {
            int dy = y - viewList[i].paneY;
            if( dy >= -3 && dy <= 3 ) {
                selectedPaneHandle = i;
                selection = SEL_HANDLE;
            }
        }
        if( viewXMap != null && viewXMap.inside( x, y ) ) {
            selection = SEL_X;
        }
        else if( viewPMap != null && viewPMap.inside( x, y ) ) {
            selection = SEL_P;
        }
        else if( viewPotential != null && viewPotential.contains( x, y ) ) {
            selection = SEL_POTENTIAL;
            findStateByEnergy( y );
        }
        else if( viewStatesMap != null && viewStatesMap.inside( x, y ) ) {
            int termWidth = getTermWidth();
            selectedCoefX = ( x - viewStatesMap.x ) / termWidth + 1;
            selectedCoefY = ( y - viewStatesMap.y ) / termWidth + 1;
            if( selectedCoefX > maxDispTerms ) {
                selectedCoefX = selectedCoefY = -1;
            }
            if( selectedCoefY > maxDispTerms ) {
                selectedCoefX = selectedCoefY = -1;
            }
            if( selectedCoefX <= 0 || selectedCoefY <= 0 ) {
                selectedCoefX = selectedCoefY = -1;
            }
            if( selectedCoefX != -1 && selectedCoefY != -1 ) {
                selection = SEL_STATES;
            }
        }
        if( selectedCoefX != oldCoefX || selectedCoefY != oldCoefY ) {
            cv.repaint( pause );
        }
    }

    void findStateByEnergy( int y ) {
        int i, j;
        int floory = viewPotential.y + viewPotential.height - 5;
        double ymult = 200;
        double dist = 100;
        for( i = 1; i != sampleCount; i++ ) {
            for( j = 1; j != sampleCount; j++ ) {
                int yy = floory - (int)( ymult * elevels[i][j] );
                double d = Math.abs( y - yy );
                if( d < dist ) {
                    dist = d;
                    selectedCoefX = i;
                    selectedCoefY = j;
                }
            }
        }
    }

    public void mouseClicked( MouseEvent e ) {
        if( selection == SEL_STATES ) {
            editMagClick();
        }
        if( e.getClickCount() == 2 && selectedCoefX != -1 ) {
            enterSelectedState();
        }
    }

    public void mouseEntered( MouseEvent e ) {
    }

    public void mouseExited( MouseEvent e ) {
        if( !dragging ) {
            if( selectedCoefX != -1 ) {
                selectedCoefX = selectedCoefY = -1;
                cv.repaint( pause );
            }
            if( selectedPaneHandle != -1 ) {
                selectedPaneHandle = -1;
                cv.repaint( pause );
            }
        }
    }

    public void mousePressed( MouseEvent e ) {
        mouseMoved( e );
        if( ( e.getModifiers() & MouseEvent.BUTTON1_MASK ) == 0 ) {
            return;
        }
        if( selection == SEL_X ) {
            findGridPoint2D( viewXMap, e.getX(), e.getY() );
        }
        else if( selection == SEL_P ) {
            findGridPoint2D( viewPMap, e.getX(), e.getY() );
        }
        dragStartX = e.getX();
        dragStartY = e.getY();
        if( selectedCoefX != -1 ) {
            magDragStart = magcoef[selectedCoefX][selectedCoefY];
        }
        dragging = true;
        edit( e );
    }

    public void mouseReleased( MouseEvent e ) {
        if( ( e.getModifiers() & MouseEvent.BUTTON1_MASK ) == 0 ) {
            return;
        }
        dragging = editingFunc = dragStop = showModeDEBUG = false;
        dragSet = dragClear = false;
        mouseMoved( e );
        cv.repaint( pause );
    }

    public void itemStateChanged( ItemEvent e ) {
        if( e.getItemSelectable() == stoppedCheck ) {
            cv.repaint( pause );
            return;
        }
        if( e.getItemSelectable() instanceof CheckboxMenuItem ) {
            handleResize();
            cv.repaint( pause );
        }
        if( e.getItemSelectable() == alwaysNormItem &&
            alwaysNormItem.getState() ) {
            normalize();
            alwaysMaxItem.setState( false );
            cv.repaint( pause );
        }
        if( e.getItemSelectable() == alwaysMaxItem &&
            alwaysMaxItem.getState() ) {
            maximize();
            alwaysNormItem.setState( false );
            cv.repaint( pause );
        }
        int i;
        for( i = 0; i != waveFunctionMenu.countItems(); i++ ) {
            if( e.getItemSelectable() == waveFunctionMenu.getItem( i ) ) {
                int j;
                ( (CheckboxMenuItem)waveFunctionMenu.getItem( i ) )
                        .setState( true );
                for( j = 0; j != waveFunctionMenu.countItems(); j++ ) {
                    if( i != j ) {
                        ( (CheckboxMenuItem)waveFunctionMenu.getItem( j ) )
                                .setState( false );
                    }
                }
            }
        }
    }

    class PhaseColor {
        public double r, g, b;

        PhaseColor( double rr, double gg, double bb ) {
            r = rr;
            g = gg;
            b = bb;
        }
    }

    class View extends Rectangle {
        View() {
        }

        View( QuantumBoxFrame.View v ) {
            super( v );
        }

        double scale;
        int paneY;
        MemoryImageSource imageSource;
        Image memimage;
        int pixels[];
    }

    double t;
    double aspectRatio = 1;
    double magcoef[][];//the wave function..?
    double phasecoef[][];
    double phasecoefcos[][];
    double phasecoefsin[][];
    double phasecoefadj[][];
    //    double modephasecos;
    double elevels[][];
    float data[];
    static final double pi = 3.14159265358979323846;
    double step;
    float func[][];
    float funci[][];
    float pfuncr[][], pfunci[][];
    static final double epsilon = .00001;
    static final double epsilon2 = .003;
    Random random;
    int maxTerms = 16;
    int maxDispTerms = 10;
    int sampleCount;


    void setResolution() {
        int q = resBar.getValue();
        sampleCount = 1;
        while( q-- > 0 ) {
            sampleCount *= 2;
        }
        if( sampleCount < 8 ) {
            sampleCount = 8;
        }
        int oldMaxTerms = maxTerms;
        maxTerms = sampleCount;
        System.out.print( "sampleCount = " + maxTerms + "\n" );
        double oldmagcoef[][] = magcoef;
        double oldphasecoefadj[][] = phasecoefadj;
        magcoef = new double[maxTerms][maxTerms];
        phasecoef = new double[maxTerms][maxTerms];
        phasecoefcos = new double[maxTerms][maxTerms];
        phasecoefsin = new double[maxTerms][maxTerms];
        phasecoefadj = new double[maxTerms][maxTerms];
        func = new float[maxTerms + 1][maxTerms + 1];
        funci = new float[maxTerms + 1][maxTerms + 1];
        pfuncr = pfunci = null;
        step = pi / sampleCount;
        aspectRatio = aspectBar.getValue() / 10.;
        int i, j;
        data = new float[maxTerms * maxTerms * 2 * 4];
        elevels = new double[maxTerms][maxTerms];
        for( i = 0; i != maxTerms; i++ ) {
            for( j = 0; j != maxTerms; j++ ) {
                elevels[i][j] = i * i / ( aspectRatio * aspectRatio ) + j * j;
            }
        }
        double mult = .01 / elevels[1][1];
        for( i = 0; i != maxTerms; i++ ) {
            for( j = 0; j != maxTerms; j++ ) {
                elevels[i][j] *= mult;
            }
        }
        if( oldmagcoef != null ) {
            for( i = 0; i != oldMaxTerms && i != maxTerms; i++ ) {
                for( j = 0; j != oldMaxTerms && j != maxTerms; j++ ) {
                    magcoef[i][j] = oldmagcoef[i][j];
                    phasecoefadj[i][j] = oldphasecoefadj[i][j];
                }
            }
        }
        setupDisplay();
    }

    int getrand( int x ) {
        int q = random.nextInt();
        if( q < 0 ) {
            q = -q;
        }
        return q % x;
    }

    // given a QuantumBox shape (func[][]), calculate the frequencies.
    // Unless novel is true, we also preserve the imaginary parts
    // (funci[][]) which is the same as preserving the velocity of the
    // QuantumBox (funci is not quite the velocity but all the velocity
    // information is contained in it).
    void transform( boolean novel ) {
        t = 0;
        int nn[] = new int[2];
        nn[0] = nn[1] = maxTerms * 2;
        int x, y;
        int ymult = maxTerms * 4;
        int mx = maxTerms * 2;
        float sign = -1;
        for( x = 0; x != maxTerms * maxTerms * 8; x++ ) {
            data[x] = 0;
        }
        for( x = 1; x < sampleCount; x++ ) {
            for( y = 1; y < sampleCount; y++ ) {
                float fi = ( novel ) ? 0 : (float)funci[x][y];
                // copy func[x][y] to data array
                data[x * 2 + y * ymult] = func[x][y];
                data[x * 2 + y * ymult + 1] = fi;
                // copy func[x][y] to (x,-y), (-x,-y), (x,-y)
                data[( mx - x ) * 2 + y * ymult] = sign * func[x][y];
                data[( mx - x ) * 2 + y * ymult + 1] = sign * fi;
                data[( mx - x ) * 2 + ( mx - y ) * ymult] = func[x][y];
                data[( mx - x ) * 2 + ( mx - y ) * ymult + 1] = fi;
                data[x * 2 + ( mx - y ) * ymult] = sign * func[x][y];
                data[x * 2 + ( mx - y ) * ymult + 1] = sign * fi;
            }
        }
        ndfft( data, nn, 2, 1 );
        double norm = -4. / ( mx * mx );

        // copy frequency info
        for( x = 0; x != maxTerms; x++ ) {
            for( y = 0; y != maxTerms; y++ ) {
                double a = data[x * 2 + y * ymult] * norm;
                double b = data[x * 2 + y * ymult + 1] * norm;
                if( a < epsilon && a > -epsilon ) {
                    a = 0;
                }
                if( b < epsilon && b > -epsilon ) {
                    b = 0;
                }
                if( novel ) {
                    b = 0;
                }
                // convert complex coefficient to magnitude and phase
                magcoef[x][y] = Math.sqrt( a * a + b * b );
                double ph2 = Math.atan2( b, a );
                phasecoefadj[x][y] = ph2;
                phasecoef[x][y] = ph2;
            }
        }
        cv.repaint( pause );
        if( alwaysNormItem.getState() ) {
            normalize();
        }
        else if( alwaysMaxItem.getState() ) {
            maximize();
        }
    }

    private void increaseTime( double tadd ) {
        long sysTime = System.currentTimeMillis();
        if( lastTime == 0 ) {
            lastTime = sysTime;
        }
        tadd *= ( sysTime - lastTime ) * ( 1 / 500. );
        t += tadd;
        lastTime = sysTime;
    }

    private void resetTime() {
        t = 0;
    }

    private void updatePhases() {
        int i;
        int j;
        for( i = 0; i != maxTerms; i++ ) {
            for( j = 0; j != maxTerms; j++ ) {
                if( magcoef[i][j] < epsilon && magcoef[i][j] > -epsilon ) {
                    magcoef[i][j] = 0;
                    phasecoef[i][j] = 0;
                    phasecoefadj[i][j] = 0;
                    continue;
                }

                phasecoef[i][j] =
                        ( -elevels[i][j] * t + phasecoefadj[i][j] ) % ( 2 * pi );
                phasecoefcos[i][j] = Math.cos( phasecoef[i][j] );
                phasecoefsin[i][j] = Math.sin( phasecoef[i][j] );
            }
        }
        double norm = 0;
        double normmult = 0;
        double normmult2 = 0;
        for( i = 0; i != maxTerms; i++ ) {
            for( j = 0; j != maxTerms; j++ ) {
                norm += magcoef[i][j] * magcoef[i][j];
            }
        }
        normmult2 = 1 / norm;
        if( norm == 0 ) {
            normmult2 = 0;
        }
        normmult = Math.sqrt( normmult2 );
        genFunc( (float)normmult );
    }

    void reinit() {
        doBlank();
        magcoef[1][3] = 1;
        magcoef[3][1] = 1;
        phasecoefadj[3][1] = pi / 2;
    }

    int min( int x, int y ) {
        return ( x < y ) ? x : y;
    }

    // do fundamental
    void doGround() {
        int x, y;
        for( x = 0; x != sampleCount; x++ ) {
            for( y = 0; y != sampleCount; y++ ) {
                magcoef[x][y] = 0;
            }
        }
        magcoef[1][1] = 1;
    }

    void doBlank() {
        int x, y;
        for( x = 0; x <= sampleCount; x++ ) {
            for( y = 0; y <= sampleCount; y++ ) {
                func[x][y] = 0;
            }
        }
        transform( true );
    }

    void normalize() {
        double norm = 0;
        int i, j;
        for( i = 0; i != sampleCount; i++ ) {
            for( j = 0; j != sampleCount; j++ ) {
                norm += magcoef[i][j] * magcoef[i][j];
            }
        }
        if( norm == 0 ) {
            return;
        }
        double normmult = 1 / Math.sqrt( norm );
        for( i = 0; i != sampleCount; i++ ) {
            for( j = 0; j != sampleCount; j++ ) {
                magcoef[i][j] *= normmult;
            }
        }
//        cv.repaint( pause );
    }

    void maximize() {
        int i, j;
        double maxm = 0;
        for( i = 0; i != sampleCount; i++ ) {
            for( j = 0; j != sampleCount; j++ ) {
                if( magcoef[i][j] > maxm ) {
                    maxm = magcoef[i][j];
                }
            }
        }
        if( maxm == 0 ) {
            return;
        }
        for( i = 0; i != sampleCount; i++ ) {
            for( j = 0; j != sampleCount; j++ ) {
                magcoef[i][j] *= 1 / maxm;
            }
        }
//        cv.repaint( pause );
    }

    void genFunc( float normmult ) {
        int nn[] = new int[2];
        nn[0] = nn[1] = maxTerms * 2;
        int x, y;
        int ymult = maxTerms * 4;
        int mx = maxTerms * 2;
        float sign = -1;
        for( x = 0; x != maxTerms * maxTerms * 8; x++ ) {
            data[x] = 0;
        }
        for( x = 0; x != sampleCount; x++ ) {
            for( y = 0; y != sampleCount; y++ ) {
                float c = (float)phasecoefcos[x][y];
                float s = (float)phasecoefsin[x][y];
                //
                // use these formulas to generate mode functions using
                // inverse fft:
                //
                // cos(xn) -> (exp(ixn)+exp(-ixn))/2
                // cos(xn)cos(yn) -> .25*(exp(ixn)exp(iyn))
                //    +exp(-ixn)exp(-iyn)+exp(-ixn)exp(iyn)+exp(ixn)exp(-iyn)
                // sin(xn) -> (exp(ixn)-exp(-ixn))/2i
                // sin(xn)sin(yn) -> -.25*(exp(ixn)exp(iyn)
                //    +exp(-ixn)exp(-iyn)-exp(-ixn)exp(iyn)-exp(ixn)exp(-iyn)
                //
                float a = (float)( -.25 * magcoef[x][y] );
                float b = 0;
                float ap = a * c - b * s;
                float bp = b * c + a * s;
                data[x * 2 + y * ymult] = ap;
                data[x * 2 + y * ymult + 1] = bp;
                if( x > 0 ) {
                    data[( mx - x ) * 2 + y * ymult] = sign * ap;
                    data[( mx - x ) * 2 + y * ymult + 1] = sign * bp;
                    if( y > 0 ) {
                        data[( mx - x ) * 2 + ( mx - y ) * ymult] = ap;
                        data[( mx - x ) * 2 + ( mx - y ) * ymult + 1] = bp;
                    }
                }
                if( y > 0 ) {
                    data[x * 2 + ( mx - y ) * ymult] = sign * ap;
                    data[x * 2 + ( mx - y ) * ymult + 1] = sign * bp;
                }
            }
        }
        ndfft( data, nn, 2, -1 );
        normmult *= ( 1 / 16.f );
        // double tot = 0;
        for( x = 0; x <= sampleCount; x++ ) {
            for( y = 0; y <= sampleCount; y++ ) {
                func[x][y] = data[x * 2 + y * ymult] * normmult;
                funci[x][y] = data[x * 2 + y * ymult + 1] * normmult;
                // tot += func[x][y]*func[x][y] + funci[x][y]*funci[x][y];
            }
        }
        // System.out.print("tot " + tot+ "\n");
    }

    double logep2 = 0;

    int logcoef( double x ) {
        double ep2 = epsilon2;
        int sign = ( x < 0 ) ? -1 : 1;
        x *= sign;
        if( x < ep2 ) {
            return 0;
        }
        if( logep2 == 0 ) {
            logep2 = -Math.log( 2 * ep2 );
        }
        return (int)( 255 * sign * ( Math.log( x + ep2 ) + logep2 ) / logep2 );
    }

    void enterSelectedState() {
        int i, j;
        for( i = 0; i != maxTerms; i++ ) {
            for( j = 0; j != maxTerms; j++ ) {
                if( selectedCoefX != i || selectedCoefY != j ) {
                    magcoef[i][j] = 0;
                }
            }
        }
        magcoef[selectedCoefX][selectedCoefY] = 1;
    }

    // n-dimensional discrete FFT from Numerical Recipes
    void ndfft( float data[], int nn[], int ndim, int isign ) {
        int ntot = 1;
        int nprev = 1;
        int idim;
        float i2pi = (float)( isign * 2 * pi );

        for( idim = 0; idim < ndim; idim++ ) {
            ntot *= nn[idim];
        }
        //int steps = 0;

        for( idim = 0; idim < ndim; idim++ ) {

            int n = nn[idim];
            int nrem = ntot / ( n * nprev );
            int ip1 = 2 * nprev;
            int ip2 = ip1 * n;
            int ip3 = ip2 * nrem;
            int i2rev = 0;
            int i2;
            int ifp1;

            /*
            * Bit reversal stuff.
            */

            for( i2 = 0; i2 < ip2; i2 += ip1 ) {

                int ibit;

                if( i2 < i2rev ) {

                    int i1;

                    for( i1 = i2; i1 < i2 + ip1; i1 += 2 ) {

                        int i3;

                        for( i3 = i1; i3 < ip3; i3 += ip2 ) {

                            int i3rev = i2rev + i3 - i2;
                            float tempr = data[i3];
                            float tempi = data[i3 + 1];

                            data[i3] = data[i3rev];
                            data[i3 + 1] = data[i3rev + 1];
                            data[i3rev] = tempr;
                            data[i3rev + 1] = tempi;
                            //steps++;
                        }

                    }

                }

                ibit = ip2 / 2;
                while( ( ibit > ip1 ) && ( i2rev > ibit - 1 ) ) {

                    i2rev -= ibit;
                    ibit /= 2;

                }

                i2rev += ibit;

            }

            /*
            * Danielson-Lanczos stuff.
            */

            ifp1 = ip1;
            while( ifp1 < ip2 ) {

                int ifp2 = 2 * ifp1;
                float theta = i2pi / ( (float)ifp2 / ip1 );
                float wpr;
                float wpi;
                float wr = 1.0f;
                float wi = 0.0f;
                int i3;

                wpr = (float)Math.sin( 0.5 * theta );
                wpr *= wpr * -2.0;
                wpi = (float)Math.sin( theta );

                for( i3 = 0; i3 < ifp1; i3 += ip1 ) {

                    int i1;
                    float wtemp;

                    for( i1 = i3; i1 < i3 + ip1; i1 += 2 ) {

                        for( i2 = i1; i2 < ip3; i2 += ifp2 ) {

                            int i21 = i2 + 1;
                            int k2 = i2 + ifp1;
                            int k21 = k2 + 1;
                            float tempr = ( wr * data[k2] ) -
                                          ( wi * data[k21] );
                            float tempi = ( wr * data[k21] ) +
                                          ( wi * data[k2] );

                            data[k2] = data[i2] - tempr;
                            data[k21] = data[i21] - tempi;

                            data[i2] += tempr;
                            data[i21] += tempi;
                            //steps++;

                        }

                    }

                    wtemp = wr;
                    wr += ( wr * wpr ) - ( wi * wpi );
                    wi += ( wi * wpr ) + ( wtemp * wpi );

                }
                ifp1 = ifp2;

            }
            nprev *= n;

        }
        //System.out.print(steps + "\n");
    }

}
