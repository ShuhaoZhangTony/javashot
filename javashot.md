Java Dynamic Call Graph

# Introduction #
This project uses Java instrumentation capabilities to captures the dynamic flow of program execution.


# Details #

The program generates a set of .dot files which can bee visualized using the tools from (http://www.graphviz.org/) and standard unix tools such as grep, awk, ... <br>

For viewing large graphs use ZGRViewer or xdot.py (download from <a href='http://www.graphviz.org/);'>http://www.graphviz.org/);</a> you can also use gvpr or standard unix grep to extract a subset of the graph.  <br>

The generated graph can be seen as UML activity diagram (which is by the way isomorph to the UML sequence diagram). <br>

I've included a gawk script in the scripts directory that pretty prints the generated .dot files to a format reminiscent of UML sequence diagram. You can use it like this:  <b><code>gawk -f sequence.awk yourDotFile.dot | less </code></b> (Windows users can download "Minimalist GNU for Windows" <a href='http://www.mingw.org/'>http://www.mingw.org/</a>).<br>

The project uses javassist for code instrumentation (you have to download this library and put it inside the lib folder with the source code). <br>

The System property JAVASHOT_HOME must be set in you system which points to the directory containing javashot.properties file and the directory capture for ".dot" files saving. to instrument a Java project (whether its a Standard application or a J2EE one) add the following java option when you start your application:<br>
-javaagent:/yourContinaingDir/javashot.jar <br>