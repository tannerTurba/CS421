#lang racket

;; Returns an empty list, which represents an empty abst tree.
(define (abst-create-empty) '())

;; Returns a list containing the root, the left subtree, the right subtree, and the number of nodes.
(define (abst-create root left right)
  (list root left right (+ 1(+ (count-nodes left) (count-nodes right)))))

;; Returns the value stored at the root of the tree bst.
(define (root bst)
  (car bst))

;; Returns the left subtree of the tree bst. 
(define (left-subtree bst)
  (cadr bst))

;; Returns the right subtree of the tree bst. 
(define (right-subtree bst)
  (caddr bst))

;; Returns the size of the tree bst.
(define (size bst)
  (cond ((abst-empty? bst) 0)
        (else (cadddr bst))))

;; Determines if the tree bst is empty.
(define (abst-empty? bst)
  (equal? bst '()))

;; Returns the size of the tree bst.
(define (count-nodes bst)
  (cond ((abst-empty? bst) 0)
        ((equal? 0 bst) 0)
        ((number? bst) 1)
        (else (+ (+ 1 (count-nodes (left-subtree bst))) (count-nodes (right-subtree bst))))))

;; Determines if the tree bst is a leaf node.
(define (is-leaf? bst)
  (and (equal? 0 (size (left-subtree bst))) (equal? 0 (size (right-subtree bst)))))
  
;; Returns the augmented binary search tree that results from inserting x into binary-searchtree bst.
;; Function f is a predicate that accepts two elements of the type contained in the tree and returns true if the left operand is less than the right.
(define (abst-insert bst f x)
  (cond ((abst-empty? bst) (abst-create x '() '()))
        ((f x (root bst)) (abst-create (root bst) (abst-insert (left-subtree bst) f x) (right-subtree bst)))
        ((f (root bst) x) (abst-create (root bst) (left-subtree bst) (abst-insert (right-subtree bst) f x)))
        (else bst)))

;; Returns true if bst contains element x as defined by the predicate f and false otherwise.
;; Function f is a predicate that accepts two elements of the type contained in the tree and returns true if the
;; two elements are equal and false otherwise. Function g is a predicate that accepts two elements of the type
;; contained in the tree and returns true if the left operand is less than the right.
(define (abst-contains bst f g x)
  (cond ((abst-empty? bst) #f)
        ((g x (root bst)) (abst-contains (left-subtree bst) f g x))
        ((g (root bst) x) (abst-contains (right-subtree bst) f g x))
        (else (f x (root bst)))))

;; Returns the position of x in an in-order listing of bst. Predicates f and g are identical to those described in abst-contains. This method must not have a linear runtime.
(define (abst-position bst f g x)
  (position-helper bst f g x 1))

;; Returns the position of x in an in-order listing of bst. Predicates f and g are identical to those described in abst-contains.
;; Index is used to keep track of the current index. Returns -1 if the tree is empty or does not contain x. 
(define (position-helper bst f g x index)
  (cond ((abst-empty? bst) -1)
        ((not (abst-contains bst f g x)) -1)
        ((g x (root bst)) (position-helper (left-subtree bst) f g x index))
        ((g (root bst) x) (position-helper (right-subtree bst) f g x (+ 1 (+ index (size (left-subtree bst))))))
        (else (+ index (size (left-subtree bst))))))

;; Returns the nth element of bst in an in-order listing of bst. Returns -1 if n is greater than the size of bst.
(define (abst-nth bst n)
  (cond ((abst-empty? bst) 0)
        ((> n (size bst)) -1)
        ((equal? n (+ 1 (size (left-subtree bst)))) (root bst))
        ((< (size (left-subtree bst)) n) (abst-nth (right-subtree bst) (- n (+ 1 (size (left-subtree bst))))))
        (else (abst-nth (left-subtree bst) n))))

;; Returns the augmented binary search tree representing bst after removing x where f and g are predicates as defined in bst-contains.
(define (abst-remove bst f g x)
  (cond ((not (abst-contains bst f g x)) bst)
        ((g x (root bst)) (abst-create (root bst) (abst-remove (left-subtree bst) f g x) (right-subtree bst)))
        ((g (root bst) x) (abst-create (root bst) (left-subtree bst) (abst-remove (right-subtree bst) f g x)))
        (else (cond ((is-leaf? bst) '())
                    ((and (< 0 (size (left-subtree bst))) (< 0 (size (right-subtree bst))))
                         (abst-create (find-replacement bst f g) (abst-remove (left-subtree bst) f g (find-replacement bst f g)) (abst-remove (right-subtree bst) f g (find-replacement bst f g))))
                    ((< 0 (size (left-subtree bst))) (left-subtree bst))
                    ((< 0 (size (right-subtree bst))) (right-subtree bst))))))

;; Returns a subtree to replace a node with two children that is being removed.
(define (find-replacement bst f g)
  (cond ((not (abst-empty? (left-subtree (right-subtree bst)))) (root (left-subtree (right-subtree bst))))
        ((not (abst-empty? (right-subtree (left-subtree bst)))) (root (right-subtree (left-subtree bst))))
        (else (root (right-subtree bst)))))

;; Returns the elements of bst in pre-order sequence.
(define (abst-pre-elements bst)
  (cond ((abst-empty? bst) '())
        (else (append (list (root bst)) (abst-pre-elements (left-subtree bst)) (abst-pre-elements (right-subtree bst))))))

;; Returns the elements of bst in in-order sequence.
(define (abst-in-elements bst)
   (cond ((abst-empty? bst) '())
        (else (append (abst-in-elements (left-subtree bst)) (list (root bst)) (abst-in-elements (right-subtree bst))))))

;; Returns the elements of bst in post-order sequence.
(define (abst-post-elements bst)
  (cond ((abst-empty? bst) '())
        (else (append (abst-post-elements (left-subtree bst)) (abst-post-elements (right-subtree bst)) (list (root bst))))))

;; Returns the abst that results from adding each element in list xs to an empty bst in the
;; order they occur in xs using f as the bst-insert predicate.
(define (list->abst xs f)
  (to-tree-helper (abst-create-empty) xs f))

;; A helper function that retuns a new tree that results from inserting each element of the list xs into the tree tree, using the predicate f. 
(define (to-tree-helper tree xs f)
  (if (equal? xs '()) tree (to-tree-helper (abst-insert tree f (car xs)) (cdr xs) f)))

(provide abst-create-empty abst-create abst-insert abst-contains abst-position abst-nth abst-remove abst-pre-elements abst-in-elements abst-post-elements list->abst)
