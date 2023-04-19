
#ifndef CLIENT_TASK_H
#define CLIENT_TASK_H

#endif //CLIENT_TASK_H
#include <thread>
#include "ConnectionHandler.h"

class task{


public:
    task(std::string host, short port, int id);
    task(const task& other);

    ~task();

    void runReadFromServer();
    void runWriteToServer();
    bool toClose();
    ConnectionHandler& getConnectionHandler();
private:
    ConnectionHandler connectionHandler;
    std::string name;
    bool logOut;
    int id;
};