Feature: Transfering money

  Scenario: Transfer amount from one account to another
    Given Account with id "sourceId" has balance of 1000
    Given Account with id "destinationId" has balance of 0
    When Account with id "sourceId" transfers 1000 to account with id "destinationId"
    Then Account with id "sourceId" balance is 0
    Then Account with id "destinationId" balance is 1000