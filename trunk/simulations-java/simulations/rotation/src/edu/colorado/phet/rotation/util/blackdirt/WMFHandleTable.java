package edu.colorado.phet.rotation.util.blackdirt;

import java.util.*;
public class WMFHandleTable{
   private    Vector handleTable;
   private    Hashtable MRecordTable;

   public WMFHandleTable(){
     Integer i;
     i = new Integer(-1);
     handleTable = new Vector();
     this.MRecordTable = new Hashtable();
   }

   public MetaRecord selectObject(int index){
     Integer i;
     Integer j;
     java.util.Enumeration thisVector;
     MetaRecord m;

      i = new Integer(-1);
        try{
//            System.out.println ("h index " + index);
            i = (Integer)handleTable.elementAt(index);
//            System.out.println ("h i     " + i);
           }  catch(StringIndexOutOfBoundsException e){
            System.err.println(e);
           }

//      thisVector = handleTable.elements();
//      while(thisVector.hasMoreElements()){
//          j = (Integer)thisVector.nextElement();
//          System.out.println ("table elemment " + j);
//      }

        m = (MetaRecord)MRecordTable.get(i);
     return(m);
   }

   public void deleteObject(int index){
      Integer i;
      i = new Integer(-1);
      handleTable.setElementAt((Integer) i, index);
      i = new Integer(index);
      MRecordTable.remove(i);


   }

   public void addObject(int recordValue, MetaRecord m){
   int index;
   Integer h;
   Integer i;

   h = new Integer(recordValue);


   i = new Integer(-1); // -1

   if (handleTable.contains(i)){  // if there is a free handle due to delete
     index = handleTable.indexOf(i);  // get the index of the deleted record
     handleTable.setElementAt(h, index); //set the new value
   }
   else{
     handleTable.addElement(h);
   }

   MRecordTable.put(h, m);

  }
}

