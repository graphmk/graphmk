// 
// Decompiled by Procyon v0.5.36
// 

package com.graphmk.utils;

import java.util.Map;
import java.util.Comparator;

public class VirtualNodeComparator implements Comparator
{
    private Map<String, Integer> vNodes;
    
    public VirtualNodeComparator(final Map<String, Integer> vNodes) {
        this.vNodes = vNodes;
    }
    
    @Override
    public int compare(final Object o1, final Object o2) {
        return this.vNodes.get(o1).compareTo(this.vNodes.get(o2));
    }
}
