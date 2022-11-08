package com.samples

import groovy.util.logging.Slf4j
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Filter
import io.micronaut.http.annotation.Get
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.filter.HttpServerFilter
import io.micronaut.http.filter.ServerFilterChain
import io.micronaut.http.filter.ServerFilterPhase
import io.micronaut.runtime.Micronaut
import groovy.transform.CompileStatic
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import jakarta.inject.Inject
import org.reactivestreams.Publisher
import org.slf4j.MDC

import java.util.concurrent.ThreadFactory

class InheritableMDCThreadFactory implements ThreadFactory {

    @Override
    Thread newThread(Runnable r) {

        def contextMap = MDC.copyOfContextMap
        if (!contextMap) {
            return new Thread(r)
        }

        def runnable = {

            MDC.contextMap = contextMap
            try {
                r.run()
            } finally {
                MDC.clear()
            }
        }
        return new Thread(runnable)
    }

}

@Filter('/api/**')
@Slf4j
class AuthTraceFilter implements HttpServerFilter {

    @Override
    Publisher<MutableHttpResponse<?>> doFilter(HttpRequest<?> request, ServerFilterChain chain) {

        MDC.put('key', 'value')
        return chain.proceed(request)
    }

    @Override
    int getOrder() {
        return ServerFilterPhase.TRACING.order()
    }

}

@Controller
@ExecuteOn(TaskExecutors.IO)
@Slf4j
class SampleController {

    @Inject
    @Client('/')
    HttpClient client

    @Get('/run')
    void run() {

        for (;;) {

            def request = HttpRequest.GET('/api/samples')
            client.toBlocking().exchange(request)
        }
    }

    @Get('/api/samples')
    HttpResponse<String> sample() {

        log.info('On controller side')
        return HttpResponse.ok('sample')
    }

}

@CompileStatic
class Application {
    static void main(String[] args) {
        Micronaut.run(Application, args)
    }
}
