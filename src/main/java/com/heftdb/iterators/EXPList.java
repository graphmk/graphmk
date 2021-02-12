// 
// Decompiled by Procyon v0.5.36
// 

package com.heftdb.iterators;

import java.util.NoSuchElementException;
import java.util.Map;
import java.util.Iterator;
import com.heftdb.mkcore.VertexOptMk;
import com.tinkerpop.blueprints.Direction;
import com.heftdb.mkcore.mkelement.MkVertex;
import java.util.List;
import com.heftdb.mkcore.interfaces.Vertex;
import java.util.AbstractList;

public class EXPList extends AbstractList<Vertex>
{
    private List<? extends Vertex> outVs;
    private List<? extends Vertex> inVs;
    MkVertex theNode;
    
    public EXPList(final MkVertex theNode) {
        this.theNode = theNode;
        this.outVs = theNode.getPhysicalNeighbors(Direction.OUT);
        this.inVs = theNode.getPhysicalNeighbors(Direction.IN);
    }
    
    @Override
    public MkVertex get(final int index) {
        return null;
    }
    
    @Override
    public int size() {
        int toRet = 0;
        for (final Vertex v : this.outVs) {
            if (v instanceof VertexOptMk) {
                toRet += v.getPhysicalNeighbors(Direction.IN).size() - 1;
            }
            else {
                ++toRet;
            }
        }
        toRet += this.inVs.size();
        return toRet;
    }
    
    @Override
    public Iterator<Vertex> iterator() {
        return new Iterator<Vertex>() {
            int outCurrPosition = EXPList.this.outVs.size() - 1;
            int inCurrPosition = EXPList.this.inVs.size() - 1;
            List<? extends MkVertex> currRealNeighbors;
            private Map<String, Integer> currBMPIndex;
            
            @Override
            public boolean hasNext() {
                return this.outCurrPosition >= 0 || this.inCurrPosition >= 0;
            }
            
            @Override
            public Vertex next() {
                if (this.outCurrPosition >= 0) {
                    return EXPList.this.outVs.get(this.outCurrPosition--);
                }
                if (this.inCurrPosition >= 0) {
                    return EXPList.this.inVs.get(this.inCurrPosition--);
                }
                throw new NoSuchElementException();
            }
        };
    }
}
