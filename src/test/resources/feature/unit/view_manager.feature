@unit-test
  Feature: View manager

    Scenario: Pushed views don't exceed the push limit
      Given the following pushed peers:
        | address     | port |
        | 127.0.0.1   | 8080 |
        | 192.161.0.1 | 9090 |
        | 127.0.0.1   | 8080 |
      And the following pulled peers:
        | address     | port |
        | 127.0.0.1   | 8080 |
        | 192.161.0.1 | 9090 |
        | 127.0.0.1   | 8080 |
        | 192.161.0.1 | 9090 |
      And the following sampled peers:
        | address     | port |
        | ::1         | 9999 |
        | 127.0.0.1   | 8080 |
        | 192.161.0.1 | 9090 |
        | 127.0.0.1   | 8080 |
        | 192.161.0.1 | 9090 |
      When the view is updated
      Then the new dynamic view is persisted
      And the samples are updated

    Scenario: Pushed views exceed the push limit
      Given the following pushed peers:
        | address     | port |
        | 127.0.0.1   | 8080 |
        | 192.161.0.1 | 9090 |
        | 127.0.0.1   | 8080 |
        | 127.0.0.1   | 8080 |
        | 192.161.0.1 | 9090 |
        | 127.0.0.1   | 8080 |
        | 127.0.0.1   | 8080 |
        | 192.161.0.1 | 9090 |
        | 127.0.0.1   | 8080 |
      And the following pulled peers:
        | address     | port |
        | 127.0.0.1   | 8080 |
        | 192.161.0.1 | 9090 |
        | 127.0.0.1   | 8080 |
        | 192.161.0.1 | 9090 |
      And the following sampled peers:
        | address     | port |
        | ::1         | 9999 |
        | 127.0.0.1   | 8080 |
        | 192.161.0.1 | 9090 |
        | 127.0.0.1   | 8080 |
        | 192.161.0.1 | 9090 |
      When the view is updated
      Then the dynamic view is not updated

    Scenario: Limited pushes
      Given that the dynamic view contains the following peers:
        | address     | port |
        | 127.0.0.1   | 8080 |
        | 192.161.0.1 | 9090 |
        | 127.0.0.1   | 8080 |
        | 127.0.0.1   | 8080 |
        | 192.161.0.1 | 9090 |
        | 127.0.0.1   | 8080 |
        | 127.0.0.1   | 8080 |
        | 192.161.0.1 | 9090 |
        | 127.0.0.1   | 8080 |
        | 127.0.0.1   | 8080 |
      And the alpha parameter is "0.4"
      When the peers for push are queried
      Then the service returns "4" peers