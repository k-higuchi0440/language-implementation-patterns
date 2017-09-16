package antlr3.pattern19_class.cymbol; /***
 * Excerpted from "Language Implementation Patterns",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/tpdsl for more book information.
***/
import antlr3.pattern19_class.scope.Scope;
import antlr3.pattern19_class.symbol.Symbol;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.Token;
public class CymbolAST extends CommonTree {
    public Scope scope;   // set by antlr3.pattern19_class.def.Def.g; ID lives in which scope?
    public Symbol symbol; // set by Ref.g; point at def in symbol table
    public CymbolAST(Token t) { super(t); }
}
