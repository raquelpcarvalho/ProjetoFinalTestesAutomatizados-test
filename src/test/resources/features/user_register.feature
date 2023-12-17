Feature: User register

  Scenario: User is not registered
    Given user is unknown
    When user is registered with success
    Then user is known

  Scenario: User without document
    Given user without document
    When user failed to register
    Then notify cpf must be not null
    And user is still unknown

  Scenario: User with invalid email
    Given user is unknown
    And user has an invalid email
    When user failed to register
    Then notify email must be valid
    And user is still unknown

  Scenario: User with a short password
    Given user is unknown
    And user has a short password
    When user failed to register
    Then notify password must be at least 8 characters
    And user is still unknown