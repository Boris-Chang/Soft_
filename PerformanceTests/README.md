

## Run tests

### 1. Run Backend

### 2. Download JMeter

### 3. Open Terminal in JMeter bin folder

### 4. Run Init.jmx 

`./jmeter -n -t <path_to_tests_root>/Init.jmx`

### 5. Run Tests 

`./jmeter -n -t <path_to_tests_root>/<TestType>/<TestType>.jmx`

Tests will create Log files with results.

You need to run Init.jmx only one time (before tests) per Backend lifecycle. It creates users and souls for tests.




