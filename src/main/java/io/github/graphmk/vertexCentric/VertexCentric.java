// 
// Decompiled by Procyon v0.5.36
// 

package io.github.graphmk.vertexCentric;

import io.github.graphmk.mkcore.interfaces.Graph;
import io.github.graphmk.mkcore.interfaces.Vertex;
import io.github.graphmk.mkcore.mkelement.MkVertex;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.ArrayList;

public class VertexCentric
{
    private Graph graph;
    public int spreadOnto;
    public boolean hault;
    private Integer superstep;
    private Executor ex;
    private int numOfVertices;
    public int numOfActiveVertices;
    
    public int getNumOfVertices() {
        return this.numOfVertices;
    }
    
    public void setNumOfVertices(final int numOfVertices) {
        this.numOfVertices = numOfVertices;
    }
    
    public VertexCentric(final Graph g, final int spreadOnto) {
        this.hault = false;
        this.superstep = 0;
        this.graph = g;
        this.spreadOnto = spreadOnto;
    }
    
    public VertexCentric(final Graph g) {
        this.hault = false;
        this.superstep = 0;
        this.graph = g;
        this.spreadOnto = Runtime.getRuntime().availableProcessors();
    }
    
    public void run(final Executor ex) throws InterruptedException {
        this.ex = ex;
        final List<Vertex> vertices = this.graph.getVertices();
        this.numOfActiveVertices = vertices.size();
        this.numOfVertices = vertices.size();
        final int eachThread = vertices.size() / this.spreadOnto;
        int at = 0;
        final ArrayList<Thread> threads = new ArrayList<Thread>();
        int numThreads = this.spreadOnto;
        final int k = vertices.size() % this.spreadOnto;
        if (k != 0) {
            ++numThreads;
        }
        final Runnable barrierAction = new BarrierAction(this);
        final CyclicBarrier barrier = new CyclicBarrier(numThreads, barrierAction);
        for (int i = 0; i < numThreads - 1; ++i) {
            final List<Vertex> l = vertices.subList(at, at + eachThread);
            at += eachThread;
            final Thread t = new Thread(new ThreadExecutor(ex, l, this, barrier));
            threads.add(t);
        }
        if (at != vertices.size()) {
            final List<Vertex> j = vertices.subList(at, vertices.size());
            final Thread t2 = new Thread(new ThreadExecutor(ex, j, this, barrier));
            threads.add(t2);
        }
        final Iterator<Thread> iterator = threads.iterator();
        while (iterator.hasNext()) {
            final Thread t2 = iterator.next();
            t2.start();
        }
        final Iterator<Thread> iterator2 = threads.iterator();
        while (iterator2.hasNext()) {
            final Thread t2 = iterator2.next();
            t2.join();
        }
    }
    
    public void setHault(final boolean hault) {
        this.hault = hault;
    }
    
    public boolean getHault() {
        return this.hault;
    }
    
    public Integer getSuperstep() {
        return this.superstep;
    }
    
    public void incrementSuperstep() {
        final Integer superstep = this.superstep;
        ++this.superstep;
    }
    
    public synchronized void decNumOfActiveVertices() {
        --this.numOfActiveVertices;
    }
    
    public synchronized int getNumOfActiveVertices() {
        return this.numOfActiveVertices;
    }
    
    public void voteToHalt(final Vertex v) {
        v.voteToHalt();
        this.decNumOfActiveVertices();
    }
    
    public void setSuperstep(final int num) {
        this.superstep = num;
    }
    
    public Graph getGraph() {
        return this.graph;
    }
    
    public void setNewValues() {
        for (final Vertex v : this.graph.getVertices()) {
            ((MkVertex)v).terminateSuperstep(this.ex.getName());
        }
    }
    
    public void clearValues() {
        for (final Vertex v : this.graph.getVertices()) {
            ((MkVertex)v).setVal(null);
            ((MkVertex)v).setPrevVal(null);
            ((MkVertex)v).wake();
        }
    }
}
