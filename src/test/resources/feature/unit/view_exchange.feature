@unit-test
Feature: Partial view exchange

  Scenario: Periodically announce the view
    Given that the rps port is "8080"
    And that the rps host is "localhost"
    And that the round duration is "5000" milliseconds
    When the view exchange scheduler starts
    Then it periodically announces the view