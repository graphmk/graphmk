// 
// Decompiled by Procyon v0.5.36
// 

package io.github.graphmk.vertexCentric;

import java.util.Map;
import java.util.HashSet;
import com.tinkerpop.blueprints.Direction;
import java.util.Set;
import java.util.HashMap;
import io.github.graphmk.mkcore.interfaces.Vertex;

public class LCCExecutor extends Executor
{
    public LCCExecutor() {
        super("LCC");
    }
    
    @Override
    public void compute(final Vertex v, final VertexCentric p) {
        final int degree = v.getProperty("Degree");
        final Map<String, Set<String>> neighbors = new HashMap<String, Set<String>>();
        for (final Vertex e : v.getVertices(Direction.BOTH, new String[0])) {
            final String from = e.toString();
            if (!neighbors.containsKey(from)) {
                neighbors.put(from, new HashSet<String>());
            }
            for (final Vertex w : e.getVertices(Direction.BOTH, new String[0])) {
                if (!w.toString().equals(v.toString()) && neighbors.containsKey(w.toString()) && !neighbors.get(w.toString()).contains(from)) {
                    neighbors.get(from).add(w.toString());
                }
            }
        }
        int numOfNeighborsConnected = 0;
        for (final String s : neighbors.keySet()) {
            final Set<String> value = neighbors.get(s);
            numOfNeighborsConnected += value.size();
        }
        if (degree > 1) {
            v.setVal(2.0 * numOfNeighborsConnected / (degree * (degree - 1)));
        }
        else {
            v.setVal(0.0);
        }
        p.voteToHalt(v);
    }
}
