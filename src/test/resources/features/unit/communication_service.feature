@unit-test
Feature: Communication service

  Scenario Outline: Receive and forward messages
    Given that the communication service has a receiver of "<messageType>"
    When the message is received
    Then it is forwarded to the receiver
    Examples:
      | messageType         |
      | NSE_ESTIMATE        |
      | RPS_QUERY           |
      | GOSSIP_NOTIFICATION |

  Scenario Outline: Send message to other modules
    Given a message of type "<messageType>" has to be sent
    When the message is sent
    Then it is forwarded to the client "<clientType>"
    Examples:
      | messageType     | clientType |
      | GOSSIP_ANNOUNCE | GOSSIP     |
      | GOSSIP_ANNOUNCE | GOSSIP     |
      | NSE_QUERY       | NSE        |