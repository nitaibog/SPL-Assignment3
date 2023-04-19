# All Targets
all: BGSclient

# Tool invocations
# Executable "BGSclient" depends on the files inside bin folder
BGSclient:  bin/ClientMain.o bin/ConnectionHandler.o bin/task.o
	@echo 'Building target: BGSclient'
	@echo 'Invoking: C++ Linker'
	g++ -o bin/BGSclient bin/ClientMain.o bin/ConnectionHandler.o bin/task.o  -lboost_system -lpthread
	@echo 'Finished building target: BGSclient'
	@echo ' '


# Depends on the source and header files

bin/ClientMain.o: src/ClientMain.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/ClientMain.o src/ClientMain.cpp

# Depends on the source and header files
bin/ConnectionHandler.o: src/ConnectionHandler.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/ConnectionHandler.o src/ConnectionHandler.cpp

bin/task.o: src/task.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/task.o src/task.cpp


#Clean the build directory
clean:
	rm -f bin/*