<br>
<div align="center">
  <a href="https://gitlab.utwente.nl/cs21-22">
    <img src="RC/src/main/resources/static/images/logo.png" alt="Logo" width="238px" height="137px">
  </a>

  <h3 align="center">RC Racing System</h3>

  <p align="center">
    An entertaining project for RC Racing.
  </p>
</div>



## About The Project

With this project we are aiming at making RC racing more engaging and make it more of a social activity. 

To achieve this purpose we have created a web application from which it is possible to add friends and challenge them. There is also of course a touch of competitiveness, we keep scores of your challenges and there is a leaderboard visible to all users.

To race, you need to be pyhsically present at the racing track designed by us with our RC car. After interacting with the web application you can race on the track and we will keep track of your time.



## Built With

Backend:
* Java Spring Framework
* PostgreSQL

Frontend:
* HTML/CSS
* JavaScript
* JQuery

Track:
* RC Car
* Raspberry Pi 4
* IR Sensors
* Python



## Getting Started

To setup the system, read the prerequisites and set up your environment correctly, then go through the installation section step-by-step.


### Prerequisites

To setup the system correctly you need our track, 3 IR sensors, a RPi 4, 9 female-to-female cables, 6 female-to-male cables, and a computer to host the web application.

The computer that will host the web application and connect to the RPi remotely also has to include the password to the database in its system environment variables, with the key being `RC_DB_PASS` and the value being the password to the database.

For running the Java Spring application you need Java 11 and we advise to use IntelliJ.


### Installation

To setup the system follow the instructions below.
1. Download the latest version of the system from the git repository.
2. Import the Java project via IntelliJ.
3. Setup the maven dependencies.
4. Put the IR sensors in the correct spots on the track.
5. Connect the IR sensors to power and ground.
6. Connect the data out pins of the IR sensors to GPIO pins 23, 24, and 25.
7. Remotely connect to the RPi via SSH.
8. Copy the RPi folder found in the repository to your home directory inside the RPi.
9. Go to the RPi directory and run the bash script `setup_gpio.sh` with sudo.
10. Copy the inet4 address found in the output of the bash script.
11. The script will prompt a question asking to setup a socket, give the copied ip address as the answer and also give a free port.
12. After the socket is binded go back to IntelliJ.
13. Run the java class `RcApplication.java`.
14. The java application will ask a socket to connect to. Give the same ip address and the port you wrote for the bash script.
15. Go to localhost:8443, and discard the possible warning message coming from your browser.
16. Create or login to your account. 



## Testing

Below you can find detailed instructions on how to run tests.


### Postman API testing:

1. Import the collection https://www.getpostman.com/collections/a91c1413ba01c724c95f
2. Log-in to the RC-Racing System
3. Find what cookie you are using at the moment, and copy it
4. Click on "Cookies" in the postman app(under the button "SEND")
5. Add the domain name "localhost"
6. Add a cookie with the following JSESSIONID=[your cookie]; Path=/; Domain=localhost; Secure; HttpOnly;
7. Click on "SAVE"
8. Test the different API requests by choosing a request and clicking on SEND
9. Test it by logging in to other users as well (cookie isn't changed if the same browser is used!)
10. You can add a request according to the appropriate package (Player/Race/etc.)
11. Type the request you wish to test, and click on "SEND". Then, check if the results are as you expect!


### SELENIUM-IDE System Testing:

1. Download the SELENIUM-IDE for Chrome (https://chrome.google.com/webstore/detail/selenium-ide/mooikfkahbdckldjjndioackbalphokd?hl=en)
2. Log-in to the RC-Racing System
3. Open the SELENIUM-IDE
4. Open a project...
5. Choose the SIDE file called "RC-RacingSystem.side", attached in the project
6. Run the differerent tests. Pay attention to the fact that the tests are based on a testing user (liranneta23), and therefore some tests would probably fail if you run them from different users (for instance the "logging-in test")
7. In case you want to add tests by yourself:
	a. Add a test (click on the "+" button near "Tests")
	b. Click on "REC" (in the top right, below the 3 dots)
	c. Navigate to the screen/s you wish to test
	d. In case you want to test the value of a specific object, right-click on it, and then:
		Selenium-IDE --> Assert --> Text
	e. The actions which are checked during the recording, including the "Asserts", will be added to the   SELENIUM-IDE
	f. Choose a test you want to examine, and "Run current test"
	h. You can examine all the tests at once, by choosing "Run all tests"



## Usage
