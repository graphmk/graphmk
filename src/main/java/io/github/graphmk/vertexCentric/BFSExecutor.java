// 
// Decompiled by Procyon v0.5.36
// 

package io.github.graphmk.vertexCentric;

import com.tinkerpop.blueprints.Direction;
import io.github.graphmk.mkcore.mkelement.MkVertex;
import io.github.graphmk.mkcore.interfaces.Vertex;

public class BFSExecutor extends Executor
{
    public BFSExecutor() {
        super("BFS_Seen");
    }
    
    @Override
    public void compute(final Vertex v, final VertexCentric p) {
        final MkVertex gv = (MkVertex)v;
        if (gv.getId().toString().equals("A")) {
            System.out.println("Visiting :" + gv);
            gv.setVal(true);
        }
        if (gv.getVal() instanceof Boolean) {
            for (final Vertex w : v.getVertices(Direction.BOTH, new String[0])) {
                if (w.getVal() == null) {
                    w.setVal("next");
                }
            }
            p.voteToHalt(gv);
        }
        else if (gv.getVal() instanceof String) {
            System.out.println("Visiting: " + gv);
            gv.setVal(true);
        }
    }
}
