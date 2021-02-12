// 
// Decompiled by Procyon v0.5.36
// 

package com.heftdb.parser;

import org.antlr.v4.runtime.InputMismatchException;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.DefaultErrorStrategy;

public class BailErrorStrategy extends DefaultErrorStrategy
{
    @Override
    public void recover(final Parser recognizer, final RecognitionException e) {
        throw new RuntimeException(e);
    }
    
    @Override
    public Token recoverInline(final Parser recognizer) throws RecognitionException {
        throw new RuntimeException(new InputMismatchException(recognizer));
    }
    
    @Override
    public void sync(final Parser recognizer) {
    }
}
