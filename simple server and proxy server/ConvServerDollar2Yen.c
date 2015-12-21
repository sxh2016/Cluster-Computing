
/**************************************************************************
 *
 *  CS 6421 - Simple Conversation
 *      It sends out a welcome message when receive connection from client.
 *  Compilation: gcc -o conv_server conv_server.c to compile
 *  Execution:
 *      server: ./conv_server portnum
 * 
 **************************************************************************/
#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#include <unistd.h>
#include <inttypes.h>
#include <fcntl.h>
#include <netdb.h>
#include <netinet/in.h>
#include <errno.h>
#include <getopt.h>
#include <string.h>
#include <sys/socket.h>
#include <string.h>
#include <stddef.h>


static char* port;
static char* server_ip;


/*
 * Print a usage message
 */
static void
usage(const char* progname) {
    printf("Usage:  ./conv_server portnum\n");
}

/*
 *If connection is established then send out welcome message
 */

//--TODO: add your converting functions here

void
process(int sock)
{
    long n;
    int BUFSIZE = 1024;
    char buf[BUFSIZE];
    //char* msg = "Welcome, you are connected to a C-based server\n";
    char* userInput;
    char* token;
    const char s[2] = " ";
    //char* str;
    char* welcome_msg = "Welcome to the Dollar($) to Yen(y) System!\n";
    char* errmsg = "Input error, we can only format conversion between y and $!\n";
    char *arr[3];
    //char* msg2 = "Welcome to the Yen(Y) to Dollar($) System!\n";
    int mod1;
    char output[20];
    double result;
    
    
    /* Write a welcome message to the client */
    n = write(sock, welcome_msg, strlen(welcome_msg));
    
    if (n <= 0){
        perror("ERROR writing to socket");
        //exit(1);
        close(sock);
        return;
    }
    
    /* read and print the client's request */
    bzero(buf, BUFSIZE);
    n = read(sock, buf, BUFSIZE);
    if (n <= 0){
        perror("ERROR reading from socket");
        close(sock);
        return;
    }
    userInput = buf;
    
    printf("Received message: %s\n", userInput);
    
    // get the first unit,second unit,and value from input
    token = strtok(userInput,s);
    arr[0] = token;
    
    token = strtok(NULL,s);
    arr[1] = token;
    
    token = strtok(NULL,s);
    arr[2] = token;
    
    
    //chose which direction to converssion
    if(strcmp(arr[0],"$") ==0 && strcmp(arr[1],"y") == 0) {
        mod1 = 0;
        if (n <= 0){
            perror("ERROR writing to socket");
            close(sock);
            return;
        }
    }
    
    
    else if(strcmp(arr[0],"y") ==0 && strcmp(arr[1],"$") == 0) {
        mod1 = 1;
        if (n < 0){
            perror("ERROR writing to socket");
            //exit(1);
            close(sock);
            return;
        }
    }
    
    else{
        n = write(sock, errmsg, strlen(errmsg));
        if (n <= 0){
            perror("ERROR writing to socket");
            close(sock);
            return;
        }
        close(sock);
        return;
    }
    
    //@ calculate the result after conversion
    result = atof(arr[2]);
    if(mod1 == 0) result = result * 119.33;
    else if(mod1 == 1) result = result * 0.0084;
    printf("%lf\n",result);

    sprintf(output,"%6.4f\n",result);
    n = write(sock,output,strlen(output));
    
    if (n <= 0){
        perror("ERROR writing to socket");
        close(sock);
        return;
    }
    close(sock);
}

/*
 * Server
 */
int
server( void )
{
    int optval = 1;
    int sockfd, newsockfd;
    socklen_t clilen;
    struct sockaddr_in serv_addr, cli_addr;
    
    /* First call to socket() function */
    sockfd = socket(AF_INET, SOCK_STREAM, 0);
    setsockopt(sockfd, SOL_SOCKET, SO_REUSEADDR, &optval, sizeof optval);
    
    if (sockfd < 0){
        perror("ERROR opening socket");
        exit(1);
    }
    /* Initialize socket structure */
    bzero((char *) &serv_addr, sizeof(serv_addr));
    serv_addr.sin_family = AF_INET;
    serv_addr.sin_addr.s_addr = INADDR_ANY;
    serv_addr.sin_port = htons(atoi(port));
    /* Now bind the host address using bind() call.*/
    if (bind(sockfd, (struct sockaddr *) &serv_addr, sizeof(serv_addr)) < 0){
        perror("ERROR on binding");
        exit(1);
    }
    /* Listening for the client */
    listen(sockfd,5);
    clilen = sizeof(cli_addr);
    
    /* Loop forever receiving client connections */
    while(1) {
        /* Accept connection from the client */
        newsockfd = accept(sockfd, (struct sockaddr *)&cli_addr, &clilen);
        if (newsockfd < 0){
            perror("ERROR on accept");
            exit(1);
        }
        printf("Accepted connection from client\n");
        /* Process a client request on new socket */
        process(newsockfd);
    }
    
    /*clean up*/
    //close(sockfd);
    
    return 0;
}

int main(int argc, char ** argv){
    const char* progname = argv[0];
    if (argc != 2){
        usage(progname);
        exit(1);
    }
    
    port = argv[1];
    if (server() != 0){
        printf("server in trouble\n");
        exit(1);
    }
}


