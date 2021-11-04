from selenium import webdriver
from selenium.webdriver.common.keys import Keys

from time import sleep

url = "https://localhost:8443"


driver = webdriver.Chrome('C:\\Users\\user\\Liran\\Code\\Python\\Crawlers\\chromedriver')

url = driver.command_executor._url
session_id = driver.session_id

print(session_id)
print(url)

driver = webdriver.Remote(command_executor=url,desired_capabilities={})
driver.close()   # this prevents the dummy browser
driver.session_id = session_id


driver.get(url)