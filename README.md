# Graph Coloring

Finding a minimal coloration - a coloration of the graph with a minimal number of colors - is known to be a NP-complete problem, and every graph can be colored with one more color than the maximum vertex degree (see https://en.wikipedia.org/wiki/Graph_coloring).

This Java project is an polynomial implementation finding a graph coloration with a number of coloration lower or equal than one more color than the maximum vertex degree of the graph. 

This project has been done with IntellIJ. The project directory is `project`.

## Prerequisites

- JDK 11
- Jbotsim

Jbotsim is a library from the resarch laboratory of Bordeaux. It offers a GUI for plotting graphes, see https://jbotsim.io/ to integrate Jbotsim with IntellIJ (click on `Take the tour !` under the `Quick start` section).


## How to run

Simply click on the run button from IntellIJ once a configuration done - the two main classes are `Main.java` and `CycleMain.java`.

**Note:** Please note that `CycleMain.java` only works for graph called *cycles* (https://en.wikipedia.org/wiki/Cycle_graph).

Let's have an example from `Main.java`. Once you run the configuration, you will see this window.

<img src="https://github.com/ltomas837/graphColoring/blob/main/screenshots/initialWindow.png">

Click then on the window to plot a graph: a vertex appears at each click (click again on the vertex to make it disappearing), and two vertices suffiently close make an edge. You can change the position of a vertex by dragging and dropping the vertex.
Once the desired graph is shaped, right click on the window and click on the action you want. Click on `start execution` for the program to run till the end and display the result found.  

<img src="https://github.com/ltomas837/graphColoring/blob/main/screenshots/startRunning.png">

See below an example of result: the graph includes 37 vertices, a maximum degree of 5 and the coloration find includes 5 colors.

<img src="https://github.com/ltomas837/graphColoring/blob/main/screenshots/coloredGraph.png">


