package electron.electricField;

import electron.core.ParticleContainer;
import electron.core.RandomSystemFactory;
import electron.gui.*;
import electron.gui.addRemove.AddRemove;
import electron.gui.addRemove.PanelAdapter;
import electron.gui.addRemove.SystemAdapter;
import electron.gui.media.MediaControl;
import electron.gui.mouse.ParticleGrabber;
import electron.gui.mouse.ParticleThrower;
import electron.gui.popupMenu.MenuConstructor;
import electron.gui.popupMenu.ParticlePopupListener;
import electron.gui.vectorChooser.DefaultVectorPainter;
import electron.gui.vectorChooser.VectorChooser;
import electron.gui.vectorChooser.VectorPainter;
import electron.laws.CoulombsLaw;
import electron.laws.ForceLawAdapter;
import electron.particleFactory.CustomizableParticleFactory;
import electron.particleFactory.ShowParticlePropertyDialog;
import electron.utils.ResourceLoader;
import phys2d.DoublePoint;
import phys2d.Particle;
import phys2d.System2D;
import phys2d.SystemRunner;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Vector;

import util.ExitOnClose;

public class FieldNode2 extends JApplet {
    public void init() {
        if (args == null)
            args = new String[]{"10"};
        int num = 0;
        int x = 50;
        int y = 50;
        int width = 300;
        int height = 300;
        ShowParticlePropertyDialog sppd = new ShowParticlePropertyDialog(1, -1);

        Particle def = sppd.getDialog().getProperties();
        CustomizableParticleFactory ef = new CustomizableParticleFactory(x, y, width, height, def);
        sppd.getDialog().addParticlePropertyListener(ef);

        //ElectronFactory ef=new ElectronFactory(x,y,width,height);
        RandomSystemFactory randFact = new RandomSystemFactory(10, num, x, y, width, height);
        ElectricForceLaw fieldLaw = new ElectricForceLaw(new DoublePoint(0, 0));
        randFact.addForceLaw(fieldLaw);

        CoulombsLaw law = new CoulombsLaw(100000);
        ForceLawAdapter cla = new ForceLawAdapter(new Particle[0], law);
        randFact.addForceLaw(cla);

        System2D sys = randFact.newSystem();

        double dt = .15;
        int wait = 35;
        SystemRunner sr = new SystemRunner(sys, dt, wait);
        new Thread(sr).start();

        ParticlePanel pp = new ParticlePanel();
        //ParticlePainter painter=new DotPainter(Color.blue,16);
        BufferedImage bi = null;
        try {
//            bi = ResourceLoader.loadBufferedImage("images/Electron3.GIF", pp, true);
            bi = ResourceLoader.loadBufferedImage("images/electron9.gif", pp, true);
        }
        catch( InterruptedException e ) {
            e.printStackTrace();
        }
//        bi = electron.utils.AlphaFixer.patchAlpha(bi);
        ParticlePainter painter = new ImagePainter(bi);

        randFact.updatePanel(pp, sys, painter);
        //ParticleGrabber pg=new ParticleGrabber(pp,sys,sr);
        ParticleGrabber pg = new ParticleThrower(pp, sys, sr, 5, 18);
        pp.addMouseListener(pg);
        pp.addMouseMotionListener(pg);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(pp, BorderLayout.CENTER);
        validate();

        MediaControl mc = null;
        try {
            mc = new MediaControl(sr, randFact, pp, dt, wait, painter,
                            ResourceLoader.loadBufferedImage("icons/media/Play24.gif", pp, false),
                            ResourceLoader.loadBufferedImage("icons/media/Pause24.gif", pp, false),
                            ResourceLoader.loadBufferedImage("icons/media/Stop24.gif", pp, false));
        }
        catch( InterruptedException e ) {
            e.printStackTrace();
        }
        JPanel controlPanel = (mc.getPanel());


        AddRemove ar = new AddRemove(new Vector(), ef, pp, painter);
        ar.add(new PanelAdapter(pp, painter));
        SystemAdapter sa = new SystemAdapter(sys);
        ar.add(sa);

        for (int i = 0; i < sys.numLaws(); i++)
            if (sys.lawAt(i) instanceof ParticleContainer)
                ar.add((ParticleContainer) sys.lawAt(i));

        JPanel addRemovePanel = ar.getJPanel();

        JButton propertyButton = new JButton("Properties");
        propertyButton.addActionListener(sppd);
        addRemovePanel.add(propertyButton);


        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.X_AXIS));
        southPanel.add(controlPanel);
        southPanel.add(addRemovePanel);
        VectorPainter vp = new DefaultVectorPainter(Color.blue, new BasicStroke(6), Math.PI / 8, 10);

        VectorChooser fieldPanel = new VectorChooser(130, 130, 1, 1, vp);
        fieldPanel.addListener(fieldLaw);
        southPanel.add(fieldPanel);

        Border etched = BorderFactory.createEtchedBorder();
        Border b = BorderFactory.createTitledBorder(etched, "External Field");
        fieldPanel.setBorder(b);
        getContentPane().add(southPanel, BorderLayout.SOUTH);

        mc.add(ar);/*Notify of reset actions.*/
        mc.add(pg);

        Painter wally = new WallPainter(x - 15, y - 15, width + 25, height + 25, new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER), Color.blue);
        pp.add(wally);

        int numFieldDots = 6;
        if (args.length > 0)
            numFieldDots = Integer.parseInt(args[0]);

        VectorPainter vp2 = new DefaultVectorPainter(Color.blue, new BasicStroke(2), Math.PI / 8, 4);
        ElectricFieldPainter electricFieldPainter = new ElectricFieldPainter(x, y, width, height, numFieldDots, numFieldDots, vp2);
        electricFieldPainter.addSource(fieldLaw);
        int maxArrowLength = 25;
        ChargeFieldSource cs = new ChargeFieldSource(cla, 120000, maxArrowLength); //sa
        electricFieldPainter.addSource(cs);

        pp.add(electricFieldPainter);

        MenuConstructor emcee = new FieldMenuConstructor(cs, pp);
        ParticlePopupListener ml = new ParticlePopupListener(pp, emcee);
        pp.addMouseListener(ml);

        JMenuBar jj = new JMenuBar();
        setJMenuBar(jj);
        //jj.add(emcee.getMenu(new Particle()));
        DiscreteFieldSlider dfs = new DiscreteFieldSlider(electricFieldPainter, pp);
        JMenuItem fieldDiscretionItem = new JMenuItem("Set Field Discreteness");
        fieldDiscretionItem.addActionListener(dfs);
        JMenu fieldMenu = new JMenu("Electric Field");
        fieldMenu.add(fieldDiscretionItem);
        jj.add(fieldMenu);

    }

    static String[] args;

    public static void main(String[] argx) {
        args = argx;
        JApplet j = new FieldNode2();
        j.setSize(new Dimension(600, 600));
        j.init();
        JFrame f = new JFrame(j.getClass().getName());
        f.setContentPane(j);
        f.addWindowListener(new ExitOnClose());
        f.setSize(new Dimension(500, 560));
        //f.pack();
        f.setVisible(true);
    }
}
