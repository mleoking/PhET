package edu.colorado.phet.naturalselection.persistence;

import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;

public class BunnyConfig implements IProguardKeepClass {

    private int id;

    private int fatherId;
    private int motherId;
    private int potentialMateId;

    private boolean mated;
    private boolean mutated;
    private boolean alive;

    private int[] childrenIds;

    private int age;
    private int generation;

    private boolean colorPhenotypeRegular;
    private boolean teethPhenotypeRegular;
    private boolean tailPhenotypeRegular;

    private boolean colorFatherGenotypeRegular;
    private boolean teethFatherGenotypeRegular;
    private boolean tailFatherGenotypeRegular;

    private boolean colorMotherGenotypeRegular;
    private boolean teethMotherGenotypeRegular;
    private boolean tailMotherGenotypeRegular;

    private double x, y, z;

    private int sinceHopTime;
    private boolean movingRight;
    private int hunger;

    private double hopX, hopY, hopZ;

    public BunnyConfig() {
    }

    public int getId() {
        return id;
    }

    public void setId( int id ) {
        this.id = id;
    }

    public int getFatherId() {
        return fatherId;
    }

    public void setFatherId( int fatherId ) {
        this.fatherId = fatherId;
    }

    public int getMotherId() {
        return motherId;
    }

    public void setMotherId( int motherId ) {
        this.motherId = motherId;
    }

    public int getPotentialMateId() {
        return potentialMateId;
    }

    public void setPotentialMateId( int potentialMateId ) {
        this.potentialMateId = potentialMateId;
    }

    public boolean isMated() {
        return mated;
    }

    public void setMated( boolean mated ) {
        this.mated = mated;
    }

    public boolean isMutated() {
        return mutated;
    }

    public void setMutated( boolean mutated ) {
        this.mutated = mutated;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive( boolean alive ) {
        this.alive = alive;
    }

    public int[] getChildrenIds() {
        return childrenIds;
    }

    public void setChildrenIds( int[] childrenIds ) {
        this.childrenIds = childrenIds;
    }

    public int getAge() {
        return age;
    }

    public void setAge( int age ) {
        this.age = age;
    }

    public int getGeneration() {
        return generation;
    }

    public void setGeneration( int generation ) {
        this.generation = generation;
    }

    public boolean isColorPhenotypeRegular() {
        return colorPhenotypeRegular;
    }

    public void setColorPhenotypeRegular( boolean colorPhenotypeRegular ) {
        this.colorPhenotypeRegular = colorPhenotypeRegular;
    }

    public boolean isTeethPhenotypeRegular() {
        return teethPhenotypeRegular;
    }

    public void setTeethPhenotypeRegular( boolean teethPhenotypeRegular ) {
        this.teethPhenotypeRegular = teethPhenotypeRegular;
    }

    public boolean isTailPhenotypeRegular() {
        return tailPhenotypeRegular;
    }

    public void setTailPhenotypeRegular( boolean tailPhenotypeRegular ) {
        this.tailPhenotypeRegular = tailPhenotypeRegular;
    }

    public boolean isColorFatherGenotypeRegular() {
        return colorFatherGenotypeRegular;
    }

    public void setColorFatherGenotypeRegular( boolean colorFatherGenotypeRegular ) {
        this.colorFatherGenotypeRegular = colorFatherGenotypeRegular;
    }

    public boolean isTeethFatherGenotypeRegular() {
        return teethFatherGenotypeRegular;
    }

    public void setTeethFatherGenotypeRegular( boolean teethFatherGenotypeRegular ) {
        this.teethFatherGenotypeRegular = teethFatherGenotypeRegular;
    }

    public boolean isTailFatherGenotypeRegular() {
        return tailFatherGenotypeRegular;
    }

    public void setTailFatherGenotypeRegular( boolean tailFatherGenotypeRegular ) {
        this.tailFatherGenotypeRegular = tailFatherGenotypeRegular;
    }

    public boolean isColorMotherGenotypeRegular() {
        return colorMotherGenotypeRegular;
    }

    public void setColorMotherGenotypeRegular( boolean colorMotherGenotypeRegular ) {
        this.colorMotherGenotypeRegular = colorMotherGenotypeRegular;
    }

    public boolean isTeethMotherGenotypeRegular() {
        return teethMotherGenotypeRegular;
    }

    public void setTeethMotherGenotypeRegular( boolean teethMotherGenotypeRegular ) {
        this.teethMotherGenotypeRegular = teethMotherGenotypeRegular;
    }

    public boolean isTailMotherGenotypeRegular() {
        return tailMotherGenotypeRegular;
    }

    public void setTailMotherGenotypeRegular( boolean tailMotherGenotypeRegular ) {
        this.tailMotherGenotypeRegular = tailMotherGenotypeRegular;
    }

    public double getX() {
        return x;
    }

    public void setX( double x ) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY( double y ) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ( double z ) {
        this.z = z;
    }

    public int getSinceHopTime() {
        return sinceHopTime;
    }

    public void setSinceHopTime( int sinceHopTime ) {
        this.sinceHopTime = sinceHopTime;
    }

    public boolean isMovingRight() {
        return movingRight;
    }

    public void setMovingRight( boolean movingRight ) {
        this.movingRight = movingRight;
    }

    public int getHunger() {
        return hunger;
    }

    public void setHunger( int hunger ) {
        this.hunger = hunger;
    }

    public double getHopX() {
        return hopX;
    }

    public void setHopX( double hopX ) {
        this.hopX = hopX;
    }

    public double getHopY() {
        return hopY;
    }

    public void setHopY( double hopY ) {
        this.hopY = hopY;
    }

    public double getHopZ() {
        return hopZ;
    }

    public void setHopZ( double hopZ ) {
        this.hopZ = hopZ;
    }


}
