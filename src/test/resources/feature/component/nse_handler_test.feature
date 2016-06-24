@component-test
Feature: Nse handler communicates with NSE module

  Scenario: NSE handler periodically sends queries to the server
  Normal behaviour
    Given that the NSE server is running on the configured port
    And that it responds as follows:
      | networkEstimatedSize | standardDeviation |
      | 30                   | 5                 |
    When nse handler is queried after 3 rounds
    Then the handler responds as follows:
      | networkEstimatedSize | standardDeviation |
      | 30                   | 5                 |
    And the nse module has received at least 3 nse queries

  Scenario: NSE module unexpectedly goes down without returning
  The handler is expected to keep in memory the latest learned results
    Given that the NSE server is running on the configured port
    And that it responds as follows:
      | networkEstimatedSize | standardDeviation |
      | 30                   | 5                 |
    When the nse module goes down
    Then the handler responds as follows:
      | networkEstimatedSize | standardDeviation |
      | 30                   | 5                 |

  Scenario: NSE module unexpectedly goes down, but later returns
    It is expected that the handler will start receiving new nse estimations
    Given that the NSE server is running on the configured port
    And that it responds as follows:
      | networkEstimatedSize | standardDeviation |
      | 30                   | 5                 |
    And that after the down time it responds as follows:
      | networkEstimatedSize | standardDeviation |
      | 150                  | 15                |
    When the nse module goes down for 3 rounds
    Then the handler responds as follows:
      | networkEstimatedSize | standardDeviation |
      | 150                  | 15                |