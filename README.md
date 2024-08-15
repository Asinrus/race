## RACE - a tiny library for integration testing

Race - a tiny library written on Java 11 to help developers do integration testing and not compile in the head 
transaction behavior.

### Why `race`?
`Race` (according to Cambridge dictionary) - a competition in which all the competitors try to be the fastest and to 
finish first. 

In this case, it uses as an association a competition when competitors start at the one time and are trying to 
make an operation and check their interaction.

Let's assume - we have the next method: 
```java
@Service
public class CustomerService {
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public String changeName(Long id, String name) {
        Customer entity = repository.findById(id).orElseThrow();
        entity.setName(name);
        repository.save(entity);
        return name;
    }
}
```
To test a transaction behaviour we need to the next without `race`:
```java
    void test() {

        Map<String, Callable<String>> tasks = new HashMap<>();
        tasks.put("John", () -> customerService.changeName(1L, "John"));
        tasks.put("Derek", () -> customerService.changeName(1L, "Derek"));

        ExecutorService executorService = Executors.newFixedThreadPool(tasks.size());
        CountDownLatch startLatch = new CountDownLatch(1);
        List<Future<String>> futures = new ArrayList<>();

        for (Callable<String> task : tasks.values()) {
            futures.add(executorService.submit(() -> {
                try {
                    startLatch.await();  // wait for the start signal
                    return task.call();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }));
        }

        startLatch.countDown();  // give the start signal

        executorService.shutdown(); 
        executorService.awaitTermination(30, TimeUnit.SECONDS); // shut down the executor service

        for (Future<String> future : futures) {
            try {
                String result = future.get();  // get the result of the task
                // verify result
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
```

With `race`:

```java
@Test
void testWithNaming() {
    race(Map.of(
            "Mike", () -> customerService.changeName(1L, "Mike"),
            "Derek", () -> customerService.changeName(1L, "Derek")))
        .withAssertion(executionResult -> {
            var derekResult = executionResult.get("Derek");
            var mikeResult = executionResult.get("Mike");
            var oneContainsError = mikeResult.isHasError() ^ derekResult.isHasError();
            assertTrue(oneContainsError);
        })
        .go();
}
```

### Examples:
[Link] to the examples (https://github.com/Asinrus/race-examples)

### Usage: 
1. if you have a database, and you want to be sure that you
understand how you code works when you use a transaction onto a database
2. if you have a external containerize product which has a some rules to resolve data contention in case of 
   multithreading access


### Don't use it: 
1. If you want to test Java memory model. Please, use a [Lincheck](https://github.com/JetBrains/lincheck)

