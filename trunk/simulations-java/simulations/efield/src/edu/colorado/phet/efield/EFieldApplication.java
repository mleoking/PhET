package edu.colorado.phet.efield;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Locale;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.Border;

import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.efield.electron.core.ParticleContainer;
import edu.colorado.phet.efield.electron.core.RandomSystemFactory;
import edu.colorado.phet.efield.electron.electricField.*;
import edu.colorado.phet.efield.electron.gui.*;
import edu.colorado.phet.efield.electron.gui.addRemove.AddRemove;
import edu.colorado.phet.efield.electron.gui.addRemove.PanelAdapter;
import edu.colorado.phet.efield.electron.gui.addRemove.SystemAdapter;
import edu.colorado.phet.efield.electron.gui.media.MediaControl;
import edu.colorado.phet.efield.electron.gui.mouse.ParticleGrabber;
import edu.colorado.phet.efield.electron.gui.mouse.ParticleThrower;
import edu.colorado.phet.efield.electron.gui.popupMenu.MenuConstructor;
import edu.colorado.phet.efield.electron.gui.popupMenu.ParticlePopupListener;
import edu.colorado.phet.efield.electron.gui.vectorChooser.DefaultVectorPainter;
import edu.colorado.phet.efield.electron.gui.vectorChooser.VectorChooser;
import edu.colorado.phet.efield.electron.gui.vectorChooser.VectorPainter;
import edu.colorado.phet.efield.electron.laws.CoulombsLaw;
import edu.colorado.phet.efield.electron.laws.ForceLawAdapter;
import edu.colorado.phet.efield.electron.particleFactory.CustomizableParticleFactory;
import edu.colorado.phet.efield.electron.particleFactory.ShowParticlePropertyDialog;
import edu.colorado.phet.efield.electron.phys2d_efield.DoublePoint;
import edu.colorado.phet.efield.electron.phys2d_efield.Particle;
import edu.colorado.phet.efield.electron.phys2d_efield.System2D;
import edu.colorado.phet.efield.electron.phys2d_efield.SystemRunner;
import edu.colorado.phet.efield.electron.utils.ResourceLoader;

public class EFieldApplication extends JApplet {
    // Localization
    public static final String localizedStringsPath = "efield/localization/efield-strings";
    private String applicationLocale = null;

    public void init() {
        if ( applicationLocale == null ) {
            applicationLocale = Toolkit.getDefaultToolkit().getProperty( "javaws.phet.locale", null );
            if ( applicationLocale != null && !applicationLocale.equals( "" ) ) {
                SimStrings.getInstance().setLocale( new Locale( applicationLocale ) );
            }
        }
        SimStrings.setStrings( localizedStringsPath );

        if ( args == null ) {
            args = new String[]{"10"};
        }
        int num = 0;
        int x = 50;
        int y = 50;
        int width = 300;
        int height = 300;
        ShowParticlePropertyDialog sppd = new ShowParticlePropertyDialog( 1, -1 );

        Particle def = sppd.getDialog().getProperties();
        CustomizableParticleFactory ef = new CustomizableParticleFactory( x, y, width, height, def );
        sppd.getDialog().addParticlePropertyListener( ef );

        //ElectronFactory ef=new ElectronFactory(x,y,width,height);
        RandomSystemFactory randFact = new RandomSystemFactory( 10, num, x, y, width, height );
        ElectricForceLaw fieldLaw = new ElectricForceLaw( new DoublePoint( 0, 0 ) );
        randFact.addForceLaw( fieldLaw );

        CoulombsLaw law = new CoulombsLaw( 100000 );
        ForceLawAdapter cla = new ForceLawAdapter( new Particle[0], law );
        randFact.addForceLaw( cla );

        System2D sys = randFact.newSystem();

        double dt = .15;
        int wait = 35;
        SystemRunner sr = new SystemRunner( sys, dt, wait );
        new Thread( sr ).start();

        ParticlePanel pp = new ParticlePanel();
        //ParticlePainter painter=new DotPainter(Color.blue,16);
        BufferedImage bi = null;
        try {
//            bi = ResourceLoader.loadBufferedImage("images/Electron3.GIF", pp, true);
            bi = ResourceLoader.loadBufferedImage( "efield/images/electron9.gif", pp, true );
        }
        catch( InterruptedException e ) {
            e.printStackTrace();
        }
//        bi = electron.utils.AlphaFixer.patchAlpha(bi);
        ParticlePainter painter = new ImagePainter( bi );

        randFact.updatePanel( pp, sys, painter );
        //ParticleGrabber pg=new ParticleGrabber(pp,sys,sr);
        ParticleGrabber pg = new ParticleThrower( pp, sys, sr, 5, 18 );
        pp.addMouseListener( pg );
        pp.addMouseMotionListener( pg );

        getContentPane().setLayout( new BorderLayout() );
        getContentPane().add( pp, BorderLayout.CENTER );
        validate();

        MediaControl mc = null;
        try {
            mc = new MediaControl( sr, randFact, pp, painter,
                                   ResourceLoader.loadBufferedImage( "efield/icons/media/Play24.gif", pp, false ),
                                   ResourceLoader.loadBufferedImage( "efield/icons/media/Pause24.gif", pp, false ),
                                   ResourceLoader.loadBufferedImage( "efield/icons/media/Stop24.gif", pp, false ) );
        }
        catch( InterruptedException e ) {
            e.printStackTrace();
        }
        JPanel controlPanel = ( mc.getPanel() );


        AddRemove ar = new AddRemove( new Vector(), ef, pp, painter );
        ar.add( new PanelAdapter( pp, painter ) );
        SystemAdapter sa = new SystemAdapter( sys );
        ar.add( sa );

        for ( int i = 0; i < sys.numLaws(); i++ ) {
            if ( sys.lawAt( i ) instanceof ParticleContainer ) {
                ar.add( (ParticleContainer) sys.lawAt( i ) );
            }
        }

        JPanel addRemovePanel = ar.getJPanel();

        JButton propertyButton = new JButton( SimStrings.get( "FieldNode2.PropertiesButton" ) );
        propertyButton.addActionListener( sppd );
        addRemovePanel.add( propertyButton );


        JPanel southPanel = new JPanel();
        southPanel.setLayout( new BoxLayout( southPanel, BoxLayout.X_AXIS ) );
        southPanel.add( controlPanel );
        southPanel.add( addRemovePanel );
        VectorPainter vp = new DefaultVectorPainter( Color.blue, new BasicStroke( 6 ), Math.PI / 8, 10 );

        VectorChooser fieldPanel = new VectorChooser( 130, 130, 1, 1, vp );
        fieldPanel.addListener( fieldLaw );
        southPanel.add( fieldPanel );

        Border etched = BorderFactory.createEtchedBorder();
        Border b = BorderFactory.createTitledBorder( etched, SimStrings.get( "FieldNode2.ExternalFieldBorder" ) );
        fieldPanel.setBorder( b );
        getContentPane().add( southPanel, BorderLayout.SOUTH );

        mc.add( ar );/*Notify of reset actions.*/
        mc.add( pg );

        Painter wally = new WallPainter( x - 15, y - 15, width + 25, height + 25, new BasicStroke( 8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER ), Color.blue );
        pp.add( wally );

        int numFieldDots = 6;
//        if( args.length > 0 ) {
//            numFieldDots = Integer.parseInt( args[0] );
//        }

        VectorPainter vp2 = new DefaultVectorPainter( Color.blue, new BasicStroke( 2 ), Math.PI / 8, 4 );
        ElectricFieldPainter electricFieldPainter = new ElectricFieldPainter( x, y, width, height, numFieldDots, numFieldDots, vp2 );
        electricFieldPainter.addSource( fieldLaw );
        int maxArrowLength = 25;
        ChargeFieldSource cs = new ChargeFieldSource( cla, 120000, maxArrowLength ); //sa
        electricFieldPainter.addSource( cs );

        pp.add( electricFieldPainter );

        MenuConstructor emcee = new FieldMenuConstructor( cs, pp );
        ParticlePopupListener ml = new ParticlePopupListener( pp, emcee );
        pp.addMouseListener( ml );

        JMenuBar jj = new JMenuBar();
        setJMenuBar( jj );
        //jj.add(emcee.getMenu(new Particle()));
        DiscreteFieldSlider dfs = new DiscreteFieldSlider( electricFieldPainter, pp );
        JMenuItem fieldDiscretionItem = new JMenuItem( SimStrings.get( "FieldNode2.SetFieldDiscretenessMenuItem" ) );
        fieldDiscretionItem.addActionListener( dfs );
        JMenu fieldMenu = new JMenu( SimStrings.get( "FieldNode2.ElectricFieldMenuTitle" ) );
        fieldMenu.add( fieldDiscretionItem );
        jj.add( fieldMenu );

    }

    static String[] args = null;

    public static void main( final String[] argx ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                PhetLookAndFeel phetLookAndFeel = new PhetLookAndFeel();
                phetLookAndFeel.initLookAndFeel();
                SimStrings.getInstance().init( argx, localizedStringsPath );

                EFieldApplication j = new EFieldApplication();
                j.setSize( new Dimension( 600, 600 ) );

                String argsKey = "user.language=";
                for ( int i = 0; i < argx.length; i++ ) {
                    if ( argx[i].startsWith( argsKey ) ) {
                        args = new String[argx.length - 1];
                        break;
                    }
                }
                if ( args == null ) {
                    args = argx;
                }
                else {
                    int k = 0;
                    for ( int i = 0; i < argx.length; i++ ) {
                        if ( !argx[i].startsWith( argsKey ) ) {
                            args[k++] = argx[i];
                        }
                    }
                }

                j.init();

                JFrame f = new JFrame( SimStrings.get( "EFieldApplication.title" ) );
                f.setContentPane( j );
                f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
                f.setSize( new Dimension( 500, 600 ) );
                //f.pack();
                f.setVisible( true );
            }
        } );
    }
}
