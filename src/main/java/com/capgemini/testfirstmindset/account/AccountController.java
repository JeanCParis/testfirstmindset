package com.capgemini.testfirstmindset.account;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@Api(tags = "accounts")
@RestController
@RequestMapping(path = "/accounts")
@CrossOrigin(origins = "*")
@Component
public class AccountController {

    private AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping(value = "/{id}")
    @ApiOperation(value = "Get account by id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "The account's id", dataType = "string", paramType = "path", required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Account successfully returned", response = Account.class),
            @ApiResponse(code = 404, message = "Not Found"git ),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<Account> getAccountById(@PathVariable String id) {
        HttpHeaders headers = new HttpHeaders();
        Optional<Account> result = accountService.getAccountById(id);

        if (result.isPresent()) {
            return new ResponseEntity(result, headers, OK);
        }
        return new ResponseEntity(headers, NOT_FOUND);
    }
}
