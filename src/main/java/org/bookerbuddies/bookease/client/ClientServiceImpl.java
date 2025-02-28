package org.bookerbuddies.bookease.client;

import org.bookerbuddies.bookease.account.Account;
import org.bookerbuddies.bookease.account.AccountRepository;
import org.bookerbuddies.bookease.client.dto.RegisterAccount;
import org.bookerbuddies.bookease.client.exception.ClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Service
public class ClientServiceImpl implements ClientService {
    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public Client newRegistration(RegisterAccount registerAccount) throws ClientException {
        Account account = new Account(registerAccount.getName(), registerAccount.getBalance(), "client");
        account = this.accountRepository.save(account);
        if ((account.getName() == null && account.getBalance() == null)) {
            throw new ClientException("Account balance & name is null");
        }

        Client client = new Client(registerAccount.getClientId(), registerAccount.getEmail(), registerAccount.getPassword(), registerAccount.getName(), account);
        client = this.clientRepository.save(client);

        if (client.getEmail() == null || client.getPassword() == null || client.getName() == null) {
            throw new ClientException("You have not filled all your client credentials.Check it once.");
        }
        return client;
    }

    @Override
    public Client loginPage(String email, String password) throws ClientException {
        Client client = this.clientRepository.findByEmailAndPassword(email, password);
        if (client == null) {
            throw new ClientException("You have not entered the credentials properly");
        }
        return client;
    }


    @Override
    public Client updateClient(RegisterAccount registerAccount) throws ClientException {
        Optional<Client> getClient = clientRepository.findById(registerAccount.getClientId());
        if (getClient.isEmpty()) {
            throw new ClientException("Client id not found");
        }
        getClient.get().setEmail(registerAccount.getEmail());
        getClient.get().setName(registerAccount.getName());
        getClient.get().setPassword(registerAccount.getPassword());
        clientRepository.save(getClient.get());
        return getClient.get();
    }

    @Override
    public Client getClientbyId(Integer clientId) throws ClientException {
        Optional<Client> client = this.clientRepository.findById(clientId);
        if (client.isEmpty()) {
            throw new ClientException("Your clientId cannot be null or doesn't match");
        }
        return client.get();
    }


    public Client deleteClientById(Integer clientId) throws ClientException {
        Optional<Client> client = this.clientRepository.findById(clientId);
        if (client.isEmpty()) {
            throw new ClientException("Your clientId might be null or doesn't match");
        }

        Client clientToDelete = client.get();
        this.clientRepository.deleteById(clientId);
        return clientToDelete;
    }
}