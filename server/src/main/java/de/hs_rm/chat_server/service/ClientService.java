package de.hs_rm.chat_server.service;

import de.hs_rm.chat_server.model.client.Client;
import de.hs_rm.chat_server.model.user.User;

import java.util.HashMap;
import java.util.Map;

public class ClientService {

    private static ClientService instance;
    private final HashMap<String, Client> clients = new HashMap<>();

    private ClientService() {
    }

    public static synchronized ClientService getInstance() {
        if (ClientService.instance == null) {
            ClientService.instance = new ClientService();
        }

        return ClientService.instance;
    }

    public Map<String, Client> getClients() {
        return this.clients;
    }

    public void addClient(String username, Client client) {
        clients.put(username, client);
    }

    public void deleteClient(String username) {
        clients.remove(username);
    }

    public Client getClient(String username) {
        return clients.get(username);
    }

}
