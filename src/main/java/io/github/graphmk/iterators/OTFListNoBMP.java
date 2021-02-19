// 
// Decompiled by Procyon v0.5.36
// 

package io.github.graphmk.iterators;

import java.util.NoSuchElementException;
import io.github.graphmk.mkcore.VertexOptMk;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import org.apache.commons.lang.NotImplementedException;
import com.tinkerpop.blueprints.Direction;
import io.github.graphmk.mkcore.mkelement.MkVertex;
import java.util.List;
import io.github.graphmk.mkcore.interfaces.Vertex;
import java.util.AbstractList;

public class OTFListNoBMP extends AbstractList<Vertex>
{
    private List<? extends Vertex> outVs;
    private List<? extends Vertex> inVs;
    MkVertex theNode;
    
    public OTFListNoBMP(final MkVertex theNode) {
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
        throw new NotImplementedException();
    }
    
    @Override
    public Iterator<Vertex> iterator() {
        return new Iterator<Vertex>() {
            int outCurrPosition = OTFListNoBMP.this.outVs.size() - 1;
            int inCurrPosition = OTFListNoBMP.this.inVs.size() - 1;
            int outCurrIndx = -1;
            List<? extends Vertex> currRealNeighbors;
            Set<String> union = new HashSet<String>();
            
            @Override
            public boolean hasNext() {
                while (this.outCurrPosition >= 0 || this.outCurrIndx >= 0) {
                    if (this.outCurrIndx < 0) {
                        final Vertex v = OTFListNoBMP.this.outVs.get(this.outCurrPosition);
                        if (v instanceof VertexOptMk) {
                            --this.outCurrPosition;
                            this.currRealNeighbors = ((VertexOptMk)v).getPhysicalNeighbors(Direction.IN);
                            this.outCurrIndx = this.currRealNeighbors.size() - 1;
                            if (this.outCurrIndx >= 0 && ((Vertex)this.currRealNeighbors.get(this.outCurrIndx)).toString().equals(OTFListNoBMP.this.theNode.toString())) {
                                --this.outCurrIndx;
                            }
                            if (this.outCurrIndx < 0) {
                                continue;
                            }
                            if (!this.union.contains(((Vertex)this.currRealNeighbors.get(this.outCurrIndx)).toString())) {
                                return true;
                            }
                            --this.outCurrIndx;
                        }
                        else {
                            if (!v.toString().equals(OTFListNoBMP.this.theNode.toString()) && !this.union.contains(v.toString())) {
                                return true;
                            }
                            --this.outCurrPosition;
                        }
                    }
                    else {
                        if (this.outCurrIndx < 0) {
                            continue;
                        }
                        while (this.outCurrIndx >= 0 && ((Vertex)this.currRealNeighbors.get(this.outCurrIndx)).toString().equals(OTFListNoBMP.this.theNode.toString())) {
                            --this.outCurrIndx;
                        }
                        if (this.outCurrIndx < 0) {
                            continue;
                        }
                        if (!this.union.contains(((Vertex)this.currRealNeighbors.get(this.outCurrIndx)).toString())) {
                            return true;
                        }
                        --this.outCurrIndx;
                    }
                }
                return this.inCurrPosition >= 0 && !this.union.contains(OTFListNoBMP.this.inVs.get(this.inCurrPosition).toString());
            }
            
            @Override
            public Vertex next() {
                if (this.outCurrIndx >= 0 || this.outCurrPosition >= 0) {
                    if (this.outCurrIndx < 0) {
                        final Vertex v = OTFListNoBMP.this.outVs.get(this.outCurrPosition--);
                        this.union.add(v.toString());
                        return v;
                    }
                    final Vertex v = (Vertex)this.currRealNeighbors.get(this.outCurrIndx--);
                    this.union.add(v.toString());
                    return v;
                }
                else {
                    if (this.inCurrPosition >= 0) {
                        final Vertex v = OTFListNoBMP.this.inVs.get(this.inCurrPosition--);
                        this.union.add(v.toString());
                        return v;
                    }
                    throw new NoSuchElementException();
                }
            }
        };
    }
}
