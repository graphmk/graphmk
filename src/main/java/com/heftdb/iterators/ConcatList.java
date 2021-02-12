// 
// Decompiled by Procyon v0.5.36
// 

package com.heftdb.iterators;

import java.util.NoSuchElementException;
import java.util.Iterator;
import com.heftdb.mkcore.VertexOptMk;
import com.tinkerpop.blueprints.Direction;
import com.heftdb.mkcore.mkelement.MkVertex;
import java.util.List;
import com.heftdb.mkcore.interfaces.Vertex;
import java.util.AbstractList;

public class ConcatList extends AbstractList<Vertex>
{
    private List<? extends Vertex> outVs;
    private List<? extends Vertex> inVs;
    MkVertex theNode;
    
    public ConcatList(final MkVertex theNode) {
        this.theNode = theNode;
        this.outVs = theNode.getPhysicalNeighbors(Direction.OUT);
        this.inVs = theNode.getPhysicalNeighbors(Direction.IN);
    }
    
    @Override
    public Vertex get(final int index) {
        return null;
    }
    
    @Override
    public int size() {
        int toRet = 0;
        for (final Vertex v : this.outVs) {
            if (v instanceof VertexOptMk) {
                toRet += ((MkVertex)v).getPhysicalNeighbors(Direction.IN).size() - 1;
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
            int outCurrPosition = ConcatList.this.outVs.size() - 1;
            int inCurrPosition = ConcatList.this.inVs.size() - 1;
            int outCurrIndx = -1;
            List<? extends Vertex> currRealNeighbors;
            
            @Override
            public boolean hasNext() {
                while (this.outCurrPosition >= 0 || this.outCurrIndx >= 0) {
                    if (this.outCurrIndx < 0) {
                        final Vertex v = ConcatList.this.outVs.get(this.outCurrPosition);
                        if (v instanceof VertexOptMk) {
                            --this.outCurrPosition;
                            this.currRealNeighbors = ((VertexOptMk)v).getPhysicalNeighbors(Direction.IN);
                            this.outCurrIndx = this.currRealNeighbors.size() - 1;
                            if (this.outCurrIndx >= 0 && ((Vertex)this.currRealNeighbors.get(this.outCurrIndx)).toString().equals(ConcatList.this.theNode.toString())) {
                                --this.outCurrIndx;
                            }
                            if (this.outCurrIndx >= 0) {
                                return true;
                            }
                            continue;
                        }
                        else {
                            if (!v.toString().equals(ConcatList.this.theNode.toString())) {
                                return true;
                            }
                            --this.outCurrPosition;
                        }
                    }
                    else {
                        if (this.outCurrIndx < 0) {
                            continue;
                        }
                        while (this.outCurrIndx >= 0 && ((Vertex)this.currRealNeighbors.get(this.outCurrIndx)).toString().equals(ConcatList.this.theNode.toString())) {
                            --this.outCurrIndx;
                        }
                        if (this.outCurrIndx >= 0) {
                            return true;
                        }
                        continue;
                    }
                }
                return this.inCurrPosition >= 0;
            }
            
            @Override
            public Vertex next() {
                if (this.outCurrIndx >= 0 || this.outCurrPosition >= 0) {
                    if (this.outCurrIndx < 0) {
                        final Vertex v = ConcatList.this.outVs.get(this.outCurrPosition--);
                        return v;
                    }
                    return (Vertex)this.currRealNeighbors.get(this.outCurrIndx--);
                }
                else {
                    if (this.inCurrPosition >= 0) {
                        return ConcatList.this.inVs.get(this.inCurrPosition--);
                    }
                    throw new NoSuchElementException();
                }
            }
        };
    }
}
