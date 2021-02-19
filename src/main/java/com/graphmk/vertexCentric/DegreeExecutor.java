// 
// Decompiled by Procyon v0.5.36
// 

package com.graphmk.vertexCentric;

import com.tinkerpop.blueprints.Direction;
import com.graphmk.mkcore.interfaces.Vertex;

public class DegreeExecutor extends Executor
{
    public DegreeExecutor() {
        super("Degree");
    }
    
    @Override
    public void compute(final Vertex v, final VertexCentric p) {
        v.setVal(0);
        for (final Vertex w : v.getVertices(Direction.BOTH, new String[0])) {
            v.setVal((int)v.getVal() + 1);
        }
        p.voteToHalt(v);
    }
}
