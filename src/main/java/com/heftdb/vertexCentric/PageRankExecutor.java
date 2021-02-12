// 
// Decompiled by Procyon v0.5.36
// 

package com.heftdb.vertexCentric;

import com.heftdb.mkcore.mkelement.MkVertex;
import com.tinkerpop.blueprints.Direction;
import com.heftdb.mkcore.interfaces.Vertex;

public class PageRankExecutor extends Executor
{
    public PageRankExecutor() {
        super("PR");
    }
    
    @Override
    public void compute(final Vertex v, final VertexCentric p) {
        final int degree = v.getProperty("Degree");
        if (p.getSuperstep() == 0) {
            v.setVal(1.0 / degree);
        }
        if (p.getSuperstep() >= 1) {
            double sum = 0.0;
            for (final Vertex e : v.getVertices(Direction.BOTH, new String[0])) {
                sum += (double)((MkVertex)e).getPrevVal();
            }
            final double newPageRank = 0.15 / p.getNumOfVertices() + 0.85 * sum;
            v.setVal(newPageRank / degree);
        }
        if (p.getSuperstep() == 30) {
            v.setVal((double)v.getPrevVal() * degree);
            if (degree == 0) {
                v.setVal(0.0);
            }
            p.voteToHalt(v);
        }
    }
}
