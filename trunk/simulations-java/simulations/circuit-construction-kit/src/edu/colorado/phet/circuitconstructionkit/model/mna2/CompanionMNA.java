package edu.colorado.phet.circuitconstructionkit.model.mna2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CompanionMNA {
    abstract static class CompanionModel {
        ArrayList<MNA.Battery> batteries;
        ArrayList<MNA.Resistor> resistors;
        ArrayList<MNA.CurrentSource> currentSources;

        protected CompanionModel( ArrayList<MNA.Battery> batteries, ArrayList<MNA.Resistor> resistors, ArrayList<MNA.CurrentSource> currentSources ) {
            this.batteries = batteries;
            this.resistors = resistors;
            this.currentSources = currentSources;
        }

        abstract double getCurrent( MNA.Solution solution );

        abstract double getVoltage( MNA.Solution solution );

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) {
                return true;
            }
            if ( o == null || getClass() != o.getClass() ) {
                return false;
            }

            CompanionModel that = (CompanionModel) o;

            if ( !batteries.equals( that.batteries ) ) {
                return false;
            }
            if ( !currentSources.equals( that.currentSources ) ) {
                return false;
            }
            if ( !resistors.equals( that.resistors ) ) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = batteries.hashCode();
            result = 31 * result + resistors.hashCode();
            result = 31 * result + currentSources.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "CompanionModel{" +
                   "batteries=" + batteries +
                   ", resistors=" + resistors +
                   ", currentSources=" + currentSources +
                   '}';
        }
    }

    interface NodeCreator {
        int newNode();
    }

    interface HasCompanionModel {
        CompanionModel getCompanionModel( double dt, NodeCreator newNode );
    }

    static class Capacitor extends MNA.Element implements HasCompanionModel {
        double capacitance;
        double voltage;
        double current;

        Capacitor( int node0, int node1, double capacitance, double voltage, double current ) {
            super( node0, node1 );
            this.capacitance = capacitance;
            this.voltage = voltage;
            this.current = current;
        }

        public CompanionModel getCompanionModel( final double dt, NodeCreator newNode ) {
            //linear companion model for capacitor, using trapezoidal approximation, under thevenin model, see http://dev.hypertriton.com/edacious/trunk/doc/lec.pdf
            //and p.23 pillage
            //our signs differ from Pillage because:
            //at T=0 across an uncharged capacitor, the capacitor should create a simulated voltage that prevents more charge
            //from building up on the capacitor; this means a negative voltage (or a backwards battery)
            int midNode = newNode.newNode();
            ArrayList<MNA.Battery> batteries = new ArrayList<MNA.Battery>();
            batteries.add( new MNA.Battery( node0, midNode, voltage - dt * current / 2 / capacitance ) );
            ArrayList<MNA.Resistor> resistors = new ArrayList<MNA.Resistor>();
            resistors.add( new MNA.Resistor( midNode, node1, dt / 2 / capacitance ) );

//            System.out.println("capacitor companion: mid = " + midNode+", batteries="+batteries+", resistors="+resistors);

            return new CompanionModel( batteries, resistors, new ArrayList<MNA.CurrentSource>() ) {
                double getCurrent( MNA.Solution solution ) {
                    return solution.getCurrent( batteries.get( 0 ) );
                }

                double getVoltage( MNA.Solution solution ) {
                    return voltage - dt / 2 / capacitance * ( current + getCurrent( solution ) );
                }
            };
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) {
                return true;
            }
            if ( o == null || getClass() != o.getClass() ) {
                return false;
            }
            if ( !super.equals( o ) ) {
                return false;
            }

            Capacitor capacitor = (Capacitor) o;

            if ( Double.compare( capacitor.capacitance, capacitance ) != 0 ) {
                return false;
            }
            if ( Double.compare( capacitor.current, current ) != 0 ) {
                return false;
            }
            if ( Double.compare( capacitor.voltage, voltage ) != 0 ) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            long temp;
            temp = capacitance != +0.0d ? Double.doubleToLongBits( capacitance ) : 0L;
            result = 31 * result + (int) ( temp ^ ( temp >>> 32 ) );
            temp = voltage != +0.0d ? Double.doubleToLongBits( voltage ) : 0L;
            result = 31 * result + (int) ( temp ^ ( temp >>> 32 ) );
            temp = current != +0.0d ? Double.doubleToLongBits( current ) : 0L;
            result = 31 * result + (int) ( temp ^ ( temp >>> 32 ) );
            return result;
        }

        @Override
        public String toString() {
            return "Capacitor{" +
                   "capacitance=" + capacitance +
                   ", voltage=" + voltage +
                   ", current=" + current +
                   '}';
        }
    }

    public static class Inductor extends MNA.Element implements HasCompanionModel {
        double inductance;
        double voltage;
        double current;

        Inductor( int node0, int node1, double inductance, double voltage, double current ) {
            super( node0, node1 );
            this.inductance = inductance;
            this.voltage = voltage;
            this.current = current;
        }

        public CompanionModel getCompanionModel( final double dt, NodeCreator newNode ) {
            //Thevenin, Pillage p.23.  Pillage says this is the model used in Spice
            int midNode = newNode.newNode();
            ArrayList<MNA.Battery> batteries = new ArrayList<MNA.Battery>();
            batteries.add( new MNA.Battery( node0, midNode, voltage + 2 * inductance * current / dt ) );
            ArrayList<MNA.Resistor> resistors = new ArrayList<MNA.Resistor>();
            resistors.add( new MNA.Resistor( midNode, node1, 2 * inductance / dt ) );
            return new CompanionModel( batteries, resistors, new ArrayList<MNA.CurrentSource>() ) {
                double getCurrent( MNA.Solution solution ) {
                    return solution.getCurrent( batteries.get( 0 ) );
                }

                double getVoltage( MNA.Solution solution ) {
                    return ( getCurrent( solution ) - current ) * 2 * inductance / dt - voltage;
                }
            };
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) {
                return true;
            }
            if ( o == null || getClass() != o.getClass() ) {
                return false;
            }
            if ( !super.equals( o ) ) {
                return false;
            }

            Inductor inductor = (Inductor) o;

            if ( Double.compare( inductor.current, current ) != 0 ) {
                return false;
            }
            if ( Double.compare( inductor.inductance, inductance ) != 0 ) {
                return false;
            }
            if ( Double.compare( inductor.voltage, voltage ) != 0 ) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            long temp;
            temp = inductance != +0.0d ? Double.doubleToLongBits( inductance ) : 0L;
            result = 31 * result + (int) ( temp ^ ( temp >>> 32 ) );
            temp = voltage != +0.0d ? Double.doubleToLongBits( voltage ) : 0L;
            result = 31 * result + (int) ( temp ^ ( temp >>> 32 ) );
            temp = current != +0.0d ? Double.doubleToLongBits( current ) : 0L;
            result = 31 * result + (int) ( temp ^ ( temp >>> 32 ) );
            return result;
        }

        @Override
        public String toString() {
            return "Inductor{" +
                   "inductance=" + inductance +
                   ", voltage=" + voltage +
                   ", current=" + current +
                   '}';
        }
    }

    //This models a battery with a resistance in series
    public static class ResistiveBattery extends MNA.Element implements HasCompanionModel {
        double voltage;
        double resistance;

        ResistiveBattery( int node0, int node1, double voltage, double resistance ) {
            super( node0, node1 );
            this.voltage = voltage;
            this.resistance = resistance;
        }

        public CompanionModel getCompanionModel( double dt, NodeCreator newNode ) {
            int midNode = newNode.newNode();
            ArrayList<MNA.Battery> batteries = new ArrayList<MNA.Battery>();
            batteries.add( new MNA.Battery( node0, midNode, voltage ) );
            ArrayList<MNA.Resistor> resistors = new ArrayList<MNA.Resistor>();
            resistors.add( new MNA.Resistor( midNode, node1, resistance ) );
            return new CompanionModel( batteries, resistors, new ArrayList<MNA.CurrentSource>() ) {
                double getCurrent( MNA.Solution solution ) {
                    return solution.getCurrent( batteries.get( 0 ) );
                }

                double getVoltage( MNA.Solution solution ) {
                    return solution.getVoltageDifference( node0, node1 );
                }
            };
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) {
                return true;
            }
            if ( o == null || getClass() != o.getClass() ) {
                return false;
            }
            if ( !super.equals( o ) ) {
                return false;
            }

            ResistiveBattery that = (ResistiveBattery) o;

            if ( Double.compare( that.resistance, resistance ) != 0 ) {
                return false;
            }
            if ( Double.compare( that.voltage, voltage ) != 0 ) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            long temp;
            temp = voltage != +0.0d ? Double.doubleToLongBits( voltage ) : 0L;
            result = 31 * result + (int) ( temp ^ ( temp >>> 32 ) );
            temp = resistance != +0.0d ? Double.doubleToLongBits( resistance ) : 0L;
            result = 31 * result + (int) ( temp ^ ( temp >>> 32 ) );
            return result;
        }

        @Override
        public String toString() {
            return "ResistiveBattery{" +
                   "voltage=" + voltage +
                   ", resistance=" + resistance +
                   '}';
        }
    }

    static class InitialCondition {
        double voltage;
        double current;

        InitialCondition( double voltage, double current ) {
            this.voltage = voltage;
            this.current = current;
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) {
                return true;
            }
            if ( o == null || getClass() != o.getClass() ) {
                return false;
            }

            InitialCondition that = (InitialCondition) o;

            if ( Double.compare( that.current, current ) != 0 ) {
                return false;
            }
            if ( Double.compare( that.voltage, voltage ) != 0 ) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            temp = voltage != +0.0d ? Double.doubleToLongBits( voltage ) : 0L;
            result = (int) ( temp ^ ( temp >>> 32 ) );
            temp = current != +0.0d ? Double.doubleToLongBits( current ) : 0L;
            result = 31 * result + (int) ( temp ^ ( temp >>> 32 ) );
            return result;
        }

        @Override
        public String toString() {
            return "InitialCondition{" +
                   "voltage=" + voltage +
                   ", current=" + current +
                   '}';
        }
    }

    public static class FullCircuit extends MNA.AbstractCircuit {
        List<? extends ResistiveBattery> batteries;
        List<? extends MNA.Resistor> resistors;
        List<? extends Capacitor> capacitors;
        List<? extends Inductor> inductors;

        public FullCircuit( List<? extends ResistiveBattery> batteries, List<? extends MNA.Resistor> resistors, List<? extends Capacitor> capacitors, List<? extends Inductor> inductors ) {
            this.batteries = batteries;
            this.resistors = resistors;
            this.capacitors = capacitors;
            this.inductors = inductors;
        }

        FullCircuit stepInTime( double dt ) {
            CompanionSolution solution = solve( dt );
            ArrayList<Capacitor> newcapacitors = new ArrayList<Capacitor>();
            for ( Capacitor c : capacitors ) {
                newcapacitors.add( new Capacitor( c.node0, c.node1, c.capacitance, solution.getVoltage( c ), solution.getCurrent( c ) ) );
            }
            ArrayList<Inductor> newinductors = new ArrayList<Inductor>();
            for ( Inductor i : inductors ) {
                newinductors.add( new Inductor( i.node0, i.node1, i.inductance, solution.getVoltage( i ), solution.getCurrent( i ) ) );
            }
            return new FullCircuit( batteries, resistors, newcapacitors, newinductors );
        }

        FullCircuit getInitializedCircuit() {
            InitialConditionSet initConditions = getInitialConditions();
            ArrayList<Capacitor> newcapacitors = new ArrayList<Capacitor>();
            for ( Capacitor c : capacitors ) {
                newcapacitors.add( new Capacitor( c.node0, c.node1, c.capacitance, initConditions.capacitorMap.get( c ).voltage, initConditions.capacitorMap.get( c ).current ) );
            }
            ArrayList<Inductor> newinductors = new ArrayList<Inductor>();
            for ( Inductor i : inductors ) {
                newinductors.add( new Inductor( i.node0, i.node1, i.inductance, initConditions.inductorMap.get( i ).voltage, initConditions.inductorMap.get( i ).current ) );
            }
            return new FullCircuit( batteries, resistors, newcapacitors, newinductors );
        }

        //Create a circuit that has correct initial voltages and currents for capacitors and inductors
        //This is done by:
        // treating a capacitor as a R=0.0 resistor and computing the current through it
        // treating an inductor as a R=INF resistor and computing the voltage drop across it
        //Todo: finding inital bias currently ignores internal resistance in batteries
        //Todo: Is this computation even used by CCK?  Should it be?
        InitialConditionSet getInitialConditions() {
            ArrayList<MNA.Battery> b = new ArrayList<MNA.Battery>();
            ArrayList<MNA.Resistor> r = new ArrayList<MNA.Resistor>( resistors );
            ArrayList<MNA.CurrentSource> cs = new ArrayList<MNA.CurrentSource>();

            for ( ResistiveBattery batt : batteries ) {
                b.add( new MNA.Battery( batt.node0, batt.node1, batt.voltage ) );//todo: account for internal resistance of battery in initial bias computation
            }
            HashMap<Capacitor, MNA.Resistor> capToRes = new HashMap<Capacitor, MNA.Resistor>();
            for ( Capacitor c : capacitors ) {
                MNA.Resistor resistor = new MNA.Resistor( c.node0, c.node1, 0.0 );
                r.add( resistor );
                capToRes.put( c, resistor );
            }

            HashMap<Inductor, MNA.Resistor> indToRes = new HashMap<Inductor, MNA.Resistor>();
            for ( Inductor i : inductors ) {
                MNA.Resistor resistor = new MNA.Resistor( i.node0, i.node1, 1E14 );
                r.add( resistor );//todo: could make base model handle Infinity properly, via maths or via circuit architecture remapping
                indToRes.put( i, resistor );
            }
            MNA.Circuit circuit = new MNA.Circuit( b, r );
            MNA.Solution solution = circuit.solve();

            HashMap<Capacitor, InitialCondition> capacitorMap = new HashMap<Capacitor, InitialCondition>();
            for ( Capacitor c : capacitors ) {
                capacitorMap.put( c, new InitialCondition( 0, solution.getCurrent( capToRes.get( c ) ) ) );
            }

            HashMap<Inductor, InitialCondition> inductorMap = new HashMap<Inductor, InitialCondition>();
            for ( Inductor i : inductors ) {
                inductorMap.put( i, new InitialCondition( solution.getVoltage( indToRes.get( i ) ), 0 ) );
            }
            return new InitialConditionSet( capacitorMap, inductorMap );
        }

        class InitialConditionSet {
            HashMap<Capacitor, InitialCondition> capacitorMap;
            HashMap<Inductor, InitialCondition> inductorMap;

            InitialConditionSet( HashMap<Capacitor, InitialCondition> capacitorMap, HashMap<Inductor, InitialCondition> inductorMap ) {
                this.capacitorMap = capacitorMap;
                this.inductorMap = inductorMap;
            }

            @Override
            public boolean equals( Object o ) {
                if ( this == o ) {
                    return true;
                }
                if ( o == null || getClass() != o.getClass() ) {
                    return false;
                }

                InitialConditionSet that = (InitialConditionSet) o;

                if ( !capacitorMap.equals( that.capacitorMap ) ) {
                    return false;
                }
                if ( !inductorMap.equals( that.inductorMap ) ) {
                    return false;
                }

                return true;
            }

            @Override
            public int hashCode() {
                int result = capacitorMap.hashCode();
                result = 31 * result + inductorMap.hashCode();
                return result;
            }

            @Override
            public String toString() {
                return "InitialConditionSet{" +
                       "capacitorMap=" + capacitorMap +
                       ", inductorMap=" + inductorMap +
                       '}';
            }
        }

        CompanionCircuit getCompanionModel( double dt ) {
            ArrayList<MNA.Battery> b = new ArrayList<MNA.Battery>();//batteries use companion model since they have optionally have internal resistance

            ArrayList<MNA.Resistor> r = new ArrayList<MNA.Resistor>( resistors );
            ArrayList<MNA.CurrentSource> cs = new ArrayList<MNA.CurrentSource>();

            final ArrayList<Integer> usedIndices = new ArrayList<Integer>();

            HashMap<HasCompanionModel, CompanionModel> companionMap = new HashMap<HasCompanionModel, CompanionModel>();
            ArrayList<HasCompanionModel> sourceElements = new ArrayList<HasCompanionModel>();
            sourceElements.addAll( capacitors );
            sourceElements.addAll( inductors );
            sourceElements.addAll( batteries );

            for ( HasCompanionModel c : sourceElements ) {
                CompanionModel cm = c.getCompanionModel( dt, new NodeCreator() {
                    public int newNode() {
                        return getFreshIndex( usedIndices );
                    }
                } );
                companionMap.put( c, cm );
                for ( MNA.Battery battery : cm.batteries ) {
                    b.add( battery );
                }
                for ( MNA.Resistor resistor : cm.resistors ) {
                    r.add( resistor );
                }
                for ( MNA.CurrentSource currentSource : cm.currentSources ) {
                    cs.add( currentSource );
                }
            }

            return new CompanionCircuit( new MNA.Circuit( b, r, cs ), companionMap );
        }

        //Find the first node index that is unused in the node set or used indices, and update the used indices
        int getFreshIndex( ArrayList<Integer> usedIndices ) {
            int selected = -1;
            int testIndex = 0;
            while ( selected == -1 ) {
                if ( !getNodeSet().contains( testIndex ) && !usedIndices.contains( testIndex ) ) {
                    selected = testIndex;
                }
                testIndex = testIndex + 1;
            }
            usedIndices.add( selected );
            return selected;
        }

        ArrayList<MNA.Element> getElements() {
            ArrayList<MNA.Element> elements = new ArrayList<MNA.Element>();
            elements.addAll( batteries );
            elements.addAll( resistors );
            elements.addAll( capacitors );
            elements.addAll( inductors );
            return elements;
        }

        CompanionSolution solve( double dt ) {
            CompanionCircuit companionModel = getCompanionModel( dt );
            MNA.Solution solution = companionModel.circuit.solve();
            return new CompanionSolution( this, companionModel, solution );
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) {
                return true;
            }
            if ( o == null || getClass() != o.getClass() ) {
                return false;
            }

            FullCircuit that = (FullCircuit) o;

            if ( !batteries.equals( that.batteries ) ) {
                return false;
            }
            if ( !capacitors.equals( that.capacitors ) ) {
                return false;
            }
            if ( !inductors.equals( that.inductors ) ) {
                return false;
            }
            if ( !resistors.equals( that.resistors ) ) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = batteries.hashCode();
            result = 31 * result + resistors.hashCode();
            result = 31 * result + capacitors.hashCode();
            result = 31 * result + inductors.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "FullCircuit{" +
                   "batteries=" + batteries +
                   ", resistors=" + resistors +
                   ", capacitors=" + capacitors +
                   ", inductors=" + inductors +
                   '}';
        }
    }

    static class CompanionCircuit {
        MNA.Circuit circuit;
        HashMap<HasCompanionModel, CompanionModel> elementMap;

        CompanionCircuit( MNA.Circuit circuit, HashMap<HasCompanionModel, CompanionModel> elementMap ) {
            this.circuit = circuit;
            this.elementMap = elementMap;
        }

        double getCurrent( HasCompanionModel c, MNA.Solution solution ) {
            return elementMap.get( c ).getCurrent( solution );
        }

        double getVoltage( HasCompanionModel c, MNA.Solution solution ) {
            return elementMap.get( c ).getVoltage( solution );
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) {
                return true;
            }
            if ( o == null || getClass() != o.getClass() ) {
                return false;
            }

            CompanionCircuit that = (CompanionCircuit) o;

            if ( !circuit.equals( that.circuit ) ) {
                return false;
            }
            if ( !elementMap.equals( that.elementMap ) ) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = circuit.hashCode();
            result = 31 * result + elementMap.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "CompanionCircuit{" +
                   "circuit=" + circuit +
                   ", elementMap=" + elementMap +
                   '}';
        }
    }

    public static class CompanionSolution extends MNA.ISolution {
        FullCircuit fullCircuit;
        CompanionCircuit companionModel;
        MNA.Solution solution;

        CompanionSolution( FullCircuit fullCircuit, CompanionCircuit companionModel, MNA.Solution solution ) {
            this.fullCircuit = fullCircuit;
            this.companionModel = companionModel;
            this.solution = solution;
        }

        double getNodeVoltage( int node ) {
            return solution.getNodeVoltage( node );
        }

        double getVoltage( MNA.Element e ) {
            if ( e instanceof HasCompanionModel ) {
                return companionModel.getVoltage( (HasCompanionModel) e, solution );
            }
            else {
                return solution.getVoltage( e );
            }
        }

        double getCurrent( MNA.Element e ) {
            if ( e instanceof HasCompanionModel ) {
                return companionModel.getCurrent( (HasCompanionModel) e, solution );
            }
            else {
                return solution.getCurrent( e );
            }
        }
    }
}