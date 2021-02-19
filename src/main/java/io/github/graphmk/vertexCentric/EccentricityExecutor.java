// 
// Decompiled by Procyon v0.5.36
// 

package io.github.graphmk.vertexCentric;

import io.github.graphmk.mkcore.interfaces.Vertex;
import io.github.graphmk.mkcore.mkelement.MkVertex;
import com.tinkerpop.blueprints.Direction;

import java.util.HashMap;

public class EccentricityExecutor extends Executor
{
    public EccentricityExecutor() {
        super("Eccentricity");
    }
    
    @Override
    public void compute(final Vertex v, final VertexCentric p) {
        if (p.getSuperstep() == 0) {
            v.setVal(new HashMap());
            final HashMap<String, Integer> myset = (HashMap<String, Integer>)v.getVal();
            for (final Vertex e : v.getVertices(Direction.BOTH, new String[0])) {
                myset.put(e.toString(), 1);
            }
        }
        else {
            final HashMap<String, Integer> myset = (HashMap<String, Integer>)v.getVal();
            boolean someChange = false;
            int eccentricity = 0;
            for (final Vertex e2 : v.getVertices(Direction.BOTH, new String[0])) {
                final MkVertex ge = (MkVertex)e2;
                if (!ge.isHalted()) {
                    final HashMap<String, Integer> eSet = (HashMap<String, Integer>)ge.getPrevVal();
                    for (final String s : eSet.keySet()) {
                        if (!myset.containsKey(s)) {
                            myset.put(s, eSet.get(s) + 1);
                            someChange = true;
                        }
                    }
                }
            }
            if (!someChange) {
                for (final Integer s2 : myset.values()) {
                    if (s2 > eccentricity) {
                        eccentricity = s2;
                    }
                }
                v.setVal(eccentricity);
                System.out.println(v + " My Eccentricity: " + v.getVal());
                p.voteToHalt(v);
            }
        }
    }
}
