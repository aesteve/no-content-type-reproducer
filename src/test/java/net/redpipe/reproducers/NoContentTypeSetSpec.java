package net.redpipe.reproducers;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.Timeout;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.rxjava.ext.web.client.WebClient;
import net.redpipe.engine.core.AppGlobals;
import net.redpipe.engine.core.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class NoContentTypeSetSpec {

    private Server server;
    private final static int PORT = 9090;
    private final static JsonObject CONF = new JsonObject().put("http_port", PORT);
    private final static WebClientOptions CLIENT_CONF = new WebClientOptions().setDefaultHost("localhost").setDefaultPort(PORT);

    @Rule
    public Timeout rule = Timeout.seconds(5);

    @Before
    public void setup(TestContext ctx) {
        final Async async = ctx.async();
        server = new Server();
        server
            .start(CONF, ContentTypeResource.class)
            .subscribe(v -> async.complete(), ctx::fail);
    }

    @After
    public void tearDown(TestContext ctx) {
        final Async async = ctx.async();
        server.close().subscribe(v -> {
            server = null;
            async.complete();
        }, e -> {
            server = null;
            ctx.fail(e);
        });
    }

    @Test
    public void shouldNotHang(TestContext ctx) {
        final Async async = ctx.async();
        client().get("/nocontenttype").rxSend().subscribe(
                resp -> {
                    ctx.assertEquals(500, resp.statusCode());
                    async.complete();
                },
                ctx::fail
        );
    }

    @Test
    public void worksFineWhenSync(TestContext ctx) {
        final Async async = ctx.async();

        client().get("/syncnocontenttype").rxSend().subscribe(
                resp -> {
                    ctx.assertEquals(500, resp.statusCode()); // "no messagebodywriter found"
                    async.complete();
                },
                ctx::fail
        );
    }

    @Test
    public void worksFineWithJson(TestContext ctx) {
        final Async async = ctx.async();
        client().get("/json").rxSend().subscribe(
                resp -> {
                    ctx.assertEquals(200, resp.statusCode());
                    ctx.assertEquals(new JsonArray(), resp.bodyAsJsonArray());
                    async.complete();
                },
                ctx::fail
        );
    }


    protected WebClient client() {
        return WebClient.create(AppGlobals.get().getVertx(), CLIENT_CONF);
    }

}
