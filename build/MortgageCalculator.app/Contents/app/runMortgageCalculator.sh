#!/bin/bash
# Open a new Terminal window and run the Java command
osascript -e 'tell application "Terminal" to do script "java -jar \"$(pwd)/MortgageCalculatorTest-0.0.1-SNAPSHOT.jar\""'

