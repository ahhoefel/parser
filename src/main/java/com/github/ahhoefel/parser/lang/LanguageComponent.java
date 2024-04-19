package com.github.ahhoefel.parser.lang;

import com.github.ahhoefel.parser.ShiftReduceResolver;

/**
 * Provides a modular way of specifying a grammar.
 * 
 * <p>
 * A grammar can be built from one or more LanguageComponents. Each
 * implementation of LanguageComponent overrides {@code provideRules}.
 * 
 * <p>
 * For example, the following component implements a simple identifier which is
 * is one or more alphanumeric symbols.
 * 
 * <pre>{@code
 * public static class IdentifierComponent implements LanguageComponent { @Override
 *     public void provideRules(SymbolProvider provider, ShiftReduceResolver resolver, RuleEmitter rules) {
 *         Symbol identifier = provider.createAndExport("identifier");
 *         Symbol alphanumeric = provider.require("alphanumeric");
 *         Rule repeatRule = rules.emit(identifier, alphanumeric, identifier);
 *         Rule finalRule = rules.emit(identifier, alphanumeric);
 *         repeatRule.setAction(ConcatAction.SINGLETON);
 *         finalRule.setAction(ConcatAction.SINGLETON);
 *         resolver.addShiftPreference(finalRule, alphanumeric);
 *     }
 * }
 * }</pre>
 * 
 * @see com.github.ahhoefel.parser.LRParser
 */
public interface LanguageComponent {

    /**
     * Used to specify a portion of a grammar.
     * 
     * @param provider Provides a way to create, export and import symbols from
     *                 other components.
     * @param resolver Used to specify shift/reduce preferences to resolve
     *                 ambiguities in the grammar.
     * @param emit     Constructs grammar rules.
     */
    void provideRules(SymbolProvider provider, ShiftReduceResolver resolver, RuleEmitter emit);

}