#include "../include/task.h"
#include <thread>
#include "../include/ConnectionHandler.h"
#include <boost/asio.hpp>


task::task(std::string host, short port, int id) : connectionHandler(host,port), name("CLIENT#"), logOut(false), id(id){}


task::task(const task& other):connectionHandler("42",2),name(other.name), logOut(other.logOut) , id(other.id){}

task:: ~task(){
    this->connectionHandler.close();
}
void task::runReadFromServer() {
    while (!this->logOut && !connectionHandler.isLogOut()) {
        if (!connectionHandler.isLogOut()) {
            std::string answer;
            connectionHandler.getLine(answer);
            if (!answer.empty()) {
                std::cout << this->name + " " + answer << std::endl;
            }
            std::string::size_type index(answer.find('#', 0));
            std::string s = answer.substr(index + 1, index + 6);
            if (s.compare("ACK 3") == 0) {
                this->logOut = true;
                connectionHandler.close();
            }
        }
    }
}

void task::runWriteToServer() {
    while (!this->logOut && !connectionHandler.isLogOut()) {
        if (!connectionHandler.isLogOut()) {
            const short bufsize = 1024;
            char buf[bufsize];
            std::cin.getline(buf, bufsize);
            std::string line(buf);

            connectionHandler.sendLine(line);
        }
    }
}
bool task::toClose() {
    return this->connectionHandler.isLogOut();
}
ConnectionHandler &task::getConnectionHandler() {
    return this->connectionHandler;
}