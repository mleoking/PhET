//// Copyright 2002-2011, University of Colorado
//package edu.colorado.phet.simsharing;
//
//import java.io.Serializable;
//
///**
// * @author Sam Reid
// */
//public class StudentMetadata implements Serializable {
//    private StudentID studentID;
//    private int numSamples;
//    private final long serverTime;
//
//    public StudentMetadata( StudentID studentID, int numSamples, long serverTime ) {
//        this.studentID = studentID;
//        this.numSamples = numSamples;
//        this.serverTime = serverTime;
//    }
//
//    public StudentID getStudentID() {
//        return studentID;
//    }
//
//    public int getNumSamples() {
//        return numSamples;
//    }
//
//    public long getServerTime() {
//        return serverTime;
//    }
//
//    @Override
//    public String toString() {
//        return "StudentMetadata{" +
//               "studentID=" + studentID +
//               ", numSamples=" + numSamples +
//               ", serverTime=" + serverTime +
//               '}';
//    }
//}
