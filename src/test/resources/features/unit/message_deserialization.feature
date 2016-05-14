@unit-test
Feature: Message deserialization
  Scenario Outline: Identify message types
    Given the following byte sequence: "<byteSequence>"
    When the message is deserialized
    Then a message of type "<messageType>" is returned
    Examples:
      | messageType  | byteSequence                        |
      | NSE_ESTIMATE | 00 0C 02 09 00 00 03 E8 00 00 00 0F |
      | NSE_ESTIMATE | 00 0C 02 09 00 00 27 0F 00 00 00 05 |
      | RPS_QUERY    | 00 04 02 1C                         |
