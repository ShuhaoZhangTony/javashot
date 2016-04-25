This project uses Java instrumentation capabilities to captures the dynamic execution flow of a Java program, which enable you to better understand the execution of your Java program.<br>
The program generates a set of .dot files which can bee visualized using the tools from (<a href='http://www.graphviz.org/'>http://www.graphviz.org/</a>). <br>
use ZGRViewer or xdot.py (<a href='http://www.graphviz.org/Resources.php'>http://www.graphviz.org/Resources.php</a>) for viewing large graphs. You can also use gvpr or standard unix grep to extract a subset of a large graph.<br>
The generated graph can be seen as UML activity diagram (which is by the way isomorph to the UML sequence diagram).<br>
I've included a gawk script in the scripts directory that pretty prints the generated .dot files to a format reminiscent of UML sequence diagram. You can use it like this: <b><code>gawk -f sequence.awk yourDotFile.dot | less </code></b>  (Windows users can download "Minimalist GNU for Windows" <a href='http://www.mingw.org/'>http://www.mingw.org/</a>). This will help you understand the execution flow of a Java program and draw UML sequence diagrams for an undocumented Use Case of your Java application.<br>
The project uses javassist for code instrumentation (you have to download this library for your own and put it inside a "lib" folder with the source code).<br>

<b>How To use this library :</b> <br>
<pre><code>1 - You need a Java runtime 1.6 (or higher).<br>
2 - Download javassist.jar (from http://www.jboss.org/javassist/downloads).<br>
3 - Download javashot.zip (via Downloads tab) and then extract it to a directory of your choice. <br>
You now have a directory like this "myChoiceDir/javashot".<br>
4 - Add an Environment Variables in your System, called JAVASHOT_HOME which contains the path to the directory you just created (in our example "myChoiceDir/javashot").<br>
5 - Put javassist.jar inside "myChoiceDir/javashot/lib/".<br>
6 - Open file "myChoiceDir/javashot/javashot.properties" to configure the capture.<br>
7 - Run the application you want to instrument like this:<br>
-&gt; For a standalone application:<br>
java MyClass -javaagent:myChoiceDir/javashot/dist/javashot.jar<br>
-&gt; For an embedded application inside an application server you need to add the following JVM argument "-javaagent:myChoiceDir/javashot/dist/javashot.jar".<br>
8 - All the captured data will be kept in myChoiceDir/javashot/capture directory.<br>
</code></pre>
<b> Screen Shots </b> <br> The section download contains some screen shots <code>(*.png)</code>
Also the tutorial <a href='http://code.google.com/p/javashot/wiki/HitchhikersGuideToTheJettyServer'>http://code.google.com/p/javashot/wiki/HitchhikersGuideToTheJettyServer</a> describes in depth the process used in generating thoses screen shots <br>
<b> Source Code </b><br>
You can download the source code (via Source tab), this is an eclipse project.<br>
<br>
If you encounter any problem, have a suggestion or comment please email <a href='mailto:javashotj@gmail.com'>mailto:javashotj@gmail.com</a>