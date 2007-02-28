//  HEPsimulator
//  High Energy Particle Physics Simulator
//
//  Written by Mark Hale (mark.hale@physics.org)

import java.awt.*;
import java.awt.event.*;
import JSci.physics.relativity.*;
import JSci.physics.quantum.*;
import JSci.physics.particles.*;

/**
* HEPsimulator.
* This is the main application class.
* @version 1.3
* @author Mark Hale (mark.hale@physics.org)
*/
public final class HEPsimulator extends Frame implements Runnable {
        private final Runnable thr=this;
        private MenuBar mBar=new MenuBar();
        private Menu mExp=new Menu("Experiment");
        private Menu mRes=new Menu("Results");
        private MenuItem mDev=new MenuItem("Device");
        private MenuItem mSet=new MenuItem("Setup");
        private MenuItem mCalc=new MenuItem("Calculate");
        private DeviceDialog dlgDevice;
        private SetupDialog dlgSetup;
        /**
        * The Particle Database.
        */
        private QuantumParticle particles[]=new QuantumParticle[38];

        private QuantumParticle ingoing[]=new QuantumParticle[2];
        private QuantumParticle outgoing[]=new QuantumParticle[2];
        private CMframe uni;
        /**
        * Constructor.
        */
        public HEPsimulator() {
                super("HEP simulator");
                addWindowListener(new WindowAdapter() {
                        public void windowClosing(WindowEvent evt) {
                                dlgDevice.dispose();
                                dlgSetup.dispose();
                                dispose();
                                System.exit(0);
                        }
                });
                mDev.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                                dlgDevice.show();
                        }
                });
                mSet.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                                dlgSetup.show();
                        }
                });
                mCalc.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                                new Thread(thr).start();
                        }
                });
                setSize(400,300);
                mBar.add(mExp);
                mExp.add(mDev);
                mExp.add(mSet);
                mBar.add(mRes);
                mRes.add(mCalc);
                setMenuBar(mBar);
                loadDatabase();
                dlgDevice=new DeviceDialog(this,dlgSetup=new SetupDialog(this,particles));
        }
        public static void main(String args[]) {
                HEPsimulator sim=new HEPsimulator();
                sim.start();
        }
        public void start() {
                show();
        }
        /**
        * Performs the experiment.
        */
        public void run() {
                uni=new CMframe();
                ingoing[0]=dlgSetup.getParticleA();
                ingoing[1]=dlgSetup.getParticleB();
                uni.setRelCMvel(ingoing[0],ingoing[1]);
                uni.addParticle(ingoing[0]);
                if(ingoing[1]!=null)
                        uni.addParticle(ingoing[1]);

                try {
                        for(int i=0;i<particles.length;i++) {
                                outgoing[0]=(QuantumParticle) particles[i].getClass().newInstance();
                                for(int j=i;j<particles.length;j++) {
                                        outgoing[1]=(QuantumParticle) particles[j].getClass().newInstance();
                                        if(uni.conserve(outgoing[0],outgoing[1]))
                                                new ResultsDialog(this,ingoing,outgoing,uni.interact()).show();
                                }
                        }
                } catch(Exception e) {}
        }
        /**
        * Loads up the particle database.
        */
        private void loadDatabase() {
// LEPTIONS (12)
                particles[0]=new Electron();
                particles[1]=new Positron();
                particles[2]=new ElectronNeutrino();
                particles[3]=new AntiElectronNeutrino();
                particles[4]=new Muon();
                particles[5]=new AntiMuon();
                particles[6]=new MuonNeutrino();
                particles[7]=new AntiMuonNeutrino();
                particles[8]=new Tau();
                particles[9]=new AntiTau();
                particles[10]=new TauNeutrino();
                particles[11]=new AntiTauNeutrino();
// MESONS (8)
                particles[12]=new PiZero();
                particles[13]=new PiPlus();
                particles[14]=new PiMinus();
                particles[15]=new KPlus();
                particles[16]=new KMinus();
                particles[17]=new KZero();
                particles[18]=new AntiKZero();
                particles[19]=new Eta();
// BARYONS (18)
                particles[20]=new Proton();
                particles[21]=new AntiProton();
                particles[22]=new Neutron();
                particles[23]=new AntiNeutron();
                particles[24]=new Lambda();
                particles[25]=new AntiLambda();
                particles[26]=new SigmaPlus();
                particles[27]=new AntiSigmaPlus();
                particles[28]=new SigmaZero();
                particles[29]=new AntiSigmaZero();
                particles[30]=new SigmaMinus();
                particles[31]=new AntiSigmaMinus();
                particles[32]=new XiZero();
                particles[33]=new AntiXiZero();
                particles[34]=new XiMinus();
                particles[35]=new XiPlus();
                particles[36]=new OmegaMinus();
                particles[37]=new AntiOmegaMinus();
        }
}


/**
* DeviceDialog.
* Sets the experimental apparatus.
* @version 1.3
* @author Mark Hale
*/
class DeviceDialog extends Dialog {
        private Button ok=new Button("OK");
        private SetupDialog dlgRel;
        private CheckboxGroup cbg=new CheckboxGroup();
        private Checkbox cbChamber=new Checkbox("Bubble chamber",cbg,true);
        private Checkbox cbAccelerator=new Checkbox("Particle accelerator",cbg,false);
        public DeviceDialog(Frame f,SetupDialog sd) {
                super(f,"Device Selection",true);
                addWindowListener(new WindowAdapter() {
                        public void windowClosing(WindowEvent evt) {
                                setVisible(false);
                        }
                });
                cbChamber.addItemListener(new ItemListener() {
                        public void itemStateChanged(ItemEvent evt) {
                                dlgRel.particleB.setEnabled(false);
                                dlgRel.energyB.setEnabled(false);
                        }
                });
                cbAccelerator.addItemListener(new ItemListener() {
                        public void itemStateChanged(ItemEvent evt) {
                                dlgRel.particleB.setEnabled(true);
                                dlgRel.energyB.setEnabled(true);
                        }
                });
                ok.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                                dispose();
                        }
                });
                setSize(200,120);
                dlgRel=sd;
                setLayout(new GridLayout(3,1));
                add(cbChamber);
                add(cbAccelerator);
                add(ok);
                dlgRel.particleB.setEnabled(false);
                dlgRel.energyB.setEnabled(false);
        }
}


/**
* SetupDialog.
* Sets the experimental parameters.
* @version 1.3
* @author Mark Hale
*/
class SetupDialog extends Dialog {
        private Button ok=new Button("OK");
        public Choice particleA=new Choice();
        public Choice particleB=new Choice();
        public TextField energyA,energyB;
        private QuantumParticle array[];
        public SetupDialog(Frame f,QuantumParticle qpArray[]) {
                super(f,"Device Settings",true);
                array=qpArray;
                energyA=new TextField(Double.toString(array[0].restMass()));
                energyB=new TextField(Double.toString(array[0].restMass()));
                addWindowListener(new WindowAdapter() {
                        public void windowClosing(WindowEvent evt) {
                                setVisible(false);
                        }
                });
                particleA.addItemListener(new ItemListener() {
                        public void itemStateChanged(ItemEvent evt) {
                                energyA.setText(Double.toString(array[particleA.getSelectedIndex()].restMass()));
                        }
                });
                particleB.addItemListener(new ItemListener() {
                        public void itemStateChanged(ItemEvent evt) {
                                energyB.setText(Double.toString(array[particleB.getSelectedIndex()].restMass()));
                        }
                });
                energyA.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                                if(Double.valueOf(energyA.getText()).doubleValue() < array[particleA.getSelectedIndex()].restMass())
                                        energyA.setText(Double.toString(array[particleA.getSelectedIndex()].restMass()));
                        }
                });
                energyB.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                                if(Double.valueOf(energyB.getText()).doubleValue() < array[particleB.getSelectedIndex()].restMass())
                                        energyB.setText(Double.toString(array[particleB.getSelectedIndex()].restMass()));
                        }
                });
                ok.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                                dispose();
                        }
                });
                setSize(350,130);
                GridBagLayout gb=new GridBagLayout();
                GridBagConstraints c=new GridBagConstraints();
                for(int n=0;n<array.length;n++) {
                        particleA.addItem(array[n].toString());
                        particleB.addItem(array[n].toString());
                }
                setLayout(gb);
                c.anchor=GridBagConstraints.CENTER;
                c.gridwidth=GridBagConstraints.RELATIVE;
                gb.setConstraints(particleA,c);
                add(particleA);
                c.gridwidth=GridBagConstraints.REMAINDER;
                gb.setConstraints(particleB,c);
                add(particleB);
                c.gridwidth=GridBagConstraints.RELATIVE;
                gb.setConstraints(energyA,c);
                add(energyA);
                c.gridwidth=GridBagConstraints.REMAINDER;
                gb.setConstraints(energyB,c);
                add(energyB);
                c.gridwidth=GridBagConstraints.REMAINDER;
                gb.setConstraints(ok,c);
                add(ok);
        }
        public QuantumParticle getParticleA() {
                QuantumParticle temp;
                try {
                        temp=(QuantumParticle) array[particleA.getSelectedIndex()].getClass().newInstance();
                } catch(Exception e) {
                        temp=null;
                }
                double energy=Double.valueOf(energyA.getText()).doubleValue();
                double mass=temp.restMass();
                temp.momentum=new Rank1Tensor(energy,Math.sqrt(energy*energy-mass*mass),0.0,0.0);
                return temp;
        }
        public QuantumParticle getParticleB() {
                QuantumParticle temp;
                if(particleB.isEnabled()) {
                        try {
                                temp=(QuantumParticle) array[particleB.getSelectedIndex()].getClass().newInstance();
                        } catch(Exception e) {
                                temp=null;
                        }
                        double energy=Double.valueOf(energyB.getText()).doubleValue();
                        double mass=temp.restMass();
                        temp.momentum=new Rank1Tensor(energy,-Math.sqrt(energy*energy-mass*mass),0.0,0.0);
                } else
                        temp=null;
                return temp;
        }
}


/**
* ResultsDialog.
* Displays the results of an experiment.
* @version 1.3
* @author Mark Hale
*/
class ResultsDialog extends Dialog {
        private Button ok=new Button("OK");
        public ResultsDialog(Frame f,QuantumParticle in[],QuantumParticle out[],String interact) {
                super(f,"Results",false);
                addWindowListener(new WindowAdapter() {
                        public void windowClosing(WindowEvent evt) {
                                dispose();
                        }
                });
                ok.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                                dispose();
                        }
                });
                setSize(300,300);
                setLayout(new GridLayout(9,1));
                if(in[1]==null)
                        add(new Label(in[0].toString()+"------>"));
                else
                        add(new Label(in[0].toString()+" --->  <--- "+in[1].toString()));
                add(new Label(out[0].toString()));
                add(new Label("Energy = "+Double.toString(out[0].momentum.getComponent(0))));
                add(new Label("Momentum = "+Double.toString(out[0].momentum.getComponent(1))));
                add(new Label(out[1].toString()));
                add(new Label("Energy = "+Double.toString(out[1].momentum.getComponent(0))));
                add(new Label("Momentum = "+Double.toString(out[1].momentum.getComponent(1))));
                add(new Label("Interaction was the "+interact));
                add(ok);
        }
}

/**
* CMframe.
* Defines a class representing the Centre of Mass frame.
* @version 1.3
* @author Mark Hale
*/
final class CMframe {
        /**
        * Total energy.
        */
        private double energy;
        /**
        * Quantum number totals.
        */
        private int B,I,Iz,Le,Lm,Lt,Q,S;
        /**
        * The relative velocity between the Centre
        * of Mass frame and the lab frame.
        */
        private double relCMvel;
        /**
        * Interaction.
        */
        private int interaction;
        /**
        * Interaction codes.
        */
        private final static int STRONG=1,WEAK=2,EM=3;
        /**
        * Constructor.
        */
        public CMframe() {
                energy=B=I=Iz=Le=Lm=Lt=Q=S=0;
        }
        /**
        * Checks whether Particles a and b obey the
        * conservation laws.
        */
        public boolean conserve(QuantumParticle a,QuantumParticle b) {
                double Ei,Ej,pi,pj;
        // work out interaction type (also check conservation of quantum numbers)
                if(a.charge()+b.charge()==Q && a.eLeptonQN()+b.eLeptonQN()==Le &&
                        a.muLeptonQN()+b.muLeptonQN()==Lm && a.tauLeptonQN()+b.tauLeptonQN()==Lt &&
                        a.baryonQN()+b.baryonQN()==B && Math.abs(S-a.strangeQN()-b.strangeQN())<=1) {
                        if(a.strangeQN()+b.strangeQN()==S && a.isospinZ()+b.isospinZ()==Iz) {
                                if(a.isospin()==0 && b.isospin()==0) {
                                        // EM Interaction
                                        interaction=EM;
                                } else {
                                        // Strong Interaction
                                        interaction=STRONG;
                                }
                        } else {
                                // Weak Interaction
                                interaction=WEAK;
                        }
                } else {
                        interaction=0;
                }
        // check quantum numbers are conserved
                if(interaction>0) {
        // Convert to keV for better accuracy
                        double massA=1000.0*a.restMass();
                        double massB=1000.0*b.restMass();
        // work out Ei,Ej,pi,pj
                        Ei=(energy*energy-massB*massB+massA*massA)/(2.0*energy);
                        Ej=(energy*energy-massA*massA+massB*massB)/(2.0*energy);
                        pi=(energy*energy*energy*energy +
                                massA*massA*massA*massA +
                                massB*massB*massB*massB -
                                2.0*massA*massA*energy*energy -
                                2.0*massB*massB*energy*energy -
                                2.0*massA*massA*massB*massB);
        // check consistency
                        if(pi>=0.0 && Ei>=0.0 && Ej>=0.0) {
                                pi=Math.sqrt(pi)/(2.0*energy);
        // Convert back to MeV and update E & p values
                                pi/=1000.0;
                                a.momentum=new Rank1Tensor(Ei/1000.0,pi,0.0,0.0);
                                b.momentum=new Rank1Tensor(Ej/1000.0,-pi,0.0,0.0);
        // Perform Lorentz Transform
                                if(relCMvel!=0.0) {
                                        LorentzBoost lb=new LorentzBoost(-relCMvel,0.0,0.0);
                                        a.momentum=lb.multiply(a.momentum);
                                        b.momentum=lb.multiply(b.momentum);
                                }
                                return true;
                        }
                }
                return false;
        }
        public void addParticle(QuantumParticle qp) {
                B+=qp.baryonQN();
                I+=qp.isospin();
                Iz+=qp.isospinZ();
                Le+=qp.eLeptonQN();
                Lm+=qp.muLeptonQN();
                Lt+=qp.tauLeptonQN();
                Q+=qp.charge();
                S+=qp.strangeQN();

                LorentzBoost lb=new LorentzBoost(relCMvel,0.0,0.0);
                qp.momentum=lb.multiply(qp.momentum);

                energy+=qp.momentum.getComponent(0)*1000.0;       // store as keV for better accuracy
        }
        /**
        * Set the relative centre of mass velocity.
        */
        public void setRelCMvel(QuantumParticle a,QuantumParticle b) {
                if(b==null)
                        relCMvel=a.momentum.getComponent(1)/a.momentum.getComponent(0);
                else
                        relCMvel=(a.momentum.getComponent(1)+b.momentum.getComponent(1))/(a.momentum.getComponent(0)+b.momentum.getComponent(0));
        }
        /**
        * Returns the type of interaction that took place.
        */
        public String interact() {
                switch(interaction) {
                        case STRONG : return new String("Strong Force");
                        case WEAK : return new String("Weak Force");
                        case EM : return new String("Electromagnetic Force");
                        default : return new String("Unknown - error");
                }
        }
}
