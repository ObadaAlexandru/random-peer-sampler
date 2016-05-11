@unit-test
Feature: Network Size Estimation query message

  Scenario: Query message byte serialization
    Given a NSE Query Message
    When the message is serialized
    Then the following byte sequence is returned: "00 04 02 08"