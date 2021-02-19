// 
// Decompiled by Procyon v0.5.36
// 

package io.github.graphmk.parser;

import org.antlr.v4.runtime.tree.ParseTreeListener;

public interface DatalogListener extends ParseTreeListener
{
    void enterDatalog(final DatalogParser.DatalogContext p0);
    
    void exitDatalog(final DatalogParser.DatalogContext p0);
    
    void enterLh_atom(final DatalogParser.Lh_atomContext p0);
    
    void exitLh_atom(final DatalogParser.Lh_atomContext p0);
    
    void enterRh_atom(final DatalogParser.Rh_atomContext p0);
    
    void exitRh_atom(final DatalogParser.Rh_atomContext p0);
    
    void enterStatement(final DatalogParser.StatementContext p0);
    
    void exitStatement(final DatalogParser.StatementContext p0);
    
    void enterLoop_stmt(final DatalogParser.Loop_stmtContext p0);
    
    void exitLoop_stmt(final DatalogParser.Loop_stmtContext p0);
    
    void enterValue(final DatalogParser.ValueContext p0);
    
    void exitValue(final DatalogParser.ValueContext p0);
    
    void enterPredicate(final DatalogParser.PredicateContext p0);
    
    void exitPredicate(final DatalogParser.PredicateContext p0);
    
    void enterParameters(final DatalogParser.ParametersContext p0);
    
    void exitParameters(final DatalogParser.ParametersContext p0);
    
    void enterAggregate_expr(final DatalogParser.Aggregate_exprContext p0);
    
    void exitAggregate_expr(final DatalogParser.Aggregate_exprContext p0);
    
    void enterAgg_parameter(final DatalogParser.Agg_parameterContext p0);
    
    void exitAgg_parameter(final DatalogParser.Agg_parameterContext p0);
    
    void enterAgg_predicate(final DatalogParser.Agg_predicateContext p0);
    
    void exitAgg_predicate(final DatalogParser.Agg_predicateContext p0);
}
