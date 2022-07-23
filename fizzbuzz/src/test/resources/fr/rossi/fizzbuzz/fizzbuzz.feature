Feature: FizzBuzz
  Say Fizz or Buzz

  Scenario: No Fizz No Buzz
    Given value is 1
    When I call fizzbuzz
    Then I should be told "1"
  
  Scenario: Fizz for 3
    Given value is 3
    When I call fizzbuzz
    Then I should be told "Fizz"
  
  Scenario: Fizz for 6
    Given value is 6
    When I call fizzbuzz
    Then I should be told "Fizz"
  
  Scenario: Buzz for 5
    Given value is 5
    When I call fizzbuzz
    Then I should be told "Buzz"
  
  Scenario: FizzBuzz for 15
    Given value is 15
    When I call fizzbuzz
    Then I should be told "FizzBuzz"
  
  Scenario: FizzBuzz for 0
    Given value is 15
    When I call fizzbuzz
    Then I should be told "FizzBuzz"
  
  Scenario: Fizz for negative
    Given value is -3
    When I call fizzbuzz
    Then I should be told "Fizz"