package dev.rocky.concurrency;

import org.junit.jupiter.api.Test;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

class SafeCounterTest {
 @Test void concurrentIncrementsDoNotLoseUpdates() throws Exception {
  int workers=8, each=1000;
  var pool=Executors.newFixedThreadPool(workers);
  var gate=new CountDownLatch(1);
  try {
   var tasks=new java.util.ArrayList<Future<?>>();
   for(int i=0;i<workers;i++) tasks.add(pool.submit(()->{try {gate.await();} catch(InterruptedException e){Thread.currentThread().interrupt();return;} for(int j=0;j<each;j++) counter.increment();}));
   gate.countDown();
   for(var task:tasks) task.get(10,TimeUnit.SECONDS);
   assertEquals(workers*each,counter.exactCount());
   assertEquals(workers*each,counter.throughputCount());
  } finally {pool.shutdownNow();}
 }
 private final SafeCounter counter=new SafeCounter();
}
