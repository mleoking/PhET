/* Copyright 2004, Sam Reid */
package org.falstad;

import org.netlib.lapack.Dstedc;
import org.netlib.util.intW;

import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;
import java.util.Vector;

/**
 * User: Sam Reid
 * Date: Feb 9, 2006
 * Time: 9:15:27 PM
 * Copyright (c) Feb 9, 2006 by Sam Reid
 */
class QuantumStatesFrame extends Frame implements ComponentListener, ActionListener, MouseMotionListener, MouseListener, ItemListener, DecentScrollbarListener {
    Thread engine = null;
    Dimension winSize;
    Image dbimage;
    Random random;
    int stateCount;
    int elevelCount;
    int maxStateCount = 500;
    int sampleCount = 80;
    int pSampleCount;
    double modes[][];
    double modesLeft[][];
    public static final double epsilon = .00001;
    public static final double epsilon2 = .003;
    public static final double phasorBaseEnergy = 1;
    public static final double infiniteEnergy = 1000;

    public String getAppletInfo() {
        return "QuantumStates by Paul Falstad";
    }

    Button blankButton;
    Button groundButton;
    Button normalizeButton;
    Button maximizeButton;
    Button rescaleButton;
    Checkbox stoppedCheck;
    CheckboxMenuItem eCheckItem;
    CheckboxMenuItem xCheckItem;
    CheckboxMenuItem pCheckItem;
    CheckboxMenuItem densityCheckItem;
    CheckboxMenuItem sumAllCheckItem;
    CheckboxMenuItem parityCheckItem;
    CheckboxMenuItem currentCheckItem;
    CheckboxMenuItem leftRightCheckItem;
    CheckboxMenuItem infoCheckItem;
    CheckboxMenuItem statesCheckItem;
    CheckboxMenuItem expectCheckItem;
    CheckboxMenuItem uncertaintyCheckItem;
    CheckboxMenuItem probCheckItem;
    CheckboxMenuItem probPhaseCheckItem;
    CheckboxMenuItem reImCheckItem;
    CheckboxMenuItem magPhaseCheckItem;
    CheckboxMenuItem alwaysNormItem;
    CheckboxMenuItem alwaysMaxItem;
    CheckboxMenuItem adiabaticItem;
    Menu waveFunctionMenu;
    MenuItem measureEItem;
    MenuItem measureXItem;
    MenuItem exitItem;
    Choice mouseChooser;
    Choice setupChooser;
    Vector setupList;
    QuantumStatesFrame.Setup setup;
    DecentScrollbar forceBar, speedBar, resBar;
    DecentScrollbar massBar, aux1Bar, aux2Bar, aux3Bar;
    Label aux1Label, aux2Label, aux3Label;
    QuantumStatesFrame.View viewPotential, viewX, viewP, viewParity, viewStates, viewSumAll, viewDensity, viewCurrent, viewLeft, viewRight, viewInfo;
    QuantumStatesFrame.View viewList[];
    int viewCount;
    double magcoef[];
    double phasecoef[];
    double phasecoefcos[];
    double phasecoefsin[];
    double phasecoefadj[];
    double elevels[];
    double dispmax[];
    static final double pi = 3.14159265358979323846;
    double step;
    double func[];
    double funci[];
    double pdata[], pdatar[], pdatai[], currentData[], parityData[];
    double pot[];
    double mass;
    int selectedCoef;
    int selectedPaneHandle;
    static final int stateBuffer = 5;
    double selectedPState;
    static final int SEL_NONE = 0;
    static final int SEL_POTENTIAL = 1;
    static final int SEL_X = 2;
    static final int SEL_P = 3;
    static final int SEL_STATES = 4;
    static final int SEL_HANDLE = 5;
    static final int MOUSE_EIGEN = 0;
    static final int MOUSE_EDIT = 1;
    static final int MOUSE_GAUSS = 2;
    static final int MOUSE_TRANSLATE = 3;
    int selection;
    int dragX, dragY;
    int xpoints[], ypoints[];
    boolean dragging;
    boolean startup, selectGround;
    boolean statesChanged, adjustingStates, adjustingWaveFunc;
    boolean setupModified;
    double t;
    int pause;
    static final int phaseColorCount = 480;
    Color phaseColors[];
    QuantumStatesFrame.FFT fft;

    int getrand( int x ) {
        int q = random.nextInt();
        if( q < 0 ) {
            q = -q;
        }
        return q % x;
    }

    QuantumStatesCanvas cv;
    QuantumStates applet;
    NumberFormat showFormat;

    QuantumStatesFrame( QuantumStates a ) {
        super( "1-d Quantum States Applet v1.6" );
        applet = a;
    }

    public void init() {
        startup = true;
        xpoints = new int[5];
        ypoints = new int[5];
        setupList = new Vector();
        QuantumStatesFrame.Setup s = new QuantumStatesFrame.InfiniteWellSetup();
        while( s != null ) {
            setupList.addElement( s );
            s = s.createNext();
        }
        selectedCoef = -1;
        setLayout( new QuantumStatesLayout() );
        cv = new QuantumStatesCanvas( this );
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
        m.add( pCheckItem = getCheckItem( "Momentum" ) );
        pCheckItem.setState( true );
        m.add( sumAllCheckItem = getCheckItem( "Sum All States" ) ); /*m.add(densityCheckItem = getCheckItem("Density of Levels")); densityCheckItem.setState(true);*/
        m.add( parityCheckItem = getCheckItem( "Parity" ) );
        m.add( currentCheckItem = getCheckItem( "Probability Current" ) );
        m.add( leftRightCheckItem = getCheckItem( "Left/Right Waves" ) );
        m.add( infoCheckItem = getCheckItem( "Values/Dimensions" ) );
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
        probPhaseCheckItem.setState( true );
        m2.add( reImCheckItem = getCheckItem( "Real + Imaginary Parts" ) );
        m2.add( magPhaseCheckItem = getCheckItem( "Magnitude + Phase" ) );
        m = new Menu( "Measure" );
        mb.add( m );
        m.add( measureEItem = getMenuItem( "Measure Energy" ) );
        m.add( measureXItem = getMenuItem( "Measure Position" ) );
        setMenuBar( mb );
        m = new Menu( "Options" );
        mb.add( m );
        m.add( alwaysNormItem = getCheckItem( "Always Normalize" ) );
        m.add( alwaysMaxItem = getCheckItem( "Always Maximize" ) );
        m.add( adiabaticItem = getCheckItem( "Adiabatic Changes" ) );
        adiabaticItem.setState( true );
        setMenuBar( mb );
        setupChooser = new Choice();
        int i;
        for( i = 0; i != setupList.size(); i++ ) {
            setupChooser.add( "Setup: " + ( (QuantumStatesFrame.Setup)setupList.elementAt( i ) ).getName() );
        }
        setup = (QuantumStatesFrame.Setup)setupList.elementAt( 0 );
        setupChooser.addItemListener( this );
        add( setupChooser );
        mouseChooser = new Choice();
        mouseChooser.add( "Mouse = Set Eigenstate" );
        mouseChooser.add( "Mouse = Edit Function" );
        mouseChooser.add( "Mouse = Create Gaussian" );
        mouseChooser.add( "Mouse = Translate Function" );
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
        add( rescaleButton = new Button( "Rescale Graphs" ) );
        rescaleButton.addActionListener( this );
        stoppedCheck = new Checkbox( "Stopped" );
        stoppedCheck.addItemListener( this );
        add( stoppedCheck );
        add( new Label( "Simulation Speed", Label.CENTER ) );
        add( speedBar = new DecentScrollbar( this, 80, 1, 300 ) );
        add( new Label( "Resolution", Label.CENTER ) );
        add( resBar = new DecentScrollbar( this, 260, 180, maxStateCount ) );
        add( new Label( "Particle Mass", Label.CENTER ) );
        add( massBar = new DecentScrollbar( this, 16, 1, 100 ) );
        add( aux1Label = new Label( "Aux 1", Label.CENTER ) );
        add( aux1Bar = new DecentScrollbar( this, 50, 1, 100 ) );
        add( aux2Label = new Label( "Aux 2", Label.CENTER ) );
        add( aux2Bar = new DecentScrollbar( this, 50, 1, 100 ) );
        add( aux3Label = new Label( "Aux 3", Label.CENTER ) );
        add( aux3Bar = new DecentScrollbar( this, 50, 1, 100 ) );
        try {
            String param = applet.getParameter( "PAUSE" );
            if( param != null ) {
                pause = Integer.parseInt( param );
            }
        }
        catch( Exception e ) { }
        magcoef = new double[maxStateCount];
        phasecoef = new double[maxStateCount];
        phasecoefcos = new double[maxStateCount];
        phasecoefsin = new double[maxStateCount];
        phasecoefadj = new double[maxStateCount];
        dispmax = new double[maxStateCount];
        setResolution();
        phaseColors = new Color[phaseColorCount + 1];
        for( i = 0; i != phaseColorCount; i++ ) {
            int pm = phaseColorCount / 6;
            int a1 = i % pm;
            int a2 = a1 * 255 / pm;
            int a3 = 255 - a2;
            Color c = null;
            switch( i / pm ) {
                case 0:
                    c = new Color( 255, a2, 0 );
                    break;
                case 1:
                    c = new Color( a3, 255, 0 );
                    break;
                case 2:
                    c = new Color( 0, 255, a2 );
                    break;
                case 3:
                    c = new Color( 0, a3, 255 );
                    break;
                case 4:
                    c = new Color( a2, 0, 255 );
                    break;
                case 5:
                    c = new Color( 255, 0, a3 );
                    break;
            }
            phaseColors[i] = c;
        }
        phaseColors[phaseColorCount] = phaseColors[0];
        random = new Random();
        reinit();
        cv.setBackground( Color.black );
        cv.setForeground( Color.lightGray );
        showFormat = DecimalFormat.getInstance();
        showFormat.setMaximumFractionDigits( 2 );
        resize( 750, 600 );
        handleResize();
        Dimension x = getSize();
        Dimension screen = getToolkit().getScreenSize();
        setLocation( ( screen.width - x.width ) / 2, ( screen.height - x.height ) / 2 );
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

    void reinit() {
        doSetup();
    }

    void handleResize() {
        Dimension d = winSize = cv.getSize();
        if( winSize.width == 0 ) {
            return;
        }
        int potsize = ( viewPotential == null ) ? 0 : viewPotential.height;
        int statesize = ( viewStates == null ) ? 50 : viewStates.height;
        viewX = viewP = viewParity = viewCurrent = viewLeft = viewRight = viewStates = viewPotential = viewInfo = viewSumAll = viewDensity = null;
        viewList = new QuantumStatesFrame.View[20];
        int i = 0;
        if( eCheckItem.getState() ) {
            viewList[i++] = viewPotential = new QuantumStatesFrame.View(); /*if (densityCheckItem.getState()) viewList[i++] = viewDensity = new View();*/
        }
        if( xCheckItem.getState() ) {
            viewList[i++] = viewX = new QuantumStatesFrame.View();
        }
        if( sumAllCheckItem.getState() ) {
            viewList[i++] = viewSumAll = new QuantumStatesFrame.View();
        }
        if( pCheckItem.getState() ) {
            viewList[i++] = viewP = new QuantumStatesFrame.View();
        }
        if( parityCheckItem.getState() ) {
            viewList[i++] = viewParity = new QuantumStatesFrame.View();
        }
        if( currentCheckItem.getState() ) {
            viewList[i++] = viewCurrent = new QuantumStatesFrame.View();
        }
        if( leftRightCheckItem.getState() && setup.allowLeftRight() ) {
            viewList[i++] = viewLeft = new QuantumStatesFrame.View();
            viewList[i++] = viewRight = new QuantumStatesFrame.View();
        }
        if( infoCheckItem.getState() ) {
            viewList[i++] = viewInfo = new QuantumStatesFrame.View();
        }
        if( statesCheckItem.getState() ) {
            viewList[i++] = viewStates = new QuantumStatesFrame.View();
        }
        viewCount = i;
        int sizenum = viewCount;
        int toth = winSize.height; // preserve size of potential and state panes if possible
        if( potsize > 0 && viewPotential != null ) {
            sizenum--;
            toth -= potsize;
        }
        if( statesize > 0 && viewStates != null ) {
            sizenum--;
            toth -= statesize;
        } // info pane is fixed size
        int infosize = 4 * 15 + 5;
        if( viewInfo != null ) {
            sizenum--;
            toth -= infosize;
        }
        int cury = 0;
        for( i = 0; i != viewCount; i++ ) {
            QuantumStatesFrame.View v = viewList[i];
            int h = toth / sizenum;
            if( v == viewPotential && potsize > 0 ) {
                h = potsize;
            }
            else if( v == viewStates && statesize > 0 ) {
                h = statesize;
            }
            else if( v == viewInfo ) {
                h = infosize;
            }
            v.x = 0;
            v.width = winSize.width;
            v.y = cury;
            v.height = h;
            cury += h;
        }
        setGraphLines();
        dbimage = createImage( d.width, d.height );
    }

    void setGraphLines() {
        int i;
        for( i = 0; i != viewCount; i++ ) {
            QuantumStatesFrame.View v = viewList[i];
            v.mid_y = v.y + v.height / 2;
            v.ymult = .90 * v.height / 2;
            v.lower_y = (int)( v.mid_y + v.ymult );
            v.ymult2 = v.ymult * 2;
        }
    }

    void doGround() {
        int x;
        for( x = 0; x != stateCount; x++ ) {
            magcoef[x] = 0;
        }
        magcoef[0] = 1;
        t = 0;
        rescaleGraphs();
    }

    void doBlank() {
        t = 0; // workaround bug in IE
        if( winSize != null && winSize.width > 0 ) {
            dbimage = createImage( winSize.width, winSize.height );
        }
        int x;
        for( x = 0; x != sampleCount; x++ ) {
            func[x] = funci[x] = 0;
        }
        for( x = 0; x != stateCount; x++ ) {
            magcoef[x] = 0;
        }
    }

    void normalize() {
        double norm = 0;
        int i;
        for( i = 0; i != stateCount; i++ ) {
            norm += magcoef[i] * magcoef[i];
        }
        if( norm == 0 ) {
            return;
        }
        double normmult = 1 / Math.sqrt( norm );
        for( i = 0; i != stateCount; i++ ) {
            magcoef[i] *= normmult;
        }
        cv.repaint( pause );
    }

    void maximize() {
        int i;
        double maxm = 0;
        for( i = 0; i != stateCount; i++ ) {
            if( magcoef[i] > maxm ) {
                maxm = magcoef[i];
            }
        }
        if( maxm == 0 ) {
            return;
        }
        for( i = 0; i != stateCount; i++ ) {
            magcoef[i] *= 1 / maxm;
        }
        cv.repaint( pause );
    }

    void rescaleGraphs() {
        int i;
        for( i = 0; i != viewCount; i++ ) {
            viewList[i].scale = 0;
        }
    }

    void transform() {
        int x, y;
        t = 0;
        for( y = 0; y != stateCount; y++ ) {
            double a = 0;
            double b = 0;
            for( x = 1; x != sampleCount; x++ ) {
                a += modes[y][x] * func[x];
                b += modes[y][x] * funci[x];
            }
            if( a < epsilon && a > -epsilon ) {
                a = 0;
            }
            if( b < epsilon && b > -epsilon ) {
                b = 0;
            }
            double r = Math.sqrt( a * a + b * b );
            magcoef[y] = r;
            double ph2 = Math.atan2( b, a );
            phasecoefadj[y] = ph2;
            phasecoef[y] = ph2;
        }
        if( alwaysNormItem.getState() ) {
            normalize();
        }
        if( alwaysMaxItem.getState() ) {
            maximize();
        }
    }

    void centerString( Graphics g, String s, int y ) {
        FontMetrics fm = g.getFontMetrics();
        g.drawString( s, ( winSize.width - fm.stringWidth( s ) ) / 2, y );
    }

    public void paint( Graphics g ) {
        cv.repaint();
    }

    long lastTime;

    public void updateQuantumStates( Graphics realg ) {
        Graphics g = dbimage.getGraphics();
        if( winSize == null || winSize.width == 0 ) {
            return;
        }
        boolean allQuiet = true;
        if( !stoppedCheck.getState() && !dragging && !adjustingStates ) {
            int val = speedBar.getValue();
            double tadd = Math.exp( val / 20. ) * ( .1 / 5 );
            long sysTime = System.currentTimeMillis();
            if( lastTime == 0 ) {
                lastTime = sysTime;
            }
            t += tadd * ( sysTime - lastTime ) * ( 1 / 17. );
            lastTime = sysTime;
            allQuiet = false;
        }
        else {
            lastTime = 0;
        }
        Color gray1 = new Color( 76, 76, 76 );
        Color gray2 = new Color( 127, 127, 127 );
        g.setColor( cv.getBackground() );
        g.fillRect( 0, 0, winSize.width, winSize.height );
        g.setColor( cv.getForeground() );
        int i;
        int ox = -1, oy = -1;
        for( i = 1; i != viewCount; i++ ) {
            g.setColor( i == selectedPaneHandle ? Color.yellow : Color.gray );
            g.drawLine( 0, viewList[i].y, winSize.width, viewList[i].y );
        }
        if( statesChanged ) {
            cv.setCursor( Cursor.getPredefinedCursor( WAIT_CURSOR ) );
            if( adjustingStates ) {
                genStates( false );
            }
            else {
                realg.setColor( cv.getBackground() );
                FontMetrics fm = realg.getFontMetrics();
                String cs = "Calculating...";
                realg.fillRect( 0, winSize.height - 30, 20 + fm.stringWidth( cs ), 30 );
                realg.setColor( Color.white );
                realg.drawString( cs, 10, winSize.height - 10 );
                genStates( true );
                if( !adiabaticItem.getState() ) {
                    transform();
                }
                else {
                    rescaleGraphs();
                }
            }
            cv.setCursor( null );
            statesChanged = false;
            if( startup ) {
                magcoef[0] = magcoef[1] = 1;
                startup = false;
            }
            else if( selectGround ) {
                magcoef[0] = 1;
                selectGround = false;
            }
        }
        ox = -1;
        int j;
        double norm = 0;
        if( !adjustingStates ) {
            for( j = 0; j != stateCount; j++ ) {
                if( magcoef[j] < epsilon && magcoef[j] > -epsilon ) {
                    magcoef[j] = phasecoef[j] = phasecoefadj[j] = 0;
                    continue;
                }
                phasecoef[j] = ( -( elevels[j] + phasorBaseEnergy ) * t + phasecoefadj[j] ) % ( 2 * pi );
                if( phasecoef[j] > pi ) {
                    phasecoef[j] -= 2 * pi;
                }
                else if( phasecoef[j] < -pi ) {
                    phasecoef[j] += 2 * pi;
                }
                phasecoefcos[j] = Math.cos( phasecoef[j] );
                phasecoefsin[j] = Math.sin( phasecoef[j] );
                norm += magcoef[j] * magcoef[j];
            }
        }
        double normmult2 = 1 / norm;
        double normmult = Math.sqrt( normmult2 );
        if( norm == 0 ) {
            normmult = normmult2 = 0;
        }
        if( viewPotential != null ) {
            int mid_y = viewPotential.mid_y;
            double ymult = viewPotential.ymult;
            viewPotential.scale = 1;
            g.setColor( gray2 );
            g.drawLine( winSize.width / 2, mid_y - (int)ymult, winSize.width / 2, mid_y + (int)ymult );
            g.setColor( Color.gray );
            for( i = 0; i != elevelCount; i++ ) {
                if( i == stateCount ) {
                    g.setColor( Color.darkGray );
                }
                double dy = elevels[i];
                int y = mid_y - (int)( ymult * dy );
                g.drawLine( 0, y, winSize.width, y );
            }
            g.setColor( Color.white );
            for( i = 0; i != sampleCount; i++ ) {
                int x = winSize.width * i / sampleCount;
                double dy = pot[i];
                int y = mid_y - (int)( ymult * dy );
                if( ox != -1 ) {
                    g.drawLine( ox, oy, x, y );
                }
                ox = x;
                oy = y;
            } // calculate expectation value of E
            if( !adjustingStates && norm != 0 && ( expectCheckItem.getState() || uncertaintyCheckItem.getState() ) ) {
                double expecte = 0;
                double expecte2 = 0;
                for( i = 0; i != stateCount; i++ ) {
                    double prob = magcoef[i] * magcoef[i] * normmult2;
                    expecte += prob * elevels[i];
                    expecte2 += prob * elevels[i] * elevels[i];
                }
                double uncert = Math.sqrt( expecte2 - expecte * expecte );
                if( uncertaintyCheckItem.getState() ) {
                    if( !( uncert >= 0 ) ) {
                        uncert = 0;
                    }
                    g.setColor( Color.blue );
                    int y = mid_y - (int)( ymult * ( expecte + uncert ) );
                    g.drawLine( 0, y, winSize.width, y );
                    y = mid_y - (int)( ymult * ( expecte - uncert ) );
                    if( expecte - uncert >= -1 ) {
                        g.drawLine( 0, y, winSize.width, y );
                    }
                }
                if( expectCheckItem.getState() ) {
                    int y = mid_y - (int)( ymult * expecte );
                    g.setColor( Color.red );
                    g.drawLine( 0, y, winSize.width, y );
                }
            }
            if( selectedCoef != -1 && !dragging ) {
                g.setColor( Color.yellow );
                int y = mid_y - (int)( ymult * elevels[selectedCoef] );
                g.drawLine( 0, y, winSize.width, y );
            }
        }
        ox = -1;
        g.setColor( Color.white );
        double maxf = 0;
        double expectx = 0;
        if( !adjustingStates && !adjustingWaveFunc ) { // calculate wave function
            for( i = 0; i != sampleCount; i++ ) {
                int x = winSize.width * i / sampleCount;
                double dr = 0, di = 0;
                for( j = 0; j != stateCount; j++ ) {
                    dr += magcoef[j] * modes[j][i] * phasecoefcos[j];
                    di += magcoef[j] * modes[j][i] * phasecoefsin[j];
                }
                dr *= normmult;
                di *= normmult;
                func[i] = dr;
                funci[i] = di;
                double dy = dr * dr + di * di;
                expectx += dy * x;
                if( dy > maxf ) {
                    maxf = dy;
                }
            }
        }
        else {
            for( i = 0; i != sampleCount; i++ ) {
                int x = winSize.width * i / sampleCount;
                double dr = func[i], di = funci[i];
                double dy = dr * dr + di * di;
                expectx += dy * x;
                if( dy > maxf ) {
                    maxf = dy;
                }
            }
        }
        if( viewX != null ) { // draw X representation
            int mid_y = viewX.mid_y;
            double ymult = viewX.ymult;
            drawFunction( g, viewX, func, funci, sampleCount, 0 );
            if( selectedCoef != -1 && !dragging ) {
                g.setColor( Color.yellow );
                ox = -1;
                for( i = 0; i != sampleCount; i++ ) {
                    int x = winSize.width * i / sampleCount;
                    double dy = modes[selectedCoef][i] / dispmax[selectedCoef];
                    int y = mid_y - (int)( ymult * dy );
                    if( ox != -1 ) {
                        g.drawLine( ox, oy, x, y );
                    }
                    ox = x;
                    oy = y;
                }
            }
            if( selectedPState != 0 ) {
                g.setColor( Color.yellow );
                ox = -1;
                int s2 = sampleCount * 2;
                for( i = 0; i != s2; i++ ) {
                    int x = winSize.width * i / s2;
                    double dy = Math.cos( selectedPState * ( i - sampleCount ) * .5 );
                    int y = mid_y - (int)( ymult * dy );
                    if( ox != -1 ) {
                        g.drawLine( ox, oy, x, y );
                    }
                    ox = x;
                    oy = y;
                }
            }
        }
        if( viewP != null ) { // draw P representation
            for( i = 0; i != pSampleCount * 2; i++ ) {
                pdata[i] = 0;
            }
            for( i = 0; i != sampleCount; i++ ) {
                int ii = ( i <= sampleCount / 2 ) ? ( sampleCount / 2 - i ) * 2 : ( pSampleCount - ( i - sampleCount / 2 ) ) * 2;
                pdata[ii] = func[sampleCount - 1 - i];
                pdata[ii + 1] = funci[sampleCount - 1 - i];
            }
            fft.transform( pdata, false );
            double pnorm = 1 / Math.sqrt( pSampleCount );
            for( i = 0; i != pSampleCount; i++ ) {
                int ii = ( i <= pSampleCount / 2 ) ? ( pSampleCount / 2 - i ) * 2 : ( pSampleCount - ( i - pSampleCount / 2 ) ) * 2;
                pdatar[i] = pdata[ii] * pnorm;
                pdatai[i] = pdata[ii + 1] * pnorm;
            }
            int offset = pSampleCount / 4;
            drawFunction( g, viewP, pdatar, pdatai, pSampleCount / 2, offset );
        }
        if( viewParity != null ) { // draw parity graph
            double pplus = 0, pminus = 0;
            for( i = 0; i != sampleCount; i++ ) {
                double a1 = func[i];
                double a2 = funci[i];
                double b1 = func[sampleCount - 1 - i];
                double b2 = funci[sampleCount - 1 - i];
                double c1 = ( a1 + b1 ) * ( a1 + b1 ) + ( a2 + b2 ) * ( a2 + b2 );
                double c2 = ( a1 - b1 ) * ( a1 - b1 ) + ( a2 - b2 ) * ( a2 - b2 );
                pplus += c1;
                pminus += c2;
            }
            parityData[90] = Math.sqrt( pplus ) / 2;
            parityData[10] = Math.sqrt( pminus ) / 2;
            drawFunction( g, viewParity, parityData, null, 100, 0 );
        }
        if( viewCurrent != null ) { // draw probability current
            for( i = 0; i != sampleCount - 1; i++ ) {
                double a1 = func[i + 1] - func[i];
                double a2 = funci[i + 1] - funci[i];
                currentData[i] = func[i] * a2 - funci[i] * a1;
            }
            drawFunction( g, viewCurrent, currentData, null, sampleCount, 0 );
        }
        if( viewLeft != null && viewRight != null && !setupModified ) {
            if( viewX != null ) {
                viewLeft.scale = viewRight.scale = viewX.scale;
            }
            if( setup instanceof QuantumStatesFrame.HarmonicOscillatorSetup ) {
                doOscLeftRight( g );
            }
            else if( setup instanceof QuantumStatesFrame.InfiniteWellSetup ) {
                doBoxLeftRight( g, normmult );
            }
        }
        if( viewSumAll != null && !adjustingStates ) {
            double sumx[] = new double[sampleCount];
            for( j = 0; j != stateCount; j++ ) {
                for( i = 0; i != sampleCount; i++ ) {
                    sumx[i] += modes[j][i] * modes[j][i];
                }
            }
            drawFunction( g, viewSumAll, sumx, null, sampleCount, 0 );
        } /*if (viewDensity != null) { double density[] = new double[20]; int dlen = density.length; for (i = 0; i != stateCount; i++) { int e = (int) (1+dlen*(elevels[i]+1)/2); if (e < dlen) density[e]++; } drawFunction(g, viewDensity, density, null, dlen, 0); }*/
        if( viewInfo != null ) {
            String s[] = new String[5];
            setup.getInfo( s, 2 ); // calculate expectation value of E
            double expecte = 0;
            for( i = 0; i != stateCount; i++ ) {
                double prob = magcoef[i] * magcoef[i] * normmult2;
                expecte += prob * elevels[i];
            }
            g.setColor( Color.white );
            s[0] = " = " + getEnergyText( expecte, true ); // we're multiplying t times elevels[x] to get the phase angle. // if t*elevels[x] = realE realt/hbar, then realt = t
//             elevels[x]
//            hbar / realE.
            double realt = t * 6.58212e-16 / convertEnergy( 1, false );
            s[1] = "t = " + getUnitText( realt, "s" );
            String massStr = ", m = " + getUnitText( mass, "eV" ) + "/c^2";
            if( mass == .511e6 ) {
                massStr += " (electron)";
            }
            s[0] += massStr;
            for( i = 0; s[i] != null; i++ ) {
                int y = ( i + 1 ) * 15;
                if( y + 4 < viewInfo.height ) {
                    centerString( g, s[i], y + viewInfo.y );
                }
            }
        }
        if( viewStates != null && !adjustingStates ) { // draw state phasors
            stateColSize = winSize.width / 10;
            if( stateColSize < 20 ) {
                stateColSize = 20;
            }
            for( i = stateColSize - 1; i >= 8; i-- ) {
                int ss = winSize.width / i;
                int h = ss * ( ( stateCount + i - 1 ) / i );
                if( h <= viewStates.height - stateBuffer ) {
                    stateColSize = i;
                }
            }
            stateSize = winSize.width / stateColSize;
            int ss2 = stateSize / 2;
            for( i = 0; i != stateCount; i++ ) {
                int x = stateSize * ( i % stateColSize ) + ss2;
                int y = stateSize * ( i / stateColSize ) + ss2 + viewStates.y + stateBuffer;
                g.setColor( i == selectedCoef ? Color.yellow : ( magcoef[i] == 0 ) ? gray2 : Color.white );
                g.drawOval( x - ss2, y - ss2, stateSize, stateSize );
                int xa = (int)( magcoef[i] * phasecoefcos[i] * ss2 );
                int ya = (int)( -magcoef[i] * phasecoefsin[i] * ss2 );
                g.drawLine( x, y, x + xa, y + ya );
                g.drawLine( x + xa - 1, y + ya, x + xa + 1, y + ya );
                g.drawLine( x + xa, y + ya - 1, x + xa, y + ya + 1 );
            }
        }
        realg.drawImage( dbimage, 0, 0, this );
        if( !stoppedCheck.getState() && !allQuiet ) {
            cv.repaint( pause );
        }
    }

    String getUnitText( double v, String u ) {
        double va = Math.abs( v );
        if( va < 1e-17 ) {
            return "0 " + u;
        }
        if( va < 1e-12 ) {
            return showFormat.format( v * 1e15 ) + " f" + u;
        }
        if( va < 1e-9 ) {
            return showFormat.format( v * 1e12 ) + " p" + u;
        }
        if( va < 1e-6 ) {
            return showFormat.format( v * 1e9 ) + " n" + u;
        }
        if( va < 1e-3 ) {
            return showFormat.format( v * 1e6 ) + " u" + u;
        }
        if( va < 1 ) {
            return showFormat.format( v * 1e3 ) + " m" + u;
        }
        if( va < 1e3 ) {
            return showFormat.format( v ) + " " + u;
        }
        if( va < 1e6 ) {
            return showFormat.format( v * 1e-3 ) + " k" + u;
        }
        if( va < 1e9 ) {
            return showFormat.format( v * 1e-6 ) + " M" + u;
        }
        if( va < 1e12 ) {
            return showFormat.format( v * 1e-9 ) + " G" + u;
        }
        if( va < 1e15 ) {
            return showFormat.format( v * 1e-12 ) + " T" + u;
        }
        return v + " " + u;
    }

    String getEnergyText( double e, boolean abs ) {
        return getUnitText( convertEnergy( e, abs ), "eV" );
    }

    String getLengthText( double x ) { // getEnergyText() assumes a 4 nm screen width
        return getUnitText( pointsToLength( x ), "m" );
    }

    double pointsToLength( double x ) { // getEnergyText() assumes a 4 nm screen width
        return 4e-9 * x / ( sampleCount - 2. );
    }

    double convertEnergy( double e, boolean abs ) { // ground state energy for an electron in a 4 nm infinite well (in eV) // (for 1 Angstrom, it's 37.603)
        double base = .023502; // the calculated ground state energy for a particle of default mass in a // well the full width of the window is 0.001891. So, if we assume the // window is 1 angstrom wide, then we multiply the elevels by base/.001891 to // get eV.
        if( abs ) {
            e += setup.getBaseEnergy();
        }
        return e * base / .0018801;
    }

    int stateColSize, stateSize;

    void drawFunction( Graphics g, QuantumStatesFrame.View view, double fr[], double fi[], int count, int offset ) {
        int i;
        double expectx = 0;
        double expectx2 = 0;
        double maxsq = 0;
        double tot = 0;
        int zero = winSize.width / 2;
        for( i = 0; i != count; i++ ) {
            int x = winSize.width * i / ( count - 1 );
            int ii = i + offset;
            double dr = fr[ii];
            double di = ( fi == null ) ? 0 : fi[ii];
            double dy = dr * dr + di * di;
            if( dy > maxsq ) {
                maxsq = dy;
            }
            int dev = x - zero;
            expectx += dy * dev;
            expectx2 += dy * dev * dev;
            tot += dy;
        }
        expectx /= tot;
        expectx2 /= tot;
        double maxnm = Math.sqrt( maxsq );
        double uncert = Math.sqrt( expectx2 - expectx * expectx );
        int ox = -1, oy = 0;
        double bestscale = 0;
        if( fi != null && ( probCheckItem.getState() || probPhaseCheckItem.getState() ) ) {
            bestscale = 1 / maxsq;
        }
        else {
            bestscale = 1 / maxnm;
        }
        if( !adjustingWaveFunc ) { // adjust scale view.scale *= 1.001;
            if( view.scale > bestscale || view.scale == 0 ) {
                view.scale = bestscale;
            }
            if( view.scale > 1e8 ) {
                view.scale = 1e8;
            }
        }
        g.setColor( Color.gray ); /* double scaler = 1e-10; while (scaler*view.scale*view.ymult*600 < view.height) scaler *= 10; System.out.print(scaler + "\n"); for (i = 0; i != 20; i++) { int y = (int) (view.ymult * i * view.scale * scaler * 10); System.out.print(i + " " + y + "\n"); if (y > view.height/2) break; g.drawLine(winSize.width-5, view.mid_y - y, winSize.width, view.mid_y - y); } */
        if( ( probCheckItem.getState() || probPhaseCheckItem.getState() || magPhaseCheckItem.getState() ) && fi != null ) { // draw probability or magnitude
            g.setColor( Color.white );
            double mult = view.ymult2 * view.scale;
            for( i = 0; i != count; i++ ) {
                int x = winSize.width * i / ( count - 1 );
                double dy = 0;
                int ii = i + offset;
                if( !magPhaseCheckItem.getState() ) {
                    dy = ( fr[ii] * fr[ii] + fi[ii] * fi[ii] );
                }
                else {
                    dy = Math.sqrt( fr[ii] * fr[ii] + fi[ii] * fi[ii] );
                }
                if( !probCheckItem.getState() ) {
                    double ang = Math.atan2( fi[ii], fr[ii] );
                    g.setColor( phaseColors[(int)( ( ang + pi ) * phaseColorCount / ( 2 * pi + .2 ) )] );
                }
                int y = view.lower_y - (int)( mult * dy );
                if( y < view.y ) {
                    y = view.y;
                }
                if( ox != -1 ) {
                    xpoints[0] = ox;
                    ypoints[0] = view.lower_y + 1;
                    xpoints[1] = ox;
                    ypoints[1] = oy;
                    xpoints[2] = x;
                    ypoints[2] = y;
                    xpoints[3] = x;
                    ypoints[3] = view.lower_y + 1;
                    g.fillPolygon( xpoints, ypoints, 4 );
                }
                ox = x;
                oy = y;
            }
        }
        else {
            int mid_y = view.mid_y;
            double mult = view.ymult * view.scale;
            if( fi != null ) {
                g.setColor( Color.blue );
                for( i = 0; i != count; i++ ) {
                    int x = winSize.width * i / ( count - 1 );
                    int ii = i + offset;
                    int y = mid_y - (int)( mult * fi[ii] );
                    if( ox != -1 ) {
                        g.drawLine( ox, oy, x, y );
                    }
                    ox = x;
                    oy = y;
                }
            }
            g.setColor( Color.white );
            ox = -1;
            for( i = 0; i != count; i++ ) {
                int x = winSize.width * i / ( count - 1 );
                int ii = i + offset;
                int y = mid_y - (int)( mult * fr[ii] );
                if( ox != -1 ) {
                    g.drawLine( ox, oy, x, y );
                }
                ox = x;
                oy = y;
            }
        }
        if( maxsq > 0 && fi != null ) {
            expectx += zero;
            if( uncertaintyCheckItem.getState() ) {
                g.setColor( Color.blue );
                g.drawLine( (int)( expectx - uncert ), view.y, (int)( expectx - uncert ), view.y + view.height );
                g.drawLine( (int)( expectx + uncert ), view.y, (int)( expectx + uncert ), view.y + view.height );
            }
            if( expectCheckItem.getState() ) {
                g.setColor( Color.red );
                g.drawLine( (int)expectx, view.y, (int)expectx, view.y + view.height );
            }
        }
    }

    void doOscLeftRight( Graphics g ) {
        int i;
        for( i = 0; i != pSampleCount * 2; i++ ) {
            pdata[i] = 0;
        }
        for( i = 0; i != sampleCount; i++ ) {
            int ii = ( i <= sampleCount / 2 ) ? ( sampleCount / 2 - i ) * 2 : ( pSampleCount - ( i - sampleCount / 2 ) ) * 2;
            pdata[ii] = func[i];
            pdata[ii + 1] = funci[i];
        }
        fft.transform( pdata, false );
        for( i = 2; i != pSampleCount; i++ ) {
            pdata[i] = 0;
        }
        fft.transform( pdata, true );
        double pnorm = 1. / pSampleCount;
        for( i = 0; i != sampleCount; i++ ) {
            int ii = ( i <= sampleCount / 2 ) ? ( sampleCount / 2 - i ) * 2 : ( pSampleCount - ( i - sampleCount / 2 ) ) * 2;
            pdatar[i] = pdata[ii] * pnorm;
            pdatai[i] = pdata[ii + 1] * pnorm;
        }
        drawFunction( g, viewLeft, pdatar, pdatai, sampleCount, 0 );
        for( i = 0; i != sampleCount; i++ ) {
            pdatar[i] = func[i] - pdatar[i];
            pdatai[i] = funci[i] - pdatai[i];
        }
        drawFunction( g, viewRight, pdatar, pdatai, sampleCount, 0 );
    }

    void doBoxLeftRight( Graphics g, double normmult ) {
        if( adjustingStates ) {
            return;
        }
        if( modesLeft == null ) { // calculate imaginary part of left-moving waves // (modes[][] is real part)
            int width = ( (QuantumStatesFrame.InfiniteWellSetup)setup ).getOffset() - 1;
            modesLeft = new double[stateCount][sampleCount];
            int i, j;
            for( i = 0; i != stateCount; i++ ) {
                int ni = i + 1;
                double sgn = ( modes[i][width] > 0 ) ? 1 : -1;
                double mult = sgn * dispmax[i];
                double xmult = pi / ( sampleCount - width * 2 - 1 );
                for( j = width; j != sampleCount - width; j++ ) {
                    double xx = ( j - width ) * xmult;
                    modesLeft[i][j] = Math.cos( xx * ni ) * mult;
                }
            }
        }
        int i, j;
        normmult *= .5;
        for( i = 0; i != sampleCount; i++ ) {
            double dr = 0, di = 0;
            for( j = 0; j != stateCount; j++ ) {
                double a = magcoef[j] * phasecoefcos[j];
                double b = magcoef[j] * phasecoefsin[j];
                dr += modes[j][i] * a - modesLeft[j][i] * b;
                di += modes[j][i] * b + modesLeft[j][i] * a;
            }
            pdatar[i] = dr * normmult;
            pdatai[i] = di * normmult;
        }
        drawFunction( g, viewLeft, pdatar, pdatai, sampleCount, 0 );
        for( i = 0; i != sampleCount; i++ ) {
            pdatar[i] = func[i] - pdatar[i];
            pdatai[i] = funci[i] - pdatai[i];
        }
        drawFunction( g, viewRight, pdatar, pdatai, sampleCount, 0 );
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
            default:
                editFunc( x, y );
                break;
        }
    }

    void editHandle( int y ) {
        int dy = y - viewList[selectedPaneHandle].y;
        QuantumStatesFrame.View upper = viewList[selectedPaneHandle - 1];
        QuantumStatesFrame.View lower = viewList[selectedPaneHandle];
        int minheight = 10;
        if( upper.height + dy < minheight || lower.height - dy < minheight ) {
            return;
        }
        upper.height += dy;
        lower.height -= dy;
        lower.y += dy;
        setGraphLines();
        cv.repaint( pause );
    }

    void editMag( int x, int y ) {
        if( selectedCoef == -1 ) {
            return;
        }
        int ss2 = stateSize / 2;
        int x0 = stateSize * ( selectedCoef % stateColSize ) + ss2;
        int y0 = stateSize * ( selectedCoef / stateColSize ) + ss2 + viewStates.y + stateBuffer;
        x -= x0;
        y -= y0;
        double mag = Math.sqrt( x * x + y * y ) / ss2;
        double ang = Math.atan2( -y, x );
        double ang0 = ( -( elevels[selectedCoef] + phasorBaseEnergy ) * t ) % ( 2 * pi );
        if( mag > 10 ) {
            mag = 0;
        }
        if( mag > 1 ) {
            mag = 1;
        }
        magcoef[selectedCoef] = mag;
        phasecoefadj[selectedCoef] = ( ang - ang0 ) % ( 2 * pi );
        if( phasecoefadj[selectedCoef] > pi ) {
            phasecoefadj[selectedCoef] -= 2 * pi;
        }
        if( alwaysNormItem.getState() ) {
            normalize();
        }
        cv.repaint( pause );
    }

    void editFunc( int x, int y ) {
        if( mouseChooser.getSelectedIndex() == MOUSE_EIGEN ) {
            if( selection == SEL_X ) {
                editXState( x, y );
                return;
            }
            if( selection == SEL_P ) {
                editPState( x, y );
                return;
            }
            if( selection == SEL_POTENTIAL ) {
                findStateByEnergy( y );
                enterSelectedState();
            }
            return;
        }
        if( mouseChooser.getSelectedIndex() == MOUSE_GAUSS ) {
            if( selection == SEL_X ) {
                editXGauss( x, y );
            }
            if( selection == SEL_P ) {
                editPGauss( x, y );
            }
            if( selection == SEL_POTENTIAL ) {
                findStateByEnergy( y );
                enterSelectedState();
            }
            return;
        }
        if( mouseChooser.getSelectedIndex() == MOUSE_TRANSLATE ) {
            if( selection == SEL_X ) {
                translateXGauss( x );
            }
            if( selection == SEL_P ) {
                translatePGauss( x );
            }
            return;
        }
        if( selection == SEL_P ) {
            return;
        }
        if( dragX == x ) {
            editFuncPoint( x, y );
            dragY = y;
        }
        else { // need to draw a line from old x,y to new x,y and // call editFuncPoint for each point on that line. yuck.
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
        if( adjustingWaveFunc ) {
            transform();
            if( alwaysNormItem.getState() ) {
                normalize();
            }
            else {
                maximize();
            }
        }
    }

    void editXGauss( int x, int y ) {
        int i;
        int xi = x * sampleCount / winSize.width;
        double mult = Math.exp( -( y - viewX.mid_y ) * .03 - 4 );
        for( i = 0; i != sampleCount; i++ ) {
            int ii = i - xi;
            func[i] = Math.exp( -ii * ii * mult );
            funci[i] = 0;
        }
        transform();
        if( alwaysNormItem.getState() ) {
            normalize();
        }
        else {
            maximize();
        }
        rescaleGraphs();
    }

    void editPGauss( int x, int y ) {
        int i;
        int xi = x * sampleCount / winSize.width;
        double mult = Math.exp( ( y - viewP.mid_y ) * .03 - 4 );
        double p = getPState( x );
        int s2 = sampleCount / 2; // rather than draw a gaussian and then transform it back to X, // we just draw a function on the X graph that looks like a gaussian // on the P graph.
        for( i = 0; i != sampleCount; i++ ) {
            int ii = i - s2;
            double n = Math.exp( -ii * ii * mult );
            func[i] = Math.cos( p * ii ) * n;
            funci[i] = Math.sin( p * ii ) * n;
        }
        selectedPState = 0;
        transform();
        if( alwaysNormItem.getState() ) {
            normalize();
        }
        else {
            maximize();
        }
        rescaleGraphs();
    }

    void translateXGauss( int x ) {
        int dx = x - dragX;
        if( dx == 0 ) {
            return;
        }
        dragX = x;
        int i;
        dx = dx * sampleCount / winSize.width;
        if( dx > 0 ) {
            for( i = sampleCount - dx - 1; i >= 0; i-- ) {
                func[i + dx] = func[i];
                funci[i + dx] = funci[i];
            }
        }
        else {
            for( i = -dx; i != sampleCount; i++ ) {
                func[i + dx] = func[i];
                funci[i + dx] = funci[i];
            }
        }
        transform();
        cv.repaint( pause );
    }

    void translatePGauss( int x ) {
        double px = getPState( x ) - getPState( dragX );
        dragX = x;
        int i;
        for( i = 0; i != sampleCount; i++ ) {
            double dr = Math.cos( px * i ) * func[i] - Math.sin( px * i ) * funci[i];
            double di = Math.cos( px * i ) * funci[i] + Math.sin( px * i ) * func[i];
            func[i] = dr;
            funci[i] = di;
        }
        transform();
        cv.repaint( pause );
    }

    void editFuncPoint( int x, int y ) {
        QuantumStatesFrame.View v = ( selection == SEL_X ) ? viewX : viewPotential;
        int lox = x * sampleCount / winSize.width;
        int hix = ( ( x + 1 ) * sampleCount - 1 ) / winSize.width;
        double val = ( v.mid_y - y ) / v.ymult;
        double val2 = ( v.lower_y - y ) / v.ymult2;
        if( val > 1 ) {
            val = 1;
        }
        if( val < -1 ) {
            val = -1;
        }
        if( val2 > 1 ) {
            val2 = 1;
        }
        if( val2 < 0 ) {
            val2 = 0;
        }
        val /= v.scale;
        val2 /= v.scale;
        if( lox < 1 ) {
            lox = 1;
        }
        if( hix >= sampleCount - 1 ) {
            hix = sampleCount - 2;
        }
        for( ; lox <= hix; lox++ ) {
            if( selection == SEL_POTENTIAL ) {
                pot[lox] = val;
                setupModified = true;
                adjustingStates = statesChanged = true;
            }
            else {
                if( probCheckItem.getState() || probPhaseCheckItem.getState() ) {
                    double valnew = Math.sqrt( val2 );
                    double n = Math.sqrt( func[lox] * func[lox] + funci[lox] * funci[lox] );
                    if( n == 0 ) {
                        func[lox] = 1;
                        n = 1;
                    } // edit magnitude, preserving phase
                    func[lox] = valnew * func[lox] / n;
                    funci[lox] = valnew * funci[lox] / n;
                }
                else if( magPhaseCheckItem.getState() ) {
                    double n = Math.sqrt( func[lox] * func[lox] + funci[lox] * funci[lox] );
                    if( n == 0 ) {
                        func[lox] = 1;
                        n = 1;
                    }
                    func[lox] = val2 * func[lox] / n;
                    funci[lox] = val2 * funci[lox] / n;
                }
                else { func[lox] = val; }
                adjustingWaveFunc = true;
            }
        }
        cv.repaint( pause );
    }

    void editXState( int x, int y ) {
        int ax = x * sampleCount / winSize.width;
        if( ax < 1 || ax >= sampleCount ) {
            return;
        }
        int i;
        for( i = 0; i != sampleCount; i++ ) {
            func[i] = funci[i] = 0;
        }
        func[ax] = 1;
        transform();
        rescaleGraphs();
        if( !alwaysNormItem.getState() ) {
            maximize();
        }
        cv.repaint( pause );
    }

    double getPState( int x ) {
        double p = ( x * pSampleCount / 2 / winSize.width ) - pSampleCount / 4;
        return p * pi / ( pSampleCount / 2 );
    }

    void editPState( int x, int y ) {
        double p = getPState( x );
        int i;
        int s2 = sampleCount / 2;
        for( i = 0; i != sampleCount; i++ ) {
            func[i] = Math.cos( p * ( i - s2 ) );
            funci[i] = Math.sin( p * ( i - s2 ) );
        }
        transform();
        rescaleGraphs();
        if( !alwaysNormItem.getState() ) {
            maximize();
        }
        selectedPState = 0;
        cv.repaint( pause );
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
        if( e.getSource() == measureEItem ) {
            measureE();
        }
        if( e.getSource() == measureXItem ) {
            measureX();
        }
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
        if( e.getSource() == rescaleButton ) {
            rescaleGraphs();
        }
    }

    public void scrollbarValueChanged( DecentScrollbar ds ) {
        System.out.print( ds.getValue() + "\n" );
        if( ds == massBar ) {
            statesChanged = adjustingStates = true;
            cv.repaint( pause );
        }
        if( ds == aux1Bar || ds == aux2Bar || ds == aux3Bar ) {
            adjustingStates = true;
            setup.drawPotential();
            statesChanged = true;
            cv.repaint( pause );
        }
        if( ds == resBar ) {
            adjustingStates = true;
        }
    }

    public void scrollbarFinished( DecentScrollbar ds ) {
        if( ds == resBar ) {
            adjustingStates = false;
            setResolution();
            reinit();
            cv.repaint( pause );
        }
        if( ds == massBar || ds == aux1Bar || ds == aux2Bar || ds == aux3Bar ) {
            adjustingStates = false;
            statesChanged = true;
            cv.repaint( pause );
        }
    }

    public boolean handleEvent( Event ev ) {
        if( ev.id == Event.WINDOW_DESTROY ) {
            applet.destroyFrame();
            return true;
        }
        return super.handleEvent( ev );
    }

    void setResolution() {
        sampleCount = resBar.getValue();
        sampleCount++;
        func = new double[sampleCount];
        funci = new double[sampleCount];
        pot = new double[sampleCount];
        statesChanged = true;
        int ps = 8 * sampleCount;
        pSampleCount = 1;
        while( pSampleCount < ps ) {
            pSampleCount *= 2;
        }
        pdata = new double[pSampleCount * 2];
        pdatar = new double[pSampleCount];
        pdatai = new double[pSampleCount];
        parityData = new double[100];
        currentData = new double[sampleCount];
        fft = new QuantumStatesFrame.FFT( pSampleCount );
    }

    void genStates( boolean getStates ) {
        System.out.println( "genstates" );
        statesChanged = false;
        int n = sampleCount;
        double d[] = new double[n];
        double e[] = new double[n];
        double z[] = new double[n * n];
        int i, j;
        mass = massBar.getValue() * .02;
        double m1 = 1 / ( mass * 1.006 );
        mass *= .511e6 / .32;
        double maxpot = -20; // correct for finite resolution
        double rescorr = sampleCount / 128.;
        m1 *= rescorr * rescorr; // express schroedinger's equation as a matrix equation // using finite differencing
        for( i = 0; i != n; i++ ) {
            if( i < n - 1 ) {
                e[i] = -m1;
            }
            d[i] = 2 * m1 + pot[i];
            if( pot[i] > maxpot ) {
                maxpot = pot[i];
            }
            int in = i * n;
            for( j = 0; j != n; j++ ) {
                z[in + j] = 0;
            }
            z[in + i] = 1;
        }
        double work[] = new double[1 + 4 * n + n * n];
        int iwork[] = new int[3 + 5 * n];
        intW info = new intW( 0 ); // use LAPACK to find the eigenvalues and eigenvectors
        Dstedc.dstedc( getStates ? "I" : "N", n, d, 0, e, 0, z, 0, n, work, 0, work.length, iwork, 0, iwork.length, info );
        elevels = new double[n];
        elevelCount = n; // now get the eigenvalues and sort them
        for( i = 0; i != n; i++ )
        { /*if (i < n-1 && d[i] == d[i+1]) System.out.print("degeneracy! " + d[i] + "\n");*/ elevels[i] = d[i]; }
        int si, sj; // sort the elevels
        for( si = 1; si < n; si++ ) {
            double v = elevels[si];
            sj = si;
            while( elevels[sj - 1] > v ) {
                elevels[sj] = elevels[sj - 1];
                sj--;
                if( sj <= 0 ) {
                    break;
                }
            }
            elevels[sj] = v;
        }
        while( maxpot > 0 && elevels[elevelCount - 1] > maxpot ) {
            elevelCount--;
        }
        stateCount = elevelCount;
        int maxs = sampleCount * 3 / 8;
        if( stateCount > maxs && getStates ) {
            stateCount = maxs;
        }
        if( !getStates ) {
            return; // get the eigenstates
        }
        modes = new double[stateCount][sampleCount];
        for( i = 0; i != stateCount; i++ ) {
            for( j = 0; j != n; j++ ) {
                if( elevels[i] == d[j] ) {
                    break;
                }
            }
            if( j == n ) {
                System.out.print( "can't find elevels! " + i + " " + elevels[i] + "\n" );
                continue;
            }
            d[j] = -1;
            int k;
            dispmax[i] = 0;
            for( k = 0; k != n; k++ ) {
                modes[i][k] = z[j * n + k];
                if( modes[i][k] > dispmax[i] ) {
                    dispmax[i] = modes[i][k];
                }
                else if( -modes[i][k] > dispmax[i] ) {
                    dispmax[i] = -modes[i][k];
                }
            }
        }
        modesLeft = null; // The energy levels calculated above for a harmonic oscillator are // not evenly spaced because of numerical error, so we force // them to be evenly spaced in order to get coherent states to work
        if( !setupModified ) {
            setup.fudgeLevels();
        }
        System.out.println( "done" );
    }

    public void mouseDragged( MouseEvent e ) {
        dragging = true;
        edit( e );
    }

    public void mouseMoved( MouseEvent e ) {
        int x = e.getX();
        int y = e.getY();
        dragX = x;
        dragY = y;
        int oldCoef = selectedCoef;
        int oldSelection = selection;
        selectedCoef = -1;
        selectedPState = 0;
        selectedPaneHandle = -1;
        selection = 0;
        int i;
        for( i = 1; i != viewCount; i++ ) {
            int dy = y - viewList[i].y;
            if( dy >= -3 && dy <= 3 ) {
                selectedPaneHandle = i;
                selection = SEL_HANDLE;
            }
        }
        Cursor cs = null;
        if( selection == SEL_HANDLE ) {
            cs = Cursor.getPredefinedCursor( N_RESIZE_CURSOR );
        }
        else if( viewX != null && viewX.contains( x, y ) ) {
            selection = SEL_X;
        }
        else if( viewP != null && viewP.contains( x, y ) ) {
            selection = SEL_P;
            selectedPState = getPState( x );
            cv.repaint( pause );
        }
        else if( viewPotential != null && viewPotential.contains( x, y ) ) {
            selection = SEL_POTENTIAL;
            if( mouseChooser.getSelectedIndex() != MOUSE_EDIT ) {
                findStateByEnergy( y );
            }
        }
        else if( viewStates != null && viewStates.contains( x, y ) ) {
            int xi = x / stateSize;
            int yi = ( y - ( viewStates.y + stateBuffer ) ) / stateSize;
            selectedCoef = xi + yi * stateColSize;
            if( selectedCoef >= stateCount ) {
                selectedCoef = -1;
            }
            if( selectedCoef != -1 ) {
                selection = SEL_STATES;
            }
        }
        cv.setCursor( cs );
        if( selection != oldSelection || selectedCoef != oldCoef ) {
            cv.repaint( pause );
        }
    }

    void findStateByEnergy( int y ) {
        if( adjustingStates || statesChanged ) {
            return;
        }
        int i;
        double dy = ( viewPotential.mid_y - y ) / viewPotential.ymult;
        double dist = 100;
        for( i = 0; i != stateCount; i++ ) {
            double d = Math.abs( elevels[i] - dy );
            if( d < dist ) {
                dist = d;
                selectedCoef = i;
            }
        }
    }

    public void mouseClicked( MouseEvent e ) {
        if( e.getClickCount() == 2 && selectedCoef != -1 ) {
            enterSelectedState();
        }
    }

    void enterSelectedState() {
        int i;
        for( i = 0; i != stateCount; i++ ) {
            if( selectedCoef != i ) {
                magcoef[i] = 0;
            }
        }
        magcoef[selectedCoef] = 1;
        cv.repaint( pause );
        rescaleGraphs();
    }

    public void mouseEntered( MouseEvent e ) {
    }

    public void mouseExited( MouseEvent e ) {
        if( !dragging ) {
            if( selectedCoef != -1 ) {
                selectedCoef = -1;
                cv.repaint( pause );
            }
            if( selectedPState != 0 ) {
                selectedPState = 0;
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
        dragging = true;
        edit( e );
    }

    public void mouseReleased( MouseEvent e ) {
        if( ( e.getModifiers() & MouseEvent.BUTTON1_MASK ) == 0 ) {
            return;
        }
        if( mouseChooser.getSelectedIndex() == MOUSE_EDIT && selection == SEL_POTENTIAL ) {
            adjustingStates = false;
            statesChanged = true;
        }
        if( selection == SEL_STATES && alwaysMaxItem.getState() ) {
            maximize();
        }
        dragging = adjustingWaveFunc = false;
        cv.repaint( pause );
    }

    public void itemStateChanged( ItemEvent e ) {
        if( e.getItemSelectable() == stoppedCheck ) {
            cv.repaint( pause );
            return;
        }
        if( e.getItemSelectable() == setupChooser ) {
            doSetup();
        }
        if( e.getItemSelectable() instanceof CheckboxMenuItem ) {
            handleResize();
            cv.repaint( pause );
        }
        if( e.getItemSelectable() == alwaysNormItem && alwaysNormItem.getState() ) {
            normalize();
            alwaysMaxItem.setState( false );
            cv.repaint( pause );
        }
        if( e.getItemSelectable() == alwaysMaxItem && alwaysMaxItem.getState() ) {
            maximize();
            alwaysNormItem.setState( false );
            cv.repaint( pause );
        }
        int i;
        for( i = 0; i != waveFunctionMenu.countItems(); i++ ) {
            if( e.getItemSelectable() == waveFunctionMenu.getItem( i ) ) {
                int j;
                ( (CheckboxMenuItem)waveFunctionMenu.getItem( i ) ).setState( true );
                for( j = 0; j != waveFunctionMenu.countItems(); j++ ) {
                    if( i != j ) {
                        ( (CheckboxMenuItem)waveFunctionMenu.getItem( j ) ).setState( false );
                    }
                }
                rescaleGraphs();
            }
        }
    }

    void doSetup() {
        doBlank();
        int i;
        for( i = 0; i != sampleCount; i++ ) {
            func[i] = funci[i] = pot[i] = 0;
        }
        setup = (QuantumStatesFrame.Setup)setupList.elementAt( setupChooser.getSelectedIndex() );
        aux1Bar.setValue( 100 );
        aux2Bar.setValue( 100 );
        aux3Bar.setValue( 100 );
        selectGround = true;
        setup.select();
        setup.drawPotential();
        setupModified = false;
        statesChanged = true;
        if( setup.getAuxBarCount() >= 2 ) {
            aux2Label.show();
            aux2Bar.show();
        }
        else {
            aux2Label.hide();
            aux2Bar.hide();
        }
        if( setup.getAuxBarCount() == 3 ) {
            aux3Label.show();
            aux3Bar.show();
        }
        else {
            aux3Label.hide();
            aux3Bar.hide();
        }
        if( setup.allowLeftRight() ) {
            leftRightCheckItem.enable();
        }
        else {
            leftRightCheckItem.disable();
            leftRightCheckItem.setState( false );
        }
        handleResize();
        validate();
        selectedCoef = -1;
    }

    void measureE() {
        normalize();
        double n = random.nextDouble();
        int i;
        for( i = 0; i != stateCount; i++ ) {
            double m = magcoef[i] * magcoef[i];
            n -= m;
            if( n < 0 ) {
                break;
            }
        }
        if( i == stateCount ) {
            return;
        }
        int pick = i;
        for( i = 0; i != stateCount; i++ ) {
            magcoef[i] = 0;
        }
        magcoef[pick] = 1;
        rescaleGraphs();
    }

    void measureX() {
        int i;
        double n = random.nextDouble();
        for( i = 0; i != sampleCount; i++ ) {
            double m = func[i] * func[i] + funci[i] * funci[i];
            n -= m;
            if( n < 0 ) {
                break;
            }
        }
        if( i == sampleCount ) {
            return;
        }
        int pick = i;
        for( i = 0; i != sampleCount; i++ ) {
            func[i] = funci[i] = 0;
        }
        func[pick] = 1;
        transform();
        rescaleGraphs();
        normalize();
    }

    class FFT {
        double wtabf[];
        double wtabi[];
        int size;

        FFT( int sz ) {
            size = sz;
            if( ( size & ( size - 1 ) ) != 0 ) {
                System.out.println( "size must be power of two!" );
            }
            calcWTable();
        }

        void calcWTable() { // calculate table of powers of w
            wtabf = new double[size];
            wtabi = new double[size];
            int i;
            for( i = 0; i != size; i += 2 ) {
                double pi = 3.1415926535;
                double th = pi * i / size;
                wtabf[i] = Math.cos( th );
                wtabf[i + 1] = Math.sin( th );
                wtabi[i] = wtabf[i];
                wtabi[i + 1] = -wtabf[i + 1];
            }
        }

        void transform( double data[], boolean inv ) {
            int i;
            int j = 0;
            int size2 = size * 2;
            if( ( size & ( size - 1 ) ) != 0 ) {
                System.out.println( "size must be power of two!" ); // bit-reversal
            }
            double q;
            int bit;
            for( i = 0; i != size2; i += 2 ) {
                if( i > j ) {
                    q = data[i];
                    data[i] = data[j];
                    data[j] = q;
                    q = data[i + 1];
                    data[i + 1] = data[j + 1];
                    data[j + 1] = q;
                } // increment j by one, from the left side (bit-reversed)
                bit = size;
                while( ( bit & j ) != 0 ) {
                    j &= ~bit;
                    bit >>= 1;
                }
                j |= bit;
            } // amount to skip through w table
            int tabskip = size << 1;
            double wtab[] = ( inv ) ? wtabi : wtabf;
            int skip1, skip2, ix, j2;
            double wr, wi, d1r, d1i, d2r, d2i, d2wr, d2wi; // unroll the first iteration of the main loop
            for( i = 0; i != size2; i += 4 ) {
                d1r = data[i];
                d1i = data[i + 1];
                d2r = data[i + 2];
                d2i = data[i + 3];
                data[i] = d1r + d2r;
                data[i + 1] = d1i + d2i;
                data[i + 2] = d1r - d2r;
                data[i + 3] = d1i - d2i;
            }
            tabskip >>= 1; // unroll the second iteration of the main loop
            int imult = ( inv ) ? -1 : 1;
            for( i = 0; i != size2; i += 8 ) {
                d1r = data[i];
                d1i = data[i + 1];
                d2r = data[i + 4];
                d2i = data[i + 5];
                data[i] = d1r + d2r;
                data[i + 1] = d1i + d2i;
                data[i + 4] = d1r - d2r;
                data[i + 5] = d1i - d2i;
                d1r = data[i + 2];
                d1i = data[i + 3];
                d2r = data[i + 6] * imult;
                d2i = data[i + 7] * imult;
                data[i + 2] = d1r - d2i;
                data[i + 3] = d1i + d2r;
                data[i + 6] = d1r + d2i;
                data[i + 7] = d1i - d2r;
            }
            tabskip >>= 1;
            for( skip1 = 16; skip1 <= size2; skip1 <<= 1 )
            { // skip2 = length of subarrays we are combining // skip1 = length of subarray after combination
                skip2 = skip1 >> 1;
                tabskip >>= 1;
                for( i = 0; i != 1000; i++ ) {
                    ; // for each subarray
                }
                for( i = 0; i < size2; i += skip1 ) {
                    ix = 0; // for each pair of complex numbers (one in each subarray)
                    for( j = i; j != i + skip2; j += 2, ix += tabskip ) {
                        wr = wtab[ix];
                        wi = wtab[ix + 1];
                        d1r = data[j];
                        d1i = data[j + 1];
                        j2 = j + skip2;
                        d2r = data[j2];
                        d2i = data[j2 + 1];
                        d2wr = d2r * wr - d2i * wi;
                        d2wi = d2r * wi + d2i * wr;
                        data[j] = d1r + d2wr;
                        data[j + 1] = d1i + d2wi;
                        data[j2] = d1r - d2wr;
                        data[j2 + 1] = d1i - d2wi;
                    }
                }
            }
        }
    }

    abstract class Setup {
        abstract String getName();

        abstract void select();

        abstract QuantumStatesFrame.Setup createNext();

        void fudgeLevels() {
        }

        boolean allowLeftRight() {
            return false;
        }

        abstract void drawPotential();

        int getAuxBarCount() {
            return 2;
        }

        void getInfo( String s[], int offset ) {
        }

        double getBaseEnergy() {
            return -1;
        }
    }

    ;

    class InfiniteWellSetup extends QuantumStatesFrame.Setup {
        String getName() {
            return "Infinite Well";
        }

        void select() {
            aux1Label.setText( "Well Width" );
        }

        double getBaseEnergy() {
            return 1;
        }

        boolean allowLeftRight() {
            return !setupModified;
        }

        void drawPotential() {
            int i;
            int width = getOffset();
            for( i = 0; i != sampleCount; i++ ) {
                pot[i] = infiniteEnergy;
            }
            for( i = width; i <= sampleCount - 1 - width; i++ ) {
                pot[i] = -1;
            }
        }

        int getOffset() {
            return ( 100 - aux1Bar.getValue() ) * ( sampleCount / 2 ) / 110 + 1;
        }

        int getAuxBarCount() {
            return 1;
        }

        void fudgeLevels() { // change levels to exact values so that states are periodic
            int i;
            for( i = 1; i != stateCount; i++ ) {
                elevels[i] = ( elevels[0] + 1 ) * ( i + 1 ) * ( i + 1 ) - 1;
            }
        }

        void getInfo( String s[], int o ) {
            s[o] = "Well width = " + getLengthText( sampleCount - getOffset() * 2 );
        }

        QuantumStatesFrame.Setup createNext() {
            return new QuantumStatesFrame.FiniteWellSetup();
        }
    }

    class FiniteWellSetup extends QuantumStatesFrame.Setup {
        String getName() {
            return "Finite Well";
        }

        void select() {
            aux1Label.setText( "Well Width" );
            aux1Bar.setValue( 100 );
            aux2Label.setText( "Well Depth" );
        }

        int getOffset() {
            return ( 100 - aux1Bar.getValue() * 6 / 10 ) * ( sampleCount / 2 ) / 110;
        }

        double getFloor() {
            return 1 - ( aux2Bar.getValue() ) / 50.;
        }

        void drawPotential() {
            int i;
            int width = getOffset();
            double floor = getFloor();
            for( i = 0; i != sampleCount; i++ ) {
                pot[i] = 1;
            }
            for( i = width; i <= sampleCount - 1 - width; i++ ) {
                pot[i] = floor;
            }
        }

        void getInfo( String s[], int o ) {
            s[o] = "Well width = " + getLengthText( sampleCount - getOffset() * 2 );
            s[o] += ", Well depth = " + getEnergyText( 1 - getFloor(), false );
        }

        QuantumStatesFrame.Setup createNext() {
            return new QuantumStatesFrame.HarmonicOscillatorSetup();
        }
    }

    class HarmonicOscillatorSetup extends QuantumStatesFrame.Setup {
        String getName() {
            return "Harmonic Oscillator";
        }

        boolean allowLeftRight() {
            return !setupModified;
        }

        void select() {
            aux1Label.setText( "Spring Constant" );
            aux1Bar.setValue( 0 );
            aux2Label.setText( "Offset" );
            aux2Bar.setValue( 50 );
        }

        double a;

        void drawPotential() {
            int i;
            double width = ( sampleCount / 2 ) / ( .062333 * aux1Bar.getValue() + 1.1 );
            int offset = ( 50 - aux2Bar.getValue() ) * ( sampleCount / 2 ) / 100;
            a = 2 / ( width * width );
            for( i = 0; i != sampleCount; i++ ) {
                int ii = offset + i - sampleCount / 2;
                pot[i] = a * ii * ii - 1;
            }
            pot[0] = pot[sampleCount - 1] = infiniteEnergy;
        }

        double getBaseEnergy() {
            return 1;
        }

        void getInfo( String s[], int o ) { // convert pixels to meters
            double p1 = 1 / pointsToLength( 1 );
            double k = a * 2 * convertEnergy( 1, false ) * p1 * p1;
            s[o] = "k = " + getUnitText( k * 1e-18, "eV" ) + "/nm^2";
            double f = Math.sqrt( k / ( mass / 8.988e16 ) ) / ( 2 * pi );
            s[o] += ", period = " + getUnitText( 1 / f, "s" );
        }

        void fudgeLevels() {
            int i;
            if( stateCount < 10 ) {
                return;
            }
            double avg = 0;
            for( i = 0; i != 10; i++ ) {
                avg += elevels[i + 1] - elevels[i];
            }
            avg /= 10;
            for( i = 1; i != stateCount; i++ ) { // if the levels are way off then leave them alone. this
                // can happen if the offset is too far to the left, for
                //  example.
                if( ( elevels[i] - elevels[i - 1] ) / avg > 2 ) {
                    break;
                }
                elevels[i] = elevels[i - 1] + avg;
            }
        }

        QuantumStatesFrame.Setup createNext() {
            return new QuantumStatesFrame.FiniteWellPairSetup();
        }
    }

    class FiniteWellPairSetup extends QuantumStatesFrame.Setup {
        String getName() {
            return "Well Pair";
        }

        void select() {
            aux1Label.setText( "Well Width" );
            aux2Bar.setValue( 10 );
            aux2Label.setText( "Well Separation" );
            aux3Label.setText( "Well Depth" );
        }

        int getAuxBarCount() {
            return 3;
        }

        double getFloor() {
            return 1 - ( aux3Bar.getValue() ) / 50.;
        }

        int getWidth() {
            return ( aux1Bar.getValue() * 3 / 4 ) * ( sampleCount / 2 ) / 110 + 1;
        }

        int getSep() {
            return ( sampleCount / 2 - getWidth() ) * aux2Bar.getValue() / 150 + 1;
        }

        void drawPotential() {
            int i;
            int width = getWidth();
            int sep = getSep();
            double floor = getFloor();
            for( i = 0; i != sampleCount; i++ ) {
                pot[i] = 1;
            }
            for( i = 0; i != width; i++ ) {
                pot[sampleCount / 2 - i - sep] = pot[sampleCount / 2 + i + sep] = floor;
            }
        }

        void getInfo( String s[], int o ) {
            s[o] = "Well width = " + getLengthText( getWidth() );
            s[o] += ", Well depth = " + getEnergyText( 1 - getFloor(), false );
            s[o + 1] = "Well separation = " + getLengthText( getSep() * 2 );
        }

        QuantumStatesFrame.Setup createNext() {
            return new QuantumStatesFrame.FiniteWellPairCoupledSetup();
        }
    }

    class FiniteWellPairCoupledSetup extends QuantumStatesFrame.Setup {
        String getName() {
            return "Coupled Well Pair";
        }

        void select() {
            aux1Label.setText( "Well Separation" );
            aux1Bar.setValue( 1 );
            aux2Label.setText( "Wall Potential" );
            aux2Bar.setValue( 50 );
        }

        int getWidth() {
            return sampleCount / 4;
        }

        int getSep() {
            return aux1Bar.getValue() * ( getWidth() - 1 ) / 150 + 1;
        }

        double getWallEnergy() {
            return -1 + ( aux2Bar.getValue() - 1 ) / 50.;
        }

        void drawPotential() {
            int i;
            int width = getWidth();
            int sep = getSep();
            double floor = -1;
            double infloor = -1 + ( aux2Bar.getValue() - 1 ) / 50.;
            for( i = 0; i != sampleCount; i++ ) {
                pot[i] = 1;
            }
            for( i = 0; i != width; i++ ) {
                pot[sampleCount / 2 - i - sep] = pot[sampleCount / 2 + i + sep] = floor;
            }
            for( i = 0; i < sep; i++ ) {
                pot[sampleCount / 2 - i] = pot[sampleCount / 2 + i] = infloor;
            }
        }

        void getInfo( String s[], int o ) {
            s[o] = "Well width = " + getLengthText( getWidth() );
            s[o] += ", Well depth = " + getEnergyText( 2, false );
            s[o + 1] = "Well separation = " + getLengthText( getSep() * 2 );
            s[o + 1] += ", Wall potential = " + getEnergyText( getWallEnergy(), true );
        }

        QuantumStatesFrame.Setup createNext() {
            return new QuantumStatesFrame.AsymmetricWellSetup();
        }
    }

    class AsymmetricWellSetup extends QuantumStatesFrame.Setup {
        String getName() {
            return "Asymmetric Well";
        }

        void select() {
            aux1Label.setText( "Width Difference" );
            aux1Bar.setValue( 60 );
            aux2Label.setText( "Depth Difference" );
            aux2Bar.setValue( 12 );
        }

        int w1;
        double floor2;

        void getInfo( String s[], int o ) {
            s[o] = "Left well width = " + getLengthText( w1 );
            s[o] += ", energy = " + getEnergyText( -1, true );
            s[o + 1] = "Right well width = " + getLengthText( sampleCount - 2 - w1 );
            s[o + 1] += ", energy = " + getEnergyText( floor2, true );
        }

        void drawPotential() {
            pot[0] = pot[sampleCount - 1] = infiniteEnergy;
            w1 = aux1Bar.getValue() * ( sampleCount - 2 ) / 100;
            floor2 = aux2Bar.getValue() * 2 / 100. - 1;
            int i;
            for( i = 1; i != w1; i++ ) {
                pot[i] = -1;
            }
            for( i = w1; i < sampleCount - 1; i++ ) {
                pot[i] = floor2;
            }
        }

        double getBaseEnergy() {
            return 1;
        }

        QuantumStatesFrame.Setup createNext() {
            return new QuantumStatesFrame.InfiniteWellFieldSetup();
        }
    }

    class InfiniteWellFieldSetup extends QuantumStatesFrame.Setup {
        String getName() {
            return "Infinite Well + Field";
        }

        void select() {
            aux1Label.setText( "Well Width" );
            aux2Label.setText( "Field Strength" );
        }

        int width;
        double field;

        void getInfo( String s[], int o ) {
            int realwidth = sampleCount - width * 2;
            s[o] = "Well width = " + getLengthText( realwidth );
            double w = pointsToLength( realwidth );
            double ed = pot[width] - pot[sampleCount - 1 - width];
            double vd = convertEnergy( ed, false );
            s[o] += ", voltage difference = " + getUnitText( vd, "V" );
            s[o + 1] = "(Particle charge = electron charge)";
        }

        void drawPotential() {
            int i;
            width = ( 100 - aux1Bar.getValue() ) * ( sampleCount / 2 ) / 110 + 1;
            for( i = 0; i != sampleCount; i++ ) {
                pot[i] = infiniteEnergy;
            }
            field = -( aux2Bar.getValue() - 50 ) / ( 50. * sampleCount / 2 );
            for( i = width; i <= sampleCount - 1 - width; i++ ) {
                pot[i] = ( i - sampleCount / 2 ) * field;
            }
        }

        double getBaseEnergy() {
            return 0;
        }

        QuantumStatesFrame.Setup createNext() {
            return new QuantumStatesFrame.WellPairCoupledFieldSetup();
        }
    }

    class WellPairCoupledFieldSetup extends QuantumStatesFrame.Setup {
        String getName() {
            return "Coupled Wells + Field";
        }

        void select() {
            aux1Label.setText( "Well Separation" );
            aux1Bar.setValue( 1 );
            aux2Label.setText( "Field Strength" );
        }

        double getBaseEnergy() {
            return .25;
        }

        void drawPotential() {
            int i;
            int width = sampleCount / 8;
            int sep = aux1Bar.getValue() * ( width - 1 ) / 101 + 1;
            double floor = -1 + getBaseEnergy();
            double infloor = 1;
            for( i = 0; i != sampleCount; i++ ) {
                pot[i] = infiniteEnergy;
            }
            double field = -( aux2Bar.getValue() - 50 ) / ( 100. * sampleCount / 2 );
            for( i = 0; i != width; i++ ) {
                double q = ( i + sep ) * field;
                pot[sampleCount / 2 - i - sep] = floor + q;
                pot[sampleCount / 2 + i + sep] = floor - q;
            }
            for( i = 0; i < sep; i++ ) {
                pot[sampleCount / 2 - i] = pot[sampleCount / 2 + i] = infloor;
            }
        }

        QuantumStatesFrame.Setup createNext() {
            return new QuantumStatesFrame.CoulombSetup();
        }
    }

    class CoulombSetup extends QuantumStatesFrame.Setup {
        String getName() {
            return "Coulomb";
        }

        void select() {
            aux1Label.setText( "Charge" );
            aux1Bar.setValue( 8 );
        }

        void drawPotential() {
            int i;
            double width = ( aux1Bar.getValue() ) * ( sampleCount / 2 ) / 110.;
            for( i = 0; i != sampleCount; i++ ) {
                int ii = i - sampleCount / 2;
                if( ii < 0 ) {
                    ii = -ii;
                }
                double v = 1 - width / ii;
                if( v < -1 ) {
                    v = -1;
                }
                pot[i] = v;
            }
            pot[0] = pot[sampleCount - 1] = infiniteEnergy;
        }

        int getAuxBarCount() {
            return 1;
        }

        QuantumStatesFrame.Setup createNext() {
            return new QuantumStatesFrame.QuarticOscillatorSetup();
        }
    }

    class QuarticOscillatorSetup extends QuantumStatesFrame.Setup {
        String getName() {
            return "Quartic Oscillator";
        }

        void select() {
            aux1Label.setText( "Spring Constant" );
            aux1Bar.setValue( 0 );
            aux2Label.setText( "Offset" );
            aux2Bar.setValue( 50 );
        }

        double a;

        void drawPotential() {
            int i;
            double width = ( sampleCount / 2 ) / ( .062333 * aux1Bar.getValue() + 1.1 );
            int offset = ( 50 - aux2Bar.getValue() ) * ( sampleCount / 2 ) / 100;
            a = 2 / ( width * width * width * width );
            for( i = 0; i != sampleCount; i++ ) {
                int ii = offset + i - sampleCount / 2;
                pot[i] = a * ii * ii * ii * ii - 1;
            }
            pot[0] = pot[sampleCount - 1] = infiniteEnergy;
        }

        double getBaseEnergy() {
            return 1;
        }

        QuantumStatesFrame.Setup createNext() {
            return new QuantumStatesFrame.FiniteWellArraySetup();
        }
    }

    class FiniteWellArraySetup extends QuantumStatesFrame.Setup {
        int width;
        int wellCount;

        String getName() {
            return "Well Array (Square)";
        }

        void select() {
            aux1Label.setText( "Well Count" );
            aux2Label.setText( "Well Depth" );
            aux3Label.setText( "Well Width" );
        }

        int getAuxBarCount() {
            return 3;
        }

        double getFloor() {
            return 1 - ( aux2Bar.getValue() ) / 50.;
        }

        void getInfo( String s[], int o ) {
            s[o] = "Well width = " + getLengthText( width );
            s[o] += ", Well count = " + wellCount;
            s[o + 1] = "Well depth = " + getEnergyText( 1 - getFloor(), false );
        }

        void drawPotential() {
            int i;
            int maxWells = 10;
            if( sampleCount > 260 ) {
                maxWells = sampleCount / 26;
            }
            int period = ( sampleCount * 7 / ( maxWells * 8 ) ) * ( aux3Bar.getValue() + 60 ) / 160;
            int sep = ( sampleCount / ( maxWells * 8 ) );
            if( sep == 0 ) {
                sep++;
                period--;
            }
            wellCount = aux1Bar.getValue() * ( maxWells ) / 101 + 1;
            double floor = getFloor();
            int offset = ( sampleCount - wellCount * period ) / 2;
            width = period - sep;
            for( i = 0; i != sampleCount; i++ ) {
                if( i < offset ) {
                    pot[i] = 1;
                }
                else {
                    int ii = i - offset;
                    int j = ( ii % period );
                    pot[i] = ( j < sep || ii / period >= wellCount ) ? 1 : floor;
                }
            }
        }

        QuantumStatesFrame.Setup createNext() {
            return new QuantumStatesFrame.HarmonicWellArraySetup();
        }
    }

    class HarmonicWellArraySetup extends QuantumStatesFrame.FiniteWellArraySetup {
        String getName() {
            return "Well Array (Harmonic)";
        }

        void drawPotential() {
            int i;
            int maxWells = 5;
            if( sampleCount > 260 ) {
                maxWells = sampleCount / 52;
            }
            int period = ( sampleCount * 7 / ( maxWells * 8 ) ) * ( aux3Bar.getValue() + 60 ) / 160;
            int sep = ( sampleCount / ( maxWells * 8 ) );
            if( sep == 0 ) {
                sep++;
                period--;
            }
            wellCount = aux1Bar.getValue() * ( maxWells ) / 101 + 1;
            double floor = getFloor();
            int offset = ( sampleCount - wellCount * period ) / 2;
            width = period - sep;
            double a = ( 1 - floor ) / ( ( width / 2 ) * ( width / 2. ) );
            for( i = 0; i != sampleCount; i++ ) {
                if( i < offset ) {
                    pot[i] = 1;
                }
                else {
                    int ii = i - offset;
                    int j = ( ii % period );
                    if( j < sep || ii / period >= wellCount ) {
                        pot[i] = 1;
                    }
                    else {
                        double xx = ( j - sep - width / 2. );
                        pot[i] = floor + xx * xx * a;
                        if( pot[i] > 1 ) {
                            pot[i] = 1;
                        }
                    }
                }
            }
        }

        QuantumStatesFrame.Setup createNext() {
            return new QuantumStatesFrame.CoulombWellArraySetup();
        }
    }

    class CoulombWellArraySetup extends QuantumStatesFrame.FiniteWellArraySetup {
        String getName() {
            return "Well Array (Coulomb)";
        }

        void select() {
            super.select();
            aux2Bar.setValue( 48 );
        }

        void getInfo( String s[], int o ) {
            s[o] = "Well width = " + getLengthText( width );
            s[o] += ", Well count = " + wellCount;
            s[o + 1] = "Well depth = " + getEnergyText( 2, false );
        }

        void drawPotential() {
            int i;
            int maxWells = 5;
            if( sampleCount > 260 ) {
                maxWells = sampleCount / 52;
            }
            int period = ( sampleCount * 7 / ( maxWells * 8 ) ) * ( aux3Bar.getValue() + 60 ) / 160;
            int sep = ( sampleCount / ( maxWells * 8 ) );
            if( sep == 0 ) {
                sep++;
                period--;
            }
            wellCount = aux1Bar.getValue() * ( maxWells ) / 101 + 1;
            double s = ( aux2Bar.getValue() ) / 400.;
            int offset = ( sampleCount - wellCount * period ) / 2;
            width = period - sep;
            s *= width / 2;
            double a = -2 * width * s / ( 2 * s - width );
            double b = 1 + 2 * a / width;
            for( i = 0; i != sampleCount; i++ ) {
                if( i < offset ) {
                    pot[i] = 1;
                }
                else {
                    int ii = i - offset;
                    int j = ( ii % period );
                    if( j < sep || ii / period >= wellCount ) {
                        pot[i] = 1;
                    }
                    else {
                        double xx = ( j - sep - width / 2. );
                        pot[i] = b - a / Math.abs( xx );
                        if( pot[i] < -1 ) {
                            pot[i] = -1;
                        }
                    }
                }
            }
        }

        QuantumStatesFrame.Setup createNext() {
            return new QuantumStatesFrame.FiniteWellArrayFieldSetup();
        }
    }

    class FiniteWellArrayFieldSetup extends QuantumStatesFrame.Setup {
        int width;
        int wellCount;

        String getName() {
            return "Well Array + Field";
        }

        void select() {
            aux1Label.setText( "Well Count" );
            aux2Label.setText( "Well Width" );
            aux3Label.setText( "Field Strength" );
            aux3Bar.setValue( 90 );
            if( massBar.getValue() == 16 && resBar.getValue() <= 260 ) { // set up interesting case (bloch oscillation)
                selectGround = false;
                magcoef[3] = .067;
                magcoef[4] = .2629;
                magcoef[5] = .6771;
                magcoef[6] = 1;
                magcoef[7] = .586156;
                magcoef[8] = .1058;
                magcoef[9] = .078;
                int i;
                for( i = 0; i != stateCount; i++ ) {
                    phasecoefadj[i] = ( i == 6 ) ? 0 : pi;
                }
            }
        }

        int getAuxBarCount() {
            return 3;
        }

        void getInfo( String s[], int o ) {
        }

        void drawPotential() {
            int i;
            int maxWells = 10;
            if( sampleCount > 260 ) {
                maxWells = sampleCount / 26;
            }
            int period = ( sampleCount * 7 / ( maxWells * 8 ) ) * ( aux2Bar.getValue() + 60 ) / 160;
            int sep = ( sampleCount / ( maxWells * 8 ) );
            if( sep == 0 ) {
                sep++;
                period--;
            }
            double field = -( aux3Bar.getValue() - 50 ) / ( 200. * sampleCount / 2 );
            wellCount = aux1Bar.getValue() * ( maxWells ) / 101 + 1;
            double floor = -.75;
            int offset = ( sampleCount - wellCount * period ) / 2;
            width = period - sep;
            for( i = 0; i != sampleCount; i++ ) {
                if( i < offset ) {
                    pot[i] = .75;
                }
                else {
                    int ii = i - offset;
                    int j = ( ii % period );
                    pot[i] = ( j < sep || ii / period >= wellCount ) ? .75 : floor;
                }
                pot[i] += ( i - sampleCount / 2 ) * field;
            }
            pot[0] = pot[sampleCount - 1] = infiniteEnergy;
        }

        QuantumStatesFrame.Setup createNext() {
            return new QuantumStatesFrame.FiniteWellArrayImpureSetup();
        }
    }

    class FiniteWellArrayImpureSetup extends QuantumStatesFrame.FiniteWellArraySetup {
        String getName() {
            return "Well Array w/ Impurity";
        }

        void select() {
            aux1Label.setText( "Impurity Position" );
            aux2Label.setText( "Well Depth" );
            aux3Label.setText( "Impurity Depth" );
            aux1Bar.setValue( 75 );
            aux3Bar.setValue( 85 );
        }

        void getInfo( String s[], int o ) {
        }

        void drawPotential() {
            int i;
            int maxWells = 10;
            if( sampleCount > 260 ) {
                maxWells = sampleCount / 26;
            }
            int period = ( sampleCount * 7 / ( maxWells * 8 ) );
            int sep = ( sampleCount / ( maxWells * 8 ) );
            if( sep == 0 ) {
                sep++;
                period--;
            }
            wellCount = maxWells;
            double floor = 1 - ( aux2Bar.getValue() ) / 50.;
            double impfloor = 1 - ( aux3Bar.getValue() ) / 50.;
            int pos = aux1Bar.getValue() * maxWells / 101;
            int offset = ( sampleCount - wellCount * period ) / 2;
            width = period - sep;
            for( i = 0; i != sampleCount; i++ ) {
                if( i < offset ) {
                    pot[i] = 1;
                }
                else {
                    int ii = i - offset;
                    int j = ( ii % period );
                    double f = ( ii / period == pos ) ? impfloor : floor;
                    pot[i] = ( j < sep || ii / period >= wellCount ) ? 1 : f;
                }
            }
        }

        double getFloor() {
            return -1;
        }

        QuantumStatesFrame.Setup createNext() {
            return new QuantumStatesFrame.FiniteWellArrayDislocSetup();
        }
    }

    class FiniteWellArrayDislocSetup extends QuantumStatesFrame.FiniteWellArraySetup {
        String getName() {
            return "Well Array w/ Dislocation";
        }

        void select() {
            aux1Label.setText( "Impurity Position" );
            aux1Bar.setValue( 75 );
            aux2Label.setText( "Well Depth" );
            aux3Label.setText( "Impurity Offset" );
            aux3Bar.setValue( 80 );
        }

        void getInfo( String s[], int o ) {
        }

        void drawPotential() {
            int i;
            int maxWells = 8;
            int period = ( sampleCount * 7 / ( maxWells * 8 ) );
            int sep = ( sampleCount / ( maxWells * 4 ) );
            if( sep == 0 ) {
                sep++;
                period--;
            }
            wellCount = maxWells;
            double floor = 1 - ( aux2Bar.getValue() ) / 50.;
            int pos = aux1Bar.getValue() * maxWells / 101;
            int offset = ( sampleCount - wellCount * period ) / 2;
            int ioffset = ( 50 - aux3Bar.getValue() ) * ( sep - 1 ) / 50;
            width = period - sep;
            for( i = 0; i != sampleCount; i++ ) {
                if( i < offset ) {
                    pot[i] = 1;
                }
                else {
                    int ii = i - offset;
                    int j = ( ii % period );
                    pot[i] = ( j < sep || ii / period == pos || ii / period >= wellCount ) ? 1 : floor;
                    ii += ioffset;
                    j = ( ii % period );
                    if( ii / period == pos && j >= sep ) {
                        pot[i] = floor;
                    }
                }
            }
        }

        double getFloor() {
            return -1;
        }

        QuantumStatesFrame.Setup createNext() {
            return new QuantumStatesFrame.RandomWellArraySetup();
        }
    }

    class RandomWellArraySetup extends QuantumStatesFrame.FiniteWellArraySetup {
        String getName() {
            return "Random Well Array";
        }

        void select() {
            aux1Label.setText( "Randomness" );
        }

        void getInfo( String s[], int o ) {
        }

        int getAuxBarCount() {
            return 1;
        }

        void drawPotential() {
            int i;
            for( i = 0; i != 10; i++ ) {
                pot[i] = 1;
            }
            int ww = 8;
            while( i < sampleCount - ( 15 + ww ) ) {
                int j;
                for( j = 0; j != ww; j++ ) {
                    pot[i++] = -1;
                }
                int sep = 8 + ( ( getrand( 15 ) - 7 ) * aux1Bar.getValue() / 100 );
                for( j = 0; j != sep; j++ ) {
                    pot[i++] = 1;
                }
            }
            for( ; i != sampleCount; i++ ) {
                pot[i] = 1;
            }
        }

        QuantumStatesFrame.Setup createNext() {
            return new QuantumStatesFrame.FiniteWell2ArraySetup();
        }
    }

    class FiniteWell2ArraySetup extends QuantumStatesFrame.Setup {
        int width;
        int wellCount;

        String getName() {
            return "2 Well Array";
        }

        void select() {
            aux1Label.setText( "Well Count" );
            aux2Label.setText( "Well Depth" );
            aux3Label.setText( "Well Width" );
        }

        int getAuxBarCount() {
            return 3;
        }

        double getFloor() {
            return 1 - ( aux2Bar.getValue() ) / 50.;
        }

        void getInfo( String s[], int o ) {
        }

        void drawPotential() {
            int i;
            int maxWells = 10;
            if( sampleCount > 260 ) {
                maxWells = sampleCount / 26;
            }
            maxWells |= 1;
            int period = ( sampleCount * 7 / ( maxWells * 8 ) ) * ( aux3Bar.getValue() + 60 ) / 160;
            int sep = ( sampleCount / ( maxWells * 8 ) );
            if( sep == 0 ) {
                sep++;
                period--;
            }
            wellCount = aux1Bar.getValue() * ( maxWells ) / 101 + 1;
            double floor = getFloor();
            int offset = ( sampleCount - wellCount * period ) / 2;
            width = period - sep;
            for( i = 0; i != sampleCount; i++ ) {
                if( i < offset ) {
                    pot[i] = 1;
                }
                else {
                    int ii = i - offset;
                    int j = ( ii % period );
                    double f = ( ii / period % 2 == 0 ) ? floor : -.5;
                    pot[i] = ( j < sep || ii / period >= wellCount ) ? 1 : f;
                }
            }
        }

        QuantumStatesFrame.Setup createNext() {
            return new QuantumStatesFrame.DeltaArraySetup();
        }
    } /*class SinusoidalLatticeSetup extends Setup { String getName() { return "Sinusoidal Lattice"; } void select() { aux1Label.setText("Well Count"); aux2Label.setText("Well Depth"); } int getAuxBarCount() { return 2; } void drawPotential() { int n = aux1Bar.getValue() / 10; double amp = aux2Bar.getValue()/100.; int buf = sampleCount/10; int i; for (i = 0; i != sampleCount; i++) pot[i] = 1; for (i = buf; i != sampleCount-buf; i++) { double xx = (i-buf)/(sampleCount-buf*2.); pot[i] = 1-amp*(1-Math.cos(xx*2*pi*n)); } } Setup createNext() { return new DeltaArraySetup(); } }*/

    class DeltaArraySetup extends QuantumStatesFrame.Setup {
        String getName() {
            return "Delta Fn Array";
        }

        void select() {
            aux1Label.setText( "Well Count" );
            aux1Bar.setValue( 32 );
            aux2Label.setText( "Well Separation" );
            aux2Bar.setValue( 30 );
        }

        void drawPotential() {
            int i;
            int maxWells = 30;
            int width = aux2Bar.getValue() / 5 + 2;
            int count = aux1Bar.getValue() * ( maxWells ) / 101 + 1;
            int offset = ( sampleCount - ( count - 1 ) * width + 1 ) / 2;
            for( i = 1; i != sampleCount; i++ ) {
                if( i < offset ) {
                    pot[i] = 1;
                }
                else {
                    int ii = i - offset;
                    int j = ( ii % width );
                    pot[i] = 1;
                    if( j == 0 && count-- > 0 ) {
                        pot[i] = -1;
                    }
                }
            }
            for( i = 0; i != width; i++ ) {
                pot[i] = pot[sampleCount - 1 - i] = 1;
            }
        }

        QuantumStatesFrame.Setup createNext() {
            return null;
        }
    }

    class View extends Rectangle {
        int mid_y, lower_y;
        double ymult, ymult2, scale;
    }
}
