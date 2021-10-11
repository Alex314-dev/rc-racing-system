from race import *

import socket

HOST = "172.25.144.135"
PORT = 8889


def connect_to_backend():
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.bind((HOST, PORT))
        print("Socket binded")
        s.listen()
        print("Socket listening\n")
        conn, addr = s.accept()
        return conn, addr


def receive_and_send(conn, addr):
    valid = True

    print("Connection from ", addr)
    while True:
        data = conn.recv(1024)
        if not data:
            break
        msg_received = data.decode("utf-8")
        print("Received: ", msg_received)

        if msg_received[0:2] == "GO":
            if msg_received[2].isdigit():
                flag = int(msg_received[2])# 1 if this is a challenge, 0 otherwise
                new_race = Race(flag)
                response = new_race.start()

                if "DONE" in response:  # check if the race was done
                    valid = True

        if not valid:
            response = "Message is invalid!"

        conn.sendall(bytes("Echo: ", "utf-8") + response)


def main():
    conn, addr = connect_to_backend()

    with conn:
        receive_and_send(conn, addr)



if __name__ == "__main__":
    main()