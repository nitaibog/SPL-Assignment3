#ifndef CONNECTION_HANDLER__
#define CONNECTION_HANDLER__


#include <string>
#include <iostream>
#include <boost/asio.hpp>
#include <mutex>
#include <condition_variable>


using boost::asio::ip::tcp;

class ConnectionHandler {
private:
    const std::string host_;
    const short port_;
    boost::asio::io_service io_service_;   // Provides core I/O functionality
    tcp::socket socket_;
    bool logOut;
public:
    ConnectionHandler(std::string host, short port);
    virtual ~ConnectionHandler();

    // Connect to the remote machine
    bool connect();

    // Read a fixed number of bytes from the server - blocking.
    // Returns false in case the connection is closed before bytesToRead bytes can be read.
    bool getBytes(char bytes[], unsigned int bytesToRead);

    // Send a fixed number of bytes from the client - blocking.
    // Returns false in case the connection is closed before all the data is sent.
    bool sendBytes(const char bytes[], int bytesToWrite);

    // Read an ascii line from the server
    // Returns false in case connection closed before a newline can be read.
    bool getLine(std::string& line);

    // Send an ascii line from the server
    // Returns false in case connection closed before all the data is sent.
    bool sendLine(std::string& line);

    // Get Ascii data from the server until the delimiter character
    // Returns false in case connection closed before null can be read.
    bool getFrameAscii(std::string& frame, char delimiter);

    // Send a message to the remote host.
    // Returns false in case connection is closed before all the data is sent.
    bool sendFrameAscii(const std::string& frame, char delimiter);

    short stringToOpCode(std::string msgType);

    std::string messageContentByType(std::string msgType, std::string content);
    // Close down the connection properly.
    void close();

    void shortToBytes(short num, char *bytesArr);

    short getShort(std::string &frame, int count);

    short bytesToShort(char *bytesArr);

    bool getString(std::vector<char> &frame);

    bool Error(std::string &frame);

    bool Ack(std::string &frame);

    bool createNotification(std::string &frame);

    bool isLogOut() const;

    std::string messageContentByType(short msgOpCode, std::string content);
}; //class ConnectionHandler#endif
#endif