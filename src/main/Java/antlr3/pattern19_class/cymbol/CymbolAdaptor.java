package antlr3.pattern19_class.cymbol;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTreeAdaptor;

/** An adaptor that tells ANTLR to build CymbolAST nodes */
public class CymbolAdaptor extends CommonTreeAdaptor {
public Object create(Token token) {
        return new CymbolAST(token);
        }
public Object dupNode(Object t) {
        if ( t==null ) {
        return null;
        }
        return create(((CymbolAST)t).token);
        }
public Object errorNode(TokenStream input, Token start, Token stop,
                        RecognitionException e)
        {
        CymbolErrorNode t = new CymbolErrorNode(input, start, stop, e);
        //System.out.println("returning error node '"+t+"' @index="+input.index());
        return t;
        }
}