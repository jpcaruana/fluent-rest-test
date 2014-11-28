/**
 * Copyright (C) 2013-2014 all@code-story.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package net.codestory.rest;

import net.codestory.http.filters.basic.BasicAuthFilter;
import net.codestory.http.payload.Payload;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static net.codestory.http.security.Users.singleUser;

public class GetTest extends AbstractTest {
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void get() {
    server.configure(routes -> routes
        .get("/", "hello")
    );

    get("/").should()
      .respond(200)
      .respond("hello")
      .respond("text/html;charset=UTF-8", "hello")
      .respond(200, "hello")
      .respond(200, "text/html;charset=UTF-8", "hello");

    get("/").should()
      .respond(200)
      .respond("hello")
      .respond("text/html;charset=UTF-8", "hello")
      .respond(200, "hello")
      .respond(200, "text/html;charset=UTF-8", "hello");
  }

  @Test
  public void fail_to_get() {
    server.configure(routes -> routes
        .get("/", "hello")
    );

    thrown.expect(AssertionError.class);
    thrown.expectMessage("Expecting \"good bye\" was \"hello\"");

    get("/").should().respond("good bye");
  }

  @Test
  public void get_with_header() {
    server.configure(routes -> routes
        .get("/", context -> context.header("name"))
    );

    get("/").withHeader("name", "value").should().respond("value");
  }

  @Test
  public void get_with_headers() {
    server.configure(routes -> routes
        .get("/", context -> context.header("first") + context.header("second"))
    );

    get("/").withHeader("first", "1").withHeader("second", "2").should().respond("12");
  }

  @Test
  public void get_with_preemptive_authentication() {
    server.configure(routes -> routes
        .filter(new BasicAuthFilter("/", "realm", singleUser("login", "pwd")))
        .get("/", context -> "Secret")
    );

    get("/").withPreemptiveAuthentication("login", "pwd").should().respond("Secret");
    get("/").withPreemptiveAuthentication("", "").should().respond(401);
    get("/").should().respond(401);
  }

  @Test
  public void get_with_basic_authentication() {
    server.configure(routes -> routes
        .filter(new BasicAuthFilter("/", "realm", singleUser("login", "pwd")))
        .get("/", context -> "Secret")
    );

    get("/").withAuthentication("login", "pwd").should().respond("Secret");
    get("/").withAuthentication("", "").should().respond(401);
    get("/").should().respond(401);
  }

  @Test
  public void get_cookie() {
    server.configure(routes -> routes
        .get("/", context -> new Payload("Hello").withCookie("name", "value"))
    );

    get("/").should().respondWithCookie("name", "value");
  }

  @Test
  public void get_cookies() {
    server.configure(routes -> routes
        .get("/", context -> new Payload("").withCookie("first", "1st").withCookie("second", "2nd"))
    );

    get("/").should().respondWithCookie("first", "1st").respondWithCookie("second", "2nd");
  }

  @Test
  public void fail_without_cookie() {
    server.configure(routes -> routes
        .get("/", context -> "")
    );

    thrown.expect(AssertionError.class);

    get("/").should().respondWithCookie("??", "??");
  }

  @Test
  public void fail_with_wrong_cookie() {
    server.configure(routes -> routes
        .get("/", context -> new Payload("").withCookie("name", "value"))
    );

    thrown.expect(AssertionError.class);

    get("/").should().respondWithCookie("name", "??");
  }

  @Test
  public void get_header() {
    server.configure(routes -> routes
        .get("/", context -> new Payload("").withHeader("name", "value"))
    );

    get("/").should().respondWithHeader("name", "value");
  }

  @Test
  public void fail_without_header() {
    server.configure(routes -> routes
        .get("/", context -> new Payload("").withHeader("name", "value"))
    );

    get("/").should().respondWithHeader("name", "value");
  }

  @Test
  public void fail_with_wrong_header() {
    server.configure(routes -> routes
        .get("/", context -> new Payload("").withHeader("name", "value"))
    );

    get("/").should().respondWithHeader("name", "value");
  }
}
