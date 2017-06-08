# KGP

[![Documentation Status](https://readthedocs.org/projects/kgp/badge/)](https://kgp.readthedocs.io/en/latest/)

*Genetic Programming framework for Kotlin.*

## About

KGP provides an implementation of tree-based genetic programming for the Kotlin language. The framework aims to be idiomatic to Kotlin as well as providing good performance for solving problems. 

KGP can be used to solve Symbolic Regression problems and features full interoperability with other JVM languages.

A guide describing the concepts of the system as well as high level usage instructions can be found [here](http://kgp.readthedocs.io/en/latest/).

Documentation for the API can be viewed [here](https://jeds6391.github.io/KGP/) for the technically minded.

## Usage

To get started using KGP a JAR build of the system can be downloaded from [here](https://github.com/JedS6391/KGP/releases/download/v0.1/KGP-1.0-SNAPSHOT.jar).

This binary can be used in two primary ways: Running the included examples or defining your own problems.

### Running included examples

The examples can be run from the command-line with the following command:

```
java -cp KGP-1.0-SNAPSHOT.jar kgp.examples.<PROBLEM>
```

where `<PROBLEM>`` is one of the problems given below:

- BasicRegressionProblem
- QuarticPolynomial
- Keijzer6
- Korns12
- Nguyen7
- Pagie1
- Vladislavleva4

For example:

```
> java -cp KGP-1.0-SNAPSHOT.jar kgp.examples.Keijzer6
Population Size: 500
Generations: 100
Tournament Size: 20
Stopping Threshold: 0.01
Crossover Rate: 0.7
Subtree Mutation Rate: 0.1
Hoist Mutation Rate: 0.05
Point Mutation Rate: 0.1
Point Replacement Rate: 0.05
Number of Offspring: 10
Function Set: [+, *, 1 /, -, sqrt]
Tree Initialistation Method: HalfAndHalf
Max Depth: 5
Number of Features: 1
Constants: [0.0, 1.0]
Fitness Function: MSE

Generation #0    Best Fitness: 0.1277353342242149    Best Length: 15
Generation #1    Best Fitness: 0.1277353342242149    Best Length: 15
Generation #2    Best Fitness: 0.1277353342242149    Best Length: 15
Generation #3    Best Fitness: 0.1277353342242149    Best Length: 15
Generation #4    Best Fitness: 0.1277353342242149    Best Length: 15
Generation #5    Best Fitness: 0.1277353342242149    Best Length: 15
Generation #6    Best Fitness: 0.1277353342242149    Best Length: 15
Generation #7    Best Fitness: 0.1277353342242149    Best Length: 15
Generation #8    Best Fitness: 0.1277353342242149    Best Length: 15
Generation #9    Best Fitness: 0.03355846193400315   Best Length: 26
Generation #10   Best Fitness: 0.03355846193400315   Best Length: 26
Generation #11   Best Fitness: 0.03355846193400315   Best Length: 26
Generation #12   Best Fitness: 0.03355846193400315   Best Length: 26
Generation #13   Best Fitness: 0.03355846193400315   Best Length: 26
Generation #14   Best Fitness: 0.03355846193400315   Best Length: 26
Generation #15   Best Fitness: 0.03355846193400315   Best Length: 26
Generation #16   Best Fitness: 0.017197977253510206  Best Length: 30
Generation #17   Best Fitness: 0.017197977253510206  Best Length: 30
Generation #18   Best Fitness: 0.017197977253510206  Best Length: 30
Generation #19   Best Fitness: 0.017197977253510206  Best Length: 30
Generation #20   Best Fitness: 0.017197977253510206  Best Length: 30
Generation #21   Best Fitness: 0.017197977253510206  Best Length: 30
Generation #22   Best Fitness: 0.017197977253510206  Best Length: 30
Generation #23   Best Fitness: 0.017197977253510206  Best Length: 30
Generation #24   Best Fitness: 0.017197977253510206  Best Length: 30
Generation #25   Best Fitness: 0.017197977253510206  Best Length: 30
Generation #26   Best Fitness: 0.015732085486019953  Best Length: 57
Generation #27   Best Fitness: 0.015732085486019953  Best Length: 57
Generation #28   Best Fitness: 0.015732085486019953  Best Length: 57
Generation #29   Best Fitness: 0.013724001360242776  Best Length: 31
Generation #30   Best Fitness: 0.013724001360242776  Best Length: 31
Generation #31   Best Fitness: 0.01352012954043422   Best Length: 29
Generation #32   Best Fitness: 0.01352012954043422   Best Length: 29
Generation #33   Best Fitness: 0.01352012954043422   Best Length: 29
Generation #34   Best Fitness: 0.01352012954043422   Best Length: 29
Generation #35   Best Fitness: 0.012942682644407491  Best Length: 30
Generation #36   Best Fitness: 0.012942682644407491  Best Length: 30
Generation #37   Best Fitness: 0.012942682644407491  Best Length: 30
Generation #38   Best Fitness: 0.012942682644407491  Best Length: 30
Generation #39   Best Fitness: 0.012942682644407491  Best Length: 30
Generation #40   Best Fitness: 0.012942682644407491  Best Length: 30
Generation #41   Best Fitness: 0.012942682644407491  Best Length: 30
Generation #42   Best Fitness: 0.012942682644407491  Best Length: 30
Generation #43   Best Fitness: 0.012942682644407491  Best Length: 30
Generation #44   Best Fitness: 0.012942682644407491  Best Length: 30
Generation #45   Best Fitness: 0.012942682644407491  Best Length: 30
Generation #46   Best Fitness: 0.012942682644407491  Best Length: 30
Generation #47   Best Fitness: 0.012942682644407491  Best Length: 30
Generation #48   Best Fitness: 0.012942682644407491  Best Length: 30
Generation #49   Best Fitness: 0.012942682644407491  Best Length: 30
Generation #50   Best Fitness: 0.012942682644407491  Best Length: 30
Generation #51   Best Fitness: 0.012942682644407491  Best Length: 30
Generation #52   Best Fitness: 0.012942682644407491  Best Length: 30
Generation #53   Best Fitness: 0.012942682644407491  Best Length: 30
Generation #54   Best Fitness: 0.012942682644407491  Best Length: 30
Generation #55   Best Fitness: 0.012942682644407491  Best Length: 30
Generation #56   Best Fitness: 0.012942682644407491  Best Length: 30
Generation #57   Best Fitness: 0.012942682644407491  Best Length: 30
Generation #58   Best Fitness: 0.012942682644407491  Best Length: 30
Generation #59   Best Fitness: 0.012942682644407491  Best Length: 30
Generation #60   Best Fitness: 0.012942682644407491  Best Length: 30
Generation #61   Best Fitness: 0.012942682644407491  Best Length: 30
Generation #62   Best Fitness: 0.012942682644407491  Best Length: 30
Generation #63   Best Fitness: 0.012942682644407491  Best Length: 30
Generation #64   Best Fitness: 0.012942682644407491  Best Length: 30
Generation #65   Best Fitness: 0.012942682644407491  Best Length: 30
Generation #66   Best Fitness: 0.012942682644407491  Best Length: 30
Generation #67   Best Fitness: 0.012942682644407491  Best Length: 30
Generation #68   Best Fitness: 0.012942682644407491  Best Length: 30
Generation #69   Best Fitness: 0.012942682644407491  Best Length: 30
Generation #70   Best Fitness: 0.012942682644407491  Best Length: 30
Generation #71   Best Fitness: 0.012942682644407491  Best Length: 30
Generation #72   Best Fitness: 0.012758801796382654  Best Length: 34
Generation #73   Best Fitness: 0.012758801796382654  Best Length: 34
Generation #74   Best Fitness: 0.012758801796382654  Best Length: 34
Generation #75   Best Fitness: 0.012758801796382654  Best Length: 34
Generation #76   Best Fitness: 0.012758801796382654  Best Length: 34
Generation #77   Best Fitness: 0.012758801796382654  Best Length: 34
Generation #78   Best Fitness: 0.012758801796382654  Best Length: 34
Generation #79   Best Fitness: 0.012758801796382654  Best Length: 34
Generation #80   Best Fitness: 0.012758801796382654  Best Length: 34
Generation #81   Best Fitness: 0.012758801796382654  Best Length: 34
Generation #82   Best Fitness: 0.012758801796382654  Best Length: 34
Generation #83   Best Fitness: 0.012758801796382654  Best Length: 34
Generation #84   Best Fitness: 0.012758801796382654  Best Length: 34
Generation #85   Best Fitness: 0.012758801796382654  Best Length: 34
Generation #86   Best Fitness: 0.012758801796382654  Best Length: 34
Generation #87   Best Fitness: 0.012758801796382654  Best Length: 34
Generation #88   Best Fitness: 0.012758801796382654  Best Length: 34
Generation #89   Best Fitness: 0.012758801796382654  Best Length: 34
Generation #90   Best Fitness: 0.012758801796382654  Best Length: 34
Generation #91   Best Fitness: 0.012758801796382654  Best Length: 34
Generation #92   Best Fitness: 0.012758801796382654  Best Length: 34
Generation #93   Best Fitness: 0.012758801796382654  Best Length: 34
Generation #94   Best Fitness: 0.012758801796382654  Best Length: 34
Generation #95   Best Fitness: 0.012758801796382654  Best Length: 34
Generation #96   Best Fitness: 0.012758801796382654  Best Length: 34
Generation #97   Best Fitness: 0.012758801796382654  Best Length: 34
Generation #98   Best Fitness: 0.012758801796382654  Best Length: 34
Generation #99   Best Fitness: 0.012758801796382654  Best Length: 34
Generation #100  Best Fitness: 0.012758801796382654  Best Length: 34

(sqrt (sqrt (* (+ (sqrt x[0]) (sqrt x[0])) (+ (sqrt (* (+ (sqrt x[0]) (sqrt x[0])) (+ x[0] (+ (sqrt (- (- (+ x[0] x[0])))) (sqrt (- (- (+ x[0] x[0])))))))) (* 1.0 0.0)))))
```

### Defining a problem

Defining your own problems is as simple as following the [usage guide](http://kgp.readthedocs.io/en/latest/usage.html/) from the documentation and including the right packages where necessary. 

The JAR can be built against using the standard Java compiler as follows:

```
javac -cp KGP-1.0-SNAPSHOT.jar MyProblemDefition.java
```

Or alternatively, if the problem is defined using Kotlin:

```
kotlinc -cp KGP-1.0-SNAPSHOT.jar MyProblemDefinition.kt
```


