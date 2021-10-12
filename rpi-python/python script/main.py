import socket
from RPi import GPIO as GPIO
import time

GPIO.setmode(GPIO.BCM)
GPIO.setup(23, GPIO.IN)
GPIO.setup(24, GPIO.IN)
GPIO.setup(25, GPIO.IN)

HOST = "169.254.146.37"
PORT = 8889

def connect_to_backend():
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.bind((HOST, PORT))
        print("Socket binded")
        s.listen()
        print("Socket listening")
        conn, addr = s.accept()
        return conn, addr

def receive_and_send(conn, addr):
    valid_protocol = False
    valid_race = False

    print("Connection from ", addr, "\n")
    while True:
        data = conn.recv(1024)
        if not data:
            break
        msg_received = data.decode("utf-8")
        print("[<=] Received: ", msg_received)

        response = "\r\n"
        if "GO" in msg_received:
            valid_protocol = True

            result = race()

            if result["status"]:
                valid_race = True
                response = str(result["sector1"])[0:5] + "~" + str(result["sector2"])[0:5] + "~" + str(result["sector3"])[0:5] + "~" + str(result["overall"])[0:5] + "\r\n"

        if not valid_protocol:
            response = "Invalid message\r\n"
        elif not valid_race:
            response = "Invalid race\r\n"

        print("[=>] Sending: ", response)
        conn.sendall(response.encode("utf-8"))

# TODO: Add invalid race logic (timeouts etc.)
def race():
    time1 = read_sensor(23) # 23
    time2 = read_sensor(23) # 24
    time3 = read_sensor(23) # 25
    time4 = read_sensor(23) # 23
    
    sector1 = time2 - time1
    sector2 = time3 - time2
    sector3 = time4 - time3
    overall = time4 - time1

    return { "status": True, "sector1": sector1, "sector2": sector2, "sector3": sector3, "overall": overall}

def read_sensor(pin): # the value of pin shall be either 23, 24, or 25 as they are the ones which were configured
    while True:
        signal = GPIO.input(pin)
        if signal == 0:
            ftime = time.time()
            time.sleep(0.7)
            return ftime

def main():
    conn, addr = connect_to_backend()

    with conn:
        receive_and_send(conn, addr)

if __name__ == "__main__":
    main()
