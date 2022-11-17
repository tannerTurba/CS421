#lang racket

;; Returns a graph with no edges.
(define (graph-create) '())

;; Returns the graph that results from removing node n from graph g.
(define (graph-remove-node g n)
  (filter (lambda (edge) (not (member n edge))) g))

;; Returns the graph that results from adding edge e to graph g. The edge is a list of length two.
(define (graph-add-edge g e)
  (cond ((null? g) e)
        (else (cons (car g) (graph-add-edge (cdr g) e)))))

;; Returns the graph that results from adding the edges expressed as two lists, froms and tos, to graph g.
(define (graph-add-edges g froms tos)
  (cond ((or (null? froms) (null? tos)) '())
        (else (cons (list (car froms) (car tos)) (graph-add-edges g (cdr froms) (cdr tos))))))

;; Returns a list of nodes that form a path from node s to node t. Returns the empty list if no such path exists.
(define (graph-shortest-path g s t)
  (cond ((null? g) '())
        ((equal? s t) (list t))
        (else (let ((shortest-path
                     (get-shortest (filter (lambda (path) (not (null? path)))
                                           (map (lambda (x) (graph-shortest-path (graph-remove-node g s) x t))
                                                (get-adjacent-nodes g s))))))
                 (if (null? shortest-path) '() (cons s shortest-path))))))


;; A helper function that finds nodes adjecent to the node N in the graph G.
(define (get-adjacent-nodes G N)
  (cond ((null? G) '())
        ((equal? (car (car G)) N) (cons (cadr (car G)) (get-adjacent-nodes (cdr G) N)))
        (else (get-adjacent-nodes (cdr G) N))))

;; Accepts a list-of-paths named paths and returns only the shortest path, following the start node s. x
(define (get-shortest paths)
  (cond ((null? paths) '())
        (else (car (sort paths (lambda (p1 p2) (< (length p1) (length p2))))))))

(provide graph-create graph-remove-node graph-add-edge graph-add-edges graph-shortest-path)
