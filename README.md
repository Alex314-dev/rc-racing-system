# M5 Project
The best Project ever made


# Testing
Postman API testing:
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

SELENIUM-IDE System Testing:
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