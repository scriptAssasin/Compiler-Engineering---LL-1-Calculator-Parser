import java.io.InputStream;
import java.io.IOException;

//LL(1)
// 1.exp -> exp2 expTail
// 2.expTail -> ^ exp2 expTail 
// 3.                | e
// 4.exp2 -> exp3 expTail2
// 5.expTail2 -> & exp3 expTail2 
// 6.                | e
// 7.exp3 -> 0 | 1 | ... | 9
// 8.                | (exp)

// FIRST+(1) =  { '0'..'9' , '(' }
// FIRST+(2) =  { '^' }
// FIRST+(3) =  { e , ')' , '$' }
// FIRST+(4) =  { '0'..'9' , '(' }
// FIRST+(5) =  { '&' }
// FIRST+(6) =  { e , ')' , '^' , '$' }
// FIRST+(7) =  { '0'..'9' }
// FIRST+(8) =  { '(' }

// Parse Table|    [0-9] 	 |     ^	 |      &	  |     (	 |      )	  |     EOF
// ___________|_____________|___________|____________|__________|____________|___________
// exp        |      1      |   error   |    error   |    1     |    error   |    error
// expTail    |    error    |     2     |    error   |   error  |     3      |      3 
// exp2       |      4      |   error   |    error   |    4     |    error   |    error
// expTail2   |    error    |     6     |      5     |   error  |     6      |      6
// exp3       |      7      |   error   |    error   |    8     |    error   |    error


// The whole structure is based on TernaryEvaluator given in the class.

class Calculator {

    private final InputStream in;

    private int lookahead;

    public Calculator(InputStream in) throws IOException {
        this.in = in;
        lookahead = in.read();
    }

    private void consume(int symbol) throws IOException, ParseError {
        if (lookahead == symbol)
            lookahead = in.read();
        else
            throw new ParseError();
    }

    private boolean isDigit(int c) {
        return '0' <= c && c <= '9';
    }

    private int evalDigit(int c) {
        return c - '0';
    }

    public int eval() throws IOException, ParseError {
        // exp is the first non-terminal
        int value = exp();

        if (lookahead != -1 && lookahead != '\n')
            throw new ParseError();

        return value;
    }

   private int exp() throws IOException, ParseError {
    //    if the first character is not digit or starting parenthesis we've got a parse error, according to our parse table.
        if(!isDigit(lookahead) && lookahead != '(')
            throw new ParseError();
    //    the exp2 non-terminal will be called first (according to our grammar) and the result will be passed on expTail in order to achieve the XOR operation
        return expTail(exp2());
   }    

   private int expTail(int left) throws IOException, ParseError {
    //  according to our parse table the accepted symbols are ^,),$=EOF and \n=EOF
        switch (lookahead) {
            case '^':
                consume('^');
    //  in case of ^ => recursive call of itself
                int right = expTail(exp2()); 
                return left ^ right; 
    //  in case of EOF or ) we call production #3 = e => nothing and return the last symbol
            case -1: case '\n': case ')':
                return left;
            default:
                throw new ParseError();
        }
   }    

   private int exp2() throws IOException, ParseError {
    // the same as exp, but we call expTail2 and exp3 instead of expTail and exp2
        if(!isDigit(lookahead) && lookahead != '(')
            throw new ParseError();

        return expTail2(exp3());
   }  
     
   private int expTail2(int left) throws IOException, ParseError {
    //  according to our parse table the accepted symbols are &,^,),$=EOF and \n=EOF
        switch (lookahead) {
            case '&':
                consume('&');
    //  in case of & => recursive call of itself
                int right = expTail2(exp3());
                return left & right; 
    //  in case of EOF or ) or ^ we call production #6 = e => nothing and return the last symbol
            case ')': case '^': case -1: case '\n':
                return left;
            default:
                throw new ParseError();
        }
   }    

   private int exp3() throws IOException, ParseError {
    //  according to our parse table the accepted symbols are numbers [0-9] and (
        switch (lookahead) {
    //  production #8 (exp). So we consume ( call exp and consume )
            case '(':
                consume('(');
                int val = exp();
                consume(')');
                return val;
            default:
    //  production #7 in case it is a number, convert the number from ASCII to digit using evalDigit and return the value 
                if(isDigit(lookahead))
                {
                    int val2 = evalDigit(lookahead);
                    consume(lookahead);
                    return val2;
                }
                throw new ParseError();
        }
   }    

}