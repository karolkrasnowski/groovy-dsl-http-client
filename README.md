# Example of how to write DSL in Groovy
API was designed for the following usage
```groovy
send GET to 'https://jsonplaceholder.typicode.com/todos/1' withConfig {
    headers Accept: 'application/json'
    timeout 2_000 milliseconds
    retry 2 times
    onSuccess { println "Received $it" }
}
```

All details are described in the blog post https://krasnowski.medium.com/how-to-write-dsl-in-groovy-6a4aabe2049e
