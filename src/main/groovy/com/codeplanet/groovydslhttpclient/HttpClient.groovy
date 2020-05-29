package com.codeplanet.groovydslhttpclient

import groovy.transform.TupleConstructor
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType

import static com.codeplanet.groovydslhttpclient.Repeater.repeatOnFail

class HttpClient {

    static HttpMethodBuilder send(HttpMethod httpMethod) {
        new HttpMethodBuilder()
    }
}

class HttpMethodBuilder {

    RequestBuilder to(String url) {
        new RequestBuilder(url)
    }
}

@TupleConstructor
class RequestBuilder {
    String url

    void withConfig(@DelegatesTo(value = RequestConfigurationSpec, strategy = Closure.DELEGATE_FIRST) Closure<Void> closure) {
        RequestConfigurationSpec requestConfigurationSpec = new RequestConfigurationSpec(url)
        requestConfigurationSpec.with closure
    }
}

@TupleConstructor
class RequestConfigurationSpec {
    String url
    Map<String, String> headers = [:]
    int timeoutInMilliseconds = 1_000
    int numberOfTries = 1

    void headers(Map<String, String> params) {
        params.each { k, v -> headers.put k, v }
    }

    TimeoutBuilder timeout(int timeoutLength) {
        timeoutInMilliseconds = timeoutLength
        new TimeoutBuilder()
    }

    RetryBuilder retry(int retryCount) {
        numberOfTries = retryCount + 1
        new RetryBuilder()
    }

    void onSuccess(@ClosureParams(value = SimpleType, options = "java.lang.String") Closure<?> closure) {
        String response = repeatOnFail(numberOfTries) {
            url.toURL()
                    .getText(
                            readTimeout: timeoutInMilliseconds,
                            requestProperties: headers
                    )
        }
        closure(response)
    }
}

@TupleConstructor
class RetryBuilder {

    RetryBuilder getTimes() {
        this
    }
}

@TupleConstructor
class TimeoutBuilder {

    TimeoutBuilder getMilliseconds() {
        this
    }
}

enum HttpMethod {
    GET
}

class Repeater {
    static <T> T repeatOnFail(int numberOfTries, Closure<T> closure) {
        int tryCount = 0
        while (tryCount++ < numberOfTries) {
            try {
                return closure()
            } catch (Exception e) {
                System.err.println e
            }
        }
        throw new RuntimeException("Failed after $numberOfTries tries")
    }
}
