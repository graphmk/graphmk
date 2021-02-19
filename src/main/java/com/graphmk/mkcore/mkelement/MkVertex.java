// 
// Decompiled by Procyon v0.5.36
// 

package com.graphmk.mkcore.mkelement;

import com.graphmk.fpgrowth.FPTree;
import com.graphmk.iterators.BMPList;
import com.graphmk.iterators.ConcatList;
import com.graphmk.iterators.OTFListNoBMP;
import com.graphmk.mkcore.MkGraph;
import com.graphmk.mkcore.interfaces.Vertex;
import com.graphmk.mkcore.GraphOptMk;
import com.graphmk.mkcore.interfaces.Graph;
import com.tinkerpop.blueprints.Edge;

import java.util.HashSet;

import com.graphmk.iterators.EXPList;

import java.util.List;
import com.tinkerpop.blueprints.Direction;
import java.util.Set;
import org.apache.log4j.Logger;
import java.io.Serializable;

public class MkVertex extends MkElement implements Vertex, Comparable<Vertex>, Serializable
{
    static final Logger logger;
    private static final long serialVersionUID = 1L;
    MkGraph graph;
    private boolean halted;
    private Object value;
    private Object prevVal;
    static Set<String> edges;
    
    public MkVertex() {
    }
    
    public Graph getGraph() {
        return this.graph;
    }
    
    @Override
    public boolean isHalted() {
        return this.halted;
    }
    
    @Override
    public void voteToHalt() {
        this.halted = true;
    }
    
    public void terminateSuperstep(final String name) {
        this.setProperty(name, this.value);
        this.prevVal = this.value;
    }
    
    public MkVertex(final Object id, final MkGraph g) {
        super(id);
        this.graph = g;
    }
    
    @Override
    public List<Vertex> getPhysicalNeighbors(final Direction direction) {
        if (direction == Direction.IN) {
            if (this.graph instanceof GraphOptMk) {
                return ((GraphOptMk)this.graph).inVertices.get(this.graph.index.get(this.toString()));
            }
            return this.graph.inVertices.get(this.graph.index.get(this.toString()));
        }
        else {
            if (direction == Direction.OUT) {
                return this.graph.outVertices.get(this.graph.index.get(this.toString()));
            }
            return null;
        }
    }
    
    @Override
    public Iterable<Vertex> getVertices(final Direction direction, final String... labels) {
        if (direction == null) {
            return null;
        }
        final Integer idx = this.graph.index.get(this.toString());
        if (this.graph instanceof GraphOptMk) {
            final GraphOptMk cg = (GraphOptMk)this.graph;
            switch (cg.getRep()) {
                case CDUP: {
                    return new OTFListNoBMP(this);
                }
                case EXP: {
                    return new EXPList(this);
                }
                case DEDUP1: {
                    return new ConcatList(this);
                }
                case BMP1: {
                    return new BMPList(this);
                }
                case BMP2: {
                    return new BMPList(this);
                }
                default: {
                    return new ConcatList(this);
                }
            }
        }
        else if (direction.equals(Direction.IN) && !(this.graph instanceof GraphOptMk)) {
            if (this.graph.inVertices.get(idx) == null) {
                return new HashSet<Vertex>();
            }
            return this.graph.inVertices.get(idx);
        }
        else {
            if (!direction.equals(Direction.OUT) || this.graph instanceof GraphOptMk) {
                return new ConcatList(this);
            }
            if (this.graph.outVertices.get(idx) == null) {
                return new HashSet<Vertex>();
            }
            return this.graph.outVertices.get(idx);
        }
    }
    
    public Iterable<Vertex> getVerticesBMP(final Direction direction) {
        if (direction == null) {
            return null;
        }
        final Integer idx = this.graph.index.get(this.toString());
        if (this.graph instanceof GraphOptMk && !((GraphOptMk)this.graph).isExpanded()) {
            return new BMPList(this);
        }
        if (direction.equals(Direction.IN) && !(this.graph instanceof GraphOptMk)) {
            if (this.graph.inVertices.get(idx) == null) {
                return new HashSet<Vertex>();
            }
            return this.graph.inVertices.get(idx);
        }
        else {
            if (!direction.equals(Direction.OUT) || this.graph instanceof GraphOptMk) {
                return new BMPList(this);
            }
            if (this.graph.outVertices.get(idx) == null) {
                return new HashSet<Vertex>();
            }
            return this.graph.outVertices.get(idx);
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof MkVertex) {
            return ((MkVertex)o).getId().toString().equals(this.getId().toString());
        }
        return o instanceof FPTree.Node && ((FPTree.Node)o).getData().equals(this);
    }
    
    @Override
    public String toString() {
        return this.getId().toString();
    }
    
    @Override
    public int compareTo(final Vertex v) {
        return this.id.toString().compareTo(v.getId().toString());
    }
    
    @Override
    public Object getVal() {
        return this.value;
    }
    
    @Override
    public void setVal(final Object value) {
        this.value = value;
    }
    
    @Override
    public Object getPrevVal() {
        return this.prevVal;
    }
    
    public void setPrevVal(final Object prevVal) {
        this.prevVal = prevVal;
    }
    
    public void wake() {
        this.halted = false;
    }
    
    @Override
    public Iterable<Edge> getEdges(final Direction direction, final String... labels) {
        return null;
    }
    
    @Override
    public void addEdge(final String label, final Vertex inVertex) {
    }
    
    static {
        logger = Logger.getLogger(MkVertex.class);
        MkVertex.edges = new HashSet<String>();
    }
}
