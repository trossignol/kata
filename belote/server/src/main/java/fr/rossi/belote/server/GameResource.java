package fr.rossi.belote.server;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/game")
public class GameResource {

    @GET
    public String hello() {
        return "hello";
    }
}
