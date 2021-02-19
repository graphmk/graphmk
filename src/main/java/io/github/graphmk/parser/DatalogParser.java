// 
// Decompiled by Procyon v0.5.36
// 

package io.github.graphmk.parser;

import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import java.util.List;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.VocabularyImpl;
import org.antlr.v4.runtime.RuntimeMetaData;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.NoViableAltException;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.Parser;

public class DatalogParser extends Parser
{
    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache;
    public static final int T__0 = 1;
    public static final int LPAR = 2;
    public static final int RPAR = 3;
    public static final int SQUOTE = 4;
    public static final int CONDITION = 5;
    public static final int COLON_H = 6;
    public static final int END_STATEMENT = 7;
    public static final int LOOP = 8;
    public static final int NODES = 9;
    public static final int EDGES = 10;
    public static final int AGGR = 11;
    public static final int STRING_LITERAL = 12;
    public static final int ID = 13;
    public static final int NUM = 14;
    public static final int WS = 15;
    public static final int RULE_datalog = 0;
    public static final int RULE_lh_atom = 1;
    public static final int RULE_rh_atom = 2;
    public static final int RULE_statement = 3;
    public static final int RULE_loop_stmt = 4;
    public static final int RULE_value = 5;
    public static final int RULE_predicate = 6;
    public static final int RULE_parameters = 7;
    public static final int RULE_aggregate_expr = 8;
    public static final int RULE_agg_parameter = 9;
    public static final int RULE_agg_predicate = 10;
    public static final String[] ruleNames;
    private static final String[] _LITERAL_NAMES;
    private static final String[] _SYMBOLIC_NAMES;
    public static final Vocabulary VOCABULARY;
    @Deprecated
    public static final String[] tokenNames;
    public static final String _serializedATN = "\u0003\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\u0003\u0011`\u0004\u0002\t\u0002\u0004\u0003\t\u0003\u0004\u0004\t\u0004\u0004\u0005\t\u0005\u0004\u0006\t\u0006\u0004\u0007\t\u0007\u0004\b\t\b\u0004\t\t\t\u0004\n\t\n\u0004\u000b\t\u000b\u0004\f\t\f\u0003\u0002\u0003\u0002\u0006\u0002\u001b\n\u0002\r\u0002\u000e\u0002\u001c\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0004\u0003\u0004\u0003\u0004\u0003\u0005\u0003\u0005\u0003\u0005\u0003\u0005\u0003\u0005\u0003\u0005\u0005\u0005+\n\u0005\u0007\u0005-\n\u0005\f\u0005\u000e\u00050\u000b\u0005\u0003\u0005\u0003\u0005\u0003\u0005\u0005\u00055\n\u0005\u0007\u00057\n\u0005\f\u0005\u000e\u0005:\u000b\u0005\u0003\u0005\u0003\u0005\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0007\u0003\u0007\u0003\b\u0003\b\u0003\b\u0003\b\u0003\t\u0003\t\u0003\t\u0003\t\u0007\tL\n\t\f\t\u000e\tO\u000b\t\u0003\t\u0003\t\u0003\n\u0003\n\u0003\n\u0003\n\u0003\n\u0003\u000b\u0003\u000b\u0005\u000bZ\n\u000b\u0003\f\u0003\f\u0003\f\u0003\f\u0003\f\u0002\u0002\r\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0002\u0004\u0003\u0002\u000b\f\u0004\u0002\u000e\u000e\u0010\u0010\\\u0002\u001a\u0003\u0002\u0002\u0002\u0004\u001e\u0003\u0002\u0002\u0002\u0006!\u0003\u0002\u0002\u0002\b$\u0003\u0002\u0002\u0002\n=\u0003\u0002\u0002\u0002\fA\u0003\u0002\u0002\u0002\u000eC\u0003\u0002\u0002\u0002\u0010G\u0003\u0002\u0002\u0002\u0012R\u0003\u0002\u0002\u0002\u0014Y\u0003\u0002\u0002\u0002\u0016[\u0003\u0002\u0002\u0002\u0018\u001b\u0005\b\u0005\u0002\u0019\u001b\u0005\n\u0006\u0002\u001a\u0018\u0003\u0002\u0002\u0002\u001a\u0019\u0003\u0002\u0002\u0002\u001b\u001c\u0003\u0002\u0002\u0002\u001c\u001a\u0003\u0002\u0002\u0002\u001c\u001d\u0003\u0002\u0002\u0002\u001d\u0003\u0003\u0002\u0002\u0002\u001e\u001f\t\u0002\u0002\u0002\u001f \u0005\u0010\t\u0002 \u0005\u0003\u0002\u0002\u0002!\"\u0007\u000f\u0002\u0002\"#\u0005\u0010\t\u0002#\u0007\u0003\u0002\u0002\u0002$%\u0005\u0004\u0003\u0002%&\u0007\b\u0002\u0002&.\u0005\u0006\u0004\u0002'*\u0007\u0003\u0002\u0002(+\u0005\u0006\u0004\u0002)+\u0005\u0004\u0003\u0002*(\u0003\u0002\u0002\u0002*)\u0003\u0002\u0002\u0002+-\u0003\u0002\u0002\u0002,'\u0003\u0002\u0002\u0002-0\u0003\u0002\u0002\u0002.,\u0003\u0002\u0002\u0002./\u0003\u0002\u0002\u0002/8\u0003\u0002\u0002\u00020.\u0003\u0002\u0002\u000214\u0007\u0003\u0002\u000225\u0005\u000e\b\u000235\u0005\u0016\f\u000242\u0003\u0002\u0002\u000243\u0003\u0002\u0002\u000257\u0003\u0002\u0002\u000261\u0003\u0002\u0002\u00027:\u0003\u0002\u0002\u000286\u0003\u0002\u0002\u000289\u0003\u0002\u0002\u00029;\u0003\u0002\u0002\u0002:8\u0003\u0002\u0002\u0002;<\u0007\t\u0002\u0002<\t\u0003\u0002\u0002\u0002=>\u0007\n\u0002\u0002>?\u0005\u0006\u0004\u0002?@\u0007\t\u0002\u0002@\u000b\u0003\u0002\u0002\u0002AB\t\u0003\u0002\u0002B\r\u0003\u0002\u0002\u0002CD\u0007\u000f\u0002\u0002DE\u0007\u0007\u0002\u0002EF\u0005\f\u0007\u0002F\u000f\u0003\u0002\u0002\u0002GH\u0007\u0004\u0002\u0002HM\u0007\u000f\u0002\u0002IJ\u0007\u0003\u0002\u0002JL\u0007\u000f\u0002\u0002KI\u0003\u0002\u0002\u0002LO\u0003\u0002\u0002\u0002MK\u0003\u0002\u0002\u0002MN\u0003\u0002\u0002\u0002NP\u0003\u0002\u0002\u0002OM\u0003\u0002\u0002\u0002PQ\u0007\u0005\u0002\u0002Q\u0011\u0003\u0002\u0002\u0002RS\u0007\r\u0002\u0002ST\u0007\u0004\u0002\u0002TU\u0005\u0014\u000b\u0002UV\u0007\u0005\u0002\u0002V\u0013\u0003\u0002\u0002\u0002WZ\u0007\u000f\u0002\u0002XZ\u0005\u0006\u0004\u0002YW\u0003\u0002\u0002\u0002YX\u0003\u0002\u0002\u0002Z\u0015\u0003\u0002\u0002\u0002[\\\u0005\u0012\n\u0002\\]\u0007\u0007\u0002\u0002]^\u0005\f\u0007\u0002^\u0017\u0003\u0002\u0002\u0002\n\u001a\u001c*.48MY";
    public static final ATN _ATN;
    
    @Deprecated
    @Override
    public String[] getTokenNames() {
        return DatalogParser.tokenNames;
    }
    
    @Override
    public Vocabulary getVocabulary() {
        return DatalogParser.VOCABULARY;
    }
    
    @Override
    public String getGrammarFileName() {
        return "Datalog.g4";
    }
    
    @Override
    public String[] getRuleNames() {
        return DatalogParser.ruleNames;
    }
    
    @Override
    public String getSerializedATN() {
        return "\u0003\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\u0003\u0011`\u0004\u0002\t\u0002\u0004\u0003\t\u0003\u0004\u0004\t\u0004\u0004\u0005\t\u0005\u0004\u0006\t\u0006\u0004\u0007\t\u0007\u0004\b\t\b\u0004\t\t\t\u0004\n\t\n\u0004\u000b\t\u000b\u0004\f\t\f\u0003\u0002\u0003\u0002\u0006\u0002\u001b\n\u0002\r\u0002\u000e\u0002\u001c\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0004\u0003\u0004\u0003\u0004\u0003\u0005\u0003\u0005\u0003\u0005\u0003\u0005\u0003\u0005\u0003\u0005\u0005\u0005+\n\u0005\u0007\u0005-\n\u0005\f\u0005\u000e\u00050\u000b\u0005\u0003\u0005\u0003\u0005\u0003\u0005\u0005\u00055\n\u0005\u0007\u00057\n\u0005\f\u0005\u000e\u0005:\u000b\u0005\u0003\u0005\u0003\u0005\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0007\u0003\u0007\u0003\b\u0003\b\u0003\b\u0003\b\u0003\t\u0003\t\u0003\t\u0003\t\u0007\tL\n\t\f\t\u000e\tO\u000b\t\u0003\t\u0003\t\u0003\n\u0003\n\u0003\n\u0003\n\u0003\n\u0003\u000b\u0003\u000b\u0005\u000bZ\n\u000b\u0003\f\u0003\f\u0003\f\u0003\f\u0003\f\u0002\u0002\r\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0002\u0004\u0003\u0002\u000b\f\u0004\u0002\u000e\u000e\u0010\u0010\\\u0002\u001a\u0003\u0002\u0002\u0002\u0004\u001e\u0003\u0002\u0002\u0002\u0006!\u0003\u0002\u0002\u0002\b$\u0003\u0002\u0002\u0002\n=\u0003\u0002\u0002\u0002\fA\u0003\u0002\u0002\u0002\u000eC\u0003\u0002\u0002\u0002\u0010G\u0003\u0002\u0002\u0002\u0012R\u0003\u0002\u0002\u0002\u0014Y\u0003\u0002\u0002\u0002\u0016[\u0003\u0002\u0002\u0002\u0018\u001b\u0005\b\u0005\u0002\u0019\u001b\u0005\n\u0006\u0002\u001a\u0018\u0003\u0002\u0002\u0002\u001a\u0019\u0003\u0002\u0002\u0002\u001b\u001c\u0003\u0002\u0002\u0002\u001c\u001a\u0003\u0002\u0002\u0002\u001c\u001d\u0003\u0002\u0002\u0002\u001d\u0003\u0003\u0002\u0002\u0002\u001e\u001f\t\u0002\u0002\u0002\u001f \u0005\u0010\t\u0002 \u0005\u0003\u0002\u0002\u0002!\"\u0007\u000f\u0002\u0002\"#\u0005\u0010\t\u0002#\u0007\u0003\u0002\u0002\u0002$%\u0005\u0004\u0003\u0002%&\u0007\b\u0002\u0002&.\u0005\u0006\u0004\u0002'*\u0007\u0003\u0002\u0002(+\u0005\u0006\u0004\u0002)+\u0005\u0004\u0003\u0002*(\u0003\u0002\u0002\u0002*)\u0003\u0002\u0002\u0002+-\u0003\u0002\u0002\u0002,'\u0003\u0002\u0002\u0002-0\u0003\u0002\u0002\u0002.,\u0003\u0002\u0002\u0002./\u0003\u0002\u0002\u0002/8\u0003\u0002\u0002\u00020.\u0003\u0002\u0002\u000214\u0007\u0003\u0002\u000225\u0005\u000e\b\u000235\u0005\u0016\f\u000242\u0003\u0002\u0002\u000243\u0003\u0002\u0002\u000257\u0003\u0002\u0002\u000261\u0003\u0002\u0002\u00027:\u0003\u0002\u0002\u000286\u0003\u0002\u0002\u000289\u0003\u0002\u0002\u00029;\u0003\u0002\u0002\u0002:8\u0003\u0002\u0002\u0002;<\u0007\t\u0002\u0002<\t\u0003\u0002\u0002\u0002=>\u0007\n\u0002\u0002>?\u0005\u0006\u0004\u0002?@\u0007\t\u0002\u0002@\u000b\u0003\u0002\u0002\u0002AB\t\u0003\u0002\u0002B\r\u0003\u0002\u0002\u0002CD\u0007\u000f\u0002\u0002DE\u0007\u0007\u0002\u0002EF\u0005\f\u0007\u0002F\u000f\u0003\u0002\u0002\u0002GH\u0007\u0004\u0002\u0002HM\u0007\u000f\u0002\u0002IJ\u0007\u0003\u0002\u0002JL\u0007\u000f\u0002\u0002KI\u0003\u0002\u0002\u0002LO\u0003\u0002\u0002\u0002MK\u0003\u0002\u0002\u0002MN\u0003\u0002\u0002\u0002NP\u0003\u0002\u0002\u0002OM\u0003\u0002\u0002\u0002PQ\u0007\u0005\u0002\u0002Q\u0011\u0003\u0002\u0002\u0002RS\u0007\r\u0002\u0002ST\u0007\u0004\u0002\u0002TU\u0005\u0014\u000b\u0002UV\u0007\u0005\u0002\u0002V\u0013\u0003\u0002\u0002\u0002WZ\u0007\u000f\u0002\u0002XZ\u0005\u0006\u0004\u0002YW\u0003\u0002\u0002\u0002YX\u0003\u0002\u0002\u0002Z\u0015\u0003\u0002\u0002\u0002[\\\u0005\u0012\n\u0002\\]\u0007\u0007\u0002\u0002]^\u0005\f\u0007\u0002^\u0017\u0003\u0002\u0002\u0002\n\u001a\u001c*.48MY";
    }
    
    @Override
    public ATN getATN() {
        return DatalogParser._ATN;
    }
    
    public DatalogParser(final TokenStream input) {
        super(input);
        this._interp = new ParserATNSimulator(this, DatalogParser._ATN, DatalogParser._decisionToDFA, DatalogParser._sharedContextCache);
    }
    
    public final DatalogContext datalog() throws RecognitionException {
        final DatalogContext _localctx = new DatalogContext(this._ctx, this.getState());
        this.enterRule(_localctx, 0, 0);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(24);
            this._errHandler.sync(this);
            int _la = this._input.LA(1);
            do {
                this.setState(24);
                switch (this._input.LA(1)) {
                    case 9:
                    case 10: {
                        this.setState(22);
                        this.statement();
                        break;
                    }
                    case 8: {
                        this.setState(23);
                        this.loop_stmt();
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this);
                    }
                }
                this.setState(26);
                this._errHandler.sync(this);
                _la = this._input.LA(1);
            } while ((_la & 0xFFFFFFC0) == 0x0 && (1L << _la & 0x700L) != 0x0L);
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }
    
    public final Lh_atomContext lh_atom() throws RecognitionException {
        final Lh_atomContext _localctx = new Lh_atomContext(this._ctx, this.getState());
        this.enterRule(_localctx, 2, 1);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(28);
            final int _la = this._input.LA(1);
            if (_la != 9 && _la != 10) {
                this._errHandler.recoverInline(this);
            }
            else {
                this.consume();
            }
            this.setState(29);
            this.parameters();
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }
    
    public final Rh_atomContext rh_atom() throws RecognitionException {
        final Rh_atomContext _localctx = new Rh_atomContext(this._ctx, this.getState());
        this.enterRule(_localctx, 4, 2);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(31);
            this.match(13);
            this.setState(32);
            this.parameters();
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }
    
    public final StatementContext statement() throws RecognitionException {
        final StatementContext _localctx = new StatementContext(this._ctx, this.getState());
        this.enterRule(_localctx, 6, 3);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(34);
            this.lh_atom();
            this.setState(35);
            this.match(6);
            this.setState(36);
            this.rh_atom();
            this.setState(44);
            this._errHandler.sync(this);
            for (int _alt = this.getInterpreter().adaptivePredict(this._input, 3, this._ctx); _alt != 2 && _alt != 0; _alt = this.getInterpreter().adaptivePredict(this._input, 3, this._ctx)) {
                if (_alt == 1) {
                    this.setState(37);
                    this.match(1);
                    this.setState(40);
                    switch (this._input.LA(1)) {
                        case 13: {
                            this.setState(38);
                            this.rh_atom();
                            break;
                        }
                        case 9:
                        case 10: {
                            this.setState(39);
                            this.lh_atom();
                            break;
                        }
                        default: {
                            throw new NoViableAltException(this);
                        }
                    }
                }
                this.setState(46);
                this._errHandler.sync(this);
            }
            this.setState(54);
            this._errHandler.sync(this);
            for (int _la = this._input.LA(1); _la == 1; _la = this._input.LA(1)) {
                this.setState(47);
                this.match(1);
                this.setState(50);
                switch (this._input.LA(1)) {
                    case 13: {
                        this.setState(48);
                        this.predicate();
                        break;
                    }
                    case 11: {
                        this.setState(49);
                        this.agg_predicate();
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this);
                    }
                }
                this.setState(56);
                this._errHandler.sync(this);
            }
            this.setState(57);
            this.match(7);
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }
    
    public final Loop_stmtContext loop_stmt() throws RecognitionException {
        final Loop_stmtContext _localctx = new Loop_stmtContext(this._ctx, this.getState());
        this.enterRule(_localctx, 8, 4);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(59);
            this.match(8);
            this.setState(60);
            this.rh_atom();
            this.setState(61);
            this.match(7);
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }
    
    public final ValueContext value() throws RecognitionException {
        final ValueContext _localctx = new ValueContext(this._ctx, this.getState());
        this.enterRule(_localctx, 10, 5);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(63);
            final int _la = this._input.LA(1);
            if (_la != 12 && _la != 14) {
                this._errHandler.recoverInline(this);
            }
            else {
                this.consume();
            }
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }
    
    public final PredicateContext predicate() throws RecognitionException {
        final PredicateContext _localctx = new PredicateContext(this._ctx, this.getState());
        this.enterRule(_localctx, 12, 6);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(65);
            this.match(13);
            this.setState(66);
            this.match(5);
            this.setState(67);
            this.value();
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }
    
    public final ParametersContext parameters() throws RecognitionException {
        final ParametersContext _localctx = new ParametersContext(this._ctx, this.getState());
        this.enterRule(_localctx, 14, 7);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(69);
            this.match(2);
            this.setState(70);
            this.match(13);
            this.setState(75);
            this._errHandler.sync(this);
            for (int _la = this._input.LA(1); _la == 1; _la = this._input.LA(1)) {
                this.setState(71);
                this.match(1);
                this.setState(72);
                this.match(13);
                this.setState(77);
                this._errHandler.sync(this);
            }
            this.setState(78);
            this.match(3);
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }
    
    public final Aggregate_exprContext aggregate_expr() throws RecognitionException {
        final Aggregate_exprContext _localctx = new Aggregate_exprContext(this._ctx, this.getState());
        this.enterRule(_localctx, 16, 8);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(80);
            this.match(11);
            this.setState(81);
            this.match(2);
            this.setState(82);
            this.agg_parameter();
            this.setState(83);
            this.match(3);
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }
    
    public final Agg_parameterContext agg_parameter() throws RecognitionException {
        final Agg_parameterContext _localctx = new Agg_parameterContext(this._ctx, this.getState());
        this.enterRule(_localctx, 18, 9);
        try {
            this.setState(87);
            switch (this.getInterpreter().adaptivePredict(this._input, 7, this._ctx)) {
                case 1: {
                    this.enterOuterAlt(_localctx, 1);
                    this.setState(85);
                    this.match(13);
                    break;
                }
                case 2: {
                    this.enterOuterAlt(_localctx, 2);
                    this.setState(86);
                    this.rh_atom();
                    break;
                }
            }
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }
    
    public final Agg_predicateContext agg_predicate() throws RecognitionException {
        final Agg_predicateContext _localctx = new Agg_predicateContext(this._ctx, this.getState());
        this.enterRule(_localctx, 20, 10);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(89);
            this.aggregate_expr();
            this.setState(90);
            this.match(5);
            this.setState(91);
            this.value();
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }
    
    static {
        RuntimeMetaData.checkVersion("4.5", "4.5");
        _sharedContextCache = new PredictionContextCache();
        ruleNames = new String[] { "datalog", "lh_atom", "rh_atom", "statement", "loop_stmt", "value", "predicate", "parameters", "aggregate_expr", "agg_parameter", "agg_predicate" };
        _LITERAL_NAMES = new String[] { null, "','", "'('", "')'", "'''", null, "':-'", "'.'", "'For'", "'Nodes'", "'Edges'" };
        _SYMBOLIC_NAMES = new String[] { null, null, "LPAR", "RPAR", "SQUOTE", "CONDITION", "COLON_H", "END_STATEMENT", "LOOP", "NODES", "EDGES", "AGGR", "STRING_LITERAL", "ID", "NUM", "WS" };
        VOCABULARY = new VocabularyImpl(DatalogParser._LITERAL_NAMES, DatalogParser._SYMBOLIC_NAMES);
        tokenNames = new String[DatalogParser._SYMBOLIC_NAMES.length];
        for (int i = 0; i < DatalogParser.tokenNames.length; ++i) {
            DatalogParser.tokenNames[i] = DatalogParser.VOCABULARY.getLiteralName(i);
            if (DatalogParser.tokenNames[i] == null) {
                DatalogParser.tokenNames[i] = DatalogParser.VOCABULARY.getSymbolicName(i);
            }
            if (DatalogParser.tokenNames[i] == null) {
                DatalogParser.tokenNames[i] = "<INVALID>";
            }
        }
        _ATN = new ATNDeserializer().deserialize("\u0003\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\u0003\u0011`\u0004\u0002\t\u0002\u0004\u0003\t\u0003\u0004\u0004\t\u0004\u0004\u0005\t\u0005\u0004\u0006\t\u0006\u0004\u0007\t\u0007\u0004\b\t\b\u0004\t\t\t\u0004\n\t\n\u0004\u000b\t\u000b\u0004\f\t\f\u0003\u0002\u0003\u0002\u0006\u0002\u001b\n\u0002\r\u0002\u000e\u0002\u001c\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0004\u0003\u0004\u0003\u0004\u0003\u0005\u0003\u0005\u0003\u0005\u0003\u0005\u0003\u0005\u0003\u0005\u0005\u0005+\n\u0005\u0007\u0005-\n\u0005\f\u0005\u000e\u00050\u000b\u0005\u0003\u0005\u0003\u0005\u0003\u0005\u0005\u00055\n\u0005\u0007\u00057\n\u0005\f\u0005\u000e\u0005:\u000b\u0005\u0003\u0005\u0003\u0005\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0007\u0003\u0007\u0003\b\u0003\b\u0003\b\u0003\b\u0003\t\u0003\t\u0003\t\u0003\t\u0007\tL\n\t\f\t\u000e\tO\u000b\t\u0003\t\u0003\t\u0003\n\u0003\n\u0003\n\u0003\n\u0003\n\u0003\u000b\u0003\u000b\u0005\u000bZ\n\u000b\u0003\f\u0003\f\u0003\f\u0003\f\u0003\f\u0002\u0002\r\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0002\u0004\u0003\u0002\u000b\f\u0004\u0002\u000e\u000e\u0010\u0010\\\u0002\u001a\u0003\u0002\u0002\u0002\u0004\u001e\u0003\u0002\u0002\u0002\u0006!\u0003\u0002\u0002\u0002\b$\u0003\u0002\u0002\u0002\n=\u0003\u0002\u0002\u0002\fA\u0003\u0002\u0002\u0002\u000eC\u0003\u0002\u0002\u0002\u0010G\u0003\u0002\u0002\u0002\u0012R\u0003\u0002\u0002\u0002\u0014Y\u0003\u0002\u0002\u0002\u0016[\u0003\u0002\u0002\u0002\u0018\u001b\u0005\b\u0005\u0002\u0019\u001b\u0005\n\u0006\u0002\u001a\u0018\u0003\u0002\u0002\u0002\u001a\u0019\u0003\u0002\u0002\u0002\u001b\u001c\u0003\u0002\u0002\u0002\u001c\u001a\u0003\u0002\u0002\u0002\u001c\u001d\u0003\u0002\u0002\u0002\u001d\u0003\u0003\u0002\u0002\u0002\u001e\u001f\t\u0002\u0002\u0002\u001f \u0005\u0010\t\u0002 \u0005\u0003\u0002\u0002\u0002!\"\u0007\u000f\u0002\u0002\"#\u0005\u0010\t\u0002#\u0007\u0003\u0002\u0002\u0002$%\u0005\u0004\u0003\u0002%&\u0007\b\u0002\u0002&.\u0005\u0006\u0004\u0002'*\u0007\u0003\u0002\u0002(+\u0005\u0006\u0004\u0002)+\u0005\u0004\u0003\u0002*(\u0003\u0002\u0002\u0002*)\u0003\u0002\u0002\u0002+-\u0003\u0002\u0002\u0002,'\u0003\u0002\u0002\u0002-0\u0003\u0002\u0002\u0002.,\u0003\u0002\u0002\u0002./\u0003\u0002\u0002\u0002/8\u0003\u0002\u0002\u00020.\u0003\u0002\u0002\u000214\u0007\u0003\u0002\u000225\u0005\u000e\b\u000235\u0005\u0016\f\u000242\u0003\u0002\u0002\u000243\u0003\u0002\u0002\u000257\u0003\u0002\u0002\u000261\u0003\u0002\u0002\u00027:\u0003\u0002\u0002\u000286\u0003\u0002\u0002\u000289\u0003\u0002\u0002\u00029;\u0003\u0002\u0002\u0002:8\u0003\u0002\u0002\u0002;<\u0007\t\u0002\u0002<\t\u0003\u0002\u0002\u0002=>\u0007\n\u0002\u0002>?\u0005\u0006\u0004\u0002?@\u0007\t\u0002\u0002@\u000b\u0003\u0002\u0002\u0002AB\t\u0003\u0002\u0002B\r\u0003\u0002\u0002\u0002CD\u0007\u000f\u0002\u0002DE\u0007\u0007\u0002\u0002EF\u0005\f\u0007\u0002F\u000f\u0003\u0002\u0002\u0002GH\u0007\u0004\u0002\u0002HM\u0007\u000f\u0002\u0002IJ\u0007\u0003\u0002\u0002JL\u0007\u000f\u0002\u0002KI\u0003\u0002\u0002\u0002LO\u0003\u0002\u0002\u0002MK\u0003\u0002\u0002\u0002MN\u0003\u0002\u0002\u0002NP\u0003\u0002\u0002\u0002OM\u0003\u0002\u0002\u0002PQ\u0007\u0005\u0002\u0002Q\u0011\u0003\u0002\u0002\u0002RS\u0007\r\u0002\u0002ST\u0007\u0004\u0002\u0002TU\u0005\u0014\u000b\u0002UV\u0007\u0005\u0002\u0002V\u0013\u0003\u0002\u0002\u0002WZ\u0007\u000f\u0002\u0002XZ\u0005\u0006\u0004\u0002YW\u0003\u0002\u0002\u0002YX\u0003\u0002\u0002\u0002Z\u0015\u0003\u0002\u0002\u0002[\\\u0005\u0012\n\u0002\\]\u0007\u0007\u0002\u0002]^\u0005\f\u0007\u0002^\u0017\u0003\u0002\u0002\u0002\n\u001a\u001c*.48MY".toCharArray());
        _decisionToDFA = new DFA[DatalogParser._ATN.getNumberOfDecisions()];
        for (int i = 0; i < DatalogParser._ATN.getNumberOfDecisions(); ++i) {
            DatalogParser._decisionToDFA[i] = new DFA(DatalogParser._ATN.getDecisionState(i), i);
        }
    }
    
    public static class DatalogContext extends ParserRuleContext
    {
        public List<StatementContext> statement() {
            return this.getRuleContexts(StatementContext.class);
        }
        
        public StatementContext statement(final int i) {
            return this.getRuleContext((Class<? extends StatementContext>)StatementContext.class, i);
        }
        
        public List<Loop_stmtContext> loop_stmt() {
            return this.getRuleContexts(Loop_stmtContext.class);
        }
        
        public Loop_stmtContext loop_stmt(final int i) {
            return this.getRuleContext((Class<? extends Loop_stmtContext>)Loop_stmtContext.class, i);
        }
        
        public DatalogContext(final ParserRuleContext parent, final int invokingState) {
            super(parent, invokingState);
        }
        
        @Override
        public int getRuleIndex() {
            return 0;
        }
        
        @Override
        public void enterRule(final ParseTreeListener listener) {
            if (listener instanceof DatalogListener) {
                ((DatalogListener)listener).enterDatalog(this);
            }
        }
        
        @Override
        public void exitRule(final ParseTreeListener listener) {
            if (listener instanceof DatalogListener) {
                ((DatalogListener)listener).exitDatalog(this);
            }
        }
    }
    
    public static class Lh_atomContext extends ParserRuleContext
    {
        public ParametersContext parameters() {
            return this.getRuleContext((Class<? extends ParametersContext>)ParametersContext.class, 0);
        }
        
        public TerminalNode NODES() {
            return this.getToken(9, 0);
        }
        
        public TerminalNode EDGES() {
            return this.getToken(10, 0);
        }
        
        public Lh_atomContext(final ParserRuleContext parent, final int invokingState) {
            super(parent, invokingState);
        }
        
        @Override
        public int getRuleIndex() {
            return 1;
        }
        
        @Override
        public void enterRule(final ParseTreeListener listener) {
            if (listener instanceof DatalogListener) {
                ((DatalogListener)listener).enterLh_atom(this);
            }
        }
        
        @Override
        public void exitRule(final ParseTreeListener listener) {
            if (listener instanceof DatalogListener) {
                ((DatalogListener)listener).exitLh_atom(this);
            }
        }
    }
    
    public static class Rh_atomContext extends ParserRuleContext
    {
        public TerminalNode ID() {
            return this.getToken(13, 0);
        }
        
        public ParametersContext parameters() {
            return this.getRuleContext((Class<? extends ParametersContext>)ParametersContext.class, 0);
        }
        
        public Rh_atomContext(final ParserRuleContext parent, final int invokingState) {
            super(parent, invokingState);
        }
        
        @Override
        public int getRuleIndex() {
            return 2;
        }
        
        @Override
        public void enterRule(final ParseTreeListener listener) {
            if (listener instanceof DatalogListener) {
                ((DatalogListener)listener).enterRh_atom(this);
            }
        }
        
        @Override
        public void exitRule(final ParseTreeListener listener) {
            if (listener instanceof DatalogListener) {
                ((DatalogListener)listener).exitRh_atom(this);
            }
        }
    }
    
    public static class StatementContext extends ParserRuleContext
    {
        public List<Lh_atomContext> lh_atom() {
            return this.getRuleContexts(Lh_atomContext.class);
        }
        
        public Lh_atomContext lh_atom(final int i) {
            return this.getRuleContext((Class<? extends Lh_atomContext>)Lh_atomContext.class, i);
        }
        
        public TerminalNode COLON_H() {
            return this.getToken(6, 0);
        }
        
        public List<Rh_atomContext> rh_atom() {
            return this.getRuleContexts(Rh_atomContext.class);
        }
        
        public Rh_atomContext rh_atom(final int i) {
            return this.getRuleContext((Class<? extends Rh_atomContext>)Rh_atomContext.class, i);
        }
        
        public TerminalNode END_STATEMENT() {
            return this.getToken(7, 0);
        }
        
        public List<PredicateContext> predicate() {
            return this.getRuleContexts(PredicateContext.class);
        }
        
        public PredicateContext predicate(final int i) {
            return this.getRuleContext((Class<? extends PredicateContext>)PredicateContext.class, i);
        }
        
        public List<Agg_predicateContext> agg_predicate() {
            return this.getRuleContexts(Agg_predicateContext.class);
        }
        
        public Agg_predicateContext agg_predicate(final int i) {
            return this.getRuleContext((Class<? extends Agg_predicateContext>)Agg_predicateContext.class, i);
        }
        
        public StatementContext(final ParserRuleContext parent, final int invokingState) {
            super(parent, invokingState);
        }
        
        @Override
        public int getRuleIndex() {
            return 3;
        }
        
        @Override
        public void enterRule(final ParseTreeListener listener) {
            if (listener instanceof DatalogListener) {
                ((DatalogListener)listener).enterStatement(this);
            }
        }
        
        @Override
        public void exitRule(final ParseTreeListener listener) {
            if (listener instanceof DatalogListener) {
                ((DatalogListener)listener).exitStatement(this);
            }
        }
    }
    
    public static class Loop_stmtContext extends ParserRuleContext
    {
        public TerminalNode LOOP() {
            return this.getToken(8, 0);
        }
        
        public Rh_atomContext rh_atom() {
            return this.getRuleContext((Class<? extends Rh_atomContext>)Rh_atomContext.class, 0);
        }
        
        public TerminalNode END_STATEMENT() {
            return this.getToken(7, 0);
        }
        
        public Loop_stmtContext(final ParserRuleContext parent, final int invokingState) {
            super(parent, invokingState);
        }
        
        @Override
        public int getRuleIndex() {
            return 4;
        }
        
        @Override
        public void enterRule(final ParseTreeListener listener) {
            if (listener instanceof DatalogListener) {
                ((DatalogListener)listener).enterLoop_stmt(this);
            }
        }
        
        @Override
        public void exitRule(final ParseTreeListener listener) {
            if (listener instanceof DatalogListener) {
                ((DatalogListener)listener).exitLoop_stmt(this);
            }
        }
    }
    
    public static class ValueContext extends ParserRuleContext
    {
        public TerminalNode STRING_LITERAL() {
            return this.getToken(12, 0);
        }
        
        public TerminalNode NUM() {
            return this.getToken(14, 0);
        }
        
        public ValueContext(final ParserRuleContext parent, final int invokingState) {
            super(parent, invokingState);
        }
        
        @Override
        public int getRuleIndex() {
            return 5;
        }
        
        @Override
        public void enterRule(final ParseTreeListener listener) {
            if (listener instanceof DatalogListener) {
                ((DatalogListener)listener).enterValue(this);
            }
        }
        
        @Override
        public void exitRule(final ParseTreeListener listener) {
            if (listener instanceof DatalogListener) {
                ((DatalogListener)listener).exitValue(this);
            }
        }
    }
    
    public static class PredicateContext extends ParserRuleContext
    {
        public TerminalNode ID() {
            return this.getToken(13, 0);
        }
        
        public TerminalNode CONDITION() {
            return this.getToken(5, 0);
        }
        
        public ValueContext value() {
            return this.getRuleContext((Class<? extends ValueContext>)ValueContext.class, 0);
        }
        
        public PredicateContext(final ParserRuleContext parent, final int invokingState) {
            super(parent, invokingState);
        }
        
        @Override
        public int getRuleIndex() {
            return 6;
        }
        
        @Override
        public void enterRule(final ParseTreeListener listener) {
            if (listener instanceof DatalogListener) {
                ((DatalogListener)listener).enterPredicate(this);
            }
        }
        
        @Override
        public void exitRule(final ParseTreeListener listener) {
            if (listener instanceof DatalogListener) {
                ((DatalogListener)listener).exitPredicate(this);
            }
        }
    }
    
    public static class ParametersContext extends ParserRuleContext
    {
        public TerminalNode LPAR() {
            return this.getToken(2, 0);
        }
        
        public List<TerminalNode> ID() {
            return this.getTokens(13);
        }
        
        public TerminalNode ID(final int i) {
            return this.getToken(13, i);
        }
        
        public TerminalNode RPAR() {
            return this.getToken(3, 0);
        }
        
        public ParametersContext(final ParserRuleContext parent, final int invokingState) {
            super(parent, invokingState);
        }
        
        @Override
        public int getRuleIndex() {
            return 7;
        }
        
        @Override
        public void enterRule(final ParseTreeListener listener) {
            if (listener instanceof DatalogListener) {
                ((DatalogListener)listener).enterParameters(this);
            }
        }
        
        @Override
        public void exitRule(final ParseTreeListener listener) {
            if (listener instanceof DatalogListener) {
                ((DatalogListener)listener).exitParameters(this);
            }
        }
    }
    
    public static class Aggregate_exprContext extends ParserRuleContext
    {
        public TerminalNode AGGR() {
            return this.getToken(11, 0);
        }
        
        public TerminalNode LPAR() {
            return this.getToken(2, 0);
        }
        
        public TerminalNode RPAR() {
            return this.getToken(3, 0);
        }
        
        public Agg_parameterContext agg_parameter() {
            return this.getRuleContext((Class<? extends Agg_parameterContext>)Agg_parameterContext.class, 0);
        }
        
        public Aggregate_exprContext(final ParserRuleContext parent, final int invokingState) {
            super(parent, invokingState);
        }
        
        @Override
        public int getRuleIndex() {
            return 8;
        }
        
        @Override
        public void enterRule(final ParseTreeListener listener) {
            if (listener instanceof DatalogListener) {
                ((DatalogListener)listener).enterAggregate_expr(this);
            }
        }
        
        @Override
        public void exitRule(final ParseTreeListener listener) {
            if (listener instanceof DatalogListener) {
                ((DatalogListener)listener).exitAggregate_expr(this);
            }
        }
    }
    
    public static class Agg_parameterContext extends ParserRuleContext
    {
        public TerminalNode ID() {
            return this.getToken(13, 0);
        }
        
        public Rh_atomContext rh_atom() {
            return this.getRuleContext((Class<? extends Rh_atomContext>)Rh_atomContext.class, 0);
        }
        
        public Agg_parameterContext(final ParserRuleContext parent, final int invokingState) {
            super(parent, invokingState);
        }
        
        @Override
        public int getRuleIndex() {
            return 9;
        }
        
        @Override
        public void enterRule(final ParseTreeListener listener) {
            if (listener instanceof DatalogListener) {
                ((DatalogListener)listener).enterAgg_parameter(this);
            }
        }
        
        @Override
        public void exitRule(final ParseTreeListener listener) {
            if (listener instanceof DatalogListener) {
                ((DatalogListener)listener).exitAgg_parameter(this);
            }
        }
    }
    
    public static class Agg_predicateContext extends ParserRuleContext
    {
        public Aggregate_exprContext aggregate_expr() {
            return this.getRuleContext((Class<? extends Aggregate_exprContext>)Aggregate_exprContext.class, 0);
        }
        
        public TerminalNode CONDITION() {
            return this.getToken(5, 0);
        }
        
        public ValueContext value() {
            return this.getRuleContext((Class<? extends ValueContext>)ValueContext.class, 0);
        }
        
        public Agg_predicateContext(final ParserRuleContext parent, final int invokingState) {
            super(parent, invokingState);
        }
        
        @Override
        public int getRuleIndex() {
            return 10;
        }
        
        @Override
        public void enterRule(final ParseTreeListener listener) {
            if (listener instanceof DatalogListener) {
                ((DatalogListener)listener).enterAgg_predicate(this);
            }
        }
        
        @Override
        public void exitRule(final ParseTreeListener listener) {
            if (listener instanceof DatalogListener) {
                ((DatalogListener)listener).exitAgg_predicate(this);
            }
        }
    }
}
