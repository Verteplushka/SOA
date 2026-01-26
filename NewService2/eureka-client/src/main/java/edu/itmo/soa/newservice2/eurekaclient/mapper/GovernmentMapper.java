package edu.itmo.soa.newservice2.eurekaclient.mapper;

public final class GovernmentMapper {
    private GovernmentMapper() {
    }

    public static edu.itmo.soa.newservice2.eurekaclient.model.Government toModel(edu.itmo.soa.newservice2.eurekaclient.client.Government client) {
        if (client == null) {
            return null;
        }
        return edu.itmo.soa.newservice2.eurekaclient.model.Government
                .valueOf(client.name());
    }

    public static edu.itmo.soa.newservice2.eurekaclient.client.Government toClient(edu.itmo.soa.newservice2.eurekaclient.model.Government model) {
        if (model == null) {
            return null;
        }
        return edu.itmo.soa.newservice2.eurekaclient.client.Government
                .valueOf(model.name());
    }
}
