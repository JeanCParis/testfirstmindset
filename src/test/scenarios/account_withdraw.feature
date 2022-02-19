Feature: Withdrawing money

Scenario: Withdraw amount smaller than balance
  Given Account with id "accountId1" has balance of 1000
  When Account with id "accountId1" withdraws 600
  Then Account with id "accountId1" balance is 400

Scenario: Withdraw amount greater than balance
  Given Account with id "accountId2" has balance of 400
  When Account with id "accountId2" withdraws 500
  Then Account with id "accountId2" balance is 400

Scenario: Withdraw negative amount
  Given Account with id "accountId3" has balance of 400
  When Account with id "accountId3" withdraws -500
  Then Account with id "accountId3" balance is 400