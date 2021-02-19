// 
// Decompiled by Procyon v0.5.36
// 

package com.graphmk.parser;

import org.antlr.v4.runtime.TokenStream;

public class TranslatorListener extends DatalogBaseListener
{
    DatalogParser parser;
    String result;
    
    public TranslatorListener(final DatalogParser parser) {
        this.parser = parser;
    }
    
    @Override
    public void enterLh_atom(final DatalogParser.Lh_atomContext ctx) {
        final TokenStream tokens = this.parser.getTokenStream();
        System.out.println(ctx.NODES());
        System.out.println(ctx.EDGES());
        final String pars = tokens.getText(ctx.parameters());
        System.out.println(pars);
        int i = 0;
        while (ctx.parameters().getChild(i) != null) {
            System.out.println(ctx.parameters().getChild(i++));
        }
        this.result += "YO";
    }
}
