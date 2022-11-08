package com.samples

import groovy.util.logging.Slf4j
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.runtime.EmbeddedApplication
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.AutoCleanup
import spock.lang.Specification
import spock.lang.Subject

@MicronautTest
@Slf4j
class MicronautSamplesSpec extends Specification {

    @Inject
    EmbeddedApplication<?> application

    @AutoCleanup
    @Client('/')
    @Inject
    @Subject
    HttpClient httpClient

    void 'It works'() {
        expect:
        application.running
    }

    def 'Should MDC inherited by child thread'() {

        given:
        def request = HttpRequest.GET('/api/samples')

        when:
        for (;;) {

            httpClient.toBlocking()
              .exchange(request, String)
        }

        then:
        true
    }

}
