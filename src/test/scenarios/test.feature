Feature: Withdrawing money

Scenario: Withdraw amount smaller then balance
  Given Account has balance of 1000
   When 500 is withdrawn
   Then Remaining account balance is 500