package ru.alex9043.accountservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.alex9043.accountservice.controller.AccountRequestDto;
import ru.alex9043.accountservice.model.Account;
import ru.alex9043.accountservice.model.Role;
import ru.alex9043.accountservice.repository.AccountRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;

    public String register(AccountRequestDto accountRequestDto) {
        Account account = modelMapper.map(accountRequestDto, Account.class);
        account.getRoles().add(Role.ROLE_USER);
        accountRepository.save(account);
        return "Account register successfully";
    }
}
