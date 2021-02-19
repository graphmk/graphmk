// 
// Decompiled by Procyon v0.5.36
// 

package io.github.graphmk.mkcore;

import io.github.graphmk.mkcore.interfaces.Vertex;
import io.github.graphmk.mkcore.mkelement.MkVertex;

import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Map;

public class VertexOptMk extends MkVertex
{
    private static final long serialVersionUID = 1L;
    private Map<String, Integer> bitMapIndex;
    private BitSet single_bitmap;
    private ArrayList<BitSet> bitmaps;
    
    public Map<String, Integer> getBMPIndex() {
        return this.bitMapIndex;
    }
    
    public BitSet getBitMap() {
        return null;
    }
    
    public ArrayList<BitSet> getBitmaps() {
        return this.bitmaps;
    }
    
    public void initBitMaps(final int size) {
        this.bitmaps = new ArrayList<BitSet>(size);
        this.bitMapIndex = new LinkedHashMap<String, Integer>();
    }
    
    public VertexOptMk(final Object id, final MkGraph g) {
        super(id, g);
    }
    
    public VertexOptMk() {
    }
    
    public void setBitAtIndexHashM(final int indxToUnset, final Vertex v) {
        if (this.bitMapIndex.get(v.toString()) != indxToUnset) {
            this.bitmaps.get(this.bitMapIndex.get(v.toString())).flip(indxToUnset);
        }
    }
    
    public void removeBitMapFor(final Vertex v) {
        this.bitMapIndex.remove(v.toString());
    }
    
    public void rebuildBMPIndex2() {
        final ArrayList<BitSet> newBMPs = new ArrayList<BitSet>();
        for (final String s : this.bitMapIndex.keySet()) {
            newBMPs.add(this.bitmaps.get(this.bitMapIndex.get(s)));
        }
        int i = 0;
        for (final String s2 : this.bitMapIndex.keySet()) {
            this.bitMapIndex.put(s2, i++);
        }
        this.bitmaps = newBMPs;
    }
    
    public void rebuildBMPIndex3() {
        final ArrayList<BitSet> newBMPs = new ArrayList<BitSet>();
        for (final String s : this.bitMapIndex.keySet()) {
            final BitSet newBMP = new BitSet();
            final BitSet oldBMP = this.bitmaps.get(this.bitMapIndex.get(s));
            int j = 0;
            for (final Integer i : this.bitMapIndex.values()) {
                newBMP.set(j++, oldBMP.get(i));
            }
            newBMPs.add(newBMP);
        }
        int k = 0;
        for (final String s2 : this.bitMapIndex.keySet()) {
            this.bitMapIndex.put(s2, k++);
        }
        this.bitmaps = newBMPs;
    }
    
    public void setBMPIndex(final Map<String, Integer> newIndex) {
        this.bitMapIndex = newIndex;
    }
    
    public void setBitMap(final BitSet bmp) {
        this.single_bitmap = bmp;
    }
}
