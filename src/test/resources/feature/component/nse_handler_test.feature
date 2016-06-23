@component-test
Feature: Nse handler communicates with NSE module

  Scenario: NSE handler periodically sends queries to the server
    Given that the NSE server is running on port 8080
    And that it responds as follows:
      | networkEstimatedSize | standardDeviation |
      | 30                   | 5                 |
    When the handler is asked
    Then it responds as follows:
      | networkEstimatedSize | standardDeviation |
      | 30                   | 5                 |