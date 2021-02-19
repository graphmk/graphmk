// 
// Decompiled by Procyon v0.5.36
// 

package com.graphmk.utils;

import java.util.Map;
import java.util.Comparator;

public class VirtualDegreeComparator implements Comparator<String>
{
    boolean incr;
    Map<String, Integer> virtualIndex;
    
    public VirtualDegreeComparator(final Map<String, Integer> vIndex, final boolean incr) {
        this.incr = incr;
        this.virtualIndex = vIndex;
    }
    
    @Override
    public int compare(final String o1, final String o2) {
        if (this.incr) {
            return this.virtualIndex.get(o1) - this.virtualIndex.get(o2);
        }
        return this.virtualIndex.get(o2) - this.virtualIndex.get(o1);
    }
}
