package xyz.a5s7.lift.cli;

import org.junit.Test;
import xyz.a5s7.lift.cli.Request;

import static org.junit.Assert.*;

public class RequestTest {
    @Test
    public void shouldReturnUnknownRequest() {
        Request request = Request.parse(" c csdf");
        assertEquals(new Request(Request.Type.UNKNOWN, 0), request);
    }

    @Test
    public void shouldReturnUnknownRequest1() {
        Request request = Request.parse(" c1 ");
        assertEquals(new Request(Request.Type.UNKNOWN, 0), request);
    }

    @Test
    public void shouldReturnUnknownWhenEmptyString() {
        Request request = Request.parse("   ");
        assertEquals(new Request(Request.Type.UNKNOWN, 0), request);
    }

    @Test
    public void shouldReturnGoRequest() {
        Request request = Request.parse(" g    123 ");
        assertEquals(new Request(Request.Type.GO, 123), request);
    }

    @Test
    public void shouldReturnCallRequest() {
        Request request = Request.parse(" c    -1 ");
        assertEquals(new Request(Request.Type.CALL, -1), request);
    }
}