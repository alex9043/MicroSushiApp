package ru.alex9043.accountservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.alex9043.accountservice.dto.AccountRequestDto;
import ru.alex9043.accountservice.service.AccountService;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("test")
    public String test() {
        return "Hello from AccountService!";
    }

    @PostMapping("register")
    public String register(@RequestBody AccountRequestDto accountRequestDto) {
        return accountService.register(accountRequestDto);
    }
}
