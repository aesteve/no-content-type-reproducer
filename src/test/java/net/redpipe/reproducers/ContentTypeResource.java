package net.redpipe.reproducers;

import rx.Single;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.Collections;
import java.util.List;

@Path("/")
public class ContentTypeResource {

    @GET
    @Path("nocontenttype")
    public Single<List<String>> nothing() {
        return Single.just(Collections.emptyList());
    }

    @GET
    @Path("json")
    @Produces("application/json")
    public Single<List<String>> strings() {
        return nothing();
    }

    @GET
    @Path("syncnocontenttype")
    public List<String> nothingSync() {
        return Collections.emptyList();
    }


}
