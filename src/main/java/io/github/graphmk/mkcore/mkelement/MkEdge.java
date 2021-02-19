// 
// Decompiled by Procyon v0.5.36
// 

package io.github.graphmk.mkcore.mkelement;

import com.tinkerpop.blueprints.util.StringFactory;
import com.tinkerpop.blueprints.util.ExceptionFactory;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import java.io.Serializable;
import com.tinkerpop.blueprints.Edge;

class MkEdge extends MkElement implements Edge, Serializable
{
    private final String label;
    private final Vertex inVertex;
    private final Vertex outVertex;
    
    protected MkEdge(final String id, final Vertex outVertex, final Vertex inVertex, final String label) {
        super(id);
        this.label = label;
        this.outVertex = outVertex;
        this.inVertex = inVertex;
    }
    
    @Override
    public String getLabel() {
        return this.label;
    }
    
    @Override
    public Vertex getVertex(final Direction direction) throws IllegalArgumentException {
        if (direction.equals(Direction.IN)) {
            return this.inVertex;
        }
        if (direction.equals(Direction.OUT)) {
            return this.outVertex;
        }
        throw ExceptionFactory.bothIsNotSupported();
    }
    
    @Override
    public String toString() {
        return StringFactory.edgeString(this);
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (this.outVertex.getId().equals(((MkEdge)o).outVertex.getId().toString()) && this.inVertex.getId().equals(((MkEdge)o).inVertex.getId().toString()));
    }
}
