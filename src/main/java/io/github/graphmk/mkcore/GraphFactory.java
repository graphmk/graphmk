// 
// Decompiled by Procyon v0.5.36
// 

package io.github.graphmk.mkcore;

import io.github.graphmk.mkcore.interfaces.Graph;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;

public class GraphFactory
{
    public Graph create(final String graphType) {
        if (graphType.equals("TinkerGraph")) {
            return (Graph)new TinkerGraph();
        }
        if (graphType.equals("AdjacencyCSR")) {
            return new MkGraph();
        }
        if (graphType.equals("Condensed")) {
            return new GraphOptMk();
        }
        throw new UnsupportedOperationException("Can't instanciate this graph");
    }
}
