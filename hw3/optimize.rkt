#lang scheme

;; simplify: takes a scheme expression and simplifies that expression by applying the simplification rules.
(define (simplify exp)
  (cond
        ((number? exp) exp)
        ((symbol? exp) exp)
        ((is-add? exp) (add (simplify (operand1 exp)) (simplify (operand2 exp))))
        ((is-subtract? exp) (subtract (simplify (operand1 exp)) (simplify (operand2 exp))))
        ((is-multiply? exp) (multiply (simplify (operand1 exp)) (simplify (operand2 exp))))
        ((is-divide? exp) (divide (simplify (operand1 exp)) (simplify (operand2 exp))))
        (else exp)))

;; A helper function that returns the operator from an expression.
(define (operator exp)
  (if (null? exp) '() (car exp)))

;; A helper function that returns the first subexpression from an expression.
(define (operand1 exp)
  (cond ((null? exp) '())
        ((number? exp) exp)
        (else (cadr exp))))

;; A helper function that returns the second subexpression from an expression.
(define (operand2 exp)
  (cond ((null? exp) '())
        ((number? exp) exp)
        (else (caddr exp))))

;; A helper function used to determine if an expression is addition.
(define (is-add? exp)
  (equal? '+ (operator exp)))

;; A helper function used to determine if an expression is multiplication.
(define (is-multiply? exp)
  (equal? '* (operator exp)))

;; A helper function used to determine if an expression is subtraction.
(define (is-subtract? exp)
  (equal? '- (operator exp)))

;; A helper function used to determine if an expression is division.
(define (is-divide? exp)
  (equal? '/ (operator exp)))

;; A helper function to the add and multiply functions that will return a sorted version of the expression with x and y being subexpressions. 
(define (sorter operator x y)
  (cond ((and (list? x) (list? y))
         (cond ((is-multiply? x) (list operator x y))
               ((is-multiply? y) (list operator y x))
               ((is-add? x) (list operator x y))
               ((is-add? y) (list operator y x))
               ((is-subtract? x) (list operator x y))
               ((is-subtract? y) (list operator y x))
               ((is-divide? x) (list operator x y))
               ((is-divide? y) (list operator y x))))
        ((list? x) (list operator y x))
        ((list? y) (list operator x y))
        ((number? x) (list operator y x))
        ((number? y) (list operator x y))
        (else (sort (list operator x y) symbol<?))))

;; A helper function for performing the add operation. Returns "error" when necessary.
(define (add x y)
  (cond ((equal? x 'error) 'error)
        ((equal? y 'error) 'error)
        ((equal? '0 x) y)
        ((equal? '0 y) x)
        ((and (number? x) (number? y)) (+ x y))
        (else (sorter '+ x y))))

;; A helper function for performing the subtract operation. Returns "error" when necessary.
(define (subtract x y)
  (cond ((equal? x 'error) 'error)
        ((equal? y 'error) 'error)
        ((equal? '0 y) x)
        ((equal? x y) '0)
        ((and (number? x) (number? y)) (- x y))
        (else (list '- x y))))

;; A helper function for performing the multiply operation. Returns "error" when necessary.
(define (multiply x y)
  (cond ((equal? x 'error) 'error)
        ((equal? y 'error) 'error)
        ((equal? '0 x) '0)
        ((equal? '0 y) '0)
        ((equal? '1 x) y)
        ((equal? '1 y) x)
        ((and (number? x) (number? y)) (* x y))
        (else (sorter '* x y))))

;; A helper function for performing the divide operation. Returns "error" when necessary.
(define (divide x y)
  (cond ((equal? x 'error) 'error)
        ((equal? y 'error) 'error)
        ((equal? '1 y) x)
        ((equal? '0 y) 'error)
        ((equal? x y) '1)
        ((equal? '0 x) '0)
        ((and (number? x) (number? y)) (/ x y))
        (else (list '/ x y))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Excepts an expression and returns the optimized version of it.
(define (optimize exp)
  (if (null? (populate-map (simplify exp))) (simplify exp) (optimizer (simplify exp) (populate-map (simplify exp)) '())))

;; Excepts a simplified expression exp, a list of lists that contain identifier/common-expression pairs named pairs, and
;; a list containing pairs with common-expressions replaced with other identifiers named symbol-map.
(define (optimizer exp pairs symbol-map)
  (cond ((null? pairs) (list 'let* symbol-map exp))
        (else (optimizer (find-replace-exp (car pairs) exp) (find-replace-pairs (car pairs) (cdr pairs)) (append symbol-map (list (car pairs)))))))

;; Sorts a list of expressions lst by the length of each expression.
(define (lst-len-sort lst)
  (sort lst (lambda (x y) (< (exp-length x) (exp-length y)))))

;; Returns the length of the expression x by the number of elements it contains.
(define (exp-length x)
  (if (is-atomic? x) 1 (+ 1 (+ (exp-length (cadr x)) (exp-length (caddr x))))))

;; Excepts an expressio and returns a list populated with the common subexpressions mapped to their unique identifiers.
(define (populate-map exp)
  (map (lambda (subexp) (create-symbol-exp-pair subexp)) (lst-len-sort (get-duplicates (find-subexpressions exp)))))

;; Returns a list of all duplicate expressions from the list lst.
(define (get-duplicates lst)
  (cond ((null? lst) '())
        ((member (car lst) (cdr lst)) (cons (car lst) (get-duplicates (cdr lst))))
        (else (get-duplicates (cdr lst)))))

;; Returns the list of common subexpressions lst from the expression exp.
(define (find-subexpressions exp)
  (cond ((is-atomic? exp) '())
        (else (cons exp (append (find-subexpressions (cadr exp)) (find-subexpressions (caddr exp)))))))

;; Returns a pair that includes a unique identifier and the expression it represents.
(define (create-symbol-exp-pair exp)
  (list (gensym) exp))

;; Returns the unique identifier from a pair.
(define (V pair)
  (car pair))

;; Returns the expression from a pair.
(define (EXP pair)
  (cadr pair))

;; A predicate to determine if x is an atomic value.
(define (is-atomic? x) 
  (or (number? x) (symbol? x)))

;; In the expression exp, find and replace the common subexpression from pair with the associated identifier.
(define (find-replace-exp pair exp)
 (cond ((equal? (EXP pair) exp) (V pair))
       ((is-atomic? exp) exp)
       (else (list (car exp)
                   (find-replace-exp pair (cadr exp))
                   (find-replace-exp pair (caddr exp))))))

;; In the list of pairs name pairs, find and replace the common subexpression from the list replacement with the associated identifier.
(define (find-replace-pairs replacement pairs)
  (cond ((null? pairs) '())
        (else (list (append
               (list (V (car pairs)) (find-replace-exp replacement (EXP (car pairs))))
               (find-replace-pairs replacement (cdr pairs)))))))

(provide optimize)
