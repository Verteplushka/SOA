package edu.itmo.soa.newservice2.eurekaclient.mapper;

public final class CoordinatesMapper {
    private CoordinatesMapper() {
    }
    public static edu.itmo.soa.newservice2.eurekaclient.model.Coordinates toModel(edu.itmo.soa.newservice2.eurekaclient.client.Coordinates client) {
        if (client == null) {
            return null;
        }

        edu.itmo.soa.newservice2.eurekaclient.model.Coordinates model =
                new edu.itmo.soa.newservice2.eurekaclient.model.Coordinates();
        model.setX(client.getX());
        model.setY(client.getY());

        return model;
    }

    public static edu.itmo.soa.newservice2.eurekaclient.client.Coordinates toClient(edu.itmo.soa.newservice2.eurekaclient.model.Coordinates model) {
        if (model == null) {
            return null;
        }

        edu.itmo.soa.newservice2.eurekaclient.client.Coordinates client =
                new edu.itmo.soa.newservice2.eurekaclient.client.Coordinates();
        client.setX(model.getX());
        client.setY((int) model.getY());

        return client;
    }
}
