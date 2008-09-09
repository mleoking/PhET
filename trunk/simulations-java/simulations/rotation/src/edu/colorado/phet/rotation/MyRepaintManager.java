// Copyright 2007 University of Colorado
package edu.colorado.phet.rotation;

import java.awt.*;
import java.util.*;

import javax.swing.*;

import edu.umd.cs.piccolox.pswing.PSwingRepaintManager;

public class MyRepaintManager extends PSwingRepaintManager {
    private final HashMap componentToDirtyRects = new HashMap();
    private boolean coalesceRectangles = true;

    private static MyRepaintManager instance;
    private boolean doMyCoalesce = false;

    public MyRepaintManager() {
        if( instance != null ) {
            throw new RuntimeException( "instance already exists" );
        }
        instance = this;
    }

    public static MyRepaintManager getInstance() {
        return instance;
    }

    public void doUpdateNow() {
        ArrayList components=new ArrayList( );
        Set keys = componentToDirtyRects.keySet();
        //workaround to avoid concurrentmodification problem exhibited with multiple frames
        for( Iterator iterator = keys.iterator(); iterator.hasNext(); ) {
            JComponent jComponent = (JComponent)iterator.next();
            components.add(jComponent);
        }

        for (int k=0;k<components.size();k++){
            JComponent jComponent = (JComponent)components.get(k);
            ArrayList origRect = (ArrayList)componentToDirtyRects.get( jComponent );
            ArrayList rect = consolidateList( origRect );
            for( int i = 0; i < rect.size(); i++ ) {
                Rectangle rectangle = (Rectangle)rect.get( i );
                jComponent.paintImmediately( rectangle );
            }
        }
        componentToDirtyRects.clear();

    }

    private ArrayList consolidateList( ArrayList origRect ) {
        ArrayList newList = new ArrayList( origRect );

        for( int i = 0; i < newList.size(); i++ ) {
            Rectangle a = (Rectangle)newList.get( i );

            for( int j = 0; j < newList.size(); j++ ) {
                Rectangle b = (Rectangle)newList.get( j );

                if( a != b && a.contains( b ) ) {
                    newList.remove( j );

                    --j;
                }
            }
        }

        //System.out.println("original size="+origRect.size()+", new size="+newList.size());

        return newList;
    }

    public synchronized void addDirtyRegion( JComponent c, int x, int y, int w, int h ) {
        if( doMyCoalesce && coalesceRectangles ) {
            if( !componentToDirtyRects.containsKey( c ) ) {
                componentToDirtyRects.put( c, new ArrayList() );
            }
            ArrayList list = (ArrayList)componentToDirtyRects.get( c );
            list.add( new Rectangle( x, y, w, h ) );
        }
        else {
            componentToDirtyRects.clear();
            super.addDirtyRegion( c, x, y, w, h );
        }
    }

    public void setDoMyCoalesce( boolean doMyCoalesce ) {
        this.doMyCoalesce = doMyCoalesce;
    }

    public boolean isCoalesceRectangles() {
        return coalesceRectangles;
    }

    public void setCoalesceRectangles( boolean coalesceRectangles ) {
        this.coalesceRectangles = coalesceRectangles;
    }
}
