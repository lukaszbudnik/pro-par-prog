/**
 * Copyright (C) 2015 Łukasz Budnik <lukasz.budnik@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.github.lukaszbudnik.proparprog


import java.io.PrintStream
import java.util.concurrent.{ThreadFactory, ThreadPoolExecutor, Executors}

import com.google.common.util.concurrent.ThreadFactoryBuilder
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

import scala.concurrent.{ExecutionContext, Await, Future}

@RunWith(classOf[JUnitRunner])
class FuturesSpec extends Specification {

  def slow1:Int = {
    Thread.sleep(300)
    123
  }

  def slow2:String = {
    Thread.sleep(700)
    "456"
  }

  def slow3:Long = {
    Thread.sleep(500)
    789
  }

  "Futures" should {

    "run in parallel" in {
      val f1 = Future { slow1 }
      val f2 = Future { slow2 }
      val f3 = Future { slow3 }

      val r1 = Await.result(f1, (300 + 30).milli)
      val r2 = Await.result(f2, (700 + 70).milli)
      val r3 = Await.result(f3, (500 + 50).milli)

      r1 must beEqualTo(123)
      r2 must beEqualTo("456")
      r3 must beEqualTo(789)
    }

    "be composable" in {
      val f1 = Future { slow1 }
      val f2 = Future { slow2 }
      val f3 = Future { slow3 }

      val all = for {
        r1 <- f1
        r2 <- f2
        r3 <- f3
      } yield (r1, r2, r3)

      val (r1, r2, r3) = Await.result(all, (700 + 70).milli)

      r1 must beEqualTo(123)
      r2 must beEqualTo("456")
      r3 must beEqualTo(789)
    }

    "run with custom execution context" in {

      // use a helper ExecutionContext.fromExecutor() instead of boiler plate code below
      // this is just a verbose example

      val ec = new ExecutionContext {
        val threadFactory = new ThreadFactoryBuilder().setNameFormat("customExecutionContext-thread-%d").build
        val threadPool = Executors.newFixedThreadPool(Runtime.getRuntime.availableProcessors * 2, threadFactory)
          .asInstanceOf[ThreadPoolExecutor]

        def execute(runnable: Runnable) {
          println("About to run runnable ==> " + runnable)
          println("On my custom thread pool of size ==> " + threadPool.getMaximumPoolSize)
          threadPool.submit(runnable)
        }

        def reportFailure(t: Throwable) = t.printStackTrace
      }

      val f1 = Future.apply(slow1)(ec)
      val r1 = Await.result(f1, (300 + 30).milli)

      r1 must beEqualTo(123)
    }

  }

}
