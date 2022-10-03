#lang scheme

;; partition: accepts a list L and an integer N. The function partitions L into lists of length N and returns these partitions as a list. The last element in the returned list may
;; have a length that is less than N. If N is non-positive, it is treated as the value 1.
(define (partition L N)
  (cond ((null? L) '())
        ((<= N 0) (partition L 1))
        ((< (length L) N) (cons L '()))
        (else (cons (partition-helper L N) (partition (drop L N) N)))))

;; A helper function that accepts a list L and a number N to construct a list that is N items long.
(define (partition-helper L N)
  (cond ((null? L) '())
        ((<= N 0) '())
        (else (cons (car L) (partition-helper (cdr L) (- N 1))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; cycle: Accepts a list of elements ALIST and an integer N. This function returns a list containing N repetitions of the elements of ALIST. If N is non-positive, this function returns the empty list. 
(define (cycle ALIST N)
  (cond ((null? ALIST) '())
        ((<= N 0) '())
        (else (append ALIST (cycle ALIST (- N 1))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; list-replace: Accepts a list of elements and returns that list where all SYM's (a single symbol) have been replaced by the VAL (some scheme value). The replacement must occur even within nested lists.
(define (list-replace ALIST SYM VAL)
  (cond ((null? ALIST) '())
        ((list? (car ALIST)) (cons (list-replace (car ALIST) SYM VAL) (list-replace (cdr ALIST) SYM VAL)))
        ((equal? (car ALIST) SYM) (cons VAL (list-replace (cdr ALIST) SYM VAL)))
        (else (cons (car ALIST) (list-replace (cdr ALIST) SYM VAL)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; repeat: Creates a list of length count where each element is given by VAL. COUNT is a non-negative integer and VAL is any scheme value.
(define (repeat VAL COUNT)
  (cond ((>= 0 COUNT) '())
        (else (cons VAL (repeat VAL (- COUNT 1))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; summer: Takes a list of numbers L and generates a list of the runnining sums.
(define (summer L)
  (cond ((null? L) '())
        (else (cons (car L) (summer-helper (cdr L) (car L))))))

;; The helper function to summer. Takes the list of numbers L and the value of the previous number N. 
(define (summer-helper L N-1)
  (cond ((null? L) '())
        (else (cons (+ (car L) N-1) (summer-helper (cdr L) (+ (car L) N-1))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; counts: Takes a list of items XS and generates a counting of the elements in XS. The returned object is a list of lists. Each element of the returned object is a list of length two
;; containing an element X of XS and an integer denoting the number of occurrences of X in XS. The order of the elements in the computed list is not specified.
;; returns a list containg pairs of a unique element in L and the amount of times the unique element occurs in L
(define (counts XS)
  (cond ((null? XS) '())
        (else (cons (list (car XS) (count-xs XS (car XS))) (counts (remove-xs XS (car XS)))))))

;; A helper function to counts. Removes all occurrence of X in L to help count X only once. 
(define (remove-xs L X)
  (cond ((null? L)'())
        ((equal? X (car L)) (remove-xs (cdr L) X))
        (else (cons (car L) (remove-xs (cdr L) X)))))

;; A helper fucntion to counts. Counts the number of symbols X in the list L.
(define (count-xs L X)
  (cond ((null? L) 0)
        (else (if (eq? (car L) X) ( + 1 (count-xs (cdr L) X)) (count-xs (cdr L) X)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; prefix: Takes a list L and integer number N. If the length of L is greater-than or equal-to N, the method returns the first N elements of L as a list.
;; If N is negative, the method returns the empty list. If the length of L is less-than N, the method returns L.
(define (prefix L N)
  (cond ((null? L) '())
        ((<= N 0) '())
        ((>= (length L) N) (cons (car L) (prefix (cdr L) (- N 1))))
        (else L)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Accepts an el-graph g and returns an x-graph of g.
(define (el-graph->x-graph g)
  (map (lambda (x) (list (caar x) (map cadr x)))
       (group-by car g eq?)))

;; Accepts an x-graph g and returns an el-graph of g.
(define (x-graph->el-graph g)
  (append-map (lambda (x) (map (lambda (target) (list (car x) target)) (cadr x))) g))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Takes an E-expression and evaluates that expression.
(define (evaluate exp)
  (evaluate-helper exp '()))

;; Exp is the expression to be evaluated. Env is the environment which is a list of pairs that contain a symbol and its value.
(define (evaluate-helper exp env)
  (cond ((number? exp) exp)
        ((equal? exp 'undefined) 'undefined)
        ((symbol? exp) (get-from-environment env exp))
        ((equal? (car exp) 'block) (evaluate-helper (car (cddr exp)) (block (cadr exp) env)))
        ((equal? (car exp) '+) (add (evaluate-helper (cadr exp) env) (evaluate-helper (caddr exp) env)))
        ((equal? (car exp) '-) (subtract (evaluate-helper (cadr exp) env) (evaluate-helper (caddr exp) env)))
        ((equal? (car exp) '*) (multiply (evaluate-helper (cadr exp) env) (evaluate-helper (caddr exp) env)))
        ((equal? (car exp) '/) (divide (evaluate-helper (cadr exp) env) (evaluate-helper (caddr exp) env)))
        ((equal? (car exp) '^) (exponent (evaluate-helper (cadr exp) env) (evaluate-helper (caddr exp) env)))
        ((equal? (car exp) '%) (my-modulo (evaluate-helper (cadr exp) env) (evaluate-helper (caddr exp) env)))
        (else exp)))

;; A helper function for performing the add operation, or returns undefined if undefined
(define (add x y)
  (cond ((or (equal? x 'undefined) (equal? y 'undefined)) 'undefined)
        (else (+ x y))))

;; A helper function for performing the subtract operation, or returns undefined if undefined
(define (subtract x y)
  (cond ((or (equal? x 'undefined) (equal? y 'undefined)) 'undefined)
        (else (- x y))))

;; A helper function for performing the multiply operation, or returns undefined if undefined
(define (multiply x y)
  (cond ((or (equal? x 'undefined) (equal? y 'undefined)) 'undefined)
        (else (* x y))))

;; A helper function for performing the divide operation, or returns undefined if undefined
(define (divide x y)
  (cond ((or (equal? x 'undefined) (equal? y 'undefined)) 'undefined)
        (else (/ x y))))

;; A helper function for performing the exponent operation, or returns undefined if undefined
(define (exponent x y)
  (cond ((or (equal? x 'undefined) (equal? y 'undefined)) 'undefined)
        (else (expt x y))))

;; A helper function for performing the modulo operation, or returns undefined if undefined
(define (my-modulo x y)
  (cond ((or (equal? x 'undefined) (equal? y 'undefined)) 'undefined)
        (else (modulo x y))))

;; A helper function that handles a block statement. Takes a list names lst and a list of pairs named env as parameters.
(define (block lst env)
  (cond ((null? lst) )
        (else (append-map (lambda (x) (add-to-environment env (car x) (evaluate-helper (cdr x) env))) lst))))

;; A helper function that adds a key and value pair to an environment(env).
(define (add-to-environment env key value)
  (cond ((null? env) (list (create-entry key value)))
        ((equal? (car (car env)) key) (cons (create-entry key value)(cdr env)))
        (else (cons (car env) (add-to-environment (cdr env) key value)))))

;; A helper function that creates a key-value pair.
(define (create-entry key value)
  (list key value))

;; A helper function to retrieve the value of a key-value pair from a list names env, based on the value of key. 
(define (get-from-environment env key)
  (cond ((null? env) 'undefined)
         ((equal? key (car (car env))) (car (cadr (car env))))
         (else (get-from-environment (cdr env) key))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(provide partition cycle list-replace repeat summer counts prefix el-graph->x-graph x-graph->el-graph evaluate)
