// 
// Decompiled by Procyon v0.5.36
// 

package io.github.graphmk.vertexCentric;

public class BarrierAction implements Runnable
{
    VertexCentric context;
    
    public BarrierAction(final VertexCentric p) {
        this.context = p;
    }
    
    @Override
    public void run() {
        this.context.setNewValues();
        this.context.incrementSuperstep();
        if (this.context.numOfActiveVertices == 0) {
            this.context.setHault(true);
            this.context.clearValues();
        }
    }
}
