1. How to run your code :

	Server -  1) mvn clean
			2) mvn compile
        		3) Reactor -
					   mvn exec:java -Dexec.mainClass="bgu.spl.net.impl.BGSServer.ReactorMain" -Dexec.args="<port> <NumOfThreads>" 
			    Tpc - 
					   mvn exec:java -Dexec.mainClass="bgu.spl.net.impl.BGSServer.TPCMain" -Dexec.args="<port>" 
	
	
	Client -   1) make clean
		      2) make
		      3) cd bin
            	      4) ./BGSclient <ip> <port> <AnyString>  


1.2) Example -   1) REGISTER username password 12-12-1995
				  2) LOGIN username password 1
				  3) LOGOUT
				  4) FOLLOW 0/1 userNameToFollow
				  5) POST content / POST @Tag1 @Tag2 content
				  6) PM username Content
				  7) LOGSTAT
				  8) STAT username1 username2 ..
				  9) BLOCK userNameToBlock


2. Filtered Words -
   					PATH : bgu/spl/net/impl/Messages/PM.java
 				 	Line: 12 , String array - filters
