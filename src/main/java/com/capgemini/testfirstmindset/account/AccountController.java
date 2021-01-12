package com.capgemini.testfirstmindset.account;

import com.capgemini.testfirstmindset.common.ApiCreatedResourceBody;
import com.capgemini.testfirstmindset.common.ApiErrors;
import com.capgemini.testfirstmindset.withdraw.WithdrawDTO;
import com.capgemini.testfirstmindset.withdraw.WithdrawDTOValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@Api(tags = "accounts")
@RestController
@RequestMapping(path = "/accounts")
@CrossOrigin(origins = "*")
@Component
public class AccountController {

    private AccountService accountService;
    private WithdrawDTOValidator withdrawDTOValidator;

    public AccountController(AccountService accountService, WithdrawDTOValidator withdrawDTOValidator) {
        this.accountService = accountService;
        this.withdrawDTOValidator = withdrawDTOValidator;
    }

    @GetMapping(value = "/{id}")
    @ApiOperation(value = "Get account by id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "The account's id", dataType = "string", paramType = "path", required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Account successfully returned", response = Account.class),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<Account> getAccountById(@PathVariable String id) {
        Optional<Account> result = accountService.getAccountById(id);

        if (result.isPresent()) {
            return new ResponseEntity(result, OK);
        }
        return new ResponseEntity(NOT_FOUND);
    }

    @PostMapping
    @ApiOperation(value = "Create an account")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Account created", response = String.class),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 409, message = "Conflict")})
    @ResponseStatus(CREATED)
    public ResponseEntity createAccount(@RequestBody AccountDTO accountDTO) {

        ApiErrors errors = accountService.checkPreConditionsForAccountCreation(accountDTO);
        if (errors.hasErrors()) {
            return ResponseEntity
                    .status(BAD_REQUEST)
                    .body(errors);
        }

        ApiErrors conflictErrors = accountService.checkUniqueness(accountDTO);

        if (conflictErrors.hasErrors()) {
            return ResponseEntity
                    .status(CONFLICT)
                    .body(conflictErrors);
        }

        String accountId = accountService.createAccount(accountDTO);

        return ResponseEntity
                .status(CREATED)
                .body(new ApiCreatedResourceBody(accountId));
    }

    @PutMapping(value = "/{id}/withdraw")
    @ApiOperation(value = "Withdraw from account")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "The account's id", dataType = "string", paramType = "path", required = true),
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Withdraw done"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    public ResponseEntity withdraw(@PathVariable String id, @RequestBody WithdrawDTO withdraw) {

        Optional<Account> result = accountService.getAccountById(id);
        if (result.isEmpty()) {
            return new ResponseEntity(NOT_FOUND);
        }

        ApiErrors errors = withdrawDTOValidator.validate(withdraw);
        if (errors.hasErrors()) {
            return ResponseEntity
                    .status(BAD_REQUEST)
                    .body(errors);
        }

        errors = accountService.withdrawFromAccount(result.get(), withdraw.getAmount());
        if (errors.hasErrors()) {
            return ResponseEntity
                    .status(BAD_REQUEST)
                    .body(errors);
        }

        return new ResponseEntity(OK);
    }
}
