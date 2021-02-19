// 
// Decompiled by Procyon v0.5.36
// 

package io.github.graphmk.vertexCentric;

import io.github.graphmk.mkcore.interfaces.Vertex;

public abstract class  Executor
{
    private String name;
    
    public Executor(final String name) {
        this.name = name;
    }
    
    public abstract void compute(final Vertex p0, final VertexCentric p1);
    
    public String getName() {
        return this.name;
    }
}
