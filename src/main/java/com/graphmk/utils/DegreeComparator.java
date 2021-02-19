// 
// Decompiled by Procyon v0.5.36
// 

package com.graphmk.utils;

import com.graphmk.mkcore.interfaces.Vertex;
import java.util.Comparator;

public class DegreeComparator implements Comparator<Vertex>
{
    boolean incr;
    
    public DegreeComparator(final boolean incr) {
        this.incr = incr;
    }
    
    @Override
    public int compare(final Vertex o1, final Vertex o2) {
        if (this.incr) {
            return Integer.valueOf(o1.getProperty("Degree").toString()) - Integer.valueOf(o2.getProperty("Degree").toString());
        }
        return Integer.valueOf(o2.getProperty("Degree").toString()) - Integer.valueOf(o1.getProperty("Degree").toString());
    }
}
