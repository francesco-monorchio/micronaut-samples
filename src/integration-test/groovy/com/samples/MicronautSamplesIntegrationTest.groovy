package com.samples

import groovy.util.logging.Slf4j
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

@MicronautTest
@Slf4j
class MicronautSamplesIntegrationTest extends Specification {

    @Inject
    EmbeddedServer server

    void 'test it works'() {

        expect:
        server.running
    }

}
