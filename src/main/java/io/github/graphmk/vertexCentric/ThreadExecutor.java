// 
// Decompiled by Procyon v0.5.36
// 

package io.github.graphmk.vertexCentric;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import io.github.graphmk.mkcore.interfaces.Vertex;
import java.util.List;

public class ThreadExecutor implements Runnable
{
    private List<Vertex> toExecute;
    private Executor ex;
    private VertexCentric context;
    private CyclicBarrier barrier;
    
    public ThreadExecutor(final Executor ex, final List<Vertex> toExecute, final VertexCentric context, final CyclicBarrier barrier) {
        this.toExecute = toExecute;
        this.ex = ex;
        this.context = context;
        this.barrier = barrier;
    }
    
    @Override
    public void run() {
        try {
            for (final Vertex v : this.toExecute) {
                this.ex.compute(v, this.context);
            }
            this.barrier.await();
            while (!this.context.getHault()) {
                for (final Vertex v : this.toExecute) {
                    if (!v.isHalted()) {
                        this.ex.compute(v, this.context);
                    }
                }
                this.barrier.await();
            }
        }
        catch (InterruptedException | BrokenBarrierException ex2) {
            final Exception ex = null;
            final Exception e = ex;
            e.printStackTrace();
        }
    }
}
