@component-test
Feature: Persist view

  Scenario: Reset peer age
    Given the following incoming view:
      | identifier                                                       | address     | port | age |
      | 9d3038c01347716800688830eaf52204deb78affe74a5f0c6e0a48fd414d44be | 127.0.0.1   | 8080 | 0   |
      | 4bcec83ef856c50c6866f3b0f3942e011104b5ecc6d955d1e7061faff86070d4 | 192.161.0.1 | 9090 | 0   |
      | a1dd17db87e4889c74e8f073d3aafc56d7a5be13076c262fb6c1eb04a153ef7e | ::1         | 9999 | 0   |
    And the current view is as follows:
      | identifier                                                       | address     | port | age |
      | 9d3038c01347716800688830eaf52204deb78affe74a5f0c6e0a48fd414d44be | 127.0.0.1   | 8080 | 0   |
      | 4bcec83ef856c50c6866f3b0f3942e011104b5ecc6d955d1e7061faff86070d4 | 192.161.0.1 | 9090 | 0   |
      | a1dd17db87e4889c74e8f073d3aafc56d7a5be13076c262fb6c1eb04a153ef7e | ::1         | 9999 | 15  |
    When the incoming view is merged
    Then the resulting view is as follows:
      | identifier                                                       | address     | port | age |
      | 9d3038c01347716800688830eaf52204deb78affe74a5f0c6e0a48fd414d44be | 127.0.0.1   | 8080 | 0   |
      | 4bcec83ef856c50c6866f3b0f3942e011104b5ecc6d955d1e7061faff86070d4 | 192.161.0.1 | 9090 | 0   |
      | a1dd17db87e4889c74e8f073d3aafc56d7a5be13076c262fb6c1eb04a153ef7e | ::1         | 9999 | 0   |

  Scenario: Eviction during merge
    Given the following incoming view:
      | identifier                                                       | address              | port | age |
      | 9d3038c01347716800688830eaf52204deb78affe74a5f0c6e0a48fd414d44be | 127.0.0.1            | 8080 | 0   |
      | 1d6bb1fbc873cffcfd0e59c284788bb1320f5ca9f785adee5b81e6915bb8416f | 2001:cdba::3257:9652 | 9090 | 0   |
    And the current view is as follows:
      | identifier                                                       | address     | port | age |
      | 4bcec83ef856c50c6866f3b0f3942e011104b5ecc6d955d1e7061faff86070d4 | 192.161.0.1 | 9090 | 0   |
      | a1dd17db87e4889c74e8f073d3aafc56d7a5be13076c262fb6c1eb04a153ef7e | ::1         | 9999 | 15  |
    And the maximum view size is "2"
    When the incoming view is merged
    Then the resulting view is as follows:
      | identifier                                                       | address     | port | age |
      | 9d3038c01347716800688830eaf52204deb78affe74a5f0c6e0a48fd414d44be | 127.0.0.1   | 8080 | 0   |
      | 4bcec83ef856c50c6866f3b0f3942e011104b5ecc6d955d1e7061faff86070d4 | 192.161.0.1 | 9090 | 0   |