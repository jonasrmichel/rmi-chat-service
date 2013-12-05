rmi-chat-service
================

This is a distributed chat room service built using the Java Remote Method Invocation (RMI) framework. In the chat service <i>chat room providers</i> may create and subsequently register any number of chat rooms with the service’s registry (not to be confused with the Java RMI registry). Chat clients may query the service’s registry for registered chat rooms, join them, and send messages to the room’s participants.

<h2>Usage</h2>
The chat service's components may be started from the command line.

From the command line, cd to the directory containing this file.
<pre><code>$ cd /path/to/this/directory</code></pre>

Temporarily configure your machine's <code>CLASSPATH</code>.
<pre><code>$ export CLASSPATH=$CLASSPATH:/path/to/this/directory/bin</code></pre>

Start the Java RMI registry.
<pre><code>$ rmiregistry &</code></pre>

Start the chat service's registry.
<pre><code>$ java -cp bin \</code>
<code>    -Djava.rmi.server.codebase=file:bin/ \</code>
<code>    -Djava.security.policy=file:src/edu/courses/middleware/chatservice/policy \</code>
<code>    edu.courses.middleware.chatservice.ChatRegistry</code></pre>
    
Next, you may start any number of chat room providers each in their own command line window.
To start a chat room provider open a new command line window and perform the first step.
Then you may start the chat room provider providing the hostname of the machine on which the
chat service registry is running. This will be <code>localhost</code> if using one machine.
<pre><code>$ java -cp bin \</code>
<code>    -Djava.rmi.server.codebase=file:bin/ \</code>
<code>    -Djava.security.policy=file:src/edu/courses/middleware/chatservice/policy \</code>
<code>    edu.courses.middleware.chatservice.ChatProvider localhost</code></pre>
    
A chat room provider may be controlled using its command line interface (CLI).
Once a chat room provider is started you will be prompted for a screen name.
Enter a screen name to register your chat room provider's client with the chat registry.
You may then operate the chat room provider using valid commands.
At any time you use the command "h" (help) to see a list of available commands and their usage.

<h2>Note</h2>
Chat room providers aren't required to run on the same machine as the chat service registry.
The only requirement is that chat room providers are provided the hostname of the machine
on which the chat service registry is running.
