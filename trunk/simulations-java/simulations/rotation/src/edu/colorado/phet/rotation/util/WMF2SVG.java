package edu.colorado.phet.rotation.util;
// Copyright 1997, 1998,1999 Carmen Delessio (carmen@blackdirt.com)
// Black Dirt Software http://www.blackdirt.com/graphics
// Free for non-commercial use

// revisions:
// May 12,1999 corrected bad URL http://www.w3.org
// Corrected using <desc> as placeholders by embedding in their own <g>
//http://www.blackdirt.com/graphics/svg/wmf2svg.java


public class MetaRecord extends Object {

    private   int         rdSize;
    private   short       rdFunction;
    private   byte[]      rdParm;



    public MetaRecord(int rdSize, short rdFunction, byte[] rdParm) {
        this.rdSize = rdSize;
        this.rdFunction = rdFunction;
        this.rdParm = rdParm; // arraycopy
        //rdParm = new byte[4];
    }

    public void initialize(int rdSize, short rdFunction, byte[] rdParm) {
        this.rdSize = rdSize;
        this.rdFunction = rdFunction;
        this.rdParm = rdParm; // arraycopy
        //rdParm = new byte[4];
    }

    public int getSize() {
        return rdSize;
    }

    public short getFunction() {
        return rdFunction;
    }

    public byte[] getParm() {
        return rdParm;
    }

}
