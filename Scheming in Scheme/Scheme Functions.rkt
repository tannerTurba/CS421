#lang scheme

;;DONE 
;; partition: accepts a list L and an integer N. The function partitions L into lists of length N and returns these partitions as a list. The last element in the returned list may
;; have a length that is less than N. If N is non-positive, it is treated as the value 1.
(define (partition L N)
  (cond ((null? L) '())
        ((<= N 0) (partition L 1))
        ((< (length L) N) (cons L '()))
        (else (cons (partition-helper L N) (partition (drop L N) N)))))

(define (partition-helper L N)
  (cond ((null? L) '())
        ((<= N 0) '())
        (else (cons (car L) (partition-helper (cdr L) (- N 1))))))

;; DONE
;; cycle: Accepts a list of elements ALIST and an integer N. This function returns a list containing N repetitions of the elements of ALIST. If N is non-positive, this function returns the empty list. 
(define (cycle ALIST N)
  (cond ((null? ALIST) '())
        ((<= N 0) '())
        (else (append ALIST (cycle ALIST (- N 1))))))

;; DONE
;; list-replace: Accepts a list of elements and returns that list where all SYM's (a single symbol) have been replaced by the VAL (some scheme value). The replacement must occur even within nested lists.
(define (list-replace ALIST SYM VAL)
  (cond ((null? ALIST) '())
        ((list? (car ALIST)) (cons (list-replace (car ALIST) SYM VAL) (list-replace (cdr ALIST) SYM VAL)))
        ((equal? (car ALIST) SYM) (cons VAL (list-replace (cdr ALIST) SYM VAL)))
        (else (cons (car ALIST) (list-replace (cdr ALIST) SYM VAL)))))

;; DONE
;; repeat: Creates a list of length count where each element is given by VAL. COUNT is a non-negative integer and VAL is any scheme value.
(define (repeat VAL COUNT)
  (cond ((>= 0 COUNT) '())
        (else (cons VAL (repeat VAL (- COUNT 1))))))

;; DONE
;; summer: Takes a list of numbers L and generates a list of the runnining sums.
(define (summer L)
  (cond ((null? L) '())
        (else (cons (car L) (summer-helper (cdr L) (car L))))))

;; The helper function to summer. Takes the list of numbers L and the value of the previous number N. 
(define (summer-helper L N-1)
  (cond ((null? L) '())
        (else (cons (+ (car L) N-1) (summer-helper (cdr L) (+ (car L) N-1))))))

;; counts: Takes a list of items XS and generates a counting of the elements in XS. The returned object is a list of lists. Each element of the returned object is a list of length two
;; containing an element X of XS and an integer denoting the number of occurrences of X in XS. The order of the elements in the computed list is not specified.
;; returns a list containg pairs of a unique element in L and the amount of times the unique element occurs in L
(define (counts XS)
  (cond ((null? XS) '())
        (else (cons (list (car XS) (count_occurrences XS (car XS))) (counts (remove_occurrences XS (car XS)))))))

;; removes all occurrence of X in L
(define (remove_occurrences L X)
  (cond ((null? L)'())
        ((equal? X (car L)) (remove_occurrences (cdr L) X))
        (else (cons (car L) (remove_occurrences (cdr L) X)))))

;; returns the number of times X is found in L
(define (count_occurrences L X)
  (cond ((null? L) 0)
        (else (if (eq? (car L) X) ( + 1 (count_occurrences (cdr L) X)) (count_occurrences (cdr L) X)))))

;;DONE
;; prefix: Takes a list L and integer number N. If the length of L is greater-than or equal-to N, the method returns the first N elements of L as a list.
;; If N is negative, the method returns the empty list. If the length of L is less-than N, the method returns L.
(define (prefix L N)
  (cond ((null? L) '())
        ((<= N 0) '())
        ((>= (length L) N) (cons (car L) (prefix (cdr L) (- N 1))))
        (else L)))

;; Accepts an el-graph g and returns an x-graph of g.
(define (el-graph->x-graph g)
  (map (lambda (graph)
         (list (caar graph) (map cadr graph)))
       (group-by car g eq?)))

;; Accepts an x-graph g and returns an el-graph of g.
(define (x-graph->el-graph g)
  (append-map (lambda (adjacency)
                (map (lambda (target)
                       (list (car adjacency) target))
                     (cadr adjacency))) g))

;; Takes an E-expression and evaluates that expression.
(define (evaluate exp)
  (evaluate-helper exp '()))

(define (evaluate-helper exp env)
  (cond ((null? exp) 0)
        ((to-number (get-first-value exp)) (to-number (get-first-value exp)))
        ((list? exp) (if (equal? (symbol->string (get-first-value exp)) "block") (evaluate-helper (car (cddr exp)) (block (cadr exp) env))
                         ((get-operation (car exp)) (evaluate-helper (cadr exp) env) (evaluate-helper (caddr exp) env))))
        ((equal? (string-length(symbol->string (get-first-value exp))) 1) (if (environment-contains env exp) (get-from-environment env exp) ('undefined)))
        (else exp)))

;; Function that handles a block
(define (block lst env)
  (cond ((null? lst) )
        (else (append-map (lambda (x) (add-to-environment env (car x) (evaluate-helper (cdr x) env))) lst))))

(define (add-to-environment env key value)
  (cond ((null? env) (list (create-entry key value)))
        ((equal? (car (car env)) key) (cons (create-entry key value)(cdr env)))
        (else (cons (car env) (add-to-environment (cdr env) key value)))))

(define (create-entry key value)
  (list key value))

(define (get-from-environment env key)
  (cond ((null? env) '())
         ((equal? key (car (car env))) (cdr (car env)))
         (else (get-from-environment (cdr env) key))))

(define (environment-contains env key)
  (cond ((null? env) #f)
        ((equal? key (car (car env))) #t)
        (else (environment-contains (cdr env) key))))

(define (get-first-value x)
  (cond ((null? x))
        ((list? x) (car x))
        (else x)))

;; A list of the primitive operators
(define primitives
  (list (cons '+ +)
        (cons '- -)
        (cons '/ /)
        (cons '* *)
        (cons '= =)
        (cons '> >)
        (cons '< <)
        (cons '% modulo)
        (cons '^ expt)))

;; Looks up a symbol from the primitive environment to get the procedure.
(define (get-operation symb)
  (cond ((null? primitives) '())
        (else (operation-lookup symb (car primitives) (cdr primitives)))))

(define (operation-lookup symb pair lst)
  (cond ((null? lst) '())
        ((equal? (car pair) symb) (cdr pair))
        (else (operation-lookup symb (car lst) (cdr lst)))))

;; Converts a symbol to a numerical value.
(define (to-number x)
  (cond ((null? x) #f)
        ((number? x) x)
        (else #f)))
        ;;(else (string->number (symbol->string x)))))



