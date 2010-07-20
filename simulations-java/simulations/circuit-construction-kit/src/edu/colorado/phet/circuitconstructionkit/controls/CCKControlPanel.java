package edu.colorado.phet.circuitconstructionkit.controls;

import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.CCKResources;
import edu.colorado.phet.circuitconstructionkit.CCKStrings;
import edu.colorado.phet.circuitconstructionkit.model.Circuit;
import edu.colorado.phet.circuitconstructionkit.model.Junction;
import edu.colorado.phet.circuitconstructionkit.model.analysis.KirkhoffSolver;
import edu.colorado.phet.circuitconstructionkit.persistence.CircuitXML;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.BranchNodeFactory;
import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.servicemanager.InputStreamFileContents;
import edu.colorado.phet.common.phetcommon.servicemanager.PhetServiceManager;
import edu.colorado.phet.common.phetcommon.view.HelpPanel;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.common.phetcommon.view.util.PhetOptionPane;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import net.n3.nanoxml.*;

import javax.jnlp.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * User: Sam Reid
 * Date: Jun 1, 2004
 * Time: 11:03:06 AM
 */
public class CCKControlPanel extends edu.colorado.phet.common.phetcommon.view.ControlPanel {
    private CCKModule module;
    private JCheckBox seriesAmmeter;
    private AdvancedControlPanel advancedControlPanel;
    private JPanel advancedPanel;
    private boolean debugging = false;

    public CCKControlPanel(final CCKModule module, Module m) {
        advancedControlPanel = new AdvancedControlPanel(module);
        advancedControlPanel.setBorder(null);
//        JLabel logoLabel = new JLabel( new ImageIcon( PhetCommonResources.getInstance().getImage( "logos/phet-logo-120x50.jpg" ) ) );
//        logoLabel.setToolTipText( CCKResources.getString( "CCK3ControlPanel.PhETToolTip" ) );
//        logoLabel.setBorder( BorderFactory.createRaisedBevelBorder() );
//        logoLabel.setBorder( BorderFactory.createLineBorder( Color.black, 2 ) );
//        logoLabel.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
//        logoLabel.addMouseListener( new MouseAdapter() {
//            public void mouseReleased( MouseEvent e ) {
//                showPhetPage();
//            }
//        } );
        this.module = module;
        JPanel filePanel = getFilePanel();
        if (useAdvanced()) {
            advancedPanel = new AdvancedControlPanel(module);
        }

        JPanel visualPanel = makeVisualPanel();
        JPanel toolPanel = null;
        try {
            toolPanel = makeToolPanel();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        JPanel sizePanel = new SizeControlPanel(module);

        JButton jb = new JButton(CCKResources.getString("CCK3ControlPanel.LocalHelpButton"));
        jb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showHelpImage();
            }
        });
        JButton browserGIF = new JButton(CCKResources.getString("CCK3ControlPanel.GIFHelpButton"));
        browserGIF.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showHelpGIF();
            }
        });

        JButton xml = new JButton(CCKResources.getString("CCK3ControlPanel.ShowXMLButton"));
        xml.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CircuitXML.toXML(module.getCircuit());
            }
        });

        JButton changeBunch = new JButton(CCKResources.getString("CCK3ControlPanel.ChangeViewButton"));
        changeBunch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int IMAX = 200;
                for (int i = 0; i < IMAX; i++) {
                    System.out.println("i = " + i + "/" + IMAX);
                    module.setLifelike(!module.isLifelike());
                }
            }
        });

//        JPanel titlePanel = new JPanel();
//        titlePanel.add( logoLabel );
//        add( titlePanel );
        add(filePanel);
        if (module.getParameters().isUseVisualControlPanel()) {
            add(visualPanel);
        }
        add(toolPanel);
        add(sizePanel);

        JButton testLifelikeSchematic = new JButton("Test Lifelike/Schematic");
        testLifelikeSchematic.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < 100; i++) {
                    module.setLifelike(!module.isLifelike());
                }
            }
        });
        if (debugging) {
            add(testLifelikeSchematic);
        }

        if (useAdvanced()) {
            add(advancedPanel);
        }

        if (module.getParameters().showGrabBag()) {
            addGrabBag();
        }

        if (module.getParameters().getAllowDynamics()) {
            addControl(new ResetDynamicsButton(module));
        }
        addControl(Box.createVerticalStrut(5));
        addResetAllButton(new Resettable() {
            public void reset() {
                module.clear();
            }
        });
        super.addControlFullWidth(new HelpPanel(m));
    }

    private void add(JComponent component) {
        addControlFullWidth(component);
    }

    private boolean useAdvanced() {
        return module.getParameters().getUseAdvancedControlPanel();
    }

    private void addGrabBag() {
        module.addGrabBag();
    }

    private JPanel makeToolPanel() throws IOException {
        JPanel toolPanel = new JPanel();
        toolPanel.setLayout(new GridBagLayout());
        GridBagConstraints lhs = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
        GridBagConstraints rhs = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);

        ImageIcon voltIcon = new ImageIcon(ImageLoader.loadBufferedImage("circuit-construction-kit/images/dvm-thumb.gif"));
        ImageIcon nonContactAmmIcon = new ImageIcon(ImageLoader.loadBufferedImage("circuit-construction-kit/images/va-thumb.gif"));
        ImageIcon ammIcon = new ImageIcon(ImageLoader.loadBufferedImage("circuit-construction-kit/images/ammeter60.gif"));

        final JCheckBox voltmeter = new JCheckBox(CCKResources.getString("CCK3ControlPanel.VoltmeterCheckBox"), false);
        voltmeter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                module.setVoltmeterVisible(voltmeter.isSelected());

            }
        });

        final JCheckBox virtualAmmeter = new JCheckBox(CCKResources.getString("CCK3ControlPanel.NonContactAmmeterCheckBox"), false);
        virtualAmmeter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                module.setVirtualAmmeterVisible(virtualAmmeter.isSelected());
            }
        });
        seriesAmmeter = new JCheckBox(CCKResources.getString("CCK3ControlPanel.AmmeterCheckBox"), false);
        seriesAmmeter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                module.setSeriesAmmeterVisible(seriesAmmeter.isSelected());
            }
        });

        toolPanel.add(voltmeter, rhs);
        rhs.gridy++;

        toolPanel.add(seriesAmmeter, rhs);
        rhs.gridy++;

        if (module.getParameters().useNonContactAmmeter()) {
            toolPanel.add(virtualAmmeter, rhs);
            rhs.gridy++;
        }
        lhs.gridy = 0;
        toolPanel.add(new JLabel(voltIcon), lhs);
        lhs.gridy++;
        toolPanel.add(new JLabel(ammIcon), lhs);
        lhs.gridy++;
        if (module.getParameters().useNonContactAmmeter()) {
            toolPanel.add(new JLabel(nonContactAmmIcon), lhs);
            lhs.gridy++;
        }

        if (module.getParameters().getAllowDynamics()) {
            try {
                ImageIcon timerIcon = new ImageIcon(ImageLoader.loadBufferedImage("circuit-construction-kit/images/stopwatch-thumb.png"));
                final JCheckBox timerButton = new JCheckBox(CCKStrings.getString("stopwatch"), module.isStopwatchVisible());
                timerButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        module.setStopwatchVisible(timerButton.isSelected());
                    }
                });
                toolPanel.add(new JLabel(timerIcon), lhs);
                toolPanel.add(timerButton, rhs);
                lhs.gridy++;
                rhs.gridy++;
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            ImageIcon chartIcon = new ImageIcon(ImageLoader.loadBufferedImage("circuit-construction-kit/images/detector-thumb.gif"));
            toolPanel.add(new JLabel(chartIcon), lhs);
            JButton floatingChartButton = new JButton(CCKStrings.getString("add.current.chart"));
            floatingChartButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    module.addCurrentChart();
                }
            });
            toolPanel.add(floatingChartButton, rhs);
            rhs.gridy++;
            lhs.gridy++;

            ImageIcon voltageIcon = new ImageIcon(ImageLoader.loadBufferedImage("circuit-construction-kit/images/detector-thumb.gif"));
            toolPanel.add(new JLabel(chartIcon), lhs);
            JButton voltageChartButton = new JButton(CCKStrings.getString("add.voltage"));
            voltageChartButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    module.addVoltageChart();
                }
            });
            toolPanel.add(voltageChartButton, rhs);
        }
        toolPanel.setBorder(new CCKTitledBorder(CCKResources.getString("CCK3ControlPanel.ToolsPanelBorder")));
        return toolPanel;
    }

    private JPanel makeVisualPanel() {
        final JRadioButton lifelike = new JRadioButton(CCKResources.getString("CCK3ControlPanel.LIfelikeRadioButton"), module.isLifelike());
        final JRadioButton schematic = new JRadioButton(CCKResources.getString("CCK3ControlPanel.SchematicRadioButton"), !module.isLifelike());

        module.addBranchNodeFactoryListener(new BranchNodeFactory.Listener(){
            public void displayStyleChanged() {
                lifelike.setSelected(module.isLifelike());
            schematic.setSelected(!module.isLifelike());
            }
        });

        lifelike.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                module.setLifelike(true);
            }
        });
        schematic.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                module.setLifelike(false);
            }
        });

        JPanel visualizationPanel = new VerticalLayoutPanel();

        JPanel radioButtons = new JPanel();

        radioButtons.add(lifelike);
        if (module.getParameters().allowSchematicMode()) {
            radioButtons.add(schematic);
        }
        visualizationPanel.add(radioButtons);
        if (module.getParameters().allowShowReadouts()) {
            visualizationPanel.add(new ShowReadoutPanel(module));
        }
        visualizationPanel.setBorder(new CCKTitledBorder(CCKResources.getString("CCK3ControlPanel.VisualPanelBorder")));
        return visualizationPanel;
    }

    private void load() throws IOException, XMLException {
        FileOpenService fos = null;
        try {
            fos = PhetServiceManager.getFileOpenService(module.getSimulationPanel());
        }
        catch (UnavailableServiceException e) {
            e.printStackTrace();
        }
        FileContents open = fos.openFileDialog(null, null);
        if (open == null) {
            return;
        }
        InputStreamReader isr = new InputStreamReader(open.getInputStream());
        BufferedReader br = new BufferedReader(isr);
        String str = "";
        while (br.ready()) {
            String read = br.readLine();
            System.out.println("read = " + read);
            str += read;
        }
        IXMLParser parser = new StdXMLParser();
        parser.setReader(new StdXMLReader(new StringReader(patchString(str))));
        parser.setBuilder(new StdXMLBuilder());
        parser.setValidator(new NonValidator());

        IXMLElement parsed = (IXMLElement) parser.parse();
        Circuit circuit = CircuitXML.parseXML(parsed, module.getCircuitChangeListener(), module);
        if (isOldVersionCCK(str)) {
            flipY(circuit);
            PhetOptionPane.showMessageDialog(this, "<html>The file you loaded is from an earlier version of this program, <br>and some " +
                    "parts of the circuit may be oriented incorrectly.  <br><br>Manually correct any problems, and be sure to save the new circuit.</html>");
        }
        module.setCircuit(circuit);
    }

    private boolean isOldVersionCCK(String str) {
        return str.indexOf("edu.colorado.phet.cck3.circuit.Branch") >= 0 || str.indexOf("edu.colorado.phet.cck3.circuit.components.") >= 0;
    }

    private void flipY(Circuit circuit) {
        for (int i = 0; i < circuit.numJunctions(); i++) {
            Junction j = circuit.junctionAt(i);
            double y = j.getY();
            double offsetFrom5 = y - 5;
            double flipped = -offsetFrom5;
            double newY = 5 + flipped;
//            System.out.println( "y = " + y + ", newY=" + newY );
            j.setPosition(j.getX(), newY);
        }
//        for( int i = 0; i < circuit.numBranches(); i++ ) {
//            if( !( circuit.branchAt( i ) instanceof Battery ) ) {
//                Junction a = circuit.branchAt( i ).getStartJunction();
//                Junction b = circuit.branchAt( i ).getEndJunction();
//                Junction c = new Junction( a.getX() + 1, b.getY() );
//                Junction d = new Junction( a.getX(), b.getY() + 1 );
//                circuit.branchAt( i ).setStartJunction( c );
//                circuit.branchAt( i ).setEndJunction( d );
//                circuit.branchAt( i ).setStartJunction( b );
//                circuit.branchAt( i ).setEndJunction( a );
//            }
//            if( circuit.branchAt( i ) instanceof Bulb ) {
//                Bulb bulb = (Bulb)circuit.branchAt( i );
//                bulb.flip( circuit );
//            }
//        }
    }

    private String patchString(String str) {
        str = str.replaceAll("edu.colorado.phet.cck3.circuit.Branch", "edu.colorado.phet.cck.model.components.Wire");
        str = str.replaceAll("edu.colorado.phet.cck3.circuit.components.", "edu.colorado.phet.cck.model.components.");
        str = str.replaceAll("edu.colorado.phet.cck3.", "edu.colorado.phet.cck.");//grab bag
        return str;
    }

    private void save() throws IOException {
        FileSaveService fos = null;
        try {
            fos = PhetServiceManager.getFileSaveService(module.getSimulationPanel());
        }
        catch (UnavailableServiceException e) {
            e.printStackTrace();
        }

        XMLElement xml = CircuitXML.toXML(module.getCircuit());
        StringWriter sw = new StringWriter();
        XMLWriter writer = new XMLWriter(sw);
        writer.write(xml);
        String circuitxml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + sw.toString();
        InputStream stream = new ByteArrayInputStream(circuitxml.getBytes());
        FileContents data = new InputStreamFileContents("circuitxml", stream);
        FileContents out = fos.saveFileDialog(null, null, data.getInputStream(), null);
        if (out != null){
            System.out.println("Saved to "+out.getName()+" as: " + out);
        }
    }

    public void showHelpGIF() {
        try {
            BasicService bs = PhetServiceManager.getBasicService();
            URL url = new URL(CCKResources.getString("CCK3ControlPanel.CCKHelpGifURL"));
            System.out.println("url = " + url);
            bs.showDocument(url);
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (UnavailableServiceException e) {
            e.printStackTrace();
        }
    }

    public void showHelpImage() {
        final JFrame imageFrame = new JFrame();
        try {
            BufferedImage image = ImageLoader.loadBufferedImage("circuit-construction-kit/images/cck-help.gif");

            JLabel label = new JLabel(new ImageIcon(image));
            imageFrame.setContentPane(label);
            imageFrame.pack();
            SwingUtils.centerWindowOnScreen(imageFrame);
            imageFrame.setVisible(true);
            imageFrame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    imageFrame.dispose();
                }
            });
            imageFrame.setResizable(false);
        }
        catch (IOException e) {
            e.printStackTrace();
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            PhetOptionPane.showMessageDialog(module.getSimulationPanel(), sw.getBuffer().toString(),
                    CCKResources.getString("CCK3ControlPanel.ErrorLoadingHelpDialog"));
        }
    }

    private static void printEm(CCKModule module) {
        KirkhoffSolver ks = new KirkhoffSolver();
        Circuit circuit = module.getCircuit();
        KirkhoffSolver.MatrixTable mt = new KirkhoffSolver.MatrixTable(circuit);
        System.out.println("mt = " + mt);

        KirkhoffSolver.Equation[] junctionEquations = ks.getJunctionEquations(circuit, mt);
        KirkhoffSolver.Equation[] loopEquations = ks.getLoopEquations(circuit, mt);
        KirkhoffSolver.Equation[] ohmsLaws = ks.getOhmsLaw(circuit, mt);

        String je = mt.describe(junctionEquations, CCKResources.getString("CCK3ControlPanel.JunctionEquations"));
        String le = mt.describe(loopEquations, CCKResources.getString("CCK3ControlPanel.LoopEquations"));
        String oh = mt.describe(ohmsLaws, CCKResources.getString("CCK3ControlPanel.OhmsLawEquations"));
        System.out.println(je);
        System.out.println(le);
        System.out.println(oh);

        JFrame readoutFrame = new JFrame(CCKResources.getString("CCK3ControlPanel.CircuitEquationsFrame"));
        String plainText = je + "\n" + le + "\n" + oh + "\n";
        JTextArea jta = new JTextArea(plainText) {
            protected void paintComponent(Graphics g) {
                ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                super.paintComponent(g);
            }
        };
        jta.setEditable(false);

        jta.setFont(new Font("Courier New", Font.BOLD, 18));

        readoutFrame.setContentPane(new JScrollPane(jta));
        readoutFrame.pack();
        SwingUtils.centerWindowOnScreen(readoutFrame);
        readoutFrame.setVisible(true);
    }


    private JPanel getFilePanel() {
        final JButton save = new JButton(CCKResources.getString("CCK3ControlPanel.SaveButton"));
        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    save();
                }
                catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        JButton load = new JButton(CCKResources.getString("CCK3ControlPanel.LoadButton"));
        load.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    load();
                }
                catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });

        JPanel filePanel = new JPanel();

        filePanel.add(save);
        filePanel.add(load);
        filePanel.setBorder(new CCKTitledBorder(CCKResources.getString("CCK3ControlPanel.FilePanelBorder")));
        return filePanel;
    }

    public static class CCKTitledBorder extends TitledBorder {
        public CCKTitledBorder(String title) {
            super(BorderFactory.createRaisedBevelBorder(), title);
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            super.paintBorder(c, g, x, y, width, height);
        }
    }

    public boolean isSeriesAmmeterSelected() {
        return seriesAmmeter.isSelected();
    }

    static class AdvancedControlPanel extends AdvancedPanel {
        private CCKModule module;
        private JDialog dialog;
        private ResistivitySlider resistivitySlider;
        private JCheckBox hideElectrons;

        public AdvancedControlPanel(final CCKModule module) {
            super(CCKResources.getString("CCK3ControlPanel.Enable"), CCKResources.getString("CCK3ControlPanel.Disable"));
            this.module = module;
            resistivitySlider = new ResistivitySlider();

            addControl(resistivitySlider);
            resistivitySlider.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    double value = resistivitySlider.getValue();
                    if (value <= 0) {
                        new RuntimeException("Illegal resistivity: " + value).printStackTrace();
                    }
                    module.getResistivityManager().setResistivity(value);
                }
            });

            hideElectrons = new JCheckBox(CCKResources.getString("CCK3ControlPanel.HideElectronsCheckBox"),
                    !module.isElectronsVisible());
            hideElectrons.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    module.setElectronsVisible(!hideElectrons.isSelected());
                }
            });
            addControl(hideElectrons);
            JButton close = new JButton(CCKResources.getString("CCK3ControlPanel.CloseButton"));
            close.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dialog.setVisible(false);
                }
            });
            setBorder(new CCKTitledBorder(CCKResources.getString("CCK3ControlPanel.AdvancedPanelBorder")));
        }

        /* Shows the advanced controls in a dialog. */

        public void showDialog() {
            if (dialog == null) {
                Window parent = SwingUtilities.getWindowAncestor(module.getSimulationPanel());
                dialog = new JDialog((Frame) parent, CCKResources.getString("CCK3ControlPanel.AdvancedControlsDialog"));
                dialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
                dialog.setModal(false);
                dialog.setContentPane(this);
                SwingUtilities.updateComponentTreeUI(dialog);
                dialog.pack();
                SwingUtils.centerDialogInParent(dialog);
            }
            dialog.setVisible(true);
        }
    }

    private void showPhetPage() {

        try {
            BasicService bs = PhetServiceManager.getBasicService();
            URL url = new URL(CCKResources.getString("CCK3ControlPanel.PhETURL"));
            bs.showDocument(url);
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (UnavailableServiceException e) {
            e.printStackTrace();
        }
    }

}