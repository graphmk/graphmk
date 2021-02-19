// 
// Decompiled by Procyon v0.5.36
// 

package com.graphmk.fpgrowth;

import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;
import com.graphmk.mkcore.GraphOptMk;
import com.graphmk.mkcore.mkelement.MkVertex;
import java.util.HashMap;
import com.graphmk.mkcore.interfaces.Vertex;
import java.util.List;
import java.util.Map;

public class FPGrowth<T>
{
    private Map<String, List<Vertex>> freqItemSets;
    private FPTree<Vertex> fptree;
    
    public FPGrowth() {
        this.freqItemSets = new HashMap<String, List<Vertex>>();
        this.fptree = new FPTree<Vertex>(new MkVertex("null", null));
    }
    
    public Map<String, List<Vertex>> CollectAndSort(final GraphOptMk g, final int minSupport) {
        final Map<String, Integer> freqItems = new HashMap<String, Integer>();
        for (final String s : g.getVirtualIndex().keySet()) {
            final ArrayList<Vertex> l = new ArrayList<Vertex>(g.getInVertices().get(g.index.get(s)));
            this.freqItemSets.put(s, l);
            for (final Vertex v : l) {
                final String vId = v.toString();
                if (freqItems.containsKey(vId)) {
                    freqItems.put(vId, freqItems.get(vId) + 1);
                }
                else {
                    freqItems.put(vId, 1);
                }
            }
        }
        final Comparator<Vertex> freqItemsComparator = new Comparator<Vertex>() {
            @Override
            public int compare(final Vertex o1, final Vertex o2) {
                int retVal = freqItems.get(o2.toString()) - freqItems.get(o1.toString());
                if (retVal == 0) {
                    retVal = -o2.toString().compareTo(o1.toString());
                }
                return retVal;
            }
        };
        for (final List<Vertex> vl : this.freqItemSets.values()) {
            Collections.sort(vl, freqItemsComparator);
        }
        for (final List<Vertex> p : this.freqItemSets.values()) {
            for (int i = p.size() - 1; i >= 0; --i) {
                final Vertex v = p.get(i);
                final String vId = v.toString();
                final int sup = freqItems.get(vId);
                if (sup >= minSupport) {
                    break;
                }
                p.remove(i);
            }
        }
        return this.freqItemSets;
    }
    
    public void generateTree() {
        if (!this.freqItemSets.isEmpty()) {
            System.out.println("Size of the frequent Item sets " + this.freqItemSets.size());
            for (final String s : this.freqItemSets.keySet()) {
                this.fptree.addPath(this.freqItemSets.get(s));
            }
        }
    }
    
    public FPTree<Vertex> getFptree() {
        return this.fptree;
    }
    
    public void setFptree(final FPTree<Vertex> fptree) {
        this.fptree = fptree;
    }
    
    public List<FPTree.Pattern<T>> FreqPGrowth(final FPTree<T> tree, final T forNode, final int minSupp, final int pattSize) {
        final List<FPTree.Pattern<T>> allPatterns = new ArrayList<FPTree.Pattern<T>>();
        if (tree.getNumPaths() == 1 && forNode != null && tree.getRoot().getChildren().size() > 0) {
            if (pattSize > 2) {
                for (final FPTree.Pattern<T> p : tree.generateFPs(tree, forNode, minSupp)) {
                    if (p.getP().size() == pattSize) {
                        allPatterns.add(p);
                    }
                }
            }
            else {
                allPatterns.addAll(tree.generateFPs(tree, forNode, minSupp));
            }
        }
        else {
            final List<List<FPTree.Node<T>>> condPB = new ArrayList<List<FPTree.Node<T>>>();
            final List<String> list = new ArrayList<String>(tree.headerTable.keySet());
            for (int i = list.size() - 1; i >= 0; --i) {
                final String v = list.get(i);
                for (FPTree.Node<T> travel = tree.headerTable.get(v); travel != null; travel = travel.nodeLink) {
                    final List<FPTree.Node<T>> m = travel.getCondPath();
                    condPB.add(m);
                }
                final FPTree<T> tr = new FPTree<T>((T)new MkVertex("null", null));
                tr.builCondFPTree(condPB, minSupp);
                condPB.clear();
                if (tr.headerTable.size() > 0) {
                    if (pattSize > 2) {
                        for (final FPTree.Pattern<T> p2 : this.FreqPGrowth(tr, tree.headerTable.get(v).getData(), minSupp, pattSize)) {
                            if (p2.getP().size() == pattSize) {
                                allPatterns.add(p2);
                            }
                        }
                    }
                    else {
                        allPatterns.addAll(this.FreqPGrowth(tr, tree.headerTable.get(v).getData(), minSupp, pattSize));
                    }
                }
            }
        }
        return allPatterns;
    }
}
