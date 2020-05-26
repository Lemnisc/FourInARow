 _____                _                
|  __ \              | |               
| |__) |___  __ _  __| |_ __ ___   ___ 
|  _  // _ \/ _` |/ _` | '_ ` _ \ / _ \
| | \ \  __/ (_| | (_| | | | | | |  __/
|_|  \_\___|\__,_|\__,_|_| |_| |_|\___|
                                       
									   
In case of compiler errors due to conflicting jdk versions
compiled binaries have been supplied in the bin folder.

 ___                      
/ __| ___ _ ___ _____ _ _ 
\__ \/ -_) '_\ V / -_) '_|
|___/\___|_|  \_/\___|_|  

To start the server navigate to the folder named bin_server 
and execute the file by running:

java -jar vieropeenrij.jar <PORT>

<PORT> 	- The server will listen on the specified port if it is free.
		- The server will listen on ALL available network interfaces.
		- This can be changed by editing the source files and recompiling.

    _ _         _   
 __| (_)___ _ _| |_ 
/ _| | / -_) ' \  _|
\__|_|_\___|_||_\__|

To run a client navigate to the folder named bin_client
and execute the file by running:

java -jar vieropeenrij.jar <SERVERIP> <PORT> <NAME> <HUMAN|MONTE|AI>

<SERVERIP> 	- The server's IP.
<PORT>		- The server's port.
<NAME>		- Your name
<HUMAN>		- If you'd like to play yourself.
<MONTE>		- If you'd like our "advanced" Monte Carlo AI to play for you.
<AI>		- An AI that randomly places a move. (Not very smart!)