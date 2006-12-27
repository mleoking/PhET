// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package util;

import java.io.PrintStream;

public class ThreadHelper
{

    public static void quietNap(int i)
    {
        try
        {
            Thread.sleep(i);
        }
        catch(InterruptedException interruptedexception)
        {
            System.out.println("Napping disturbed");
        }
    }

}
