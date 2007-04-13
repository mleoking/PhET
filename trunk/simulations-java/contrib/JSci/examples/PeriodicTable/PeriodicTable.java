import java.awt.*;
import java.awt.event.*;
import JSci.chemistry.*;
import JSci.chemistry.periodictable.*;

/**
* Periodic Table.
* This will run under the J2ME Personal Profile.
* @author Mark Hale
* @version 1.4
*/
public final class PeriodicTable extends Frame {
        private static final Color purple = new Color(0.75f,0.75f,1.0f);
        private static final boolean showNumbering = true;

        public static void main(String argv[]) {
                new PeriodicTable();
        }
        private static void setDefaultSize(Component c, int defaultWidth, int defaultHeight) {
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                final int width = (defaultWidth < screenSize.width) ? defaultWidth : screenSize.width;
                final int height = (defaultHeight < screenSize.height) ? defaultHeight : screenSize.height;
                c.setSize(width, height);
        }
        public PeriodicTable() {
                super("Periodic Table");
                addWindowListener(new WindowAdapter() {
                        public void windowClosing(WindowEvent evt) {
                                dispose();
                                System.exit(0);
                        }
                });
                System.out.print("Loading elements...");
                Panel panel = new Panel();
                createTable(panel, this);
                System.out.println("done.");
                ScrollPane scrollPane = new ScrollPane();
                scrollPane.add(panel);
                add(scrollPane);
                setDefaultSize(this, 650, 400);
                setVisible(true);
        }
        private static void createTable(Container container, Frame frame) {
                GridBagLayout gb = new GridBagLayout();
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.fill=GridBagConstraints.BOTH;
                container.setLayout(gb);
                gbc.gridy=0;
                if(showNumbering) {
                        gbc.gridx=1;
                        container.add(createLabel("1", gb, gbc));
                        gbc.gridx++;
                        container.add(createLabel("2", gb, gbc));
                        gbc.gridx++;
                        container.add(createLabel("3", gb, gbc));
                        gbc.gridx++;
                        container.add(createLabel("4", gb, gbc));
                        gbc.gridx++;
                        container.add(createLabel("5", gb, gbc));
                        gbc.gridx++;
                        container.add(createLabel("6", gb, gbc));
                        gbc.gridx++;
                        container.add(createLabel("7", gb, gbc));
                        gbc.gridx++;
                        container.add(createLabel("8", gb, gbc));
                        gbc.gridx++;
                        container.add(createLabel("9", gb, gbc));
                        gbc.gridx++;
                        container.add(createLabel("10", gb, gbc));
                        gbc.gridx++;
                        container.add(createLabel("11", gb, gbc));
                        gbc.gridx++;
                        container.add(createLabel("12", gb, gbc));
                        gbc.gridx++;
                        container.add(createLabel("13", gb, gbc));
                        gbc.gridx++;
                        container.add(createLabel("14", gb, gbc));
                        gbc.gridx++;
                        container.add(createLabel("15", gb, gbc));
                        gbc.gridx++;
                        container.add(createLabel("16", gb, gbc));
                        gbc.gridx++;
                        container.add(createLabel("17", gb, gbc));
                        gbc.gridx++;
                        container.add(createLabel("18", gb, gbc));
                        gbc.gridy++;
                }
                gbc.gridx=0;
                if(showNumbering) {
                        container.add(createLabel("1", gb, gbc));
                        gbc.gridx++;
                }
                container.add(createButton("Hydrogen", gb, gbc, frame));
                gbc.gridx+=17;
                container.add(createButton("Helium", gb, gbc, frame));
                gbc.gridx=0;
                gbc.gridy++;
                if(showNumbering) {
                        container.add(createLabel("2", gb, gbc));
                        gbc.gridx++;
                }
                container.add(createButton("Lithium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Beryllium", gb, gbc, frame));
                gbc.gridx+=11;
                container.add(createButton("Boron", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Carbon", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Nitrogen", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Oxygen", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Fluorine", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Neon", gb, gbc, frame));
                gbc.gridx=0;
                gbc.gridy++;
                if(showNumbering) {
                        container.add(createLabel("3", gb, gbc));
                        gbc.gridx++;
                }
                container.add(createButton("Sodium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Magnesium", gb, gbc, frame));
                gbc.gridx+=11;
                container.add(createButton("Aluminium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Silicon", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Phosphorus", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Sulphur", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Chlorine", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Argon", gb, gbc, frame));
                gbc.gridx=0;
                gbc.gridy++;
                if(showNumbering) {
                        container.add(createLabel("4", gb, gbc));
                        gbc.gridx++;
                }
                container.add(createButton("Potassium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Calcium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Scandium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Titanium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Vanadium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Chromium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Manganese", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Iron", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Cobalt", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Nickel", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Copper", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Zinc", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Gallium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Germanium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Arsenic", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Selenium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Bromine", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Krypton", gb, gbc, frame));
                gbc.gridx=0;
                gbc.gridy++;
                if(showNumbering) {
                        container.add(createLabel("5", gb, gbc));
                        gbc.gridx++;
                }
                container.add(createButton("Rubidium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Strontium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Yttrium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Zirconium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Niobium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Molybdenum", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Technetium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Ruthenium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Rhodium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Palladium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Silver", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Cadmium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Indium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Tin", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Antimony", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Tellurium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Iodine", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Xenon", gb, gbc, frame));
                gbc.gridx=0;
                gbc.gridy++;
                if(showNumbering) {
                        container.add(createLabel("6", gb, gbc));
                        gbc.gridx++;
                }
                container.add(createButton("Caesium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Barium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Lanthanum", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Hafnium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Tantalum", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Tungsten", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Rhenium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Osmium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Iridium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Platinum", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Gold", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Mercury", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Thallium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Lead", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Bismuth", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Polonium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Astatine", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Radon", gb, gbc, frame));
                gbc.gridx=0;
                gbc.gridy++;
                if(showNumbering) {
                        container.add(createLabel("7", gb, gbc));
                        gbc.gridx++;
                }
                container.add(createButton("Francium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Radium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Actinium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Unnilquadium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Unnilpentium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Unnilhexium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Unnilseptium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Unniloctium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Unnilennium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Ununnilium", gb, gbc, frame));
                if(showNumbering) {
                        gbc.gridx=0;
                        gbc.gridy++;
                        Label blankline=new Label();
                        gb.setConstraints(blankline, gbc);
                        container.add(blankline);
                }
                gbc.gridx=0;
                gbc.gridy++;
                if(showNumbering) {
                        container.add(createLabel("6", gb, gbc));
                        gbc.gridx++;
                }
                gbc.gridx+=3;
                container.add(createButton("Cerium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Praseodymium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Neodymium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Promethium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Samarium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Europium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Gadolinium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Terbium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Dysprosium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Holmium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Erbium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Thulium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Ytterbium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Lutetium", gb, gbc, frame));
                gbc.gridx=0;
                gbc.gridy++;
                if(showNumbering) {
                        container.add(createLabel("7", gb, gbc));
                        gbc.gridx++;
                }
                gbc.gridx+=3;
                container.add(createButton("Thorium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Protactinium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Uranium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Neptunium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Plutonium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Americium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Curium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Berkelium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Californium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Einsteinium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Fermium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Mendelevium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Nobelium", gb, gbc, frame));
                gbc.gridx++;
                container.add(createButton("Lawrencium", gb, gbc, frame));
        }
        private static Label createLabel(String text, GridBagLayout gb, GridBagConstraints gbc) {
                Label label = new Label(text, Label.CENTER);
                gb.setConstraints(label, gbc);
                return label;
        }
        private static Button createButton(String elemName, GridBagLayout gb, GridBagConstraints gbc, Frame frame) {
                Element elem = JSci.chemistry.PeriodicTable.getElement(elemName);
                if(elem == null)
                        return null;
                Button but=new Button(elem.toString());
                if(elem instanceof AlkaliMetal)
                        but.setBackground(Color.cyan);
                else if(elem instanceof AlkaliEarthMetal)
                        but.setBackground(purple);
                else if(elem instanceof NonMetal)
                        but.setBackground(Color.green);
                else if(elem instanceof TransitionMetal)
                        but.setBackground(Color.orange);
                else if(elem instanceof RareEarthMetal)
                        but.setBackground(Color.red);
                else if(elem instanceof Halogen)
                        but.setBackground(Color.yellow);
                else if(elem instanceof NobleGas)
                        but.setBackground(Color.pink);
                but.addActionListener(new ButtonAdapter(frame, elem));
                gb.setConstraints(but, gbc);
                return but;
        }
        static class ButtonAdapter implements ActionListener {
                private final Frame owner;
                private final Element element;
                public ButtonAdapter(Frame f, Element e) {
                        owner = f;
                        element = e;
                }
                public void actionPerformed(ActionEvent evt) {
                        new InfoDialog(owner, element);
                }
        }
        static class InfoDialog extends Dialog {
                private void displayNumber(double x) {
                        if(x==x)
                                add(new Label(String.valueOf(x)));
                        else /* NaN */
                                add(new Label("Unknown"));
                }
                private void displayNumber(double x,String units) {
                        if(x==x)
                                add(new Label(String.valueOf(x)+' '+units));
                        else /* NaN */
                                add(new Label("Unknown"));
                }
                public InfoDialog(Frame parent, Element e) {
                        super(parent,e.getName());
                        addWindowListener(new WindowAdapter() {
                                public void windowClosing(WindowEvent evt) {
                                        dispose();
                                }
                        });
                        setLayout(new GridLayout(11,2));
                        add(new Label("Atomic Number"));
                        add(new Label(String.valueOf(e.getAtomicNumber())));
                        add(new Label("Mass Number"));
                        add(new Label(String.valueOf(e.getMassNumber())));
                        add(new Label("Density"));
                        displayNumber(e.getDensity(), "g/cm^3");
                        add(new Label("Boiling Point"));
                        displayNumber(e.getBoilingPoint(), "K");
                        add(new Label("Melting Point"));
                        displayNumber(e.getMeltingPoint(), "K");
                        add(new Label("Atomic Radius"));
                        displayNumber(e.getAtomicRadius());
                        add(new Label("Covalent Radius"));
                        displayNumber(e.getCovalentRadius());
                        add(new Label("Electronegativity"));
                        displayNumber(e.getElectronegativity());
                        add(new Label("Specific Heat"));
                        displayNumber(e.getSpecificHeat());
                        add(new Label("Electrical Conductivity"));
                        displayNumber(e.getElectricalConductivity());
                        add(new Label("Thermal Conductivity"));
                        displayNumber(e.getThermalConductivity());
                        setDefaultSize(this, 250, 250);
                        setVisible(true);
                }
        }
}
