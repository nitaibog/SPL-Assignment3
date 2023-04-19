#include "../include/ConnectionHandler.h"
#include <string>
#include <iostream>
#include <boost/asio.hpp>
#include <mutex>
#include <boost/algorithm/string.hpp>


using boost::asio::ip::tcp;
using std::cin;
using std::cout;
using std::cerr;
using std::endl;
using std::string;


ConnectionHandler::ConnectionHandler(string host, short port): host_(host), port_(port), io_service_(), socket_(io_service_), logOut(
        false){}

ConnectionHandler::~ConnectionHandler() {
    close();
}

bool ConnectionHandler::connect() {
    std::cout << "Starting connect to "
              << host_ << ":" << port_ << std::endl;
    try {
        tcp::endpoint endpoint(boost::asio::ip::address::from_string(host_), port_); // the server endpoint
        boost::system::error_code error;
        socket_.connect(endpoint, error);
        if (error)
            throw boost::system::system_error(error);
    }
    catch (std::exception& e) {
        std::cerr << "Connection failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandler::getBytes(char bytes[], unsigned int bytesToRead) {
    size_t tmp = 0;
    boost::system::error_code error;
    try {
        while (!error && bytesToRead > tmp ) {
            tmp += socket_.read_some(boost::asio::buffer(bytes+tmp, bytesToRead-tmp), error);
        }
        if(error) {
            throw boost::system::system_error(error);
        }
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandler::sendBytes(const char bytes[], int bytesToWrite) {
    int tmp = 0;
    boost::system::error_code error;
    try {
        while (!error && bytesToWrite > tmp ) {
            tmp += socket_.write_some(boost::asio::buffer(bytes + tmp, bytesToWrite - tmp), error);
        }
        if(error)
            throw boost::system::system_error(error); /////
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandler::getLine(std::string& line) {
    return getFrameAscii(line, '\n');
}

bool ConnectionHandler::sendLine(std::string& line) {
    return sendFrameAscii(line, '\n'); //// change the delineter to ;
}

bool ConnectionHandler::getFrameAscii(std::string& frame, char delimiter) {
    short msgOpCode = getShort(frame,2);
    frame = "";
    if(msgOpCode == 9){
        return createNotification(frame);
    } else if (msgOpCode == 10){
        return Ack(frame);
    } else return Error(frame);
    return true;
}

bool ConnectionHandler::sendFrameAscii(const std::string& frame, char delimiter) {
    string::size_type indexOfSpace(frame.find(' ',0));
    std::string messageTypeByName(frame.substr(0,indexOfSpace));
    short msgOpCode(stringToOpCode(messageTypeByName));
    bool result;
    char bytes[2];
    shortToBytes(msgOpCode,bytes);
    sendBytes(bytes,2);
    if (msgOpCode == 3 || msgOpCode == 7){
        result = sendBytes(";",1);
    } else {
        string msgContent(frame.substr(indexOfSpace + 1, frame.length() - (indexOfSpace + 1)));
        string msg(messageContentByType(msgOpCode, msgContent));
        msg.append(";");
        result = sendBytes(msg.c_str(), msg.length());
    }
    return result;
}


short ConnectionHandler::stringToOpCode(std::string msgType){
    short msgOpCode;
    if (msgType == "REGISTER")
        msgOpCode = 1 ;
    else if (msgType == "LOGIN")
        msgOpCode = 2;
    else if (msgType == "LOGOUT")
        msgOpCode = 3 ;
    else if (msgType == "FOLLOW")
        msgOpCode = 4 ;
    else if (msgType == "POST")
        msgOpCode = 5;
    else if (msgType == "PM")
        msgOpCode = 6;
    else if (msgType == "LOGSTAT")
        msgOpCode = 7;
    else if (msgType == "STAT")
        msgOpCode = 8;
    else msgOpCode = 12;

    return msgOpCode;
}

std::string ConnectionHandler::messageContentByType(short msgOpCode, std::string content){
    std::vector<string> res;
    std::string msg;
    if (msgOpCode == 1) {
        boost::split(res, content, [](char c) { return c == ' '; });
        msg = res[0] + '\0' + res[1] + '\0' + res[2];
        return msg;
    } else if (msgOpCode == 2){
        boost::split(res, content, [](char c) { return c == ' '; });
        msg = res[0] + '\0' + res[1] + '\0' + res[2] + '\0' ;
        return msg;
    } else if (msgOpCode == 4){
        boost::split(res, content, [](char c) { return c == ' '; });
        msg = res[0] + res[1];
        return msg;
    } else if (msgOpCode == 5) {
        return content;
    } else if (msgOpCode == 6){
        boost::split(res, content, [](char c) { return c == ' '; });
        time_t now = time(0);
        tm *tets = localtime(&now);
        msg = res[0] + '\0';
        for (unsigned int i = 1; i < res.size(); ++i) {
            msg.append(res[i] + '\0');
        }
        string date(std::to_string(tets->tm_mday) + "-" + std::to_string(tets->tm_mon + 1) + "-" + "2022" + '\0');
        msg.append(date);
        return msg;
    } else if (msgOpCode == 8 ){
        return content;
    } else return content;

}

void ConnectionHandler::shortToBytes(short num, char* bytesArr){
    bytesArr[0] = ((num >> 8) & 0xFF);
    bytesArr[1] = (num & 0xFF);
}

short ConnectionHandler::bytesToShort(char* bytesArr){
    short result = (short)((bytesArr[0] & 0xff) << 8);
    result += (short)(bytesArr[1] & 0xff);
    return result;
}
// Close down the connection properly.
void ConnectionHandler::close() {
    try{
        socket_.close();
    } catch (...) {
        std::cout << "closing failed: connection already closed" << std::endl;
    }
}

short ConnectionHandler::getShort(std::string& frame, int count){
    char ch;
    int count2(0);
    try {
        do{
            if (getBytes(&ch, 1)) {
                if (ch != ';') {
                    frame.append(1, ch);
                }
                count2++;
            }
        } while (count2 < count && ch != ';');
    } catch (std::exception& e) {
        return -1;
    }
    char bytes[2];
    if (ch == ';'){
        return -1;
    }
    if (count == 2){
        bytes[0] = frame[0];
        bytes[1] = frame[1];
        return bytesToShort(bytes);
    }
    char test[2];
    test[0] = 0;
    test[1] = frame[0];
    return bytesToShort(test);
}


bool ConnectionHandler::getString(std::vector<char>& frame){
    char ch;
    try {
        do{
            if (getBytes(&ch, 1)) {
                if (ch != '\0') {
                    frame.push_back(ch);
                } else {
                    frame.push_back(' ');
                    return true;
                }
            } else {
                return false;
            }
        } while (ch != '\0');
    } catch (std::exception& e) {
        return false;
    }
    return true;
}

bool ConnectionHandler::Error(std::string& frame){
    short myMsgOpCode = getShort(frame,2);
    if(myMsgOpCode == -1)
        return false;

    frame = "ERROR " + std::to_string(myMsgOpCode);
    return true;
}


bool ConnectionHandler::Ack(std::string& frame){
    short myMsgOpCode = getShort(frame, 2);
    if (myMsgOpCode == -1)
        return false;

    frame = "";

    if(myMsgOpCode == 4){
        std::vector<char> frameVector;
        getString(frameVector);
//        short follow = getShort(frame,1);
        frame = "";
        string name(frameVector.data(),frameVector.size());
        frame = "ACK 4 " + name;
        return true;
    }
    else if (myMsgOpCode == 7 || myMsgOpCode == 8){
        std::vector<std::vector<short>> frameVector;
        short temp[4];
        std::vector<short> test;
        short currShort(0);
        while (currShort != -1){
            for (int i = 0; i < 4; ++i) {
                currShort = getShort(frame,2);
                if (currShort == -1)
                    break;
                temp[i] = currShort;
                test.push_back(currShort);
                frame = "";
            }
            if (currShort != -1) {
//                frameVector.push_back(temp);
                frameVector.push_back(test);
                test.clear();
            }
        }
        for (unsigned int i = 0; i < frameVector.size(); ++i) {
            frame += "ACK " + std::to_string(myMsgOpCode) + " " + std::to_string(frameVector[i][0]) + " " + std::to_string(frameVector[i][1]) +
                     " " + std::to_string(frameVector[i][2]) + " " + std::to_string(frameVector[i][3]) + "\n";
        }
        return true;
    } else if (myMsgOpCode == 3){
        this->logOut = true;
    }
    frame = "ACK " + std::to_string(myMsgOpCode);
    return true;
}

bool ConnectionHandler::createNotification(std::string& frame) {
    short msgCode = getShort(frame, 1);
    if (msgCode == -1) {
        return false;
    }
    string p = "";
    if (msgCode == 0){
        p = "PM";
    } else p = "Public";

    frame = "";
    std::vector<char> frameVector;
    getString(frameVector);
    string name(frameVector.data(), frameVector.size());
    frameVector.clear();
    getString(frameVector);
    string content(frameVector.data(), frameVector.size());
    frame = "NOTIFICATION "  + p + " " + name + " " + content;
    return true;
}

bool ConnectionHandler::isLogOut() const {
    return logOut;
}