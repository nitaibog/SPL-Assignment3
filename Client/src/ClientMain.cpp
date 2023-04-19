#include <stdlib.h>
#include "../include/ConnectionHandler.h"
#include <thread>
#include "../include/task.h"

int main (int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);


    int id = 0;
    task t(host,port,id);

//    ConnectionHandler connectionHandler(host, port);
    if (!t.getConnectionHandler().connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }

    std::thread Reader (&task::runReadFromServer, &t);
    std::thread Writer (&task::runWriteToServer, &t);

    Reader.join();
    Writer.detach();
    if (t.toClose()){
        t.getConnectionHandler().close();
    }

    return 0;
}