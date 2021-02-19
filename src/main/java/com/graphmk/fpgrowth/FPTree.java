// 
// Decompiled by Procyon v0.5.36
// 

package com.graphmk.fpgrowth;

import com.tinkerpop.blueprints.Vertex;
import java.util.LinkedList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

public class FPTree<T>
{
    private Node<T> root;
    Map<String, Node<T>> headerTable;
    private int numPaths;
    
    public FPTree(final T data) {
        this.setRoot(new Node<T>(data));
        this.headerTable = new LinkedHashMap<String, Node<T>>();
        this.numPaths = 1;
    }
    
    public Node<T> getRoot() {
        return this.root;
    }
    
    public void setRoot(final Node<T> root) {
        this.root = root;
    }
    
    public void addPath(final List<T> list) {
        Node<T> travel = this.root;
        for (final T v : list) {
            if (((Node<Object>)travel).children.contains(v)) {
                final int indx = ((Node<Object>)travel).children.indexOf(v);
                final Node<T> nxt = (Node<T>) ((Node<Object>)travel).children.get(indx);
                ((Node<Object>)nxt).count++;
                travel = nxt;
            }
            else {
                final Node<T> n = new Node<T>(v);
                if (!this.headerTable.containsKey(v.toString())) {
                    this.headerTable.put(v.toString(), n);
                }
                else {
                    final Node<T> toLink = this.headerTable.get(((Node<Object>)n).data.toString());
                    this.connectNodeLink(n, toLink);
                }
                ((Node<Object>)n).count++;
                travel.addChild(n);
                travel = n;
            }
        }
    }
    
    public void addCondPatt(final List<Node<T>> cp) {
        Node<T> travel = this.root;
        for (final Node<T> v : cp) {
            if (((Node<Object>)travel).children.contains(v)) {
                final int indx = ((Node<Object>)travel).children.indexOf(v);
                final Node<Object> node;
                final Node<T> nxt = (Node<T>)(node = ((Node<Object>)travel).children.get(indx));
                node.count += ((Node<Object>)v).count;
                travel = nxt;
            }
            else {
                final Node<T> n = v;
                if (!this.headerTable.containsKey(((Node<Object>)v).data.toString())) {
                    this.headerTable.put(((Node<Object>)v).data.toString(), n);
                }
                else {
                    final Node<T> toLink = this.headerTable.get(((Node<Object>)n).data.toString());
                    this.connectNodeLink(n, toLink);
                }
                travel.addChild(n);
                travel = n;
            }
        }
    }
    
    public int getNumPaths() {
        return this.numPaths;
    }
    
    public void setNumPaths(final int numPaths) {
        this.numPaths = numPaths;
    }
    
    private List<List<Node<T>>> getCondFPaths(final List<List<Node<T>>> condPB, final int minSupp) {
        final Map<String, Integer> freqItems = new HashMap<String, Integer>();
        final List<List<Node<T>>> freqItemSets = new ArrayList<List<Node<T>>>();
        for (final List<Node<T>> p : condPB) {
            final List<Node<T>> toAdd = new ArrayList<Node<T>>();
            for (final Node<T> n : p) {
                toAdd.add(n);
                final String vId = ((Node<Object>)n).data.toString();
                if (freqItems.containsKey(vId)) {
                    freqItems.put(vId, freqItems.get(vId) + ((Node<Object>)n).count);
                }
                else {
                    freqItems.put(vId, ((Node<Object>)n).count);
                }
            }
            freqItemSets.add(toAdd);
        }
        final Comparator freqItemsComparator = new Comparator() {
            @Override
            public int compare(final Object o1, final Object o2) {
                int retVal = freqItems.get(((Node<Object>)o2).data.toString()) - freqItems.get(((Node<Object>)o1).data.toString());
                if (retVal == 0) {
                    retVal = -((Node<Object>)o2).data.toString().compareTo(((Node)o1).toString());
                }
                return retVal;
            }
        };
        for (final List<Node<T>> vl : freqItemSets) {
            Collections.sort(vl, freqItemsComparator);
        }
        for (final List<Node<T>> p2 : freqItemSets) {
            for (int i = p2.size() - 1; i >= 0; --i) {
                final String vId2 = ((Node<Object>)p2.get(i)).data.toString();
                final int sup = freqItems.get(vId2);
                if (sup >= minSupp) {
                    break;
                }
                p2.remove(i);
            }
        }
        return freqItemSets;
    }
    
    FPTree<T> builCondFPTree(final List<List<Node<T>>> condPB, final int minSupp) {
        final List<List<Node<T>>> paths = this.getCondFPaths(condPB, minSupp);
        for (final List<Node<T>> pth : paths) {
            this.addCondPatt(pth);
        }
        final List<String> toRem = new ArrayList<String>();
        for (final String s : this.headerTable.keySet()) {
            for (Node<T> travel = this.headerTable.get(s); travel != null && travel.nodeLink != null; travel = travel.nodeLink) {
                if (((Node<Object>)travel.nodeLink).count < minSupp) {
                    travel.nodeLink.getParent().removeChild(travel.nodeLink);
                    travel.nodeLink = travel.nodeLink.nodeLink;
                }
            }
        }
        for (final String s : this.headerTable.keySet()) {
            if (((Node<Object>)this.headerTable.get(s)).count < minSupp) {
                toRem.add(s);
            }
        }
        for (final String s : toRem) {
            final Node<T> n = this.headerTable.get(s);
            if (n != null) {
                this.headerTable.put(s, n.nodeLink);
                n.getParent().removeChild(n);
            }
        }
        toRem.clear();
        for (final String s : this.headerTable.keySet()) {
            if (this.headerTable.get(s) == null) {
                toRem.add(s);
            }
        }
        for (final String s : toRem) {
            if (this.headerTable.get(s) == null) {
                this.headerTable.remove(s);
            }
        }
        final List<String> list = new ArrayList<String>(this.headerTable.keySet());
        for (int i = list.size() - 1; i >= 0; --i) {
            if (this.headerTable.get(list.get(i)) != null) {
                if (this.headerTable.get(list.get(i)).getChildren().size() > 1) {
                    ++this.numPaths;
                }
            }
        }
        return this;
    }
    
    public List<Pattern<T>> generateFPs(final FPTree<T> tree, final T forNode, final int minsupp) {
        final List<Pattern<T>> allPatts = new ArrayList<Pattern<T>>();
        final List<String> list = new ArrayList<String>(tree.headerTable.keySet());
        final Pattern<T> thePattern = new Pattern<T>();
        thePattern.getP().add(forNode);
        int startingNode = 0;
        for (int i = list.size() - 1; i >= 0; --i) {
            if (tree.headerTable.get(list.get(i)) != null) {
                if (((Node<Object>)tree.headerTable.get(list.get(i))).count >= minsupp) {
                    startingNode = i;
                    break;
                }
            }
        }
        if (list.size() - 1 >= startingNode) {
            final List<Pattern<T>> patterns = new ArrayList<Pattern<T>>();
            thePattern.setCount(((Node<Object>)tree.root.getChildren().get(0)).count);
            this.mine(thePattern, tree.headerTable.get(list.get(startingNode)), patterns, minsupp);
            allPatts.addAll(patterns);
        }
        return allPatts;
    }
    
    private void mine(final Pattern<T> thePattern, final Node<T> travel, final List<Pattern<T>> currList, final int minsupp) {
        final Pattern<T> toAdd = new Pattern<T>();
        if (((Node<Object>)travel).count >= minsupp) {
            toAdd.getP().addAll((Collection<? extends T>)thePattern.getP());
            toAdd.getP().add((T)((Node<Object>)travel).data);
            if (((Node<Object>)travel).count < ((Pattern<Object>)thePattern).count) {
                toAdd.setCount(((Node<Object>)travel).count);
            }
            else {
                toAdd.setCount(((Pattern<Object>)thePattern).count);
            }
            if (toAdd.getP().size() > 1) {
                currList.add(toAdd);
            }
        }
        if (!((Node<Object>)travel.getParent()).data.toString().equals("null") && thePattern.getP().size() <= 1) {
            this.mine(thePattern, travel.getParent(), currList, minsupp);
        }
        if (!((Node<Object>)travel.getParent()).data.toString().equals("null") && toAdd.getP().size() <= 1) {
            this.mine(toAdd, travel.getParent(), currList, minsupp);
        }
    }
    
    public Map<String, Node<T>> getHeaderTable() {
        return this.headerTable;
    }
    
    public void setHeaderTable(final Map<String, Node<T>> headerTable) {
        this.headerTable = headerTable;
    }
    
    private void connectNodeLink(final Node<T> n, Node<T> travel) {
        if (travel.nodeLink == null) {
            travel.nodeLink = n;
        }
        else {
            travel = travel.nodeLink;
            this.connectNodeLink(n, travel);
        }
    }
    
    @Override
    public String toString() {
        return toStringTree(this.root);
    }
    
    public static String toStringTree(final Node node) {
        final StringBuilder buffer = new StringBuilder();
        return toStringTreeHelper(node, buffer, new LinkedList<Iterator<Node>>()).toString();
    }
    
    private static String toStringTreeDrawLines(final List<Iterator<Node>> parentIterators, final boolean amLast) {
        final StringBuilder result = new StringBuilder();
        final Iterator<Iterator<Node>> it = parentIterators.iterator();
        while (it.hasNext()) {
            final Iterator<Node> anIt = it.next();
            if (anIt.hasNext() || (!it.hasNext() && amLast)) {
                result.append("   |");
            }
            else {
                result.append("    ");
            }
        }
        return result.toString();
    }
    
    private static StringBuilder toStringTreeHelper(final Node node, final StringBuilder buffer, final List<Iterator<Node>> parentIterators) {
        if (!parentIterators.isEmpty()) {
            final boolean amLast = !parentIterators.get(parentIterators.size() - 1).hasNext();
            buffer.append("\n");
            final String lines = toStringTreeDrawLines(parentIterators, amLast);
            buffer.append(lines);
            buffer.append("\n");
            buffer.append(lines);
            buffer.append("- ");
        }
        buffer.append(node.toString());
        if (!node.getChildren().isEmpty()) {
            final Iterator<Node> it = (Iterator<Node>)node.getChildren().iterator();
            parentIterators.add(it);
            while (it.hasNext()) {
                final Node child = it.next();
                toStringTreeHelper(child, buffer, parentIterators);
            }
            parentIterators.remove(it);
        }
        return buffer;
    }
    
    public static class Pattern<T>
    {
        private List<T> p;
        private int count;
        
        public Pattern() {
            this.p = new ArrayList<T>();
            this.count = 0;
        }
        
        public Pattern(final List<T> l, final int count) {
            this.count = count;
            this.p = l;
        }
        
        @Override
        public String toString() {
            return this.p.toString() + ":" + this.count;
        }
        
        public List<T> getP() {
            return this.p;
        }
        
        public void setP(final List<T> p) {
            this.p = p;
        }
        
        public int getCount() {
            return this.count;
        }
        
        public void setCount(final int count) {
            this.count = count;
        }
    }
    
    public static class Node<T>
    {
        private T data;
        private int count;
        private Node<T> parent;
        private List<Node<T>> children;
        Node<T> nodeLink;
        
        public Node<T> getNodeLink() {
            return this.nodeLink;
        }
        
        public void setNodeLink(final Node<T> nodeLink) {
            this.nodeLink = nodeLink;
        }
        
        public Node(final T data) {
            this.data = data;
            this.children = new ArrayList<Node<T>>();
        }
        
        public Node(final Node<T> n) {
            this.data = n.data;
        }
        
        public T getData() {
            return this.data;
        }
        
        public void addChild(final Node<T> n) {
            n.setParent(this);
            this.children.add(n);
        }
        
        public void removeChild(final Node<T> n) {
            this.children.remove(n);
        }
        
        public List<Node<T>> getChildren() {
            return this.children;
        }
        
        List<Node<T>> getCondPath() {
            final List<Node<T>> toRet = this.getCondPathHelper(this, this, new ArrayList<Node<T>>());
            Collections.reverse(toRet);
            return toRet;
        }
        
        private List<Node<T>> getCondPathHelper(final Node<T> node, final Node<T> travel, final List<Node<T>> list) {
            if (travel.getParent().data.toString().equals("null")) {
                return list;
            }
            final Node<T> n = new Node<T>(travel.getParent().data);
            n.count = node.count;
            list.add(n);
            return this.getCondPathHelper(node, travel.getParent(), list);
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o instanceof Vertex) {
                return this.data.equals(o);
            }
            return this.data.equals(((Node)o).data);
        }
        
        @Override
        public String toString() {
            return this.data + ":" + this.count + "->" + this.nodeLink;
        }
        
        public Node<T> getParent() {
            return this.parent;
        }
        
        public void setParent(final Node<T> parent) {
            this.parent = parent;
        }
    }
}
