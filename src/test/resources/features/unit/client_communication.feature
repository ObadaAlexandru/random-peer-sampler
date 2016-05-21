@unit-test
 Feature: Nse and gossip client message communication

   Scenario: Send NSE query and receive answer
     Given a nse client
     When a nse query is send
     Then a nse estimation is received

  Scenario: Send Gossip notify message and wait for some notification
     Given a gossip client
     When a gossip notify message is send
     Then some gossip notification is received