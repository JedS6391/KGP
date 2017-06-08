Advanced Design
***************

Parsimony Pressure
==================

KGP provides the ability to use fitness functions which apply parsimony pressure. This technique allows for programs to be penalised based on their length as a means to combat bloat in the population.

Bloat is the phenomena where individuals in the population continue to grow without a corresponding increase in fitness. Parsimony pressure allows longer but better solutions to be promoted in the population, while denying longer programs that aren't any better in terms of fitness.

The ``kgp.fitness.Metric`` module provides the basic ``BaseMetric`` which is a metric with no penalty applied, but the ``ParsimonyAwareMetric`` applies a penalty based on a specified coefficient and the program length.

In combination with a high percentage of hoist mutations (a destructive operation), the population should remain relatively bloat free.

Parallelism
===========

KGP's base evolution model makes use of parallelism when evaluating the fitness of individuals in the population. This causes a dramatic speed up over sequential case-by-case evaluation.

