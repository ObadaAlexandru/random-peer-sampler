@unit-test
Feature: Serialize messages to byte lists

  Scenario Outline: Gossip announce message byte serialization
    Given a gossip announce message with "<ttl>" and "<datatype>" and "<payload>"
    When the message is serialized
    Then the following byte sequence is returned: "<byteSequence>"
    Examples:
      | ttl  | datatype | payload                          | byteSequence                                                                           |
      | 1    | 1234     | 571e72a5c0e6d751df489389e7986193 | 002801F4000100000004D23537316537326135633065366437353164663438393338396537393836313933 |
      | 503  | 9        | 92a8d77cae2579314736837969035532 | 002801F401F700000000093932613864373763616532353739333134373336383337393639303335353332 |
      | 9999 | 800      | 0c9823ba4759e89a3987630236d4     | 002401F4270F000000032030633938323362613437353965383961333938373633303233366434         |
      | 7    | 1        | 0fff                             | 000C01F40007000000000130666666                                                         |

  Scenario: Gossip notify message byte serialization
    Given a Gossip Notify Message
    When the message is serialized
    Then the following byte sequence is returned: "00 04 01 F5"

  Scenario Outline: Gossip notification message byte serialization
    Given a gossip notification message with "<datatype>" and "<payload>"
    When the message is serialized
    Then the following byte sequence is returned: "<byteSequence>"
    Examples:
      | datatype | payload                          | byteSequence                                                                         |
      | 1234     | 5a1e72a5c0e6d751df489389e7986193 | 002801F60000000004D23561316537326135633065366437353164663438393338396537393836313933 |
      | 9        | 9218d77cae2579314736837969035532 | 002801F60000000000093932313864373763616532353739333134373336383337393639303335353332 |
      | 800      | 0c9823ba4739e89a3987630236d4     | 002401F600000000032030633938323362613437333965383961333938373633303233366434         |
      | 1        | 0fff                             | 000C01F600000000000130666666                                                         |

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
      | 192.168.1.1          | 9d3038c01347716800688830eaf52204deb78affe74a5f0c6e0a48fd414d44be | 9090 | 002C021D9D3038C01347716800688830EAF52204DEB78AFFE74A5F0C6E0A48FD414D44BE23820004C0A80101                         |
      | 127.0.0.1            | 4bcec83ef856c50c6866f3b0f3942e011104b5ecc6d955d1e7061faff86070d4 | 4444 | 002C021D4BCEC83EF856C50C6866F3B0F3942E011104B5ECC6D955D1E7061FAFF86070D4115C00047F000001                         |
      | 2001:cdba::3257:9652 | 1d6bb1fbc873cffcfd0e59c284788bb1320f5ca9f785adee5b81e6915bb8416f | 8080 | 0038021D1D6BB1FBC873CFFCFD0E59C284788BB1320F5CA9F785ADEE5B81E6915BB8416F1F9000062001CDBA000000000000000032579652 |
      | ::1                  | a1dd17db87e4889c74e8f073d3aafc56d7a5be13076c262fb6c1eb04a153ef7e | 7777 | 0038021DA1DD17DB87E4889C74E8F073D3AAFC56D7A5BE13076C262FB6C1EB04A153EF7E1E61000600000000000000000000000000000001 |

  Scenario Outline: RPS Peer View message byte serialization
    ##### THIS HAS TO BE REFACTORED TO A TABLE
    Given an RPS view message with "<address>" and "<digest>" and "<port>" and "<address2>" and "<digest2>" and "<port2>" and "<address3>" and "<digest3>" and "<port3>"
    When the message is serialized
    Then the following byte sequence is returned: "<byteSequence>"
    Examples:
      | address              | digest                                                           | port | address2             | digest2                                                          | port2 | address3             | digest3                                                          | port3 | byteSequence                                                                                                                                                                                                                                                                                             |
      | 192.168.1.1          | 9d3038c01347716800688830eaf52204deb78affe74a5f0c6e0a48fd414d44be | 9090 | 127.0.0.1            | 4bcec83ef856c50c6866f3b0f3942e011104b5ecc6d955d1e7061faff86070d4 | 4444  | 2001:cdba::3257:9652 | 1d6bb1fbc873cffcfd0e59c284788bb1320f5ca9f785adee5b81e6915bb8416f | 8080  | 0088021E9D3038C01347716800688830EAF52204DEB78AFFE74A5F0C6E0A48FD414D44BE23820004C0A801014BCEC83EF856C50C6866F3B0F3942E011104B5ECC6D955D1E7061FAFF86070D4115C00047F0000011D6BB1FBC873CFFCFD0E59C284788BB1320F5CA9F785ADEE5B81E6915BB8416F1F9000062001CDBA000000000000000032579652                         |
      | 127.0.0.1            | 4bcec83ef856c50c6866f3b0f3942e011104b5ecc6d955d1e7061faff86070d4 | 4444 | 2001:cdba::3257:9652 | 1d6bb1fbc873cffcfd0e59c284788bb1320f5ca9f785adee5b81e6915bb8416f | 8080  | ::1                  | a1dd17db87e4889c74e8f073d3aafc56d7a5be13076c262fb6c1eb04a153ef7e | 7777  | 0094021E4BCEC83EF856C50C6866F3B0F3942E011104B5ECC6D955D1E7061FAFF86070D4115C00047F0000011D6BB1FBC873CFFCFD0E59C284788BB1320F5CA9F785ADEE5B81E6915BB8416F1F9000062001CDBA000000000000000032579652A1DD17DB87E4889C74E8F073D3AAFC56D7A5BE13076C262FB6C1EB04A153EF7E1E61000600000000000000000000000000000001 |
      | 2001:cdba::3257:9652 | 1d6bb1fbc873cffcfd0e59c284788bb1320f5ca9f785adee5b81e6915bb8416f | 8080 | ::1                  | a1dd17db87e4889c74e8f073d3aafc56d7a5be13076c262fb6c1eb04a153ef7e | 7777  | 192.168.1.1          | 9d3038c01347716800688830eaf52204deb78affe74a5f0c6e0a48fd414d44be | 9090  | 0094021E1D6BB1FBC873CFFCFD0E59C284788BB1320F5CA9F785ADEE5B81E6915BB8416F1F9000062001CDBA000000000000000032579652A1DD17DB87E4889C74E8F073D3AAFC56D7A5BE13076C262FB6C1EB04A153EF7E1E610006000000000000000000000000000000019D3038C01347716800688830EAF52204DEB78AFFE74A5F0C6E0A48FD414D44BE23820004C0A80101 |
      | ::1                  | a1dd17db87e4889c74e8f073d3aafc56d7a5be13076c262fb6c1eb04a153ef7e | 7777 | 2001:cdba::3257:9652 | 1d6bb1fbc873cffcfd0e59c284788bb1320f5ca9f785adee5b81e6915bb8416f | 8080  | 192.168.1.1          | 9d3038c01347716800688830eaf52204deb78affe74a5f0c6e0a48fd414d44be | 9090  | 0094021EA1DD17DB87E4889C74E8F073D3AAFC56D7A5BE13076C262FB6C1EB04A153EF7E1E610006000000000000000000000000000000011D6BB1FBC873CFFCFD0E59C284788BB1320F5CA9F785ADEE5B81E6915BB8416F1F9000062001CDBA0000000000000000325796529D3038C01347716800688830EAF52204DEB78AFFE74A5F0C6E0A48FD414D44BE23820004C0A80101 |
      