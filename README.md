# Compiler-Engineering---LL-1-Calculator-Parser

The calculator accepts expressions with the bitwise AND(&) and XOR(^) operators, as well as parentheses. The grammar (for single-digit numbers) is summarized in:

exp -> num | exp op exp | (exp)

op -> ^ | &

num -> 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9

Translation of the above grammar to LL(1):

exp -> exp ^ exp2 | exp2
exp2 -> exp2 & exp3 | exp3
exp3 -> 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | (exp3)

1.exp -> exp2 expTail
2.expTail -> ^ exp2 expTail 
3.                | e
4.exp2 -> exp3 expTail2
5.expTail2 -> & exp3 expTail2 
6.                | e
7.exp3 -> 0 | 1 | ... | 9
8.                | (exp)

FIND FIRST, FOLLOW AND FIRST+ SETS AND Parse Table

FIRST(1)= {[0-9], '('}
FIRST(2) = {'^'}
FIRST(3) = {e}
FIRST(4) = {[0-9], '('}
FIRST(5) = {&}
FIRST(6) = {e}
FIRST(7) = {[0-9]}
FIRST(8) = {'('}

FOLLOW(exp) = {')', $}
FOLLOW(expTail) = {')', $}
FOLLOW(exp2) = {'^', ')', $}
FOLLOW(expTail2) = {')', '^', $}
FOLLOW(exp3) = { ')', '&' , '^' , $ }

FIRST+(1) =  { '0'..'9' , '(' }
FIRST+(2) =  { '^' }
FIRST+(3) =  { e , ')' , '$' }
FIRST+(4) =  { '0'..'9' , '(' }
FIRST+(5) =  { '&' }
FIRST+(6) =  { e , ')' , '^' , '$' }
FIRST+(7) =  { '0'..'9' }
FIRST+(8) =  { '(' }


Parse Table|    [0-9] 	 |     ^	 |      &	  |     (	 |      )	  |     EOF
___________|_____________|___________|____________|__________|____________|___________
exp        |      1      |   error   |    error   |    1     |    error   |    error
expTail    |    error    |     2     |    error   |   error  |     3      |      3 
exp2       |      4      |   error   |    error   |    4     |    error   |    error
expTail2   |    error    |     6     |      5     |   error  |     6      |      6
exp3       |      7      |   error   |    error   |    8     |    error   |    error