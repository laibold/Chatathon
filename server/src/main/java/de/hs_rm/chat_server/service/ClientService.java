package de.hs_rm.chat_server.service;

import de.hs_rm.chat_server.model.client.Client;
import de.hs_rm.chat_server.model.user.User;

import java.util.HashMap;

public class ClientService {

    private static ClientService instance;
    private final HashMap<User, Client> clients = new HashMap<>();

    private ClientService() {}

    public static synchronized ClientService getInstance() {
        if (ClientService.instance == null) {
            ClientService.instance = new ClientService();
        }

        return ClientService.instance;
    }

    public HashMap<User, Client> getClients() {
        return this.clients;
    }

    public void addClient(User user, Client client) {
        clients.put(user, client);
    }

    public void deleteClient(User user) {
        clients.remove(user);
    }

    public Client getClient(User user) {
        return clients.get(user);
    }

}
