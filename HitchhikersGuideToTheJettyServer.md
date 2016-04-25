This document describes the steps used to understand the start up process of Jetty Server using javashot, Graphviz tools and Unix like tools. This same process can be used to analyse your own Java applications. So Don't Panic, take your towel and follow me ;-)

I'm using GNU-Linux (Fedora12 to be precise), Windows user's can also benefit from this tutorial if they have a GNU like system installed.

**1. Download javashot as described in the document: http://code.google.com/p/javashot/wiki/javashot** <br>
<b>2. Customize javashot.properties<br>
<pre><code># startCaptureAt: The short name of the class at which the capture will start<br>
startCaptureAt = Server<br>
<br>
# instrumentationClassPattern: comma separated pattern (regexp) for the classes to be instrumented; if not specified no class will be instrumented<br>
instrumentationClassPattern = .*jetty.*<br>
<br>
#javassitExtraClassPath: comma separated locations for classes and jars required by javassist during instrumentation (if the need arises: specially <br>
# during instrumentation of a programs running on a web application server such as JBoss and Tomcat,)<br>
javassitExtraClassPath = /usr/share/jetty/lib/jetty-6.1.20.jar,/usr/share/jetty/lib/[tomcat6-servlet-2.5-api].jar,/usr/share/jetty/lib/jetty-util-6.1.20.jar<br>
</code></pre></b> <br>
<b>3. Type this in your shell:<br>
<pre><code>export JAVA_OPTIONS=-javaagent:/YourPath/javashot/dist/javashot.jar<br>
</code></pre></b> <br>
<b>4. Type this in your shell:<br>
<pre><code>jetty start&amp;<br>
</code></pre></b> <br>
<b>5. Type this in your shell:<br>
<pre><code>jetty stop<br>
</code></pre></b> <br>
<b>6. Now you go inside the directory <code>/YourPath/javashot/capture/</code> you will find a directory created with the capture's date, inside this directory you will find .dot corresponding to the capture, this file has the following form Server_HHMMSS.dot</b><br>
<br>
<i>7. There is alos another problem with this capture, the .dot file contain's the $ character which confuse graphviz tools, so we need to convert $ to another char say <b>, here is how I do it<br>
<pre><code>cat Server_HHMMSS.dot| tr $ _  &gt; Server_HHMMSS_2.dot<br>
</code></pre></b></i><br>
<b>8. The generated Server_HHMMSS.dot contains a lot of information and can't be processed immediately by the graphviz tools, this will take a long time and the resulting image will be too confusing (the use of xdot.py or ZGRViewer may help here). Are  we stuck ? No Unix tools come to the rescue (Secrete Weapon: Unix tools are powerful weapon in the right hand, but the sad story is that most developers simply ignore theme. A good place to start is Unix for Poets ( <a href='http://www.sslmit.unibo.it/~baroni/compling04/UnixforPoets.pdf'>http://www.sslmit.unibo.it/~baroni/compling04/UnixforPoets.pdf</a> )):<br>
<pre><code>gawk 'BEGIN{FS="-&gt;"} {print $1}' Server_HHMMSS_2.dot | sort | uniq -c | sort -nr<br>
</code></pre>
this command will extract all the classes name from the .dot file with there occurrence.</b><br>
here is the result:<br>
<pre><code>  1230 WebXmlConfiguration<br>
   1213 ServletHandler<br>
   1138 SelectChannelConnector<br>
   1045 Server<br>
    742 ServletHolder<br>
    729 WebAppClassLoader<br>
    632 PathMap<br>
    573 WebAppContext<br>
    518 Holder<br>
    428 PathMap$Entry<br>
    413 ContextHandler<br>
    328 ServletHolder$Config<br>
    227 ServletMapping<br>
    224 DefaultServlet<br>
    220 AbstractHandler<br>
    201 HandlerWrapper<br>
    188 ResourceHandler<br>
    181 MimeTypes<br>
    133 ContextHandler$SContext<br>
    120 FilterMapping<br>
    108 SecurityHandler<br>
    105 TagLibConfiguration<br>
     89 AbstractConfiguration<br>
     86 Server$ShutdownHookThread<br>
     84 ContextDeployer<br>
     80 ConstraintMapping<br>
     77 HandlerCollection<br>
     70 SessionHandler<br>
     70 AbstractConnector<br>
     68 NamingEntry<br>
     61 FilterHolder<br>
     60 Context<br>
     55 AbstractHandlerContainer<br>
     51 HashUserRealm<br>
     50 EnvConfiguration<br>
     48 ResourceCache<br>
     47 AbstractSessionManager<br>
     38 HashSessionManager<br>
     36 FilterHolder$Config<br>
     32 Constraint<br>
     30 HashUserRealm$User<br>
     25 ContextDeployer$1<br>
     21 ContextHandlerCollection<br>
     21 Configuration<br>
     19 ErrorPageErrorHandler<br>
     18 JettyWebXmlConfiguration<br>
     14 WebInfConfiguration<br>
     12 HashUserRealm$KnownUser<br>
     12 Credential<br>
     10 IncludableGzipFilter<br>
     10 AbstractConnector$Acceptor<br>
      9 WebAppDeployer<br>
      8 ServletHolder$SingleThreadedWrapper<br>
      8 EnvEntry<br>
      7 ContextDeployer$ScannerListener<br>
      6 RequestLogHandler<br>
      6 HashUserRealm$WrappedUser<br>
      5 Password<br>
      4 NCSARequestLog<br>
      4 Dispatcher<br>
      4 DefaultServlet$NIOResourceCache<br>
      4 Context$SContext<br>
      4 AbstractBuffers<br>
      3 ErrorHandler<br>
      2 SecurityHandler$NotChecked<br>
      2 Resource<br>
      2 Link<br>
      2 HashSessionManager$2<br>
      2 HashSessionIdManager<br>
      2 FormAuthenticator<br>
      2 Credential$MD5<br>
      1 _START_<br>
      1 HttpGenerator<br>
      1 digraph Server{<br>
      1 Credential$Crypt<br>
      1 AbstractSessionManager$NullSessionContext<br>
      1 AbstractGenerator<br>
      1 AbstractBuffers$1<br>
      1 }<br>
</code></pre>
from this result we immediately notice that the classes most used during the startup process are: <code>WebXmlConfiguration</code> (1230 occurrences), <code>ServletHandler</code> (1213 occurrences),   <code>SelectChannelConnector</code> (1138 occurrences), <code>Server</code> (1045 occurrences).<br>
<br>
<b>9. Now you can choose whatever class you are interested in, I choose the Credential (12 occurrences) because the resulting images are not too huge too be integrated here:<br>
<pre><code>grep Credential Server_HHMMSS_2.dot | gawk 'BEGIN {print "digraph g {"} {print} END {print "}"}' | dot -Tpng &gt; dotJettyCredential.png <br>
</code></pre>
Here is the resulting image : <img src='http://javashot.googlecode.com/files/dotJettyCredential.png' /></b><br> or you can try another look:<br>
<pre><code>grep Credential Server_HHMMSS_2.dot  | gawk 'BEGIN {print "digraph g {"} {print} END {print "}"}' | circo -Tpng &gt; circoJettyCredential.png<br>
</code></pre>
Here is the resulting image : <img src='http://javashot.googlecode.com/files/circoJettyCredential.png' />
<br>
<b>10. We can alos look at the sequence diagram using the sequence.awk script inside javashot's scripts  directory :<br>
<pre><code>grep Credential Server_HHMMSS_2.dot  | gawk 'BEGIN {print "digraph g {"} {print} END {print "}"}' | gawk -f ../../scripts/sequence.awk<br>
</code></pre>
this gives us the following result:<br>
<pre><code>	HashUserRealm+---&gt;Credential  (154:getCredential)<br>
		          Credential+---&gt;Password  (155:Password)<br>
		          Credential&lt;---+Password  (158)<br>
	HashUserRealm&lt;---+Credential  (159)<br>
	HashUserRealm+---&gt;Credential  (168:getCredential)<br>
		          Credential+---&gt;Credential_Crypt  (169:Credential_Crypt)<br>
		          Credential&lt;---+Credential_Crypt  (170)<br>
	HashUserRealm&lt;---+Credential  (171)<br>
	HashUserRealm+---&gt;Credential  (186:getCredential)<br>
		          Credential+---&gt;Credential_MD5  (187:Credential_MD5)<br>
		          Credential&lt;---+Credential_MD5  (188)<br>
	HashUserRealm&lt;---+Credential  (189)<br>
	HashUserRealm+---&gt;Credential  (200:getCredential)<br>
		          Credential+---&gt;Password  (201:Password)<br>
		          Credential&lt;---+Password  (202)<br>
	HashUserRealm&lt;---+Credential  (203)<br>
	HashUserRealm+---&gt;Credential  (212:getCredential)<br>
		          Credential+---&gt;Password  (213:Password)<br>
		          Credential&lt;---+Password  (214)<br>
	HashUserRealm&lt;---+Credential  (215)<br>
	HashUserRealm+---&gt;Credential  (224:getCredential)<br>
		          Credential+---&gt;Credential_MD5  (225:Credential_MD5)<br>
		          Credential&lt;---+Credential_MD5  (226)<br>
	HashUserRealm&lt;---+Credential  (227)<br>
</code></pre></b> <br>
<b>11. From now on you can write wathever hichhiker's guide you like :"The Hichhiker's Guide To Tomcat", "The Hichhicker's Guide to JBoss", the possibilities are infinite ;-)</b>

I hope this tutorial will help you during your everyday struggle in understanding software.