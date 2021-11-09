import socket
from RPi import GPIO as GPIO
import time
import multiprocessing

GPIO.setmode(GPIO.BCM)
GPIO.setup(23, GPIO.IN)
GPIO.setup(24, GPIO.IN)
GPIO.setup(25, GPIO.IN)

bridge = multiprocessing.Event()

def connect_to_backend(host, port):
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.bind((host, port))
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

            result = race(conn)

            if result["status"]:
                valid_race = True
                response = str(result["sector1"])[0:5] + "~" + str(result["sector2"])[0:5] + "~" + str(result["sector3"])[0:5] + "~" + str(result["overall"])[0:5] + "\r\n"

        if not valid_protocol:
            response = "Invalid message\r\n"
        elif not valid_race:
            response = "Invalid race\r\n"

        print("[=>] Sending: ", response)
        conn.sendall(response.encode("utf-8"))
        valid_protocol = False
        valid_race = False

def timer(conn):
    conn.sendall("Started\r\n".encode("utf-8"))

def race(conn):
    time1 = read_sensor(23) # 23
    if time1 == -1: return { "status": False } # add || timeX < 5(or another value) to the if statement for the real racing scenerios
    
    timer(conn);
    
    time2 = read_sensor(24) # 24
    if time2 == -1: return { "status": False }
    
    time3 = read_sensor(25) # 25
    if time3 == -1: return { "status": False }
    
    time4 = read_sensor(23) # 23
    if time4 == -1: return { "status": False }
    
    sector1 = time2 - time1
    sector2 = time3 - time2
    sector3 = time4 - time3
    overall = time4 - time1

    return { "status": True, "sector1": sector1, "sector2": sector2, "sector3": sector3, "overall": overall }

def timeout_sensor():
    time.sleep(180)
    bridge.set()

def read_sensor(pin): # the value of pin shall be either 23, 24, or 25 as they are the ones which were configured
    timeout_thread = multiprocessing.Process(target=timeout_sensor, args=())
    timeout_thread.start()

    while True:
        signal = GPIO.input(pin) # read pin
        if signal == 0:
            timeout_thread.terminate()

            ftime = time.time()
            # time.sleep(0.1) # -- for testing purposes -- delete after connecting 3 IR sensors
            return ftime
        elif bridge.is_set(): # check the timeout event
            print("Timeout")
            bridge.clear()
            break
    
    return -1

def main():
    host, port = input("Please enter a host, port pair (e.g. input: 130.68.0.3 8890): ").split()
    conn, addr = connect_to_backend(host, int(port))

    with conn:
        receive_and_send(conn, addr)

if __name__ == "__main__":
    main()
