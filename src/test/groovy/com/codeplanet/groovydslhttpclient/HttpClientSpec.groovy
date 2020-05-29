package com.codeplanet.groovydslhttpclient

import groovy.json.JsonSlurper
import spock.lang.Specification

import static com.codeplanet.groovydslhttpclient.HttpClient.send
import static com.codeplanet.groovydslhttpclient.HttpMethod.GET

class HttpClientSpec extends Specification {

    JsonSlurper jsonSlurper = new JsonSlurper()

    def 'should fetch data over HTTP'() {
        given:
        String response = ''

        when:
        send GET to 'https://jsonplaceholder.typicode.com/todos/1' withConfig {
            headers Accept: 'application/json'
            timeout 2_000 milliseconds
            retry 2 times
            onSuccess { response = it }
        }

        then:
        jsonSlurper.parseText(response) == jsonSlurper.parseText('''
            {
              "userId": 1,
              "id": 1,
              "title": "delectus aut autem",
              "completed": false
            }
        ''')
    }
}
