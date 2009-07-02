package edu.colorado.phet.naturalselection.developer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.naturalselection.NaturalSelectionApplication;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.NaturalSelectionSettings;
import edu.colorado.phet.naturalselection.model.NaturalSelectionModel;
import edu.colorado.phet.naturalselection.module.NaturalSelectionModule;

public class DeveloperSettingsPanel extends VerticalLayoutPanel {

    NaturalSelectionApplication app;
    private NaturalSelectionModule module;
    private NaturalSelectionModel model;
    private NaturalSelectionSettings nsSettings;
    private List<Setting> settings;
    private EasyGridBagLayout layout;
    private int col = 0;
    private int row = 0;

    public DeveloperSettingsPanel( NaturalSelectionApplication app ) {
        this.app = app;

        JPanel container = new JPanel();
        layout = new EasyGridBagLayout( container );
        container.setLayout( layout );

        settings = new LinkedList<Setting>();

        module = (NaturalSelectionModule) app.getActiveModule();
        model = module.getMyModel();
        nsSettings = NaturalSelectionConstants.getSettings();

        setBorder( new TitledBorder( "Settings" ) );

        addSetting( new Setting( "clockFrameRate",
                                 new SpinnerNumberModel( nsSettings.getClockFrameRate(), 1, 50, 1 ),
                                 "Frames per second (controls animation and model)" ) {
            public void apply() {
                model.getClock().setDelay( 1000 / getInt() );
            }
        } );

        addSetting( new Setting( "ticksPerYear",
                                 new SpinnerNumberModel( nsSettings.getTicksPerYear(), 30, 1000, 10 ),
                                 "Number of clock ticks per year. When smaller, generations will pass more quickly." ) {
            public void apply() {
                nsSettings.setTicksPerYear( getDouble() );
            }
        } );

        addSetting( new Setting( "selectionTick",
                                 new SpinnerNumberModel( nsSettings.getSelectionTick(), 0, 1000, 10 ),
                                 "The tick when the selection factor activates (wolves frenzy or bunnies starve). " +
                                 "This must be in between 0 and ticksPerYear." ) {
            public void apply() {
                nsSettings.setSelectionTick( getDouble() );
            }
        } );

        addSetting( new Setting( "frenzyTicks",
                                 new SpinnerNumberModel( nsSettings.getFrenzyTicks(), 15 * 1, 15 * 50, 150 ),
                                 "Maximum number of clock ticks per frenzy" ) {
            public void apply() {
                nsSettings.setFrenzyTicks( getDouble() );
            }
        } );

        addSetting( new Setting( "bunniesDieWhenThisOld",
                                 new SpinnerNumberModel( nsSettings.getBunniesDieWhenTheyAreThisOld(), 2, 12, 1 ),
                                 "As the title says, bunnies die when they get to this age." ) {
            public void apply() {
                nsSettings.setBunniesDieWhenTheyAreThisOld( getInt() );
            }
        } );

        addSetting( new Setting( "bunnyBetweenHopTime",
                                 new SpinnerNumberModel( nsSettings.getBunnyBetweenHopTime(), 0, 500, 5 ),
                                 "Number of ticks between hopping for each bunny." ) {
            public void apply() {
                nsSettings.setBunnyBetweenHopTime( getInt() );
            }
        } );

        addSetting( new Setting( "bunnyHopTime",
                                 new SpinnerNumberModel( nsSettings.getBunnyHopTime(), 2, 100, 2 ),
                                 "Number of ticks when the bunny is in mid-air. " +
                                 "Suggested to change bunnyNormalHopDistance with this" ) {
            public void apply() {
                nsSettings.setBunnyHopTime( getInt() );
            }
        } );

        addSetting( new Setting( "bunnyHopHeight",
                                 new SpinnerNumberModel( nsSettings.getBunnyHopHeight(), 5, 300, 5 ),
                                 "The height at which bunnies hop to." ) {
            public void apply() {
                nsSettings.setBunnyHopHeight( getInt() );
            }
        } );

        addSetting( new Setting( "bunnyNormalHopDistance",
                                 new SpinnerNumberModel( nsSettings.getBunnyNormalHopDistance(), 2, 100, 2 ),
                                 "The distance each bunny hop goes" ) {
            public void apply() {
                nsSettings.setBunnyNormalHopDistance( getDouble() );
            }
        } );

        addSetting( new Setting( "bunnyHungerThreshold",
                                 new SpinnerNumberModel( nsSettings.getBunnyHungerThreshold(), 5, 1000, 5 ),
                                 "When bunnies (with long teeth now) get hungry, they go to the bush. The bush resets to 0. " +
                                 "This should be less than bunnyMaxHunger" ) {
            public void apply() {
                nsSettings.setBunnyHungerThreshold( getInt() );
            }
        } );

        addSetting( new Setting( "bunnyMaxHunger",
                                 new SpinnerNumberModel( nsSettings.getBunnyMaxHunger(), 0, 1000, 5 ),
                                 "Maximum hunger. Currently the lower (closer) it is to bunnyHungerThreshold, " +
                                 "the more bunnies at the start will go towards bushes." ) {
            public void apply() {
                nsSettings.setBunnyMaxHunger( getInt() );
            }
        } );

        nextCol();

        addSetting( new Setting( "wolfMaxStep",
                                 new SpinnerNumberModel( nsSettings.getWolfMaxStep(), 1, 20, 1 ),
                                 "The amount of distance a wolf can move in a tick." ) {
            public void apply() {
                nsSettings.setWolfMaxStep( getDouble() );
            }
        } );

        addSetting( new Setting( "wolfDoubleBackDistance",
                                 new SpinnerNumberModel( nsSettings.getWolfDoubleBackDistance(), 6, 20 * 6, 6 ),
                                 "If a wolf is too close to a bunny to eat it (the mouth is a certain distance from the middle of the wolf), " +
                                 "the wolf has to move away to this distance before going back to eat the bunny." ) {
            public void apply() {
                nsSettings.setWolfDoubleBackDistance( getDouble() );
            }
        } );

        addSetting( new Setting( "wolfKillDistance",
                                 new SpinnerNumberModel( nsSettings.getWolfKillDistance(), 2, 40, 2 ),
                                 "The distance the wolf head can be from a bunny to kill it, when pointing the correct direction" ) {
            public void apply() {
                nsSettings.setWolfKillDistance( getDouble() );
            }
        } );

        addSetting( new Setting( "wolfBase",
                                 new SpinnerNumberModel( nsSettings.getWolfBase(), 0, 50, 1 ),
                                 "When a wolf frenzy starts, the number of wolves = wolfBase + population / bunniesPerWolves" ) {
            public void apply() {
                nsSettings.setWolfBase( getInt() );
            }
        } );

        addSetting( new Setting( "bunniesPerWolves",
                                 new SpinnerNumberModel( nsSettings.getBunniesPerWolves(), 1, 100, 1 ),
                                 "When a wolf frenzy starts, the number of wolves = wolfBase + population / bunniesPerWolves" ) {
            public void apply() {
                nsSettings.setBunniesPerWolves( getInt() );
            }
        } );

        addSetting( new Setting( "maxPopulation",
                                 new SpinnerNumberModel( nsSettings.getMaxPopulation(), 10, 10000, 10 ),
                                 "Maximum population of bunnies. If exceeded, the 'Bunnies take over the world' dialog will be shown, " +
                                 "and the user will have to reset." ) {
            public void apply() {
                nsSettings.setMaxPopulation( getInt() );
            }
        } );

        addSetting( new Setting( "mutatingBunnyBase",
                                 new SpinnerNumberModel( nsSettings.getMutatingBunnyBase(), 1, 100, 1 ),
                                 "When possible, at least this many bunnies will mutate when the user desires." ) {
            public void apply() {
                nsSettings.setMutatingBunnyBase( getInt() );
            }
        } );

        addSetting( new Setting( "mutatingBunnyPerBunnies",
                                 new SpinnerNumberModel( nsSettings.getMutatingBunnyPerBunnies(), 1, 100, 1 ),
                                 "Total number of bunnies to mutate = mutatingBunnyBase + population / mutatingBunnyPerBunnies" ) {
            public void apply() {
                nsSettings.setMutatingBunnyPerBunnies( getInt() );
            }
        } );


        nextCol();

        layout.addComponent( new JLabel( "P_base(die) = (pop + offset) ^ exponent * scale " ), row++, col );
        layout.addComponent( new JLabel( "P_blend|regular(die) = min( maxKillFraction, P_base(die) * blendScale )" ), row++, col );
        layout.addComponent( new JLabel( "P_noBlend|longTeeth(die) = min( maxKillFraction, P_base(die) )" ), row++, col );

        addSetting( new Setting( "maxKillFraction",
                                 new SpinnerNumberModel( nsSettings.getMaxKillFraction(), 0.1, 1, 0.025 ) ) {
            public void apply() {
                nsSettings.setMaxKillFraction( getDouble() );
            }
        } );

        addSetting( new Setting( "wolfSelectionBunnyOffset",
                                 new SpinnerNumberModel( nsSettings.getWolfSelectionBunnyOffset(), -50, 50, 1 ) ) {
            public void apply() {
                nsSettings.setWolfSelectionBunnyOffset( getDouble() );
            }
        } );

        addSetting( new Setting( "wolfSelectionBunnyExponent",
                                 new SpinnerNumberModel( nsSettings.getWolfSelectionBunnyExponent(), 0.1, 1, 0.05 ) ) {
            public void apply() {
                nsSettings.setWolfSelectionBunnyExponent( getDouble() );
            }
        } );

        addSetting( new Setting( "wolfSelectionScale",
                                 new SpinnerNumberModel( nsSettings.getWolfSelectionScale(), 0.05, 1, 0.05 ) ) {
            public void apply() {
                nsSettings.setWolfSelectionScale( getDouble() );
            }
        } );

        addSetting( new Setting( "wolfSelectionBlendScale",
                                 new SpinnerNumberModel( nsSettings.getWolfSelectionBlendScale(), 0.1, 1, 0.1 ) ) {
            public void apply() {
                nsSettings.setWolfSelectionBlendScale( getDouble() );
            }
        } );


        addSetting( new Setting( "foodSelectionBunnyOffset",
                                 new SpinnerNumberModel( nsSettings.getFoodSelectionBunnyOffset(), -50, 50, 1 ) ) {
            public void apply() {
                nsSettings.setFoodSelectionBunnyOffset( getDouble() );
            }
        } );

        addSetting( new Setting( "foodSelectionBunnyExponent",
                                 new SpinnerNumberModel( nsSettings.getFoodSelectionBunnyExponent(), 0.1, 1, 0.05 ) ) {
            public void apply() {
                nsSettings.setFoodSelectionBunnyExponent( getDouble() );
            }
        } );

        addSetting( new Setting( "foodSelectionScale",
                                 new SpinnerNumberModel( nsSettings.getFoodSelectionScale(), 0.05, 1, 0.05 ) ) {
            public void apply() {
                nsSettings.setFoodSelectionScale( getDouble() );
            }
        } );

        addSetting( new Setting( "foodSelectionBlendScale",
                                 new SpinnerNumberModel( nsSettings.getFoodSelectionBlendScale(), 0.1, 1, 0.1 ) ) {
            public void apply() {
                nsSettings.setFoodSelectionBlendScale( getDouble() );
            }
        } );

        add( container );

        JButton resetWithChangesButton = new JButton( "Reset with Changes" );
        add( resetWithChangesButton );

        resetWithChangesButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                for ( Setting setting : settings ) {
                    setting.apply();
                }
                module.reset();
            }
        } );
    }

    public void nextCol() {
        col++;
        row = 0;
    }

    public void addSetting( Setting setting ) {
        layout.addComponent( setting, row++, col );
        settings.add( setting );
    }

    private abstract class Setting extends JPanel {
        private String label;
        private Number defaultValue;
        private SpinnerModel model;

        private JSpinner spinner;


        protected Setting( String label, SpinnerModel model ) {
            this.label = label;
            this.model = model;
            this.defaultValue = defaultValue;

            add( new JLabel( label ) );

            spinner = new JSpinner( model );
            add( spinner );
        }

        protected Setting( String label, SpinnerModel model, String toolTip ) {
            this.label = label;
            this.model = model;
            this.defaultValue = defaultValue;

            add( new JLabel( label ) );

            spinner = new JSpinner( model );
            add( spinner );

            this.setToolTipText( toolTip );
        }

        public int getInt() {
            return getNumber().intValue();
        }

        public double getDouble() {
            return getNumber().doubleValue();
        }

        public Number getNumber() {
            return (Number) model.getValue();
        }

        public abstract void apply();
    }
}
