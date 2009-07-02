package edu.colorado.phet.naturalselection;

import edu.colorado.phet.naturalselection.defaults.NaturalSelectionDefaults;

public class NaturalSelectionSettings {

    //----------------------------------------------------------------------------
    // Defaults
    //----------------------------------------------------------------------------

    private boolean clockStartRunning = NaturalSelectionDefaults.CLOCK_RUNNING;
    private int clockFrameRate = NaturalSelectionDefaults.CLOCK_FRAME_RATE;
    private double clockDT = NaturalSelectionDefaults.CLOCK_DT;

    private double ticksPerYear = NaturalSelectionDefaults.TICKS_PER_YEAR;
    private double selectionTick = NaturalSelectionDefaults.SELECTION_TICK;
    private double maxKillFraction = NaturalSelectionDefaults.MAX_KILL_FRACTION;
    private double frenzyTicks = NaturalSelectionDefaults.FRENZY_TICKS;

    private int bunniesDieWhenTheyAreThisOld = NaturalSelectionDefaults.BUNNIES_DIE_WHEN_THEY_ARE_THIS_OLD;

    private boolean bunniesBecomeSterile = NaturalSelectionDefaults.BUNNIES_BECOME_STERILE;
    private int bunniesSterileWhenThisOld = NaturalSelectionDefaults.BUNNIES_STERILE_WHEN_THIS_OLD;

    private int bunnyBetweenHopTime = NaturalSelectionDefaults.BUNNY_BETWEEN_HOP_TIME;
    private int bunnyHopTime = NaturalSelectionDefaults.BUNNY_HOP_TIME;
    private int bunnyHopHeight = NaturalSelectionDefaults.BUNNY_HOP_HEIGHT;
    private double bunnyNormalHopDistance = NaturalSelectionDefaults.BUNNY_NORMAL_HOP_DISTANCE;
    private int bunnyHungerThreshold = NaturalSelectionDefaults.BUNNY_HUNGER_THRESHOLD;
    private int bunnyMaxHunger = NaturalSelectionDefaults.BUNNY_MAX_HUNGER;
    private double bunnySideSpacer = NaturalSelectionDefaults.BUNNY_SIDE_SPACER;

    private double wolfMaxStep = NaturalSelectionDefaults.WOLF_MAX_STEP;
    private double wolfDoubleBackDistance = NaturalSelectionDefaults.WOLF_DOUBLE_BACK_DISTANCE;
    private double wolfKillDistance = NaturalSelectionDefaults.WOLF_KILL_DISTANCE;

    private int maxPopulation = NaturalSelectionDefaults.MAX_POPULATION;

    private int wolfBase = NaturalSelectionDefaults.WOLF_BASE;
    private int bunniesPerWolves = NaturalSelectionDefaults.BUNNIES_PER_WOLVES;

    private double wolfSelectionBunnyOffset = NaturalSelectionDefaults.WOLF_SELECTION_BUNNY_OFFSET;
    private double wolfSelectionBunnyExponent = NaturalSelectionDefaults.WOLF_SELECTION_BUNNY_EXPONENT;
    private double wolfSelectionScale = NaturalSelectionDefaults.WOLF_SELECTION_SCALE;
    private double wolfSelectionBlendScale = NaturalSelectionDefaults.WOLF_SELECTION_BLEND_SCALE;

    private double foodSelectionBunnyOffset = NaturalSelectionDefaults.FOOD_SELECTION_BUNNY_OFFSET;
    private double foodSelectionBunnyExponent = NaturalSelectionDefaults.FOOD_SELECTION_BUNNY_EXPONENT;
    private double foodSelectionScale = NaturalSelectionDefaults.FOOD_SELECTION_SCALE;
    private double foodSelectionBlendScale = NaturalSelectionDefaults.FOOD_SELECTION_BLEND_SCALE;

    private int mutatingBunnyBase = NaturalSelectionDefaults.MUTATING_BUNNY_BASE;
    private int mutatingBunnyPerBunnies = NaturalSelectionDefaults.MUTATING_BUNNY_PER_BUNNIES;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------

    public NaturalSelectionSettings() {
    }

    //----------------------------------------------------------------------------
    // Getters and setters
    //----------------------------------------------------------------------------

    public boolean isClockStartRunning() {
        return clockStartRunning;
    }

    public void setClockStartRunning( boolean clockStartRunning ) {
        this.clockStartRunning = clockStartRunning;
    }

    public int getClockFrameRate() {
        return clockFrameRate;
    }

    public void setClockFrameRate( int clockFrameRate ) {
        this.clockFrameRate = clockFrameRate;
    }

    public double getClockDT() {
        return clockDT;
    }

    public void setClockDT( double clockDT ) {
        this.clockDT = clockDT;
    }

    public double getSelectionTick() {
        return selectionTick;
    }

    public void setSelectionTick( double selectionTick ) {
        this.selectionTick = selectionTick;
    }

    public double getTicksPerYear() {
        return ticksPerYear;
    }

    public void setTicksPerYear( double ticksPerYear ) {
        this.ticksPerYear = ticksPerYear;
    }

    public double getMaxKillFraction() {
        return maxKillFraction;
    }

    public void setMaxKillFraction( double maxKillFraction ) {
        this.maxKillFraction = maxKillFraction;
    }

    public double getFrenzyTicks() {
        return frenzyTicks;
    }

    public void setFrenzyTicks( double frenzyTicks ) {
        this.frenzyTicks = frenzyTicks;
    }

    public int getBunniesDieWhenTheyAreThisOld() {
        return bunniesDieWhenTheyAreThisOld;
    }

    public void setBunniesDieWhenTheyAreThisOld( int bunniesDieWhenTheyAreThisOld ) {
        this.bunniesDieWhenTheyAreThisOld = bunniesDieWhenTheyAreThisOld;
    }

    public boolean isBunniesBecomeSterile() {
        return bunniesBecomeSterile;
    }

    public void setBunniesBecomeSterile( boolean bunniesBecomeSterile ) {
        this.bunniesBecomeSterile = bunniesBecomeSterile;
    }

    public int getBunniesSterileWhenThisOld() {
        return bunniesSterileWhenThisOld;
    }

    public void setBunniesSterileWhenThisOld( int bunniesSterileWhenThisOld ) {
        this.bunniesSterileWhenThisOld = bunniesSterileWhenThisOld;
    }

    public int getBunnyBetweenHopTime() {
        return bunnyBetweenHopTime;
    }

    public void setBunnyBetweenHopTime( int bunnyBetweenHopTime ) {
        this.bunnyBetweenHopTime = bunnyBetweenHopTime;
    }

    public int getBunnyHopTime() {
        return bunnyHopTime;
    }

    public void setBunnyHopTime( int bunnyHopTime ) {
        this.bunnyHopTime = bunnyHopTime;
    }

    public int getBunnyHopHeight() {
        return bunnyHopHeight;
    }

    public void setBunnyHopHeight( int bunnyHopHeight ) {
        this.bunnyHopHeight = bunnyHopHeight;
    }

    public double getBunnyNormalHopDistance() {
        return bunnyNormalHopDistance;
    }

    public void setBunnyNormalHopDistance( double bunnyNormalHopDistance ) {
        this.bunnyNormalHopDistance = bunnyNormalHopDistance;
    }

    public int getBunnyHungerThreshold() {
        return bunnyHungerThreshold;
    }

    public void setBunnyHungerThreshold( int bunnyHungerThreshold ) {
        this.bunnyHungerThreshold = bunnyHungerThreshold;
    }

    public int getBunnyMaxHunger() {
        return bunnyMaxHunger;
    }

    public void setBunnyMaxHunger( int bunnyMaxHunger ) {
        this.bunnyMaxHunger = bunnyMaxHunger;
    }

    public double getBunnySideSpacer() {
        return bunnySideSpacer;
    }

    public void setBunnySideSpacer( double bunnySideSpacer ) {
        this.bunnySideSpacer = bunnySideSpacer;
    }

    public double getWolfMaxStep() {
        return wolfMaxStep;
    }

    public void setWolfMaxStep( double wolfMaxStep ) {
        this.wolfMaxStep = wolfMaxStep;
    }

    public double getWolfDoubleBackDistance() {
        return wolfDoubleBackDistance;
    }

    public void setWolfDoubleBackDistance( double wolfDoubleBackDistance ) {
        this.wolfDoubleBackDistance = wolfDoubleBackDistance;
    }

    public double getWolfKillDistance() {
        return wolfKillDistance;
    }

    public void setWolfKillDistance( double wolfKillDistance ) {
        this.wolfKillDistance = wolfKillDistance;
    }

    public int getMaxPopulation() {
        return maxPopulation;
    }

    public void setMaxPopulation( int maxPopulation ) {
        this.maxPopulation = maxPopulation;
    }

    public int getWolfBase() {
        return wolfBase;
    }

    public void setWolfBase( int wolfBase ) {
        this.wolfBase = wolfBase;
    }

    public int getBunniesPerWolves() {
        return bunniesPerWolves;
    }

    public void setBunniesPerWolves( int bunniesPerWolves ) {
        this.bunniesPerWolves = bunniesPerWolves;
    }

    public double getWolfSelectionBunnyOffset() {
        return wolfSelectionBunnyOffset;
    }

    public void setWolfSelectionBunnyOffset( double wolfSelectionBunnyOffset ) {
        this.wolfSelectionBunnyOffset = wolfSelectionBunnyOffset;
    }

    public double getWolfSelectionBunnyExponent() {
        return wolfSelectionBunnyExponent;
    }

    public void setWolfSelectionBunnyExponent( double wolfSelectionBunnyExponent ) {
        this.wolfSelectionBunnyExponent = wolfSelectionBunnyExponent;
    }

    public double getWolfSelectionScale() {
        return wolfSelectionScale;
    }

    public void setWolfSelectionScale( double wolfSelectionScale ) {
        this.wolfSelectionScale = wolfSelectionScale;
    }

    public double getWolfSelectionBlendScale() {
        return wolfSelectionBlendScale;
    }

    public void setWolfSelectionBlendScale( double wolfSelectionBlendScale ) {
        this.wolfSelectionBlendScale = wolfSelectionBlendScale;
    }

    public double getFoodSelectionBunnyOffset() {
        return foodSelectionBunnyOffset;
    }

    public void setFoodSelectionBunnyOffset( double foodSelectionBunnyOffset ) {
        this.foodSelectionBunnyOffset = foodSelectionBunnyOffset;
    }

    public double getFoodSelectionBunnyExponent() {
        return foodSelectionBunnyExponent;
    }

    public void setFoodSelectionBunnyExponent( double foodSelectionBunnyExponent ) {
        this.foodSelectionBunnyExponent = foodSelectionBunnyExponent;
    }

    public double getFoodSelectionScale() {
        return foodSelectionScale;
    }

    public void setFoodSelectionScale( double foodSelectionScale ) {
        this.foodSelectionScale = foodSelectionScale;
    }

    public double getFoodSelectionBlendScale() {
        return foodSelectionBlendScale;
    }

    public void setFoodSelectionBlendScale( double foodSelectionBlendScale ) {
        this.foodSelectionBlendScale = foodSelectionBlendScale;
    }

    public int getMutatingBunnyBase() {
        return mutatingBunnyBase;
    }

    public void setMutatingBunnyBase( int mutatingBunnyBase ) {
        this.mutatingBunnyBase = mutatingBunnyBase;
    }

    public int getMutatingBunnyPerBunnies() {
        return mutatingBunnyPerBunnies;
    }

    public void setMutatingBunnyPerBunnies( int mutatingBunnyPerBunnies ) {
        this.mutatingBunnyPerBunnies = mutatingBunnyPerBunnies;
    }
}
