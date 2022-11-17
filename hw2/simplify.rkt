#lang racket

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

(provide simplify)
