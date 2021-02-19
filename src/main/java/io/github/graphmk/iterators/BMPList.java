// 
// Decompiled by Procyon v0.5.36
// 

package io.github.graphmk.iterators;

import java.util.NoSuchElementException;
import java.util.BitSet;
import java.util.Iterator;

import io.github.graphmk.mkcore.VertexOptMk;
import io.github.graphmk.mkcore.interfaces.Vertex;
import com.tinkerpop.blueprints.Direction;
import io.github.graphmk.mkcore.mkelement.MkVertex;

import java.util.List;
import java.util.AbstractList;

public class BMPList extends AbstractList<Vertex>
{
    private List<? extends Vertex> outVs;
    private List<? extends Vertex> inVs;
    MkVertex theNode;
    
    public BMPList(final MkVertex theNode) {
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
            int outCurrPosition = BMPList.this.outVs.size() - 1;
            int inCurrPosition = BMPList.this.inVs.size() - 1;
            int outCurrIndx = -1;
            BitSet currBMP = null;
            List<? extends Vertex> currRealNeighbors;
            
            @Override
            public boolean hasNext() {
                while (this.outCurrPosition >= 0 || this.outCurrIndx >= 0) {
                    if (this.outCurrIndx < 0) {
                        final Vertex v = BMPList.this.outVs.get(this.outCurrPosition);
                        if (v instanceof VertexOptMk) {
                            final VertexOptMk cv = (VertexOptMk)v;
                            --this.outCurrPosition;
                            this.currRealNeighbors = cv.getPhysicalNeighbors(Direction.IN);
                            this.currBMP = cv.getBitmaps().get(cv.getBMPIndex().get(BMPList.this.theNode.toString()));
                            this.outCurrIndx = this.currRealNeighbors.size() - 1;
                            while (this.outCurrIndx >= 0 && !this.currBMP.get(this.outCurrIndx)) {
                                --this.outCurrIndx;
                            }
                            if (this.outCurrIndx >= 0) {
                                return true;
                            }
                            continue;
                        }
                        else {
                            if (!v.toString().equals(BMPList.this.theNode.toString())) {
                                return true;
                            }
                            continue;
                        }
                    }
                    else {
                        if (this.outCurrIndx < 0) {
                            continue;
                        }
                        while (this.outCurrIndx >= 0 && !this.currBMP.get(this.outCurrIndx)) {
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
                        final Vertex v = BMPList.this.outVs.get(this.outCurrPosition--);
                        return v;
                    }
                    return (Vertex)this.currRealNeighbors.get(this.outCurrIndx--);
                }
                else {
                    if (this.inCurrPosition >= 0) {
                        return BMPList.this.inVs.get(this.inCurrPosition--);
                    }
                    throw new NoSuchElementException();
                }
            }
        };
    }
}
