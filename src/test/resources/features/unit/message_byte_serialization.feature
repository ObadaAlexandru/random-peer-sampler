@unit-test
Feature: Serialize messages to byte lists

  Scenario: Query message byte serialization
    Given an NSE Query Message
    When the message is serialized
    Then the following byte sequence is returned: "00 04 02 08"

  Scenario Outline: Estimation message byte serialization
    Given an NSE estimation Message with estimated size "<size>" and standard deviation "<standardDeviation>"
    When the message is serialized
    Then the following byte sequence is returned: "<byteSequence>"
    Examples:
      | size       | standardDeviation | byteSequence                        |
      | 1000       | 15                | 00 0C 02 09 00 00 03 E8 00 00 00 0F |
      | 9999       | 5                 | 00 0C 02 09 00 00 27 0F 00 00 00 05 |
      | 0          | 0                 | 00 0C 02 09 00 00 00 00 00 00 00 00 |
      | 4294967295 | 65535             | 00 0C 02 09 FF FF FF FF 00 00 FF FF |

  Scenario: RPS Query message byte serialization
    Given an RPS Query Message
    When the message is serialized
    Then the following byte sequence is returned: "00 04 02 1C"

  Scenario Outline: RPS Peer message byte serialization
    Given an RPS message with "<ip>" and "<digest>" and "<port>"
    When the message is serialized
    Then the following byte sequence is returned: "<byteSequence>"
    Examples:
      | ip                   | digest                                                           | port | byteSequence                                                                                                     |
      | 192.168.1.1          | 9d3038c01347716800688830eaf52204deb78affe74a5f0c6e0a48fd414d44be | 9090 | 002C021D9D3038C01347716800688830EAF52204DEB78AFFE74A5F0C6E0A48FD414D44BE23820000C0A80101                         |
      | 127.0.0.1            | 4bcec83ef856c50c6866f3b0f3942e011104b5ecc6d955d1e7061faff86070d4 | 4444 | 002C021D4BCEC83EF856C50C6866F3B0F3942E011104B5ECC6D955D1E7061FAFF86070D4115C00007F000001                         |
      | 2001:cdba::3257:9652 | 1d6bb1fbc873cffcfd0e59c284788bb1320f5ca9f785adee5b81e6915bb8416f | 8080 | 0038021D1D6BB1FBC873CFFCFD0E59C284788BB1320F5CA9F785ADEE5B81E6915BB8416F1F9000002001CDBA000000000000000032579652 |
      | ::1                  | a1dd17db87e4889c74e8f073d3aafc56d7a5be13076c262fb6c1eb04a153ef7e | 7777 | 0038021DA1DD17DB87E4889C74E8F073D3AAFC56D7A5BE13076C262FB6C1EB04A153EF7E1E61000000000000000000000000000000000001 |
