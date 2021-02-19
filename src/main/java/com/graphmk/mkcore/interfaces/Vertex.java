// 
// Decompiled by Procyon v0.5.36
// 

package com.graphmk.mkcore.interfaces;

import java.util.List;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Element;

public interface Vertex extends Element, Comparable<Vertex>
{
    Iterable<Edge> getEdges(final Direction p0, final String... p1);
    
    Iterable<Vertex> getVertices(final Direction p0, final String... p1);
    
    List<Vertex> getPhysicalNeighbors(final Direction p0);
    
    void addEdge(final String p0, final Vertex p1);
    
    Object getVal();
    
    void setVal(final Object p0);
    
    void voteToHalt();
    
    Object getPrevVal();
    
    boolean isHalted();
}
