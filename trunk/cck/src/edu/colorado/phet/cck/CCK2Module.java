/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck;

import edu.colorado.phet.cck.common.*;
import edu.colorado.phet.cck.elements.ErrorGraphic;
import edu.colorado.phet.cck.elements.ImageSuite;
import edu.colorado.phet.cck.elements.ammeter.Ammeter;
import edu.colorado.phet.cck.elements.ammeter.AmmeterGraphic;
import edu.colorado.phet.cck.elements.branch.*;
import edu.colorado.phet.cck.elements.branch.components.*;
import edu.colorado.phet.cck.elements.circuit.Circuit;
import edu.colorado.phet.cck.elements.circuit.CircuitGraphic;
import edu.colorado.phet.cck.elements.circuit.CircuitObserver;
import edu.colorado.phet.cck.elements.dvm.Voltmeter;
import edu.colorado.phet.cck.elements.dvm.VoltmeterGraphic;
import edu.colorado.phet.cck.elements.junction.Junction;
import edu.colorado.phet.cck.elements.kirkhoff.CircuitSolver;
import edu.colorado.phet.cck.elements.particles.ParticleLayout;
import edu.colorado.phet.cck.elements.particles.ParticleSet;
import edu.colorado.phet.cck.elements.particles.ParticleSetGraphic;
import edu.colorado.phet.cck.util.MyConsoleHandler;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.math.PhetVector;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.model.simpleobservable.SimpleObserver;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.ApplicationDescriptor;
import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.common.view.apparatuspanelcontainment.ApparatusPanelContainer;
import edu.colorado.phet.common.view.apparatuspanelcontainment.SingleApparatusPanelContainer;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.InteractiveGraphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.graphics.transforms.TransformListener;
import edu.colorado.phet.common.view.util.AspectRatioLayout;
import edu.colorado.phet.common.view.util.framesetup.FrameSetup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * User: Sam Reid
 * Date: Aug 28, 2003
 * Time: 1:28:07 AM
 * Copyright (c) Aug 28, 2003 by Sam Reid
 */
public class CCK2Module extends Module {
    public static final double ELECTRON_SEPARATION = .35;

    private ModelViewTransform2D transform;
    boolean antialias = true;

    private ParticleLayout particleLayout;

    private ParticleSet particleSet;
    private ParticleSetGraphic particleSetGraphic;
    private Circuit circuit;
    private CircuitGraphic circuitGraphic;

    private ClickToDeselect clickToDeselect;
    private VoltmeterGraphic voltmeterGraphic;

    CCK2ImageSuite imageSuite;
    private static PhetFrame frame;
    private double creationX1;
    private double creationX2;
    private double dragY;
    private double dragDY;

    private LifelikeGraphicFactory lifelikeGraphicFactory;
    private SchematicGraphicFactory schematicGraphicFactory;

    private CircuitSolver circuitSolver;
    MyConsoleHandler consoleHandler;
    private boolean kirkhoffLogging = true;
    private Graphic particleRenderer;
    private Graphic showErrorGraphic;

    final String helpMessage =
            "Building a circuit:\n" +
            "Drag components onto the build area.\n" +
            "Connect ends together with wire.\n" +
            "Light bulb connections are at the bottom and on the left hand side of the bulb base.\n" +
            "Battery connections are at either end of the battery.\n\n" +

            "Changing values & deleting:\n" +
            "Right-click on any component to delete it, show its value, or change its value.\n" +
            "Right-click at any connection point to split that connection apart.";
    private CircuitObserver dvmUpdate = new CircuitObserver() {
        public void branchAdded(Circuit circuit2, Branch branch) {
        }

        public void branchRemoved(Circuit circuit2, Branch branch) {
            updateDVMAndAmmeter();
        }

        public void connectivityChanged(Circuit circuit2) {
            updateDVMAndAmmeter();
        }
    };

    CircuitObserver repainter = new CircuitObserver() {
        public void branchAdded(Circuit circuit2, Branch branch) {
            getApparatusPanel().repaint();
        }

        public void branchRemoved(Circuit circuit2, Branch branch) {
            getApparatusPanel().repaint();
        }

        public void connectivityChanged(Circuit circuit2) {
            getApparatusPanel().repaint();
        }
    };
    private static int numTicks = 0;
    private ComponentListener relayout = new ComponentAdapter() {
        public void componentResized(ComponentEvent e) {
            relayout();
        }

        public void componentShown(ComponentEvent e) {
            relayout();
        }
    };
    private BufferedImage flameImage;
    private JLabel helpLabel;
    private boolean firstBranch = true;
    private boolean usePointAmmeter;
    private Ammeter ammeter;
    private AmmeterGraphic ammeterGraphic;

    public BufferedImage getFlameImage() {
        return flameImage;
    }

    public CCK2Module(final boolean usePointAmmeter, AbstractClock clock) throws IOException {
        super("Circuit Construction Kit-ii");
        this.usePointAmmeter = usePointAmmeter;

        imageSuite = new CCK2ImageSuite();
        flameImage = imageSuite.getImageLoader().loadBufferedImage("images/flame.gif");
        setModel(new BaseModel(clock));
        setApparatusPanel(new ApparatusPanel());
        showErrorGraphic = new ErrorGraphic(getApparatusPanel());
        getApparatusPanel().addGraphic(showErrorGraphic, 1000);
        getApparatusPanel().addGraphic(new Graphic() {
            public void paint(Graphics2D g) {
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                if (antialias)
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            }
        }, Integer.MIN_VALUE);
        circuitSolver = new CircuitSolver();
        Color backgroundColor = new Color(166, 177, 204);//not so bright
//        Color backgroundColor=new Color(220, 220, 249);
        getApparatusPanel().setBackground(backgroundColor);
        Rectangle2D.Double modelRect = new Rectangle2D.Double(0, 0, 10, 10);
        transform = new ModelViewTransform2D(modelRect, new Rectangle(0, 0, 1, 1));

        circuit = new Circuit();
        lifelikeGraphicFactory = new LifelikeGraphicFactory(this, imageSuite.getBaseSwitchImage());
        schematicGraphicFactory = new SchematicGraphicFactory(this, imageSuite.getBaseSwitchImage());

        circuit.addCircuitObserver(circuitSolver);
        circuit.addCircuitObserver(repainter);

        circuitGraphic = new CircuitGraphic(circuit, this);
        getApparatusPanel().addGraphic(circuitGraphic, 0);
        getApparatusPanel().addComponentListener(relayout);
        particleLayout = new ParticleLayout(ELECTRON_SEPARATION);

        particleSet = new ParticleSet(circuit);
        particleSetGraphic = new ParticleSetGraphic(particleSet, getTransform(), this);

        getModel().addModelElement(particleSet);
        //render particles near junctions over the junctions.
        particleRenderer = new ParticleRenderer(particleSet, particleSetGraphic);
        getApparatusPanel().addGraphic(particleRenderer, 1000);


//        CreationBackdropGraphic cb=new CreationBackdropGraphic();

        creationX1 = 8.0;
        creationX2 = 9.5;

        dragDY = 1.05;
        int dragYInit = 9;
        dragY = dragYInit;


        final Resistor dragBranch = new Resistor(circuit, creationX1, dragY, creationX2, dragY, 1);
        dragY -= dragDY;
        final Battery batteryWithWires = new Battery(circuit, creationX1, dragY, creationX2, dragY, 9, 0);
        dragY -= dragDY;
        final BufferedImage battIm = imageSuite.getLifelikeSuite().getBatteryImage();
        final double modelWidthBatt = transform.viewToModelDifferentialX(battIm.getWidth());
//        double battSep = modelWidthBatt;
//        final Battery batteryWithoutWires = new Battery(circuit, creationX1, dragY, creationX1 + battSep, dragY, 9.0, 1);
//        final double batty = dragY;
//        dragY -= dragDY;
        final Bulb bulbCreateBranch = new Bulb(circuit, creationX1, dragY, creationX2, dragY - .5, new PhetVector(.5, 0), 10);
        dragY -= dragDY;
        final Switch switchCreateBranch = new Switch(circuit, creationX1, dragY, creationX2, dragY);
        dragY -= dragDY;
        final Wire wireDragBranch = new Wire(circuit, creationX1, dragY, creationX2, dragY);

        Rectangle2D.Double creationPanelRect = new Rectangle2D.Double(creationX1 - 1.2, dragY - .5, modelRect.width - creationX1 - .1 + 1.2, dragYInit - dragY + 1);
        HasModelShape hsm = new FixedModelShape(creationPanelRect);
        ShapeGraphic2 creationPanelGraphic = new ShapeGraphic2(hsm, getTransform(), new Color(244, 201, 255), new BasicStroke(2));
        getApparatusPanel().addGraphic(creationPanelGraphic, -1);


        DragToCreate createResistor = getDragToCreate(dragBranch, "Resistor");
        getApparatusPanel().addGraphic(createResistor, -1);


//        transform.addTransformListener(new TransformListener() {
//            public void transformChanged(ModelViewTransform2D ModelViewTransform2D) {
//                //Not quite working.
//                double modelWidthBatt = transform.viewToModelDifferentialX(battIm.getWidth() + DefaultCompositeBranchGraphic.JUNCTION_RADIUS*4);
//                batteryWithoutWires.setLength(modelWidthBatt);
////                batteryWithoutWires.setStartPointLocation(creationX1, batty);
////                batteryWithoutWires.setEndPointLocation(creationX1 + modelWidthBatt, batty);
////                batteryWithoutWires.resetDirVector();
//            }
//        });
//        DragToCreate createBattery = getDragToCreate(batteryWithoutWires, "Battery");
//        getApparatusPanel().addGraphic(createBattery, -1);

        DragToCreate createBattery2 = getDragToCreate(batteryWithWires, "Battery w/ Wires");
        getApparatusPanel().addGraphic(createBattery2, -1);

        DragToCreate createBulb = getDragToCreate(bulbCreateBranch, "Light Bulb");
        getApparatusPanel().addGraphic(createBulb, -1);

        DragToCreate createSwitch = getDragToCreate(switchCreateBranch, "Switch");
        getApparatusPanel().addGraphic(createSwitch, -1);

        DragToCreate createWire = getDragToCreate(wireDragBranch, "Wire");
        getApparatusPanel().addGraphic(createWire, -1);

        clickToDeselect = new ClickToDeselect(circuit);
        getApparatusPanel().addGraphic(clickToDeselect, -10);

        dragY -= dragDY;

        Voltmeter vm = new Voltmeter(creationX1, dragY);
        voltmeterGraphic = new VoltmeterGraphic(vm, getTransform(), imageSuite.getImageLoader());

        getApparatusPanel().addGraphic(voltmeterGraphic, 100);

        voltmeterGraphic.getRedLeadGraphic().addObserver(repaint);
        voltmeterGraphic.getBlackLeadGraphic().addObserver(repaint);
        voltmeterGraphic.getVoltmeterUnitGraphic().addObserver(repaint);

        SimpleObserver checkDVM = new SimpleObserver() {
            public void update() {
                updateDVMAndAmmeter();
            }
        };
        //Whenever the circuit moves or a dvm lead moves, check the dvm connectivity.
        voltmeterGraphic.getRedLeadGraphic().addObserver(checkDVM);
        voltmeterGraphic.getBlackLeadGraphic().addObserver(checkDVM);
        circuit.addCircuitObserver(dvmUpdate);
        transform.addTransformListener(new TransformListener() {
            public void transformChanged(ModelViewTransform2D ModelViewTransform2D) {
                getApparatusPanel().repaint();
            }
        });

//        JButton helpButton = new JButton("Help");
//        helpButton.setFont(new Font("Lucida Sans", Font.BOLD, 36));
//        helpButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                JOptionPane.showMessageDialog(getApparatusPanel(), helpMessage, "Using this simulation", JOptionPane.INFORMATION_MESSAGE);
//            }
//        });

        getApparatusPanel().setLayout(new FlowLayout(FlowLayout.LEFT));
//        getApparatusPanel().add(helpButton);
        JPanel viewPanel = new ViewPanel(this);
        getApparatusPanel().add(viewPanel);

        helpLabel = new JLabel("Right click an object to show options.");
        helpLabel.setFont(new Font("Lucida Sans", Font.BOLD, 16));
        helpLabel.setForeground(Color.black);
        getApparatusPanel().add(helpLabel);
        if (usePointAmmeter) {
            ammeter = new Ammeter(vm.getVoltmeterUnit().getX(), vm.getVoltmeterUnit().getY() + .5);
            ammeterGraphic = new AmmeterGraphic(ammeter, getTransform(), this, circuitGraphic);
            getApparatusPanel().addGraphic(ammeterGraphic, 2000);
        }

        JunctionCoverUpGraphic jcpg = new JunctionCoverUpGraphic(circuitGraphic, transform, imageSuite.getParticleImageWidth() / 2 + 3);
        getApparatusPanel().addGraphic(jcpg, 1000);

        setupLogging();
        getApparatusPanel().addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                circuitSolver.applyKirchoffsLaws(circuit);
                if (usePointAmmeter)
                    ammeterGraphic.doUpdate();
            }
        });
        getApparatusPanel().addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                getApparatusPanel().requestFocus();
            }
        });
        getApparatusPanel().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    //find the selected item.
                    for (int i = 0; i < circuit.numBranches(); i++) {
                        Branch b = circuit.branchAt(i);
                        if (b.isSelected()) {
                            circuit.removeBranch(b);
                            i = -1;
                        }
                    }
                }
            }
        });

        AmmeterBranch ammeterBranch = new AmmeterBranch(this.getCircuit(), 5.092, .414, 7.038, .408);
        //branch = x1=5.092, y1=.414, x2=7.038, y2=.408, voltage=.0, current=.0, id=0
        this.getCircuit().addBranch(ammeterBranch);

        setLifelikeWireColor(COPPER);
    }
//    public static final Color COPPER=new Color(235,160,40);
//    public static final Color COPPER=new Color(214, 18, 34);

//    public static final Color COPPER=new Color(Integer.parseInt("B87333",16));//new Color(214, 18, 34);
    public static final Color COPPER = new Color(Integer.parseInt("D98719", 16));//new Color(214, 18, 34);
//    static{
//        System.out.println("Copper=rgb="+COPPER.getRed()+", "+COPPER.getGreen()+", "+COPPER.getBlue());
//    }

    private DragToCreate getDragToCreate(final Branch branch, String name) {
        AbstractBranchGraphic cbg = circuitGraphic.createBranchGraphic(branch);
        InteractiveGraphicSource source = new InteractiveGraphicSource() {
            public InteractiveGraphic newInteractiveGraphic() {
                Branch newElm = branch.copy();
                particleLayout.layout(newElm, particleSet);
                circuit.addBranch(newElm);
                AbstractBranchGraphic gr = circuitGraphic.getGraphic(newElm);
                System.out.println("gr = " + gr);
                return gr;
            }
        };
        Point loc = transform.modelToView(branch.getX1(), branch.getY1());
        DragToCreate.Proxy proxy = new DragToCreate.Proxy() {
            public void mousePressed(InteractiveGraphic created, MouseEvent event) {
                AbstractBranchGraphic abg = (AbstractBranchGraphic) created;
                InteractiveGraphic main = abg.getMainBranchGraphic();
                main.mousePressed(event);
            }

            public void mouseDragged(InteractiveGraphic created, MouseEvent event) {
                AbstractBranchGraphic abg = (AbstractBranchGraphic) created;
                InteractiveGraphic main = abg.getMainBranchGraphic();
                main.mouseDragged(event);
            }

            public void mouseReleased(InteractiveGraphic created, MouseEvent event) {
                AbstractBranchGraphic abg = (AbstractBranchGraphic) created;
                InteractiveGraphic main = abg.getMainBranchGraphic();
                main.mouseReleased(event);
            }
        };
        final DragToCreate dragToCreate = new DragToCreate(cbg, source, name, loc, proxy);
        transform.addTransformListener(new TransformListener() {
            public void transformChanged(ModelViewTransform2D ModelViewTransform2D) {
                dragToCreate.setTipLocation(transform.modelToView(branch.getX1(), branch.getY1()));
            }
        });
        return dragToCreate;
    }

    class MyFormatter extends Formatter {
        public String format(LogRecord record) {
            String s = record.getMessage() + "\n";
            return s;
        }
    }

    private void setupLogging() {
        MyFormatter mf = new MyFormatter();
        consoleHandler = new MyConsoleHandler(mf);
        circuitSolver.getLogger().addHandler(consoleHandler);
    }

    private void setView(BranchGraphicFactory gf) {
        circuitGraphic.setBranchGraphicFactory(gf);
        getApparatusPanel().repaint();
    }

    void setLifelikeView() {
        setView(lifelikeGraphicFactory);
    }

    void setSchematicView() {
        setView(schematicGraphicFactory);
    }


    public void updateDVMAndAmmeter() {
        voltmeterGraphic.updateVoltageReading(circuitGraphic);
        if (usePointAmmeter)
            ammeterGraphic.doUpdate();
    }

    SimpleObserver repaint = new SimpleObserver() {
        public void update() {
            getApparatusPanel().repaint();
        }
    };

    public void deselectAll() {
        clickToDeselect.deselectAll(getApparatusPanel());
    }

    private void relayout() {
        transform.setViewBounds(getApparatusPanel().getBounds());
    }

    public void activate(PhetApplication app) {
    }

    public void deactivate(PhetApplication app) {
    }

    public void activateInternal(PhetApplication app) {
    }

    public void deactivateInternal(PhetApplication app) {
    }

    public ModelViewTransform2D getTransform() {
        return transform;
    }

    public void repaint() {
        getApparatusPanel().repaint();
    }

    public ImageSuite getLifelikeImageSuite() {
        return imageSuite.getLifelikeSuite();
    }


    public void relayoutElectrons(Branch branch) {
        particleLayout.layout(branch, particleSet);
    }

    public void relayoutElectrons(Junction junction) {
        particleLayout.layout(circuit, junction, particleSet);
    }

    public void removeElectrons(Junction junction) {
        Branch[] br = circuit.getBranches(junction);
        for (int i = 0; i < br.length; i++) {
            Branch branch2 = br[i];
            removeElectrons(branch2);
        }
    }

    private void removeElectrons(Branch branch2) {
        particleSet.removeParticlesForBranch(branch2);
    }

    public Circuit getCircuit() {
        return circuit;
    }

    public void junctionDragged() {
        updateDVMAndAmmeter();
    }

    public void branchMoved() {
        updateDVMAndAmmeter();
    }

    public PhetFrame getPhetFrame() {
        return frame;
    }

    public ParticleSetGraphic getParticleSetGraphic() {
        return particleSetGraphic;
    }

    public LifelikeGraphicFactory getLifelikeGraphicFactory() {
        return lifelikeGraphicFactory;
    }

    public ImageSuite getSchematicImageSuite() {
        return imageSuite.getSchematicSuite();
    }

    public SchematicGraphicFactory getSchematicGraphicFactory() {
        return schematicGraphicFactory;
    }


    void clearCircuit() {
        while (circuit.numBranches() > 0) {
            circuit.removeBranch(circuit.branchAt(0));
        }//clear the old circuit
        AmmeterBranch ammeterBranch = new AmmeterBranch(this.getCircuit(), 5.092, .414, 7.038, .408);
        //branch = x1=5.092, y1=.414, x2=7.038, y2=.408, voltage=.0, current=.0, id=0
        this.getCircuit().addBranch(ammeterBranch);
    }

    void setCircuit(Circuit c) {
        clearCircuit();
        for (int i = 0; i < c.numBranches(); i++) {
            Branch b = c.branchAt(i);
            circuit.attachBranch(b);
        }
        particleLayout.layout(circuit, particleSet);
        circuit.fireConnectivityChanged();
        getApparatusPanel().repaint();
    }

    public void setKirkhoffLogging(boolean on) {
        circuitSolver.setLogging(on);
        this.kirkhoffLogging = on;
    }

    public void applyKirkhoffsLaws() {
        circuitSolver.applyKirchoffsLaws(circuit);
    }

    public boolean isKirkhoffLoggingEnabled() {
        return kirkhoffLogging;
    }

    public CCK2ImageSuite getImageSuite() {
        return imageSuite;
    }

    public static void main(String[] args) throws IOException {
        SwingTimerClock stc = new SwingTimerClock(1, 30, true);
        boolean usePointAmmeter = false;
        final CCK2Module module = new CCK2Module(usePointAmmeter, stc);
//        FrameSetup fs = new FrameSetup() {
//            public void initialize(JFrame frame) {
//                frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
//            }
//        };
        ApplicationDescriptor ad = new ApplicationDescriptor("Circuit Construction Kit II",
                "Create, interact with and observe simple circuits.",
                "ii-V6.5", new FrameSetup() {
                    public void initialize(JFrame jFrame) {
                        jFrame.setVisible(true);
                        jFrame.setSize(600, 600);
                    }
                });

        PhetApplication app = new PhetApplication(ad, module, stc);
        app.getApplicationView().getPhetFrame().setSize(Toolkit.getDefaultToolkit().getScreenSize().width * 3 / 4, Toolkit.getDefaultToolkit().getScreenSize().height * 3 / 4);
//        app.getApplicationView().getPhetFrame().setExtendedState(JFrame.MAXIMIZED_BOTH);
        app.getApplicationView().getBasicPhetPanel().setAppControlPanel(null);
        frame = app.getApplicationView().getPhetFrame();

        JMenuBar jmb = frame.getJMenuBar();
        for (int i = 0; i < jmb.getMenuCount(); i++) {
            JMenu menu = jmb.getMenu(i);
            if (menu.getText().toLowerCase().equals("controls") || menu.getText().toLowerCase().equals("file") || menu.getText().toLowerCase().equals("help")) {
                jmb.remove(menu);
                i = -1;
            }
        }

        JMenu helpMenu = new JMenu("Help");
        JMenuItem about = new JMenuItem("About");

        final String title = "About the CCK";
        VersionUtils.VersionInfo vi = VersionUtils.readVersionInfo(app);
        final String message = "Circuit Construction Kit II\n" + "Build time=" + vi.getBuildTime() + "\nBuild Number=" + vi.getBuildNumber() + "\nby PhET";
        about.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, message, title, JOptionPane.INFORMATION_MESSAGE);
            }
        });
        helpMenu.add(about);
        FileMenu fileMenu = new FileMenu(module, frame);
        JMenu cckMenu = new CCKMenu(module);

        jmb.add(fileMenu, 0);
        jmb.add(cckMenu);
        jmb.add(helpMenu);
        app.startApplication(module);
//        final ThreadClock tc = new ThreadClock(30, 30) {
//            public void start() {
//                super.start();
//            }
//        };


//        TickListener tl = new TickListener() {
//            public void clockTicked(AbstractClock abstractClock) {
//                Thread t = Thread.currentThread();
////                t.setPriority(Thread.MIN_PRIORITY);
//                tc.removeTickListener(this);
//            }
//        };
//        tc.addTickListener(tl);


//        DefaultClock dc = new DefaultClock(stc, new ConstantTimeConverter(30));
//        dc.addTickListener(new TickListener() {
//            public void clockTicked(AbstractClock abstractClock) {
//                module.getModel().clockTicked(null, 30);
//                if (numTicks < 100) {
//                    module.repaint();
//                    numTicks++;
//                }
//            }
//        });
//        dc.start();
        app.getApplicationView().getPhetFrame().setExtendedState(JFrame.MAXIMIZED_BOTH);
        module.getApparatusPanel().repaint();
        enableAspectRatio(app, module);
    }

    private static void enableAspectRatio(PhetApplication app, Module module) {
        ApparatusPanelContainer apc = app.getApplicationView().getApparatusPanelContainer();
        if (apc instanceof SingleApparatusPanelContainer) {
            SingleApparatusPanelContainer sapc = (SingleApparatusPanelContainer) apc;
            sapc.getComponent().setLayout(new AspectRatioLayout(module.getApparatusPanel(), 10, 10, .75));
            app.getApplicationView().getBasicPhetPanel().invalidate();
            app.getApplicationView().getBasicPhetPanel().validate();
            app.getApplicationView().getBasicPhetPanel().repaint();
        }
    }

    public void setLifelikeWireColor(Color color) {
        lifelikeGraphicFactory.setWireColor(color);
        circuitGraphic.setLifelikeWireColor(color);
    }
}