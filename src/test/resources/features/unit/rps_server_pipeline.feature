@unit-test
 Feature: Rps server inbound/outbound pipeline

   Scenario: Message forwarded to receiver
     Given that the following byte sequence "00 04 02 1C"
     When the data is received
     Then the receiver receives a message of type "RPS_QUERY"

    Scenario: Message sent to peer
      Given an RPS peer message with address "192.168.1.1", port "9090" and id "9d3038c01347716800688830eaf52204deb78affe74a5f0c6e0a48fd414d44be"
      When the message is sent out
      Then the peer receives the following byte sequence "002C021D9D3038C01347716800688830EAF52204DEB78AFFE74A5F0C6E0A48FD414D44BE23820004C0A80101"