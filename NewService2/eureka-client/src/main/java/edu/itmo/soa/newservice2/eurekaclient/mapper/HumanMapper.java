package edu.itmo.soa.newservice2.eurekaclient.mapper;

public final class HumanMapper {
    private HumanMapper() {
    }

    public static edu.itmo.soa.newservice2.eurekaclient.model.Human toModel(edu.itmo.soa.newservice2.eurekaclient.client.Human client) {
        if (client == null) {
            return null;
        }
        edu.itmo.soa.newservice2.eurekaclient.model.Human model =
                new edu.itmo.soa.newservice2.eurekaclient.model.Human();
        model.setAge(client.getAge());
        return model;
    }

    public static edu.itmo.soa.newservice2.eurekaclient.client.Human toClient(edu.itmo.soa.newservice2.eurekaclient.model.Human model) {
        if (model == null) {
            return null;
        }
        edu.itmo.soa.newservice2.eurekaclient.client.Human client =
                new edu.itmo.soa.newservice2.eurekaclient.client.Human();
        client.setAge(model.getAge());
        return client;
    }
}
