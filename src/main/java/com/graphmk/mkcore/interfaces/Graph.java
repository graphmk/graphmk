// 
// Decompiled by Procyon v0.5.36
// 

package com.graphmk.mkcore.interfaces;

import java.util.Map;
import com.tinkerpop.blueprints.GraphQuery;
import com.tinkerpop.blueprints.Edge;
import java.util.List;
import com.tinkerpop.blueprints.Features;

public interface Graph
{
    Features getFeatures();
    
    Vertex addVertex(final Object p0);
    
    Vertex getVertex(final Object p0);
    
    void removeVertex(final Vertex p0);
    
    List<Vertex> getVertices();
    
    Iterable<Vertex> getVertices(final String p0, final Object p1);
    
    Edge addEdge(final Object p0, final Vertex p1, final Vertex p2, final String p3);
    
    Edge getEdge(final Object p0);
    
    void removeEdge(final Edge p0);
    
    Iterable<Edge> getEdges();
    
    Iterable<Edge> getEdges(final String p0, final Object p1);
    
    GraphQuery query();
    
    void shutdown();
    
    Map<String, Integer> getIndex();
}
