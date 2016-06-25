@component-test
Feature: RpsHandler reacts on incoming RPS query messages

  Scenario: Incoming query is answered
    Given a running RPS service
    When the RPS service is queried
    Then the handler responds with a random peer message